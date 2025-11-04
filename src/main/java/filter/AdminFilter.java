//package filter;
//
//import java.io.IOException;
//import javax.servlet.Filter;
//import javax.servlet.FilterChain;
//import javax.servlet.FilterConfig;
//import javax.servlet.ServletException;
//import javax.servlet.ServletRequest;
//import javax.servlet.ServletResponse;
//import javax.servlet.annotation.WebFilter;
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import javax.servlet.http.HttpSession;
//
///**
// * Filter này bảo vệ các trang Admin (trong thư mục /admin/).
// * Nó chỉ cho phép 'QUAN_TRI' truy cập.
// * Nó PHẢI chạy SAU KHI AuthFilter đã chạy.
// */
//// **LƯU Ý:** Annotation này sẽ được GHI ĐÈ bởi web.xml
//// Chúng ta vẫn khai báo để rõ ràng, nhưng web.xml sẽ quyết định
//@WebFilter(filterName = "AdminFilter", urlPatterns = {"/admin/*"})
//public class AdminFilter implements Filter {
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        // Không cần init gì
//    }
//
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        HttpServletResponse httpResponse = (HttpServletResponse) response;
//        HttpSession session = httpRequest.getSession(false);
//
//        // Giả định AuthFilter đã chạy và đảm bảo session.getAttribute("USER") tồn tại.
//        // Chúng ta chỉ cần kiểm tra vai trò (ROLE).
//        
//        String role = (String) session.getAttribute("ROLE");
//
//        if ("QUAN_TRI".equals(role)) {
//            // 1. Là Admin -> OK, cho phép truy cập /admin/*
//            chain.doFilter(request, response);
//        } else {
//            // 2. Không phải Admin (là Bệnh nhân, Bác sĩ, Lễ tân...)
//            System.out.println("AdminFilter: Chặn truy cập (Vai trò: " + role + ") vào trang Admin.");
//            
//            // Đá họ về trang chủ TƯƠNG ỨNG của họ
//            if ("BAC_SI".equals(role) || "LE_TAN".equals(role)) {
//                httpResponse.sendRedirect(httpRequest.getContextPath() + "/staff/dashboard.jsp");
//            } else {
//                // Mặc định (Bệnh nhân)
//                httpResponse.sendRedirect(httpRequest.getContextPath() + "/home.jsp");
//            }
//        }
//    }
//
//    @Override
//    public void destroy() {
//        // Không cần làm gì
//    }
//}