package controller;

import exception.ValidationException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
 * Controller xử lý các nghiệp vụ liên quan đến Bệnh Nhân (Patient). **ĐÃ CẬP
 * NHẬT (Giai đoạn 6):** Thêm bước Xác nhận Hồ sơ (Confirm Profile).
 */
@WebServlet(name = "BenhNhanController", urlPatterns = {"/BenhNhanController"})
public class BenhNhanController extends HttpServlet {

    // (Hằng số của Admin)
    private static final String BENHNHAN_LIST_PAGE = "admin/danhSachBenhNhan.jsp";
    private static final String BENHNHAN_FORM_PAGE = "admin/formBenhNhan.jsp";
    private static final String ERROR_PAGE = "error.jsp";

    // (Hằng số của Bệnh nhân)
    private static final String EDIT_PROFILE_PAGE = "user/editProfile.jsp";
    private static final String VIEW_PROFILE_PAGE = "user/viewProfile.jsp";
    private static final String HOME_PAGE = "home.jsp";

    // --- **THÊM HẰNG SỐ MỚI (Theo ý tưởng của bạn)** ---
    private static final String CONFIRM_PROFILE_PAGE = "user/confirmProfile.jsp";

    private final BenhNhanService benhNhanService = new BenhNhanService();
    private final TaiKhoanService taiKhoanService = new TaiKhoanService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String url = ERROR_PAGE;
        boolean isRedirect = false;
        String redirectUrl = null;

        try {
            if (action == null || action.isEmpty()) {
                action = "listBenhNhan";
            }

            switch (action) {
                // --- Logic của Admin (Giữ nguyên) ---
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

                // --- Logic Bệnh nhân (Đã cập nhật) ---
                case "showProfile": // Dành cho "Xem Hồ sơ"
                    url = showViewProfile(request);
                    if (url.startsWith("BenhNhanController")) {
                        isRedirect = true;
                        redirectUrl = url;
                    }
                    break;
                case "showEditProfile": // Dành cho "Sửa Hồ sơ" (lần đầu)
                    url = showEditProfile(request, null); // null = không có ID
                    break;

                // --- **THÊM MỚI (Theo ý tưởng của bạn)** ---
                case "showEditProfileWithExisting": // Nút "Cần cập nhật"
                    url = showEditProfileWithExisting(request);
                    break;
                // --- **KẾT THÚC THÊM MỚI** ---

                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho GET.");
                    url = ERROR_PAGE;
            }
        } catch (ValidationException e) {
            // ... (Xử lý lỗi)
        } catch (Exception e) {
            // ... (Xử lý lỗi)
        } finally {
            if (isRedirect) {
                response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + "/" + url));
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
        boolean loadListAfterSuccess = true;
        boolean redirectAfterSuccess = false;
        String successRedirectUrl = null;
        String errorFormPage = BENHNHAN_FORM_PAGE;

