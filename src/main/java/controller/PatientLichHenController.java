package controller;

import exception.ValidationException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Collections;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// Import DTOs
import model.dto.LichHenDTO;
import model.dto.NhanVienDTO;
import model.dto.TaiKhoanDTO;
import model.dto.KhoaDTO;

// Import DAOs/Services
import model.dao.BenhNhanDAO;
import model.Entity.BenhNhan;
import service.LichHenService;
import service.NhanVienService;
import service.KhoaService;

// Import Utilities
import com.google.gson.Gson;

/**
 * Controller MỚI: Chỉ xử lý nghiệp vụ Lịch Hẹn cho BỆNH NHÂN. Cập nhật: Hỗ trợ
 * chọn Khoa/BS, Tìm kiếm, Hủy lịch.
 */
@WebServlet(name = "PatientLichHenController", urlPatterns = {"/PatientLichHenController"})
public class PatientLichHenController extends HttpServlet {

    // Khai báo URL cho các trang JSP
    private static final String PATIENT_BOOKING_PAGE = "DatLichHen.jsp";
    private static final String MY_APPOINTMENTS_PAGE = "LichHenCuaToi.jsp";
    private static final String ERROR_PAGE = "error.jsp";

    // Khởi tạo các Service/DAO cần thiết
    private final LichHenService lichHenService = new LichHenService();
    private final NhanVienService nhanVienService = new NhanVienService();
    private final BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    private final KhoaService khoaService = new KhoaService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        TaiKhoanDTO currentUser = (session != null) ? (TaiKhoanDTO) session.getAttribute("USER") : null;

        if (currentUser == null || !"BENH_NHAN".equals(currentUser.getVaiTro())) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        String url = MY_APPOINTMENTS_PAGE;

