package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// Import Service (Giả sử bạn đã có)
import service.ThongBaoService;
import service.TaiKhoanService;

// Import DTO
import model.dto.ThongBaoDTO;
import model.dto.TaiKhoanDTO; // Cần để lấy danh sách tài khoản

@WebServlet(name = "ThongBaoController", urlPatterns = {"/ThongBaoController"})
public class ThongBaoController extends HttpServlet {

    private ThongBaoService thongBaoService;
    private TaiKhoanService taiKhoanService;

    // Các vai trò có thể có trong hệ thống
    // Nên lấy động từ DB hoặc config file
    private static final List<String> ROLES = Arrays.asList("QUAN_TRI", "BAC_SI", "Y_TA", "LE_TAN", "BENH_NHAN");


    @Override
    public void init() {
        this.thongBaoService = new ThongBaoService();
        this.taiKhoanService = new TaiKhoanService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) action = "listNotifications"; // Mặc định là hiển thị danh sách

        try {
            switch (action) {
                case "getThongBaoForUpdate":
                    showUpdateForm(request, response);
                    break;
                case "listNotifications":
                default:
                    listNotifications(request, response);
                    break;
            }
        } catch (Exception e) {
             request.setAttribute("error", "Lỗi xử lý yêu cầu GET: " + e.getMessage());
             e.printStackTrace();
             // Chuyển đến trang danh sách (hoặc trang lỗi) để hiển thị lỗi
             listNotifications(request, response); 
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action không được cung cấp.");
            return;
        }

        try {
            switch (action) {
                case "createThongBao":
                    createNotification(request, response);
                    break;
                case "updateThongBao":
                    updateNotification(request, response);
                    break;
                case "deleteThongBao":
                    deleteNotification(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action không hợp lệ: " + action);
            }
        } catch (Exception e) {
             // Lỗi chung khi xử lý POST, quay lại trang list với thông báo lỗi
             String errorMsg = java.net.URLEncoder.encode("Lỗi xử lý yêu cầu POST: " + e.getMessage(), "UTF-8");
             response.sendRedirect("MainController?action=listNotifications&error=" + errorMsg);
             e.printStackTrace();
        }
    }

    /**
     * Hiển thị danh sách thông báo (có tìm kiếm) và form tạo mới
     */
    private void listNotifications(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String searchKeyword = request.getParameter("searchKeyword");
        
        // 1. Lấy danh sách thông báo (đã lọc theo keyword và chỉ lấy 'ACTIVE')
        List<ThongBaoDTO> notificationList = thongBaoService.searchActiveNotifications(searchKeyword);
        
        // 2. Lấy danh sách tài khoản (để chọn khi gửi cho tài khoản cụ thể)
        List<TaiKhoanDTO> accountList = taiKhoanService.getAllTaiKhoan(); // Giả sử Service có hàm này
        
        // 3. Gửi dữ liệu ra JSP
        request.setAttribute("notificationList", notificationList);
        request.setAttribute("accountList", accountList);
        request.setAttribute("roles", ROLES); // Gửi danh sách vai trò

        request.getRequestDispatcher("ThongBao.jsp").forward(request, response);
    }
    
    /**
     * Lấy thông tin thông báo cần sửa và hiển thị form cập nhật
     */
    private void showUpdateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
            
        int notificationId = Integer.parseInt(request.getParameter("id"));
        
        // 1. Lấy thông tin thông báo cần sửa
        ThongBaoDTO notificationToUpdate = thongBaoService.getNotificationById(notificationId);
        
        // 2. Lấy các danh sách cần thiết (giống như listNotifications)
        List<ThongBaoDTO> notificationList = thongBaoService.searchActiveNotifications(null); // Lấy lại list để hiển thị bảng
        List<TaiKhoanDTO> accountList = taiKhoanService.getAllTaiKhoan();
        
        // 3. Gửi dữ liệu ra JSP
        request.setAttribute("notificationToUpdate", notificationToUpdate); // Dữ liệu cho form sửa
        request.setAttribute("notificationList", notificationList);
        request.setAttribute("accountList", accountList);
        request.setAttribute("roles", ROLES);

