package controller;

import exception.ValidationException; // --- THÊM MỚI ---
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
    private static final String KHOA_FORM_PAGE = "admin/formKhoa.jsp";    // Trang form tạo/sửa Khoa
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
                // --- CẬP NHẬT: Xóa "deleteKhoa" khỏi doGet ---
                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho GET.");
                    url = ERROR_PAGE;
            }
        // --- CẬP NHẬT: Tách riêng 2 catch ---
        } catch (ValidationException ve) {
            // Lỗi nghiệp vụ (VD: getById không thấy) -> Chuyển đến trang lỗi
            log("Lỗi validation tại KhoaController (doGet): " + ve.getMessage(), ve);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi nghiệp vụ: " + ve.getMessage());
            url = ERROR_PAGE; // Hoặc có thể là trang list nếu muốn
        } catch (Exception e) {
            // Lỗi hệ thống -> Chuyển đến trang lỗi
            log("Lỗi hệ thống tại KhoaController (doGet): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
            url = ERROR_PAGE;
        // --- KẾT THÚC CẬP NHẬT ---
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
        String url = ERROR_PAGE; // Mặc định là lỗi
        boolean loadListAfterSuccess = true; // Cờ để load lại list sau khi CUD thành công

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
                // --- CẬP NHẬT: Chuyển "deleteKhoa" sang doPost ---
                case "deleteKhoa":
                    url = deleteKhoa(request);
                    break;
                // --- KẾT THÚC CẬP NHẬT ---
                default:
                    loadListAfterSuccess = false;
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho POST.");
            }
            
            // Nếu thành công (không phải trang lỗi/form) và cần load lại list
            if (loadListAfterSuccess && !url.equals(ERROR_PAGE) && !url.equals(KHOA_FORM_PAGE)) {
                url = listKhoa(request);
            }

        // --- CẬP NHẬT: Tách riêng 2 catch (Giống NhanVienController) ---
        } catch (ValidationException ve) {
            // Lỗi nghiệp vụ (Tên trống, tên trùng, regex fail...)
            log("Lỗi validation tại KhoaController (doPost): " + ve.getMessage());
            // Quay lại form với thông báo lỗi và dữ liệu đã nhập
            handleServiceException(request, ve, action); 
            url = KHOA_FORM_PAGE; 
            loadListAfterSuccess = false; // Không load lại list khi có lỗi
        } catch (Exception e) {
            // Lỗi hệ thống (Lỗi DB, NullPointerException...)
            log("Lỗi hệ thống tại KhoaController (doPost): " + e.getMessage(), e);
            // Gửi đến trang lỗi chung
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống nghiêm trọng: " + e.getMessage());
            url = ERROR_PAGE; 
            loadListAfterSuccess = false; 
        // --- KẾT THÚC CẬP NHẬT ---
        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    /**
     * Lấy danh sách Khoa và chuyển đến trang hiển thị.
     * (Giữ nguyên)
     */
    private String listKhoa(HttpServletRequest request) throws Exception {
        List<KhoaDTO> list = khoaService.getAllKhoa();
        request.setAttribute("LIST_KHOA", list);
        return KHOA_LIST_PAGE;
    }

     /**
     * Lấy thông tin Khoa cần sửa và hiển thị form.
     * (Giữ nguyên - lỗi sẽ được bắt bởi doGet)
     */
    private String showKhoaEditForm(HttpServletRequest request) throws ValidationException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            // Ném ValidationException nếu không tìm thấy
            KhoaDTO khoa = khoaService.getKhoaById(id); 
            request.setAttribute("KHOA_DATA", khoa); // Gửi DTO của khoa cần sửa
            request.setAttribute("formAction", "updateKhoa"); // Gửi action cho form biết là cập nhật
            return KHOA_FORM_PAGE;
        } catch (NumberFormatException e) {
             // Ném ValidationException để doGet bắt được và đưa ra trang lỗi
             throw new ValidationException("ID Khoa không hợp lệ.");
        }
    }

    /**
     * Xử lý logic tạo mới một Khoa.
     * (Giữ nguyên - lỗi sẽ được bắt bởi doPost)
     */
    private String createKhoa(HttpServletRequest request) throws ValidationException, Exception {
        KhoaDTO newKhoaDTO = createDTOFromRequest(request);
        KhoaDTO result = khoaService.createKhoa(newKhoaDTO);
        request.setAttribute("SUCCESS_MESSAGE", "Tạo khoa '" + result.getTenKhoa() + "' thành công!");
        return KHOA_LIST_PAGE; // Quay về trang danh sách
    }

    /**
     * Xử lý logic cập nhật một Khoa.
     * (Giữ nguyên - lỗi sẽ được bắt bởi doPost)
     */
    private String updateKhoa(HttpServletRequest request) throws ValidationException, Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id")); // Lấy ID từ hidden input trong form
            KhoaDTO khoaDTO = createDTOFromRequest(request);
            KhoaDTO result = khoaService.updateKhoa(id, khoaDTO);
            request.setAttribute("SUCCESS_MESSAGE", "Cập nhật khoa '" + result.getTenKhoa() + "' thành công!");
            return KHOA_LIST_PAGE; // Quay về trang danh sách
        } catch (NumberFormatException e) {
            // Ném ValidationException để doPost bắt được và đưa về form
            throw new ValidationException("ID Khoa không hợp lệ khi cập nhật.");
        }
    }

     /**
     * Xử lý logic xóa một Khoa. (Đã chuyển sang POST)
     */
    private String deleteKhoa(HttpServletRequest request) throws ValidationException, Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            khoaService.deleteKhoa(id);
            request.setAttribute("SUCCESS_MESSAGE", "Xóa khoa thành công!");
            return KHOA_LIST_PAGE; // Trả về trang list để doPost load lại
        } catch (NumberFormatException e) {
            // Ném ValidationException để doPost bắt được và đưa về trang lỗi
            throw new ValidationException("ID Khoa không hợp lệ khi xóa.");
        }
        // Các Exception khác (ValidationException từ Service, Exception hệ thống)
        // sẽ tự động được ném ra và bị bắt bởi doPost
    }
    
    // --- THÊM MỚI: Hàm xử lý lỗi validation (Giống NhanVienController) ---
    /**
     * Xử lý lỗi từ Service (chỉ dành cho ValidationException) và gửi lại form.
     */
    private void handleServiceException(HttpServletRequest request, ValidationException e, String formAction) {
        request.setAttribute("ERROR_MESSAGE", e.getMessage());
        // Giữ lại dữ liệu người dùng đã nhập để hiển thị lại trên form
        request.setAttribute("KHOA_DATA", createDTOFromRequest(request));
        request.setAttribute("formAction", formAction);
        // (Khoa form không cần load dependencies nên hàm này đơn giản hơn)
    }
    // --- KẾT THÚC THÊM MỚI ---


    /**
     * Hàm tiện ích tạo KhoaDTO từ request.
     * (Giữ nguyên)
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