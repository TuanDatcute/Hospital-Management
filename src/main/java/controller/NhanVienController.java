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
import model.dto.KhoaDTO;
import model.dto.NhanVienDTO;
import model.dto.TaiKhoanDTO;
import service.KhoaService;
import service.NhanVienService;
import service.TaiKhoanService;

/**
 * Controller xử lý các nghiệp vụ liên quan đến Nhân Viên (Staff).
 */
@WebServlet(name = "NhanVienController", urlPatterns = {"/NhanVienController"})
public class NhanVienController extends HttpServlet {

    // Khai báo URL cho các trang JSP
    private static final String NHANVIEN_LIST_PAGE = "admin/danhSachNhanVien.jsp";
    private static final String NHANVIEN_FORM_PAGE = "admin/formNhanVien.jsp";
    private static final String ERROR_PAGE = "error.jsp";

    // Khởi tạo các Service cần thiết
    private final NhanVienService nhanVienService = new NhanVienService();
    private final TaiKhoanService taiKhoanService = new TaiKhoanService();
    private final KhoaService khoaService = new KhoaService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String url = NHANVIEN_LIST_PAGE; // Mặc định

        try {
            if (action == null || action.isEmpty()) {
                action = "listNhanVien";
            }

            switch (action) {
                case "listNhanVien":
                    url = listNhanVien(request);
                    break;
                case "showCreateForm":
                    loadFormDependencies(request, "createNhanVien"); // Truyền action vào
                    request.setAttribute("formAction", "createNhanVien");
                    url = NHANVIEN_FORM_PAGE;
                    break;
                case "showEditForm":
                    url = showEditForm(request);
                    break;
                // Không xử lý delete bằng GET nữa
                default:
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho GET.");
                    url = ERROR_PAGE;
            }
        } catch (Exception e) {
            log("Lỗi tại NhanVienController (doGet): " + e.getMessage(), e);
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
        boolean loadListAfterSuccess = true; // Biến cờ để kiểm soát việc load lại list

        try {
            if (action == null || action.isEmpty()) {
                throw new Exception("Hành động không được chỉ định.");
            }

            switch (action) {
                case "createNhanVien":
                    url = createNhanVien(request);
                    break;
                case "updateNhanVien":
                    url = updateNhanVien(request);
                    break;
                case "deleteNhanVien": // Xử lý Soft Delete
                    url = softDeleteNhanVien(request);
                    break;
                default:
                     loadListAfterSuccess = false;
                    request.setAttribute("ERROR_MESSAGE", "Hành động '" + action + "' không hợp lệ cho POST.");
            }
             // Nếu thành công (không phải trang lỗi/form) và cần load lại list
            if (loadListAfterSuccess && !url.equals(ERROR_PAGE) && !url.equals(NHANVIEN_FORM_PAGE)) {
                 url = listNhanVien(request); // Load lại danh sách sau khi CUD thành công
            }

        } catch (Exception e) {
            log("Lỗi tại NhanVienController (doPost): " + e.getMessage(), e);
            handleServiceException(request, e, action); // Hàm xử lý lỗi validation
            url = NHANVIEN_FORM_PAGE; // Quay lại form khi có lỗi
            loadListAfterSuccess = false; // Không load lại list khi có lỗi

        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    /**
     * Lấy danh sách Nhân Viên và chuyển đến trang hiển thị.
     */
    private String listNhanVien(HttpServletRequest request) throws Exception {
        // Service đã được cập nhật để chỉ lấy nhân viên hoạt động
        List<NhanVienDTO> list = nhanVienService.getAllNhanVien();
        request.setAttribute("LIST_NHANVIEN", list);
        return NHANVIEN_LIST_PAGE;
    }

     /**
     * Lấy thông tin Nhân Viên cần sửa và hiển thị form.
     */
    private String showEditForm(HttpServletRequest request) throws Exception {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            // Service đã được cập nhật để chỉ lấy nhân viên hoạt động
            NhanVienDTO nhanVien = nhanVienService.getNhanVienById(id);
            request.setAttribute("NHANVIEN_DATA", nhanVien); // Dữ liệu nhân viên cần sửa
            loadFormDependencies(request, "updateNhanVien"); // Tải danh sách Khoa
            request.setAttribute("formAction", "updateNhanVien");
             return NHANVIEN_FORM_PAGE;
        } catch (NumberFormatException e) {
             request.setAttribute("ERROR_MESSAGE", "ID Nhân viên không hợp lệ.");
            return ERROR_PAGE;
        }
        // Exception từ Service (VD: nhân viên bị khóa) sẽ được bắt ở doGet
    }

    /**
     * Xử lý logic tạo mới một Nhân Viên.
     */
    private String createNhanVien(HttpServletRequest request) throws Exception {
        NhanVienDTO newNhanVienDTO = createDTOFromRequest(request);
        NhanVienDTO result = nhanVienService.createNhanVien(newNhanVienDTO);
        request.setAttribute("SUCCESS_MESSAGE", "Tạo nhân viên '" + result.getHoTen() + "' thành công!");
        // Trả về trang list để doPost gọi listNhanVien
        return NHANVIEN_LIST_PAGE;
    }

    /**
     * Xử lý logic cập nhật một Nhân Viên.
     */
    private String updateNhanVien(HttpServletRequest request) throws Exception {
         try {
            int id = Integer.parseInt(request.getParameter("id"));
            NhanVienDTO nhanVienDTO = createDTOFromRequest(request);
            NhanVienDTO result = nhanVienService.updateNhanVien(id, nhanVienDTO);
            request.setAttribute("SUCCESS_MESSAGE", "Cập nhật nhân viên '" + result.getHoTen() + "' thành công!");
            return NHANVIEN_LIST_PAGE;
        } catch (NumberFormatException e) {
             request.setAttribute("ERROR_MESSAGE", "ID Nhân viên không hợp lệ khi cập nhật.");
             return ERROR_PAGE;
        }
        // Exception từ Service (VD: nhân viên bị khóa) sẽ được bắt ở doPost
    }

    /**
     * Xử lý logic Soft Delete một Nhân Viên.
     */
    private String softDeleteNhanVien(HttpServletRequest request) {
         try {
            int id = Integer.parseInt(request.getParameter("id"));
            nhanVienService.softDeleteNhanVien(id);
            request.setAttribute("SUCCESS_MESSAGE", "Đã vô hiệu hóa nhân viên thành công!");
            return NHANVIEN_LIST_PAGE; // Trả về trang list để doPost load lại
        } catch (NumberFormatException e) {
             request.setAttribute("ERROR_MESSAGE", "ID Nhân viên không hợp lệ khi xóa.");
             return ERROR_PAGE;
        } catch (Exception e) {
            log("Lỗi khi vô hiệu hóa nhân viên: " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Vô hiệu hóa nhân viên thất bại: " + e.getMessage());
             // Vẫn quay về trang list để người dùng biết lỗi
             return NHANVIEN_LIST_PAGE;
        }
    }


    /**
     * Tải các dữ liệu phụ thuộc cho form (danh sách Tài khoản hoạt động chưa gán, Khoa).
     */
    private void loadFormDependencies(HttpServletRequest request, String formAction) {
        try {
            if ("createNhanVien".equals(formAction)) {
                 // TODO: Cần Service/DAO để lấy danh sách tài khoản CHƯA ĐƯỢC GÁN
                 // Tạm thời lấy tất cả tài khoản HOẠT ĐỘNG
                 List<TaiKhoanDTO> allTaiKhoan = taiKhoanService.getAllTaiKhoan();
                 List<TaiKhoanDTO> activeAndUnassignedTaiKhoan = allTaiKhoan.stream()
                         .filter(tk -> "HOAT_DONG".equals(tk.getTrangThai()))
                         // .filter(tk -> !isTaiKhoanAssigned(tk.getId())) // Hàm kiểm tra giả định
                         .collect(Collectors.toList());
                 request.setAttribute("LIST_TAIKHOAN", activeAndUnassignedTaiKhoan);
            }

            List<KhoaDTO> listKhoa = khoaService.getAllKhoa();
            request.setAttribute("LIST_KHOA", listKhoa);
        } catch (Exception e) {
            log("Không thể tải dữ liệu phụ thuộc cho form Nhân viên: " + e.getMessage(), e);
            request.setAttribute("LOAD_FORM_ERROR", "Lỗi tải danh sách Tài khoản/Khoa.");
        }
    }

     /**
     * Xử lý lỗi từ Service (thường là lỗi validation) và gửi lại form.
     */
    private void handleServiceException(HttpServletRequest request, Exception e, String formAction) {
        request.setAttribute("ERROR_MESSAGE", e.getMessage());
        request.setAttribute("NHANVIEN_DATA", createDTOFromRequest(request));
        request.setAttribute("formAction", formAction);
        loadFormDependencies(request, formAction); // Tải lại dropdowns
    }


    /**
     * Hàm tiện ích tạo NhanVienDTO từ request.
     */
    private NhanVienDTO createDTOFromRequest(HttpServletRequest request) {
         NhanVienDTO dto = new NhanVienDTO();
         String idStr = request.getParameter("id");
         if(idStr != null && !idStr.isEmpty()){
             try {
                 dto.setId(Integer.parseInt(idStr));
             } catch (NumberFormatException e) { /* ignore */ }
         }
         dto.setHoTen(request.getParameter("hoTen"));
         dto.setGioiTinh(request.getParameter("gioiTinh"));
         dto.setDiaChi(request.getParameter("diaChi"));
         dto.setSoDienThoai(request.getParameter("soDienThoai"));
         dto.setChuyenMon(request.getParameter("chuyenMon"));
         dto.setBangCap(request.getParameter("bangCap"));

         String ngaySinhStr = request.getParameter("ngaySinh");
         if (ngaySinhStr != null && !ngaySinhStr.isEmpty()) {
             try {
                 dto.setNgaySinh(LocalDateTime.parse(ngaySinhStr));
             } catch (DateTimeParseException e) {
                 log("Lỗi parse ngày sinh: " + ngaySinhStr);
                 // Có thể set attribute báo lỗi nếu cần
             }
         }

         // Chỉ lấy TaiKhoanId khi tạo mới
         String formAction = request.getParameter("action");
         if ("createNhanVien".equals(formAction)) {
             String taiKhoanIdStr = request.getParameter("taiKhoanId");
             if (taiKhoanIdStr != null && !taiKhoanIdStr.isEmpty()) {
                 try {
                     dto.setTaiKhoanId(Integer.parseInt(taiKhoanIdStr));
                 } catch (NumberFormatException e) { /* ignore */ }
             }
         } else if ("updateNhanVien".equals(formAction)) {
             // Khi update, không lấy TaiKhoanId từ request
             // Lấy từ hidden field nếu cần thiết, nhưng không nên cho sửa
             String hiddenTaiKhoanId = request.getParameter("hiddenTaiKhoanId"); // Ví dụ tên hidden field
             if (hiddenTaiKhoanId != null && !hiddenTaiKhoanId.isEmpty()) {
                 try {
                     // Chỉ set lại để gửi về form nếu có lỗi, Service không dùng cái này
                     dto.setTaiKhoanId(Integer.parseInt(hiddenTaiKhoanId));
                 } catch (NumberFormatException e) { /* ignore */ }
             }
         }


         String khoaIdStr = request.getParameter("khoaId");
         if (khoaIdStr != null && !khoaIdStr.isEmpty() && !khoaIdStr.equals("0")) {
             try {
                 dto.setKhoaId(Integer.parseInt(khoaIdStr));
             } catch (NumberFormatException e) { /* ignore */ }
         } else {
             dto.setKhoaId(null);
         }

         return dto;
    }

    @Override
    public String getServletInfo() {
        return "Controller quản lý các nghiệp vụ liên quan đến Nhân Viên.";
    }

    // (Hàm giả định để lọc tài khoản - cần implement thực tế)
    // private boolean isTaiKhoanAssigned(int taiKhoanId) {
    //     try {
    //         return nhanVienDAO.isTaiKhoanIdLinked(taiKhoanId) || benhNhanDAO.isTaiKhoanIdLinked(taiKhoanId);
    //     } catch (Exception e) {
    //         log("Lỗi kiểm tra tài khoản đã gán: " + e.getMessage());
    //         return true; // Failsafe
    //     }
    // }
}