package controller;

import exception.ValidationException;
import java.io.IOException;
import java.time.LocalDate; // <-- **THÊM IMPORT NÀY**
import java.time.format.DateTimeParseException; // <-- **THÊM IMPORT NÀY**
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.dto.BenhNhanDTO;
import model.dto.TaiKhoanDTO;
import service.BenhNhanService; 
import service.TaiKhoanService;

/**
 * Servlet này xử lý các nghiệp vụ bảo mật nhạy cảm.
 * **ĐÃ CẬP NHẬT:** Hoàn thành logic "Sửa Khó" (Phone, CCCD, Name, DOB).
 */
@WebServlet(name = "SecurityController", urlPatterns = {"/SecurityController"})
public class SecurityController extends HttpServlet {

    private final TaiKhoanService taiKhoanService = new TaiKhoanService();
    private final BenhNhanService benhNhanService = new BenhNhanService(); 

    // Hằng số cho các action
    private static final String ACTION_SHOW_CONFIRM = "showConfirmPassword";
    private static final String ACTION_CONFIRM_PASSWORD = "confirmPassword";
    
    // --- **CẬP NHẬT** ---
    private static final String ACTION_SHOW_EDIT_PHONE = "showEditPhone";
    private static final String ACTION_SAVE_PHONE = "savePhone";
    private static final String ACTION_SHOW_EDIT_CCCD = "showEditCCCD"; 
    private static final String ACTION_SAVE_CCCD = "saveCCCD"; 
    private static final String ACTION_SHOW_EDIT_NAME = "showEditName"; // <-- MỚI
    private static final String ACTION_SAVE_NAME = "saveName"; // <-- MỚI
    private static final String ACTION_SHOW_EDIT_DOB = "showEditDOB"; // <-- MỚI
    private static final String ACTION_SAVE_DOB = "saveDOB"; // <-- MỚI
    // --- **KẾT THÚC CẬP NHẬT** ---

