package controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.dto.BenhNhanDTO;
import model.dto.TaiKhoanDTO;
import service.BenhNhanService;
import service.TaiKhoanService;

/**
 * Controller xử lý các nghiệp vụ liên quan đến Bệnh Nhân (Patient).
 */
@WebServlet(name = "BenhNhanController", urlPatterns = {"/BenhNhanController"})
public class BenhNhanController extends HttpServlet {

    private static final String BENHNHAN_LIST_PAGE = "admin/danhSachBenhNhan.jsp";
    private static final String BENHNHAN_FORM_PAGE = "admin/formBenhNhan.jsp";
    private static final String ERROR_PAGE = "error.jsp";

    private final BenhNhanService benhNhanService = new BenhNhanService();
    private final TaiKhoanService taiKhoanService = new TaiKhoanService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String url = BENHNHAN_LIST_PAGE;

        try {
            if (action == null || action.isEmpty()) {
                action = "listBenhNhan";
            }

            switch (action) {
                case "listBenhNhan":
                    url = listBenhNhan(request);
                    break;
                case "showBenhNhanCreateForm":
                    loadFormDependencies(request, "createBenhNhan");
                    request.setAttribute("formAction", "createBenhNhan");
                    url = BENHNHAN_FORM_PAGE;
                    break;
                case "showBenhNhanEditForm":
                    url = showBenhNhanEditForm(request);
                    break;
                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho GET.");
                    url = ERROR_PAGE;
            }
        } catch (Exception e) {
            log("Lỗi tại BenhNhanController (doGet): " + e.getMessage(), e);
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
        boolean loadListAfterSuccess = true;

        try {
            if (action == null || action.isEmpty()) {
                throw new Exception("Hành động không được chỉ định.");
            }

            switch (action) {
                case "createBenhNhan":
                    url = createBenhNhan(request);
                    break;
                case "updateBenhNhan":
                    url = updateBenhNhan(request);
                    break;
                case "deleteBenhNhan": // Action Soft Delete
                    url = softDeleteBenhNhan(request);
                    break;
                default:
                     loadListAfterSuccess = false;
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho POST.");
            }
            if (loadListAfterSuccess && !url.equals(ERROR_PAGE) && !url.equals(BENHNHAN_FORM_PAGE)) {
                 url = listBenhNhan(request);
            }

        } catch (Exception e) {
            log("Lỗi tại BenhNhanController (doPost): " + e.getMessage(), e);
            handleServiceException(request, e, action);
            url = BENHNHAN_FORM_PAGE;
            loadListAfterSuccess = false;

        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    /**
     * Lấy danh sách Bệnh nhân và chuyển đến trang hiển thị.
     */
    private String listBenhNhan(HttpServletRequest request) throws Exception {
        List<BenhNhanDTO> list = benhNhanService.getAllBenhNhan();
        request.setAttribute("LIST_BENHNHAN", list);
        return BENHNHAN_LIST_PAGE;
    }

     /**
     * Lấy thông tin Bệnh nhân cần sửa và hiển thị form.
     */
    private String showBenhNhanEditForm(HttpServletRequest request) throws Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            BenhNhanDTO benhNhan = benhNhanService.getBenhNhanById(id);
            request.setAttribute("BENHNHAN_DATA", benhNhan);
            loadFormDependencies(request, "updateBenhNhan");
            request.setAttribute("formAction", "updateBenhNhan");
             return BENHNHAN_FORM_PAGE;
        } catch (NumberFormatException e) {
             request.setAttribute("ERROR_MESSAGE", "ID Bệnh nhân không hợp lệ.");
            return ERROR_PAGE;
        }
    }

    /**
     * Xử lý logic tạo mới một Bệnh nhân.
     */
    private String createBenhNhan(HttpServletRequest request) throws Exception {
        BenhNhanDTO newBenhNhanDTO = createDTOFromRequest(request);
        BenhNhanDTO result = benhNhanService.createBenhNhan(newBenhNhanDTO);
        request.setAttribute("SUCCESS_MESSAGE", "Tạo bệnh nhân '" + result.getHoTen() + "' thành công!");
        return BENHNHAN_LIST_PAGE;
    }

    /**
     * Xử lý logic cập nhật một Bệnh nhân.
     */
    private String updateBenhNhan(HttpServletRequest request) throws Exception {
         try {
            int id = Integer.parseInt(request.getParameter("id"));
            BenhNhanDTO benhNhanDTO = createDTOFromRequest(request);
            BenhNhanDTO result = benhNhanService.updateBenhNhan(id, benhNhanDTO);
            request.setAttribute("SUCCESS_MESSAGE", "Cập nhật bệnh nhân '" + result.getHoTen() + "' thành công!");
            return BENHNHAN_LIST_PAGE;
        } catch (NumberFormatException e) {
             request.setAttribute("ERROR_MESSAGE", "ID Bệnh nhân không hợp lệ khi cập nhật.");
             return ERROR_PAGE;
        }
    }

