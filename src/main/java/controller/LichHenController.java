package controller;

import exception.ValidationException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.List; // BỎ import 'Arrays' nếu không dùng
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.dto.BenhNhanDTO;
import model.dto.KhoaDTO;
import model.dto.LichHenDTO;

import service.BenhNhanService;
import service.KhoaService;
import service.LichHenService;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;
import model.dto.NhanVienDTO;
import service.NhanVienService;
import com.google.gson.Gson;

/**
 * Controller xử lý các nghiệp vụ liên quan đến Lịch Hẹn (Appointment). (ĐÃ NÂNG
 * CẤP: Sửa lỗi gọi hàm BenhNhanService)
 */
@WebServlet(name = "LichHenController", urlPatterns = {"/LichHenController"})
public class LichHenController extends HttpServlet {

    // (Các hằng số giữ nguyên)
    private static final String LICHHEN_LIST_PAGE = "admin/danhSachLichHen.jsp";
    private static final String LICHHEN_FORM_PAGE = "admin/formLichHen.jsp";
    private static final String ERROR_PAGE = "error.jsp";

    // (Các Service giữ nguyên)
    private final LichHenService lichHenService = new LichHenService();
    private final BenhNhanService benhNhanService = new BenhNhanService();
    private final NhanVienService nhanVienService = new NhanVienService();
    private final KhoaService khoaService = new KhoaService();
    private final Gson gson = new Gson();

    // Hằng số cho phân trang
    private static final int PAGE_SIZE = 10; // 10 lịch hẹn mỗi trang

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // (Hàm doGet giữ nguyên y hệt, không thay đổi)
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        String url = LICHHEN_LIST_PAGE;


        try {
            if (action == null || action.isEmpty()) {
                action = "listLichHen"; // Đặt action mặc định
            }
            switch (action) {
                case "getDoctorsByKhoa":
                    handleGetDoctorsByKhoa(request, response);
                    return; // Đã xử lý, không forward
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
                case "showCreateAppointmentForm":
                    url = loadAppointmentFormDependencies(request);
                    break;
                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho GET.");

            }

            request.getRequestDispatcher(url).forward(request, response);

        } catch (Exception e) {
            log("Lỗi tại LichHenController (doGet): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi: " + e.getMessage());
            // Forward đến trang lỗi khi có exception
            request.getRequestDispatcher(ERROR_PAGE).forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // (Hàm doPost giữ nguyên y hệt, không thay đổi)
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        String url = ERROR_PAGE;
        String formErrorPage = ERROR_PAGE;
        try {
            if (action == null || action.isEmpty()) {
                throw new Exception("Hành động không được chỉ định.");
            }
            if ("createLichHen".equals(action) || "updateLichHen".equals(action)) {
                formErrorPage = LICHHEN_FORM_PAGE;
            } else if ("createAppointment".equals(action)) {
                formErrorPage = "lichHenDat.jsp";
            } else if ("updateLichHenStatus".equals(action)) {
                formErrorPage = LICHHEN_LIST_PAGE;
            }
            switch (action) {
                case "createLichHen":
                    url = createLichHen(request);
                    break;
                case "updateLichHen":
                    url = updateLichHen(request);
                    break;
                case "updateLichHenStatus":
                    url = updateLichHenStatus(request);
                    break;
                case "createAppointment":
                    url = createAppointment(request, response);
                    break;
                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho POST.");
                    url = ERROR_PAGE;
            }
            if (url.startsWith("redirect:")) {
                String redirectUrl = url.substring("redirect:".length());
                response.sendRedirect(request.getContextPath() + redirectUrl);
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
     * (Hàm listLichHen giữ nguyên y hệt, không thay đổi)
     */
    private String listLichHen(HttpServletRequest request) throws Exception {
        int page = 1;
        String pageStr = request.getParameter("page");
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        List<LichHenDTO> list = lichHenService.getAllLichHenPaginated(page, PAGE_SIZE);
        long totalLichHen = lichHenService.getLichHenCount();
        long totalPages = (long) Math.ceil((double) totalLichHen / PAGE_SIZE);
        request.setAttribute("LIST_LICHHEN", list);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);
        return LICHHEN_LIST_PAGE;
    }

    /**
     * (Hàm createLichHen giữ nguyên y hệt, không thay đổi)
     */
    private String createLichHen(HttpServletRequest request) throws ValidationException, Exception {
        LichHenDTO newLichHenDTO = createDTOFromRequest(request);
        LichHenDTO result = lichHenService.createLichHen(newLichHenDTO);
        request.getSession().setAttribute("SUCCESS_MESSAGE", "Tạo lịch hẹn thành công! ID: " + result.getId() + ", STT: " + result.getStt());
        return "redirect:MainController?action=listLichHen";
    }

    /**
     * (Hàm showLichHenEditForm giữ nguyên y hệt, không thay đổi)
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
     * (Hàm updateLichHen giữ nguyên y hệt, không thay đổi)
     */
    private String updateLichHen(HttpServletRequest request) throws ValidationException, Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            LichHenDTO dto = createDTOFromRequest(request);
            LichHenDTO result = lichHenService.updateLichHen(id, dto);
            request.getSession().setAttribute("SUCCESS_MESSAGE", "Cập nhật lịch hẹn ID " + result.getId() + " thành công!");
            return "redirect:MainController?action=listLichHen";
        } catch (NumberFormatException e) {
            throw new ValidationException("ID Lịch hẹn không hợp lệ khi cập nhật.");
        }
    }