    // Hằng số cho các trang JSP
    private static final String CONFIRM_FORM_PAGE = "confirmPassword.jsp";
    private static final String EDIT_PHONE_PAGE = "editPhone.jsp"; 
    private static final String EDIT_CCCD_PAGE = "editCCCD.jsp"; 
    private static final String EDIT_NAME_PAGE = "editName.jsp"; // <-- MỚI
    private static final String EDIT_DOB_PAGE = "editDOB.jsp"; // <-- MỚI
    private static final String PROFILE_PAGE_ACTION = "MainController?action=showProfile"; 
    private static final String LOGIN_PAGE = "login.jsp";
    private static final String ERROR_PAGE = "error.jsp";

    
    /**
     * Xử lý GET: Hiển thị form xác thực, hoặc form sửa đổi.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        
        String action = request.getParameter("action");
        String url = ERROR_PAGE;
        HttpSession session = request.getSession(false); 
        
        try {
            if (session == null || session.getAttribute("USER") == null) {
                throw new ValidationException("Bạn cần đăng nhập để thực hiện việc này.");
            }
            TaiKhoanDTO currentUser = (TaiKhoanDTO) session.getAttribute("USER");
            BenhNhanDTO benhNhan; // Khai báo 1 lần

            if (ACTION_SHOW_CONFIRM.equals(action)) {
                // Hiển thị form 'confirmPassword.jsp'
                String nextAction = request.getParameter("next");
                if (nextAction == null || nextAction.trim().isEmpty()) {
                    throw new ValidationException("Không rõ hành động tiếp theo.");
                }
                request.setAttribute("nextAction", nextAction);
                url = CONFIRM_FORM_PAGE;
                
            } 
            else if (ACTION_SHOW_EDIT_PHONE.equals(action)) {
                validateSensitiveEditAccess(session); // Kiểm tra "vé"
                session.removeAttribute("SENSITIVE_EDIT_ALLOWED"); // Xóa "vé"
                
                benhNhan = benhNhanService.getBenhNhanByTaiKhoanId(currentUser.getId());
                if (benhNhan == null) { throw new Exception("Không tìm thấy hồ sơ bệnh nhân."); }
                
                request.setAttribute("currentPhone", benhNhan.getSoDienThoai());
                url = EDIT_PHONE_PAGE;
            } 
            else if (ACTION_SHOW_EDIT_CCCD.equals(action)) {
                validateSensitiveEditAccess(session); 
                session.removeAttribute("SENSITIVE_EDIT_ALLOWED");
                
                benhNhan = benhNhanService.getBenhNhanByTaiKhoanId(currentUser.getId());
                if (benhNhan == null) { throw new Exception("Không tìm thấy hồ sơ bệnh nhân."); }
                
                request.setAttribute("currentCCCD", benhNhan.getCccd());
                url = EDIT_CCCD_PAGE;
            } 
            // --- **BẮT ĐẦU THÊM MỚI (doGet)** ---
            else if (ACTION_SHOW_EDIT_NAME.equals(action)) {
                validateSensitiveEditAccess(session); 
                session.removeAttribute("SENSITIVE_EDIT_ALLOWED");
                
                benhNhan = benhNhanService.getBenhNhanByTaiKhoanId(currentUser.getId());
                if (benhNhan == null) { throw new Exception("Không tìm thấy hồ sơ bệnh nhân."); }
                
                request.setAttribute("currentName", benhNhan.getHoTen());
                url = EDIT_NAME_PAGE;
            }
            else if (ACTION_SHOW_EDIT_DOB.equals(action)) {
                validateSensitiveEditAccess(session); 
                session.removeAttribute("SENSITIVE_EDIT_ALLOWED");
                
                benhNhan = benhNhanService.getBenhNhanByTaiKhoanId(currentUser.getId());
                if (benhNhan == null) { throw new Exception("Không tìm thấy hồ sơ bệnh nhân."); }
                
                request.setAttribute("currentDOB", benhNhan.getNgaySinh());
                url = EDIT_DOB_PAGE;
            }
            // --- **KẾT THÚC THÊM MỚI** ---
            else {
                request.setAttribute("ERROR_MESSAGE", "Hành động không hợp lệ.");
            }
            
        } catch (ValidationException e) {
            request.setAttribute("ERROR_MESSAGE", e.getMessage());
            url = LOGIN_PAGE; 
        } catch (Exception e) {
            log("Lỗi SecurityController (doGet): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Lỗi hệ thống nghiêm trọng.");
        }
        
        request.getRequestDispatcher(url).forward(request, response);
    }

    /**
     * Xử lý POST: Submit mật khẩu, hoặc submit SĐT/CCCD/Tên/Ngày sinh mới.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");

        String action = request.getParameter("action");
        String url = ERROR_PAGE;
        boolean isRedirect = false; 
        BenhNhanDTO benhNhan = null; // Khai báo 1 lần
        
        try {
            HttpSession session = request.getSession(false);
            if (session == null || session.getAttribute("USER") == null) {
                throw new ValidationException("Phiên làm việc đã hết hạn. Vui lòng đăng nhập lại.");
            }
            TaiKhoanDTO currentUser = (TaiKhoanDTO) session.getAttribute("USER");

            if (ACTION_CONFIRM_PASSWORD.equals(action)) {
                // --- KỊCH BẢN 1: USER SUBMIT MẬT KHẨU ---
                
                String nextAction = request.getParameter("nextAction"); 
                String password = request.getParameter("password");
                
                taiKhoanService.login(currentUser.getTenDangNhap(), password);
                
                session.setAttribute("SENSITIVE_EDIT_ALLOWED", true); 
                
                switch (nextAction) {
                    case "edit_phone":
                        url = "SecurityController?action=" + ACTION_SHOW_EDIT_PHONE;
                        break;
                    case "edit_cccd":
                        url = "SecurityController?action=" + ACTION_SHOW_EDIT_CCCD;
                        break;
                    // --- **BẮT ĐẦU THÊM MỚI (confirmPassword)** ---
                    case "edit_name":
                        url = "SecurityController?action=" + ACTION_SHOW_EDIT_NAME;
                        break;
                    case "edit_dob":
                        url = "SecurityController?action=" + ACTION_SHOW_EDIT_DOB;
                        break;
                    // --- **KẾT THÚC THÊM MỚI** ---
                    default:
                        throw new ValidationException("Hành động '" + nextAction + "' không được hỗ trợ.");
                }
                
                isRedirect = true; 

            } 
            else if (ACTION_SAVE_PHONE.equals(action)) {
                // --- KỊCH BẢN 2: USER SUBMIT SĐT MỚI ---
                String newPhone = request.getParameter("newPhone");
                benhNhan = benhNhanService.getBenhNhanByTaiKhoanId(currentUser.getId());
                if (benhNhan == null) { throw new Exception("Không tìm thấy hồ sơ bệnh nhân."); }
                
                benhNhanService.updateSoDienThoai(benhNhan.getId(), newPhone);
                
                session.setAttribute("SUCCESS_MESSAGE", "Cập nhật số điện thoại thành công!");
                url = PROFILE_PAGE_ACTION;
                isRedirect = true;
                
            } 
            else if (ACTION_SAVE_CCCD.equals(action)) {
                // --- KỊCH BẢN 3: USER SUBMIT CCCD MỚI ---
                String newCCCD = request.getParameter("newCCCD");
                benhNhan = benhNhanService.getBenhNhanByTaiKhoanId(currentUser.getId());
                if (benhNhan == null) { throw new Exception("Không tìm thấy hồ sơ bệnh nhân."); }
                
                benhNhanService.updateCCCD(benhNhan.getId(), newCCCD);
                
                session.setAttribute("SUCCESS_MESSAGE", "Cập nhật CCCD thành công!");
                url = PROFILE_PAGE_ACTION;
                isRedirect = true;
            }
            // --- **BẮT ĐẦU THÊM MỚI (doPost)** ---
            else if (ACTION_SAVE_NAME.equals(action)) {
                // --- KỊCH BẢN 4: USER SUBMIT HỌ TÊN MỚI ---
                String newName = request.getParameter("newName");
                benhNhan = benhNhanService.getBenhNhanByTaiKhoanId(currentUser.getId());
                if (benhNhan == null) { throw new Exception("Không tìm thấy hồ sơ bệnh nhân."); }
                
                benhNhanService.updateHoTen(benhNhan.getId(), newName);
                
                session.setAttribute("SUCCESS_MESSAGE", "Cập nhật họ tên thành công!");
                url = PROFILE_PAGE_ACTION;
                isRedirect = true;
            }
            else if (ACTION_SAVE_DOB.equals(action)) {
                // --- KỊCH BẢN 5: USER SUBMIT NGÀY SINH MỚI ---
                String newDOB_String = request.getParameter("newDOB");
                LocalDate newDOB;
                try {
                    newDOB = LocalDate.parse(newDOB_String);
                } catch (DateTimeParseException e) {
                    throw new ValidationException("Định dạng ngày sinh không hợp lệ.");
                }

                benhNhan = benhNhanService.getBenhNhanByTaiKhoanId(currentUser.getId());
                if (benhNhan == null) { throw new Exception("Không tìm thấy hồ sơ bệnh nhân."); }
                
                benhNhanService.updateNgaySinh(benhNhan.getId(), newDOB);
                
                session.setAttribute("SUCCESS_MESSAGE", "Cập nhật ngày sinh thành công!");
                url = PROFILE_PAGE_ACTION;
                isRedirect = true;
            }
            // --- **KẾT THÚC THÊM MỚI** ---
            else {
                throw new ValidationException("Hành động không hợp lệ.");
            }
            
        } catch (ValidationException e) {
            log("Lỗi Validation tại SecurityController (doPost): " + e.getMessage());
            request.setAttribute("ERROR_MESSAGE", e.getMessage());
            
            // Lấy SĐT/CCCD/Tên/Ngày sinh CŨ để gửi lại cho form lỗi
            try {
                 benhNhan = benhNhanService.getBenhNhanByTaiKhoanId(((TaiKhoanDTO)request.getSession().getAttribute("USER")).getId());
            } catch(Exception ex) {}

            // Quyết định forward về form nào
            if (ACTION_CONFIRM_PASSWORD.equals(action)) {
                request.setAttribute("nextAction", request.getParameter("nextAction"));
                url = CONFIRM_FORM_PAGE; 
            } else if (ACTION_SAVE_PHONE.equals(action)) {
                if(benhNhan != null) request.setAttribute("currentPhone", benhNhan.getSoDienThoai());
                url = EDIT_PHONE_PAGE;
            } 
            else if (ACTION_SAVE_CCCD.equals(action)) {
                if(benhNhan != null) request.setAttribute("currentCCCD", benhNhan.getCccd());
                url = EDIT_CCCD_PAGE;
            }
            // --- **BẮT ĐẦU THÊM MỚI (Xử lý lỗi)** ---
            else if (ACTION_SAVE_NAME.equals(action)) {
                if(benhNhan != null) request.setAttribute("currentName", benhNhan.getHoTen());
                url = EDIT_NAME_PAGE;
            }
            else if (ACTION_SAVE_DOB.equals(action)) {
                if(benhNhan != null) request.setAttribute("currentDOB", benhNhan.getNgaySinh());
                url = EDIT_DOB_PAGE;
            }
            // --- **KẾT THÚC THÊM MỚI** ---
            
        } catch (Exception e) {
            log("Lỗi Hệ thống tại SecurityController (doPost): " + e.getMessage(), e);
            request.setAttribute("ERROR_MESSAGE", "Lỗi hệ thống nghiêm trọng.");
            url = ERROR_PAGE;
        }
        
        if (isRedirect) {
            response.sendRedirect(url);
        } else {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }
    
    /**
     * **HÀM HELPER:** Kiểm tra "vé thông hành" trong session.
     */
    private void validateSensitiveEditAccess(HttpSession session) throws ValidationException {
        Boolean allowed = (Boolean) session.getAttribute("SENSITIVE_EDIT_ALLOWED");
        if (allowed == null || !allowed) {
            // Người dùng cố gắng truy cập link trực tiếp mà không qua xác thực
            throw new ValidationException("Bạn chưa xác thực. Vui lòng thử lại từ trang hồ sơ.");
        }
    }
}