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
     * - Nhận 'searchKeyword' để lọc danh sách phòng.
     * - Tải danh sách khoa (cho form).
     * - Xử lý 'getRoomForUpdate' (giữ nguyên).
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8"); // Đảm bảo đọc UTF-8
        
        String action = request.getParameter("action");
        if (action == null) action = "listRooms";

        // =============================================
        //        BẮT ĐẦU THAY ĐỔI
        // =============================================
        // Lấy từ khóa tìm kiếm
        String searchKeyword = request.getParameter("searchKeyword");
        // =============================================
        //        KẾT THÚC THAY ĐỔI
        // =============================================

        try {
            // 1. Luôn tải danh sách khoa (cho form)
            List<KhoaDTO> khoaList = khoaService.getAllKhoa();
            request.setAttribute("khoaList", khoaList);
            
            // =============================================
            //        BẮT ĐẦU THAY ĐỔI
            // =============================================
            
            // 2. Tải danh sách phòng (ĐÃ LỌC)
            // Giả sử service của bạn có method searchPhongBenh(keyword)
            // Nếu keyword là null/rỗng, service này sẽ trả về tất cả
            List<PhongBenhDTO> roomList = phongBenhService.searchPhongBenh(searchKeyword);
            request.setAttribute("roomList", roomList);

            // =============================================
            //        KẾT THÚC THAY ĐỔI
            // =============================================

            // 3. Xử lý nếu có action 'getRoomForUpdate' (Giữ nguyên)
            if (action.equals("getRoomForUpdate")) {
                int roomId = Integer.parseInt(request.getParameter("roomId"));
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
        // ... (GIỮ NGUYÊN TOÀN BỘ PHẦN doPost, KHÔNG CẦN SỬA) ...
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        switch (action) {
            case "createRoom":
                createRoom(request, response);
                break;
            case "updateRoom":
                updateRoom(request, response);
                break;
        }
    }

    // ... (CÁC HÀM createRoom VÀ updateRoom GIỮ NGUYÊN) ...
    private void createRoom(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String urlRedirect = "MainController?action=listRooms";
        try {
            String tenPhong = request.getParameter("tenPhong");
            String loaiPhong = request.getParameter("loaiPhong");
            int sucChua = Integer.parseInt(request.getParameter("sucChua"));
            int khoaId = Integer.parseInt(request.getParameter("khoaId"));
            
            PhongBenhDTO newRoom = new PhongBenhDTO();
            newRoom.setTenPhong(tenPhong);
            newRoom.setLoaiPhong(loaiPhong);
            newRoom.setSucChua(sucChua);
            newRoom.setKhoaId(khoaId);

            phongBenhService.createPhongBenh(newRoom);
            
            urlRedirect += "&createSuccess=true";
        } catch (Exception e) {
            urlRedirect += "&createError=Details: " + e.getMessage();
            e.printStackTrace();
        }
        response.sendRedirect(urlRedirect);
    }
    
    private void updateRoom(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String urlRedirect = "MainController?action=listRooms";
        try {
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            String tenPhong = request.getParameter("tenPhong");
            String loaiPhong = request.getParameter("loaiPhong");
            int sucChua = Integer.parseInt(request.getParameter("sucChua"));
            int khoaId = Integer.parseInt(request.getParameter("khoaId"));
            
            PhongBenhDTO roomToUpdate = new PhongBenhDTO();
            roomToUpdate.setId(roomId);
            roomToUpdate.setTenPhong(tenPhong);
            roomToUpdate.setLoaiPhong(loaiPhong);
            roomToUpdate.setSucChua(sucChua);
            roomToUpdate.setKhoaId(khoaId);

            phongBenhService.updatePhongBenh(roomToUpdate);
            
            urlRedirect += "&updateSuccess=true";
        } catch (Exception e) {
            urlRedirect += "&updateError=Details: " + e.getMessage();
            e.printStackTrace();
        }
        response.sendRedirect(urlRedirect);
    }
}