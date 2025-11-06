package controller;

import exception.ValidationException;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// --- THÊM MỚI ---
import model.dto.BenhNhanDTO; // (Giữ nguyên)
import model.dto.NhanVienDTO;
import model.dto.TaiKhoanDTO;
import service.BenhNhanService;
import service.NhanVienService;
import service.TaiKhoanService;
import util.EmailUtils; // <-- **THÊM IMPORT MỚI**
// --- KẾT THÚC THÊM MỚI ---

/**
 * Controller xử lý các nghiệp vụ liên quan đến người dùng. **ĐÃ CẬP NHẬT (Giai
 * đoạn 3):** Kích hoạt logic Xác thực Email.
 */
@WebServlet(name = "UserController", urlPatterns = {"/UserController"})
public class UserController extends HttpServlet {

    // (Các hằng số trang JSP... giữ nguyên)
    private static final String LOGIN_PAGE = "login.jsp";
    private static final String HOME_PAGE = "home.jsp";
    private static final String ADMIN_DASHBOARD_PAGE = "admin/dashboard.jsp";
    private static final String STAFF_DASHBOARD_PAGE = "staff/dashboard.jsp";

    private static final String USER_LIST_PAGE = "admin/danhSachTaiKhoan.jsp";
    private static final String USER_FORM_PAGE = "admin/formTaiKhoan.jsp";
    private static final String CHANGE_PASSWORD_PAGE = "user/changePassword.jsp";
    private static final String REGISTER_PAGE = "login.jsp";
    private static final String ERROR_PAGE = "error.jsp";

    // --- **THÊM HẰNG SỐ MỚI (CLEAN CODE)** ---
    private static final String ACTION_LOGIN = "login";
    private static final String ACTION_REGISTER = "register";
    private static final String ACTION_CREATE_USER = "createUser";
    private static final String ACTION_UPDATE_STATUS = "updateUserStatus";
    private static final String ACTION_CHANGE_PASSWORD = "changePassword";
    private static final String ROLE_BENH_NHAN = "BENH_NHAN";
    // --- **KẾT THÚC THÊM HẰNG SỐ** ---

    private final TaiKhoanService taiKhoanService = new TaiKhoanService();
    private final BenhNhanService benhNhanService = new BenhNhanService();
    private final NhanVienService nhanVienService = new NhanVienService();

