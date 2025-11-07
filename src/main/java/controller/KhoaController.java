package controller;

import exception.ValidationException;
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
 * Controller xử lý các nghiệp vụ liên quan đến Khoa (Department). (ĐÃ NÂNG CẤP:
 * Thêm Phân trang, Tìm kiếm, và PRG Pattern, Xóa Mềm)
 */
@WebServlet(name = "KhoaController", urlPatterns = {"/KhoaController"})
public class KhoaController extends HttpServlet {

    // (Các hằng số JSP giữ nguyên)
    private static final String KHOA_LIST_PAGE = "admin/danhSachKhoa.jsp";
    private static final String KHOA_FORM_PAGE = "admin/formKhoa.jsp";
    private static final String ERROR_PAGE = "error.jsp";

    // === HẰNG SỐ MỚI CHO PHÂN TRANG ===
    private static final int PAGE_SIZE = 10; // 10 khoa mỗi trang

    private final KhoaService khoaService = new KhoaService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String url = ERROR_PAGE;

        try {
            if (action == null || action.isEmpty()) {
                action = "listKhoa";
            }

            switch (action) {
                case "listKhoa":
                    url = listKhoa(request); // <-- ĐÃ NÂNG CẤP (Tìm kiếm + Phân trang)
                    break;
                case "showKhoaCreateForm":
                    request.setAttribute("formAction", "createKhoa");
                    url = KHOA_FORM_PAGE;
                    break;
                case "showKhoaEditForm":
                    url = showKhoaEditForm(request);
                    break;
                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho GET.");
                    url = ERROR_PAGE;
            }
        } catch (ValidationException ve) {
            log("Lỗi validation tại KhoaController (doGet): " + ve.getMessage(), ve);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi nghiệp vụ: " + ve.getMessage());
            url = ERROR_PAGE;
        } catch (Exception e) {
            log("Lỗi hệ thống tại KhoaController (doGet): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống: " + e.getMessage());
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
        String formErrorPage = KHOA_FORM_PAGE; // Quay về form nếu lỗi

        try {
            if (action == null || action.isEmpty()) {
                throw new Exception("Hành động không được chỉ định.");
            }

            if ("softDeleteKhoa".equals(action)) {
                formErrorPage = KHOA_LIST_PAGE; // Lỗi xóa quay về trang list
            }

            switch (action) {
                case "createKhoa":
                    url = createKhoa(request); // Trả về redirect
                    break;
                case "updateKhoa":
                    url = updateKhoa(request); // Trả về redirect
                    break;
                case "softDeleteKhoa":
                    url = softDeleteKhoa(request); // Trả về redirect
                    break;
                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho POST.");
                    url = ERROR_PAGE;
            }

        } catch (ValidationException ve) {
            log("Lỗi validation tại KhoaController (doPost): " + ve.getMessage());
            handleServiceException(request, ve, action);
            url = formErrorPage; // Sẽ là KHOA_FORM_PAGE hoặc KHOA_LIST_PAGE
        } catch (Exception e) {
            log("Lỗi hệ thống tại KhoaController (doPost): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống nghiêm trọng: " + e.getMessage());
            url = ERROR_PAGE;
        } finally {
            // (Áp dụng PRG Pattern)
            if (url.startsWith("redirect:")) {
                response.sendRedirect(request.getContextPath() + "/" + url.substring("redirect:".length()));
            } else {
                request.getRequestDispatcher(url).forward(request, response);
            }
        }
    }

    /**
     * (ĐÃ NÂNG CẤP: Phân trang + Tìm kiếm) Lấy danh sách Khoa
     */
    private String listKhoa(HttpServletRequest request) throws Exception {
        // 1. Lấy tham số trang
        int page = 1;
        String pageStr = request.getParameter("page");
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }

        // 2. Lấy tham số tìm kiếm
        String keyword = request.getParameter("keyword");

        // 3. Khai báo biến
        List<KhoaDTO> list;
        long totalKhoa;

