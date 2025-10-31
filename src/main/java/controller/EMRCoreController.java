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
import model.dto.ChiTietDonThuocDTO;
import model.dto.DichVuDTO;
import model.dto.DonThuocDTO;
import model.dto.LichHenDTO;
import model.dto.NhanVienDTO;
import model.dto.PhieuKhamBenhDTO;
import service.BenhNhanService;   // Import các Service cần thiết
import service.ChiDinhDichVuService;
import service.DichVuService;
import service.DonThuocService;
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
    private static final String SUCCESS_PAGE = "DanhSachPhieuKham.jsp";
    private static final String CREATE_ENCOUNTER_PAGE = "PhieuKhamBenh.jsp";
    private static final String ENCOUNTER_LIST_PAGE = "DanhSachPhieuKham.jsp";
    private static final String ENCOUNTER_DETAIL_PAGE = "ChiTietPhieuKham.jsp";
    private static final String PRINT_ENCOUNTER_PAGE = "inBenhAn.jsp";

    // Khởi tạo các Service cần thiết ở cấp lớp để tái sử dụng
    private final PhieuKhamBenhService phieuKhamService = new PhieuKhamBenhService();
    private final BenhNhanService benhNhanService = new BenhNhanService();
    private final NhanVienService nhanVienService = new NhanVienService();
    private final LichHenService lichHenService = new LichHenService();
    private final ChiDinhDichVuService chiDinhService = new ChiDinhDichVuService();
    private final DichVuService dv = new DichVuService();
    private final DonThuocService donThuocService = new DonThuocService();

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
                case "printEncounter":
                    url = printEncounter(request);
                    break;
                case "getEncounterDetails":
                    url = showCreateForm(request);
                    break;
                case "listAllEncounters":
                    url = listAllEncounters(request);
                    break;
                case "viewEncounterDetails":
                    url = viewEncounterDetails(request);
                    break;
                case "showCreateEncounterForm":
                    url = showCreateForm(request);
                    break;
                case "showCreateDonThuocForm":
                    url = showCreateForm(request);
                    break;
                case "showUpdateEncounterForm":
                    url = showUpdateForm(request);
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
                case "updateEncounter":
                    url = updateEncounter(request);
                    break;
                case "addServiceRequest":
                    url = addServiceRequest(request);
                    break;
                case "createEncounter":
                    url = createEncounter(request);
                    break;
                case "updateServiceResult":
                    url = updateServiceResult(request);
                    break;
                case "completeEncounter":
                    url = completeEncounter(request);
                    break;
            }
            if (url.startsWith("redirect:")) {
                String redirectUrl = url.substring("redirect:".length());
                response.sendRedirect(request.getContextPath() + redirectUrl);
            } else {
                request.getRequestDispatcher(url).forward(request, response);
            }
        } catch (Exception e) {
            log("Lỗi tại EMRCoreController (doPost): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã có lỗi nghiêm trọng xảy ra: " + e.getMessage());
            request.getRequestDispatcher(ERROR_PAGE).forward(request, response);
        }
    }

    /**
     * Lấy danh sách tất cả phiếu khám và hiển thị có tìm kiếm.
     */
    private String listAllEncounters(HttpServletRequest request) {
        String keyword = request.getParameter("keyword"); // Lấy từ khóa từ URL
        List<PhieuKhamBenhDTO> danhSachPhieuKham;

        try {
            if (keyword != null && !keyword.trim().isEmpty()) {
                // Nếu có từ khóa -> gọi hàm tìm kiếm
                danhSachPhieuKham = phieuKhamService.searchEncounters(keyword);
                request.setAttribute("searchKeyword", keyword); // Gửi lại từ khóa để hiển thị
            } else {
                // Nếu không có từ khóa -> gọi hàm lấy tất cả
                danhSachPhieuKham = phieuKhamService.getAllEncounters();
            }
            request.setAttribute("danhSachPhieuKham", danhSachPhieuKham);
        } catch (Exception e) {
            log("Lỗi khi lấy danh sách phiếu khám: ", e);
            request.setAttribute("ERROR_MESSAGE", "Không thể tải danh sách phiếu khám.");
        }
        return ENCOUNTER_LIST_PAGE;
    }

    /**
     * Lấy chi tiết một phiếu khám và hiển thị (chuyển hướng đến trang chi
     * tiết).
     */
    private String viewEncounterDetails(HttpServletRequest request) throws ValidationException {
        String idStr = request.getParameter("id");

        if (idStr == null || idStr.trim().isEmpty()) {
            throw new ValidationException("ID phiếu khám không hợp lệ hoặc bị thiếu.");
        }
        try {
            int id = Integer.parseInt(request.getParameter("id"));

            PhieuKhamBenhDTO phieuKham = phieuKhamService.getEncounterById(id);

            if (phieuKham == null) {
                throw new ValidationException("Không tìm thấy phiếu khám với ID: " + id);
            }

            // 2. Lấy danh sách dịch vụ cho dropdown 
            List<DichVuDTO> danhSachDichVu = dv.getAllServices();
            List<ChiTietDonThuocDTO> danhSachDonThuoc = donThuocService.getChiTietByPhieuKhamId(id);

            // 3. Gửi các đối tượng cần thiết cho JSP
            request.setAttribute("phieuKham", phieuKham);
            request.setAttribute("danhSachDichVu", danhSachDichVu);
            request.setAttribute("danhSachDonThuoc", danhSachDonThuoc);

            return ENCOUNTER_DETAIL_PAGE;
        } catch (ValidationException e) {
            throw new ValidationException("ID phiếu khám phải là một con số.");
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
            if (ngayTaiKhamStr != null && !ngayTaiKhamStr.isEmpty()) {
                newEncounterDTO.setNgayTaiKham(LocalDateTime.parse(ngayTaiKhamStr));
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
            newEncounterDTO.setTrangThai("CHUA_HOAN_THANH");
            // 2. Gọi tầng Service để thực hiện logic nghiệp vụ
            PhieuKhamBenhDTO result = phieuKhamService.createEncounter(newEncounterDTO);

            String keyword = result.getMaPhieuKham();
//            // Cần mã hóa keyword để đảm bảo URL hợp lệ
            String encodedKeyword = java.net.URLEncoder.encode(keyword, "UTF-8");

            // 3. Xử lý kết quả thành công
            request.setAttribute("SUCCESS_MESSAGE", "Tạo phiếu khám thành công! ID: " + result.getId());
            return "redirect:/MainController?action=listAllEncounters&keyword=" + encodedKeyword;

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
     * Lấy dữ liệu phiếu khám và forward đến trang in.
     */
    private String printEncounter(HttpServletRequest request) throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));

        PhieuKhamBenhDTO phieuKham = phieuKhamService.getEncounterById(id);
        if (phieuKham == null) {
            throw new Exception("Không tìm thấy phiếu khám để in.");
        }
        request.setAttribute("phieuKham", phieuKham);
        return PRINT_ENCOUNTER_PAGE;
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

    /**
     * Xử lý yêu cầu thêm một chỉ định dịch vụ vào phiếu khám. Luôn trả về một
     * URL để redirect, dù thành công hay thất bại.
     *
     * @param request HttpServletRequest
     * @return URL để redirect, có tiền tố "redirect:".
     */
    private String addServiceRequest(HttpServletRequest request) {
        // Lấy ID phiếu khám để xây dựng URL redirect
        String phieuKhamIdStr = request.getParameter("phieuKhamId");
        String redirectUrl = "/MainController?action=viewEncounterDetails&id=" + phieuKhamIdStr;

        try {
            // 1. Lấy và kiểm tra các tham số
            String dichVuIdStr = request.getParameter("dichVuId");
            if (phieuKhamIdStr == null || dichVuIdStr == null) {
                throw new ValidationException("ID phiếu khám hoặc dịch vụ không được để trống.");
            }

            int phieuKhamId = Integer.parseInt(phieuKhamIdStr);
            int dichVuId = Integer.parseInt(dichVuIdStr);

            // 2. Gọi Service để thực hiện nghiệp vụ
            chiDinhService.createServiceRequest(phieuKhamId, dichVuId);

            // 3. Đặt thông báo thành công vào session
            request.getSession().setAttribute("SUCCESS_MESSAGE", "Đã thêm dịch vụ vào phiếu khám thành công!");

        } catch (ValidationException | NumberFormatException e) {
            // 4. Xử lý lỗi nghiệp vụ hoặc lỗi định dạng
            // Đặt thông báo lỗi vào session
            request.getSession().setAttribute("ERROR_MESSAGE", e.getMessage());
        } catch (Exception e) {
            // 5. Xử lý các lỗi hệ thống khác
            log("Lỗi hệ thống khi thêm chỉ định dịch vụ: ", e);
            request.getSession().setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống nghiêm trọng.");
        }

        // 6. Luôn trả về URL để redirect
        return "redirect:" + redirectUrl;
    }

    /**
     * Xử lý yêu cầu cập nhật kết quả và trạng thái của một chỉ định dịch vụ.
     *
     * @return URL để redirect về trang chi tiết phiếu khám.
     */
    private String updateServiceResult(HttpServletRequest request) {
        String phieuKhamIdStr = request.getParameter("phieuKhamId");
        // Luôn xây dựng URL redirect trước, dù thành công hay thất bại
        String redirectUrl = "/MainController?action=viewEncounterDetails&id=" + phieuKhamIdStr;

        try {

            int chiDinhId = Integer.parseInt(request.getParameter("chiDinhId"));
            String ketQua = request.getParameter("ketQuaMoi");
            String trangThai = request.getParameter("trangThaiMoi"); // Giả sử có dropdown trạng thái

            chiDinhService.updateResultAndStatus(chiDinhId, ketQua, trangThai);

            request.getSession().setAttribute("SUCCESS_MESSAGE", "Đã cập nhật kết quả dịch vụ thành công!");

        } catch (ValidationException | NumberFormatException e) {
            request.getSession().setAttribute("ERROR_MESSAGE", e.getMessage());
        } catch (Exception e) {
            log("Lỗi khi cập nhật kết quả dịch vụ: ", e);
            request.getSession().setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống khi cập nhật.");
        }

        // 6. Luôn trả về URL để redirect
        return "redirect:" + redirectUrl;
    }

    /**
     * Lấy dữ liệu phiếu khám cần sửa và hiển thị form.
     */
    private String showUpdateForm(HttpServletRequest request) throws Exception {
        int id = Integer.parseInt(request.getParameter("id"));
        PhieuKhamBenhDTO phieuKham = phieuKhamService.getEncounterById(id);
        if (phieuKham == null) {
            throw new Exception("Không tìm thấy phiếu khám để cập nhật.");
        }

        request.setAttribute("phieuKham", phieuKham); // Gửi đối tượng cần sửa đến JSP
        loadCreateFormDependencies(request); // Vẫn cần tải danh sách bác sĩ cho dropdown
        return CREATE_ENCOUNTER_PAGE; // Dùng lại trang form tạo mới
    }

    /**
     * Xử lý logic cập nhật thông tin phiếu khám.
     */
    private String updateEncounter(HttpServletRequest request) {
        int id = Integer.parseInt(request.getParameter("id"));
        PhieuKhamBenhDTO dto = new PhieuKhamBenhDTO();
        dto.setId(id); // Quan trọng: set ID cho DTO

        try {
            // Lấy dữ liệu mới từ form
            // Lưu ý: Không lấy maPhieuKham và benhNhanId vì không cho sửa
            dto.setBacSiId(Integer.parseInt(request.getParameter("bacSiId")));
            dto.setTrieuChung(request.getParameter("trieuChung"));
            // 1. Lấy dữ liệu từ request và đóng gói vào DTO

            dto.setHuyetAp(request.getParameter("huyetAp"));
            dto.setChanDoan(request.getParameter("chanDoan"));
            dto.setKetLuan(request.getParameter("ketLuan"));

            String thoiGianKhamStr = request.getParameter("thoiGianKham");
            if (thoiGianKhamStr != null && !thoiGianKhamStr.isEmpty()) {
                dto.setThoiGianKham(LocalDateTime.parse(thoiGianKhamStr));
            }

            String ngayTaiKhamStr = request.getParameter("ngayTaiKham");
            if (ngayTaiKhamStr != null && !ngayTaiKhamStr.isEmpty()) {
                dto.setNgayTaiKham(LocalDateTime.parse(ngayTaiKhamStr));
            }

            String nhietDoStr = request.getParameter("nhietDo");
            if (nhietDoStr != null && !nhietDoStr.isEmpty()) {
                dto.setNhietDo(new BigDecimal(nhietDoStr));
            }

            String nhipTimStr = request.getParameter("nhipTim");
            if (nhipTimStr != null && !nhipTimStr.isEmpty()) {
                dto.setNhipTim(Integer.parseInt(nhipTimStr));
            }
            String nhipThoStr = request.getParameter("nhipTho");
            if (nhipTimStr != null && !nhipTimStr.isEmpty()) {
                dto.setNhipTho(Integer.parseInt(nhipThoStr));
            }

            String lichHenStr = request.getParameter("lichHenId");
            if (lichHenStr != null && !lichHenStr.isEmpty()) {
                dto.setLichHenId(Integer.parseInt(lichHenStr));
            }
            phieuKhamService.updateEncounter(dto); // Giả sử Service có hàm update

            //maPhieuKham để tìm kiếm
            String maPhieuKham = request.getParameter("maPhieuKham");
            request.getSession().setAttribute("SUCCESS_MESSAGE", "Cập nhật phiếu khám thành công!");
            return "redirect:/MainController?action=listAllEncounters&keyword=" + maPhieuKham;

        } catch (ValidationException e) {
            request.setAttribute("ERROR_MESSAGE", e.getMessage());
            request.setAttribute("phieuKham", dto); // Gửi lại dữ liệu đã nhập
            loadCreateFormDependencies(request);
            return CREATE_ENCOUNTER_PAGE;
        } catch (Exception e) {
            request.setAttribute("ERROR_MESSAGE", e.getMessage());
            request.setAttribute("phieuKham", dto); // Gửi lại dữ liệu đã nhập
            loadCreateFormDependencies(request);
            return CREATE_ENCOUNTER_PAGE;
        }
    }

    /**
     * Xử lý yêu cầu hoàn thành một phiếu khám.
     *
     * @return URL để redirect về trang chi tiết.
     */
    private String completeEncounter(HttpServletRequest request) {
        String phieuKhamIdStr = request.getParameter("phieuKhamId");
        try {
            int phieuKhamId = Integer.parseInt(phieuKhamIdStr);
            phieuKhamService.completeEncounterStatus(phieuKhamId);
            request.getSession().setAttribute("SUCCESS_MESSAGE", "Đã hoàn thành phiếu khám thành công!");
        } catch (ValidationException e) {
            request.getSession().setAttribute("ERROR_MESSAGE", e.getMessage());
        } catch (Exception e) {
            log("Lỗi khi hoàn thành phiếu khám: ", e);
            request.getSession().setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống.");
        }
        // Luôn redirect về lại trang chi tiết để xem kết quả
        return "redirect:/MainController?action=viewEncounterDetails&id=" + phieuKhamIdStr;
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    @Override
    public String getServletInfo() {
        return "Servlet điều phối chính cho các chức năng của bệnh án điện tử.";
    }// </editor-fold>
}
