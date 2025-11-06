package controller;

import exception.ValidationException;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import service.TaiKhoanService; // Đảm bảo import Service

/**
 * Servlet này CHỈ xử lý một việc: Bắt request GET từ link xác thực email.
 * Nó không có giao diện (JSP).
 * Tên đường dẫn "@WebServlet" phải khớp với link trong EmailUtils.
 */
@WebServlet(name = "VerifyController", urlPatterns = {"/verify"})
public class VerifyController extends HttpServlet {

    private final TaiKhoanService taiKhoanService = new TaiKhoanService();

    /**
     * Xử lý khi người dùng nhấp vào link trong email (HTTP GET).
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        String token = request.getParameter("token");
        String url = "login.jsp"; // Nơi chuyển hướng mặc định
        HttpSession session = request.getSession(true); // Lấy session để đặt thông báo

        try {
            // 1. Lấy token từ URL
            if (token == null || token.isEmpty()) {
                throw new ValidationException("Token không hợp lệ hoặc bị thiếu.");
            }
            
            // 2. Giao cho Service xử lý
            // (Service sẽ tìm token, check hết hạn, và set 'HOAT_DONG')
            taiKhoanService.verifyToken(token);
            
            // 3. Đặt thông báo thành công để hiển thị trên trang login
            session.setAttribute("SUCCESS_MESSAGE", "Xác thực tài khoản thành công! Vui lòng đăng nhập.");
            
        } catch (ValidationException e) {
            // Lỗi nghiệp vụ (vd: token hết hạn, token sai)
            // Hiển thị lỗi này trên trang login
            session.setAttribute("ERROR_MESSAGE", e.getMessage());
            url = "login.jsp"; 
            
        } catch (Exception e) {
            // Lỗi hệ thống nghiêm trọng (vd: Lỗi CSDL khi update)
            log("Lỗi nghiêm trọng tại VerifyController: " + e.getMessage(), e);
            session.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống, vui lòng thử lại sau.");
            url = "error.jsp"; // Chuyển đến trang lỗi chung
            
        } finally {
            // 4. Chuyển hướng người dùng (luôn luôn)
            response.sendRedirect(url);
        }
    }

    /**
     * Xử lý POST (Nếu có ai đó cố tình gọi)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Hành động POST không được hỗ trợ, chỉ chuyển về GET
        doGet(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Controller xử lý xác thực tài khoản qua email.";
    }
}