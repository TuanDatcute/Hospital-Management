package controller;

import exception.ValidationException;
import java.io.IOException;
import java.time.LocalDateTime; // Cần import LocalDateTime
import java.time.OffsetDateTime;
import java.time.ZoneOffset; // Cần import ZoneOffset
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.dto.BenhNhanDTO;
import model.dto.KhoaDTO;
import model.dto.LichHenDTO;
import model.dto.NhanVienDTO;
import service.BenhNhanService;
import service.KhoaService;
import service.LichHenService;
import service.NhanVienService;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import model.dto.NhanVienDTO;
import service.NhanVienService;

/**
 * Controller xử lý các nghiệp vụ liên quan đến Lịch Hẹn (Appointment).
 */
@WebServlet(name = "LichHenController", urlPatterns = {"/LichHenController"})
public class LichHenController extends HttpServlet {

    // Khai báo URL cho các trang JSP
    private static final String LICHHEN_LIST_PAGE = "admin/danhSachLichHen.jsp";
    private static final String LICHHEN_FORM_PAGE = "admin/formLichHen.jsp";
    private static final String ERROR_PAGE = "error.jsp";

    // Khởi tạo các Service cần thiết
    private final LichHenService lichHenService = new LichHenService();
    private final BenhNhanService benhNhanService = new BenhNhanService();
    private final NhanVienService nhanVienService = new NhanVienService();
    private final KhoaService khoaService = new KhoaService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        // Các yêu cầu này sẽ tự xử lý response và kết thúc (return)
        String url = ERROR_PAGE; // Đặt URL lỗi làm mặc định
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
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String url = ERROR_PAGE;
        boolean loadListAfterSuccess = true;

