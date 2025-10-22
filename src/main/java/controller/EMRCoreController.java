package controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.dto.PhieuKhamBenhDTO;
import service.PhieuKhamBenhService; // Import lớp Service

/**
 * Servlet đóng vai trò là Front Controller cho các nghiệp vụ cốt lõi của Bệnh
 * án điện tử (EMR). Nó điều phối các yêu cầu dựa trên tham số 'action'.
 */
@WebServlet(name = "EMRCoreController", urlPatterns = {"/EMRCoreController"})
public class EMRCoreController extends HttpServlet {

    // Khai báo URL cho các trang JSP để dễ quản lý
    private static final String ERROR_PAGE = "error.jsp";
    private static final String SUCCESS_PAGE = "danhSachPhieuKham.jsp";
    private static final String CREATE_ENCOUNTER_PAGE = "taoPhieuKham.jsp";

    // Khởi tạo Service ở cấp lớp để tái sử dụng trong các phương thức
    private final PhieuKhamBenhService phieuKhamService = new PhieuKhamBenhService();

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8"); // Đảm bảo đọc tiếng Việt

        String action = request.getParameter("action");
        String url = ERROR_PAGE; // Mặc định là trang lỗi

        try {
            if (action == null || action.isEmpty()) {
                throw new Exception("Hành động không được chỉ định.");
            }

            // Sử dụng switch-case để gọi phương thức xử lý tương ứng
            switch (action) {
                case "createEncounter":
                    url = createEncounter(request, response);
                    break;
                // Bạn có thể thêm các case khác ở đây
                // case "updateEncounter":
                //     url = updateEncounter(request, response);
                //     break;
                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ.");
            }
        } catch (Exception e) {
            log("Lỗi tại EMRCoreController: " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã có lỗi nghiêm trọng xảy ra: " + e.getMessage());
        } finally {
            // Chuyển hướng đến trang đã được xác định
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    /**
     * Xử lý logic tạo mới một Phiếu Khám Bệnh.
     *
     * @param request HttpServletRequest
     * @param response HttpServletResponse
     * @return URL của trang JSP để forward tới.
     */
    private String createEncounter(HttpServletRequest request, HttpServletResponse response) {
        PhieuKhamBenhDTO newEncounterDTO = new PhieuKhamBenhDTO();
        try {
            // 1. Lấy dữ liệu từ request và đóng gói vào DTO
            newEncounterDTO.setMaPhieuKham(request.getParameter("maPhieuKham"));
            newEncounterDTO.setTrieuChung(request.getParameter("trieuChung"));
            newEncounterDTO.setHuyetAp(request.getParameter("huyetAp"));
            newEncounterDTO.setChanDoan(request.getParameter("chanDoan"));
            newEncounterDTO.setKetLuan(request.getParameter("ketLuan"));

            // 2. Chuyển đổi và kiểm tra các kiểu dữ liệu khác
            String thoiGianKhamStr = request.getParameter("thoiGianKham");
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
            if (nhipThoStr != null && !nhipThoStr.isEmpty()) {
                newEncounterDTO.setNhipTho(Integer.parseInt(nhipThoStr));
            }

            // Các trường ID bắt buộc
            newEncounterDTO.setBenhNhanId(Integer.parseInt(request.getParameter("benhNhanId")));
            newEncounterDTO.setBacSiId(Integer.parseInt(request.getParameter("bacSiId")));

            // 3. Gọi tầng Service để thực hiện logic nghiệp vụ
            PhieuKhamBenhDTO result = phieuKhamService.createEncounter(newEncounterDTO);

            // 4. Xử lý kết quả trả về
            if (result != null) {
                request.setAttribute("SUCCESS_MESSAGE", "Tạo phiếu khám thành công! ID phiếu khám là: " + result.getId());
                return SUCCESS_PAGE;
            } else {
                request.setAttribute("ERROR_MESSAGE", "Tạo phiếu khám thất bại. Dữ liệu có thể không hợp lệ.");
                request.setAttribute("ENCOUNTER_DATA", newEncounterDTO); // Gửi lại dữ liệu đã nhập
                return CREATE_ENCOUNTER_PAGE;
            }

        } catch (NumberFormatException | DateTimeParseException e) {
            log("Lỗi :" + e.getMessage());
            request.setAttribute("ERROR_MESSAGE", e.getMessage());
            request.setAttribute("ENCOUNTER_DATA", newEncounterDTO); // Gửi lại dữ liệu đã nhập
            return CREATE_ENCOUNTER_PAGE;
        } catch (Exception e) {
            log("Lỗi không xác định trong createEncounter: " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
            return ERROR_PAGE;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Servlet điều phối chính cho các chức năng của bệnh án điện tử.";
    }// </editor-fold>
}
