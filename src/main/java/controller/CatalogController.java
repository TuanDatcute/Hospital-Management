/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import exception.ValidationException;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.dto.DichVuDTO;
import service.DichVuService;

/**
 *
 * @author SunnyU
 */
@WebServlet(name = "CatalogController", urlPatterns = {"/CatalogController"})
public class CatalogController extends HttpServlet {

    private static final String ERROR_PAGE = "error.jsp";
    private static final String SUCCESS_PAGE = "danhSachPhieuKham.jsp";
    private static final String CREATE_SERVICE_PAGE = "DichVu.jsp";

    private static final DichVuService dichVuService = new DichVuService();

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
                case "showCreateServiceForm":
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
                case "createService":
                    url = createService(request);
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

    private String showCreateForm(HttpServletRequest request) {
        return CREATE_SERVICE_PAGE;
    }

    private String createService(HttpServletRequest request) {
        // Tạo một DTO để chứa dữ liệu từ form
        DichVuDTO newServiceDTO = new DichVuDTO();

        try {

            // 1. LẤY DỮ LIỆU THÔ TỪ REQUEST
            String tenDichVu = request.getParameter("tenDichVu");
            String moTa = request.getParameter("moTa");
            String donGiaStr = request.getParameter("donGia");

            // 2. ĐÓNG GÓI DỮ LIỆU VÀO DTO (VALIDATE & CONVERT)
            // Gán các giá trị đã lấy vào DTO để có thể gửi lại cho form nếu có lỗi
            newServiceDTO.setTenDichVu(tenDichVu);
            newServiceDTO.setMoTa(moTa);

            // Chuyển đổi đơn giá từ String sang BigDecimal
            if (donGiaStr != null && !donGiaStr.isEmpty()) {
                newServiceDTO.setDonGia(new BigDecimal(donGiaStr));
            }

            // 3. GỌI TẦNG SERVICE ĐỂ XỬ LÝ NGHIỆP VỤ
            DichVuDTO result = dichVuService.createService(newServiceDTO);

            // 4. XỬ LÝ KẾT QUẢ THÀNH CÔNG
            request.setAttribute("SUCCESS_MESSAGE", "Đã tạo dịch vụ '" + result.getTenDichVu() + "' thành công!");
            // Có thể chuyển hướng về trang danh sách dịch vụ
            return "danhSachDichVu.jsp";

        } catch (ValidationException e) {
            // Bắt lỗi nghiệp vụ (ví dụ: tên trùng) từ Service
            log("Lỗi nghiệp vụ khi tạo dịch vụ: " + e.getMessage());
            request.setAttribute("ERROR_MESSAGE", e.getMessage());
            request.setAttribute("SERVICE_DATA", newServiceDTO); // Gửi lại dữ liệu đã nhập
            return CREATE_SERVICE_PAGE; // Quay lại trang tạo để sửa

        } catch (NumberFormatException e) {
            // Bắt lỗi định dạng số (ví dụ: nhập chữ vào ô đơn giá)
            log("Lỗi định dạng đơn giá: " + e.getMessage());
            request.setAttribute("ERROR_MESSAGE", "Đơn giá phải là một con số hợp lệ.");
            request.setAttribute("SERVICE_DATA", newServiceDTO); // Gửi lại dữ liệu đã nhập
            return CREATE_SERVICE_PAGE;

        } catch (Exception e) {
            // Bắt các lỗi hệ thống không lường trước được
            log("Lỗi hệ thống khi tạo dịch vụ: " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống nghiêm trọng.");
            return "error.jsp";
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
