package controller;

import com.google.gson.Gson;
import exception.ValidationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Collections;
import java.util.List;
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

/**
 *
 * @author SunnyU
 */
@WebServlet(name = "NurseLichHenController", urlPatterns = {"/NurseLichHenController"})
public class NurseLichHenController extends HttpServlet {

    // Khai báo URL cho các trang JSP
    private static final String LICHHEN_LIST_PAGE = "NurseListLichHen.jsp";
    private static final String ERROR_PAGE = "error.jsp";

    // Khởi tạo các Service cần thiết
    private final LichHenService lichHenService = new LichHenService();
    private final BenhNhanService benhNhanService = new BenhNhanService();
    private final NhanVienService nhanVienService = new NhanVienService();
    private final KhoaService khoaService = new KhoaService();
    private final Gson gson = new Gson();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");

        String url = ERROR_PAGE;
        try {
            if (action == null || action.isEmpty()) {
                action = "listLichHen"; // Đặt action mặc định
            }

            switch (action) {
                case "listLichHenNurse":
                    url = listLichHen(request);
                    break;
                case "getDoctorsByKhoa":
                    handleGetDoctorsByKhoa(request, response);
                    return; // Đã xử lý, không forward              
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

        try {
            if (action == null || action.isEmpty()) {
                throw new Exception("Hành động không được chỉ định.");
            }

            switch (action) {
                case "createAppointment":
                    url = createAppointment(request, response);
                    break;
                case "updateAppointmentStatus":
                    url = updateAppointmentStatus(request);
                    break;
                default:
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
            request.setAttribute("ERROR_MESSAGE", "Đã có lỗi nghiêm trọng xảy ra: " + e.getMessage());
            request.getRequestDispatcher(ERROR_PAGE).forward(request, response);

        }
    }

    //====================================================Dat=================================================
    private String createAppointment(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        LichHenDTO dto = new LichHenDTO();
        String redirectUrl = "redirect:/MainController?action=listLichHenNurse&keyword="; // URL nếu thành công

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
            LichHenDTO result = lichHenService.createAppointmentByNurse(dto);
            String encodedKeyword = java.net.URLEncoder.encode(result.getTenBenhNhan(), "UTF-8");
            // 3. Xử lý thành công
            request.getSession().setAttribute("SUCCESS_MESSAGE", "Tạo lịch hẹn thành công!");
            return redirectUrl + encodedKeyword;

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

    /**
     * Lấy danh sách Lịch hẹn (có tìm kiếm) và chuyển đến trang hiển thị.
     */
    private String listLichHen(HttpServletRequest request) {
        String keyword = request.getParameter("keyword");
        List<LichHenDTO> danhSachLichHen;

        try {
            if (keyword != null && !keyword.trim().isEmpty()) {
                danhSachLichHen = lichHenService.searchAppointments(keyword);
                request.setAttribute("searchKeyword", keyword);
            } else {
                danhSachLichHen = lichHenService.getAllAppointments();
            }
            request.setAttribute("danhSachLichHen", danhSachLichHen);
        } catch (Exception e) {
            log("Lỗi khi lấy danh sách lịch hẹn: ", e);
            request.setAttribute("ERROR_MESSAGE", "Không thể tải danh sách lịch hẹn.");
        }
        return LICHHEN_LIST_PAGE;
    }

    /**
     * Xử lý yêu cầu cập nhật trạng thái của một lịch hẹn.
     *
     * @return URL để redirect về lại trang danh sách.
     */
    private String updateAppointmentStatus(HttpServletRequest request) {
        String redirectUrl = "redirect:/MainController?action=listLichHenNurse";
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            String newStatus = request.getParameter("trangThaiMoi");

            lichHenService.updateAppointmentStatus(id, newStatus);
            request.getSession().setAttribute("SUCCESS_MESSAGE", "Cập nhật trạng thái lịch hẹn thành công!");

        } catch (ValidationException | NumberFormatException e) {
            request.getSession().setAttribute("ERROR_MESSAGE", e.getMessage());
        } catch (Exception e) {
            log("Lỗi khi cập nhật trạng thái lịch hẹn: ", e);
            request.getSession().setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống.");
        }

        // Luôn redirect về trang danh sách
        return redirectUrl;
    }

    @Override
    public String getServletInfo() {
        return "Controller quản lý các nghiệp vụ liên quan đến Lịch Hẹn.";
    }
}
