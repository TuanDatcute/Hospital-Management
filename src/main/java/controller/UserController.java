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

// --- **MERGE:** Thêm tất cả import cần thiết từ cả 2 nhánh ---
import model.Entity.TaiKhoan;
import model.dto.BenhNhanDTO;
import model.dto.NhanVienDTO; // <-- Từ nhánh 'main'
import model.dto.TaiKhoanDTO;
import service.BenhNhanService;
import service.NhanVienService; // <-- Từ nhánh 'main'
import service.TaiKhoanService;
import util.EmailUtils;
// --- **KẾT THÚC MERGE** ---

/**
 * Controller xử lý các nghiệp vụ liên quan đến người dùng. **ĐÃ CẬP NHẬT (Giai
 * đoạn 5):** Đã merge logic Auth và logic NhanVienInfo.
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

    // **MERGE:** Giữ hằng số từ nhánh của bạn
    private static final String VERIFY_EMAIL_PAGE = "verifyEmail.jsp";

    // (Các hằng số ACTION... giữ nguyên)
    private static final String ACTION_LOGIN = "login";
    private static final String ACTION_REGISTER = "register";
    private static final String ACTION_CREATE_USER = "createUser";
    private static final String ACTION_UPDATE_STATUS = "updateUserStatus";
    private static final String ACTION_CHANGE_PASSWORD = "changePassword";
    private static final String ROLE_BENH_NHAN = "BENH_NHAN";
    private static final String ACTION_RESEND_VERIFICATION = "resendVerification";

    private final TaiKhoanService taiKhoanService = new TaiKhoanService();
    private final BenhNhanService benhNhanService = new BenhNhanService();
    // **MERGE:** Thêm NhanVienService từ nhánh 'main'
    private final NhanVienService nhanVienService = new NhanVienService();

    /**
     * (doGet... giữ nguyên từ nhánh của bạn)
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
     * (doPost... đã merge)
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

            // **MERGE:** Giữ logic gán errorFormPage từ nhánh của bạn
            if (ACTION_LOGIN.equals(action)) {
                errorFormPage = LOGIN_PAGE;
            } else if (ACTION_REGISTER.equals(action)) {
                errorFormPage = REGISTER_PAGE;
            } else if (ACTION_RESEND_VERIFICATION.equals(action)) {
                errorFormPage = VERIFY_EMAIL_PAGE;
            } else if (ACTION_CREATE_USER.equals(action) || ACTION_UPDATE_STATUS.equals(action)) {
                errorFormPage = USER_FORM_PAGE;
            } else if (ACTION_CHANGE_PASSWORD.equals(action)) {
                errorFormPage = CHANGE_PASSWORD_PAGE;
            }

            switch (action) {
                case ACTION_LOGIN:
                    url = login(request, response);

                    if (url.startsWith("BenhNhanController")) {
                        redirectAfterSuccess = true;
                        successRedirectUrl = url;
                    } else {
                        redirectAfterSuccess = (url.equals(HOME_PAGE) || url.equals(ADMIN_DASHBOARD_PAGE) || url.equals(STAFF_DASHBOARD_PAGE) || url.equals(CHANGE_PASSWORD_PAGE));
                        successRedirectUrl = url;
                    }
                    break;
                case ACTION_CREATE_USER:
                    url = createUser(request);
                    successRedirectUrl = USER_LIST_PAGE;
                    redirectAfterSuccess = true;
                    break;
                case ACTION_UPDATE_STATUS:
                    url = updateUserStatus(request);
                    successRedirectUrl = USER_LIST_PAGE;
                    redirectAfterSuccess = true;
                    break;
                case ACTION_CHANGE_PASSWORD:
                    url = changePassword(request);
                    redirectAfterSuccess = (url.equals(HOME_PAGE) || url.equals(ADMIN_DASHBOARD_PAGE) || url.equals(STAFF_DASHBOARD_PAGE));
                    successRedirectUrl = url;
                    break;
                // **MERGE:** Giữ logic 'register' và 'resend' từ nhánh của bạn
                case ACTION_REGISTER:
                    url = register(request);
                    break;
                case ACTION_RESEND_VERIFICATION:
                    url = resendVerification(request);
                    break;
                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho POST.");
            }

            if (!redirectAfterSuccess && !url.equals(ERROR_PAGE) && !url.equals(USER_FORM_PAGE)
                    && !url.equals(CHANGE_PASSWORD_PAGE) && !url.equals(REGISTER_PAGE)
                    && !url.equals(VERIFY_EMAIL_PAGE)) {
                url = listUsers(request);
            }

        } catch (ValidationException e) {
            log("Lỗi Validation tại UserController (doPost): " + e.getMessage());

            // **MERGE:** Giữ logic xử lý lỗi từ nhánh của bạn
            if (ACTION_RESEND_VERIFICATION.equals(action)) {
                request.setAttribute("ERROR_MESSAGE", e.getMessage());
                request.setAttribute("email", request.getParameter("email"));
            } else {
                handleServiceException(request, e, action, errorFormPage);
            }

            url = errorFormPage;
            redirectAfterSuccess = false;

        } catch (Exception e) {
            log("Lỗi Hệ thống tại UserController (doPost): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Lỗi hệ thống: " + e.getMessage());
            url = ERROR_PAGE;
            redirectAfterSuccess = false;

        } finally {
            if (redirectAfterSuccess) {
                String finalUrl = request.getContextPath() + "/" + successRedirectUrl;
                response.sendRedirect(response.encodeRedirectURL(finalUrl));
            } else {
                request.getRequestDispatcher(url).forward(request, response);
            }
        }
    }

    /**
     * Xử lý đăng nhập. **ĐÃ MERGE:** Kết hợp logic lấy NhanVienDTO (từ 'main')
     * và logic "Ép điền hồ sơ" (từ nhánh của bạn).
     */
    private String login(HttpServletRequest request, HttpServletResponse response)
            throws ValidationException, Exception {

        String tenDangNhap = request.getParameter("username");
        String matKhau = request.getParameter("password");

        // --- (Logic từ 'main') ---
        TaiKhoanDTO user = taiKhoanService.login(tenDangNhap, matKhau);

        // **MERGE:** Lấy NhanVienDTO (logic của nhánh 'main')
        NhanVienDTO userInfo = null;
        try {
            // Chỉ lấy thông tin nhân viên nếu họ không phải là bệnh nhân
            if (!ROLE_BENH_NHAN.equals(user.getVaiTro())) {
                userInfo = nhanVienService.getNhanVienByTaiKhoanId(user.getId());
            }
        } catch (Exception e) {
            log("Lỗi khi lấy NhanVienDTO cho TaiKhoan ID: " + user.getId() + ". " + e.getMessage());
            // (Không ném lỗi, chỉ log lại, vẫn cho phép đăng nhập)
        }

        HttpSession session = request.getSession();

        session.setAttribute("USER", user);
        session.setAttribute("ROLE", user.getVaiTro());

        // **MERGE:** Thêm các thuộc tính session từ nhánh 'main'
        session.setAttribute("LOGIN_USER_INFO", userInfo); // (Có thể là null nếu là Bệnh nhân)
        session.setAttribute("LOGIN_ACCOUNT", user);

        // --- (Logic từ nhánh của bạn) ---
        // 1. Kiểm tra ép đổi mật khẩu
        if ("CAN_DOI".equals(user.getTrangThaiMatKhau())) {
            session.setAttribute("FORCE_CHANGE_PASS_MSG",
                    "Đây là lần đăng nhập đầu tiên. Vì lý do bảo mật, bạn phải đổi mật khẩu ngay lập tức.");
            return CHANGE_PASSWORD_PAGE;
        }

        // 2. Phân luồng theo vai trò
        if ("QUAN_TRI".equals(user.getVaiTro())) {
            return ADMIN_DASHBOARD_PAGE;
        } else if ("BAC_SI".equals(user.getVaiTro()) || "LE_TAN".equals(user.getVaiTro())) {
            return STAFF_DASHBOARD_PAGE;

            // 3. Phân luồng cho Bệnh nhân (Logic ép điền hồ sơ của bạn)
        } else {
            BenhNhanDTO profile = null;
            try {
                profile = benhNhanService.getBenhNhanByTaiKhoanId(user.getId());
            } catch (Exception e) {
                log("Lỗi khi kiểm tra hồ sơ bệnh nhân lúc login (ID: " + user.getId() + "): " + e.getMessage());
            }

            if (profile == null) {
                // **KỊCH BẢN A: CHƯA CÓ HỒ SƠ**
                return "BenhNhanController?action=showEditProfile"; // (Đúng)
            } else {
                // **KỊCH BẢN B: ĐÃ CÓ HỒ SƠ**
                return HOME_PAGE;
            }
        }
    }

    // (changePassword... giữ nguyên)
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

    // (createUser... giữ nguyên)
    private String createUser(HttpServletRequest request) throws ValidationException, Exception {
        TaiKhoanDTO newTaiKhoanDTO = createDTOFromRequest(request);
        String plainPassword = request.getParameter("password");
        if (plainPassword == null || plainPassword.length() < 6) {
            throw new ValidationException("Mật khẩu tạm thời phải có ít nhất 6 ký tự.");
        }
        TaiKhoanDTO result = taiKhoanService.createTaiKhoan(newTaiKhoanDTO, plainPassword);
        request.setAttribute("SUCCESS_MESSAGE", "Tạo tài khoản '" + result.getTenDangNhap() + "' thành công!");
        return USER_LIST_PAGE;
    }

    /**
     * (Hàm register... giữ nguyên từ nhánh của bạn)
     */
    private String register(HttpServletRequest request) throws ValidationException, Exception {
        String tenDangNhap = request.getParameter("username");
        String email = request.getParameter("email");
        String matKhau = request.getParameter("password");
        String confirmMatKhau = request.getParameter("confirmPassword");

        if (tenDangNhap == null || email == null || matKhau == null || confirmMatKhau == null
                || tenDangNhap.trim().isEmpty() || email.trim().isEmpty() || matKhau.isEmpty()) {
            throw new ValidationException("Vui lòng điền đầy đủ thông tin.");
        }
        if (!matKhau.equals(confirmMatKhau)) {
            throw new ValidationException("Mật khẩu và xác nhận mật khẩu không khớp.");
        }

        TaiKhoanDTO dto = new TaiKhoanDTO();
        dto.setTenDangNhap(tenDangNhap);
        dto.setEmail(email);
        dto.setVaiTro(ROLE_BENH_NHAN);

        TaiKhoanDTO savedTaiKhoan = taiKhoanService.createTaiKhoan(dto, matKhau);
        if (savedTaiKhoan == null || savedTaiKhoan.getId() <= 0) {
            throw new Exception("Tạo tài khoản thất bại (Service trả về null).");
        }

        try {
            String token = taiKhoanService.findVerificationTokenByEmail(email);
            EmailUtils.sendVerificationEmail(email, tenDangNhap, token);
        } catch (Exception e) {
            log("LỖI NGHIÊM TRỌNG KHI GỬI EMAIL: " + e.getMessage(), e);
        }

        request.setAttribute("email", email);
        return VERIFY_EMAIL_PAGE;
    }

    /**
     * (resendVerification... giữ nguyên từ nhánh của bạn)
     */
    private String resendVerification(HttpServletRequest request) throws ValidationException, Exception {
        String email = request.getParameter("email");

        try {
            TaiKhoan taiKhoan = taiKhoanService.resendVerificationEmail(email);

            EmailUtils.sendVerificationEmail(
                    taiKhoan.getEmail(),
                    taiKhoan.getTenDangNhap(),
                    taiKhoan.getVerificationToken()
            );

            request.setAttribute("SUCCESS_MESSAGE", "Đã gửi lại email thành công!");

        } catch (ValidationException e) {
            throw e;
        } catch (Exception e) {
            log("LỖI GỬI LẠI EMAIL: " + e.getMessage(), e);
            throw new ValidationException("Gửi lại email thất bại. Vui lòng thử lại sau.");
        }

        request.setAttribute("email", email);
        return VERIFY_EMAIL_PAGE;
    }

    /**
     * (handleServiceException... giữ nguyên từ nhánh của bạn)
     */
    private void handleServiceException(HttpServletRequest request, Exception e, String formAction, String errorFormPage) {
        request.setAttribute("ERROR_MESSAGE", e.getMessage());
        if (ACTION_REGISTER.equals(formAction)) {
            request.setAttribute("username_register", request.getParameter("username"));
            request.setAttribute("email_register", request.getParameter("email"));
            request.setAttribute("formAction", ACTION_REGISTER);
        } else if (ACTION_RESEND_VERIFICATION.equals(formAction)) {
            request.setAttribute("email", request.getParameter("email"));
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