        try {
            if (action == null || action.isEmpty()) {
                throw new Exception("Hành động không được chỉ định.");
            }

            switch (action) {
                case "createLichHen":
                    url = createLichHen(request);
                    break;
                case "updateLichHenStatus":
                    url = updateLichHenStatus(request);
                    break;
                case "createAppointment":
                    url = createAppointment(request, response);
                    break;
                default:
                    loadListAfterSuccess = false;
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho POST.");
            }

            if (url.startsWith("redirect:")) {
                String redirectUrl = url.substring("redirect:".length());
                response.sendRedirect(request.getContextPath() + redirectUrl);
            } else {
                request.getRequestDispatcher(url).forward(request, response);
            }

        } catch (Exception e) {
            log("Lỗi tại LichHenController (doPost): " + e.getMessage(), e);
            handleServiceException(request, e, action);
            request.setAttribute("ERROR_MESSAGE", "Đã có lỗi nghiêm trọng xảy ra: " + e.getMessage());
            request.getRequestDispatcher(ERROR_PAGE).forward(request, response);

        }
    }

    /**
     * Lấy danh sách Lịch hẹn và chuyển đến trang hiển thị.
     */
    private String listLichHen(HttpServletRequest request) throws Exception {
        List<LichHenDTO> list = lichHenService.getAllLichHen();
        request.setAttribute("LIST_LICHHEN", list);
        return LICHHEN_LIST_PAGE;
    }

    /**
     * Xử lý logic tạo mới một Lịch hẹn.
     */
    private String createLichHen(HttpServletRequest request) throws Exception {
        LichHenDTO newLichHenDTO = createDTOFromRequest(request);
        LichHenDTO result = lichHenService.createLichHen(newLichHenDTO);
        request.setAttribute("SUCCESS_MESSAGE", "Tạo lịch hẹn thành công! ID: " + result.getId() + ", STT: " + result.getStt());
        return LICHHEN_LIST_PAGE;
    }

    /**
     * Xử lý logic cập nhật trạng thái một Lịch hẹn.
     */
    private String updateLichHenStatus(HttpServletRequest request) throws Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String newTrangThai = request.getParameter("trangThai");
            String ghiChu = request.getParameter("ghiChu");

            LichHenDTO result = lichHenService.updateTrangThaiLichHen(id, newTrangThai, ghiChu);
            request.setAttribute("SUCCESS_MESSAGE", "Cập nhật trạng thái lịch hẹn ID " + result.getId() + " thành công!");
            return LICHHEN_LIST_PAGE;
        } catch (NumberFormatException e) {
            request.setAttribute("ERROR_MESSAGE", "ID Lịch hẹn không hợp lệ.");
            return ERROR_PAGE;
        }
    }

    /**
     * Tải danh sách Bệnh nhân và Bác sĩ (đang hoạt động) cho form.
     */
    private void loadFormDependencies(HttpServletRequest request) {
        try {
            List<BenhNhanDTO> listBenhNhan = benhNhanService.getAllBenhNhan();
            List<NhanVienDTO> listBacSi = nhanVienService.findDoctorsBySpecialty();

            request.setAttribute("LIST_BENHNHAN", listBenhNhan);
            request.setAttribute("LIST_BACSI", listBacSi);

        } catch (Exception e) {
            log("Không thể tải danh sách Bệnh nhân/Bác sĩ cho form Lịch hẹn: " + e.getMessage(), e);
            request.setAttribute("LOAD_FORM_ERROR", "Lỗi tải danh sách Bệnh nhân/Bác sĩ.");
        }
    }

    /**
     * Xử lý lỗi từ Service và gửi lại form (chủ yếu cho action tạo).
     */
    private void handleServiceException(HttpServletRequest request, Exception e, String formAction) {
        request.setAttribute("ERROR_MESSAGE", e.getMessage());
        if ("createLichHen".equals(formAction)) {
            request.setAttribute("LICHHEN_DATA", createDTOFromRequest(request));
            request.setAttribute("formAction", formAction);
            loadFormDependencies(request);
        }
    }

    /**
     * Hàm tiện ích tạo LichHenDTO từ request.
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

        // --- PHẦN SỬA LỖI OffsetDateTime ---
        String thoiGianHenStr = request.getParameter("thoiGianHen"); // Input dạng yyyy-MM-ddTHH:mm
        if (thoiGianHenStr != null && !thoiGianHenStr.isEmpty()) {
            try {
                // 1. Parse chuỗi từ input thành LocalDateTime
                LocalDateTime localDateTime = LocalDateTime.parse(thoiGianHenStr);

                // 2. Xác định Offset (ví dụ: +07:00 cho Việt Nam)
                // Có thể lấy động dựa trên cài đặt server hoặc múi giờ người dùng nếu phức tạp hơn
                ZoneOffset offset = ZoneOffset.ofHours(7);

                // 3. Tạo OffsetDateTime từ LocalDateTime và Offset
                dto.setThoiGianHen(OffsetDateTime.of(localDateTime, offset));

            } catch (DateTimeParseException e) {
                log("Lỗi parse OffsetDateTime từ chuỗi '" + thoiGianHenStr + "': " + e.getMessage());
                // Có thể set attribute báo lỗi nếu cần
            }
        }
        // --- KẾT THÚC SỬA ---

        return dto;
    }

    //====================================================Dat=================================================
    private String createAppointment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        LichHenDTO dto = new LichHenDTO();
        String redirectUrl = "redirect:/MainController?action=admin/danhSachLichHen.jsp"; // URL nếu thành công

        try {
            // 1. Lấy dữ liệu từ form
            dto.setBenhNhanId(Integer.parseInt(request.getParameter("benhNhanId")));
            dto.setBacSiId(Integer.parseInt(request.getParameter("bacSiId")));

            // Chuyển đổi chuỗi "yyyy-MM-ddTHH:mm" từ input datetime-local
            String thoiGianHenStr = request.getParameter("thoiGianHen");
            LocalDateTime ldt = LocalDateTime.parse(thoiGianHenStr);
            // Chuyển sang OffsetDateTime (giả sử múi giờ +7)
            OffsetDateTime odt = ldt.atOffset(ZoneOffset.of("+07:00"));
            dto.setThoiGianHen(odt);

            dto.setLyDoKham(request.getParameter("lyDoKham"));
            dto.setGhiChu(request.getParameter("ghiChu"));

            // 2. Gọi Service để thực hiện nghiệp vụ
            lichHenService.createAppointmentByNurse(dto);

            // 3. Xử lý thành công
            request.getSession().setAttribute("SUCCESS_MESSAGE", "Tạo lịch hẹn thành công!");
            return redirectUrl;

        } catch (ValidationException | DateTimeParseException e) {
            // 4. Xử lý lỗi nghiệp vụ hoặc lỗi định dạng
            request.setAttribute("ERROR_MESSAGE", e.getMessage());
            // Tải lại các dropdown nếu cần
            loadAppointmentFormDependencies(request);
            return "lichHenDat.jsp";
        } catch (Exception e) {
            return "lichHenDat.jsp";
        }
    }

    private String loadAppointmentFormDependencies(HttpServletRequest request) {
        try {
            List<BenhNhanDTO> danhSachBenhNhan = benhNhanService.getAllBenhNhan();
            // Lấy danh sách Khoa, KHÔNG lấy danh sách Bác sĩ
            List<KhoaDTO> danhSachKhoa = khoaService.getAllKhoa();

            request.setAttribute("danhSachBenhNhan", danhSachBenhNhan);
            request.setAttribute("danhSachKhoa", danhSachKhoa); // Gửi danh sách Khoa
            return "lichHenDat.jsp";
        } catch (Exception e) {
            log("Không thể tải dữ liệu cho form tạo lịch hẹn: " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Không thể tải danh sách bệnh nhân và bác sĩ.");
            return "error.jsp";
        }
    }

    protected void handleGetDoctorsByKhoa(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");

        try {
            String khoaIdParam = request.getParameter("khoaId");

            if (khoaIdParam == null || khoaIdParam.trim().isEmpty()) {
                response.getWriter().write("[]"); // Trả về mảng rỗng
                return;
            }

            int khoaId = Integer.parseInt(khoaIdParam);

            // Gọi service để lấy danh sách bác sĩ
            List<NhanVienDTO> danhSachBacSi = nhanVienService.getDoctorsByKhoaId(khoaId);

            // Tạo danh sách JSON
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
            response.getWriter().write(json);

        } catch (NumberFormatException e) {
            log("Lỗi định dạng khoaId: " + e.getMessage());
            response.getWriter().write("[]"); // Trả về mảng rỗng khi lỗi
        } catch (Exception e) {
            log("Lỗi khi lấy danh sách bác sĩ: " + e.getMessage(), e);
            response.getWriter().write("[]"); // Trả về mảng rỗng khi lỗi
        }
    }

    @Override
    public String getServletInfo() {
        return "Controller quản lý các nghiệp vụ liên quan đến Lịch Hẹn.";
    }
}
