package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import model.dto.KhoaDTO;
import model.dto.PhongBenhDTO;
import service.KhoaService;
import service.PhongBenhService;

@WebServlet(name = "PhongBenhController", urlPatterns = {"/PhongBenhController"})
public class PhongBenhController extends HttpServlet {

    private PhongBenhService phongBenhService;
    private KhoaService khoaService;

    @Override
    public void init() {
        this.phongBenhService = new PhongBenhService();
        this.khoaService = new KhoaService();
    }

    /**
     * Sửa đổi doGet:
     * - Tải danh sách phòng và khoa.
     * - Nếu action là 'getRoomForUpdate', tải thêm phòng đó và đặt vào attribute.
     * - Forward đến JSP một lần duy nhất.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String action = request.getParameter("action");
        if (action == null) action = "listRooms";

        try {
            // 1. Luôn tải danh sách khoa (cho form)
            List<KhoaDTO> khoaList = khoaService.getAllKhoa();
            request.setAttribute("khoaList", khoaList);
            
            // 2. Luôn tải danh sách phòng (cho bảng)
            List<PhongBenhDTO> roomList = phongBenhService.getAllPhongBenh();
            request.setAttribute("roomList", roomList);

            // 3. Xử lý nếu có action 'getRoomForUpdate'
            if (action.equals("getRoomForUpdate")) {
                int roomId = Integer.parseInt(request.getParameter("roomId"));
                // Giả sử bạn có service method này
                PhongBenhDTO roomToUpdate = phongBenhService.getPhongBenhById(roomId); 
                request.setAttribute("roomToUpdate", roomToUpdate);
            }
            
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi tải dữ liệu: " + e.getMessage());
        } finally {
            // 4. Luôn forward đến JSP
            request.getRequestDispatcher("PhongBenh.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8"); // Thêm để hỗ trợ Tiếng Việt
        
        String action = request.getParameter("action");
        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        switch (action) {
            case "createRoom":
                createRoom(request, response);
                break;
            case "updateRoom": // Đã thêm
                updateRoom(request, response);
                break;
        }
    }

    /**
     * Đã hoàn thiện:
     * - Tạo DTO từ request.
     * - Gọi service để thêm mới.
     */
    private void createRoom(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String urlRedirect = "MainController?action=listRooms";
        try {
            String tenPhong = request.getParameter("tenPhong");
            String loaiPhong = request.getParameter("loaiPhong");
            int sucChua = Integer.parseInt(request.getParameter("sucChua"));
            int khoaId = Integer.parseInt(request.getParameter("khoaId"));
            
            // --- Hoàn thiện logic ---
            PhongBenhDTO newRoom = new PhongBenhDTO();
            newRoom.setTenPhong(tenPhong);
            newRoom.setLoaiPhong(loaiPhong);
            newRoom.setSucChua(sucChua);
            newRoom.setKhoaId(khoaId);

            // Giả sử bạn có service method này
            phongBenhService.createPhongBenh(newRoom);
            // -------------------------
            
            urlRedirect += "&createSuccess=true";
        } catch (Exception e) {
            urlRedirect += "&createError=Details: " + e.getMessage();
            e.printStackTrace(); // Giúp debug
        }
        response.sendRedirect(urlRedirect);
    }
    
    /**
     * Chức năng mới:
     * - Lấy ID phòng từ form.
     * - Tạo DTO và gọi service để cập nhật.
     */
    private void updateRoom(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String urlRedirect = "MainController?action=listRooms";
        try {
            // Lấy ID của phòng cần cập nhật
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String tenPhong = request.getParameter("tenPhong");
            String loaiPhong = request.getParameter("loaiPhong");
            int sucChua = Integer.parseInt(request.getParameter("sucChua"));
            int khoaId = Integer.parseInt(request.getParameter("khoaId"));
            
            PhongBenhDTO roomToUpdate = new PhongBenhDTO();
            roomToUpdate.setId(roomId); // ID là quan trọng nhất
            roomToUpdate.setTenPhong(tenPhong);
            roomToUpdate.setLoaiPhong(loaiPhong);
            roomToUpdate.setSucChua(sucChua);
            roomToUpdate.setKhoaId(khoaId);

            // Giả sử bạn có service method này
            phongBenhService.updatePhongBenh(roomToUpdate);
            
            urlRedirect += "&updateSuccess=true";
        } catch (Exception e) {
            urlRedirect += "&updateError=Details: " + e.getMessage();
            e.printStackTrace(); // Giúp debug
        }
        response.sendRedirect(urlRedirect);
    }
}