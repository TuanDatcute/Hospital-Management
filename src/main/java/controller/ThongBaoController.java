package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

// Import Service
import service.ThongBaoService;
import service.TaiKhoanService;

// Import DTO
import model.dto.TaiKhoanDTO;
import model.dto.GroupedThongBaoDTO; // <-- Dùng DTO đã gộp nhóm

@WebServlet(name = "ThongBaoController", urlPatterns = {"/ThongBaoController"})
public class ThongBaoController extends HttpServlet {

    private ThongBaoService thongBaoService;
    private TaiKhoanService taiKhoanService;

    @Override
    public void init() {
        this.thongBaoService = new ThongBaoService();
        this.taiKhoanService = new TaiKhoanService();
    }

    /**
     * doGet chỉ còn 1 chức năng: hiển thị danh sách (listNotifications)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        // Bất kể action là gì (hoặc null), đều hiển thị trang danh sách
        try {
            listNotifications(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi xử lý yêu cầu GET: " + e.getMessage());
            e.printStackTrace();
            // Vẫn cố gắng forward đến JSP để hiển thị lỗi
            request.getRequestDispatcher("ThongBao.jsp").forward(request, response);
        }
    }

    /**
     * doPost chỉ còn 1 chức năng: tạo thông báo (createThongBao)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        // Chỉ xử lý action "createThongBao"
        if ("createThongBao".equals(action)) {
            try {
                createNotification(request, response);
            } catch (Exception e) {
                String errorMsg = java.net.URLEncoder.encode("Lỗi xử lý yêu cầu POST: " + e.getMessage(), "UTF-8");
                response.sendRedirect("MainController?action=listNotifications&error=" + errorMsg);
                e.printStackTrace();
            }
        } else {
            // Nếu action không phải "createThongBao", báo lỗi
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action POST không hợp lệ: " + (action != null ? action : "null"));
        }
    }

    /**
     * CẬP NHẬT: Lấy danh sách ROLES động từ Service
     */
    private void listNotifications(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String searchKeyword = request.getParameter("searchKeyword");

        // 1. Lấy danh sách thông báo ĐÃ GỘP NHÓM
        List<GroupedThongBaoDTO> groupedNotificationList = thongBaoService.searchGroupedNotifications(searchKeyword);

        // 2. Lấy danh sách tài khoản (để chọn khi gửi cho tài khoản cụ thể)
        List<TaiKhoanDTO> accountList = taiKhoanService.getActiveAndUnassignedAccounts(searchKeyword);

        // === THAY ĐỔI ===
        // 3. Lấy danh sách Vai trò ĐỘNG từ CSDL
        List<String> rolesList = taiKhoanService.getDistinctRoles();
        // ================

        // 4. Gửi dữ liệu ra JSP
        request.setAttribute("notificationList", groupedNotificationList);
        request.setAttribute("accountList", accountList);
        request.setAttribute("roles", rolesList); // Gửi danh sách động

        request.getRequestDispatcher("ThongBao.jsp").forward(request, response);
    }

    /**
     * Xử lý tạo thông báo mới (Giữ nguyên)
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
}
