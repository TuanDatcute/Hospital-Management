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
     * doGet (KHÔNG THAY ĐỔI)
     * Hàm 'searchPhongBenh' ở Service sẽ tự động lọc bỏ
     * các phòng đã bị xóa mềm ('NGUNG_HOAT_DON').
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8"); // Đảm bảo đọc UTF-8
        
        String action = request.getParameter("action");
        if (action == null) action = "listRooms";

        String searchKeyword = request.getParameter("searchKeyword");

        try {
            // 1. Luôn tải danh sách khoa (cho form)
            List<KhoaDTO> khoaList = khoaService.getAllKhoa();
            request.setAttribute("khoaList", khoaList);
            
            // 2. Tải danh sách phòng (ĐÃ LỌC)
            // Service sẽ chỉ trả về các phòng 'HOAT_DONG'
            List<PhongBenhDTO> roomList = phongBenhService.searchPhongBenh(searchKeyword);
            request.setAttribute("roomList", roomList);

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

    /**
     * CẬP NHẬT: Thêm case "deleteRoom"
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
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
            // === THÊM MỚI ===
            case "deleteRoom":
                deleteRoom(request, response);
                break;
        }
    }

    /**
     * HÀM MỚI: Xử lý yêu cầu xóa mềm phòng bệnh
     */
    private void deleteRoom(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String urlRedirect = "MainController?action=listRooms";
        try {
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            
            // 1. Gọi service để thực hiện logic nghiệp vụ
            // (Service sẽ kiểm tra giường 'DANG_SU_DUNG',
            //  xóa mềm giường con, và cuối cùng xóa mềm phòng)
            phongBenhService.softDeletePhongBenh(roomId);
            
            // 2. Gửi thông báo thành công
            urlRedirect += "&deleteSuccess=true";
            
        } catch (Exception e) {
            // 3. Bắt lỗi nghiệp vụ (vd: phòng đang có bệnh nhân)
            urlRedirect += "&deleteError=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8");
            e.printStackTrace();
        }
        // 4. Chuyển hướng
        response.sendRedirect(urlRedirect);
    }
    
    // (Hàm createRoom giữ nguyên)
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
    
    // (Hàm updateRoom giữ nguyên)
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
            roomToUpdate.setTrangThai("HOAT_DONG");

            phongBenhService.updatePhongBenh(roomToUpdate);
            
            urlRedirect += "&updateSuccess=true";
        } catch (Exception e) {
            urlRedirect += "&updateError=Details: " + e.getMessage();
            e.printStackTrace();
        }
        response.sendRedirect(urlRedirect);
    }
}