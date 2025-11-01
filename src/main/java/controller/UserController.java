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
import model.dto.BenhNhanDTO;
// --- KẾT THÚC THÊM MỚI ---

import model.dto.TaiKhoanDTO;

// --- THÊM MỚI ---
import service.BenhNhanService;
// --- KẾT THÚC THÊM MỚI ---

import service.TaiKhoanService;

/**
 * Controller xử lý các nghiệp vụ liên quan đến người dùng (đăng nhập, đăng
 * xuất, quản lý tài khoản...).
 */
@WebServlet(name = "UserController", urlPatterns = {"/UserController"})
public class UserController extends HttpServlet {

    // (Các hằng số trang JSP... giữ nguyên)
    private static final String LOGIN_PAGE = "login.jsp";
    private static final String HOME_PAGE = "home.jsp"; // Trang của Bệnh nhân
    private static final String ADMIN_DASHBOARD_PAGE = "admin/dashboard.jsp"; // Trang của Admin
    private static final String STAFF_DASHBOARD_PAGE = "staff/dashboard.jsp"; // Trang của Nhân viên

    private static final String USER_LIST_PAGE = "admin/danhSachTaiKhoan.jsp";
    private static final String USER_FORM_PAGE = "admin/formTaiKhoan.jsp";
    private static final String CHANGE_PASSWORD_PAGE = "user/changePassword.jsp";
    private static final String REGISTER_PAGE = "login.jsp"; // (Đã gộp)
    private static final String ERROR_PAGE = "error.jsp";

    private final TaiKhoanService taiKhoanService = new TaiKhoanService();

    // --- THÊM MỚI: Khởi tạo BenhNhanService ---
    private final BenhNhanService benhNhanService = new BenhNhanService();
    // --- KẾT THÚC THÊM MỚI ---

