package filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Filter này kiểm tra xem người dùng đã đăng nhập (đã xác thực) chưa
 * trước khi cho phép truy cập các trang cần bảo vệ.
 */
@WebFilter(filterName = "AuthFilter", urlPatterns = {"/*"}) // Áp dụng Filter cho TẤT CẢ request
public class AuthFilter implements Filter {

    // Danh sách các tài nguyên (URL) công khai, không cần đăng nhập (Whitelist)
    private List<String> publicUrls;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Khởi tạo danh sách các URL công khai
        publicUrls = new ArrayList<>();
        
        // --- CÁC TRANG JSP CÔNG KHAI ---
        publicUrls.add("/login.jsp");      
        publicUrls.add("/register.jsp");   
        publicUrls.add("/index.jsp");     
        
        // --- THƯ MỤC CÔNG KHAI ---
        // Giả sử các file CSS, JS, ảnh nằm ngoài WEB-INF (là public)
        publicUrls.add("/css/");         
        publicUrls.add("/images/");     
        publicUrls.add("/js/");
        
        // Bất kỳ đường dẫn nào khác, nếu không có action, sẽ bị chặn.
    }

    /**
     * Phương thức lọc chính, chạy trên MỌI request.
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        HttpSession session = httpRequest.getSession(false); 

        String contextPath = httpRequest.getContextPath(); // /Hospital_Managerment
        String requestUri = httpRequest.getRequestURI(); 
        String path = requestUri.substring(contextPath.length()); // Đường dẫn tương đối (ví dụ: /home.jsp)

        // 1. Kiểm tra xem người dùng đã đăng nhập chưa
        boolean loggedIn = (session != null && session.getAttribute("USER") != null);

        // 2. Kiểm tra xem tài nguyên yêu cầu có nằm trong danh sách "công khai" không
        boolean isPublicResource = false;
        for (String publicUrl : publicUrls) {
            if (path.startsWith(publicUrl)) {
                isPublicResource = true;
                break;
            }
        }
        
        // 3. Xử lý đặc biệt cho MainController
        if (path.equals("/MainController")) {
            String action = request.getParameter("action");
            if (action != null) {
                // Các action được phép truy cập công khai (chưa cần login)
                if (action.equals("login") || action.equals("showUserRegister") || action.equals("register")) {
                    isPublicResource = true;
                }
            }
        }

        // 4. Quyết định điều hướng
        if (loggedIn || isPublicResource) {
            // Đã đăng nhập HOẶC truy cập trang public -> Cho đi tiếp
            chain.doFilter(request, response);
        } else {
            // Chưa đăng nhập VÀ cố gắng truy cập trang private (home.jsp, admin/...)
            System.out.println("AuthFilter: Chặn truy cập trái phép vào: " + path);
            httpResponse.sendRedirect(contextPath + "/login.jsp");
        }
    }

    @Override
    public void destroy() {
        // Không cần làm gì
    }

    // --- Các hàm boilerplate khác đã được lược bỏ ---
}