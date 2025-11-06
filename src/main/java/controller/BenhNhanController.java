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
 * Controller xử lý các nghiệp vụ liên quan đến Bệnh Nhân (Patient). **ĐÃ CẬP
 * NHẬT (Giai đoạn 2):** Triển khai luồng "Hoàn tất Hồ sơ".
 */
@WebServlet(name = "BenhNhanController", urlPatterns = {"/BenhNhanController"})
public class BenhNhanController extends HttpServlet {

    // (Hằng số của Admin)
    private static final String BENHNHAN_LIST_PAGE = "admin/danhSachBenhNhan.jsp";
    private static final String BENHNHAN_FORM_PAGE = "admin/formBenhNhan.jsp";
    private static final String ERROR_PAGE = "error.jsp";

    // --- HẰNG SỐ MỚI (Cho Bệnh nhân) ---
    private static final String FILL_PROFILE_PAGE = "user/fillProfile.jsp"; // Trang điền thông tin
    private static final String VIEW_PROFILE_PAGE = "user/viewProfile.jsp"; // Trang xem thông tin (giả sử)
    private static final String HOME_PAGE = "home.jsp"; // Trang chủ bệnh nhân

    private final BenhNhanService benhNhanService = new BenhNhanService();
    private final TaiKhoanService taiKhoanService = new TaiKhoanService();

