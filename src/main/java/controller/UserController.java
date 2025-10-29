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
import model.dto.TaiKhoanDTO;
import service.TaiKhoanService;

/**
 * Controller xử lý các nghiệp vụ liên quan đến người dùng (đăng nhập, đăng
 * xuất, quản lý tài khoản...).
 */
@WebServlet(name = "UserController", urlPatterns = {"/UserController"})
public class UserController extends HttpServlet {

    private static final String LOGIN_PAGE = "login.jsp";
    private static final String HOME_PAGE = "home.jsp";
    private static final String ADMIN_DASHBOARD_PAGE = "admin/dashBoard.jsp";
    private static final String USER_LIST_PAGE = "admin/danhSachTaiKhoan.jsp";
    private static final String USER_FORM_PAGE = "admin/formTaiKhoan.jsp";
    private static final String CHANGE_PASSWORD_PAGE = "user/changePassword.jsp";
    private static final String REGISTER_PAGE = "register.jsp";
    private static final String ERROR_PAGE = "error.jsp";

    private final TaiKhoanService taiKhoanService = new TaiKhoanService();

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
                    case "showUserRegister":
                        url = REGISTER_PAGE;
                        break;
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

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String url = ERROR_PAGE;
        boolean redirectAfterSuccess = false;
        String successRedirectUrl = null;

        try {
            if (action == null || action.isEmpty()) {
                throw new Exception("Hành động không được chỉ định.");
            }

            switch (action) {
                case "login":
                    url = login(request, response);
                    redirectAfterSuccess = (url.equals(HOME_PAGE) || url.equals(ADMIN_DASHBOARD_PAGE));
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
                    redirectAfterSuccess = (url.equals(HOME_PAGE) || url.equals(ADMIN_DASHBOARD_PAGE));
                    successRedirectUrl = url;
                    break;
                case "register":
                    url = register(request);
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

        } catch (Exception e) {
            log("Lỗi tại UserController (doPost): " + e.getMessage(), e);
            String errorFormPage = ERROR_PAGE;
            if ("createUser".equals(action) || "updateUserStatus".equals(action)) {
                errorFormPage = USER_FORM_PAGE;
            } else if ("changePassword".equals(action)) {
                errorFormPage = CHANGE_PASSWORD_PAGE;
            } else if ("register".equals(action)) {
                errorFormPage = REGISTER_PAGE;
            } else if ("login".equals(action)) {
                errorFormPage = LOGIN_PAGE;
            }
            handleServiceException(request, e, action, errorFormPage);
            url = errorFormPage;
            redirectAfterSuccess = false;
        } finally {
            if (redirectAfterSuccess) {
                if (action.equals("register")) {
                    // Trường hợp này không cần encode vì login.jsp là trang public
                    response.sendRedirect(successRedirectUrl);
                } else {
                    // **SỬA LỖI SESSION: Dùng response.encodeRedirectURL()**
                    String finalUrl = request.getContextPath() + "/" + successRedirectUrl;
                    response.sendRedirect(response.encodeRedirectURL(finalUrl));
                }
            } else {
                request.getRequestDispatcher(url).forward(request, response);
            }
        }
    }

    /**
     * Xử lý logic đăng nhập VÀ PHÂN LUỒNG.
     */
    private String login(HttpServletRequest request, HttpServletResponse response)  {
        String tenDangNhap = request.getParameter("username");
        String matKhau = request.getParameter("password");
        try {
            TaiKhoanDTO user = taiKhoanService.login(tenDangNhap, matKhau);
            HttpSession session = request.getSession();
            session.setAttribute("USER", user);
            session.setAttribute("ROLE", user.getVaiTro());

            if ("QUAN_TRI".equals(user.getVaiTro())) {
                return ADMIN_DASHBOARD_PAGE;
            }
            return HOME_PAGE;

        } catch (ValidationException e) {
            log("Lỗi đăng nhập: " + e.getMessage());
            request.setAttribute("ERROR_MESSAGE", e.getMessage());
            return LOGIN_PAGE;
        }
        catch (Exception e) {
            log("Lỗi đăng nhập: " + e.getMessage());
            request.setAttribute("ERROR_MESSAGE", e.getMessage());
            return ERROR_PAGE;
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
    private String showUserEditForm(HttpServletRequest request) throws Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            TaiKhoanDTO taiKhoan = taiKhoanService.getTaiKhoanById(id);
            request.setAttribute("USER_DATA", taiKhoan);
            request.setAttribute("formAction", "updateUserStatus");
            return USER_FORM_PAGE;
        } catch (NumberFormatException e) {
            request.setAttribute("ERROR_MESSAGE", "ID Tài khoản không hợp lệ.");
            return ERROR_PAGE;
        }
    }

