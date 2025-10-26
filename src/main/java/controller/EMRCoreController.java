package controller;

import exception.ValidationException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
import model.dto.PhieuKhamBenhDTO;
import service.BenhNhanService;   // Import các Service cần thiết
import service.LichHenService;
import service.NhanVienService;
import service.PhieuKhamBenhService;

/**
 * Servlet đóng vai trò là Front Controller cho các nghiệp vụ của Bệnh án điện
 * tử (EMR).
 */
@WebServlet(name = "EMRCoreController", urlPatterns = {"/EMRCoreController"})
public class EMRCoreController extends HttpServlet {

    // Khai báo URL cho các trang JSP 
    private static final String ERROR_PAGE = "error.jsp";
    private static final String SUCCESS_PAGE = "danhSachPhieuKham.jsp";
    private static final String CREATE_ENCOUNTER_PAGE = "PhieuKhamBenh.jsp";

    // Khởi tạo các Service cần thiết ở cấp lớp để tái sử dụng
    private final PhieuKhamBenhService phieuKhamService = new PhieuKhamBenhService();
    private final BenhNhanService benhNhanService = new BenhNhanService();
    private final NhanVienService nhanVienService = new NhanVienService();
    private final LichHenService lichHenService = new LichHenService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String url = ERROR_PAGE;

