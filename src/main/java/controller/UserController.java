package controller;

import exception.ValidationException; // (Giữ nguyên import của bạn)
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.dto.BenhNhanDTO;
import model.dto.TaiKhoanDTO;
import service.BenhNhanService;
import service.TaiKhoanService;


// --- THÊM MỚI ---


import service.TaiKhoanService;
import util.EmailUtils; // <-- 1. IMPORT LỚP TIỆN ÍCH
// --- KẾT THÚC THÊM MỚI ---

/**
 * Controller xử lý các nghiệp vụ liên quan đến người dùng (đăng nhập, đăng
 * xuất, quản lý tài khoản...). **ĐÃ CẬP NHẬT:** Tích hợp luồng xác thực email.
 */
@WebServlet(name = "UserController", urlPatterns = {"/UserController"})
public class UserController extends HttpServlet {

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

    private final BenhNhanService benhNhanService = new BenhNhanService();
    // (Không cần 'new EmailUtils()' vì nó là static)

    /**
     * **CẬP NHẬT:** Thêm 'case verify' và 'catch (ValidationException)'
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

                    // --- 3. THÊM CASE MỚI ĐỂ XỬ LÝ LINK EMAIL ---
                    case "verify":
                        url = verifyAccount(request);
                        // Dùng redirect để xóa token khỏi URL trình duyệt
                        response.sendRedirect(url);
                        return; // Ngắt luôn, không forward
                    // --- KẾT THÚC THÊM MỚI ---

                    default:
                        request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho GET.");
                }
            }
            // --- CẬP NHẬT: Tách riêng catch để xử lý lỗi token ---
        } catch (ValidationException e) {
            log("Lỗi Validation tại UserController (doGet): " + e.getMessage(), e);
            if ("verify".equals(action)) {
                // Nếu token lỗi (hết hạn, sai), đưa ra trang lỗi
                request.setAttribute("ERROR_MESSAGE", e.getMessage());
                url = ERROR_PAGE;
            } else {
                request.setAttribute("ERROR_MESSAGE", "Lỗi nghiệp vụ: " + e.getMessage());
                url = ERROR_PAGE;
            }
        } catch (Exception e) {
            log("Lỗi Hệ thống tại UserController (doGet): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
        } finally {
            if (url.equals(LOGIN_PAGE) && action != null && action.equals("logout")) {
                response.sendRedirect(request.getContextPath() + "/" + url);
            } else {
                // (Hàm redirect đã return, nên 'finally' này chỉ chạy cho các case forward)
                request.getRequestDispatcher(url).forward(request, response);
            }
        }
    }


    /**
     * (doPost... giữ nguyên cấu trúc)
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

        String errorFormPage = ERROR_PAGE; // Trang quay về nếu lỗi Validation

        try {
            if (action == null || action.isEmpty()) {
                throw new Exception("Hành động không được chỉ định.");
            }

            // (Xác định errorFormPage... giữ nguyên)
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
                    url = createUser(request);
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
                    url = register(request); // <-- GỌI HÀM REGISTER ĐÃ CẬP NHẬT

                    url = register(request);
                    if (url.equals(LOGIN_PAGE)) {
                        redirectAfterSuccess = true;
                        successRedirectUrl = "login.jsp"; // Sẽ redirect về trang login
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
                    // Dùng sendRedirect cho register để hiển thị SUCCESS_MESSAGE
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


    // (login, changePassword, createUser... giữ nguyên)
    // ... (hàm login, changePassword, createUser) ...

    /**
     * Xử lý logic đăng nhập VÀ PHÂN LUỒNG.
     */
    private String login(HttpServletRequest request, HttpServletResponse response)
            throws ValidationException, Exception {
        String tenDangNhap = request.getParameter("username");
        String matKhau = request.getParameter("password");
        // Service (Bước 3) sẽ tự động ném lỗi "CHUA_XAC_THUC" ở đây
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



    /**
     * Xử lý logic thay đổi mật khẩu VÀ PHÂN LUỒNG 3 HƯỚNG.
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



    // --- **BẮT ĐẦU SỬA HÀM createUser THEO YÊU CẦU** ---
    /**
     * Xử lý logic tạo mới một Tài khoản (Đã đơn giản hóa).
     */
    private String createUser(HttpServletRequest request) throws ValidationException, Exception {
        TaiKhoanDTO newTaiKhoanDTO = createDTOFromRequest(request);
        String plainPassword = request.getParameter("password");
        if (plainPassword == null || plainPassword.length() < 6) {
            throw new ValidationException("Mật khẩu tạm thời phải có ít nhất 6 ký tự.");
        }


        // --- **ĐÃ XÓA LOGIC KIỂM TRA 'confirmPassword'** ---

        TaiKhoanDTO result = taiKhoanService.createTaiKhoan(newTaiKhoanDTO, plainPassword);
        request.setAttribute("SUCCESS_MESSAGE", "Tạo tài khoản '" + result.getTenDangNhap() + "' thành công!");
        return USER_LIST_PAGE;
    }
    // --- **KẾT THÚC SỬA HÀM createUser** ---

    // --- **BẮT ĐẦU CẬP NHẬT HÀM register (BƯỚC 4/6)** ---
    /**
     * Xử lý logic đăng ký, tạo bệnh nhân, VÀ gửi email xác thực.
=======    /**
     * Xử lý logic đăng ký tài khoản (cho Bệnh nhân).

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


        // --- BƯỚC 1: TẠO TÀI KHOẢN (với trạng thái CHUA_XAC_THUC) ---


        TaiKhoanDTO dto = new TaiKhoanDTO();
        dto.setTenDangNhap(tenDangNhap);
        dto.setEmail(email);
        dto.setVaiTro("BENH_NHAN");
        // **THAY ĐỔI QUAN TRỌNG:** Đặt trạng thái chờ kích hoạt
        dto.setTrangThai("CHUA_XAC_THUC");

        // (Service sẽ tự set trangThaiMatKhau là DA_DOI cho BENH_NHAN)
        TaiKhoanDTO savedTaiKhoan = taiKhoanService.createTaiKhoan(dto, matKhau);
        if (savedTaiKhoan == null || savedTaiKhoan.getId() <= 0) {
            throw new Exception("Tạo tài khoản thất bại (Service trả về null).");
        }

        // --- BƯỚC 2: TẠO HỒ SƠ BỆNH NHÂN (Giữ nguyên) ---
        BenhNhanDTO benhNhanDto = new BenhNhanDTO();
        benhNhanDto.setTaiKhoanId(savedTaiKhoan.getId());
        benhNhanDto.setHoTen(tenDangNhap); // Dùng username làm họ tên tạm
        // (Service sẽ tự tạo maBenhNhan)
        benhNhanService.createBenhNhan(benhNhanDto);

        // --- BƯỚC 3: GỬI EMAIL XÁC THỰC (Logic mới) ---
        try {
            // 3.1. Tạo một token ngẫu nhiên
            String token = java.util.UUID.randomUUID().toString();

            // 3.2. Lưu token vào CSDL (Sử dụng hàm ta đã viết ở Bước 3)
            taiKhoanService.saveVerificationToken(savedTaiKhoan.getId(), token);

            // 3.3. Tạo link xác thực đầy đủ (ví dụ: http://localhost:8080/MyWeb/UserController?action=verify&token=...)
            String verificationLink = "http://" + request.getServerName() + ":" + request.getServerPort()
                    + request.getContextPath()
                    + "/UserController?action=verify&token=" + token; // <-- Trỏ về chính Controller này

            // 3.4. Xây dựng nội dung và Gửi
            String htmlContent = util.EmailUtils.buildVerificationEmailHtml(tenDangNhap, verificationLink);
            util.EmailUtils.sendEmail(email, "Xác thực tài khoản Bệnh viện", htmlContent); // <-- Gọi bằng util.EmailUtils

        } catch (Exception e) {
            // LỖI: Không gửi được email
            log("LỖI GỬI EMAIL: Không thể gửi email xác thực cho " + email + ": " + e.getMessage());
            // (Không cần ném lỗi cho người dùng, chỉ cần log lại)
        }

        // --- BƯỚC 4: TRẢ VỀ TRANG LOGIN ---

        dto.setTrangThai("HOAT_DONG");

        taiKhoanService.createTaiKhoan(dto, matKhau);
        HttpSession session = request.getSession(true);
        // **THAY ĐỔI THÔNG BÁO:**
        session.setAttribute("SUCCESS_MESSAGE", "Đăng ký thành công! Vui lòng kiểm tra email (cả mục Spam) để kích hoạt tài khoản.");
        return LOGIN_PAGE;
    }


    // --- **THÊM HÀM MỚI (BƯỚC 5/6)** ---
    /**
     * Xử lý logic khi người dùng nhấp vào link xác thực email (từ doGet).
     */
    private String verifyAccount(HttpServletRequest request) throws ValidationException, Exception {
        String token = request.getParameter("token");

        // 5.1. Kiểm tra token
        if (token == null || token.isEmpty()) {
            throw new ValidationException("Link xác thực không hợp lệ hoặc bị thiếu token.");
        }

        // 5.2. Gọi Service (Hàm chúng ta đã viết ở Bước 3)
        // Service sẽ ném ValidationException nếu token sai/hết hạn
        taiKhoanService.verifyToken(token);

        // 5.3. Gửi thông báo thành công về trang Login
        HttpSession session = request.getSession(true);
        session.setAttribute("SUCCESS_MESSAGE", "Xác thực tài khoản thành công! Bây giờ bạn có thể đăng nhập.");

        // 5.4. Trả về URL của trang Login
        return LOGIN_PAGE;
    }
    // --- **KẾT THÚC THÊM HÀM MỚI** ---

    // (handleServiceException, logout, listUsers, showUserEditForm, updateUserStatus, createDTOFromRequest, getServletInfo... giữ nguyên)
    // ...

    /**
     * Xử lý lỗi từ Service và gửi lại form tương ứng.
     */
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

    // ... (Các hàm còn lại: logout, listUsers, showUserEditForm, updateUserStatus, createDTOFromRequest, getServletInfo... giữ nguyên) ...
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
