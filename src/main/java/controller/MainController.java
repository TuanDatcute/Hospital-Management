package controller;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet điều hướng chính (Front Controller). (ĐÃ SỬA LỖI MERGE CONFLICT)
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

        // (Đã thêm 'updateLichHen' mà chúng ta đã làm)
        String[] lichHenActions = {"listLichHen", "showLichHenCreateForm", "createLichHen", "updateLichHenStatus",
            "showLichHenEditForm", "updateLichHen", // <-- Logic Sửa Lịch Hẹn
            "showCreateAppointmentForm", "createAppointment"};

        String[] userActions = {"login", "logout", "listUsers",
            "showUserCreateForm", "createUser",
            "showUserEditForm", "updateUserStatus",
            "showChangePasswordForm", "changePassword",
            "register", "resendVerification"
        };

        // (Đã dùng 'softDeleteKhoa' từ các bước trước)
        String[] khoaActions = {"listKhoa", "showKhoaCreateForm", "createKhoa",
            "showKhoaEditForm", "updateKhoa", "softDeleteKhoa"};

        // (Đã dùng 'softDeleteNhanVien' từ các bước trước)
        String[] nhanVienActions = {"listNhanVien", "showNhanVienCreateForm", "createNhanVien",
            "showNhanVienEditForm", "updateNhanVien", "softDeleteNhanVien"};

        String[] DonThuocActions = {"addDetail", "updateDetail", "deleteDetail", "viewDetails", "listAll", "showCreateDonThuocForm", "createPrescription"};

        // === BẮT ĐẦU SỬA LỖI CONFLICT ===
        // Giữ lại "softDeleteBenhNhan" từ HEAD (logic Xóa Mềm mới của chúng ta)
        // và xóa "deleteBenhNhan" từ nhánh cũ.
        String[] benhNhanActions = {"listBenhNhan", "showBenhNhanCreateForm", "createBenhNhan",
            "showBenhNhanEditForm", "updateBenhNhan", "softDeleteBenhNhan",
            "showProfile", // (Xem hồ sơ)
            "showEditProfile", // (Sửa hồ sơ)
            "saveProfile", // (Lưu hồ sơ)
            "confirmAndLink", // (Nút "Liên kết ngay")
            "showEditProfileWithExisting", // (Nút "Cần cập nhật")
            "updateAndLink" // (Lưu sau khi "Cần cập nhật")
    };
        // === KẾT THÚC SỬA LỖI CONFLICT ===

        String[] phongBenhActions = {"createRoom", "listRooms", "updateRoom", "getRoomForUpdate", "deleteRoom", "showCreateRoomForm"};
        String[] giuongBenhActions = {"assignBed", "releaseBed", "listBeds", "createBed", "deleteBed", "updateBed", "getBedForUpdate", "showCreateBedForm"};
        String[] hoaDon_GiaoDichThanhToanActions = {"viewInvoice", "payInvoice", "listInvoices", "generateInvoice", "printInvoice"};
        String[] thongBaoActions = {"createThongBao", "listNotifications"};
        String[] userThongBaoActions = {"viewMyNotifications", "markNotificationAsRead", "deleteMyNotification"};
        String[] patientLichHenActions = {"myAppointments", "showPatientBookingForm", "bookAppointment", "cancelAppointment"};
        String[] verifyActions = {"verify"};
        String[] resetActions = {"requestReset", "performReset"};
        String[] securityActions = {"showConfirmPassword", "confirmPassword",
            "showEditPhone", "savePhone",
            "showEditCCCD", "saveCCCD",
            "showEditName", "saveName",
            "showEditDOB", "saveDOB"
        };

        // (Logic AJAX của bạn giữ nguyên)
        String[] ajaxActions = {"getBacSiByKhoa"};
        if (Arrays.asList(ajaxActions).contains(action)) {
            if ("getBacSiByKhoa".equals(action)) {
                new PatientLichHenController().doGet(request, response);
                return;
            }
        }

        // (Logic điều hướng IF/ELSE IF giữ nguyên)
        if (action == null || action.isEmpty()) {
            url = LOGIN_PAGE;
        } else if (Arrays.asList(userActions).contains(action)) {
            url = USER_CONTROLLER;
        } else if (Arrays.asList(verifyActions).contains(action)) {
            url = VERIFY_CONTROLLER;
        } else if (Arrays.asList(resetActions).contains(action)) {
            url = RESET_CONTROLLER;
        } else if (Arrays.asList(securityActions).contains(action)) {
            url = SECURITY_CONTROLLER;
        } else if (Arrays.asList(benhNhanActions).contains(action)) {
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
        } else if (Arrays.asList(phongBenhActions).contains(action)) {
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

    // (Các hàm doGet, doPost, getServletInfo giữ nguyên)
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
