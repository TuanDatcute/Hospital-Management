package controller;

import exception.ValidationException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
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
    private static final String ERROR_PAGE = "error.jsp";

    // (Các Service giữ nguyên)
    private final LichHenService lichHenService = new LichHenService();
    private final BenhNhanService benhNhanService = new BenhNhanService();
    private final NhanVienService nhanVienService = new NhanVienService();
    private final KhoaService khoaService = new KhoaService();
    private static final String LICHHEN_FORM_PAGE = "admin/formLichHen.jsp";
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

   
    @Override

    public String getServletInfo() {
        return "Controller quản lý các nghiệp vụ liên quan đến Lịch Hẹn.";
    }
}
