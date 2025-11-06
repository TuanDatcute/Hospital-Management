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
     * === ĐÃ CẬP NHẬT LOGIC doGet ===
     * Phương thức này giờ đây sẽ điều hướng (forward) đến các trang JSP
     * khác nhau dựa trên 'action':
     * - "listRooms": Tải danh sách phòng và đi đến PhongBenh.jsp
     * - "showCreateRoomForm": Tải danh sách khoa và đi đến PhongBenhForm.jsp
     * - "getRoomForUpdate": Tải phòng cần sửa + danh sách khoa và đi đến PhongBenhForm.jsp
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) action = "listRooms"; // Action mặc định

        String url = "PhongBenh.jsp"; // Trang mặc định
        
        try {
            switch (action) {
                case "showCreateRoomForm":
                    // 1. Chỉ cần tải danh sách Khoa cho form
                    List<KhoaDTO> khoaList = khoaService.getAllKhoa();
                    request.setAttribute("khoaList", khoaList);
                    // 2. Chuyển đến trang form
                    url = "PhongBenhForm.jsp";
                    break;
                    
                case "getRoomForUpdate":
                    // 1. Lấy ID phòng
                    int roomId = Integer.parseInt(request.getParameter("roomId"));
                    // 2. Tải thông tin phòng cần sửa
                    PhongBenhDTO roomToUpdate = phongBenhService.getPhongBenhById(roomId);
                    // 3. Tải danh sách Khoa cho dropdown
                    List<KhoaDTO> khoaListForUpdate = khoaService.getAllKhoa(); 
                    
                    // 4. Gửi cả hai qua request
                    request.setAttribute("roomToUpdate", roomToUpdate);
                    request.setAttribute("khoaList", khoaListForUpdate);
                    // 5. Chuyển đến trang form
                    url = "PhongBenhForm.jsp";
                    break;
                    
                case "listRooms":
                default:
                    // 1. Lấy từ khóa tìm kiếm
                    String searchKeyword = request.getParameter("searchKeyword");
                    // 2. Tải danh sách phòng đã lọc
                    List<PhongBenhDTO> roomList = phongBenhService.searchPhongBenh(searchKeyword);
                    // 3. Gửi danh sách qua request
                    request.setAttribute("roomList", roomList);
                    // 4. Chuyển đến trang danh sách (url mặc định đã đúng)
                    url = "PhongBenh.jsp";
                    break;
            }
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi tải dữ liệu: " + e.getMessage());
            // Nếu có lỗi, vẫn quay về trang danh sách để hiển thị
            url = "PhongBenh.jsp"; 
            e.printStackTrace();
        }
        
        // Chuyển hướng (forward) đến trang JSP đã chọn
        request.getRequestDispatcher(url).forward(request, response);
    }

    /**
     * === KHÔNG CẦN THAY ĐỔI ===
     * Logic doPost của bạn đã rất chuẩn (dùng switch, gọi hàm riêng, 
     * và sendRedirect) nên được giữ nguyên.
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
            case "deleteRoom":
                deleteRoom(request, response);
                break;
        }
    }

    /**
     * HÀM MỚI: Xử lý yêu cầu xóa mềm phòng bệnh (GIỮ NGUYÊN)
     */
    private void deleteRoom(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String urlRedirect = "MainController?action=listRooms";
        try {
            int roomId = Integer.parseInt(request.getParameter("roomId"));
            
            // 1. Gọi service để thực hiện logic nghiệp vụ
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
            roomToUpdate.setTrangThai("HOAT_DONG"); // Giữ nguyên logic cũ

            phongBenhService.updatePhongBenh(roomToUpdate);
            
            urlRedirect += "&updateSuccess=true";
        } catch (Exception e) {
            urlRedirect += "&updateError=Details: " + e.getMessage();
            e.printStackTrace();
        }
        response.sendRedirect(urlRedirect);
    }
}