package controller;

import exception.ValidationException;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.dto.KhoaDTO;
import model.dto.NhanVienDTO;
import model.dto.TaiKhoanDTO;
import service.KhoaService;
import service.NhanVienService;
import service.TaiKhoanService;

/**
 * Controller xử lý các nghiệp vụ liên quan đến Nhân Viên (Staff). (ĐÃ NÂNG CẤP:
 * Thêm Phân trang và Post-Redirect-Get)
 */
@WebServlet(name = "NhanVienController", urlPatterns = {"/NhanVienController"})
public class NhanVienController extends HttpServlet {

    // (Các hằng số JSP giữ nguyên)
    private static final String NHANVIEN_LIST_PAGE = "admin/danhSachNhanVien.jsp";
    private static final String NHANVIEN_FORM_PAGE = "admin/formNhanVien.jsp";
    private static final String ERROR_PAGE = "error.jsp";

    // (Các Service giữ nguyên)
    private final NhanVienService nhanVienService = new NhanVienService();
    private final TaiKhoanService taiKhoanService = new TaiKhoanService();
    private final KhoaService khoaService = new KhoaService();

    // === HẰNG SỐ MỚI CHO PHÂN TRANG ===
    private static final int PAGE_SIZE = 10; // 10 nhân viên mỗi trang

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String url = ERROR_PAGE; // Đặt mặc định là lỗi

        try {
            if (action == null || action.isEmpty()) {
                action = "listNhanVien";
            }

            switch (action) {
                case "listNhanVien":
                    url = listNhanVien(request); // Đã sửa để dùng phân trang
                    break;
                case "showNhanVienCreateForm":
                    loadFormDependencies(request, "createNhanVien");
                    request.setAttribute("formAction", "createNhanVien");
                    url = NHANVIEN_FORM_PAGE;
                    break;
                case "showNhanVienEditForm":
                    url = showNhanVienEditForm(request);
                    break;
                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho GET.");
                    url = ERROR_PAGE;
            }
        } catch (ValidationException e) {
            log("Lỗi validation tại NhanVienController (doGet): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi nghiệp vụ: " + e.getMessage());
            url = ERROR_PAGE;
        } catch (Exception e) {
            log("Lỗi hệ thống tại NhanVienController (doGet): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
            url = ERROR_PAGE;
        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String url = ERROR_PAGE; // Mặc định là lỗi
        String formErrorPage = NHANVIEN_FORM_PAGE; // Quay về form nếu lỗi validation

        try {
            if (action == null || action.isEmpty()) {
                throw new Exception("Hành động không được chỉ định.");
            }

            // Xác định trang lỗi cho action "delete"
            if ("softDeleteNhanVien".equals(action)) {
                formErrorPage = NHANVIEN_LIST_PAGE; // Lỗi xóa thì quay về trang list
            }

            switch (action) {
                case "createNhanVien":
                    url = createNhanVien(request); // Sửa: Trả về redirect
                    break;
                case "updateNhanVien":
                    url = updateNhanVien(request); // Sửa: Trả về redirect
                    break;
                case "softDeleteNhanVien":
                    url = softDeleteNhanVien(request); // Sửa: Trả về redirect
                    break;
                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho POST.");
                    url = ERROR_PAGE;
            }

            // --- BẮT ĐẦU SỬA (ÁP DỤNG PRG PATTERN) ---
            // (Đã xóa block `if (loadListAfterSuccess...)` cũ)
        } catch (ValidationException ve) {
            // Lỗi nghiệp vụ (VD: SĐT trùng, tên trống...)
            log("Lỗi validation tại NhanVienController (doPost): " + ve.getMessage());
            // Quay lại form với thông báo lỗi và dữ liệu đã nhập
            handleServiceException(request, ve, action);
            url = formErrorPage; // Sẽ là NHANVIEN_FORM_PAGE

        } catch (Exception e) {
            // Lỗi hệ thống (VD: Lỗi DB, NullPointerException...)
            log("Lỗi hệ thống tại NhanVienController (doPost): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống nghiêm trọng: " + e.getMessage());
            url = ERROR_PAGE;
        } finally {
            // Logic PRG: Nếu url là redirect, thì redirect. Ngược lại, forward.
            if (url.startsWith("redirect:")) {
                response.sendRedirect(request.getContextPath() + "/" + url.substring("redirect:".length()));
            } else {
                request.getRequestDispatcher(url).forward(request, response);
            }
        }
        // --- KẾT THÚC SỬA ---
    }

/**
     * === ĐÃ NÂNG CẤP (PHÂN TRANG + TÌM KIẾM) ===
     * Lấy danh sách Nhân Viên (có phân trang và tìm kiếm)
     */
    private String listNhanVien(HttpServletRequest request) throws Exception {
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
        List<NhanVienDTO> list;
        long totalNhanVien;

        // 4. Logic nghiệp vụ: Kiểm tra xem có tìm kiếm hay không
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Kịch bản TÌM KIẾM
            String trimmedKeyword = keyword.trim();
            list = nhanVienService.searchNhanVienPaginated(trimmedKeyword, page, PAGE_SIZE);
            totalNhanVien = nhanVienService.getNhanVienSearchCount(trimmedKeyword);
            request.setAttribute("searchKeyword", keyword); // <-- QUAN TRỌNG: Gửi lại keyword
        } else {
            // Kịch bản XEM TẤT CẢ (phân trang)
            list = nhanVienService.getAllNhanVienPaginated(page, PAGE_SIZE);
            totalNhanVien = nhanVienService.getNhanVienCount();
        }

        // 5. Tính toán và Gửi dữ liệu về JSP
        long totalPages = (long) Math.ceil((double) totalNhanVien / PAGE_SIZE);

        request.setAttribute("LIST_NHANVIEN", list);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        return NHANVIEN_LIST_PAGE;
    }

