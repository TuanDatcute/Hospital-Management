package controller;

import exception.ValidationException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
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

/**
 * Controller xử lý các nghiệp vụ liên quan đến Bệnh Nhân (Patient). (ĐÃ NÂNG
 * CẤP: Thêm Tìm kiếm & Sửa lỗi Xóa Mềm)
 */
@WebServlet(name = "BenhNhanController", urlPatterns = {"/BenhNhanController"})
public class BenhNhanController extends HttpServlet {

    // (Các hằng số JSP giữ nguyên)
    private static final String BENHNHAN_LIST_PAGE = "admin/danhSachBenhNhan.jsp";
    private static final String BENHNHAN_FORM_PAGE = "admin/formBenhNhan.jsp";
    private static final String ERROR_PAGE = "error.jsp";
    private static final String EDIT_PROFILE_PAGE = "user/editProfile.jsp";
    private static final String VIEW_PROFILE_PAGE = "user/viewProfile.jsp";
    private static final String HOME_PAGE = "home.jsp";
    private static final String CONFIRM_PROFILE_PAGE = "user/confirmProfile.jsp";

    // === HẰNG SỐ MỚI CHO PHÂN TRANG ===
    private static final int PAGE_SIZE = 10; // 10 bệnh nhân mỗi trang

    // (Các Service giữ nguyên)
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
                // --- Logic của Admin ---
                case "listBenhNhan":
                    url = listBenhNhan(request); // <-- ĐÃ SỬA DÙNG PHÂN TRANG & TÌM KIẾM
                    break;

                case "showBenhNhanCreateForm":
                    loadFormDependencies(request, "createBenhNhan");
                    request.setAttribute("formAction", "createBenhNhan");

                    // === BẮT ĐẦU THÊM MỚI (LẤY MÃ DỰ KIẾN) ===
                    try {
                        // Gọi hàm "nhìn trộm"
                        String nextMa = benhNhanService.getNextMaBenhNhan();

                        // Tạo một DTO rỗng và gán mã dự kiến vào
                        BenhNhanDTO dto = new BenhNhanDTO();
                        dto.setMaBenhNhan(nextMa);

                        // Gửi DTO này về form (thay vì không gửi gì)
                        request.setAttribute("BENHNHAN_DATA", dto);

                    } catch (Exception e) {
                        log("Lỗi khi tạo mã bệnh nhân mới: " + e.getMessage(), e);
                        request.setAttribute("ERROR_MESSAGE", "Không thể tạo mã bệnh nhân tự động.");
                    }
                    // === KẾT THÚC THÊM MỚI ===

                    url = BENHNHAN_FORM_PAGE;
                    break;
                case "showBenhNhanEditForm":
                    url = showBenhNhanEditForm(request);
                    break;

