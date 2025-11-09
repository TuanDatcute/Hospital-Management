package controller;

import exception.ValidationException; // --- THÊM MỚI ---
import java.io.IOException;
import java.time.LocalDate; // --- THÊM MỚI ---
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList; // --- THÊM MỚI ---
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
 * Controller xử lý các nghiệp vụ liên quan đến Nhân Viên (Staff).
 */
@WebServlet(name = "NhanVienController", urlPatterns = {"/NhanVienController"})
public class NhanVienController extends HttpServlet {

    // Khai báo URL cho các trang JSP
    private static final String NHANVIEN_LIST_PAGE = "admin/danhSachNhanVien.jsp";
    private static final String NHANVIEN_FORM_PAGE = "admin/formNhanVien.jsp";
    private static final String ERROR_PAGE = "error.jsp";

    // Khởi tạo các Service cần thiết
    private final NhanVienService nhanVienService = new NhanVienService();
    private final TaiKhoanService taiKhoanService = new TaiKhoanService();
    private final KhoaService khoaService = new KhoaService();

    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String url = NHANVIEN_LIST_PAGE; // Mặc định

        try {
            if (action == null || action.isEmpty()) {
                action = "listNhanVien";
            }

            switch (action) {
                case "listNhanVien":
                    url = listNhanVien(request);
                    break;
                case "showNhanVienCreateForm":
                    loadFormDependencies(request, "createNhanVien"); // Truyền action vào
                    request.setAttribute("formAction", "createNhanVien");
                    url = NHANVIEN_FORM_PAGE;
                    break;
                case "showNhanVienEditForm":
                    url = showNhanVienEditForm(request);
                    break;
                // Không xử lý delete bằng GET nữa
                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho GET.");
                    url = ERROR_PAGE;
            }
            // --- CẬP NHẬT: Tách riêng 2 catch ---
        } catch (ValidationException e) {
            // Lỗi nghiệp vụ (VD: getById không thấy) -> Chuyển đến trang lỗi
            log("Lỗi validation tại NhanVienController (doGet): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi nghiệp vụ: " + e.getMessage());
            url = ERROR_PAGE; // Hoặc có thể là trang list nếu muốn
        } catch (Exception e) {
            // Lỗi hệ thống -> Chuyển đến trang lỗi
            log("Lỗi hệ thống tại NhanVienController (doGet): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
            url = ERROR_PAGE;
            // --- KẾT THÚC CẬP NHẬT ---
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
        boolean loadListAfterSuccess = true; // Biến cờ để kiểm soát việc load lại list

        try {
            if (action == null || action.isEmpty()) {
                throw new Exception("Hành động không được chỉ định.");
            }

            switch (action) {
                case "createNhanVien":
                    url = createNhanVien(request);
                    break;
                case "updateNhanVien":
                    url = updateNhanVien(request);
                    break;
                case "softDeleteNhanVien": // Xử lý Soft Delete
                    url = softDeleteNhanVien(request);
                    break;
                default:
                    loadListAfterSuccess = false;
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho POST.");
            }
            // Nếu thành công (không phải trang lỗi/form) và cần load lại list
            if (loadListAfterSuccess && !url.equals(ERROR_PAGE) && !url.equals(NHANVIEN_FORM_PAGE)) {
                url = listNhanVien(request); // Load lại danh sách sau khi CUD thành công
            }

            // --- CẬP NHẬT: Tách riêng 2 catch ---
        } catch (ValidationException ve) {
            // Lỗi nghiệp vụ (VD: SĐT trùng, tên trống...)
            log("Lỗi validation tại NhanVienController (doPost): " + ve.getMessage());
            // Quay lại form với thông báo lỗi và dữ liệu đã nhập
            handleServiceException(request, ve, action);
            url = NHANVIEN_FORM_PAGE;
            loadListAfterSuccess = false; // Không load lại list khi có lỗi
        } catch (Exception e) {
            // Lỗi hệ thống (VD: Lỗi DB, NullPointerException...)
            log("Lỗi hệ thống tại NhanVienController (doPost): " + e.getMessage(), e);
            // Gửi đến trang lỗi chung
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống nghiêm trọng: " + e.getMessage());
            url = ERROR_PAGE;
            loadListAfterSuccess = false;
            // --- KẾT THÚC CẬP NHẬT ---
        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    /**
     * Lấy danh sách Nhân Viên (có phân trang VÀ tìm kiếm) và chuyển đến trang
     * hiển thị.
     */
    private String listNhanVien(HttpServletRequest request) throws Exception {

        // 1. Xử lý lấy số trang (page)
        int page = 1;
        String pageStr = request.getParameter("page");
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                log("Tham số 'page' không hợp lệ, sử dụng trang 1.", e);
                page = 1;
            }
        }
        if (page < 1) {
            page = 1; // Đảm bảo trang không bao giờ < 1
        }

        // 2. Xử lý lấy từ khóa tìm kiếm (keyword)
        String keyword = request.getParameter("keyword");
        List<NhanVienDTO> list;
        long totalNhanVien;

        // 3. Phân nhánh logic: Có tìm kiếm hay chỉ phân trang?
        if (keyword != null && !keyword.trim().isEmpty()) {
            // --- TRƯỜNG HỢP CÓ TÌM KIẾM ---
            String trimmedKeyword = keyword.trim();

            // Gọi service tìm kiếm CÓ PHÂN TRANG
            list = nhanVienService.searchNhanVienPaginated(trimmedKeyword, page, PAGE_SIZE);

            // Gọi service đếm kết quả TÌM KIẾM
            totalNhanVien = nhanVienService.getNhanVienSearchCount(trimmedKeyword);

            // Gửi lại keyword về JSP để hiển thị lại trên ô tìm kiếm
            request.setAttribute("searchKeyword", keyword);

        } else {
            // --- TRƯỜNG HỢP CHỈ XEM DANH SÁCH (KHÔNG TÌM KIẾM) ---

            // Gọi service lấy tất cả CÓ PHÂN TRANG
            list = nhanVienService.getAllNhanVienPaginated(page, PAGE_SIZE);

            // Gọi service đếm TẤT CẢ
            totalNhanVien = nhanVienService.getNhanVienCount();
        }

        // 4. Tính toán và gửi thông tin phân trang về JSP
        long totalPages = (long) Math.ceil((double) totalNhanVien / PAGE_SIZE);

        request.setAttribute("LIST_NHANVIEN", list);       // Danh sách để hiển thị
        request.setAttribute("currentPage", page);         // Trang hiện tại
        request.setAttribute("totalPages", totalPages);    // Tổng số trang
        request.setAttribute("totalItems", totalNhanVien); // (Tùy chọn) Tổng số mục

        return NHANVIEN_LIST_PAGE;
    }

    /**
     * Lấy thông tin Nhân Viên cần sửa và hiển thị form. (Giữ nguyên - lỗi sẽ
     * được bắt bởi doGet)
     */
    private String showNhanVienEditForm(HttpServletRequest request) throws Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            // Service đã được cập nhật để chỉ lấy nhân viên hoạt động
            // Sẽ ném ValidationException nếu không tìm thấy hoặc bị khóa
            NhanVienDTO nhanVien = nhanVienService.getNhanVienById(id);
            request.setAttribute("NHANVIEN_DATA", nhanVien); // Dữ liệu nhân viên cần sửa
            loadFormDependencies(request, "updateNhanVien"); // Tải danh sách Khoa
            request.setAttribute("formAction", "updateNhanVien");
            return NHANVIEN_FORM_PAGE;
        } catch (NumberFormatException e) {
            request.setAttribute("ERROR_MESSAGE", "ID Nhân viên không hợp lệ.");
            return ERROR_PAGE;
        }
        // Exception từ Service (VD: nhân viên bị khóa) sẽ được bắt ở doGet
    }

    /**
     * Xử lý logic tạo mới một Nhân Viên. (Giữ nguyên - lỗi sẽ được bắt bởi
     * doPost)
     */
    private String createNhanVien(HttpServletRequest request) throws Exception {
        NhanVienDTO newNhanVienDTO = createDTOFromRequest(request);
        NhanVienDTO result = nhanVienService.createNhanVien(newNhanVienDTO);
        request.setAttribute("SUCCESS_MESSAGE", "Tạo nhân viên '" + result.getHoTen() + "' thành công!");
        // Trả về trang list để doPost gọi listNhanVien
        return NHANVIEN_LIST_PAGE;
    }

    /**
     * Xử lý logic cập nhật một Nhân Viên. (Giữ nguyên - lỗi sẽ được bắt bởi
     * doPost)
     */
    private String updateNhanVien(HttpServletRequest request) throws Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            NhanVienDTO nhanVienDTO = createDTOFromRequest(request);
            NhanVienDTO result = nhanVienService.updateNhanVien(id, nhanVienDTO);
            request.setAttribute("SUCCESS_MESSAGE", "Cập nhật nhân viên '" + result.getHoTen() + "' thành công!");
            return NHANVIEN_LIST_PAGE;
        } catch (NumberFormatException e) {
            request.setAttribute("ERROR_MESSAGE", "ID Nhân viên không hợp lệ khi cập nhật.");
            return ERROR_PAGE;
        }
        // Exception từ Service (VD: nhân viên bị khóa) sẽ được bắt ở doPost
    }

    /**
     * Xử lý logic Soft Delete một Nhân Viên. (Cập nhật để bắt
     * ValidationException cụ thể)
     */
    private String softDeleteNhanVien(HttpServletRequest request) {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            nhanVienService.softDeleteNhanVien(id);
            request.setAttribute("SUCCESS_MESSAGE", "Đã vô hiệu hóa nhân viên thành công!");
            return NHANVIEN_LIST_PAGE; // Trả về trang list để doPost load lại
        } catch (NumberFormatException e) {
            request.setAttribute("ERROR_MESSAGE", "ID Nhân viên không hợp lệ khi xóa.");
            return ERROR_PAGE;
            // --- CẬP NHẬT ---
        } catch (ValidationException ve) {
            log("Lỗi validation khi vô hiệu hóa nhân viên: " + ve.getMessage(), ve);
            request.setAttribute("ERROR_MESSAGE", "Vô hiệu hóa nhân viên thất bại: " + ve.getMessage());
            return NHANVIEN_LIST_PAGE; // Quay về trang list
        } catch (Exception e) {
            // --- KẾT THÚC CẬP NHẬT ---
            log("Lỗi hệ thống khi vô hiệu hóa nhân viên: " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Vô hiệu hóa nhân viên thất bại (lỗi hệ thống): " + e.getMessage());
            return ERROR_PAGE; // Lỗi hệ thống nên về trang lỗi
        }
    }

    /**
     * Tải các dữ liệu phụ thuộc cho form (danh sách Tài khoản hoạt động chưa
     * gán, Khoa).
     */
    private void loadFormDependencies(HttpServletRequest request, String formAction) {
        try {
            if ("createNhanVien".equals(formAction)) {
                // --- ✨ SỬA LẠI NHƯ SAU ---
                // Gọi hàm mới (ở Bước 2) để lấy TẤT CẢ vai trò nhân viên (trừ bệnh nhân)
                List<TaiKhoanDTO> unassignedStaffAccounts = taiKhoanService.getAllActiveAndUnassignedStaffAccounts();

                request.setAttribute("LIST_TAIKHOAN", unassignedStaffAccounts);
                // --- KẾT THÚC SỬA ---
            }

            List<KhoaDTO> listKhoa = khoaService.getAllKhoa();
            request.setAttribute("LIST_KHOA", listKhoa);
        } catch (Exception e) {
            log("Không thể tải dữ liệu phụ thuộc cho form Nhân viên: " + e.getMessage(), e);
            request.setAttribute("LOAD_FORM_ERROR", "Lỗi tải danh sách Tài khoản/Khoa.");
        }
    }

    /**
     * Xử lý lỗi từ Service (chỉ dành cho ValidationException) và gửi lại form.
     */
    private void handleServiceException(HttpServletRequest request, ValidationException e, String formAction) {
        request.setAttribute("ERROR_MESSAGE", e.getMessage());
        // Giữ lại dữ liệu người dùng đã nhập để hiển thị lại trên form
        request.setAttribute("NHANVIEN_DATA", createDTOFromRequest(request));
        request.setAttribute("formAction", formAction);
        // Tải lại các dropdowns (Khoa, Tài khoản...)
        loadFormDependencies(request, formAction);
    }

    /**
     * Hàm tiện ích tạo NhanVienDTO từ request.
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
        dto.setGioiTinh(request.getParameter("giớiTinh"));
        dto.setDiaChi(request.getParameter("diaChi"));
        dto.setSoDienThoai(request.getParameter("soDienThoai"));
        dto.setChuyenMon(request.getParameter("chuyenMon"));
        dto.setBangCap(request.getParameter("bangCap"));

        String ngaySinhStr = request.getParameter("ngaySinh");
        if (ngaySinhStr != null && !ngaySinhStr.isEmpty()) {
            try {
                // --- CẬP NHẬT: Sửa lỗi Parse ---
                // Input type="date" gửi về "YYYY-MM-DD"
                // Parse thành LocalDate trước, sau đó chuyển sang LocalDateTime (vào lúc 00:00)
                dto.setNgaySinh(LocalDate.parse(ngaySinhStr).atStartOfDay());
                // --- KẾT THÚC CẬP NHẬT ---
            } catch (DateTimeParseException e) {
                log("Lỗi parse ngày sinh: " + ngaySinhStr);
                // Có thể set attribute báo lỗi nếu cần
            }
        }

        // Chỉ lấy TaiKhoanId khi tạo mới
        String formAction = request.getParameter("action");
        if ("createNhanVien".equals(formAction)) {
            String taiKhoanIdStr = request.getParameter("taiKhoanId");
            if (taiKhoanIdStr != null && !taiKhoanIdStr.isEmpty()) {
                try {
                    dto.setTaiKhoanId(Integer.parseInt(taiKhoanIdStr));
                } catch (NumberFormatException e) {
                    /* ignore */ }
            }
        } else if ("updateNhanVien".equals(formAction)) {
            // Khi update, không lấy TaiKhoanId từ request
            // Lấy từ hidden field nếu cần thiết, để gửi về form nếu có lỗi
            String hiddenTaiKhoanId = request.getParameter("hiddenTaiKhoanId"); // Ví dụ tên hidden field
            if (hiddenTaiKhoanId != null && !hiddenTaiKhoanId.isEmpty()) {
                try {
                    // Chỉ set lại để gửi về form nếu có lỗi, Service không dùng cái này
                    dto.setTaiKhoanId(Integer.parseInt(hiddenTaiKhoanId));
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
