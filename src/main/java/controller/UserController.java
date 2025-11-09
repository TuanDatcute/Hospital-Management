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

import model.Entity.TaiKhoan;
import model.dto.BenhNhanDTO;
import model.dto.NhanVienDTO;
import model.dto.TaiKhoanDTO;
import service.BenhNhanService;
import service.NhanVienService;
import service.TaiKhoanService;
import util.EmailUtils;

/**
 * Controller xử lý các nghiệp vụ liên quan đến người dùng. (PHIÊN BẢN GỘP: Giữ
 * logic Redirect của Bản 1 + Sửa lỗi Session của Bản 2)
 */
@WebServlet(name = "UserController", urlPatterns = {"/UserController"})
public class UserController extends HttpServlet {

    // (Các hằng số JSP giữ nguyên từ Bản 1)
    private static final String LOGIN_PAGE = "login.jsp";
    private static final String HOME_PAGE = "index.jsp";
    private static final String ADMIN_DASHBOARD_PAGE = "admin/dashboard.jsp";
    private static final String STAFF_DASHBOARD_PAGE = "staff/dashboard.jsp";
    private static final String USER_LIST_PAGE = "admin/danhSachTaiKhoan.jsp";
    private static final String USER_FORM_PAGE = "admin/formTaiKhoan.jsp";
    private static final String CHANGE_PASSWORD_PAGE = "user/changePassword.jsp";
    private static final String REGISTER_PAGE = "login.jsp";
    private static final String ERROR_PAGE = "error.jsp";
    private static final String VERIFY_EMAIL_PAGE = "verifyEmail.jsp";

    // (Các hằng số ACTION giữ nguyên từ Bản 1)
    private static final String ACTION_LOGIN = "login";
    private static final String ACTION_REGISTER = "register";
    private static final String ACTION_CREATE_USER = "createUser";
    private static final String ACTION_UPDATE_STATUS = "updateUserStatus";
    private static final String ACTION_CHANGE_PASSWORD = "changePassword";
    private static final String ROLE_BENH_NHAN = "BENH_NHAN";
    private static final String ACTION_RESEND_VERIFICATION = "resendVerification";

    // Hằng số Phân trang
    private static final int PAGE_SIZE = 10;

    // (Các Service giữ nguyên từ Bản 1)
    private final TaiKhoanService taiKhoanService = new TaiKhoanService();
    private final BenhNhanService benhNhanService = new BenhNhanService();
    private final NhanVienService nhanVienService = new NhanVienService();

    // === HELPER METHOD (Lấy từ Bản 1) ===
    private String getRedirectUrl(HttpServletRequest request, String jspPath) {
        // Trả về chuỗi "redirect:/ContextPath/jspPath"
        return "redirect:" + request.getContextPath() + "/" + jspPath;
    }
    // ========================================================================

    // (Hàm doGet giữ nguyên từ Bản 1)
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

    // (Hàm doPost giữ nguyên từ Bản 1)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        String url = ERROR_PAGE;
        String errorFormPage = ERROR_PAGE;
        try {
            if (action == null || action.isEmpty()) {
                throw new Exception("Hành động không được chỉ định.");
            }
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
                        url = "redirect:" + url;
                    }
                    break;
                case ACTION_CREATE_USER:
                    url = createUser(request);
                    break;
                case ACTION_UPDATE_STATUS:
                    url = updateUserStatus(request);
                    break;
                case ACTION_CHANGE_PASSWORD:
                    url = changePassword(request);
                    break;
                case ACTION_REGISTER:
                    url = register(request);
                    break;
                case ACTION_RESEND_VERIFICATION:
                    url = resendVerification(request);
                    break;
                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho POST.");
            }
        } catch (ValidationException e) {
            log("Lỗi Validation tại UserController (doPost): " + e.getMessage());
            if (ACTION_RESEND_VERIFICATION.equals(action)) {
                request.setAttribute("ERROR_MESSAGE", e.getMessage());
                request.setAttribute("email", request.getParameter("email"));
            } else {
                handleServiceException(request, e, action, errorFormPage);
            }
            url = errorFormPage;
        } catch (Exception e) {
            log("Lỗi Hệ thống tại UserController (doPost): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Lỗi hệ thống: " + e.getMessage());
            url = ERROR_PAGE;
        } finally {
            if (url.startsWith("redirect:")) {
                String redirectUrl = url.substring("redirect:".length());
                response.sendRedirect(redirectUrl);
            } else {
                request.getRequestDispatcher(url).forward(request, response);
            }
        }
    }

