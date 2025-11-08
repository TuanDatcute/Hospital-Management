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

    private static final String KHOA_LIST_PAGE = "admin/danhSachKhoa.jsp";
    private static final String KHOA_FORM_PAGE = "admin/formKhoa.jsp";
    private static final String ERROR_PAGE = "error.jsp";
    private static final int PAGE_SIZE = 10;
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
                    url = listKhoa(request);
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
        String formErrorPage = KHOA_FORM_PAGE;

        try {
            if (action == null || action.isEmpty()) {
                throw new Exception("Hành động không được chỉ định.");
            }

            if ("softDeleteKhoa".equals(action)) { // Sửa: Đổi tên action
                formErrorPage = KHOA_LIST_PAGE;
            }

            switch (action) {
                case "createKhoa":
                    url = createKhoa(request);
                    break;
                case "updateKhoa":
                    url = updateKhoa(request);
                    break;
                case "softDeleteKhoa": // Sửa: Đổi tên action
                    url = softDeleteKhoa(request);
                    break;
                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho POST.");
                    url = ERROR_PAGE;
            }
        } catch (ValidationException ve) {
            log("Lỗi validation tại KhoaController (doPost): " + ve.getMessage());
            handleServiceException(request, ve, action);
            url = formErrorPage;
        } catch (Exception e) {
            log("Lỗi hệ thống tại KhoaController (doPost): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống nghiêm trọng: " + e.getMessage());
            url = ERROR_PAGE;
        } finally {
            if (url.startsWith("redirect:")) {
                response.sendRedirect(url.substring("redirect:".length()));
            } else {
                request.getRequestDispatcher(url).forward(request, response);
            }
        }
    }

    /**
     * (Đã Nâng cấp: Phân trang + Tìm kiếm)
     */
    private String listKhoa(HttpServletRequest request) throws Exception {
        int page = 1;
        String pageStr = request.getParameter("page");
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException e) {
                page = 1;
            }
        }
        String keyword = request.getParameter("keyword");
        List<KhoaDTO> list;
        long totalKhoa;

        if (keyword != null && !keyword.trim().isEmpty()) {
            String trimmedKeyword = keyword.trim();
            list = khoaService.searchKhoaPaginated(trimmedKeyword, page, PAGE_SIZE);
            totalKhoa = khoaService.getKhoaSearchCount(trimmedKeyword);
            request.setAttribute("searchKeyword", keyword);
        } else {
            list = khoaService.getAllKhoaPaginated(page, PAGE_SIZE);
            totalKhoa = khoaService.getKhoaCount();
        }

        long totalPages = (long) Math.ceil((double) totalKhoa / PAGE_SIZE);

        request.setAttribute("LIST_KHOA", list);
        request.setAttribute("currentPage", page);
        request.setAttribute("totalPages", totalPages);

        return KHOA_LIST_PAGE;
    }

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

    private String createKhoa(HttpServletRequest request) throws ValidationException, Exception {
        KhoaDTO newKhoaDTO = createDTOFromRequest(request);
        KhoaDTO result = khoaService.createKhoa(newKhoaDTO);
        request.getSession().setAttribute("SUCCESS_MESSAGE", "Tạo khoa '" + result.getTenKhoa() + "' thành công!");
        return "redirect:MainController?action=listKhoa";
    }

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
     * (Đã Nâng cấp: Đổi tên hàm và gọi Xóa Mềm)
     */
    private String softDeleteKhoa(HttpServletRequest request) throws ValidationException, Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            khoaService.softDeleteKhoa(id); // Đã gọi hàm Xóa Mềm mới
            request.getSession().setAttribute("SUCCESS_MESSAGE", "Vô hiệu hóa khoa thành công!");
            return "redirect:MainController?action=listKhoa";
        } catch (NumberFormatException e) {
            throw new ValidationException("ID Khoa không hợp lệ khi vô hiệu hóa.");
        }
    }

    private void handleServiceException(HttpServletRequest request, ValidationException e, String formAction) {
        request.setAttribute("ERROR_MESSAGE", e.getMessage());
        request.setAttribute("KHOA_DATA", createDTOFromRequest(request));
        request.setAttribute("formAction", formAction);
    }

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
