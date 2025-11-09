package controller;

import exception.ValidationException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.dto.BenhNhanDTO;
import model.dto.LichHenDTO;
import model.dto.NhanVienDTO;
import model.dto.KhoaDTO; // Thêm import KhoaDTO
import service.BenhNhanService;
import service.LichHenService;
import service.NhanVienService;
import service.KhoaService; // Thêm import KhoaService
import com.google.gson.Gson; // Thêm import Gson
import java.io.PrintWriter; // Thêm import PrintWriter
import java.time.LocalDate;
import java.util.ArrayList; // Thêm import
import java.util.HashMap; // Thêm import
import java.util.Map; // Thêm import

/**
 * Controller xử lý các nghiệp vụ liên quan đến Lịch Hẹn (Appointment). (Đã GỘP:
 * Giữ lại Phân trang, Sửa Admin, và logic của Dat)
 */
@WebServlet(name = "LichHenController", urlPatterns = {"/LichHenController"})
public class LichHenController extends HttpServlet {

    // (Các hằng số JSP)
    private static final String LICHHEN_LIST_PAGE = "admin/danhSachLichHen.jsp";
    private static final String LICHHEN_FORM_PAGE = "admin/formLichHen.jsp";
    private static final String ERROR_PAGE = "error.jsp";
    private static final String DAT_LICH_HEN_PAGE = "lichHenDat.jsp"; // Trang của Dat

    // (Các Service)
    private final LichHenService lichHenService = new LichHenService();
    private final BenhNhanService benhNhanService = new BenhNhanService();
    private final NhanVienService nhanVienService = new NhanVienService();
    private final KhoaService khoaService = new KhoaService(); // Thêm KhoaService
    private final Gson gson = new Gson();
    // Hằng số cho phân trang
    private static final int PAGE_SIZE = 10;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8"); // Đặt lên đầu
        String action = request.getParameter("action");
        String url = ERROR_PAGE;

        try {
            if (action == null || action.isEmpty()) {
                action = "listLichHen";
            }

            switch (action) {
                // --- Logic của Admin ---
                case "listLichHen":
                    url = listLichHen(request);
                    break;
                case "showLichHenCreateForm":
                    loadFormDependencies(request);
                    request.setAttribute("formAction", "createLichHen");
                    url = LICHHEN_FORM_PAGE;
                    break;
                case "showLichHenEditForm":
                    url = showLichHenEditForm(request);
                    break;

                // --- Logic của Dat (Lễ tân) ---
                case "showCreateAppointmentForm": // (Từ code Dat)
                    url = loadAppointmentFormDependencies(request);
                    break;
                case "getDoctorsByKhoa": // (AJAX từ code Dat)
                    handleGetDoctorsByKhoa(request, response);
                    return; // Dừng lại, không forward

                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho GET.");
            }
        } catch (Exception e) {
            log("Lỗi tại LichHenController (doGet): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi: " + e.getMessage());
            url = ERROR_PAGE;
        } finally {
            if (!response.isCommitted()) { // Kiểm tra nếu AJAX đã commit
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
                throw new Exception("Hành động không được chỉ định.");
            }

            // (Xác định trang lỗi cho từng action)
            if ("createLichHen".equals(action) || "updateLichHen".equals(action)) {
                formErrorPage = LICHHEN_FORM_PAGE;
            } else if ("createAppointment".equals(action)) {
                formErrorPage = DAT_LICH_HEN_PAGE; // Trang của Dat
            } else if ("updateLichHenStatus".equals(action)) {
                formErrorPage = LICHHEN_LIST_PAGE;
            }

            switch (action) {
                // --- Logic của Admin ---
                case "createLichHen":
                    url = createLichHen(request);
                    break;
                case "updateLichHen":
                    url = updateLichHen(request);
                    break;
                case "updateLichHenStatus":
                    url = updateLichHenStatus(request);
                    break;

                // --- Logic của Dat (Lễ tân) ---
                case "createAppointment":
                    url = createAppointment(request, response);
                    break;

                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho POST.");
                    url = ERROR_PAGE;
            }

            if (url.startsWith("redirect:")) {
                // SỬA: Đảm bảo redirect đúng ngữ cảnh (ContextPath)
                String redirectUrl = url.substring("redirect:".length());
                if (redirectUrl.startsWith("/")) {
                    response.sendRedirect(request.getContextPath() + redirectUrl);
                } else {
                    response.sendRedirect(redirectUrl);
                }
            } else {
                request.getRequestDispatcher(url).forward(request, response);
            }

        } catch (ValidationException e) {
            log("Lỗi Validation tại LichHenController (doPost): " + e.getMessage());
            handleServiceException(request, e, action);
            request.getRequestDispatcher(formErrorPage).forward(request, response);
        } catch (Exception e) {
            log("Lỗi Hệ thống tại LichHenController (doPost): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã có lỗi hệ thống nghiêm trọng xảy ra: " + e.getMessage());
            request.getRequestDispatcher(ERROR_PAGE).forward(request, response);
        }
    }

    /**
     * (Đã Nâng cấp: Phân trang)
     */
