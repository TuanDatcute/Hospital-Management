package controller;

import exception.ValidationException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.HashMap; // THÊM MỚI
import java.util.Map;     // THÊM MỚI
import java.io.PrintWriter; // THÊM MỚI
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

// Import DTOs
import model.dto.BenhNhanDTO; // (Không cần thiết nếu form bệnh nhân không có)
import model.dto.LichHenDTO;
import model.dto.NhanVienDTO;
import model.dto.TaiKhoanDTO;
import model.dto.KhoaDTO; // THÊM MỚI

// Import DAOs/Services
import model.dao.BenhNhanDAO;
import model.Entity.BenhNhan;
import service.LichHenService;
import service.NhanVienService;
import service.KhoaService; // THÊM MỚI

// Import Utilities
import com.google.gson.Gson; // THÊM MỚI
import java.util.Collections;

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

    // === THÊM MỚI ===
    private final KhoaService khoaService = new KhoaService(); // Cần cho form
    private final Gson gson = new Gson(); // Cần cho AJAX
    // =================

    /**
     * CẬP NHẬT: Thêm action 'getBacSiByKhoa' (AJAX) CẬP NHẬT: 'myAppointments'
     * giờ đã bao gồm tìm kiếm CẬP NHẬT: 'showPatientBookingForm' giờ tải Khoa
     * (thay vì Bác sĩ)
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        // SỬA: Dùng "USER" theo code của bạn
        TaiKhoanDTO currentUser = (session != null) ? (TaiKhoanDTO) session.getAttribute("USER") : null;

        if (currentUser == null || !"BENH_NHAN".equals(currentUser.getVaiTro())) {
            response.sendRedirect("login.jsp");
            return;
        }

        String action = request.getParameter("action");
        String url = MY_APPOINTMENTS_PAGE; // Mặc định

        try {
            if (action == null || action.isEmpty()) {
                action = "myAppointments";
            }

            switch (action) {
                case "showPatientBookingForm":
                    url = showPatientBookingForm(request);
                    break;
                // === THÊM MỚI: Action cho AJAX ===
                case "getBacSiByKhoa":
                    getBacSiByKhoa(request, response); // Hàm này tự trả JSON
                    return; // Dừng lại, không forward
                // ==============================
                case "myAppointments":
                default:
                    url = viewMyAppointments(request, currentUser); // Hàm này đã được sửa
                    break;
            }
        } catch (Exception e) {
            log("Lỗi tại PatientLichHenController (doGet): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi: " + e.getMessage());
            url = ERROR_PAGE;
        } finally {
            if (!response.isCommitted()) { // Kiểm tra nếu AJAX đã viết response
                request.getRequestDispatcher(url).forward(request, response);
            }
        }
    }

    /**
     * CẬP NHẬT: Thêm action 'cancelAppointment'
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        // SỬA: Dùng "USER"
        TaiKhoanDTO currentUser = (session != null) ? (TaiKhoanDTO) session.getAttribute("USER") : null;

        if (currentUser == null || !"BENH_NHAN".equals(currentUser.getVaiTro())) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bạn cần đăng nhập với vai trò Bệnh nhân.");
            return;
        }

        String action = request.getParameter("action");
        String urlRedirect = "MainController?action=myAppointments"; // URL Redirect mặc định

        try {
            if ("bookAppointment".equals(action)) {
                // Thử gọi hàm bookAppointment
                urlRedirect = bookAppointment(request, currentUser);
                response.sendRedirect(urlRedirect); // Thực hiện redirect

                // === THÊM MỚI: Xử lý Hủy lịch ===
            } else if ("cancelAppointment".equals(action)) {
                cancelAppointment(request, currentUser);
                urlRedirect += "&cancelSuccess=true"; // Thêm thông báo
                response.sendRedirect(urlRedirect); // Thực hiện redirect
                // ================================

            } else {
                // Nếu action không hợp lệ
                request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho POST.");
                request.getRequestDispatcher(ERROR_PAGE).forward(request, response);
            }

        } catch (Exception e) {
            // --- XỬ LÝ LỖI (ValidationException hoặc lỗi khác) ---
            log("Lỗi tại PatientLichHenController (doPost): " + e.getMessage(), e);
            String errorPage = ERROR_PAGE;
            request.setAttribute("ERROR_MESSAGE", e.getMessage());

            // Nếu là lỗi Đặt lịch, quay lại Form Đặt lịch
            if ("bookAppointment".equals(action)) {
                errorPage = PATIENT_BOOKING_PAGE;
                try {
                    loadPatientBookingFormDependencies(request); // Tải lại Khoa
                } catch (Exception ex) {
                    request.setAttribute("LOAD_FORM_ERROR", "Lỗi tải danh sách Khoa.");
                }
                request.setAttribute("LICHHEN_DATA", createDTOFromRequest(request)); // Giữ lại dữ liệu
            } // Nếu là lỗi Hủy lịch, quay lại Trang của tôi
            else if ("cancelAppointment".equals(action)) {
                errorPage = MY_APPOINTMENTS_PAGE;
                try {
                    // Tải lại danh sách lịch hẹn để hiển thị
                    viewMyAppointments(request, currentUser);
                } catch (Exception ex) {
                    request.setAttribute("ERROR_MESSAGE", "Lỗi hủy: " + e.getMessage() + ". Lỗi tải lại: " + ex.getMessage());
                }
            }

            // 4. FORWARD (không redirect) về trang lỗi/form để hiển thị lỗi
            request.getRequestDispatcher(errorPage).forward(request, response);
        }
    }

    // --- CÁC HÀM HELPER ĐÃ CẬP NHẬT/THÊM MỚI ---
    /**
     * CẬP NHẬT: Chỉ tải danh sách Khoa (Bác sĩ sẽ được tải bằng AJAX)
     */
    private void loadPatientBookingFormDependencies(HttpServletRequest request) throws Exception {
        List<KhoaDTO> khoaList = khoaService.getAllKhoa(); // Giả sử Service có hàm này
        request.setAttribute("khoaList", khoaList);
    }

    /**
     * HÀM MỚI: Trả về JSON danh sách bác sĩ (cho AJAX)
     */
    private void getBacSiByKhoa(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        try {
            int khoaId = Integer.parseInt(request.getParameter("khoaId"));
            // Gọi Service NhanVien (đã cập nhật)
            List<NhanVienDTO> bacSiList = nhanVienService.getBacSiByKhoa(khoaId);
            out.print(gson.toJson(bacSiList));
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(Collections.singletonMap("error", "Lỗi tải danh sách bác sĩ: " + e.getMessage())));
        }
        out.flush();
    }

    /**
     * CẬP NHẬT: Tải danh sách Khoa
     */
    private String showPatientBookingForm(HttpServletRequest request) throws Exception {
        loadPatientBookingFormDependencies(request);
        return PATIENT_BOOKING_PAGE;
    }

    /**
     * CẬP NHẬT: Thêm logic Tìm kiếm
     */
    private String viewMyAppointments(HttpServletRequest request, TaiKhoanDTO currentUser) throws Exception {
        BenhNhan benhNhan = benhNhanDAO.findByTaiKhoanId(currentUser.getId());
        if (benhNhan == null) {
            request.setAttribute("ERROR_MESSAGE", "Không tìm thấy hồ sơ bệnh nhân liên kết với tài khoản này.");
            return MY_APPOINTMENTS_PAGE;
        }

        // Lấy keyword từ request
        String searchKeyword = request.getParameter("searchKeyword");

        // Gọi service đã cập nhật (có keyword)
        List<LichHenDTO> lichHenList = lichHenService.getLichHenByBenhNhan(benhNhan.getId(), searchKeyword);
        request.setAttribute("lichHenList", lichHenList);

        return MY_APPOINTMENTS_PAGE;
    }

    /**
     * CẬP NHẬT: Hàm bookAppointment (logic giữ nguyên, chỉ trả về URL)
     */
    private String bookAppointment(HttpServletRequest request, TaiKhoanDTO currentUser)
            throws Exception {

        LichHenDTO dto = createDTOFromRequest(request);
        int taiKhoanIdCuaBenhNhan = currentUser.getId();

        // Service (đã cập nhật) sẽ kiểm tra 5-limit và ném lỗi nếu full
        lichHenService.createAppointmentByPatient(dto, taiKhoanIdCuaBenhNhan);

        // Trả về URL redirect (để MainController bắt)
        return "MainController?action=myAppointments&bookSuccess=true";
    }

    /**
     * HÀM MỚI: Xử lý logic Hủy lịch hẹn
     */
    private void cancelAppointment(HttpServletRequest request, TaiKhoanDTO currentUser) throws Exception {
        int lichHenId = Integer.parseInt(request.getParameter("id"));
        int taiKhoanId = currentUser.getId();

        // Gọi service an toàn (đã thêm ở bước 2)
        lichHenService.cancelAppointmentByPatient(lichHenId, taiKhoanId);
    }

    /**
     * Hàm tiện ích tạo LichHenDTO từ request (phiên bản của Bệnh nhân). (Giữ
     * nguyên)
     */
    private LichHenDTO createDTOFromRequest(HttpServletRequest request) {
        LichHenDTO dto = new LichHenDTO();
        try {
            dto.setBacSiId(Integer.parseInt(request.getParameter("bacSiId")));
        } catch (NumberFormatException e) {
            log("Lỗi parse ID Bác sĩ (Patient Form)");
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
        return "Controller (Patient) quản lý các nghiệp vụ Lịch Hẹn của Bệnh nhân.";
    }
}
