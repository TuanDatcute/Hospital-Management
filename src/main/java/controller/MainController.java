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
    private static final String HOA_DON_GIAO_DICH_THANH_TOAN_CONTROLLER = "HoaDon_GiaoDichThanhToanController";
    private static final String THONG_BAO_CONTROLLER = "ThongBaoController";

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8"); // Đảm bảo xử lý tiếng Việt

        // 1. Lấy action từ request
        String action = request.getParameter("action");
        String url = LOGIN_PAGE; // Mặc định về trang login

        // 2. Định nghĩa danh sách action cho từng Controller
        String[] userActions = {"login", "logout", "listUsers",
            "showUserCreateForm", "createUser",
            "showUserEditForm", "updateUserStatus",
            "showChangePasswordForm", "changePassword",
            "register"};

        String[] EMRCoreActions = {"showCreateForm", "createEncounter",
            "updateEncounterDetails", "getEncounterDetails",
            "listAllEncounters", "viewEncounterDetails", "addServiceRequest"};

        String[] khoaActions = {"listKhoa", "showKhoaCreateForm", "createKhoa",
            "showKhoaEditForm", "updateKhoa", "deleteKhoa"};

        String[] nhanVienActions = {"listNhanVien", "showNhanVienCreateForm", "createNhanVien",
            "showNhanVienEditForm", "updateNhanVien", "deleteNhanVien"};

        // --- **BẮT ĐẦU SỬA: Thêm 'updateProfile'** ---
        String[] benhNhanActions = {"listBenhNhan", "showBenhNhanCreateForm", "createBenhNhan",
            "showBenhNhanEditForm", "updateBenhNhan", "deleteBenhNhan",
            "updateProfile"}; // <-- THÊM ACTION MỚI
        // --- **KẾT THÚC SỬA** ---

        String[] lichHenActions = {"listLichHen", "showLichHenCreateForm", "createLichHen",
            "updateLichHenStatus"};

        String[] CatalogActions = {"createService", "showCreateServiceForm",
            "createMedication", "showMedicationForm", "showUpdateForm",
            "updateMedicationInfo", "updateStock", "listMedications",
            "deleteMedication"};
        
        String[] DonThuocActions = {"addDetail", "updateDetail", "deleteDetail", "viewDetails", "listAll", "showCreateDonThuocForm", "createPrescription"};

        String[] PhongBenhActions = {"createRoom", "listRooms", "updateRoom", "getRoomForUpdate", "deleteRoom"};

        String[] GiuongBenhActions = {"assignBed", "releaseBed", "listBeds", "createBed", "deleteBed", "updateBed", "getBedForUpdate"};

        String[] HoaDon_GiaoDichThanhToanActions = {"viewInvoice", "payInvoice", "listInvoices", "generateInvoice"};

        String[] ThongBaoActions = {"getThongBaoForUpdate", "createThongBao", "updateThongBao", "deleteThongBao", "listNotifications"};

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
            url = BENHNHAN_CONTROLLER;
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
        // (Nếu action không khớp, url vẫn là LOGIN_PAGE)

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