    /**
     * Xử lý logic tạo mới một Tài khoản.
     */
    private String createUser(HttpServletRequest request) throws Exception {
        TaiKhoanDTO newTaiKhoanDTO = createDTOFromRequest(request);
        String plainPassword = request.getParameter("password");
        if (plainPassword == null || plainPassword.length() < 6) {
            throw new Exception("Mật khẩu khi tạo phải có ít nhất 6 ký tự.");
        }
        String confirmPassword = request.getParameter("confirmPassword");
        if (!plainPassword.equals(confirmPassword)) {
            throw new Exception("Mật khẩu và xác nhận mật khẩu không khớp.");
        }

        TaiKhoanDTO result = taiKhoanService.createTaiKhoan(newTaiKhoanDTO, plainPassword);
        request.setAttribute("SUCCESS_MESSAGE", "Tạo tài khoản '" + result.getTenDangNhap() + "' thành công!");
        return USER_LIST_PAGE;
    }

    /**
     * Xử lý logic cập nhật trạng thái (Khóa/Mở khóa) một Tài khoản.
     */
    private String updateUserStatus(HttpServletRequest request) throws Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String newTrangThai = request.getParameter("trangThai");
            TaiKhoanDTO result = taiKhoanService.updateTrangThaiTaiKhoan(id, newTrangThai);
            request.setAttribute("SUCCESS_MESSAGE", "Cập nhật trạng thái tài khoản '" + result.getTenDangNhap() + "' thành công!");
            return USER_LIST_PAGE;
        } catch (NumberFormatException e) {
            request.setAttribute("ERROR_MESSAGE", "ID Tài khoản không hợp lệ khi cập nhật trạng thái.");
            return ERROR_PAGE;
        }
    }

    /**
     * Xử lý logic thay đổi mật khẩu VÀ PHÂN LUỒNG.
     */
    private String changePassword(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER") == null) {
            request.setAttribute("ERROR_MESSAGE", "Bạn cần đăng nhập để thực hiện chức năng này.");
            return LOGIN_PAGE;
        }

        TaiKhoanDTO currentUser = (TaiKhoanDTO) session.getAttribute("USER");
        int userId = currentUser.getId();
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");

        if (oldPassword == null || newPassword == null || confirmPassword == null
                || oldPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            throw new Exception("Vui lòng nhập đầy đủ mật khẩu cũ, mới và xác nhận.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new Exception("Mật khẩu mới và xác nhận không khớp.");
        }

        taiKhoanService.changePassword(userId, oldPassword, newPassword);
        session.setAttribute("SUCCESS_MESSAGE", "Đổi mật khẩu thành công!");

        if ("QUAN_TRI".equals(currentUser.getVaiTro())) {
            return ADMIN_DASHBOARD_PAGE;
        } else {
            return HOME_PAGE;
        }
    }

    /**
     * Xử lý logic đăng ký tài khoản (cho Bệnh nhân).
     */
    private String register(HttpServletRequest request) throws Exception {
        String tenDangNhap = request.getParameter("username");
        String email = request.getParameter("email");
        try {
            String matKhau = request.getParameter("password");
            String confirmMatKhau = request.getParameter("confirmPassword");

            if (tenDangNhap == null || email == null || matKhau == null || confirmMatKhau == null
                    || tenDangNhap.trim().isEmpty() || email.trim().isEmpty() || matKhau.isEmpty()) {
                throw new Exception("Vui lòng điền đầy đủ thông tin.");
            }
            if (!matKhau.equals(confirmMatKhau)) {
                throw new Exception("Mật khẩu và xác nhận mật khẩu không khớp.");
            }

            TaiKhoanDTO dto = new TaiKhoanDTO();
            dto.setTenDangNhap(tenDangNhap);
            dto.setEmail(email);
            dto.setVaiTro("BENH_NHAN");
            dto.setTrangThai("HOAT_DONG");

            taiKhoanService.createTaiKhoan(dto, matKhau);

            HttpSession session = request.getSession(true);
            session.setAttribute("SUCCESS_MESSAGE", "Đăng ký thành công! Vui lòng đăng nhập.");
            return LOGIN_PAGE;

        } catch (Exception e) {
            log("Lỗi đăng ký: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Xử lý lỗi từ Service và gửi lại form tương ứng.
     */
    private void handleServiceException(HttpServletRequest request, Exception e, String formAction, String errorFormPage) {
        request.setAttribute("ERROR_MESSAGE", e.getMessage());
        if ("createUser".equals(formAction) || "updateUserStatus".equals(formAction)) {
            request.setAttribute("USER_DATA", createDTOFromRequest(request));
        } else if ("register".equals(formAction)) {
            request.setAttribute("username_register", request.getParameter("username"));
            request.setAttribute("email_register", request.getParameter("email"));
        }
        request.setAttribute("formAction", formAction);
    }

    /**
     * Hàm tiện ích tạo TaiKhoanDTO từ request (không bao gồm mật khẩu).
     */
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