// (Hàm login giữ nguyên từ Bản 1 - ĐÃ SỬA THEO YÊU CẦU CỦA BẠN)
    private String login(HttpServletRequest request, HttpServletResponse response) throws ValidationException, Exception {
        String tenDangNhap = request.getParameter("username");
        String matKhau = request.getParameter("password");
        TaiKhoanDTO user = taiKhoanService.login(tenDangNhap, matKhau);
        NhanVienDTO userInfo = null;
        try {
            if (!ROLE_BENH_NHAN.equals(user.getVaiTro())) {
                userInfo = nhanVienService.getNhanVienByTaiKhoanId(user.getId());
            }
        } catch (Exception e) {
            log("Lỗi khi lấy NhanVienDTO cho TaiKhoan ID: " + user.getId() + ". " + e.getMessage());
        }
        HttpSession session = request.getSession();
        session.setAttribute("USER", user);
        session.setAttribute("ROLE", user.getVaiTro());
        session.setAttribute("LOGIN_USER_INFO", userInfo);
        session.setAttribute("LOGIN_ACCOUNT", user);

        if ("CAN_DOI".equals(user.getTrangThaiMatKhau())) {
            session.setAttribute("FORCE_CHANGE_PASS_MSG", "Đây là lần đăng nhập đầu tiên. Vì lý do bảo mật, bạn phải đổi mật khẩu ngay lập tức.");
            return getRedirectUrl(request, CHANGE_PASSWORD_PAGE);
        }

        // === BẮT ĐẦU THAY ĐỔI ===
        if ("QUAN_TRI".equals(user.getVaiTro())) {
            return getRedirectUrl(request, ADMIN_DASHBOARD_PAGE);

        } else if ("BAC_SI".equals(user.getVaiTro())) {
            // YÊU CẦU CỦA BẠN: Chuyển hướng Bác Sĩ đến action listAllEncounters
            // Chúng ta trả về chuỗi "redirect:" giống như các hàm createUser/updateUserStatus
            return "redirect:mainController?action=listAllEncounters";

        } else if ("LE_TAN".equals(user.getVaiTro())
                || "DUOC_SI".equals(user.getVaiTro())
                || "Y_TA".equals(user.getVaiTro())
                || "KY_THUAT_VIEN".equals(user.getVaiTro())) {
            // Các vai trò nhân viên khác vẫn vào dashboard chung
            return getRedirectUrl(request, STAFF_DASHBOARD_PAGE);

        } else { // Vai trò còn lại là BENH_NHAN
            BenhNhanDTO profile = null;
            try {
                profile = benhNhanService.getBenhNhanByTaiKhoanId(user.getId());
            } catch (Exception e) {
                log("Lỗi khi kiểm tra hồ sơ bệnh nhân lúc login (ID: " + user.getId() + "): " + e.getMessage());
            }
            if (profile == null) {
                return "BenhNhanController?action=showEditProfile"; // OK: Controller to Controller
            } else {
                return getRedirectUrl(request, HOME_PAGE);
            }
        }
        // === KẾT THÚC THAY ĐỔI ===
    }

    /**
     * === HÀM GỘP (Lấy logic Sửa lỗi Session từ Bản 2) ===
     */
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

        // 1. Cập nhật CSDL (Logic từ Bản 2)
        taiKhoanService.changePassword(userId, oldPassword, newPassword);

        // 2. Lấy lại DTO mới nhất từ CSDL (đã có 'DA_DOI') (Logic từ Bản 2)
        TaiKhoanDTO updatedUser = taiKhoanService.getTaiKhoanById(userId);

        // 3. Cập nhật Session (Logic từ Bản 2)
        session.removeAttribute("FORCE_CHANGE_PASS_MSG");
        session.setAttribute("SUCCESS_MESSAGE", "Đổi mật khẩu thành công!");
        session.setAttribute("USER", updatedUser);
        session.setAttribute("LOGIN_ACCOUNT", updatedUser);
        // === KẾT THÚC SỬA ===

        // 4. Chuyển hướng (Logic từ Bản 1, nhưng dùng 'updatedUser')
        String vaiTro = updatedUser.getVaiTro(); // Sửa: Dùng updatedUser
        String redirectJSP;

        if ("QUAN_TRI".equals(vaiTro)) {
            redirectJSP = ADMIN_DASHBOARD_PAGE;
        } else if ("BAC_SI".equals(vaiTro)
                || "LE_TAN".equals(vaiTro)
                || "DUOC_SI".equals(vaiTro)
                || "Y_TA".equals(vaiTro)
                || "KY_THUAT_VIEN".equals(vaiTro)) {
            redirectJSP = STAFF_DASHBOARD_PAGE;
        } else if ("BENH_NHAN".equals(vaiTro)) {
            redirectJSP = HOME_PAGE;
        } else {
            log("Vai trò không xác định khi đổi mật khẩu: " + vaiTro);
            redirectJSP = STAFF_DASHBOARD_PAGE;
        }

        // Trả về URL Redirect đầy đủ (Logic từ Bản 1)
        return getRedirectUrl(request, redirectJSP);
    }

    // (Tất cả các hàm còn lại giữ nguyên từ Bản 1 vì chúng đã "xịn" rồi)
    private String register(HttpServletRequest request) throws ValidationException, Exception {
        /* (Giữ nguyên) */
        String tenDangNhap = request.getParameter("username");
        String email = request.getParameter("email");
        String matKhau = request.getParameter("password");
        String confirmMatKhau = request.getParameter("confirmPassword");
        if (tenDangNhap == null || email == null || matKhau == null || confirmMatKhau == null || tenDangNhap.trim().isEmpty() || email.trim().isEmpty() || matKhau.isEmpty()) {
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

    private String resendVerification(HttpServletRequest request) throws ValidationException, Exception {
        /* (Giữ nguyên) */
        String email = request.getParameter("email");
        try {
            TaiKhoan taiKhoan = taiKhoanService.resendVerificationEmail(email);
            EmailUtils.sendVerificationEmail(taiKhoan.getEmail(), taiKhoan.getTenDangNhap(), taiKhoan.getVerificationToken());
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

    private void handleServiceException(HttpServletRequest request, Exception e, String formAction, String errorFormPage) {
        /* (Giữ nguyên) */
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
        /* (Giữ nguyên) */
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        return "index.jsp";
    }

    private String showUserEditForm(HttpServletRequest request) throws Exception {
        /* (Giữ nguyên) */
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

    private TaiKhoanDTO createDTOFromRequest(HttpServletRequest request) {
        /* (Giữ nguyên) */
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
        /* (Giữ nguyên) */
        return "Controller xử lý đăng nhập, đăng xuất và quản lý người dùng.";
    }

    // === CÁC HÀM ADMIN KHÁC (Giữ nguyên từ Bản 1) ===
    private String listUsers(HttpServletRequest request) throws Exception {
        // ... (Logic giữ nguyên)
        int page = 1;
        String pageStr = request.getParameter("page");
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        String keyword = request.getParameter("keyword");
        List<TaiKhoanDTO> list;
        long totalTaiKhoan;

        if (keyword != null && !keyword.trim().isEmpty()) {
            String trimmedKeyword = keyword.trim();
            list = taiKhoanService.searchTaiKhoanPaginated(trimmedKeyword, page, PAGE_SIZE);
            totalTaiKhoan = taiKhoanService.getTaiKhoanSearchCount(trimmedKeyword);
            request.setAttribute("searchKeyword", keyword);
        } else {
            list = taiKhoanService.getAllTaiKhoanPaginated(page, PAGE_SIZE);
            totalTaiKhoan = taiKhoanService.getTaiKhoanCount();
        }

        long totalPages = (long) Math.ceil((double) totalTaiKhoan / PAGE_SIZE);

        request.setAttribute("LIST_TAIKHOAN", list);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        return USER_LIST_PAGE;
    }

    private String createUser(HttpServletRequest request) throws ValidationException, Exception {
        TaiKhoanDTO newTaiKhoanDTO = createDTOFromRequest(request);
        String plainPassword = request.getParameter("password");

        if (plainPassword == null || plainPassword.length() < 6) {
            throw new ValidationException("Mật khẩu tạm thời phải có ít nhất 6 ký tự.");
        }

        TaiKhoanDTO result = taiKhoanService.createTaiKhoan(newTaiKhoanDTO, plainPassword);

        request.getSession().setAttribute("SUCCESS_MESSAGE", "Tạo tài khoản '" + result.getTenDangNhap() + "' thành công!");

        return "redirect:MainController?action=listUsers";
    }

    private String updateUserStatus(HttpServletRequest request) throws ValidationException, Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String newTrangThai = request.getParameter("trangThai");
            TaiKhoanDTO result = taiKhoanService.updateTrangThaiTaiKhoan(id, newTrangThai);

            String message = "Mở khóa";
            if ("BI_KHOA".equals(newTrangThai)) {
                message = "Khóa";
            }
            request.getSession().setAttribute("SUCCESS_MESSAGE", message + " tài khoản '" + result.getTenDangNhap() + "' thành công!");

            return "redirect:MainController?action=listUsers";

        } catch (NumberFormatException e) {
            throw new ValidationException("ID Tài khoản không hợp lệ khi cập nhật trạng thái.");
        }
    }
    // === KẾT THÚC CÁC HÀM ADMIN KHÁC ===
}