    /**
     * (doGet... đã vô hiệu hóa 'verify', giữ nguyên) LƯU Ý: case "verify" đã
     * được chuyển sang VerifyController mới.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        String url = ERROR_PAGE;
        try {
            if (action == null || action.isEmpty()) {
                url = LOGIN_PAGE;
            } else {
                switch (action) {
                    case "logout":
                        url = logout(request);
                        break;
                    case "listUsers":
                        url = listUsers(request);
                        break;
                    case "showUserCreateForm":
                        request.setAttribute("formAction", "createUser");
                        url = USER_FORM_PAGE;
                        break;
                    case "showUserEditForm":
                        url = showUserEditForm(request);
                        break;
                    case "showChangePasswordForm":
                        url = CHANGE_PASSWORD_PAGE;
                        break;

                    default:
                        request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho GET.");
                }
            }
        } catch (ValidationException e) {
            log("Lỗi Validation tại UserController (doGet): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Lỗi nghiệp vụ: " + e.getMessage());
            url = ERROR_PAGE;

        } catch (Exception e) {
            log("Lỗi Hệ thống tại UserController (doGet): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
        } finally {
            if (url.equals(LOGIN_PAGE) && action != null && action.equals("logout")) {
                response.sendRedirect(request.getContextPath() + "/" + url);
            } else {
                request.getRequestDispatcher(url).forward(request, response);
            }
        }
    }

    /**
     * (doPost... đã cập nhật)
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String url = ERROR_PAGE;
        boolean redirectAfterSuccess = false;
        String successRedirectUrl = null;

        String errorFormPage = ERROR_PAGE;

        try {
            if (action == null || action.isEmpty()) {
                throw new Exception("Hành động không được chỉ định.");
            }

            // Dùng hằng số
            if (ACTION_LOGIN.equals(action)) {
                errorFormPage = LOGIN_PAGE;
            } else if (ACTION_REGISTER.equals(action)) {
                errorFormPage = REGISTER_PAGE;
            } else if (ACTION_CREATE_USER.equals(action) || ACTION_UPDATE_STATUS.equals(action)) {
                errorFormPage = USER_FORM_PAGE;
            } else if (ACTION_CHANGE_PASSWORD.equals(action)) {
                errorFormPage = CHANGE_PASSWORD_PAGE;
            }

            switch (action) {
                case ACTION_LOGIN: // Dùng hằng số
                    url = login(request, response);
                    redirectAfterSuccess = (url.equals(HOME_PAGE) || url.equals(ADMIN_DASHBOARD_PAGE) || url.equals(STAFF_DASHBOARD_PAGE) || url.equals(CHANGE_PASSWORD_PAGE));
                    successRedirectUrl = url;
                    break;
                case ACTION_CREATE_USER: // Dùng hằng số
                    url = createUser(request);
                    successRedirectUrl = USER_LIST_PAGE;
                    break;
                case ACTION_UPDATE_STATUS: // Dùng hằng số
                    url = updateUserStatus(request);
                    successRedirectUrl = USER_LIST_PAGE;
                    break;
                case ACTION_CHANGE_PASSWORD: // Dùng hằng số
                    url = changePassword(request);
                    redirectAfterSuccess = (url.equals(HOME_PAGE) || url.equals(ADMIN_DASHBOARD_PAGE) || url.equals(STAFF_DASHBOARD_PAGE));
                    successRedirectUrl = url;
                    break;
                case ACTION_REGISTER: // Dùng hằng số
                    url = register(request); // <-- GỌI HÀM REGISTER ĐÃ SỬA THEO LOGIC MỚI
                    if (url.equals(LOGIN_PAGE)) {
                        redirectAfterSuccess = true;
                        successRedirectUrl = "login.jsp";
                    }
                    break;
                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho POST.");
            }

            if (!redirectAfterSuccess && !url.equals(ERROR_PAGE) && !url.equals(USER_FORM_PAGE) && !url.equals(CHANGE_PASSWORD_PAGE) && !url.equals(REGISTER_PAGE)) {
                url = listUsers(request);
            }

        } catch (ValidationException e) {
            log("Lỗi Validation tại UserController (doPost): " + e.getMessage());
            handleServiceException(request, e, action, errorFormPage);
            url = errorFormPage;
            redirectAfterSuccess = false;

        } catch (Exception e) {
            log("Lỗi Hệ thống tại UserController (doPost): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Lỗi hệ thống: " + e.getMessage());
            url = ERROR_PAGE;
            redirectAfterSuccess = false;

        } finally {
            if (redirectAfterSuccess) {
                if (action.equals(ACTION_REGISTER)) { // Dùng hằng số
                    response.sendRedirect(successRedirectUrl);
                } else {
                    String finalUrl = request.getContextPath() + "/" + successRedirectUrl;
                    response.sendRedirect(response.encodeRedirectURL(finalUrl));
                }
            } else {
                request.getRequestDispatcher(url).forward(request, response);
            }
        }
    }

    private String login(HttpServletRequest request, HttpServletResponse response)
            throws ValidationException, Exception {
        String tenDangNhap = request.getParameter("username");
        String matKhau = request.getParameter("password");

        // **CẬP NHẬT:** Không cần comment. 
        // Service sẽ ném ValidationException nếu 'CHUA_XAC_THUC'.
        // Khối catch trong doPost sẽ bắt lỗi này và hiển thị trên LOGIN_PAGE.
        TaiKhoanDTO user = taiKhoanService.login(tenDangNhap, matKhau);
        
        NhanVienDTO userInfo = nhanVienService.getNhanVienByTaiKhoanId(user.getId());//Dat 
        HttpSession session = request.getSession();
        
        
        session.setAttribute("USER", user);
        session.setAttribute("ROLE", user.getVaiTro());
        
        //=====Dat=======
        session.setAttribute("LOGIN_USER_INFO", userInfo);
        session.setAttribute("LOGIN_ACCOUNT", user);
        //=====Dat-END=====
        
        
        if ("CAN_DOI".equals(user.getTrangThaiMatKhau())) {
            session.setAttribute("FORCE_CHANGE_PASS_MSG",
                    "Đây là lần đăng nhập đầu tiên. Vì lý do bảo mật, bạn phải đổi mật khẩu ngay lập tức.");
            return CHANGE_PASSWORD_PAGE;
        }
        if ("QUAN_TRI".equals(user.getVaiTro())) {
            return ADMIN_DASHBOARD_PAGE;
        } else if ("BAC_SI".equals(user.getVaiTro()) || "LE_TAN".equals(user.getVaiTro())) {
            return STAFF_DASHBOARD_PAGE;
        } else {
            return HOME_PAGE; // Bệnh nhân
        }
    }

    // (changePassword, createUser giữ nguyên)
    private String changePassword(HttpServletRequest request) throws ValidationException, Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER") == null) {
            throw new ValidationException("Bạn cần đăng nhập để thực hiện chức năng này.");
        }
        TaiKhoanDTO currentUser = (TaiKhoanDTO) session.getAttribute("USER");
        int userId = currentUser.getId();
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        if (oldPassword == null || newPassword == null || confirmPassword == null
                || oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            throw new ValidationException("Vui lòng nhập đầy đủ mật khẩu cũ, mới và xác nhận.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new ValidationException("Mật khẩu mới và xác nhận không khớp.");
        }
        taiKhoanService.changePassword(userId, oldPassword, newPassword);
        session.removeAttribute("FORCE_CHANGE_PASS_MSG");
        session.setAttribute("SUCCESS_MESSAGE", "Đổi mật khẩu thành công!");
        if ("QUAN_TRI".equals(currentUser.getVaiTro())) {
            return ADMIN_DASHBOARD_PAGE;
        } else if ("BAC_SI".equals(currentUser.getVaiTro()) || "LE_TAN".equals(currentUser.getVaiTro())) {
            return STAFF_DASHBOARD_PAGE;
        } else {
            return HOME_PAGE;
        }
    }

    private String createUser(HttpServletRequest request) throws ValidationException, Exception {
        TaiKhoanDTO newTaiKhoanDTO = createDTOFromRequest(request);
        String plainPassword = request.getParameter("password");
        if (plainPassword == null || plainPassword.length() < 6) {
            throw new ValidationException("Mật khẩu tạm thời phải có ít nhất 6 ký tự.");
        }
        // Service sẽ tự set trạng thái và token
        TaiKhoanDTO result = taiKhoanService.createTaiKhoan(newTaiKhoanDTO, plainPassword);
        request.setAttribute("SUCCESS_MESSAGE", "Tạo tài khoản '" + result.getTenDangNhap() + "' thành công!");
        return USER_LIST_PAGE;
    }

    // --- **BẮT ĐẦU SỬA HÀM register (GIAI ĐOẠN 3)** ---
    /**
     * Xử lý logic đăng ký. **ĐÃ CẬP NHẬT:** * 1. Không set trạng thái (Service
     * sẽ set 'CHUA_XAC_THUC'). 2. Gọi EmailUtils để gửi mail xác thực. 3. Thay
     * đổi thông báo thành công.
     */
    private String register(HttpServletRequest request) throws ValidationException, Exception {
        String tenDangNhap = request.getParameter("username");
        String email = request.getParameter("email");
        String matKhau = request.getParameter("password");
        String confirmMatKhau = request.getParameter("confirmPassword");

        // --- VALIDATION (Giữ nguyên) ---
        if (tenDangNhap == null || email == null || matKhau == null || confirmMatKhau == null
                || tenDangNhap.trim().isEmpty() || email.trim().isEmpty() || matKhau.isEmpty()) {
            throw new ValidationException("Vui lòng điền đầy đủ thông tin.");
        }
        if (!matKhau.equals(confirmMatKhau)) {
            throw new ValidationException("Mật khẩu và xác nhận mật khẩu không khớp.");
        }

        // --- BƯỚC 1: TẠO TÀI KHOẢN (Service sẽ set 'CHUA_XAC_THUC') ---
        TaiKhoanDTO dto = new TaiKhoanDTO();
        dto.setTenDangNhap(tenDangNhap);
        dto.setEmail(email);
        dto.setVaiTro(ROLE_BENH_NHAN); // Dùng hằng số

        // **XÓA BỎ DÒNG NÀY:**
        // dto.setTrangThai("HOAT_DONG"); 
        // Service (createTaiKhoan) sẽ tự động set 'CHUA_XAC_THUC' và tạo token
        TaiKhoanDTO savedTaiKhoan = taiKhoanService.createTaiKhoan(dto, matKhau);
        if (savedTaiKhoan == null || savedTaiKhoan.getId() <= 0) {
            throw new Exception("Tạo tài khoản thất bại (Service trả về null).");
        }

        // --- BƯỚC 2: (ĐÃ XÓA - Logic BenhNhan) ---
        // --- BƯỚC 3: GỬI EMAIL (KÍCH HOẠT) ---
        try {
            // Lấy token mà Service vừa tạo (thông qua hàm mới của Service)
            String token = taiKhoanService.findVerificationTokenByEmail(email);

            // --- **BẮT ĐẦU SỬA (FIX LỖI CỦA TÔI)** ---
            // Gọi tiện ích gửi mail với 3 tham số (email, tenDangNhap, token)
            EmailUtils.sendVerificationEmail(email, tenDangNhap, token);
            // --- **KẾT THÚC SỬA** ---

        } catch (Exception e) {
            // QUAN TRỌNG: Không ném lỗi ra ngoài. 
            // Việc đăng ký đã thành công, chỉ là gửi mail thất bại.
            // Log lại để admin kiểm tra (vd: sai mật khẩu email, server mail down)
            log("LỖI NGHIÊM TRỌNG KHI GỬI EMAIL: " + e.getMessage(), e);
        }

        // --- BƯỚC 4: TRẢ VỀ TRANG LOGIN VỚI THÔNG BÁO MỚI ---
        HttpSession session = request.getSession(true);
        // **THAY ĐỔI THÔNG BÁO**
        session.setAttribute("SUCCESS_MESSAGE", "Đăng ký gần hoàn tất! Vui lòng kiểm tra email (cả mục Spam) để kích hoạt tài khoản.");
        return LOGIN_PAGE;
    }
    // --- **KẾT THÚC SỬA HÀM register** ---