    /**
     * (Hàm showNhanVienEditForm giữ nguyên)
     */
    private String showNhanVienEditForm(HttpServletRequest request) throws Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            NhanVienDTO nhanVien = nhanVienService.getNhanVienById(id);
            request.setAttribute("NHANVIEN_DATA", nhanVien);
            loadFormDependencies(request, "updateNhanVien");
            request.setAttribute("formAction", "updateNhanVien");
            return NHANVIEN_FORM_PAGE;
        } catch (NumberFormatException e) {
            // Ném ValidationException để doGet bắt được và đưa ra trang lỗi
            throw new ValidationException("ID Nhân viên không hợp lệ.");
        }
    }

    /**
     * Xử lý logic tạo mới một Nhân Viên. (SỬA: Dùng Session Attribute và trả về
     * redirect)
     */
    private String createNhanVien(HttpServletRequest request) throws ValidationException, Exception {
        NhanVienDTO newNhanVienDTO = createDTOFromRequest(request);
        NhanVienDTO result = nhanVienService.createNhanVien(newNhanVienDTO);
        // Dùng Session để thông báo tồn tại sau khi redirect
        request.getSession().setAttribute("SUCCESS_MESSAGE", "Tạo nhân viên '" + result.getHoTen() + "' thành công!");
        return "redirect:MainController?action=listNhanVien";
    }

    /**
     * Xử lý logic cập nhật một Nhân Viên. (SỬA: Dùng Session Attribute và trả
     * về redirect)
     */
    private String updateNhanVien(HttpServletRequest request) throws ValidationException, Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            NhanVienDTO nhanVienDTO = createDTOFromRequest(request);
            NhanVienDTO result = nhanVienService.updateNhanVien(id, nhanVienDTO);
            // Dùng Session để thông báo tồn tại sau khi redirect
            request.getSession().setAttribute("SUCCESS_MESSAGE", "Cập nhật nhân viên '" + result.getHoTen() + "' thành công!");
            return "redirect:MainController?action=listNhanVien";
        } catch (NumberFormatException e) {
            // Ném lỗi để doPost bắt được và chuyển về form
            throw new ValidationException("ID Nhân viên không hợp lệ khi cập nhật.");
        }
    }

    /**
     * Xử lý logic Soft Delete một Nhân Viên. (SỬA: Dùng Session Attribute và
     * trả về redirect)
     */
    private String softDeleteNhanVien(HttpServletRequest request) throws ValidationException, Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            nhanVienService.softDeleteNhanVien(id);
            // Dùng Session để thông báo tồn tại sau khi redirect
            request.getSession().setAttribute("SUCCESS_MESSAGE", "Đã vô hiệu hóa nhân viên thành công!");
            return "redirect:MainController?action=listNhanVien";
        } catch (NumberFormatException e) {
            // Ném lỗi để doPost bắt được (và trả về trang list)
            throw new ValidationException("ID Nhân viên không hợp lệ khi xóa.");
        }
        // Các Exception khác (Validation, Exception) sẽ tự ném ra và bị doPost bắt
    }

    /**
     * Tải các dữ liệu phụ thuộc cho form (Tài khoản, Khoa). (SỬA: Tải danh sách
     * Khoa không phân trang cho dropdown)
     */
    private void loadFormDependencies(HttpServletRequest request, String formAction) {
        try {
            // Chỉ tải danh sách tài khoản chưa gán KHI TẠO MỚI
            if ("createNhanVien".equals(formAction)) {
                List<TaiKhoanDTO> unassignedStaffAccounts = new ArrayList<>();
                unassignedStaffAccounts.addAll(taiKhoanService.getActiveAndUnassignedAccounts("ADMIN"));
                unassignedStaffAccounts.addAll(taiKhoanService.getActiveAndUnassignedAccounts("BAC_SI"));
                unassignedStaffAccounts.addAll(taiKhoanService.getActiveAndUnassignedAccounts("DUOC_SI"));
                // Thêm các vai trò nhân viên khác nếu có...
                request.setAttribute("LIST_TAIKHOAN", unassignedStaffAccounts);
            }

            // Luôn tải danh sách Khoa (cho cả Tạo và Sửa)
            // Hàm getAllKhoa() của KhoaService là đúng, nó lấy tất cả (cho dropdown)
            List<KhoaDTO> listKhoa = khoaService.getAllKhoa();
            request.setAttribute("LIST_KHOA", listKhoa);

        } catch (Exception e) {
            log("Không thể tải dữ liệu phụ thuộc cho form Nhân viên: " + e.getMessage(), e);
            request.setAttribute("LOAD_FORM_ERROR", "Lỗi tải danh sách Tài khoản/Khoa.");
        }
    }

    /**
     * (Hàm handleServiceException giữ nguyên)
     */
    private void handleServiceException(HttpServletRequest request, ValidationException e, String formAction) {
        request.setAttribute("ERROR_MESSAGE", e.getMessage());
        request.setAttribute("NHANVIEN_DATA", createDTOFromRequest(request));
        request.setAttribute("formAction", formAction);
        loadFormDependencies(request, formAction);
    }

    /**
     * (Hàm createDTOFromRequest giữ nguyên)
     */
    private NhanVienDTO createDTOFromRequest(HttpServletRequest request) {
        NhanVienDTO dto = new NhanVienDTO();
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                dto.setId(Integer.parseInt(idStr));
            } catch (NumberFormatException e) {
                /* ignore */ }
        }
        dto.setHoTen(request.getParameter("hoTen"));
        dto.setGioiTinh(request.getParameter("gioiTinh")); // SỬA LỖI CHÍNH TẢ: "giớiTinh" -> "gioiTinh"
        dto.setDiaChi(request.getParameter("diaChi"));
        dto.setSoDienThoai(request.getParameter("soDienThoai"));
        dto.setChuyenMon(request.getParameter("chuyenMon"));
        dto.setBangCap(request.getParameter("bangCap"));

        String ngaySinhStr = request.getParameter("ngaySinh");
        if (ngaySinhStr != null && !ngaySinhStr.isEmpty()) {
            try {
                // Input type="datetime-local" gửi về "YYYY-MM-DDTHH:MM"
                // (Nếu bạn dùng type="date" thì dùng LocalDate.parse(ngaySinhStr).atStartOfDay())
                dto.setNgaySinh(LocalDateTime.parse(ngaySinhStr));
            } catch (DateTimeParseException e) {
                log("Lỗi parse ngày sinh (datetime-local): " + ngaySinhStr);
                // Thử parse kiểu "date"
                try {
                    dto.setNgaySinh(LocalDate.parse(ngaySinhStr).atStartOfDay());
                } catch (DateTimeParseException e2) {
                    log("Lỗi parse ngày sinh (date): " + ngaySinhStr);
                }
            }
        }

        // (Logic lấy taiKhoanId và khoaId giữ nguyên)
        String formAction = request.getParameter("action");
        if ("createNhanVien".equals(formAction)) {
            String taiKhoanIdStr = request.getParameter("taiKhoanId");
            if (taiKhoanIdStr != null && !taiKhoanIdStr.isEmpty()) {
                try {
                    dto.setTaiKhoanId(Integer.parseInt(taiKhoanIdStr));
                } catch (NumberFormatException e) {
                    /* ignore */ }
            }
        }

        String khoaIdStr = request.getParameter("khoaId");
        if (khoaIdStr != null && !khoaIdStr.isEmpty() && !khoaIdStr.equals("0")) {
            try {
                dto.setKhoaId(Integer.parseInt(khoaIdStr));
            } catch (NumberFormatException e) {
                /* ignore */ }
        } else {
            dto.setKhoaId(null);
        }

        return dto;
    }

    @Override
    public String getServletInfo() {
        return "Controller quản lý các nghiệp vụ liên quan đến Nhân Viên.";
    }
}
