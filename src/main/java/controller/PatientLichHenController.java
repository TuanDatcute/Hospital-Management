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
import javax.servlet.http.HttpSession;
import model.dto.BenhNhanDTO;
import model.dto.LichHenDTO;
import model.dto.NhanVienDTO;
import model.dto.TaiKhoanDTO;
import model.dao.BenhNhanDAO;
import model.Entity.BenhNhan;
import service.LichHenService;
import service.NhanVienService;

/**
 * Controller MỚI: Chỉ xử lý nghiệp vụ Lịch Hẹn cho BỆNH NHÂN.
 */
@WebServlet(name = "PatientLichHenController", urlPatterns = {"/PatientLichHenController"})
public class PatientLichHenController extends HttpServlet {

    // (Các hằng số và Service/DAO giữ nguyên)
    private static final String PATIENT_BOOKING_PAGE = "DatLichHen.jsp";
    private static final String MY_APPOINTMENTS_PAGE = "LichHenCuaToi.jsp";
    private static final String ERROR_PAGE = "error.jsp";
    private final LichHenService lichHenService = new LichHenService();
    private final NhanVienService nhanVienService = new NhanVienService();
    private final BenhNhanDAO benhNhanDAO = new BenhNhanDAO();

    // (doGet giữ nguyên, không thay đổi)
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        // SỬA: Dùng "user" theo code bạn gửi lần trước
        TaiKhoanDTO currentUser = (session != null) ? (TaiKhoanDTO) session.getAttribute("user") : null;

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
            request.getRequestDispatcher(url).forward(request, response);
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);
        // SỬA: Dùng "user"
        TaiKhoanDTO currentUser = (session != null) ? (TaiKhoanDTO) session.getAttribute("user") : null;

        if (currentUser == null || !"BENH_NHAN".equals(currentUser.getVaiTro())) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Bạn cần đăng nhập với vai trò Bệnh nhân.");
            return;
        }

        String action = request.getParameter("action");

        // Bỏ biến 'url' và 'isRedirect'
        try {
            if ("bookAppointment".equals(action)) {
                // Thử gọi hàm bookAppointment
                String redirectUrl = bookAppointment(request, currentUser);

                // Nếu hàm chạy thành công, nó sẽ trả về chuỗi redirect
                response.sendRedirect(redirectUrl); // Thực hiện redirect

            } else {
                // Nếu action không phải "bookAppointment"
                request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho POST.");
                request.getRequestDispatcher(ERROR_PAGE).forward(request, response);
            }

        } catch (Exception e) {
            // --- XỬ LÝ LỖI (ValidationException hoặc lỗi khác) ---
            log("Lỗi tại PatientLichHenController (doPost): " + e.getMessage(), e);

            // 1. Gửi lỗi về JSP
            request.setAttribute("ERROR_MESSAGE", e.getMessage());

            // 2. Tải lại dữ liệu cần thiết cho form (danh sách bác sĩ)
            try {
                loadPatientBookingFormDependencies(request);
            } catch (Exception ex) {
                request.setAttribute("LOAD_FORM_ERROR", "Lỗi tải danh sách Bác sĩ.");
            }

            // 3. Giữ lại dữ liệu người dùng đã nhập
            request.setAttribute("LICHHEN_DATA", createDTOFromRequest(request));

            // 4. FORWARD (không redirect) về trang form để hiển thị lỗi
            request.getRequestDispatcher(PATIENT_BOOKING_PAGE).forward(request, response);
        }
    }

    // --- CÁC HÀM HELPER (Giữ nguyên) ---
    private void loadPatientBookingFormDependencies(HttpServletRequest request) throws Exception {
        List<NhanVienDTO> bacSiList = nhanVienService.findDoctorsBySpecialty();
        request.setAttribute("bacSiList", bacSiList);
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
        List<LichHenDTO> lichHenList = lichHenService.getLichHenByBenhNhan(benhNhan.getId());
        request.setAttribute("lichHenList", lichHenList);
        return MY_APPOINTMENTS_PAGE;
    }

    /**
     * CẬP NHẬT: Hàm bookAppointment giờ trả về String (URL redirect) và ném
     * Exception nếu thất bại.
     */
    private String bookAppointment(HttpServletRequest request, TaiKhoanDTO currentUser)
            throws Exception { // Ném Exception để doPost bắt

        // 1. Lấy dữ liệu từ Form
        LichHenDTO dto = createDTOFromRequest(request);

        // 2. Lấy ID tài khoản từ SESSION
        int taiKhoanIdCuaBenhNhan = currentUser.getId();

        // 3. Gọi Service (Service sẽ ném ValidationException nếu lỗi)
        lichHenService.createAppointmentByPatient(dto, taiKhoanIdCuaBenhNhan);

        // 4. Nếu thành công, trả về URL redirect (để MainController bắt)
        return "MainController?action=myAppointments&bookSuccess=true";
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
            // Không set ID, Service sẽ bắt lỗi này
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
                // Service sẽ bắt lỗi (dto.getThoiGianHen() sẽ là null)
            }
        }
        return dto;
    }

    @Override
    public String getServletInfo() {
        return "Controller (Patient) quản lý các nghiệp vụ Lịch Hẹn của Bệnh nhân.";
    }
}
