package controller;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet điều hướng chính (Front Controller). Chịu trách nhiệm nhận tất cả
 * request và chuyển đến Controller phù hợp dựa vào tham số 'action'.
 *
 * @author tungi (đã chỉnh sửa)
 */
@WebServlet(name = "MainController", urlPatterns = {"/MainController"})
public class MainController extends HttpServlet {

    // --- Khai báo URL Controller ---
    private static final String LOGIN_PAGE = "login.jsp"; // Trang mặc định nếu action không hợp lệ
    private static final String USER_CONTROLLER = "UserController";
    private static final String EMRCORE_CONTROLLER = "EMRCoreController";
    private static final String KHOA_CONTROLLER = "KhoaController";
    private static final String NHANVIEN_CONTROLLER = "NhanVienController";
    private static final String BENHNHAN_CONTROLLER = "BenhNhanController";
    private static final String LICHHEN_CONTROLLER = "LichHenController";
    private static final String CATALOG_CONTROLLER = "CatalogController"; // Quản lý Dịch vụ & Thuốc
    private static final String PHONG_BENH_CONTROLLER = "PhongBenhController";
    private static final String GIUONG_BENH_CONTROLLER = "GiuongBenhController";
    private static final String DON_THUOC_CONTROLLER = "DonThuocController";
    private static final String HD_GDTT_CONTROLLER = "HoaDon_GiaoDichThanhToanController";
    private static final String THONG_BAO_CONTROLLER = "ThongBaoController";
    private static final String USER_THONG_BAO_CONTROLLER = "UserThongBaoController";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8"); // Đảm bảo xử lý tiếng Việt

        // 1. Lấy action từ request
        String action = request.getParameter("action");
        String url = LOGIN_PAGE; // Mặc định chuyển về trang login nếu action không hợp lệ

        // 2. Nhóm các action cho từng controller
        String[] EMRCoreActions = {"printEncounter", "completeEncounter", "createEncounter", "updateEncounterDetails", "getEncounterDetails", "showCreateEncounterForm", "listAllEncounters", "viewEncounterDetails", "addServiceRequest", "updateServiceResult", "showUpdateEncounterForm", "updateEncounter"};
        String[] CatalogActions = {"createService", "showCreateServiceForm", "createMedication", "showMedicationForm", "showUpdateForm", "updateMedicationInfo", "updateStock", "listMedications", "deleteMedication", "listAndSearchServices", "updateService", "showUpdateServiceForm", "deleteService"};

        String[] lichHenActions = {"listLichHen", "showLichHenCreateForm", "createLichHen", "updateLichHenStatus", "showCreateAppointmentForm", "createAppointment"};

        // (userActions của bạn đã có "verify", nhưng logic trong UserController đã bị vô hiệu hóa, nên vẫn an toàn)
        String[] userActions = {"login", "logout", "listUsers",
            "showUserCreateForm", "createUser",
            "showUserEditForm", "updateUserStatus",
            "showChangePasswordForm", "changePassword", "register", "verify"};

        String[] khoaActions = {"listKhoa", "showKhoaCreateForm", "createKhoa",
            "showKhoaEditForm", "updateKhoa", "deleteKhoa"};

        String[] nhanVienActions = {"listNhanVien", "showNhanVienCreateForm", "createNhanVien",
            "showNhanVienEditForm", "updateNhanVien", "deleteNhanVien"};

        // --- **BẮT ĐẦU SỬA (Giai đoạn 2 - Bước 5/5)** ---
        // Thêm 2 action mới cho luồng nghiệp vụ "Hoàn tất Hồ sơ"
        String[] benhNhanActions = {"listBenhNhan", "showBenhNhanCreateForm", "createBenhNhan",
            "showBenhNhanEditForm", "updateBenhNhan", "deleteBenhNhan",
            "updateProfile", // (Action này bạn đã có)
            "showProfile", // <-- THÊM ACTION NÀY (cho doGet)
            "saveProfile"};  // <-- THÊM ACTION NÀY (cho doPost)
        // --- **KẾT THÚC SỬA** ---

        String[] lichHenActions = {"listLichHen", "showLichHenCreateForm", "createLichHen",
            "updateLichHenStatus"};

        String[] DonThuocActions = {"addDetail", "updateDetail", "deleteDetail", "viewDetails", "listAll", "showCreateDonThuocForm", "createPrescription"};

        String[] phongBenhActions = {"createRoom", "listRooms", "updateRoom", "getRoomForUpdate", "deleteRoom", "showCreateRoomForm"};

        String[] giuongBenhActions = {"assignBed", "releaseBed", "listBeds", "createBed", "deleteBed", "updateBed", "getBedForUpdate"};

        String[] hoaDon_GiaoDichThanhToanActions = {"viewInvoice", "payInvoice", "listInvoices", "generateInvoice"};

        String[] thongBaoActions = {"createThongBao", "listNotifications"};

        String[] userThongBaoActions = {"viewMyNotifications", "markNotificationAsRead", "deleteMyNotification"};

        // 3. Điều hướng dựa trên action
        if (action == null || action.isEmpty()) {
            url = LOGIN_PAGE;
        } else if (Arrays.asList(userActions).contains(action)) {
            url = USER_CONTROLLER;
        } else if (Arrays.asList(EMRCoreActions).contains(action)) {
            url = EMRCORE_CONTROLLER;
        } else if (Arrays.asList(khoaActions).contains(action)) {
            url = KHOA_CONTROLLER;
        } else if (Arrays.asList(nhanVienActions).contains(action)) {
            url = NHANVIEN_CONTROLLER;
        } else if (Arrays.asList(benhNhanActions).contains(action)) {
            // **CẬP NHẬT:** Giờ đây 'showProfile' và 'saveProfile' sẽ được chuyển đến BenhNhanController
            url = BENHNHAN_CONTROLLER;
        } else if (Arrays.asList(lichHenActions).contains(action)) {
            url = LICHHEN_CONTROLLER;
        } else if (Arrays.asList(CatalogActions).contains(action)) {
            url = CATALOG_CONTROLLER;
        } else if (Arrays.asList(phongBenhActions).contains(action)) {
            url = PHONG_BENH_CONTROLLER;
        } else if (Arrays.asList(giuongBenhActions).contains(action)) {
            url = GIUONG_BENH_CONTROLLER;
        } else if (Arrays.asList(DonThuocActions).contains(action)) {
            url = DON_THUOC_CONTROLLER;
        } else if (Arrays.asList(hoaDon_GiaoDichThanhToanActions).contains(action)) {
            url = HD_GDTT_CONTROLLER;
        } else if (Arrays.asList(thongBaoActions).contains(action)) {
            url = THONG_BAO_CONTROLLER;
        } else if (Arrays.asList(userThongBaoActions).contains(action)) {
            url = USER_THONG_BAO_CONTROLLER;
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
