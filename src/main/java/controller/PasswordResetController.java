package controller;

import exception.ValidationException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Entity.TaiKhoan; // Cần import Entity để lấy tenNguoiDung
import service.TaiKhoanService;
import util.EmailUtils;

/**
 * Servlet này xử lý CẢ 2 GIAI ĐOẠN của Quên Mật khẩu:
 * 1. POST (action=requestReset): Nhận email, gọi Service tạo token, gửi email.
 * 2. GET: Nhận link click, gọi Service xác thực token, forward đến form reset.
 * 3. POST (action=performReset): Nhận mật khẩu mới, gọi Service để reset.
 */
@WebServlet(name = "PasswordResetController", urlPatterns = {"/reset"})
public class PasswordResetController extends HttpServlet {

    private final TaiKhoanService taiKhoanService = new TaiKhoanService();
    
    // --- Hằng số (Clean Code) ---
    private static final String ACTION_REQUEST_RESET = "requestReset";
    private static final String ACTION_PERFORM_RESET = "performReset";
    
    private static final String LOGIN_PAGE = "login.jsp";
    private static final String FORGOT_PASS_PAGE = "forgotPassword.jsp";
    private static final String RESET_PASS_PAGE = "resetPassword.jsp";
    private static final String ERROR_PAGE = "error.jsp";

    /**
     * **Giai đoạn 2 (GET):** Xử lý khi người dùng nhấp vào link trong email.
     * Nhiệm vụ: Xác thực token và hiển thị form resetPassword.jsp.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        
        String token = request.getParameter("token");
        String url = RESET_PASS_PAGE; // Trang đích nếu thành công
        
        try {
            // 1. Yêu cầu Service kiểm tra token (còn hạn không, có tồn tại không?)
            // (Hàm này sẽ ném ValidationException nếu thất bại)
            taiKhoanService.validatePasswordResetToken(token);
            
            // 2. Token hợp lệ, forward đến trang reset
            // Gửi token này theo để form reset có thể submit lại
            request.setAttribute("token", token); 
            
        } catch (ValidationException e) {
            // Lỗi nghiệp vụ (Token sai, hết hạn)
            // Đặt lỗi vào Session và redirect về login
            HttpSession session = request.getSession(true);
            session.setAttribute("ERROR_MESSAGE", e.getMessage());
            url = LOGIN_PAGE;
            response.sendRedirect(url);
            return; // Dừng xử lý
            
        } catch (Exception e) {
            // Lỗi hệ thống
            log("Lỗi hệ thống tại PasswordResetController (doGet): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Lỗi hệ thống nghiêm trọng.");
            url = ERROR_PAGE;
        }
        
        // Chỉ forward nếu thành công (url = RESET_PASS_PAGE)
        // Các trường hợp lỗi đã bị redirect
        request.getRequestDispatcher(url).forward(request, response);
    }

    /**
     * **Giai đoạn 1 & 3 (POST):** Xử lý khi người dùng submit form.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        
        String action = request.getParameter("action");
        String url = ERROR_PAGE; // Mặc định
        boolean isRedirect = false; // Mặc định là forward

        try {
            if (ACTION_REQUEST_RESET.equals(action)) {
                // --- GIAI ĐOẠN 1: YÊU CẦU RESET (Từ forgotPassword.jsp) ---
                url = handleRequestReset(request);
                isRedirect = true; // Luôn redirect về login
                
            } else if (ACTION_PERFORM_RESET.equals(action)) {
                // --- GIAI ĐOẠN 3: THỰC HIỆN RESET (Từ resetPassword.jsp) ---
                url = handlePerformReset(request);
                isRedirect = true; // Luôn redirect về login
                
            } else {
                throw new ValidationException("Hành động không hợp lệ.");
            }
            
        } catch (ValidationException e) {
            // Lỗi nghiệp vụ (vd: email rỗng, mật khẩu không khớp)
            log("Lỗi Validation tại PasswordResetController (doPost): " + e.getMessage());
            request.setAttribute("ERROR_MESSAGE", e.getMessage());
            
            // Quyết định xem nên forward về form nào
            if (ACTION_REQUEST_RESET.equals(action)) {
                url = FORGOT_PASS_PAGE;
            } else if (ACTION_PERFORM_RESET.equals(action)) {
                request.setAttribute("token", request.getParameter("token")); // Gửi lại token
                url = RESET_PASS_PAGE;
            }
            
        } catch (Exception e) {
            // Lỗi hệ thống nghiêm trọng
            log("Lỗi Hệ thống tại PasswordResetController (doPost): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Lỗi hệ thống nghiêm trọng.");
            url = ERROR_PAGE;
        }

        // Quyết định Forward (để giữ lỗi) hay Redirect (về login)
        if (isRedirect) {
            response.sendRedirect(url);
        } else {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    /**
     * (Helper) Xử lý Giai đoạn 1: Gửi email
     */
    private String handleRequestReset(HttpServletRequest request) throws ValidationException, Exception {
        String email = request.getParameter("email");
        
        // Service sẽ tìm email, tạo token và trả về entity (hoặc null)
        TaiKhoan taiKhoan = taiKhoanService.generatePasswordResetToken(email);
        
        if (taiKhoan != null) {
            // Chỉ gửi email nếu tài khoản tồn tại
            try {
                EmailUtils.sendPasswordResetEmail(
                    taiKhoan.getEmail(), 
                    taiKhoan.getTenDangNhap(), // Dùng tên thật để chào
                    taiKhoan.getVerificationToken() // Dùng token vừa tạo
                );
            } catch (Exception e) {
                log("Lỗi GỬI EMAIL RESET (bỏ qua): " + e.getMessage(), e);
                // Không ném lỗi ra ngoài, người dùng không cần biết email lỗi
            }
        }
        
        // (Bảo mật) Luôn luôn báo thành công, bất kể email có tồn tại hay không
        request.getSession().setAttribute("SUCCESS_MESSAGE", 
                "Nếu email của bạn tồn tại trong hệ thống, một link đặt lại mật khẩu đã được gửi.");
        
        return LOGIN_PAGE; // Chuyển hướng về login
    }

    /**
     * (Helper) Xử lý Giai đoạn 3: Lưu mật khẩu mới
     */
    private String handlePerformReset(HttpServletRequest request) throws ValidationException, Exception {
        String token = request.getParameter("token");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // Service sẽ validate tất cả (token, mật khẩu khớp, regex)
        taiKhoanService.performPasswordReset(token, newPassword, confirmPassword);
        
        // Đặt thông báo thành công
        request.getSession().setAttribute("SUCCESS_MESSAGE", "Đặt lại mật khẩu thành công! Vui lòng đăng nhập.");
        return LOGIN_PAGE; // Chuyển hướng về login
    }
    
    @Override
    public String getServletInfo() {
        return "Controller xử lý Giai đoạn 1 (yêu cầu) và Giai đoạn 2 (thực hiện) reset mật khẩu.";
    }
}