        try {
            if (action == null || action.isEmpty()) {
                action = "myAppointments";
            }

            switch (action) {
                case "showPatientBookingForm":
                    url = showPatientBookingForm(request);
                    break;
                case "getBacSiByKhoa":
                    // *** HÀM NÀY ĐÃ ĐƯỢC SỬA ĐỂ KHẮC PHỤC LỖI 500 ***
                    getBacSiByKhoa(request, response);
                    return; // Dừng lại, không forward để hiển thị danh sách bác sĩ trên trang đặt lịch hẹn
                case "myAppointments":
                default:
                    url = viewMyAppointments(request, currentUser);
                    break;
            }
        } catch (Exception e) {
            log("Lỗi tại PatientLichHenController (doGet): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi: " + e.getMessage());
            url = ERROR_PAGE;
        } finally {
            if (!response.isCommitted()) {
                request.getRequestDispatcher(url).forward(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        TaiKhoanDTO currentUser = (session != null) ? (TaiKhoanDTO) session.getAttribute("USER") : null;

        if (currentUser == null || !"BENH_NHAN".equals(currentUser.getVaiTro())) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bạn cần đăng nhập với vai trò Bệnh nhân.");
            return;
        }

        String action = request.getParameter("action");
        String urlRedirect = "PatientLichHenController?action=myAppointments"; // Sửa lại Controller URL

        try {
            if ("bookAppointment".equals(action)) {
                urlRedirect = bookAppointment(request, currentUser);
                response.sendRedirect(urlRedirect);

            } else if ("cancelAppointment".equals(action)) {
                cancelAppointment(request, currentUser);
                urlRedirect += "&cancelSuccess=true";
                response.sendRedirect(urlRedirect);

            } else {
                request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho POST.");
                request.getRequestDispatcher(ERROR_PAGE).forward(request, response);
            }

        } catch (Exception e) {
            log("Lỗi tại PatientLichHenController (doPost): " + e.getMessage(), e);
            String errorPage = ERROR_PAGE;
            request.setAttribute("ERROR_MESSAGE", e.getMessage());

            if ("bookAppointment".equals(action)) {
                errorPage = PATIENT_BOOKING_PAGE;
                try {
                    loadPatientBookingFormDependencies(request);
                } catch (Exception ex) {
                    request.setAttribute("LOAD_FORM_ERROR", "Lỗi tải danh sách Khoa.");
                }
                request.setAttribute("LICHHEN_DATA", createDTOFromRequest(request));
            } else if ("cancelAppointment".equals(action)) {
                errorPage = MY_APPOINTMENTS_PAGE;
                try {
                    viewMyAppointments(request, currentUser);
                } catch (Exception ex) {
                    request.setAttribute("ERROR_MESSAGE", "Lỗi hủy: " + e.getMessage() + ". Lỗi tải lại: " + ex.getMessage());
                }
            }

            request.getRequestDispatcher(errorPage).forward(request, response);
        }
    }

    // --- CÁC HÀM HELPER ĐÃ CẬP NHẬT/THÊM MỚI ---
    
    private void loadPatientBookingFormDependencies(HttpServletRequest request) throws Exception {
        List<KhoaDTO> khoaList = khoaService.getAllKhoa();
        request.setAttribute("khoaList", khoaList);
    }

    /**
     * HÀM SỬA LỖI 500: Đã thêm kiểm tra chuỗi rỗng cho khoaId trước khi parse.
     */
    private void getBacSiByKhoa(HttpServletRequest request, HttpServletResponse response) throws IOException {
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

    private String showPatientBookingForm(HttpServletRequest request) throws Exception {
        loadPatientBookingFormDependencies(request);
        return PATIENT_BOOKING_PAGE;
    }

    private String viewMyAppointments(HttpServletRequest request, TaiKhoanDTO currentUser) throws Exception {
        BenhNhan benhNhan = benhNhanDAO.findByTaiKhoanId(currentUser.getId());
        if (benhNhan == null) {
            request.setAttribute("ERROR_MESSAGE", "Không tìm thấy hồ sơ bệnh nhân liên kết với tài khoản này.");
            return MY_APPOINTMENTS_PAGE;
        }

        String searchKeyword = request.getParameter("searchKeyword");

        // Cần truyền benhNhanId, không phải taiKhoanId
        List<LichHenDTO> lichHenList = lichHenService.getLichHenByBenhNhan(benhNhan.getId(), searchKeyword);
        request.setAttribute("lichHenList", lichHenList);

        return MY_APPOINTMENTS_PAGE;
    }

    private String bookAppointment(HttpServletRequest request, TaiKhoanDTO currentUser)
            throws Exception {

        LichHenDTO dto = createDTOFromRequest(request);
        int taiKhoanIdCuaBenhNhan = currentUser.getId();

        lichHenService.createAppointmentByPatient(dto, taiKhoanIdCuaBenhNhan);

        // Chuyển hướng về chính Controller này để xử lý
        return "MainController?action=myAppointments&bookSuccess=true";
    }

    private void cancelAppointment(HttpServletRequest request, TaiKhoanDTO currentUser) throws Exception {
        // Cần thêm kiểm tra null/empty cho "id"
        String lichHenIdStr = request.getParameter("id");
        if (lichHenIdStr == null || lichHenIdStr.isEmpty()) {
            throw new ValidationException("Không tìm thấy ID lịch hẹn cần hủy.");
        }

        int lichHenId = Integer.parseInt(lichHenIdStr);
        int taiKhoanId = currentUser.getId();

        lichHenService.cancelAppointmentByPatient(lichHenId, taiKhoanId);
    }

    private LichHenDTO createDTOFromRequest(HttpServletRequest request) {
        LichHenDTO dto = new LichHenDTO();

        // Cập nhật: Kiểm tra chuỗi rỗng cho bacSiId
        String bacSiIdStr = request.getParameter("bacSiId");
        if (bacSiIdStr != null && !bacSiIdStr.trim().isEmpty()) {
            try {
                dto.setBacSiId(Integer.parseInt(bacSiIdStr));
            } catch (NumberFormatException e) {
                log("Lỗi parse ID Bác sĩ (Patient Form): " + bacSiIdStr);
                // Để mặc định là 0 hoặc ném lỗi nếu cần validation mạnh
            }
        }

        dto.setLyDoKham(request.getParameter("lyDoKham"));
        dto.setGhiChu(request.getParameter("ghiChu"));
        String thoiGianHenStr = request.getParameter("thoiGianHen");
        if (thoiGianHenStr != null && !thoiGianHenStr.isEmpty()) {
            try {
                LocalDateTime localDateTime = LocalDateTime.parse(thoiGianHenStr);
                // Giả định múi giờ Việt Nam +07
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
        return "Controller (Patient) quản lý các nghiệp vụ Lịch Hẹn của Bệnh nhân.";
    }
}
