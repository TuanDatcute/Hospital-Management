package controller;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.dto.KhoaDTO;
import service.KhoaService;

/**
 * Controller xử lý các nghiệp vụ liên quan đến Khoa (Department).
 */
@WebServlet(name = "KhoaController", urlPatterns = {"/KhoaController"})
public class KhoaController extends HttpServlet {

    // Khai báo URL cho các trang JSP
    private static final String KHOA_LIST_PAGE = "admin/danhSachKhoa.jsp"; // Trang danh sách Khoa
    private static final String KHOA_FORM_PAGE = "admin/formKhoa.jsp";   // Trang form tạo/sửa Khoa
    private static final String ERROR_PAGE = "error.jsp";

    // Khởi tạo Service
    private final KhoaService khoaService = new KhoaService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String url = KHOA_LIST_PAGE; // Mặc định là hiển thị danh sách

        try {
            if (action == null || action.isEmpty()) {
                action = "listKhoa"; // Nếu không có action, mặc định là list
            }

            switch (action) {
                case "listKhoa":
                    url = listKhoa(request);
                    break;
                case "showKhoaCreateForm":
                    // Chỉ cần chuyển đến trang form, không cần load dữ liệu gì thêm
                    request.setAttribute("formAction", "createKhoa"); // Gửi action cho form biết là tạo mới
                    url = KHOA_FORM_PAGE;
                    break;
                case "showKhoaEditForm":
                    url = showKhoaEditForm(request);
                    break;
                case "deleteKhoa": // Xử lý xóa bằng GET (cẩn thận!) hoặc POST tùy thiết kế
                    url = deleteKhoa(request);
                    // Sau khi xóa, load lại danh sách
                    if (!url.equals(ERROR_PAGE)) { // Chỉ load lại nếu xóa thành công
                         url = listKhoa(request); // Chuyển hướng về trang danh sách sau khi xóa
                         request.setAttribute("SUCCESS_MESSAGE", "Xóa khoa thành công!"); // Thông báo thành công
                    }
                    break;
                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho GET.");
                    url = ERROR_PAGE;
            }
        } catch (Exception e) {
            log("Lỗi tại KhoaController (doGet): " + e.getMessage(), e);
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

        try {
            if (action == null || action.isEmpty()) {
                throw new Exception("Hành động không được chỉ định.");
            }

            switch (action) {
                case "createKhoa":
                    url = createKhoa(request);
                    break;
                case "updateKhoa":
                    url = updateKhoa(request);
                    break;
                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho POST.");
            }
             // Nếu thành công (không phải trang lỗi), load lại danh sách
            if (!url.equals(ERROR_PAGE) && !url.equals(KHOA_FORM_PAGE)) {
                 url = listKhoa(request);
            }

        } catch (Exception e) {
            log("Lỗi tại KhoaController (doPost): " + e.getMessage(), e);
            // Nếu lỗi validation từ Service, quay lại form
            if (e.getMessage().contains("Tên khoa")) { // Kiểm tra thông báo lỗi
                 request.setAttribute("ERROR_MESSAGE", e.getMessage());
                 // Gửi lại dữ liệu cũ và action để form biết là đang sửa/tạo lỗi
                 request.setAttribute("KHOA_DATA", createDTOFromRequest(request));
                 request.setAttribute("formAction", action);
                 url = KHOA_FORM_PAGE;
            } else { // Lỗi hệ thống khác
                request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
            }

        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    /**
     * Lấy danh sách Khoa và chuyển đến trang hiển thị.
     */
    private String listKhoa(HttpServletRequest request) throws Exception {
        List<KhoaDTO> list = khoaService.getAllKhoa();
        request.setAttribute("LIST_KHOA", list);
        return KHOA_LIST_PAGE;
    }

     /**
     * Lấy thông tin Khoa cần sửa và hiển thị form.
     */
    private String showKhoaEditForm(HttpServletRequest request) throws Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            KhoaDTO khoa = khoaService.getKhoaById(id);
            request.setAttribute("KHOA_DATA", khoa); // Gửi DTO của khoa cần sửa
            request.setAttribute("formAction", "updateKhoa"); // Gửi action cho form biết là cập nhật
             return KHOA_FORM_PAGE;
        } catch (NumberFormatException e) {
             request.setAttribute("ERROR_MESSAGE", "ID Khoa không hợp lệ.");
            return ERROR_PAGE;
        }
    }

    /**
     * Xử lý logic tạo mới một Khoa.
     */
    private String createKhoa(HttpServletRequest request) throws Exception {
        KhoaDTO newKhoaDTO = createDTOFromRequest(request);
        KhoaDTO result = khoaService.createKhoa(newKhoaDTO);
        request.setAttribute("SUCCESS_MESSAGE", "Tạo khoa '" + result.getTenKhoa() + "' thành công!");
        return KHOA_LIST_PAGE; // Quay về trang danh sách
    }

    /**
     * Xử lý logic cập nhật một Khoa.
     */
    private String updateKhoa(HttpServletRequest request) throws Exception {
         try {
            int id = Integer.parseInt(request.getParameter("id")); // Lấy ID từ hidden input trong form
            KhoaDTO khoaDTO = createDTOFromRequest(request);
            KhoaDTO result = khoaService.updateKhoa(id, khoaDTO);
            request.setAttribute("SUCCESS_MESSAGE", "Cập nhật khoa '" + result.getTenKhoa() + "' thành công!");
            return KHOA_LIST_PAGE; // Quay về trang danh sách
        } catch (NumberFormatException e) {
             request.setAttribute("ERROR_MESSAGE", "ID Khoa không hợp lệ khi cập nhật.");
             return ERROR_PAGE;
        }
    }

     /**
     * Xử lý logic xóa một Khoa.
     */
    private String deleteKhoa(HttpServletRequest request) {
         try {
            int id = Integer.parseInt(request.getParameter("id"));
            khoaService.deleteKhoa(id);
            // Thông báo thành công sẽ được set ở doGet
            return KHOA_LIST_PAGE; // Trả về trang list để doGet load lại
        } catch (NumberFormatException e) {
             request.setAttribute("ERROR_MESSAGE", "ID Khoa không hợp lệ khi xóa.");
             return ERROR_PAGE;
        } catch (Exception e) {
             // Bắt lỗi nếu không xóa được (ví dụ do ràng buộc khóa ngoại mà CSDL không xử lý)
            log("Lỗi khi xóa khoa: " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Xóa khoa thất bại: " + e.getMessage());
            return KHOA_LIST_PAGE; // Vẫn quay về trang list nhưng có lỗi
        }
    }

    /**
     * Hàm tiện ích tạo KhoaDTO từ request.
     */
    private KhoaDTO createDTOFromRequest(HttpServletRequest request) {
         KhoaDTO dto = new KhoaDTO();
         // Lấy cả ID (dù là tạo mới hay cập nhật, dùng để gửi lại form nếu lỗi)
         String idStr = request.getParameter("id");
         if(idStr != null && !idStr.isEmpty()){
             try {
                 dto.setId(Integer.parseInt(idStr));
             } catch (NumberFormatException e) { /* Bỏ qua nếu ID không hợp lệ */ }
         }
         dto.setTenKhoa(request.getParameter("tenKhoa"));
         dto.setMoTa(request.getParameter("moTa"));
         return dto;
    }

    @Override
    public String getServletInfo() {
        return "Controller quản lý các nghiệp vụ liên quan đến Khoa.";
    }
}