    /**
     * (Hàm updateLichHenStatus giữ nguyên y hệt, không thay đổi)
     */
    private String updateLichHenStatus(HttpServletRequest request) throws Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String newTrangThai = request.getParameter("trangThai");
            String ghiChu = request.getParameter("ghiChu");
            LichHenDTO result = lichHenService.updateTrangThaiLichHen(id, newTrangThai, ghiChu);
            request.getSession().setAttribute("SUCCESS_MESSAGE", "Cập nhật trạng thái lịch hẹn ID " + result.getId() + " thành công!");
            return "redirect:MainController?action=listLichHen";
        } catch (NumberFormatException e) {
            throw new ValidationException("ID Lịch hẹn không hợp lệ.");
        }
    }

    /**
     * === BẮT ĐẦU SỬA (GỌI ĐÚNG HÀM SERVICE) === Tải danh sách Bệnh nhân và Bác
     * sĩ (đang hoạt động) cho form.
     */
    private void loadFormDependencies(HttpServletRequest request) {
        try {
            // Sửa: Gọi hàm 'getAllActiveBenhNhan()' mới (dùng cho dropdown)
            List<BenhNhanDTO> listBenhNhan = benhNhanService.getAllActiveBenhNhan();
            List<NhanVienDTO> listBacSi = nhanVienService.findDoctorsBySpecialty(); // Hàm này đã đúng

            request.setAttribute("LIST_BENHNHAN", listBenhNhan);
            request.setAttribute("LIST_BACSI", listBacSi);

        } catch (Exception e) {
            log("Không thể tải danh sách Bệnh nhân/Bác sĩ cho form Lịch hẹn: " + e.getMessage(), e);
            request.setAttribute("LOAD_FORM_ERROR", "Lỗi tải danh sách Bệnh nhân/Bác sĩ.");
        }
    }
    // === KẾT THÚC SỬA ===

    /**
     * (Hàm handleServiceException giữ nguyên y hệt, không thay đổi)
     */
    private void handleServiceException(HttpServletRequest request, Exception e, String formAction) {
        request.setAttribute("ERROR_MESSAGE", e.getMessage());
        if ("createLichHen".equals(formAction) || "updateLichHen".equals(formAction)) {
            request.setAttribute("LICHHEN_DATA", createDTOFromRequest(request));
            request.setAttribute("formAction", formAction);
            loadFormDependencies(request);
        }
    }

    /**
     * (Hàm createDTOFromRequest giữ nguyên y hệt, không thay đổi)
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
            dto.setBenhNhanId(Integer.parseInt(request.getParameter("benhNhanId")));
            dto.setBacSiId(Integer.parseInt(request.getParameter("bacSiId")));
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

    // (Các hàm ...Dat... giữ nguyên)
    /**
     * === BẮT ĐẦU SỬA (GỌI ĐÚNG HÀM SERVICE) === (Hàm này là của ...Dat...)
     */
    private String createAppointment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        LichHenDTO dto = new LichHenDTO();
        // Sửa: redirectUrl nên trỏ về action của MainController
        String redirectUrl = "redirect:/MainController?action=listLichHen"; // Sửa lại đường dẫn
        try {
            dto.setBenhNhanId(Integer.parseInt(request.getParameter("benhNhanId")));
            dto.setBacSiId(Integer.parseInt(request.getParameter("bacSiId")));
            String thoiGianHenStr = request.getParameter("thoiGianHen");
            LocalDateTime ldt = LocalDateTime.parse(thoiGianHenStr);
            OffsetDateTime odt = ldt.atOffset(ZoneOffset.of("+07:00"));
            dto.setThoiGianHen(odt);
            dto.setLyDoKham(request.getParameter("lyDoKham"));
            dto.setGhiChu(request.getParameter("ghiChu"));
            lichHenService.createAppointmentByNurse(dto); // Hàm này của bạn đã đúng
            request.getSession().setAttribute("SUCCESS_MESSAGE", "Tạo lịch hẹn thành công!");
            return redirectUrl;
        } catch (ValidationException | DateTimeParseException e) {
            request.setAttribute("ERROR_MESSAGE", e.getMessage());
            loadAppointmentFormDependencies(request); // Tải lại dropdowns
            return "lichHenDat.jsp"; // Quay về form ...Dat...
        } catch (Exception e) {
            // Thêm log
            log("Lỗi nghiêm trọng khi tạo lịch hẹn (Dat): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Lỗi hệ thống: " + e.getMessage());
            loadAppointmentFormDependencies(request);
            return "lichHenDat.jsp";
        }
    }

    /**
     * === BẮT ĐẦU SỬA (GỌI ĐÚNG HÀM SERVICE) === (Hàm này là của ...Dat...)
     */
    private String loadAppointmentFormDependencies(HttpServletRequest request) {
        try {
            // Sửa: Gọi hàm 'getAllActiveBenhNhan()' mới (dùng cho dropdown)
            List<BenhNhanDTO> danhSachBenhNhan = benhNhanService.getAllActiveBenhNhan();
            List<NhanVienDTO> danhSachBacSi = nhanVienService.findDoctorsBySpecialty();
            request.setAttribute("danhSachBenhNhan", danhSachBenhNhan);
            request.setAttribute("danhSachBacSi", danhSachBacSi);

            // Lấy danh sách Khoa, KHÔNG lấy danh sách Bác sĩ
            List<KhoaDTO> danhSachKhoa = khoaService.getAllKhoa();

            request.setAttribute("danhSachKhoa", danhSachKhoa); // Gửi danh sách Khoa

            return "lichHenDat.jsp";
        } catch (Exception e) {
            log("Không thể tải dữ liệu cho form tạo lịch hẹn (Dat): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Không thể tải danh sách bệnh nhân và bác sĩ.");
            return "error.jsp"; // Sửa: nên về trang lỗi chung
        }
    }
    // === KẾT THÚC SỬA ===

    protected void handleGetDoctorsByKhoa(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        String khoaIdStr = request.getParameter("khoaId");

        // === KHẮC PHỤC LỖI 500: Kiểm tra chuỗi rỗng/null trước khi parse ===
        if (khoaIdStr == null || khoaIdStr.trim().isEmpty()) {
            // Trả về mảng rỗng nếu không có ID khoa
            out.print(gson.toJson(Collections.emptyList()));
            out.flush();
            return;
        }

        try {
            int khoaId = Integer.parseInt(khoaIdStr);
            List<NhanVienDTO> bacSiList = nhanVienService.getBacSiByKhoa(khoaId);
            out.print(gson.toJson(bacSiList));
        } catch (NumberFormatException e) {
            // Trường hợp lỗi parse (ví dụ: gửi "abc" thay vì số)
            log("Lỗi NumberFormatException khi tải bác sĩ theo khoaId: " + khoaIdStr, e);
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); // 400 Bad Request
            out.print(gson.toJson(Collections.singletonMap("error", "ID khoa không hợp lệ.")));
        } catch (Exception e) {
            // Lỗi DB hoặc Service
            log("Lỗi tải danh sách bác sĩ tại Service: " + e.getMessage(), e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Collections.singletonMap("error", "Lỗi tải danh sách bác sĩ: " + e.getMessage())));
        }
        out.flush();
    }

    @Override
    public String getServletInfo() {
        return "Controller quản lý các nghiệp vụ liên quan đến Lịch Hẹn.";
    }
}