    // (doGet... giữ nguyên)
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
                    // (case 'showUserRegister' đã bị xóa là đúng)
                    default:
                        request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho GET.");
                }
            }
        } catch (Exception e) {
            log("Lỗi tại UserController (doGet): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi: " + e.getMessage());
        } finally {
            if (url.equals(LOGIN_PAGE) && action != null && action.equals("logout")) {
                response.sendRedirect(request.getContextPath() + "/" + url);
            } else {
                request.getRequestDispatcher(url).forward(request, response);
            }
        }
    }

    // (doPost... giữ nguyên)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String url = ERROR_PAGE;
        boolean redirectAfterSuccess = false;
        String successRedirectUrl = null;

        String errorFormPage = ERROR_PAGE; // Trang quay về nếu lỗi Validation

        try {
            if (action == null || action.isEmpty()) {
                throw new Exception("Hành động không được chỉ định.");
            }

            // Xác định trang quay về nếu lỗi
            if ("login".equals(action)) {
                errorFormPage = LOGIN_PAGE;
            } else if ("register".equals(action)) {
                errorFormPage = REGISTER_PAGE;
            } else if ("createUser".equals(action) || "updateUserStatus".equals(action)) {
                errorFormPage = USER_FORM_PAGE;
            } else if ("changePassword".equals(action)) {
                errorFormPage = CHANGE_PASSWORD_PAGE;
            }

            switch (action) {
                case "login":
                    url = login(request, response);
                    redirectAfterSuccess = (url.equals(HOME_PAGE) || url.equals(ADMIN_DASHBOARD_PAGE) || url.equals(STAFF_DASHBOARD_PAGE) || url.equals(CHANGE_PASSWORD_PAGE));
                    successRedirectUrl = url;
                    break;
                case "createUser":
                    url = createUser(request); // <-- SẼ GỌI HÀM ĐÃ SỬA
                    successRedirectUrl = USER_LIST_PAGE;
                    break;
                case "updateUserStatus":
                    url = updateUserStatus(request);
                    successRedirectUrl = USER_LIST_PAGE;
                    break;
                case "changePassword":
                    url = changePassword(request);
                    redirectAfterSuccess = (url.equals(HOME_PAGE) || url.equals(ADMIN_DASHBOARD_PAGE) || url.equals(STAFF_DASHBOARD_PAGE));
                    successRedirectUrl = url;
                    break;
                case "register":
                    url = register(request); // <-- **GỌI HÀM REGISTER ĐÃ CẬP NHẬT**
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
                if (action.equals("register")) {
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

    // (login... giữ nguyên)
    private String login(HttpServletRequest request, HttpServletResponse response)
            throws ValidationException, Exception {

        String tenDangNhap = request.getParameter("username");
        String matKhau = request.getParameter("password");

        TaiKhoanDTO user = taiKhoanService.login(tenDangNhap, matKhau);
        HttpSession session = request.getSession();
        session.setAttribute("USER", user);
        session.setAttribute("ROLE", user.getVaiTro());

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

        session.removeAttribute("FORCE_CHANGE_PASS_MSG"); // Xóa cờ ép buộc (nếu có)
        session.setAttribute("SUCCESS_MESSAGE", "Đổi mật khẩu thành công!");

        // Phân luồng trả về
        if ("QUAN_TRI".equals(currentUser.getVaiTro())) {
            return ADMIN_DASHBOARD_PAGE;
        } else if ("BAC_SI".equals(currentUser.getVaiTro()) || "LE_TAN".equals(currentUser.getVaiTro())) {
            return STAFF_DASHBOARD_PAGE;
        } else {
            return HOME_PAGE;
        }
    }

    // (createUser... giữ nguyên theo code bạn cung cấp)
    private String createUser(HttpServletRequest request) throws ValidationException, Exception {
        TaiKhoanDTO newTaiKhoanDTO = createDTOFromRequest(request);
        String plainPassword = request.getParameter("password");

        if (plainPassword == null || plainPassword.length() < 6) {
            throw new ValidationException("Mật khẩu tạm thời phải có ít nhất 6 ký tự.");
        }

        // --- (Đã xóa logic kiểm tra 'confirmPassword' theo code của bạn) ---
        TaiKhoanDTO result = taiKhoanService.createTaiKhoan(newTaiKhoanDTO, plainPassword);
        request.setAttribute("SUCCESS_MESSAGE", "Tạo tài khoản '" + result.getTenDangNhap() + "' thành công!");
        return USER_LIST_PAGE;
    }

    // --- **BẮT ĐẦU CẬP NHẬT HÀM register** ---
    /**
     * Xử lý logic đăng ký tài khoản (cho Bệnh nhân). ĐÃ CẬP NHẬT: Tự động tạo
     * hồ sơ BenhNhan tương ứng.
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

        // --- BƯỚC 1: TẠO TÀI KHOẢN ---
        TaiKhoanDTO dto = new TaiKhoanDTO();
        dto.setTenDangNhap(tenDangNhap);
        dto.setEmail(email);
        dto.setVaiTro("BENH_NHAN");
        dto.setTrangThai("HOAT_DONG");
        // (Lưu ý: TaiKhoanService sẽ tự set trangThaiMatKhau là DA_DOI cho BENH_NHAN)

        // Gọi service và *lấy lại* DTO đã lưu (để có ID)
        TaiKhoanDTO savedTaiKhoan = taiKhoanService.createTaiKhoan(dto, matKhau);

        if (savedTaiKhoan == null || savedTaiKhoan.getId() <= 0) {
            throw new Exception("Tạo tài khoản thất bại, không thể lấy ID.");
        }

        // --- BƯỚC 2: TẠO HỒ SƠ BỆNH NHÂN TƯƠNG ỨNG ---
        BenhNhanDTO benhNhanDto = new BenhNhanDTO();

        // Liên kết 2 bảng:
        benhNhanDto.setTaiKhoanId(savedTaiKhoan.getId());

        // Giải pháp tạm thời: Dùng username làm Họ Tên
        benhNhanDto.setHoTen(tenDangNhap);

        // Gọi BenhNhanService để lưu
        BenhNhanDTO savedBenhNhan = benhNhanService.createBenhNhan(benhNhanDto);

        if (savedBenhNhan == null) {
            // Nếu tạo bệnh nhân thất bại
            // (Nâng cao: Cần có Transaction để rollback việc tạo tài khoản)
            throw new Exception("Tạo Tài khoản thành công nhưng tạo Hồ sơ Bệnh nhân thất bại.");
        }

        // --- BƯỚC 3: TRẢ VỀ TRANG LOGIN (Giữ nguyên) ---
        HttpSession session = request.getSession(true);
        session.setAttribute("SUCCESS_MESSAGE", "Đăng ký thành công! Vui lòng đăng nhập.");
        return LOGIN_PAGE;
    }
    // --- **KẾT THÚC CẬP NHẬT HÀM register** ---

    // (handleServiceException... giữ nguyên)
    private void handleServiceException(HttpServletRequest request, Exception e, String formAction, String errorFormPage) {
        request.setAttribute("ERROR_MESSAGE", e.getMessage());

        if ("register".equals(formAction)) {
            request.setAttribute("username_register", request.getParameter("username"));
            request.setAttribute("email_register", request.getParameter("email"));
            request.setAttribute("formAction", "register");
        } else if ("createUser".equals(formAction) || "updateUserStatus".equals(formAction)) {
            request.setAttribute("USER_DATA", createDTOFromRequest(request));
            request.setAttribute("formAction", formAction);
        }
    }

    // (Các hàm còn lại: logout, listUsers, showUserEditForm, updateUserStatus, createDTOFromRequest, getServletInfo... giữ nguyên)
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