// BÊN TRONG LichHenController.java

    /**
     * (Đã Nâng cấp: Gộp Phân trang VÀ Tìm kiếm)
     */
    private String listLichHen(HttpServletRequest request) throws Exception {
        
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
        List<LichHenDTO> list;
        long totalLichHen;

        // 3. Phân nhánh logic: Có tìm kiếm hay chỉ phân trang?
        if (keyword != null && !keyword.trim().isEmpty()) {
            // --- TRƯỜNG HỢP CÓ TÌM KIẾM ---
            String trimmedKeyword = keyword.trim();
            
            // Gọi service tìm kiếm CÓ PHÂN TRANG
            list = lichHenService.searchLichHenPaginated(trimmedKeyword, page, PAGE_SIZE);
            
            // Gọi service đếm kết quả TÌM KIẾM
            totalLichHen = lichHenService.getLichHenSearchCount(trimmedKeyword);
            
            // Gửi lại keyword về JSP
            request.setAttribute("searchKeyword", keyword); 
            
        } else {
            // --- TRƯỜNG HỢP CHỈ XEM DANH SÁCH (KHÔNG TÌM KIẾM) ---
            
            // Đây là code cũ của bạn
            list = lichHenService.getAllLichHenPaginated(page, PAGE_SIZE);
            totalLichHen = lichHenService.getLichHenCount();
        }

        // 4. Tính toán và gửi thông tin phân trang về JSP
        long totalPages = (long) Math.ceil((double) totalLichHen / PAGE_SIZE);
        
        request.setAttribute("LIST_LICHHEN", list);       // Danh sách để hiển thị
        request.setAttribute("currentPage", page);         // Trang hiện tại
        request.setAttribute("totalPages", totalPages);    // Tổng số trang
        request.setAttribute("totalItems", totalLichHen); // (Tùy chọn) Tổng số mục

        return LICHHEN_LIST_PAGE;
    }

    /**
     * (Đã Nâng cấp: PRG)
     */
    private String createLichHen(HttpServletRequest request) throws ValidationException, Exception {
        LichHenDTO newLichHenDTO = createDTOFromRequest(request);
        LichHenDTO result = lichHenService.createLichHen(newLichHenDTO);
        request.getSession().setAttribute("SUCCESS_MESSAGE", "Tạo lịch hẹn thành công! ID: " + result.getId() + ", STT: " + result.getStt());
        // SỬA: Redirect về action, không phải JSP
        return "redirect:/LichHenController?action=listLichHen";
    }

    /**
     * HÀM MỚI (CRUD SỬA)
     */
    private String showLichHenEditForm(HttpServletRequest request) throws ValidationException, Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            LichHenDTO lichHen = lichHenService.getLichHenById(id);
            request.setAttribute("LICHHEN_DATA", lichHen);
            loadFormDependencies(request);
            request.setAttribute("formAction", "updateLichHen");
            return LICHHEN_FORM_PAGE;
        } catch (NumberFormatException e) {
            throw new ValidationException("ID Lịch hẹn không hợp lệ.");
        }
    }

    /**
     * HÀM MỚI (CRUD SỬA)
     */
    private String updateLichHen(HttpServletRequest request) throws ValidationException, Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            LichHenDTO dto = createDTOFromRequest(request);
            LichHenDTO result = lichHenService.updateLichHen(id, dto);
            request.getSession().setAttribute("SUCCESS_MESSAGE", "Cập nhật lịch hẹn ID " + result.getId() + " thành công!");
            return "redirect:/LichHenController?action=listLichHen";
        } catch (NumberFormatException e) {
            throw new ValidationException("ID Lịch hẹn không hợp lệ khi cập nhật.");
        }
    }

    /**
     * (Đã Nâng cấp: PRG)
     */
    private String updateLichHenStatus(HttpServletRequest request) throws Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String newTrangThai = request.getParameter("trangThai");
            String ghiChu = request.getParameter("ghiChu");
            LichHenDTO result = lichHenService.updateTrangThaiLichHen(id, newTrangThai, ghiChu);
            request.getSession().setAttribute("SUCCESS_MESSAGE", "Cập nhật trạng thái lịch hẹn ID " + result.getId() + " thành công!");
            return "redirect:/LichHenController?action=listLichHen";
        } catch (NumberFormatException e) {
            throw new ValidationException("ID Lịch hẹn không hợp lệ.");
        }
    }

    /**
     * SỬA: Tải danh sách đã lọc (Xóa Mềm) (Hàm này dùng chung cho cả form Admin
     * và form Dat)
     */
    private void loadFormDependencies(HttpServletRequest request) {
        try {
            // Service đã được nâng cấp để gọi DAO đã lọc Xóa Mềm
            List<BenhNhanDTO> listBenhNhan = benhNhanService.getAllBenhNhan(); // Giả sử getAllBenhNhan đã lọc
            List<NhanVienDTO> listBacSi = nhanVienService.findDoctorsBySpecialty();
            List<KhoaDTO> listKhoa = khoaService.getAllKhoa();

            request.setAttribute("LIST_BENHNHAN", listBenhNhan);
            request.setAttribute("LIST_BACSI", listBacSi);
            request.setAttribute("LIST_KHOA", listKhoa); // Thêm Khoa cho form ...Dat...

        } catch (Exception e) {
            log("Không thể tải danh sách Bệnh nhân/Bác sĩ cho form Lịch hẹn: " + e.getMessage(), e);
            request.setAttribute("LOAD_FORM_ERROR", "Lỗi tải danh sách Bệnh nhân/Bác sĩ.");
        }
    }

    /**
     * SỬA: Thêm logic cho 'createAppointment' (của Dat)
     */
    private void handleServiceException(HttpServletRequest request, Exception e, String formAction) {
        request.setAttribute("ERROR_MESSAGE", e.getMessage());
        if ("createLichHen".equals(formAction) || "updateLichHen".equals(formAction)) {
            request.setAttribute("LICHHEN_DATA", createDTOFromRequest(request));
            request.setAttribute("formAction", formAction);
            loadFormDependencies(request);
        }
        if ("createAppointment".equals(formAction)) {
            request.setAttribute("LICHHEN_DATA", createDTOFromRequest(request));
            try {
                // Tải lại dropdowns cho form ...Dat...
                loadAppointmentFormDependencies(request);
            } catch (Exception ex) {
                log("Lỗi khi tải lại form ...Dat...: " + ex.getMessage(), ex);
            }
        }
    }

    /**
     * Hàm tiện ích tạo DTO (Giữ phiên bản File 2, an toàn hơn)
     */
    private LichHenDTO createDTOFromRequest(HttpServletRequest request) {
        LichHenDTO dto = new LichHenDTO();
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                dto.setId(Integer.parseInt(idStr));
            } catch (NumberFormatException e) {
                /* ignore */ }
        }
        try {
            String bnId = request.getParameter("benhNhanId");
            String bsId = request.getParameter("bacSiId");
            if (bnId != null && !bnId.isEmpty()) {
                dto.setBenhNhanId(Integer.parseInt(bnId));
            }
            if (bsId != null && !bsId.isEmpty()) {
                dto.setBacSiId(Integer.parseInt(bsId));
            }
        } catch (NumberFormatException e) {
            log("Lỗi parse ID Bệnh nhân/Bác sĩ");
        }
        dto.setLyDoKham(request.getParameter("lyDoKham"));
        dto.setGhiChu(request.getParameter("ghiChu"));
        String thoiGianHenStr = request.getParameter("thoiGianHen");
        if (thoiGianHenStr != null && !thoiGianHenStr.isEmpty()) {
            try {
                LocalDateTime localDateTime = LocalDateTime.parse(thoiGianHenStr);
                ZoneOffset offset = ZoneOffset.ofHours(7);
                dto.setThoiGianHen(OffsetDateTime.of(localDateTime, offset));
            } catch (DateTimeParseException e) {
                log("Lỗi parse OffsetDateTime từ chuỗi '" + thoiGianHenStr + "': " + e.getMessage());
            }
        }
        return dto;
    }

    //====================================================Dat=================================================
    // (Toàn bộ khối 'Dat' được giữ lại và điều chỉnh để tương thích với
    // logic gộp của File 2)
    /**
     * Xử lý logic tạo mới Lịch hẹn (cho Lễ tân). (Sửa đổi từ File 2, ném lỗi
     * lên cho doPost xử lý)
     */
    private String createAppointment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, Exception { // Thêm Exception

        LichHenDTO dto = new LichHenDTO();
        // Sửa: Trỏ về Action thay vì JSP
        String redirectUrl = "redirect:/LichHenController?action=listLichHen";

        try {
            dto.setBenhNhanId(Integer.parseInt(request.getParameter("benhNhanId")));
            dto.setBacSiId(Integer.parseInt(request.getParameter("bacSiId")));
            String thoiGianHenStr = request.getParameter("thoiGianHen");
            if (thoiGianHenStr == null || thoiGianHenStr.isEmpty()) {
                throw new ValidationException("Vui lòng chọn thời gian hẹn.");
            }
            LocalDateTime ldt = LocalDateTime.parse(thoiGianHenStr);
            OffsetDateTime odt = ldt.atOffset(ZoneOffset.of("+07:00"));
            dto.setThoiGianHen(odt);
            dto.setLyDoKham(request.getParameter("lyDoKham"));
            dto.setGhiChu(request.getParameter("ghiChu"));

            lichHenService.createAppointmentByNurse(dto);

            request.getSession().setAttribute("SUCCESS_MESSAGE", "Tạo lịch hẹn thành công!");
            return redirectUrl;

        } catch (ValidationException | DateTimeParseException e) {
            // Ném lại lỗi để doPost bắt
            throw new ValidationException(e.getMessage());
        }
    }

    /**
     * Tải dữ liệu cho form Đặt Lịch Hẹn (của Dat). (Sửa đổi từ File 2, dùng hàm
     * lọc Xóa Mềm)
     */
    private String loadAppointmentFormDependencies(HttpServletRequest request) {
        try {
            // SỬA: Gọi hàm mới đã lọc Xóa Mềm
            List<BenhNhanDTO> danhSachBenhNhan = benhNhanService.getAllBenhNhan(); // Giả sử đã lọc
            List<KhoaDTO> danhSachKhoa = khoaService.getAllKhoa(); // DAO đã lọc

            request.setAttribute("danhSachBenhNhan", danhSachBenhNhan);
            request.setAttribute("danhSachKhoa", danhSachKhoa);
            return DAT_LICH_HEN_PAGE; // Sửa: Dùng hằng số
        } catch (Exception e) {
            log("Không thể tải dữ liệu cho form tạo lịch hẹn: " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Không thể tải danh sách bệnh nhân và bác sĩ.");
            return "error.jsp";
        }
    }

    /**
     * Xử lý AJAX lấy Bác sĩ theo Khoa (của Dat). (SỬA: Gọi hàm
     * getDoctorsByKhoaId theo đúng yêu cầu "giữ code Dat")
     */
    protected void handleGetDoctorsByKhoa(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter(); // Khai báo sớm

        try {
            String khoaIdParam = request.getParameter("khoaId");

            if (khoaIdParam == null || khoaIdParam.trim().isEmpty()) {
                out.write("[]"); // Trả về mảng rỗng
                return;
            }

            int khoaId = Integer.parseInt(khoaIdParam);

            // === SỬA THEO YÊU CẦU: Giữ lại hàm gốc của Dat từ File 1 ===
            List<NhanVienDTO> danhSachBacSi = nhanVienService.getDoctorsByKhoaId(khoaId);
            // === KẾT THÚC SỬA ===

            // (Logic JSON của bạn đã tốt, giữ nguyên)
            List<Map<String, Object>> bacSiJsonList = new ArrayList<>();
            for (NhanVienDTO bacSi : danhSachBacSi) {
                Map<String, Object> bacSiMap = new HashMap<>();
                bacSiMap.put("id", bacSi.getId());
                bacSiMap.put("hoTen", bacSi.getHoTen());
                bacSiMap.put("chuyenMon", bacSi.getChuyenMon() != null ? bacSi.getChuyenMon() : "");
                bacSiJsonList.add(bacSiMap);
            }

            // Chuyển thành JSON
            String json = new Gson().toJson(bacSiJsonList);
            out.write(json);

        } catch (NumberFormatException e) {
            log("Lỗi định dạng khoaId: " + e.getMessage());
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID Khoa không hợp lệ.");
        } catch (Exception e) {
            log("Lỗi khi lấy danh sách bác sĩ: " + e.getMessage(), e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Lỗi máy chủ khi tải bác sĩ.");
        } finally {
            out.flush();
        }
    }

    @Override
    public String getServletInfo() {
        return "Controller quản lý các nghiệp vụ liên quan đến Lịch Hẹn.";
    }
}
