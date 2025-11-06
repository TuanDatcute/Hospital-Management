package controller;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet điều hướng chính (Front Controller). **ĐÃ CẬP NHẬT:** Thêm các action
 * cho luồng Xác nhận Hồ sơ.
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
    private static final String HOA_DON_GIAO_DICH_THANH_TOAN_CONTROLLER = "HoaDon_GiaoDichThanhToanController";
    private static final String THONG_BAO_CONTROLLER = "ThongBaoController";

    // --- **THÊM MỚI (Controller Xác thực)** ---
    // (Chúng ta sẽ cần 2 Controller mới cho luồng Xác thực/Reset)
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
        // (Các mảng EMRCoreActions, CatalogActions, lichHenActions... giữ nguyên)
        String[] EMRCoreActions = {"printEncounter", "completeEncounter", "createEncounter", "updateEncounterDetails", "getEncounterDetails", "showCreateEncounterForm", "listAllEncounters", "viewEncounterDetails", "addServiceRequest", "updateServiceResult", "showUpdateEncounterForm", "updateEncounter"};
        String[] CatalogActions = {"createService", "showCreateServiceForm", "createMedication", "showMedicationForm", "showUpdateForm", "updateMedicationInfo", "updateStock", "listMedications", "deleteMedication", "listAndSearchServices", "updateService", "showUpdateServiceForm", "deleteService"};
        String[] lichHenActions = {"listLichHen", "showLichHenCreateForm", "createLichHen", "updateLichHenStatus"};
        String[] khoaActions = {"listKhoa", "showKhoaCreateForm", "createKhoa",
            "showKhoaEditForm", "updateKhoa", "deleteKhoa"};
        String[] nhanVienActions = {"listNhanVien", "showNhanVienCreateForm", "createNhanVien",
            "showNhanVienEditForm", "updateNhanVien", "deleteNhanVien"};
        String[] DonThuocActions = {"addDetail", "updateDetail", "deleteDetail", "viewDetails", "listAll", "showCreateDonThuocForm", "createPrescription"};
        String[] PhongBenhActions = {"createRoom", "listRooms", "updateRoom", "getRoomForUpdate", "deleteRoom"};
        String[] GiuongBenhActions = {"assignBed", "releaseBed", "listBeds", "createBed", "deleteBed", "updateBed", "getBedForUpdate"};
        String[] HoaDon_GiaoDichThanhToanActions = {"viewInvoice", "payInvoice", "listInvoices", "generateInvoice"};
        String[] ThongBaoActions = {"getThongBaoForUpdate", "createThongBao", "updateThongBao", "deleteThongBao", "listNotifications"};

        // --- **BẮT ĐẦU CẬP NHẬT MẢNG ACTIONS** ---
        // (userActions: Thêm 'resendVerification' mà chúng ta đã tạo)
        String[] userActions = {"login", "logout", "listUsers",
            "showUserCreateForm", "createUser",
            "showUserEditForm", "updateUserStatus",
            "showChangePasswordForm", "changePassword",
            "register", "resendVerification"
    // "verify" sẽ do VerifyController xử lý (xem bên dưới)
        };

        // (benhNhanActions: Thêm các action mới của bạn)
        String[] benhNhanActions = {"listBenhNhan", "showBenhNhanCreateForm", "createBenhNhan",
            "showBenhNhanEditForm", "updateBenhNhan", "deleteBenhNhan",
            "showProfile", // (Xem hồ sơ)
            "showEditProfile", // (Sửa hồ sơ)
            "saveProfile", // (Lưu hồ sơ)
            "confirmAndLink", // (MỚI: Nút "Liên kết ngay")
            "showEditProfileWithExisting", // (MỚI: Nút "Cần cập nhật")
            "updateAndLink" // (MỚI: Lưu sau khi "Cần cập nhật")
    };

        // (Các action cho các Servlet mới)
        String[] verifyActions = {"verify"}; // Chỉ xử lý 'verify'
        String[] resetActions = {"requestReset", "performReset"}; // Chỉ xử lý 'Quên MK'
        String[] securityActions = {"showConfirmPassword", "confirmPassword",
            "showEditPhone", "savePhone",
            "showEditCCCD", "saveCCCD",
            "showEditName", "saveName",
            "showEditDOB", "saveDOB"
        };

        // --- **KẾT THÚC CẬP NHẬT MẢNG ACTIONS** ---
        // 3. Điều hướng dựa trên action
        if (action == null || action.isEmpty()) {
            url = LOGIN_PAGE;
        } else if (Arrays.asList(userActions).contains(action)) {
            url = USER_CONTROLLER;
        } // --- **THÊM ĐIỀU HƯỚNG MỚI** ---
        else if (Arrays.asList(verifyActions).contains(action)) {
            url = VERIFY_CONTROLLER; // -> VerifyController
        } else if (Arrays.asList(resetActions).contains(action)) {
            url = RESET_CONTROLLER; // -> PasswordResetController
        } else if (Arrays.asList(securityActions).contains(action)) {
            url = SECURITY_CONTROLLER; // -> SecurityController
        } // --- **KẾT THÚC THÊM MỚI** ---
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
        } else if (Arrays.asList(PhongBenhActions).contains(action)) {
            url = PHONG_BENH_CONTROLLER;
        } else if (Arrays.asList(GiuongBenhActions).contains(action)) {
            url = GIUONG_BENH_CONTROLLER;
        } else if (Arrays.asList(DonThuocActions).contains(action)) {
            url = DON_THUOC_CONTROLLER;
        } else if (Arrays.asList(HoaDon_GiaoDichThanhToanActions).contains(action)) {
            url = HOA_DON_GIAO_DICH_THANH_TOAN_CONTROLLER;
        } else if (Arrays.asList(ThongBaoActions).contains(action)) {
            url = THONG_BAO_CONTROLLER;
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
