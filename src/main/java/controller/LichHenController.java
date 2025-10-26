package controller;

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
import model.dto.LichHenDTO;
import model.dto.NhanVienDTO;
import service.BenhNhanService;
import service.LichHenService;
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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String url = LICHHEN_LIST_PAGE;

        try {
            if (action == null || action.isEmpty()) {
                action = "listLichHen";
            }

            switch (action) {
                case "listLichHen":
                    url = listLichHen(request);
                    break;
                case "showCreateForm":
                    loadFormDependencies(request);
                    request.setAttribute("formAction", "createLichHen");
                    url = LICHHEN_FORM_PAGE;
                    break;
                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho GET.");
                    url = ERROR_PAGE;
            }
        } catch (Exception e) {
            log("Lỗi tại LichHenController (doGet): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi: " + e.getMessage());
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
                default:
                    loadListAfterSuccess = false;
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho POST.");
            }
            if (loadListAfterSuccess && !url.equals(ERROR_PAGE) && !url.equals(LICHHEN_FORM_PAGE)) {
                 url = listLichHen(request);
            }

        } catch (Exception e) {
            log("Lỗi tại LichHenController (doPost): " + e.getMessage(), e);
            handleServiceException(request, e, action);
            url = LICHHEN_FORM_PAGE;
            loadListAfterSuccess = false;

        } finally {
            request.getRequestDispatcher(url).forward(request, response);
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
         if(idStr != null && !idStr.isEmpty()){
             try { dto.setId(Integer.parseInt(idStr)); } catch (NumberFormatException e) { /* ignore */ }
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

    @Override
    public String getServletInfo() {
        return "Controller quản lý các nghiệp vụ liên quan đến Lịch Hẹn.";
    }
}