//package filter;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
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
//import model.dto.BenhNhanDTO; // <-- **THÊM 1: IMPORT DTO BỆNH NHÂN**
//import model.dto.TaiKhoanDTO; // <-- **THÊM 2: IMPORT DTO TÀI KHOẢN**
//import service.BenhNhanService; // <-- **THÊM 3: IMPORT SERVICE BỆNH NHÂN**
//
///**
// * Filter này kiểm tra xem người dùng đã đăng nhập (đã xác thực) chưa
// * VÀ kiểm tra xem Bệnh nhân đã hoàn tất hồ sơ chưa.
// */
//@WebFilter(filterName = "AuthFilter", urlPatterns = {"/*"}) // Áp dụng Filter cho TẤT CẢ request
//public class AuthFilter implements Filter {
//
//    // Danh sách các tài nguyên (URL) công khai, không cần đăng nhập (Whitelist)
//    private List<String> publicUrls;
//    private BenhNhanService benhNhanService; // <-- **THÊM 4: KHAI BÁO SERVICE**
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//        // Khởi tạo Service 1 lần khi Filter bắt đầu
//        benhNhanService = new BenhNhanService();
//        
//        // Khởi tạo danh sách các URL công khai
//        publicUrls = new ArrayList<>();
//        
//        // --- CÁC TRANG JSP CÔNG KHAI ---
//        publicUrls.add("/login.jsp");      
//        // publicUrls.add("/register.jsp"); // **SỬA 5: Xóa vì đã gộp vào login.jsp**
//        publicUrls.add("/index.jsp");     
//        
//        // --- THƯ MỤC CÔNG KHAI ---
//        publicUrls.add("/css/");         
//        publicUrls.add("/images/");     
//        publicUrls.add("/js/");
//        
//        // **THÊM 6: Trang điền hồ sơ (cần cho Bệnh nhân mới)**
//        publicUrls.add("/user/fillProfile.jsp"); 
//    }
//
//    /**
//     * Phương thức lọc chính, chạy trên MỌI request.
//     */
//    @Override
//    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
//            throws IOException, ServletException {
//
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        HttpServletResponse httpResponse = (HttpServletResponse) response;
//        HttpSession session = httpRequest.getSession(false); 
//
//        String contextPath = httpRequest.getContextPath(); // /Hospital_Managerment
//        String requestUri = httpRequest.getRequestURI(); 
//        String path = requestUri.substring(contextPath.length()); // Đường dẫn tương đối (ví dụ: /home.jsp)
//
//        // 1. Kiểm tra xem người dùng đã đăng nhập chưa
//        boolean loggedIn = (session != null && session.getAttribute("USER") != null);
//
//        // 2. Kiểm tra xem tài nguyên yêu cầu có phải là "công khai" không
//        boolean isPublicResource = false;
//        for (String publicUrl : publicUrls) {
//            if (path.startsWith(publicUrl)) {
//                isPublicResource = true;
//                break;
//            }
//        }
//        
//        // 3. Xử lý đặc biệt cho MainController
//        boolean isPublicAction = false;
//        if (path.equals("/MainController")) {
//            String action = request.getParameter("action");
//            if (action != null) {
//                // Các action công khai (chưa cần login)
//                if (action.equals("login") || action.equals("register")) {
//                    isPublicAction = true;
//                }
//                
//                // **THÊM 7: Cho phép action "updateProfile" (để Bệnh nhân mới có thể lưu)**
//                if (action.equals("updateProfile")) {
//                    isPublicAction = true;
//                }
//            }
//        }
//        
//        // --- BẮT ĐẦU LOGIC LỌC ---
//
//        if (loggedIn) {
//            // --- NGƯỜI DÙNG ĐÃ ĐĂNG NHẬP ---
//            
//            TaiKhoanDTO user = (TaiKhoanDTO) session.getAttribute("USER");
//            
//            // 4. **LOGIC MỚI (Bước 8): KIỂM TRA HOÀN TẤT HỒ SƠ**
//            // Chỉ kiểm tra Bệnh nhân
//            if ("BENH_NHAN".equals(user.getVaiTro())) {
//                BenhNhanDTO benhNhan = benhNhanService.getBenhNhanByTaiKhoanId(user.getId());
//                
//                // Kiểm tra xem hồ sơ đã hoàn tất chưa (ví dụ: cccd là bắt buộc)
//                if (benhNhan == null || benhNhan.getCccd() == null || benhNhan.getCccd().isEmpty()) {
//                    
//                    // Hồ sơ CHƯA hoàn tất.
//                    // Kiểm tra xem họ có đang cố truy cập trang khác không
//                    boolean isGoingToFillProfile = path.equals("/user/fillProfile.jsp");
//                    boolean isSubmittingProfile = path.equals("/MainController") && "updateProfile".equals(request.getParameter("action"));
//
//                    if (isGoingToFillProfile || isSubmittingProfile) {
//                        // OK, họ đang ở đúng trang điền hồ sơ (hoặc đang nhấn nút Lưu)
//                        chain.doFilter(request, response);
//                    } else {
//                        // Nếu họ cố vào home.jsp hoặc trang khác -> Ép quay lại
//                        System.out.println("AuthFilter: Ép Bệnh nhân (ID: " + user.getId() + ") điền hồ sơ.");
//                        httpResponse.sendRedirect(contextPath + "/user/fillProfile.jsp");
//                    }
//                    return; // Dừng Filter tại đây
//                }
//            }
//            
//            // Nếu đã đăng nhập VÀ hồ sơ OK (hoặc là Admin/BS), cho đi tiếp
//            chain.doFilter(request, response);
//
//        } else if (isPublicResource || isPublicAction) {
//            // --- CHƯA ĐĂNG NHẬP, NHƯNG TRUY CẬP TRANG CÔNG KHAI ---
//            chain.doFilter(request, response); // Cho đi tiếp
//            
//        } else {
//            // --- CHƯA ĐĂNG NHẬP VÀ CỐ GẮNG TRUY CẬP TRANG PRIVATE ---
//            // (Ví dụ: gõ /home.jsp hoặc /admin/dashboard.jsp)
//            System.out.println("AuthFilter: Chặn truy cập trái phép (chưa đăng nhập) vào: " + path);
//            httpResponse.sendRedirect(contextPath + "/login.jsp"); // Đá về trang đăng nhập
//        }
//    }
//
//    @Override
//    public void destroy() {
//        // Không cần làm gì
//    }
//}