    /**
     * **CẬP NHẬT:** Thêm 'case showProfile' cho bệnh nhân.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String url = ERROR_PAGE; // Mặc định là lỗi

        try {
            if (action == null || action.isEmpty()) {
                // (Nếu là admin, chuyển đến list. Nếu là bệnh nhân, chuyển đến showProfile)
                // Tạm thời mặc định là list
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

                // --- **LOGIC MỚI (Giai đoạn 2): Cho Bệnh nhân** ---
                case "showProfile":
                    url = showProfile(request);
                    break;
                // --- **KẾT THÚC THÊM MỚI** ---

                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho GET.");
                    url = ERROR_PAGE;
            }
        } catch (ValidationException e) {
            log("Lỗi Validation tại BenhNhanController (doGet): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", e.getMessage());
            url = ERROR_PAGE; // Lỗi nghiệp vụ (vd: ID sai) thì ra trang lỗi chung
        } catch (Exception e) {
            log("Lỗi Hệ thống tại BenhNhanController (doGet): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
            url = ERROR_PAGE;
        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    /**
     * **CẬP NHẬT:** Thêm 'case saveProfile' cho bệnh nhân.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String url = ERROR_PAGE;
        boolean loadListAfterSuccess = true; // (Cho Admin)

        boolean redirectAfterSuccess = false; // (Cho Bệnh nhân)
        String successRedirectUrl = null;
        String errorFormPage = BENHNHAN_FORM_PAGE; // Mặc định là form Admin

        try {
            if (action == null || action.isEmpty()) {
                throw new ValidationException("Hành động không được chỉ định.");
            }

            // **CẬP NHẬT:** Xác định trang quay về nếu lỗi
            if ("saveProfile".equals(action)) {
                errorFormPage = FILL_PROFILE_PAGE;
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

                // --- **LOGIC MỚI (Giai đoạn 2): Cho Bệnh nhân** ---
                case "saveProfile":
                    url = saveProfile(request); // Gọi hàm mới
                    if (url.equals(HOME_PAGE)) {
                        redirectAfterSuccess = true;
                        successRedirectUrl = HOME_PAGE;
                    }
                    loadListAfterSuccess = false; // Không cần tải lại danh sách admin
                    break;
                // --- **KẾT THÚC THÊM MỚI** ---

                // --- **XÓA CASE CŨ:** (updateProfile đã bị xóa) ---
                default:
                    loadListAfterSuccess = false;
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho POST.");
            }

            if (loadListAfterSuccess && !url.equals(ERROR_PAGE) && !url.equals(BENHNHAN_FORM_PAGE)) {
                url = listBenhNhan(request);
            }

        } catch (ValidationException e) { // **BẮT LỖI VALIDATION**
            log("Lỗi Validation tại BenhNhanController (doPost): " + e.getMessage(), e);

            // **CẬP NHẬT:** Xử lý lỗi cho form 'saveProfile'
            if ("saveProfile".equals(action)) {
                request.setAttribute("ERROR_MESSAGE", e.getMessage());
                // Giữ lại dữ liệu người dùng đã nhập
                request.setAttribute("BENHNHAN_DATA", createDTOFromRequest(request));
                request.setAttribute("formAction", "saveProfile"); // Đặt lại action cho form
                url = FILL_PROFILE_PAGE; // Quay lại form điền thông tin
            } else {
                // Logic cũ (cho Admin)
                handleServiceException(request, e, action);
                url = BENHNHAN_FORM_PAGE;
            }
            loadListAfterSuccess = false;
            redirectAfterSuccess = false;

        } catch (Exception e) { // **BẮT LỖI HỆ THỐNG**
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
// --- THÊM DÒNG NÀY ĐỂ DEBUG ---
                System.out.println("ACTION = [" + request.getParameter("action") + "] --- FORWARDING TO URL = [" + url + "]");

                request.getRequestDispatcher(url).forward(request, response);
            }
        }
    }

    // --- **HÀM MỚI (Giai đoạn 2): XỬ LÝ ACTION 'showProfile'** ---
    /**
     * (doGet) Kiểm tra xem bệnh nhân đã có hồ sơ chưa. Nếu có -> xem hồ sơ. Nếu
     * chưa -> chuyển đến form điền hồ sơ.
     */
    private String showProfile(HttpServletRequest request) throws Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER") == null) {
            throw new ValidationException("Vui lòng đăng nhập để xem hồ sơ.");
        }
        TaiKhoanDTO currentUser = (TaiKhoanDTO) session.getAttribute("USER");

        // Kiểm tra xem tài khoản này đã được liên kết với hồ sơ nào chưa
        BenhNhanDTO benhNhan = benhNhanService.getBenhNhanByTaiKhoanId(currentUser.getId());

        if (benhNhan != null) {
            // **Kịch bản A: Đã có hồ sơ** (đã liên kết)
            request.setAttribute("BENHNHAN_DATA", benhNhan); // Đổi tên attribute cho nhất quán
            // Tạm thời, chúng ta vẫn chuyển đến form 'fill' (nhưng ở chế độ xem/sửa)
            request.setAttribute("formAction", "saveProfile"); // Vẫn cho phép sửa
            return FILL_PROFILE_PAGE;
        } else {
            // **Kịch bản B: Chưa có hồ sơ** (Tài khoản mới đăng ký)
            request.setAttribute("formAction", "saveProfile");
            // Gửi 1 DTO rỗng, điền sẵn Họ Tên từ Tên Đăng Nhập
            BenhNhanDTO emptyProfile = new BenhNhanDTO();
            emptyProfile.setHoTen(currentUser.getTenDangNhap());
            request.setAttribute("BENHNHAN_DATA", emptyProfile);

            return FILL_PROFILE_PAGE; // Chuyển đến trang ĐIỀN hồ sơ
        }
    }

 // --- **HÀM MỚI (Giai đoạn 2): XỬ LÝ ACTION 'saveProfile'** ---
    /**
     * (doPost) Xử lý logic "Lưu Hồ sơ".
     * **ĐÃ CẬP NHẬT (Refactor):** Tách biệt 2 kịch bản:
     * 1. CẬP NHẬT: Nếu 'id' bệnh nhân được gửi lên (người dùng cũ).
     * 2. TẠO MỚI/LIÊN KẾT: Nếu 'id' là null (người dùng mới đăng ký).
     */
    private String saveProfile(HttpServletRequest request) throws ValidationException, Exception {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("USER") == null) {
            throw new ValidationException("Phiên đăng nhập hết hạn.");
        }
        TaiKhoanDTO currentUser = (TaiKhoanDTO) session.getAttribute("USER");

        // 1. Lấy dữ liệu từ form (Hàm này của bạn đã tự động lấy 'id' nếu có)
        BenhNhanDTO dtoFromForm = createDTOFromRequest(request);
        Integer benhNhanId = dtoFromForm.getId();

        // 2. Phân luồng logic
        if (benhNhanId != null && benhNhanId > 0) {
            // --- KỊCH BẢN 1: CẬP NHẬT HỒ SƠ (Đã có ID) ---

            // 2.1. KIỂM TRA BẢO MẬT: 
            //      Người dùng này có ĐÚNG là chủ của hồ sơ họ đang sửa không?
            BenhNhanDTO existingPatient = benhNhanService.getBenhNhanById(benhNhanId);
            if (existingPatient == null) {
                 throw new ValidationException("Hồ sơ bạn đang cố sửa không còn tồn tại.");
            }
            if (existingPatient.getTaiKhoanId() != currentUser.getId()) {
                // (Ghi log bảo mật ở đây nếu cần)
                log("CẢNH BÁO BẢO MẬT: Tài khoản ID " + currentUser.getId() 
                        + " đang cố sửa hồ sơ Bệnh nhân ID " + benhNhanId 
                        + " mà họ không sở hữu.");
                throw new ValidationException("Lỗi bảo mật: Bạn không có quyền sửa hồ sơ này.");
            }

            // 2.2. GỌI SERVICE CẬP NHẬT:
            //      Hàm này sẽ chịu trách nhiệm bắt Lỗi 1, 2, 3 (Validation)
            benhNhanService.updateBenhNhan(benhNhanId, dtoFromForm);
            session.setAttribute("SUCCESS_MESSAGE", "Cập nhật hồ sơ thành công!");

        } else {
            // --- KỊCH BẢN 2: TẠO MỚI / LIÊN KẾT (Chưa có ID) ---
            // (Đây là logic cũ của bạn, giờ được đặt trong 'else')

            String cccd = dtoFromForm.getCccd();
            // (Service cần validate cccd rỗng/định dạng)
            if (cccd == null || cccd.trim().isEmpty()) {
                throw new ValidationException("CCCD là thông tin bắt buộc.");
            }
            
            BenhNhanDTO existingPatientByCccd = benhNhanService.findByCccd(cccd);

            if (existingPatientByCccd != null) {
                // **Kịch bản 2A: ĐÃ TỪNG KHÁM (CCCD đã tồn tại)**

                // Kiểm tra xem hồ sơ cũ này đã bị liên kết với TÀI KHOẢN KHÁC chưa
                if (existingPatientByCccd.getTaiKhoanId() != null 
                        && existingPatientByCccd.getTaiKhoanId() != currentUser.getId()) {
                    throw new ValidationException("CCCD này đã được liên kết với một tài khoản khác.");
                }

                // Cập nhật thông tin hồ sơ theo form (ví dụ: SĐT, Địa chỉ mới)
                benhNhanService.updateBenhNhan(existingPatientByCccd.getId(), dtoFromForm);

                // Chỉ liên kết nếu hồ sơ cũ chưa có tài khoản
                if (existingPatientByCccd.getTaiKhoanId() == null) {
                    benhNhanService.linkAccountToPatient(existingPatientByCccd.getId(), currentUser.getId());
                }

                session.setAttribute("SUCCESS_MESSAGE", "Chào mừng trở lại! Chúng tôi đã cập nhật và liên kết hồ sơ y tế của bạn.");

            } else {
                // **Kịch bản 2B: NGƯỜI MỚI 100% (CCCD không tồn tại)**

                // Liên kết tài khoản này với DTO
                dtoFromForm.setTaiKhoanId(currentUser.getId());

                // Gọi hàm create (Service sẽ tự tạo maBenhNhan)
                benhNhanService.createBenhNhan(dtoFromForm);

                session.setAttribute("SUCCESS_MESSAGE", "Tạo và liên kết hồ sơ thành công!");
            }
        }

        return HOME_PAGE; // Chuyển về trang chủ Bệnh nhân
    }
    
    
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

    /**
     * (Hàm createDTOFromRequest của bạn đã đúng, giữ nguyên)
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
            dto.setTaiKhoanId(null);
        }

        return dto;
    }

    @Override
    public String getServletInfo() {
        return "Controller quản lý các nghiệp vụ liên quan đến Bệnh Nhân.";
    }
}
