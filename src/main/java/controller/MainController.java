package controller;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet điều hướng chính (Front Controller).
 * **ĐÃ CẬP NHẬT:** Đã merge (kết hợp) các tính năng Auth và tính năng Main.
 *
 * @author tungi (đã chỉnh sửa)
 */
@WebServlet(name = "MainController", urlPatterns = {"/MainController"})
public class MainController extends HttpServlet {

    // --- Khai báo URL Controller ---
    private static final String LOGIN_PAGE = "login.jsp";
    private static final String USER_CONTROLLER = "UserController";
    private static final String EMRCORE_CONTROLLER = "EMRCoreController";
    private static final String KHOA_CONTROLLER = "KhoaController";
    private static final String NHANVIEN_CONTROLLER = "NhanVienController";
    private static final String BENHNHAN_CONTROLLER = "BenhNhanController";
    private static final String LICHHEN_CONTROLLER = "LichHenController";
    private static final String CATALOG_CONTROLLER = "CatalogController"; 
    private static final String PHONG_BENH_CONTROLLER = "PhongBenhController";
    private static final String GIUONG_BENH_CONTROLLER = "GiuongBenhController";
    private static final String DON_THUOC_CONTROLLER = "DonThuocController";
    private static final String HD_GDTT_CONTROLLER = "HoaDon_GiaoDichThanhToanController"; 
    private static final String THONG_BAO_CONTROLLER = "ThongBaoController";
    private static final String USER_THONG_BAO_CONTROLLER = "UserThongBaoController";
    private static final String PATIENT_LICH_HEN_CONTROLLER = "PatientLichHenController";

    // --- (Các Controller từ nhánh Auth của bạn) ---
    private static final String VERIFY_CONTROLLER = "VerifyController";
    private static final String SECURITY_CONTROLLER = "SecurityController";
    private static final String RESET_CONTROLLER = "PasswordResetController";


    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8"); 

        String action = request.getParameter("action");
        String url = LOGIN_PAGE; 

        // 2. Nhóm các action cho từng controller
        
        String[] EMRCoreActions = {"printEncounter", "completeEncounter", "createEncounter", "updateEncounterDetails", "getEncounterDetails", "showCreateEncounterForm", "listAllEncounters", "viewEncounterDetails", "addServiceRequest", "updateServiceResult", "showUpdateEncounterForm", "updateEncounter"};
        String[] CatalogActions = {"createService", "showCreateServiceForm", "createMedication", "showMedicationForm", "showUpdateForm", "updateMedicationInfo", "updateStock", "listMedications", "deleteMedication", "listAndSearchServices", "updateService", "showUpdateServiceForm", "deleteService"};
        
        // **MERGE:** Lấy 'lichHenActions' từ nhánh 'main' (vì nó đầy đủ hơn)
        String[] lichHenActions = {"listLichHen", "showLichHenCreateForm", "createLichHen", "updateLichHenStatus", "showCreateAppointmentForm", "createAppointment"};

        // **MERGE:** Lấy 'userActions' từ nhánh của bạn (vì nó có logic Auth mới)
        String[] userActions = {"login", "logout", "listUsers",
            "showUserCreateForm", "createUser",
            "showUserEditForm", "updateUserStatus",
            "showChangePasswordForm", "changePassword",
            "register", "resendVerification" 
            // "verify" đã được chuyển đi
        };

        String[] khoaActions = {"listKhoa", "showKhoaCreateForm", "createKhoa",
            "showKhoaEditForm", "updateKhoa", "deleteKhoa"};
        String[] nhanVienActions = {"listNhanVien", "showNhanVienCreateForm", "createNhanVien",
            "showNhanVienEditForm", "updateNhanVien", "deleteNhanVien"};
        String[] DonThuocActions = {"addDetail", "updateDetail", "deleteDetail", "viewDetails", "listAll", "showCreateDonThuocForm", "createPrescription"};

        // **MERGE:** Lấy 'benhNhanActions' từ nhánh của bạn (vì nó có logic hồ sơ mới)
        String[] benhNhanActions = {"listBenhNhan", "showBenhNhanCreateForm", "createBenhNhan",
            "showBenhNhanEditForm", "updateBenhNhan", "deleteBenhNhan",
            "showProfile",     // (Xem hồ sơ)
            "showEditProfile", // (Sửa hồ sơ)
            "saveProfile",     // (Lưu hồ sơ)
            "confirmAndLink",  // (Nút "Liên kết ngay")
            "showEditProfileWithExisting", // (Nút "Cần cập nhật")
            "updateAndLink"    // (Lưu sau khi "Cần cập nhật")
        };
        