        // 4. Logic nghiệp vụ: Kiểm tra xem có tìm kiếm hay không
        if (keyword != null && !keyword.trim().isEmpty()) {
            // Kịch bản TÌM KIẾM
            String trimmedKeyword = keyword.trim();
            list = khoaService.searchKhoaPaginated(trimmedKeyword, page, PAGE_SIZE);
            totalKhoa = khoaService.getKhoaSearchCount(trimmedKeyword);
            request.setAttribute("searchKeyword", keyword); // <-- Gửi lại keyword
        } else {
            // Kịch bản XEM TẤT CẢ (phân trang)
            list = khoaService.getAllKhoaPaginated(page, PAGE_SIZE);
            totalKhoa = khoaService.getKhoaCount();
        }

        // 5. Tính toán và Gửi dữ liệu về JSP
        long totalPages = (long) Math.ceil((double) totalKhoa / PAGE_SIZE);

        request.setAttribute("LIST_KHOA", list);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        return KHOA_LIST_PAGE;
    }

    /**
     * (Hàm showKhoaEditForm giữ nguyên)
     */
    private String showKhoaEditForm(HttpServletRequest request) throws ValidationException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            KhoaDTO khoa = khoaService.getKhoaById(id);
            request.setAttribute("KHOA_DATA", khoa);
            request.setAttribute("formAction", "updateKhoa");
            return KHOA_FORM_PAGE;
        } catch (NumberFormatException e) {
            throw new ValidationException("ID Khoa không hợp lệ.");
        }
    }

    /**
     * (Đã NÂNG CẤP: Dùng PRG)
     */
    private String createKhoa(HttpServletRequest request) throws ValidationException, Exception {
        KhoaDTO newKhoaDTO = createDTOFromRequest(request);
        KhoaDTO result = khoaService.createKhoa(newKhoaDTO);
        request.getSession().setAttribute("SUCCESS_MESSAGE", "Tạo khoa '" + result.getTenKhoa() + "' thành công!");
        return "redirect:MainController?action=listKhoa";
    }

    /**
     * (Đã NÂNG CẤP: Dùng PRG)
     */
    private String updateKhoa(HttpServletRequest request) throws ValidationException, Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            KhoaDTO khoaDTO = createDTOFromRequest(request);
            KhoaDTO result = khoaService.updateKhoa(id, khoaDTO);
            request.getSession().setAttribute("SUCCESS_MESSAGE", "Cập nhật khoa '" + result.getTenKhoa() + "' thành công!");
            return "redirect:MainController?action=listKhoa";
        } catch (NumberFormatException e) {
            throw new ValidationException("ID Khoa không hợp lệ khi cập nhật.");
        }
    }

    /**
     * (Đã NÂNG CẤP: Dùng PRG và gọi Xóa Mềm)
     */
    private String softDeleteKhoa(HttpServletRequest request) throws ValidationException, Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            khoaService.softDeleteKhoa(id); // Service đã gọi Xóa Mềm
            request.getSession().setAttribute("SUCCESS_MESSAGE", "Vô hiệu hóa khoa thành công!");
            return "redirect:MainController?action=listKhoa";
        } catch (NumberFormatException e) {
            throw new ValidationException("ID Khoa không hợp lệ khi vô hiệu hóa.");
        }
    }

    // (Hàm handleServiceException giữ nguyên)
    private void handleServiceException(HttpServletRequest request, ValidationException e, String formAction) {
        request.setAttribute("ERROR_MESSAGE", e.getMessage());
        request.setAttribute("KHOA_DATA", createDTOFromRequest(request));
        request.setAttribute("formAction", formAction);
    }

    // (Hàm createDTOFromRequest giữ nguyên)
    private KhoaDTO createDTOFromRequest(HttpServletRequest request) {
        KhoaDTO dto = new KhoaDTO();
        String idStr = request.getParameter("id");
        if (idStr != null && !idStr.isEmpty()) {
            try {
                dto.setId(Integer.parseInt(idStr));
            } catch (NumberFormatException e) {
                /* Bỏ qua */ }
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