    /**
     * Xử lý logic Soft Delete Bệnh Nhân (thông qua khóa tài khoản, nếu có).
     */
    private String softDeleteBenhNhan(HttpServletRequest request) {
         try {
            int id = Integer.parseInt(request.getParameter("id"));
            // Sử dụng hàm mới của Service để lấy BN kể cả khi TK bị khóa
            BenhNhanDTO bn = benhNhanService.getBenhNhanByIdEvenIfInactive(id);

            if (bn.getTaiKhoanId() != null && bn.getTaiKhoanId() > 0) {
                // Chỉ khóa tài khoản nếu có
                taiKhoanService.khoaTaiKhoan(bn.getTaiKhoanId());
                request.setAttribute("SUCCESS_MESSAGE", "Đã vô hiệu hóa tài khoản của bệnh nhân thành công!");
            } else {
                request.setAttribute("INFO_MESSAGE", "Bệnh nhân này không có tài khoản để vô hiệu hóa.");
            }
            return BENHNHAN_LIST_PAGE; // Trở về trang list
        } catch (NumberFormatException e) {
             request.setAttribute("ERROR_MESSAGE", "ID Bệnh nhân không hợp lệ.");
             return ERROR_PAGE;
        } catch (Exception e) {
            log("Lỗi khi vô hiệu hóa bệnh nhân: " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Vô hiệu hóa thất bại: " + e.getMessage());
             return BENHNHAN_LIST_PAGE; // Vẫn quay về trang list để báo lỗi
        }
    }

    /**
     * Tải danh sách tài khoản hoạt động, chưa gán (vai trò Bệnh nhân) cho form.
     */
    private void loadFormDependencies(HttpServletRequest request, String formAction) {
        try {
            // Gọi Service mới để lấy đúng danh sách tài khoản
            List<TaiKhoanDTO> availableTaiKhoan = taiKhoanService.getActiveAndUnassignedAccounts("BENH_NHAN");
            request.setAttribute("LIST_TAIKHOAN", availableTaiKhoan);
        } catch (Exception e) {
            log("Không thể tải danh sách tài khoản cho form Bệnh nhân: " + e.getMessage(), e);
            request.setAttribute("LOAD_FORM_ERROR", "Lỗi tải danh sách Tài khoản.");
        }
    }

     /**
     * Xử lý lỗi từ Service và gửi lại form.
     */
    private void handleServiceException(HttpServletRequest request, Exception e, String formAction) {
        request.setAttribute("ERROR_MESSAGE", e.getMessage());
        request.setAttribute("BENHNHAN_DATA", createDTOFromRequest(request));
        request.setAttribute("formAction", formAction);
        loadFormDependencies(request, formAction);
    }

    /**
     * Hàm tiện ích tạo BenhNhanDTO từ request.
     */
    private BenhNhanDTO createDTOFromRequest(HttpServletRequest request) {
         BenhNhanDTO dto = new BenhNhanDTO();
         String idStr = request.getParameter("id");
         if(idStr != null && !idStr.isEmpty()){
             try { dto.setId(Integer.parseInt(idStr)); } catch (NumberFormatException e) { /* ignore */ }
         }
         dto.setMaBenhNhan(request.getParameter("maBenhNhan"));
         dto.setHoTen(request.getParameter("hoTen"));
         dto.setGioiTinh(request.getParameter("gioiTinh"));
         dto.setDiaChi(request.getParameter("diaChi"));
         dto.setSoDienThoai(request.getParameter("soDienThoai"));
         dto.setNhomMau(request.getParameter("nhomMau"));
         dto.setTienSuBenh(request.getParameter("tienSuBenh"));

         String ngaySinhStr = request.getParameter("ngaySinh");
         if (ngaySinhStr != null && !ngaySinhStr.isEmpty()) {
             try { dto.setNgaySinh(LocalDateTime.parse(ngaySinhStr)); } catch (DateTimeParseException e) { log("Lỗi parse ngày sinh: " + ngaySinhStr); }
         }

         String taiKhoanIdStr = request.getParameter("taiKhoanId");
         if (taiKhoanIdStr != null && !taiKhoanIdStr.isEmpty() && !taiKhoanIdStr.equals("0")) {
             try { dto.setTaiKhoanId(Integer.parseInt(taiKhoanIdStr)); } catch (NumberFormatException e) { /* ignore */ }
         } else {
             dto.setTaiKhoanId(null);
         }

         return dto;
    }

    @Override
    public String getServletInfo() {
        return "Controller quản lý các nghiệp vụ liên quan đến Bệnh Nhân.";
    }
}