        // **MERGE:** Lấy các mảng mới từ nhánh 'main'
        String[] phongBenhActions = {"createRoom", "listRooms", "updateRoom", "getRoomForUpdate", "deleteRoom", "showCreateRoomForm"};
        String[] giuongBenhActions = {"assignBed", "releaseBed", "listBeds", "createBed", "deleteBed", "updateBed", "getBedForUpdate", "showCreateBedForm"};
        String[] hoaDon_GiaoDichThanhToanActions = {"viewInvoice", "payInvoice", "listInvoices", "generateInvoice", "printInvoice"};
        String[] thongBaoActions = {"createThongBao", "listNotifications"};
        String[] userThongBaoActions = {"viewMyNotifications", "markNotificationAsRead", "deleteMyNotification"};
        String[] patientLichHenActions = {"myAppointments", "showPatientBookingForm", "bookAppointment", "getBacSiByKhoa", "cancelAppointment"};
        String[] verifyActions = {"verify"}; // Chỉ xử lý 'verify'
        String[] resetActions = {"requestReset", "performReset"}; // Chỉ xử lý 'Quên MK'
        String[] securityActions = {"showConfirmPassword", "confirmPassword",
            "showEditPhone", "savePhone",
            "showEditCCCD", "saveCCCD",
            "showEditName", "saveName",
            "showEditDOB", "saveDOB"
        };
        
        
        if (action == null || action.isEmpty()) {
            url = LOGIN_PAGE;
        } 
        // --- (Auth features - từ nhánh của bạn) ---
        else if (Arrays.asList(userActions).contains(action)) {
            url = USER_CONTROLLER;
        } else if (Arrays.asList(verifyActions).contains(action)) {
            url = VERIFY_CONTROLLER; 
        } else if (Arrays.asList(resetActions).contains(action)) {
            url = RESET_CONTROLLER; 
        } else if (Arrays.asList(securityActions).contains(action)) {
            url = SECURITY_CONTROLLER; 
        } 
        // --- (Feature features - từ cả 2 nhánh) ---
        else if (Arrays.asList(benhNhanActions).contains(action)) {
            url = BENHNHAN_CONTROLLER;
        } else if (Arrays.asList(EMRCoreActions).contains(action)) {
            url = EMRCORE_CONTROLLER;
        } else if (Arrays.asList(khoaActions).contains(action)) {
            url = KHOA_CONTROLLER;
        } else if (Arrays.asList(nhanVienActions).contains(action)) {
            url = NHANVIEN_CONTROLLER;
        } else if (Arrays.asList(lichHenActions).contains(action)) {
            url = LICHHEN_CONTROLLER;
        } else if (Arrays.asList(CatalogActions).contains(action)) {
            url = CATALOG_CONTROLLER;
        } else if (Arrays.asList(DonThuocActions).contains(action)) {
            url = DON_THUOC_CONTROLLER;
        }
        // --- (Main features - từ nhánh main) ---
         else if (Arrays.asList(phongBenhActions).contains(action)) {
            url = PHONG_BENH_CONTROLLER;
        } else if (Arrays.asList(giuongBenhActions).contains(action)) {
            url = GIUONG_BENH_CONTROLLER;
        } else if (Arrays.asList(hoaDon_GiaoDichThanhToanActions).contains(action)) {
            url = HD_GDTT_CONTROLLER;
        } else if (Arrays.asList(thongBaoActions).contains(action)) {
            url = THONG_BAO_CONTROLLER;
        } else if (Arrays.asList(userThongBaoActions).contains(action)) {
            url = USER_THONG_BAO_CONTROLLER;
        } else if (Arrays.asList(patientLichHenActions).contains(action)) {
            url = PATIENT_LICH_HEN_CONTROLLER;
        }

        // 4. Forward đến controller tương ứng
        request.getRequestDispatcher(url).forward(request, response);
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
        return "Main Front Controller for Hospital Management System";
    }
    // </editor-fold>
}