        request.getRequestDispatcher("ThongBao.jsp").forward(request, response);
    }

    /**
     * Xử lý tạo thông báo mới
     */
    private void createNotification(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String urlRedirect = "MainController?action=listNotifications"; // Action để list thông báo
        try {
            String tieuDe = request.getParameter("tieuDe");
            String noiDung = request.getParameter("noiDung");
            String targetType = request.getParameter("targetType"); // 'ROLE', 'ALL', 'USER'
            String targetValue = request.getParameter("targetValue"); // Role name, 'ALL', taiKhoanId

            // --- VALIDATION ---
            if (tieuDe == null || tieuDe.trim().isEmpty() || noiDung == null || noiDung.trim().isEmpty()) {
                throw new IllegalArgumentException("Tiêu đề và Nội dung không được để trống.");
            }
             if (targetType == null || targetValue == null) {
                 throw new IllegalArgumentException("Vui lòng chọn đối tượng nhận.");
             }
             if ("ROLE".equals(targetType) && targetValue.trim().isEmpty()) {
                 throw new IllegalArgumentException("Vui lòng chọn vai trò.");
             }
             if ("USER".equals(targetType) && targetValue.trim().isEmpty()) {
                 throw new IllegalArgumentException("Vui lòng chọn tài khoản.");
             }
            // --- KẾT THÚC VALIDATION ---

            // Gọi service để xử lý logic tạo (Service sẽ tìm ID tài khoản và lưu)
            thongBaoService.createNotification(tieuDe.trim(), noiDung.trim(), targetType, targetValue.trim());

            urlRedirect += "&createSuccess=true";

        } catch (IllegalArgumentException e) {
             urlRedirect += "&createError=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8");
             e.printStackTrace();
        } catch (Exception e) {
            urlRedirect += "&createError=" + java.net.URLEncoder.encode("Lỗi hệ thống khi tạo thông báo: " + e.getMessage(), "UTF-8");
            e.printStackTrace();
        }
        response.sendRedirect(urlRedirect);
    }

    /**
     * Xử lý cập nhật thông báo
     */
    private void updateNotification(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String urlRedirect = "MainController?action=listNotifications";
        int notificationId = 0;
        try {
            notificationId = Integer.parseInt(request.getParameter("id"));
            String tieuDe = request.getParameter("tieuDe");
            String noiDung = request.getParameter("noiDung");
            
             // --- VALIDATION ---
            if (tieuDe == null || tieuDe.trim().isEmpty() || noiDung == null || noiDung.trim().isEmpty()) {
                throw new IllegalArgumentException("Tiêu đề và Nội dung không được để trống.");
            }
            // --- KẾT THÚC VALIDATION ---

            // Tạo DTO để gửi đi (chỉ chứa các trường cần update)
            ThongBaoDTO dto = new ThongBaoDTO();
            dto.setId(notificationId);
            dto.setTieuDe(tieuDe.trim());
            dto.setNoiDung(noiDung.trim());

            thongBaoService.updateNotification(dto);

            urlRedirect += "&updateSuccess=true";

        } catch (NumberFormatException e) {
             urlRedirect += "&updateError=" + java.net.URLEncoder.encode("ID thông báo không hợp lệ.", "UTF-8");
             e.printStackTrace();
        } catch (IllegalArgumentException e) {
             urlRedirect += "&updateError=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8");
             if (notificationId > 0) urlRedirect += "&id=" + notificationId; // Giữ lại ID để biết lỗi của cái nào
             e.printStackTrace();
        } catch (Exception e) {
            urlRedirect += "&updateError=" + java.net.URLEncoder.encode("Lỗi hệ thống khi cập nhật: " + e.getMessage(), "UTF-8");
             if (notificationId > 0) urlRedirect += "&id=" + notificationId;
            e.printStackTrace();
        }
        response.sendRedirect(urlRedirect);
    }

    /**
     * Xử lý xóa mềm thông báo
     */
    private void deleteNotification(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String urlRedirect = "MainController?action=listNotifications";
        try {
            int notificationId = Integer.parseInt(request.getParameter("id"));
            
            // Gọi service để cập nhật trạng thái
            thongBaoService.softDeleteNotification(notificationId);
            
            urlRedirect += "&deleteSuccess=true";
            
        } catch (NumberFormatException e) {
             urlRedirect += "&deleteError=" + java.net.URLEncoder.encode("ID thông báo không hợp lệ.", "UTF-8");
             e.printStackTrace();
        } catch (Exception e) {
            urlRedirect += "&deleteError=" + java.net.URLEncoder.encode("Lỗi khi xóa: " + e.getMessage(), "UTF-8");
            e.printStackTrace();
        }
        response.sendRedirect(urlRedirect);
    }
}