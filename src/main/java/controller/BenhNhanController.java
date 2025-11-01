package controller;

import exception.ValidationException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime; // Giữ lại vì createDTOFromRequest dùng
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
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

/**
 * Controller xử lý các nghiệp vụ liên quan đến Bệnh Nhân (Patient).
 */
@WebServlet(name = "BenhNhanController", urlPatterns = {"/BenhNhanController"})
public class BenhNhanController extends HttpServlet {

    // (Các hằng số... giữ nguyên)
    private static final String BENHNHAN_LIST_PAGE = "admin/danhSachBenhNhan.jsp";
    private static final String BENHNHAN_FORM_PAGE = "admin/formBenhNhan.jsp";
    private static final String ERROR_PAGE = "error.jsp";
    private static final String FILL_PROFILE_PAGE = "user/fillProfile.jsp";
    private static final String HOME_PAGE = "home.jsp";

    private final BenhNhanService benhNhanService = new BenhNhanService();
    private final TaiKhoanService taiKhoanService = new TaiKhoanService();

    // (doGet... của bạn đã tốt, giữ nguyên)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String url = BENHNHAN_LIST_PAGE;

        try {
            if (action == null || action.isEmpty()) {
                action = "listBenhNhan";
            }

            switch (action) {
                case "listBenhNhan":
                    url = listBenhNhan(request);
                    break;
                case "showBenhNhanCreateForm":
                    loadFormDependencies(request, "createBenhNhan");
                    request.setAttribute("formAction", "createBenhNhan");
                    url = BENHNHAN_FORM_PAGE;
                    break;
                case "showBenhNhanEditForm":
                    url = showBenhNhanEditForm(request);
                    break;
                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho GET.");
                    url = ERROR_PAGE;
            }
            // --- CẬP NHẬT: Tách riêng catch cho rõ ràng ---
        } catch (ValidationException e) {
            log("Lỗi Validation tại BenhNhanController (doGet): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", e.getMessage());
            url = ERROR_PAGE;
        } catch (Exception e) {
            log("Lỗi Hệ thống tại BenhNhanController (doGet): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi: " + e.getMessage());
            url = ERROR_PAGE;
        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    // (doPost... của bạn đã tốt, giữ nguyên)
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String url = ERROR_PAGE;
        boolean loadListAfterSuccess = true;

        boolean redirectAfterSuccess = false;
        String successRedirectUrl = null;
        String errorFormPage = BENHNHAN_FORM_PAGE; // Mặc định là form Admin

        try {
            if (action == null || action.isEmpty()) {
                throw new ValidationException("Hành động không được chỉ định.");
            }

            // Xác định trang quay về nếu lỗi
            if ("updateProfile".equals(action)) {
                errorFormPage = FILL_PROFILE_PAGE;
            }

            switch (action) {
                case "createBenhNhan":
                    url = createBenhNhan(request);
                    break;
                case "updateBenhNhan":
                    url = updateBenhNhan(request);
                    break;
                case "deleteBenhNhan": // Action Soft Delete
                    url = softDeleteBenhNhan(request);
                    break;
                case "updateProfile":
                    url = updateProfile(request); // <-- GỌI HÀM ĐÃ SỬA
                    if (url.equals(HOME_PAGE)) {
                        redirectAfterSuccess = true;
                        successRedirectUrl = HOME_PAGE;
                    }
                    loadListAfterSuccess = false; // Không cần tải lại danh sách
                    break;
                default:
                    loadListAfterSuccess = false;
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho POST.");
            }

            // Nếu là create/update/delete (của Admin), tải lại danh sách
            if (loadListAfterSuccess && !url.equals(ERROR_PAGE) && !url.equals(BENHNHAN_FORM_PAGE)) {
                url = listBenhNhan(request);
            }

        } catch (ValidationException e) { // BẮT LỖI VALIDATION
            log("Lỗi Validation tại BenhNhanController (doPost): " + e.getMessage(), e);

            // CẬP NHẬT: Gửi lại dữ liệu cũ về form
            if ("updateProfile".equals(action)) {
                request.setAttribute("ERROR_MESSAGE", e.getMessage());
                // Gửi lại DTO đã nhập dở để điền lại form
                request.setAttribute("BENHNHAN_DATA", createDTOFromRequest(request));
                url = FILL_PROFILE_PAGE;
            } else {
                handleServiceException(request, e, action);
                url = BENHNHAN_FORM_PAGE;
            }
            loadListAfterSuccess = false;
            redirectAfterSuccess = false;

        } catch (Exception e) { // BẮT LỖI HỆ THỐNG
            log("Lỗi Hệ thống tại BenhNhanController (doPost): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống nghiêm trọng.");
            url = ERROR_PAGE;
            loadListAfterSuccess = false;
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

    // --- **BẮT ĐẦU SỬA HÀM updateProfile** ---
    /**
     * Xử lý lưu thông tin cá nhân từ trang fillProfile.jsp
     */
    private String updateProfile(HttpServletRequest request) throws ValidationException, Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER") == null) {
            throw new ValidationException("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.");
        }

        // --- SỬA LẠI LOGIC ---
        // 1. Lấy TAIKHOAN_ID từ session (cách duy nhất đáng tin cậy)
        TaiKhoanDTO currentUser = (TaiKhoanDTO) session.getAttribute("USER");
        if (currentUser == null) {
            throw new ValidationException("Lỗi: Không tìm thấy thông tin USER trong session.");
        }

        // 2. Dùng TAIKHOAN_ID để tìm BENHNHAN_ID
        BenhNhanDTO existingBenhNhan = benhNhanService.getBenhNhanByTaiKhoanId(currentUser.getId());

        if (existingBenhNhan == null) {
            // Đây chính là lỗi gốc (nếu đăng ký lỗi).
            // Ném ValidationException sẽ được doPost bắt và xử lý êm đẹp.
            throw new ValidationException("Không tìm thấy hồ sơ bệnh nhân nào được liên kết với tài khoản ID: " + currentUser.getId());
        }

        int benhNhanId = existingBenhNhan.getId();
        // --- KẾT THÚC SỬA LOGIC ---

        // 3. Lấy dữ liệu từ form
        BenhNhanDTO dtoFromForm = createDTOFromRequest(request);

        // 4. Gọi service để cập nhật
        // (Chúng ta sẽ cần hàm updateProfile trong BenhNhanService)
        benhNhanService.updateProfile(benhNhanId, dtoFromForm);
        
        session.setAttribute("SUCCESS_MESSAGE", "Cập nhật hồ sơ thành công! Chào mừng bạn.");
        return HOME_PAGE; // Trả về trang chủ Bệnh nhân
    }
    // --- **KẾT THÚC SỬA HÀM updateProfile** ---

