package controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.ThongBaoDAO;
import model.ThongBaoDTO;

@WebServlet(name = "ThongBaoController", urlPatterns = {"/ThongBaoController"})
public class ThongBaoController extends HttpServlet {

    // Định nghĩa đường dẫn tới trang view để dễ quản lý
    private static final String NOTIFICATION_PAGE = "notifications.jsp";
    private static final String ERROR_PAGE = "error.jsp";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String action = request.getParameter("action");

        try {
            // Kiểm tra action để gọi hàm xử lý tương ứng
            if ("listNotifications".equals(action)) {

                // --- BƯỚC 1: LẤY DỮ LIỆU ---
                HttpSession session = request.getSession();
                // Lấy thông tin người dùng từ session sau khi họ đăng nhập
                // TaiKhoan loginUser = (TaiKhoan) session.getAttribute("LOGIN_USER");

                // *** Tạm thời hardcode tài khoản ID để test. Khi tích hợp, hãy dùng ID từ session ***
                int accountId = 1;
                // if (loginUser == null) { response.sendRedirect("login.jsp"); return; }
                // int accountId = loginUser.getId();

                ThongBaoDAO dao = new ThongBaoDAO();
                List<ThongBaoDTO> notificationList = dao.listNotificationsByUser(accountId);

                // --- BƯỚC 2: GỬI DỮ LIỆU LÊN VIEW ---
                request.setAttribute("notifications", notificationList);
                request.getRequestDispatcher(NOTIFICATION_PAGE).forward(request, response);

            } else if ("markNotificationAsRead".equals(action)) {

                // --- XỬ LÝ CHO AJAX ---
                int notificationId = Integer.parseInt(request.getParameter("notificationId"));
                ThongBaoDAO dao = new ThongBaoDAO();
                boolean success = dao.markAsRead(notificationId);

                // Trả về phản hồi dạng JSON cho JavaScript
                response.setContentType("application/json");
                response.getWriter().write("{\"status\": \"" + (success ? "success" : "failed") + "\"}");
            } else if ("createNotification".equals(action)) {
                // --- LOGIC MỚI ĐỂ TẠO THÔNG BÁO ---

                // 1. Lấy dữ liệu từ form gửi lên
                String tieuDe = request.getParameter("tieuDe");
                String noiDung = request.getParameter("noiDung");
                int taiKhoanId = Integer.parseInt(request.getParameter("taiKhoanId"));

                // 2. Gọi DAO để lưu vào database
                ThongBaoDAO dao = new ThongBaoDAO();
                dao.createNotification(taiKhoanId, tieuDe, noiDung);

                // 3. Chuyển hướng (Redirect) về trang danh sách để xem kết quả
                // Dùng redirect để tránh người dùng nhấn F5 và gửi lại form
                response.sendRedirect("MainController?action=listNotifications");

                // Vì đã redirect nên không cần forward nữa, ta return để kết thúc
                return;
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Chuyển hướng tới trang lỗi nếu có vấn đề
            request.getRequestDispatcher(ERROR_PAGE).forward(request, response);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }
}