    // --- (Hàm verifyAccount đã bị xóa, chuyển sang VerifyController) ---
    // (Các hàm còn lại: handleServiceException, logout, listUsers, v.v... giữ nguyên)
    private void handleServiceException(HttpServletRequest request, Exception e, String formAction, String errorFormPage) {
        request.setAttribute("ERROR_MESSAGE", e.getMessage());
        if (ACTION_REGISTER.equals(formAction)) { // Dùng hằng số
            request.setAttribute("username_register", request.getParameter("username"));
            request.setAttribute("email_register", request.getParameter("email"));
            request.setAttribute("formAction", ACTION_REGISTER);
        } else if (ACTION_CREATE_USER.equals(formAction) || ACTION_UPDATE_STATUS.equals(formAction)) {
            request.setAttribute("USER_DATA", createDTOFromRequest(request));
            request.setAttribute("formAction", formAction);
        }
    }

    private String logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return LOGIN_PAGE;
    }

    private String listUsers(HttpServletRequest request) throws Exception {
        List<TaiKhoanDTO> list = taiKhoanService.getAllTaiKhoan();
        request.setAttribute("LIST_TAIKHOAN", list);
        return USER_LIST_PAGE;
    }

    private String showUserEditForm(HttpServletRequest request) throws Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            TaiKhoanDTO taiKhoan = taiKhoanService.getTaiKhoanById(id);
            request.setAttribute("USER_DATA", taiKhoan);
            request.setAttribute("formAction", "updateUserStatus");
            return USER_FORM_PAGE;
        } catch (NumberFormatException e) {
            throw new ValidationException("ID Tài khoản không hợp lệ.");
        }
    }

    private String updateUserStatus(HttpServletRequest request) throws Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String newTrangThai = request.getParameter("trangThai");
            TaiKhoanDTO result = taiKhoanService.updateTrangThaiTaiKhoan(id, newTrangThai);
            request.setAttribute("SUCCESS_MESSAGE", "Cập nhật trạng thái tài khoản '" + result.getTenDangNhap() + "' thành công!");
            return USER_LIST_PAGE;
        } catch (NumberFormatException e) {
            throw new ValidationException("ID Tài khoản không hợp lệ khi cập nhật trạng thái.");
        }
    }

    private TaiKhoanDTO createDTOFromRequest(HttpServletRequest request) {
        TaiKhoanDTO dto = new TaiKhoanDTO();
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                dto.setId(Integer.parseInt(idStr));
            } catch (NumberFormatException e) {
                /* ignore */ }
        }
        dto.setTenDangNhap(request.getParameter("tenDangNhap"));
        dto.setEmail(request.getParameter("email"));
        dto.setVaiTro(request.getParameter("vaiTro"));
        dto.setTrangThai(request.getParameter("trangThai"));
        return dto;
    }

    @Override
    public String getServletInfo() {
        return "Controller xử lý đăng nhập, đăng xuất và quản lý người dùng.";
    }
}
