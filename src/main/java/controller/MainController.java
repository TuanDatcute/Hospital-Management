/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(name = "MainController", urlPatterns = {"/MainController"})
public class MainController extends HttpServlet {

    // Đặt tên các trang và controller ở đây để dễ quản lý
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

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        // 1. Lấy action từ request
        String action = request.getParameter("action");
        String url = LOGIN_PAGE; // Mặc định chuyển về trang login nếu action không hợp lệ

        // 2. Nhóm các action cho từng controller

        String[] EMRCoreActions = {"createEncounter", "updateEncounterDetails", "getEncounterDetails", "showCreateForm","listAllEncounters","viewEncounterDetails"};
        String[] CatalogActions = {"createService", "showCreateServiceForm", "createMedication", "showMedicationForm", "showUpdateForm", "updateMedicationInfo", "updateStock", "listMedications", "deleteMedication"};
        String[] userActions = {"login", "logout", "listUsers", "showCreateForm", "createUser", "showEditForm", "updateUserStatus"};
        String[] khoaActions = {"listKhoa", "showCreateForm", "createKhoa", "showEditForm", "updateKhoa", "deleteKhoa"};
        String[] nhanVienActions = {"listNhanVien", "showCreateForm", "createNhanVien", "showEditForm", "updateNhanVien", "deleteNhanVien"};
        String[] benhNhanActions = {"listBenhNhan", "showCreateForm", "createBenhNhan", "showEditForm", "updateBenhNhan","deleteBenhNhan"};
        String[] lichHenActions = {"listLichHen", "showCreateForm", "createLichHen", "updateLichHenStatus"};
        String[] DonThuocActions = {"addDetail", "updateDetail", "deleteDetail", "viewDetails", "listAll", "showCreateThuocForm","createPrescription"};
        String[] PhongBenhActions = {"createRoom", "listRooms", "updateRoom", "getRoomForUpdate", "deleteRoom"};
        String[] GiuongBenhActions = {"assignBed", "releaseBed", "listBeds", "createBed", "deleteBed", "updateBed", "getBedForUpdate"};
        String[] HoaDon_GiaoDichThanhToanActions = {"viewInvoice", "payInvoice", "listInvoices", "generateInvoice"};
        String[] ThongBaoActions = {"getThongBaoForUpdate", "createThongBao", "updateThongBao", "deleteThongBao", "listNotifications"};

        // 3. Điều hướng dựa trên action
        if (action == null) {
            url = LOGIN_PAGE;
        } else if (Arrays.asList(userActions).contains(action)) {
            url = USER_CONTROLLER;
        } else if (Arrays.asList(EMRCoreActions).contains(action)) {
            url = EMRCORE_CONTROLLER;
        } else if (Arrays.asList(khoaActions).contains(action)) {
            url = KHOA_CONTROLLER;
        } else if (Arrays.asList(nhanVienActions).contains(action)) { // <-- THÊM
            url = NHANVIEN_CONTROLLER;
        } else if (Arrays.asList(benhNhanActions).contains(action)) { // <-- THÊM
            url = BENHNHAN_CONTROLLER;
        } else if (Arrays.asList(lichHenActions).contains(action)) { // <-- THÊM
            url = LICHHEN_CONTROLLER;
        } else if (Arrays.asList(PhongBenhActions).contains(action)) {
            url = PHONG_BENH_CONTROLLER;
        } else if (Arrays.asList(GiuongBenhActions).contains(action)) {
            url = GIUONG_BENH_CONTROLLER;
        } else if (Arrays.asList(CatalogActions).contains(action)) {
            url = CATALOG_CONTROLLER;
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
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