        try {
            if (action == null || action.isEmpty()) {
                // Nếu không có action, có thể chuyển về trang chủ
                // url = "home.jsp";
                throw new Exception("Hành động không được chỉ định.");
            }

            switch (action) {
                case "showCreateForm":
                    url = showCreateForm(request);
                    break;
                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho phương thức GET.");
            }
        } catch (Exception e) {
            log("Lỗi tại EMRCoreController (doGet): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã có lỗi xảy ra: " + e.getMessage());
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

        try {
            if (action == null || action.isEmpty()) {
                throw new Exception("Hành động không được chỉ định.");
            }

            switch (action) {
                case "createEncounter":
                    url = createEncounter(request);
                    break;
                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho phương thức POST.");
            }
        } catch (Exception e) {
            log("Lỗi tại EMRCoreController (doPost): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã có lỗi nghiêm trọng xảy ra: " + e.getMessage());
        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    /**
     * Xử lý logic tạo mới một Phiếu Khám Bệnh.
     */
    private String createEncounter(HttpServletRequest request) {
        PhieuKhamBenhDTO newEncounterDTO = new PhieuKhamBenhDTO();
        try {
            // 1. Lấy dữ liệu từ request và đóng gói vào DTO
            newEncounterDTO.setMaPhieuKham(request.getParameter("maPhieuKham"));
            newEncounterDTO.setTrieuChung(request.getParameter("trieuChung"));
            newEncounterDTO.setHuyetAp(request.getParameter("huyetAp"));
            newEncounterDTO.setChanDoan(request.getParameter("chanDoan"));
            newEncounterDTO.setKetLuan(request.getParameter("ketLuan"));

            String thoiGianKhamStr = request.getParameter("thoiGianKham");
            if (thoiGianKhamStr != null && !thoiGianKhamStr.isEmpty()) {
                newEncounterDTO.setThoiGianKham(LocalDateTime.parse(thoiGianKhamStr));
            }

            String ngayTaiKhamStr = request.getParameter("ngayTaiKham");
            if (thoiGianKhamStr != null && !thoiGianKhamStr.isEmpty()) {
                newEncounterDTO.setThoiGianKham(LocalDateTime.parse(thoiGianKhamStr));
            }

            String nhietDoStr = request.getParameter("nhietDo");
            if (nhietDoStr != null && !nhietDoStr.isEmpty()) {
                newEncounterDTO.setNhietDo(new BigDecimal(nhietDoStr));
            }

            String nhipTimStr = request.getParameter("nhipTim");
            if (nhipTimStr != null && !nhipTimStr.isEmpty()) {
                newEncounterDTO.setNhipTim(Integer.parseInt(nhipTimStr));
            }
            String nhipThoStr = request.getParameter("nhipTho");
            if (nhipTimStr != null && !nhipTimStr.isEmpty()) {
                newEncounterDTO.setNhipTho(Integer.parseInt(nhipThoStr));
            }

            String lichHenStr = request.getParameter("lichHenId");
            if (lichHenStr != null && !lichHenStr.isEmpty()) {
                newEncounterDTO.setLichHenId(Integer.parseInt(lichHenStr));
            }

            newEncounterDTO.setBenhNhanId(Integer.parseInt(request.getParameter("benhNhanId")));
            newEncounterDTO.setBacSiId(Integer.parseInt(request.getParameter("bacSiId")));

            // 2. Gọi tầng Service để thực hiện logic nghiệp vụ
            PhieuKhamBenhDTO result = phieuKhamService.createEncounter(newEncounterDTO);

            // 3. Xử lý kết quả thành công
            request.setAttribute("SUCCESS_MESSAGE", "Tạo phiếu khám thành công! ID: " + result.getId());
            return SUCCESS_PAGE;

        } catch (ValidationException e) {
            // Bắt lỗi nghiệp vụ (do người dùng nhập sai)
            log("Lỗi nghiệp vụ khi tạo phiếu khám: " + e.getMessage());
            request.setAttribute("ERROR_MESSAGE", e.getMessage());
            request.setAttribute("ENCOUNTER_DATA", newEncounterDTO); // Gửi lại dữ liệu đã nhập
            loadCreateFormDependencies(request); // Tải lại dữ liệu cho dropdown
            return CREATE_ENCOUNTER_PAGE;
        } catch (DateTimeParseException e) {
            // Bắt lỗi định dạng
            log("Lỗi định dạng dữ liệu: " + e.getMessage());
            request.setAttribute("ERROR_MESSAGE", "Dữ liệu ngày tháng hoặc số không hợp lệ.");
            request.setAttribute("ENCOUNTER_DATA", newEncounterDTO); // Gửi lại dữ liệu đã nhập
            loadCreateFormDependencies(request); // Tải lại dữ liệu cho dropdown
            return CREATE_ENCOUNTER_PAGE;
        } catch (Exception e) {
            // Bắt các lỗi hệ thống khác
            log("Lỗi hệ thống khi tạo phiếu khám: " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống nghiêm trọng.");
            return ERROR_PAGE;
        }
    }

    /**
     * Tải các dữ liệu cần thiết cho form tạo phiếu khám (danh sách bệnh nhân,
     * bác sĩ).
     */
    private void loadCreateFormDependencies(HttpServletRequest request) {
        try {
            List<BenhNhanDTO> danhSachBenhNhan = benhNhanService.getAllBenhNhan();
            List<NhanVienDTO> danhSachBacSi = nhanVienService.findDoctorsBySpecialty();
            List<LichHenDTO> danhSachLichHen = lichHenService.getAllLichHen();

            request.setAttribute("danhSachLichHen", danhSachLichHen);
            request.setAttribute("danhSachBenhNhan", danhSachBenhNhan);
            request.setAttribute("danhSachBacSi", danhSachBacSi);
        } catch (Exception e) {
            log("Không thể tải dữ liệu cho form tạo phiếu khám: " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Không thể tải danh sách bệnh nhân và bác sĩ.");
        }
    }

    /**
     * Chuẩn bị dữ liệu và hiển thị form tạo phiếu khám.
     */
    private String showCreateForm(HttpServletRequest request) {
        loadCreateFormDependencies(request);
        return CREATE_ENCOUNTER_PAGE;
    }
    
    

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    @Override
    public String getServletInfo() {
        return "Servlet điều phối chính cho các chức năng của bệnh án điện tử.";
    }// </editor-fold>
}