                // --- Logic Bệnh nhân (Giữ nguyên) ---
                case "showProfile":
                    url = showViewProfile(request);
                    if (url.startsWith("BenhNhanController")) {
                        isRedirect = true;
                        redirectUrl = url;
                    }
                    break;
                case "showEditProfile":
                    url = showEditProfile(request, null);
                    break;
                case "showEditProfileWithExisting":
                    url = showEditProfileWithExisting(request);
                    break;

                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho GET.");
                    url = ERROR_PAGE;
            }
        } catch (ValidationException e) {
            log("Lỗi Validation tại BenhNhanController (doGet): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Lỗi nghiệp vụ: " + e.getMessage());
            url = ERROR_PAGE;
        } catch (Exception e) {
            log("Lỗi Hệ thống tại BenhNhanController (doGet): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
            url = ERROR_PAGE;
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
        String formErrorPage = ERROR_PAGE;

        try {
            if (action == null || action.isEmpty()) {
                throw new ValidationException("Hành động không được chỉ định.");
            }

            // === SỬA (XÓA MỀM) ===
            // (Đổi tên 'deleteBenhNhan' thành 'softDeleteBenhNhan' cho nhất quán)
            if ("createBenhNhan".equals(action) || "updateBenhNhan".equals(action)) {
                formErrorPage = BENHNHAN_FORM_PAGE;
            } else if ("softDeleteBenhNhan".equals(action)) { // <-- SỬA TÊN
                formErrorPage = BENHNHAN_LIST_PAGE;
            } else if ("saveProfile".equals(action)) {
                formErrorPage = EDIT_PROFILE_PAGE;
            } else if ("confirmAndLink".equals(action)) {
                formErrorPage = CONFIRM_PROFILE_PAGE;
            }
            // === KẾT THÚC SỬA ===

            switch (action) {
                // --- Logic của Admin (Đã Sửa) ---
                case "createBenhNhan":
                    url = createBenhNhan(request);
                    break;
                case "updateBenhNhan":
                    url = updateBenhNhan(request);
                    break;

                // === SỬA (XÓA MỀM) ===
                case "softDeleteBenhNhan": // <-- SỬA TÊN
                    url = softDeleteBenhNhan(request);
                    break;
                // === KẾT THÚC SỬA ===

                // --- LOGIC BỆNH NHÂN (Giữ nguyên) ---
                case "saveProfile":
                    url = saveProfile(request);
                    if (url.equals(HOME_PAGE)) {
                        url = "redirect:" + HOME_PAGE;
                    }
                    break;
                case "confirmAndLink":
                    url = confirmAndLink(request);
                    if (url.equals(HOME_PAGE)) {
                        url = "redirect:" + HOME_PAGE;
                    }
                    break;

                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho POST.");
                    url = ERROR_PAGE;
            }
        } catch (ValidationException e) {
            log("Lỗi Validation tại BenhNhanController (doPost): " + e.getMessage(), e);
            if ("saveProfile".equals(action)) {
                request.setAttribute("ERROR_MESSAGE", e.getMessage());
                request.setAttribute("BENHNHAN_DATA", createDTOFromRequest(request));
                request.setAttribute("formAction", "saveProfile");
            } else if ("confirmAndLink".equals(action)) {
                request.setAttribute("ERROR_MESSAGE", e.getMessage());
                try {
                    int patientId = Integer.parseInt(request.getParameter("patientId"));
                    request.setAttribute("EXISTING_PATIENT", benhNhanService.getBenhNhanByIdEvenIfInactive(patientId));
                } catch (Exception ex) {
                    /* Bỏ qua */ }
            } else {
                handleServiceException(request, e, action);
            }
            url = formErrorPage;
        } catch (Exception e) {
            log("Lỗi Hệ thống tại BenhNhanController (doPost): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
            url = ERROR_PAGE;
        } finally {
            // (Logic PRG giữ nguyên)
            if (url.startsWith("redirect:")) {
                String redirectUrl = url.substring("redirect:".length());
                if (redirectUrl.startsWith(request.getContextPath()) || redirectUrl.startsWith("BenhNhanController")) {
                    response.sendRedirect(redirectUrl);
                } else {
                    // Sửa: Giờ đây redirectUrl là "MainController?action=listBenhNhan"
                    // Nó không bắt đầu bằng contextPath, nên chúng ta không cần thêm "/"
                    response.sendRedirect(redirectUrl);
                }
            } else {
                request.getRequestDispatcher(url).forward(request, response);
            }
        }
    }

    // =========================================================================
    // === CÁC HÀM ADMIN (ĐÃ NÂNG CẤP) ===
    // =========================================================================
    /**
     * === BẮT ĐẦU NÂNG CẤP (PHÂN TRANG + TÌM KIẾM) === (Thay thế hoàn toàn hàm
     * 'listBenhNhan' cũ)
     */
    private String listBenhNhan(HttpServletRequest request) throws Exception {
        // 1. Lấy tham số trang
        int page = 1;
        String pageStr = request.getParameter("page");
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        // 2. Lấy tham số tìm kiếm
        String keyword = request.getParameter("keyword");

        // 3. Khai báo biến
        List<BenhNhanDTO> list;
        long totalBenhNhan;

        // 4. Logic nghiệp vụ: Kiểm tra xem có tìm kiếm hay không
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Kịch bản TÌM KIẾM
            String trimmedKeyword = keyword.trim();
            list = benhNhanService.searchBenhNhanPaginated(trimmedKeyword, page, PAGE_SIZE);
            totalBenhNhan = benhNhanService.getBenhNhanSearchCount(trimmedKeyword);
            request.setAttribute("searchKeyword", keyword); // <-- Gửi lại keyword
        } else {
            // Kịch bản XEM TẤT CẢ (phân trang)
            list = benhNhanService.getAllBenhNhanPaginated(page, PAGE_SIZE);
            totalBenhNhan = benhNhanService.getBenhNhanCount();
        }

        // 5. Tính toán và Gửi dữ liệu về JSP
        long totalPages = (long) Math.ceil((double) totalBenhNhan / PAGE_SIZE);
        request.setAttribute("LIST_BENHNHAN", list);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        return BENHNHAN_LIST_PAGE;
    }
    // === KẾT THÚC NÂNG CẤP ===

    /**
     * (Hàm showBenhNhanEditForm giữ nguyên)
     */
    private String showBenhNhanEditForm(HttpServletRequest request) throws Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            BenhNhanDTO benhNhan = benhNhanService.getBenhNhanById(id); // Service đã lọc
            request.setAttribute("BENHNHAN_DATA", benhNhan);
            loadFormDependencies(request, "updateBenhNhan");
            request.setAttribute("formAction", "updateBenhNhan");
            return BENHNHAN_FORM_PAGE;
        } catch (NumberFormatException e) {
            throw new ValidationException("ID Bệnh nhân không hợp lệ.");
        }
    }

    /**
     * (Hàm createBenhNhan giữ nguyên)
     */
    private String createBenhNhan(HttpServletRequest request) throws ValidationException, Exception {
        BenhNhanDTO newBenhNhanDTO = createDTOFromRequest(request);
        BenhNhanDTO result = benhNhanService.createBenhNhan(newBenhNhanDTO);
        request.getSession().setAttribute("SUCCESS_MESSAGE", "Tạo bệnh nhân '" + result.getHoTen() + "' thành công!");
        return "redirect:MainController?action=listBenhNhan";
    }

    /**
     * (Hàm updateBenhNhan giữ nguyên)
     */
    private String updateBenhNhan(HttpServletRequest request) throws ValidationException, Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            BenhNhanDTO benhNhanDTO = createDTOFromRequest(request);
            BenhNhanDTO result = benhNhanService.updateBenhNhan(id, benhNhanDTO);
            request.getSession().setAttribute("SUCCESS_MESSAGE", "Cập nhật bệnh nhân '" + result.getHoTen() + "' thành công!");
            return "redirect:MainController?action=listBenhNhan";
        } catch (NumberFormatException e) {
            throw new ValidationException("ID Bệnh nhân không hợp lệ khi cập nhật.");
        }
    }

    /**
     * === BẮT ĐẦU SỬA (LOGIC XÓA MỀM) === Sửa lại để gọi hàm Xóa Mềm mới trong
     * Service
     */
    private String softDeleteBenhNhan(HttpServletRequest request) throws ValidationException, Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));

            // Sửa: Gọi hàm Xóa Mềm mới (hàm này sẽ xử lý cả Bệnh Nhân và Tài Khoản)
            // (BenhNhanService đã được nâng cấp ở lượt trước)
            benhNhanService.softDeleteBenhNhan(id);

            request.getSession().setAttribute("SUCCESS_MESSAGE", "Đã vô hiệu hóa bệnh nhân và tài khoản liên kết (nếu có) thành công!");
            return "redirect:MainController?action=listBenhNhan";

        } catch (NumberFormatException e) {
            throw new ValidationException("ID Bệnh nhân không hợp lệ.");
        }
    }
    // === KẾT THÚC SỬA ===

    /**
     * (Hàm loadFormDependencies giữ nguyên)
     */
    private void loadFormDependencies(HttpServletRequest request, String formAction) {
        try {
            List<TaiKhoanDTO> availableTaiKhoan = taiKhoanService.getActiveAndUnassignedAccounts("BENH_NHAN");
            request.setAttribute("LIST_TAIKHOAN", availableTaiKhoan);
        } catch (Exception e) {
            log("Không thể tải danh sách tài khoản cho form Bệnh nhân: " + e.getMessage(), e);
            request.setAttribute("LOAD_FORM_ERROR", "Lỗi tải danh sách Tài khoản.");
        }
    }

    /**
     * (Hàm handleServiceException giữ nguyên)
     */
    private void handleServiceException(HttpServletRequest request, Exception e, String formAction) {
        request.setAttribute("ERROR_MESSAGE", e.getMessage());
        request.setAttribute("BENHNHAN_DATA", createDTOFromRequest(request));
        request.setAttribute("formAction", formAction);
        loadFormDependencies(request, formAction);
    }

    /**
     * === SỬA (LỖI PARSE NGÀY SINH) === Sửa lại logic parse 'ngaySinh' để chấp
     * nhận 'datetime-local'
     */
    private BenhNhanDTO createDTOFromRequest(HttpServletRequest request) {
        BenhNhanDTO dto = new BenhNhanDTO();
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                dto.setId(Integer.parseInt(idStr));
            } catch (NumberFormatException e) {
                /* ignore */ }
        }
        java.util.function.Function<String, String> safeTrim = (s) -> (s != null) ? s.trim() : null;
        dto.setMaBenhNhan(safeTrim.apply(request.getParameter("maBenhNhan")));
        dto.setHoTen(safeTrim.apply(request.getParameter("hoTen")));
        dto.setGioiTinh(safeTrim.apply(request.getParameter("gioiTinh")));
        dto.setDiaChi(safeTrim.apply(request.getParameter("diaChi")));
        dto.setSoDienThoai(safeTrim.apply(request.getParameter("soDienThoai")));
        dto.setNhomMau(safeTrim.apply(request.getParameter("nhomMau")));
        dto.setTienSuBenh(safeTrim.apply(request.getParameter("tienSuBenh")));
        dto.setCccd(safeTrim.apply(request.getParameter("cccd")));

        // === SỬA LỖI (PARSE NGÀY SINH) ===
        String ngaySinhStr = request.getParameter("ngaySinh");
        if (ngaySinhStr != null && !ngaySinhStr.isEmpty()) {
            try {
                // Thử parse kiểu "datetime-local" (VD: "2025-11-01T16:56")
                LocalDateTime ldt = LocalDateTime.parse(ngaySinhStr);
                dto.setNgaySinh(ldt.toLocalDate());
            } catch (DateTimeParseException e) {
                // Thử parse kiểu "date" (VD: "2025-11-01")
                try {
                    dto.setNgaySinh(LocalDate.parse(ngaySinhStr));
                } catch (DateTimeParseException e2) {
                    log("Lỗi parse ngày sinh (không nhận diện được định dạng): " + ngaySinhStr, e2);
                }
            }
        }
        // === KẾT THÚC SỬA LỖI ===

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

    // (Các hàm Bệnh nhân 'showViewProfile', 'showEditProfile'... giữ nguyên)
    // ...
    private String showViewProfile(HttpServletRequest request) throws Exception {
        /* (Giữ nguyên) */
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER") == null) {
            throw new ValidationException("Vui lòng đăng nhập để xem hồ sơ.");
        }
        TaiKhoanDTO currentUser = (TaiKhoanDTO) session.getAttribute("USER");
        BenhNhanDTO benhNhan = benhNhanService.getBenhNhanByTaiKhoanId(currentUser.getId());
        if (benhNhan != null) {
            request.setAttribute("BENHNHAN_DATA", benhNhan);
            return VIEW_PROFILE_PAGE;
        } else {
            return "BenhNhanController?action=showEditProfile";
        }
    }

    private String showEditProfile(HttpServletRequest request, BenhNhanDTO dataToLoad) throws Exception {
        /* (Giữ nguyên) */
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER") == null) {
            throw new ValidationException("Vui lòng đăng nhập để xem hồ sơ.");
        }
        TaiKhoanDTO currentUser = (TaiKhoanDTO) session.getAttribute("USER");
        BenhNhanDTO benhNhan = dataToLoad;
        if (benhNhan == null) {
            benhNhan = benhNhanService.getBenhNhanByTaiKhoanId(currentUser.getId());
        }
        if (benhNhan != null) {
            request.setAttribute("BENHNHAN_DATA", benhNhan);
        } else {
            BenhNhanDTO emptyProfile = new BenhNhanDTO();
            emptyProfile.setHoTen(currentUser.getTenDangNhap());
            request.setAttribute("BENHNHAN_DATA", emptyProfile);
        }
        request.setAttribute("formAction", "saveProfile");
        return EDIT_PROFILE_PAGE;
    }

    private String showEditProfileWithExisting(HttpServletRequest request) throws Exception {
        /* (Giữ nguyên) */
        try {
            int patientId = Integer.parseInt(request.getParameter("patientId"));
            BenhNhanDTO existingPatient = benhNhanService.getBenhNhanByIdEvenIfInactive(patientId);
            if (existingPatient == null) {
                throw new ValidationException("Không tìm thấy hồ sơ.");
            }
            return showEditProfile(request, existingPatient);
        } catch (NumberFormatException e) {
            throw new ValidationException("ID không hợp lệ.");
        }
    }

    private String confirmAndLink(HttpServletRequest request) throws ValidationException, Exception {
        /* (Giữ nguyên) */
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
            if (existingPatient.getTaiKhoanId() != null && existingPatient.getTaiKhoanId() != currentUser.getId()) {
                throw new ValidationException("Hồ sơ này đã được liên kết với tài khoản khác.");
            }
            if (existingPatient.getTaiKhoanId() == null) {
                benhNhanService.linkAccountToPatient(patientId, currentUser.getId());
            }
            session.setAttribute("SUCCESS_MESSAGE", "Chào mừng trở lại! Hồ sơ y tế của bạn đã được liên kết thành công.");
            return HOME_PAGE;
        } catch (NumberFormatException e) {
            throw new ValidationException("ID không hợp lệ.");
        }
    }

    private String saveProfile(HttpServletRequest request) throws ValidationException, Exception {
        /* (Giữ nguyên) */
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER") == null) {
            throw new ValidationException("Phiên đăng nhập hết hạn.");
        }
        TaiKhoanDTO currentUser = (TaiKhoanDTO) session.getAttribute("USER");
        BenhNhanDTO dtoFromForm = createDTOFromRequest(request);
        Integer benhNhanId = dtoFromForm.getId();
        if (benhNhanId != null && benhNhanId > 0) {
            BenhNhanDTO existingPatient = benhNhanService.getBenhNhanById(benhNhanId);
            if (existingPatient == null) {
                throw new ValidationException("Hồ sơ bạn đang cố sửa không còn tồn tại.");
            }
            if (existingPatient.getTaiKhoanId() == null || existingPatient.getTaiKhoanId() != currentUser.getId()) {
                if (existingPatient.getTaiKhoanId() != null) {
                    throw new ValidationException("Lỗi bảo mật: Bạn không có quyền sửa hồ sơ này.");
                }
            }
            if (existingPatient.getTaiKhoanId() == null) {
                benhNhanService.linkAccountToPatient(benhNhanId, currentUser.getId());
                session.setAttribute("SUCCESS_MESSAGE", "Hồ sơ được cập nhật và liên kết thành công!");
            } else {
                session.setAttribute("SUCCESS_MESSAGE", "Cập nhật hồ sơ thành công!");
            }
            dtoFromForm.setTaiKhoanId(currentUser.getId());
            benhNhanService.updateBenhNhan(benhNhanId, dtoFromForm);
            return HOME_PAGE;
        } else {
            String cccd = dtoFromForm.getCccd();
            if (cccd == null || cccd.isEmpty()) {
                throw new ValidationException("CCCD là thông tin bắt buộc.");
            }
            BenhNhanDTO existingPatientByCccd = benhNhanService.findByCccd(cccd);
            if (existingPatientByCccd != null) {
                if (existingPatientByCccd.getTaiKhoanId() != null && existingPatientByCccd.getTaiKhoanId() != currentUser.getId()) {
                    throw new ValidationException("CCCD này đã được liên kết với một tài khoản khác.");
                }
                request.setAttribute("EXISTING_PATIENT", existingPatientByCccd);
                return CONFIRM_PROFILE_PAGE;
            } else {
                dtoFromForm.setTaiKhoanId(currentUser.getId());
                benhNhanService.createBenhNhan(dtoFromForm);
                session.setAttribute("SUCCESS_MESSAGE", "Tạo và liên kết hồ sơ thành công!");
                return HOME_PAGE;
            }
        }
    }

    @Override
    public String getServletInfo() {
        return "Controller quản lý các nghiệp vụ liên quan đến Bệnh Nhân.";
    }
}
