package controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.dto.TaiKhoanDTO; // DTO của tài khoản (lấy từ Session)
import model.dto.ThongBaoDTO; // DTO thông báo (đã làm sạch)
import service.ThongBaoService;

// URL Pattern này là "nội bộ", MainController sẽ forward đến đây
@WebServlet(name = "UserThongBaoController", urlPatterns = {"/UserThongBaoController"})
public class UserThongBaoController extends HttpServlet {

    private ThongBaoService thongBaoService;

    @Override
    public void init() {
        this.thongBaoService = new ThongBaoService();
    }

    /**
     * doGet: Chỉ còn 1 nhiệm vụ là hiển thị. MainController đã quyết định
     * action là 'viewMyNotifications'.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        TaiKhoanDTO user = (TaiKhoanDTO) session.getAttribute("user");

        if (user == null) {
            response.sendRedirect("login.jsp"); // Chuyển về trang đăng nhập
            return;
        }

        try {
            // Lấy từ khóa tìm kiếm (do MainController forward)
            String searchKeyword = request.getParameter("searchKeyword");
            int taiKhoanId = user.getId();

            // Lấy danh sách thông báo CHỈ của người dùng này
            List<ThongBaoDTO> notificationList = thongBaoService.searchActiveNotificationsForUser(taiKhoanId, searchKeyword);

            request.setAttribute("notificationList", notificationList);
            request.getRequestDispatcher("UserThongBao.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Lỗi tải thông báo: " + e.getMessage());
            request.getRequestDispatcher("UserThongBao.jsp").forward(request, response);
        }
    }

    /**
     * doPost: Xử lý các action do MainController forward đến.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();
        TaiKhoanDTO user = (TaiKhoanDTO) session.getAttribute("user");

        if (user == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bạn cần đăng nhập.");
            return;
        }

        // Lấy action MÀ MAINCONTROLLER ĐÃ GỬI
        String action = request.getParameter("action");

        // SỬA: Redirect về MainController (action GET)
        String urlRedirect = "MainController?action=viewMyNotifications";

        try {
            int thongBaoId = Integer.parseInt(request.getParameter("id"));
            int taiKhoanId = user.getId(); // An toàn

            if ("markNotificationAsRead".equals(action)) {
                thongBaoService.markAsReadForUser(thongBaoId, taiKhoanId);
            } else if ("deleteMyNotification".equals(action)) { // SỬA
                thongBaoService.softDeleteForUser(thongBaoId, taiKhoanId);
                urlRedirect += "&deleteSuccess=true";
            } else {
                // Lỗi: MainController gửi một action mà controller này không biết
                throw new Exception("Hành động POST không xác định: " + action);
            }

        } catch (NumberFormatException e) {
            urlRedirect += "&error=" + java.net.URLEncoder.encode("ID thông báo không hợp lệ.", "UTF-8");
        } catch (Exception e) {
            urlRedirect += "&error=" + java.net.URLEncoder.encode("Lỗi xử lý: " + e.getMessage(), "UTF-8");
            e.printStackTrace();
        }

        // SỬA: Redirect về MainController
        response.sendRedirect(urlRedirect);
    }
}