        try {
            if (action == null || action.isEmpty()) {
                throw new ValidationException("Hành động không được chỉ định.");
            }

            if ("saveProfile".equals(action)) {
                errorFormPage = EDIT_PROFILE_PAGE;
            } else if ("confirmAndLink".equals(action)) { // <-- Thêm trang lỗi
                errorFormPage = CONFIRM_PROFILE_PAGE;
            }

            switch (action) {
                // --- Logic của Admin (Giữ nguyên) ---
                case "createBenhNhan":
                    url = createBenhNhan(request);
                    break;
                case "updateBenhNhan":
                    url = updateBenhNhan(request);
                    break;
                case "deleteBenhNhan":
                    url = softDeleteBenhNhan(request);
                    break;

                // --- LOGIC BỆNH NHÂN (Đã sửa) ---
                case "saveProfile":
                    url = saveProfile(request); // <-- Hàm này giờ đã "thông minh"

                    if (url.equals(HOME_PAGE)) { // Nếu tạo mới/cập nhật thành công
                        redirectAfterSuccess = true;
                        successRedirectUrl = url;
                    } else if (url.equals(CONFIRM_PROFILE_PAGE)) { // Nếu cần xác nhận
                        // Không redirect, chỉ forward
                    }
                    loadListAfterSuccess = false;
                    break;

                // --- **THÊM MỚI (Theo ý tưởng của bạn)** ---
                case "confirmAndLink": // Nút "Đúng, liên kết ngay"
                    url = confirmAndLink(request);
                    if (url.equals(HOME_PAGE)) {
                        redirectAfterSuccess = true;
                        successRedirectUrl = url;
                    }
                    loadListAfterSuccess = false;
                    break;
                // --- **KẾT THÚC THÊM MỚI** ---

                default:
                    loadListAfterSuccess = false;
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho POST.");
            }

            if (loadListAfterSuccess && !url.equals(ERROR_PAGE) && !url.equals(BENHNHAN_FORM_PAGE)) {
                url = listBenhNhan(request);
            }

        } catch (ValidationException e) {
            log("Lỗi Validation tại BenhNhanController (doPost): " + e.getMessage(), e);

            if ("saveProfile".equals(action)) {
                request.setAttribute("ERROR_MESSAGE", e.getMessage());
                request.setAttribute("BENHNHAN_DATA", createDTOFromRequest(request));
                request.setAttribute("formAction", "saveProfile");
                url = EDIT_PROFILE_PAGE;
            } else if ("confirmAndLink".equals(action)) { // <-- Bắt lỗi cho confirm
                request.setAttribute("ERROR_MESSAGE", e.getMessage());
                // Cần tải lại Bệnh nhân để hiển thị
                try {
                    int patientId = Integer.parseInt(request.getParameter("patientId"));
                    request.setAttribute("EXISTING_PATIENT", benhNhanService.getBenhNhanByIdEvenIfInactive(patientId));
                } catch (Exception ex) {
                }
                url = CONFIRM_PROFILE_PAGE;
            } else {
                handleServiceException(request, e, action);
                url = BENHNHAN_FORM_PAGE;
            }
            loadListAfterSuccess = false;
            redirectAfterSuccess = false;

        } catch (Exception e) {
            // ... (Giữ nguyên)
        } finally {
            if (redirectAfterSuccess) {
                String finalUrl = request.getContextPath() + "/" + successRedirectUrl;
                response.sendRedirect(response.encodeRedirectURL(finalUrl));
            } else {
                request.getRequestDispatcher(url).forward(request, response);
            }
        }
    }

    // --- **CÁC HÀM HELPER ĐÃ ĐƯỢC TÁI CẤU TRÚC** ---
    /**
     * (doGet) Hiển thị trang 'viewProfile.jsp' (Chỉ Xem).
     */
    private String showViewProfile(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER") == null) {
            throw new ValidationException("Vui lòng đăng nhập để xem hồ sơ.");
        }
        TaiKhoanDTO currentUser = (TaiKhoanDTO) session.getAttribute("USER");
        BenhNhanDTO benhNhan = benhNhanService.getBenhNhanByTaiKhoanId(currentUser.getId());
        if (benhNhan != null) {
            request.setAttribute("BENHNHAN_DATA", benhNhan);
            return VIEW_PROFILE_PAGE; // -> user/viewProfile.jsp
        } else {
            // **SỬA:** Redirect đến action 'showEditProfile'
            return "BenhNhanController?action=showEditProfile";
        }
    }

    /**
     * (doGet) Hiển thị trang 'editProfile.jsp' (Sửa/Tạo) Kịch bản: Bệnh nhân
     * mới (lần đầu), hoặc Admin sửa.
     */
    private String showEditProfile(HttpServletRequest request, BenhNhanDTO dataToLoad) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER") == null) {
            throw new ValidationException("Vui lòng đăng nhập để xem hồ sơ.");
        }
        TaiKhoanDTO currentUser = (TaiKhoanDTO) session.getAttribute("USER");

        BenhNhanDTO benhNhan = dataToLoad; // Lấy dữ liệu được truyền vào

        if (benhNhan == null) {
            // Nếu không có dữ liệu (lần đầu login), tải dữ liệu
            benhNhan = benhNhanService.getBenhNhanByTaiKhoanId(currentUser.getId());
        }

        if (benhNhan != null) {
            // Kịch bản A: Đã có hồ sơ (Họ nhấn "Chỉnh sửa")
            request.setAttribute("BENHNHAN_DATA", benhNhan);
        } else {
            // Kịch bản B: Chưa có hồ sơ (Login lần đầu)
            BenhNhanDTO emptyProfile = new BenhNhanDTO();
            emptyProfile.setHoTen(currentUser.getTenDangNhap());
            request.setAttribute("BENHNHAN_DATA", emptyProfile);
        }

        request.setAttribute("formAction", "saveProfile");
        return EDIT_PROFILE_PAGE; // -> user/editProfile.jsp
    }

    /**
     * **HÀM MỚI (doGet):** Xử lý nút "Cần cập nhật" từ trang xác nhận.
     */
    private String showEditProfileWithExisting(HttpServletRequest request) throws Exception {
        try {
            int patientId = Integer.parseInt(request.getParameter("patientId"));
            BenhNhanDTO existingPatient = benhNhanService.getBenhNhanByIdEvenIfInactive(patientId);

            if (existingPatient == null) {
                throw new ValidationException("Không tìm thấy hồ sơ.");
            }

            // Tải dữ liệu cũ vào form
            return showEditProfile(request, existingPatient);

        } catch (NumberFormatException e) {
            throw new ValidationException("ID không hợp lệ.");
        }
    }

    /**
     * **HÀM MỚI (doPost):** Xử lý nút "Đúng, liên kết ngay".
     */
    private String confirmAndLink(HttpServletRequest request) throws ValidationException, Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER") == null) {
            throw new ValidationException("Phiên đăng nhập hết hạn.");
        }

        TaiKhoanDTO currentUser = (TaiKhoanDTO) session.getAttribute("USER");

        try {
            int patientId = Integer.parseInt(request.getParameter("patientId"));
            BenhNhanDTO existingPatient = benhNhanService.getBenhNhanByIdEvenIfInactive(patientId);

            if (existingPatient == null) {
                throw new ValidationException("Không tìm thấy hồ sơ.");
            }

            // Kiểm tra bảo mật
            if (existingPatient.getTaiKhoanId() != null
                    && existingPatient.getTaiKhoanId() != currentUser.getId()) {
                throw new ValidationException("Hồ sơ này đã được liên kết với tài khoản khác.");
            }

            // Liên kết
            if (existingPatient.getTaiKhoanId() == null) {
                benhNhanService.linkAccountToPatient(patientId, currentUser.getId());
            }

            session.setAttribute("SUCCESS_MESSAGE",
                    "Chào mừng trở lại! Hồ sơ y tế của bạn đã được liên kết thành công.");

            return HOME_PAGE;

        } catch (NumberFormatException e) {
            throw new ValidationException("ID không hợp lệ.");
        }
    }

    /**
     * **HÀM CẬP NHẬT (doPost):** saveProfile giờ đã "thông minh" hơn.
     */
    private String saveProfile(HttpServletRequest request) throws ValidationException, Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER") == null) {
            throw new ValidationException("Phiên đăng nhập hết hạn.");
        }
        TaiKhoanDTO currentUser = (TaiKhoanDTO) session.getAttribute("USER");

        BenhNhanDTO dtoFromForm = createDTOFromRequest(request);
        Integer benhNhanId = dtoFromForm.getId();

        if (benhNhanId != null && benhNhanId > 0) {
            // --- KỊCH BẢN 1: CẬP NHẬT HỒ SƠ ---
            // (User đã ở trang editProfile và nhấn Lưu)
            BenhNhanDTO existingPatient = benhNhanService.getBenhNhanById(benhNhanId);
            if (existingPatient == null) {
                throw new ValidationException("Hồ sơ bạn đang cố sửa không còn tồn tại.");
            }
            // (Kiểm tra bảo mật: user này có sở hữu hồ sơ này không)
            if (existingPatient.getTaiKhoanId() == null
                    || existingPatient.getTaiKhoanId() != currentUser.getId()) {
                // (Kiểm tra xem có phải họ đang liên kết không)
                if (existingPatient.getTaiKhoanId() != null) {
                    throw new ValidationException("Lỗi bảo mật: Bạn không có quyền sửa hồ sơ này.");
                }
            }

            // Liên kết tài khoản nếu nó chưa được liên kết
            if (existingPatient.getTaiKhoanId() == null) {
                benhNhanService.linkAccountToPatient(benhNhanId, currentUser.getId());
                session.setAttribute("SUCCESS_MESSAGE", "Hồ sơ được cập nhật và liên kết thành công!");
            } else {
                session.setAttribute("SUCCESS_MESSAGE", "Cập nhật hồ sơ thành công!");
            }

            // Cập nhật thông tin
            dtoFromForm.setTaiKhoanId(currentUser.getId()); // Đảm bảo DTO có ID
            benhNhanService.updateBenhNhan(benhNhanId, dtoFromForm);

            return HOME_PAGE; // Quay về trang chủ

        } else {
            // --- KỊCH BẢN 2: TẠO MỚI (Lần đầu điền form) ---
            String cccd = dtoFromForm.getCccd();
            if (cccd == null || cccd.isEmpty()) {
                throw new ValidationException("CCCD là thông tin bắt buộc.");
            }

            BenhNhanDTO existingPatientByCccd = benhNhanService.findByCccd(cccd);

            if (existingPatientByCccd != null) {
                // **Kịch bản 2A: ĐÃ TỪNG KHÁM - CHUYỂN SANG TRANG XÁC NHẬN**

                // Kiểm tra xem hồ sơ này đã bị người khác "chiếm" chưa
                if (existingPatientByCccd.getTaiKhoanId() != null
                        && existingPatientByCccd.getTaiKhoanId() != currentUser.getId()) {
                    throw new ValidationException("CCCD này đã được liên kết với một tài khoản khác.");
                }

                // ✅ CHUYỂN SANG TRANG XÁC NHẬN
                request.setAttribute("EXISTING_PATIENT", existingPatientByCccd);
                return CONFIRM_PROFILE_PAGE; // <-- Trả về trang JSP

            } else {
                // **Kịch bản 2B: NGƯỜI MỚI 100%**
                dtoFromForm.setTaiKhoanId(currentUser.getId());
                benhNhanService.createBenhNhan(dtoFromForm);
                session.setAttribute("SUCCESS_MESSAGE", "Tạo và liên kết hồ sơ thành công!");
                return HOME_PAGE; // Quay về trang chủ
            }
        }
    }

    // --- (Các hàm Admin và helper còn lại giữ nguyên) ---
    private String listBenhNhan(HttpServletRequest request) throws Exception {
        List<BenhNhanDTO> list = benhNhanService.getAllBenhNhan();
        request.setAttribute("LIST_BENHNHAN", list);
        return BENHNHAN_LIST_PAGE;
    }

    private String showBenhNhanEditForm(HttpServletRequest request) throws Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            BenhNhanDTO benhNhan = benhNhanService.getBenhNhanById(id);
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

// --- BẮT ĐẦU SỬA: Thêm .trim() vào TẤT CẢ các trường String ---
        // Helper an toàn để trim
        java.util.function.Function<String, String> safeTrim = (s) -> (s != null) ? s.trim() : null;

        dto.setMaBenhNhan(safeTrim.apply(request.getParameter("maBenhNhan")));
        dto.setHoTen(safeTrim.apply(request.getParameter("hoTen")));
        dto.setGioiTinh(safeTrim.apply(request.getParameter("gioiTinh")));
        dto.setDiaChi(safeTrim.apply(request.getParameter("diaChi")));
        dto.setSoDienThoai(safeTrim.apply(request.getParameter("soDienThoai")));
        dto.setNhomMau(safeTrim.apply(request.getParameter("nhomMau")));
        dto.setTienSuBenh(safeTrim.apply(request.getParameter("tienSuBenh")));
        dto.setCccd(safeTrim.apply(request.getParameter("cccd")));
// --- KẾT THÚC SỬA ---

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
            dto.setTaiKhoanId(null);
        }

        return dto;
    }

    @Override
    public String getServletInfo() {
        return "Controller quản lý các nghiệp vụ liên quan đến Bệnh Nhân.";
    }
}