    // --- (Các hàm còn lại của bạn đã tốt, giữ nguyên) ---
    private String listBenhNhan(HttpServletRequest request) throws Exception {
        List<BenhNhanDTO> list = benhNhanService.getAllBenhNhan();
        request.setAttribute("LIST_BENHNHAN", list);
        return BENHNHAN_LIST_PAGE;
    }

    private String showBenhNhanEditForm(HttpServletRequest request) throws ValidationException, Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            BenhNhanDTO benhNhan = benhNhanService.getBenhNhanById(id); // Chỉ lấy BN đang hoạt động
            request.setAttribute("BENHNHAN_DATA", benhNhan);
            loadFormDependencies(request, "updateBenhNhan");
            request.setAttribute("formAction", "updateBenhNhan");
            return BENHNHAN_FORM_PAGE;
        } catch (NumberFormatException e) {
            throw new ValidationException("ID Bệnh nhân không hợp lệ.");
        }
    }

    private String createBenhNhan(HttpServletRequest request) throws ValidationException, Exception {
        BenhNhanDTO newBenhNhanDTO = createDTOFromRequest(request);
        BenhNhanDTO result = benhNhanService.createBenhNhan(newBenhNhanDTO);
        request.setAttribute("SUCCESS_MESSAGE", "Tạo bệnh nhân '" + result.getHoTen() + "' thành công!");
        return BENHNHAN_LIST_PAGE;
    }

    private String updateBenhNhan(HttpServletRequest request) throws ValidationException, Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            BenhNhanDTO benhNhanDTO = createDTOFromRequest(request);
            BenhNhanDTO result = benhNhanService.updateBenhNhan(id, benhNhanDTO);
            request.setAttribute("SUCCESS_MESSAGE", "Cập nhật bệnh nhân '" + result.getHoTen() + "' thành công!");
            return BENHNHAN_LIST_PAGE;
        } catch (NumberFormatException e) {
            throw new ValidationException("ID Bệnh nhân không hợp lệ khi cập nhật.");
        }
    }

    private String softDeleteBenhNhan(HttpServletRequest request) throws ValidationException, Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            // Lấy BN bất kể trạng thái
            BenhNhanDTO bn = benhNhanService.getBenhNhanByIdEvenIfInactive(id);

            if (bn == null) {
                throw new ValidationException("Không tìm thấy bệnh nhân với ID: " + id);
            }

            if (bn.getTaiKhoanId() != null && bn.getTaiKhoanId() > 0) {
                taiKhoanService.khoaTaiKhoan(bn.getTaiKhoanId());
                request.setAttribute("SUCCESS_MESSAGE", "Đã vô hiệu hóa tài khoản của bệnh nhân thành công!");
            } else {
                request.setAttribute("INFO_MESSAGE", "Bệnh nhân này không có tài khoản để vô hiệu hóa.");
            }
            return BENHNHAN_LIST_PAGE;
        } catch (NumberFormatException e) {
            throw new ValidationException("ID Bệnh nhân không hợp lệ.");
        }
    }

    private void loadFormDependencies(HttpServletRequest request, String formAction) {
        try {
            List<TaiKhoanDTO> availableTaiKhoan = taiKhoanService.getActiveAndUnassignedAccounts("BENH_NHAN");
            request.setAttribute("LIST_TAIKHOAN", availableTaiKhoan);
        } catch (Exception e) {
            log("Không thể tải danh sách tài khoản cho form Bệnh nhân: " + e.getMessage(), e);
            request.setAttribute("LOAD_FORM_ERROR", "Lỗi tải danh sách Tài khoản.");
        }
    }

    private void handleServiceException(HttpServletRequest request, Exception e, String formAction) {
        request.setAttribute("ERROR_MESSAGE", e.getMessage());
        request.setAttribute("BENHNHAN_DATA", createDTOFromRequest(request));
        request.setAttribute("formAction", formAction);
        loadFormDependencies(request, formAction);
    }

    private BenhNhanDTO createDTOFromRequest(HttpServletRequest request) {
        BenhNhanDTO dto = new BenhNhanDTO();
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                dto.setId(Integer.parseInt(idStr));
            } catch (NumberFormatException e) {
                /* ignore */ }
        }

        // Lấy tất cả các trường
        dto.setMaBenhNhan(request.getParameter("maBenhNhan"));
        dto.setHoTen(request.getParameter("hoTen"));
        dto.setGioiTinh(request.getParameter("gioiTinh"));
        dto.setDiaChi(request.getParameter("diaChi"));
        dto.setSoDienThoai(request.getParameter("soDienThoai"));
        dto.setNhomMau(request.getParameter("nhomMau"));
        dto.setTienSuBenh(request.getParameter("tienSuBenh"));
        dto.setCccd(request.getParameter("cccd"));

        String ngaySinhStr = request.getParameter("ngaySinh");
        if (ngaySinhStr != null && !ngaySinhStr.isEmpty()) {
            try {
                dto.setNgaySinh(LocalDate.parse(ngaySinhStr));
            } catch (DateTimeParseException e) {
                log("Lỗi parse ngày sinh: " + ngaySinhStr + ". Yêu cầu định dạng yyyy-MM-dd.");
            }
        }

        String taiKhoanIdStr = request.getParameter("taiKhoanId");
        if (taiKhoanIdStr != null && !taiKhoanIdStr.isEmpty() && !taiKhoanIdStr.equals("0")) {
            try {
                dto.setTaiKhoanId(Integer.parseInt(taiKhoanIdStr));
            } catch (NumberFormatException e) {
                /* ignore */ }
        } else {
            // Phải set về null để logic updateProfile hoạt động (nếu là bệnh nhân tự cập nhật)
            dto.setTaiKhoanId(null);
        }

        return dto;
    }

    @Override
    public String getServletInfo() {
        return "Controller quản lý các nghiệp vụ liên quan đến Bệnh Nhân.";
    }
}
