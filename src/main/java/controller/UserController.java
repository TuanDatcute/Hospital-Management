package controller;

import java.io.IOException;
import java.util.List; // Import List
import java.util.stream.Collectors; // Import Collectors
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.dto.TaiKhoanDTO;
import service.TaiKhoanService;

/**
 * Controller xử lý các nghiệp vụ liên quan đến người dùng (đăng nhập, đăng xuất, quản lý tài khoản...).
 */
@WebServlet(name = "UserController", urlPatterns = {"/UserController"})
public class UserController extends HttpServlet {

    // --- Cập nhật URL ---
    private static final String LOGIN_PAGE = "login.jsp";
    private static final String HOME_PAGE = "home.jsp";
    private static final String USER_LIST_PAGE = "admin/danhSachTaiKhoan.jsp"; // Trang danh sách tài khoản
    private static final String USER_FORM_PAGE = "admin/formTaiKhoan.jsp";   // Trang form tạo/sửa tài khoản
    private static final String ERROR_PAGE = "error.jsp";

    // Khởi tạo Service
    private final TaiKhoanService taiKhoanService = new TaiKhoanService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String url = ERROR_PAGE; // Mặc định lỗi nếu action GET không hợp lệ

        try {
             // --- Bổ sung xử lý action GET ---
            if (action == null || action.isEmpty()) {
                 url = LOGIN_PAGE; // Nếu không action, về trang login
            } else {
                 switch (action) {
                     case "logout":
                         url = logout(request);
                         // Redirect sẽ được xử lý trong finally
                         break;
                     case "listUsers":
                         url = listUsers(request);
                         break;
                     case "showCreateForm":
                         request.setAttribute("formAction", "createUser");
                         url = USER_FORM_PAGE;
                         break;
                     case "showEditForm": // Form để sửa trạng thái/vai trò
                         url = showEditForm(request);
                         break;
                     default:
                         request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho GET.");
                 }
            }
        } catch (Exception e) {
            log("Lỗi tại UserController (doGet): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi: " + e.getMessage());
            // url đã là ERROR_PAGE
        } finally {
            if (url.equals(LOGIN_PAGE) && action != null && action.equals("logout")) {
                 response.sendRedirect(request.getContextPath() + "/" + url);
            } else {
                request.getRequestDispatcher(url).forward(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String url = ERROR_PAGE;
         boolean redirectAfterSuccess = false; // Cờ để kiểm soát redirect

        try {
            if (action == null || action.isEmpty()) {
                throw new Exception("Hành động không được chỉ định.");
            }

            switch (action) {
                case "login":
                    url = login(request, response);
                     redirectAfterSuccess = url.equals(HOME_PAGE); // Redirect nếu login thành công
                    break;
                 // --- Bổ sung xử lý action POST ---
                 case "createUser":
                     url = createUser(request);
                     // Sau khi tạo, quay về trang list (không redirect, doPost sẽ gọi listUsers)
                     break;
                 case "updateUserStatus": // Action để khóa/mở khóa
                     url = updateUserStatus(request);
                     // Sau khi cập nhật, quay về trang list
                     break;
                // Có thể thêm case "updateUserRole" nếu cần
                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho POST.");
            }

             // Nếu thành công (không phải trang lỗi/form) và KHÔNG cần redirect, load lại danh sách
             if (!redirectAfterSuccess && !url.equals(ERROR_PAGE) && !url.equals(USER_FORM_PAGE)) {
                 url = listUsers(request);
             }

        } catch (Exception e) {
            log("Lỗi tại UserController (doPost): " + e.getMessage(), e);
             // Xử lý lỗi validation khi tạo/sửa user
             if ("createUser".equals(action) || "updateUserStatus".equals(action) /* || "updateUserRole".equals(action) */) {
                 handleServiceException(request, e, action);
                 url = USER_FORM_PAGE; // Quay lại form
                 redirectAfterSuccess = false; // Không redirect khi lỗi
             } else { // Lỗi hệ thống khác (bao gồm cả lỗi login)
                 request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
                 if ("login".equals(action)) {
                      url = LOGIN_PAGE; // Lỗi login thì về trang login
                 }
             }
        } finally {
             if (redirectAfterSuccess) {
                 response.sendRedirect(request.getContextPath() + "/" + url);
             } else {
                 request.getRequestDispatcher(url).forward(request, response);
             }
        }
    }

    /**
     * Xử lý logic đăng nhập.
     */
    private String login(HttpServletRequest request, HttpServletResponse response) {
        String tenDangNhap = request.getParameter("username");
        String matKhau = request.getParameter("password");

        try {
            TaiKhoanDTO user = taiKhoanService.login(tenDangNhap, matKhau);
            HttpSession session = request.getSession();
            session.setAttribute("USER", user);
            session.setAttribute("ROLE", user.getVaiTro());
            return HOME_PAGE;
        } catch (Exception e) {
            log("Lỗi đăng nhập: " + e.getMessage());
            request.setAttribute("ERROR_MESSAGE", e.getMessage());
            return LOGIN_PAGE;
        }
    }

     /**
     * Xử lý logic đăng xuất.
     */
    private String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return LOGIN_PAGE;
    }

     // --- THÊM CÁC HÀM QUẢN LÝ TÀI KHOẢN ---

     /**
     * Lấy danh sách Tài khoản và chuyển đến trang hiển thị.
     */
    private String listUsers(HttpServletRequest request) throws Exception {
        List<TaiKhoanDTO> list = taiKhoanService.getAllTaiKhoan();
        request.setAttribute("LIST_TAIKHOAN", list);
        return USER_LIST_PAGE;
    }

     /**
     * Lấy thông tin Tài khoản cần sửa (trạng thái/vai trò) và hiển thị form.
     */
    private String showEditForm(HttpServletRequest request) throws Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            TaiKhoanDTO taiKhoan = taiKhoanService.getTaiKhoanById(id);
            request.setAttribute("USER_DATA", taiKhoan); // Gửi DTO của tài khoản cần sửa
            request.setAttribute("formAction", "updateUserStatus"); // Hoặc updateUserRole tùy bạn gộp hay tách
             return USER_FORM_PAGE;
        } catch (NumberFormatException e) {
             request.setAttribute("ERROR_MESSAGE", "ID Tài khoản không hợp lệ.");
            return ERROR_PAGE;
        }
        // Exception từ Service sẽ được bắt ở doGet
    }

    /**
     * Xử lý logic tạo mới một Tài khoản.
     */
    private String createUser(HttpServletRequest request) throws Exception {
        TaiKhoanDTO newTaiKhoanDTO = createDTOFromRequest(request);
        String plainPassword = request.getParameter("password"); // Lấy mật khẩu thô từ form

        TaiKhoanDTO result = taiKhoanService.createTaiKhoan(newTaiKhoanDTO, plainPassword);
        request.setAttribute("SUCCESS_MESSAGE", "Tạo tài khoản '" + result.getTenDangNhap() + "' thành công!");
        return USER_LIST_PAGE; // Trả về trang list để doPost gọi listUsers
    }

    /**
     * Xử lý logic cập nhật trạng thái (Khóa/Mở khóa) một Tài khoản.
     */
    private String updateUserStatus(HttpServletRequest request) throws Exception {
         try {
            int id = Integer.parseInt(request.getParameter("id"));
            String newTrangThai = request.getParameter("trangThai"); // Lấy trạng thái mới từ form

            TaiKhoanDTO result = taiKhoanService.updateTrangThaiTaiKhoan(id, newTrangThai);
            request.setAttribute("SUCCESS_MESSAGE", "Cập nhật trạng thái tài khoản '" + result.getTenDangNhap() + "' thành công!");
            return USER_LIST_PAGE;
        } catch (NumberFormatException e) {
             request.setAttribute("ERROR_MESSAGE", "ID Tài khoản không hợp lệ khi cập nhật trạng thái.");
             return ERROR_PAGE;
        }
        // Exception từ Service (VD: trạng thái không hợp lệ) sẽ được bắt ở doPost
    }

     /**
     * Xử lý lỗi từ Service (validation) khi tạo/sửa tài khoản và gửi lại form.
     */
    private void handleServiceException(HttpServletRequest request, Exception e, String formAction) {
        request.setAttribute("ERROR_MESSAGE", e.getMessage());
        // Gửi lại dữ liệu (trừ mật khẩu)
        request.setAttribute("USER_DATA", createDTOFromRequest(request));
        request.setAttribute("formAction", formAction);
        // Có thể cần tải thêm dữ liệu cho form nếu có (ví dụ: danh sách vai trò)
    }

    /**
     * Hàm tiện ích tạo TaiKhoanDTO từ request (không bao gồm mật khẩu).
     */
    private TaiKhoanDTO createDTOFromRequest(HttpServletRequest request) {
         TaiKhoanDTO dto = new TaiKhoanDTO();
         String idStr = request.getParameter("id");
         if(idStr != null && !idStr.isEmpty()){
             try {
                 dto.setId(Integer.parseInt(idStr));
             } catch (NumberFormatException e) { /* ignore */ }
         }
         dto.setTenDangNhap(request.getParameter("tenDangNhap")); // Nên đặt tên input là tenDangNhap
         dto.setEmail(request.getParameter("email"));
         dto.setVaiTro(request.getParameter("vaiTro"));
         dto.setTrangThai(request.getParameter("trangThai"));
         // Không lấy mật khẩu ở đây
         return dto;
    }


    @Override
    public String getServletInfo() {
        return "Controller xử lý đăng nhập, đăng xuất và quản lý người dùng.";
    }
}