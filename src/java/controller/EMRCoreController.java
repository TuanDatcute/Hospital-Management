/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import model.PhieuKhamBenhDAO;
import model.PhieuKhamBenhDTO; // Import DTO

/**
 *
 * @author SunnyU
 */
@WebServlet(name = "EMRCoreController", urlPatterns = {"/EMRCoreController"})
public class EMRCoreController extends HttpServlet {

    // Khai báo URL cho các trang JSP để dễ quản lý
    private static final String ERROR_PAGE = "error.jsp";
    private static final String SUCCESS_PAGE = "success.jsp";
    private static final String CREATE_ENCOUNTER_PAGE = "phieuKhamBenh.jsp";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8"); // Đảm bảo đọc tiếng Việt

        // Lấy tham số 'action' từ form
        String action = request.getParameter("action");
        String url = ERROR_PAGE; // Mặc định là trang lỗi

        try {
            if (action == null) {
                // Nếu không có action, có thể chuyển về trang chủ hoặc trang mặc định
                // url = "home.jsp"; 
                throw new Exception("Hành động không được chỉ định.");
            }

            switch (action) {
                case "createEncounter":
                    // Gọi phương thức xử lý và nhận URL để chuyển hướng
                    url = createEncounter(request, response);
                    break;
                // Thêm các case khác ở đây (ví dụ: "updateEncounter", "viewEncounter",...)
                // case "updateEncounter":
                //     url = updateEncounter(request, response);
                //     break;
                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ.");
            }
        } catch (Exception e) {
            log("Lỗi tại EMRCoreController: " + e.toString());
            request.setAttribute("ERROR_MESSAGE", "Đã có lỗi xảy ra: " + e.getMessage());
        } finally {
            // Chuyển hướng đến trang được chỉ định
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    private String createEncounter(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        try {
            // 1. Lấy dữ liệu từ request
            String maPhieuKham = request.getParameter("maPhieuKham");
            String thoiGianKhamStr = request.getParameter("thoiGianKham");
            String trieuChung = request.getParameter("trieuChung");
            String nhietDoStr = request.getParameter("nhietDo");
            String huyetAp = request.getParameter("huyetAp");
            String nhipTimStr = request.getParameter("nhipTim");
            String nhipThoStr = request.getParameter("nhipTho");
            String chanDoan = request.getParameter("chanDoan");
            String ketLuan = request.getParameter("ketLuan");
            String ngayTaiKhamStr = request.getParameter("ngayTaiKham");
            String benhNhanIdStr = request.getParameter("benhNhanId");
            String nhanVienIdStr = request.getParameter("nhanVienId");
            String lichHenIdStr = request.getParameter("lichHenId");

            // 2. Chuyển đổi và kiểm tra dữ liệu
            // Chuyển đổi ngày giờ từ chuỗi (format của input datetime-local) sang Timestamp
            Timestamp thoiGianKham = Timestamp.valueOf(LocalDateTime.parse(thoiGianKhamStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            Timestamp ngayTaiKham = null;
            if (ngayTaiKhamStr != null && !ngayTaiKhamStr.isEmpty()) {
                 ngayTaiKham = Timestamp.valueOf(LocalDateTime.parse(ngayTaiKhamStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME));
            }
            
            BigDecimal nhietDo = (nhietDoStr != null && !nhietDoStr.isEmpty()) ? new BigDecimal(nhietDoStr) : null;
            int nhipTim = (nhipTimStr != null && !nhipTimStr.isEmpty()) ? Integer.parseInt(nhipTimStr) : 0;
            int nhipTho = (nhipThoStr != null && !nhipThoStr.isEmpty()) ? Integer.parseInt(nhipThoStr) : 0;
            int benhNhanId = Integer.parseInt(benhNhanIdStr);
            int nhanVienId = Integer.parseInt(nhanVienIdStr);
            Integer lichHenId = (lichHenIdStr != null && !lichHenIdStr.isEmpty()) ? Integer.valueOf(lichHenIdStr) : null;
            
            // 3. Tạo đối tượng DTO
            PhieuKhamBenhDTO newEncounter = new PhieuKhamBenhDTO(0, maPhieuKham, thoiGianKham, trieuChung, nhietDo,
                    huyetAp, nhipTim, nhipTho, chanDoan, ketLuan, ngayTaiKham, benhNhanId, nhanVienId, lichHenId);

            // 4. Gọi DAO để lưu vào CSDL
            PhieuKhamBenhDAO dao = new PhieuKhamBenhDAO();
            PhieuKhamBenhDTO result = dao.createEncounter(newEncounter);

            // 5. Xử lý kết quả và trả về URL
            if (result != null) {
                request.setAttribute("SUCCESS_MESSAGE", "Tạo phiếu khám thành công! ID phiếu khám là: " + result.getPhieuKhamBenhId());
                return SUCCESS_PAGE;
            } else {
                request.setAttribute("ERROR_MESSAGE", "Tạo phiếu khám thất bại. Vui lòng thử lại.");
                // Trả lại các dữ liệu đã nhập để người dùng không phải nhập lại
                request.setAttribute("ENCOUNTER_DATA", newEncounter); 
                return CREATE_ENCOUNTER_PAGE;
            }

        } catch (NumberFormatException e) {
            log("Lỗi chuyển đổi dữ liệu: " + e.toString());
            request.setAttribute("ERROR_MESSAGE", "Dữ liệu số không hợp lệ (ví dụ: ID, nhịp tim...). Vui lòng kiểm tra lại.");
            return CREATE_ENCOUNTER_PAGE; // Quay lại trang tạo để sửa
        } catch (Exception e) {
            log("Lỗi không xác định trong createEncounter: " + e.toString());
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
            return ERROR_PAGE;
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods.">
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