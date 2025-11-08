package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import model.dto.ChiDinhDichVuDTO;
import model.dto.ChiTietDonThuocDTO;
import service.HoaDonService;
import service.GiaoDichThanhToanService;
import service.PhieuKhamBenhService;
import model.dto.HoaDonDTO;
import model.dto.GiaoDichThanhToanDTO;
import model.dto.PhieuKhamBenhDTO;
import service.ChiDinhDichVuService;
import service.DonThuocService;

@WebServlet(name = "HoaDon_GiaoDichThanhToanController", urlPatterns = {"/HoaDon_GiaoDichThanhToanController"})
public class HoaDon_GiaoDichThanhToanController extends HttpServlet {

    private HoaDonService hoaDonService;
    private GiaoDichThanhToanService giaoDichThanhToanService;
    private PhieuKhamBenhService phieuKhamBenhService;
    private ChiDinhDichVuService chiDinhDichVuService;
    private DonThuocService donThuocService;

    @Override
    public void init() {
        this.hoaDonService = new HoaDonService();
        this.giaoDichThanhToanService = new GiaoDichThanhToanService();
        this.phieuKhamBenhService = new PhieuKhamBenhService();
        this.chiDinhDichVuService = new ChiDinhDichVuService();
        this.donThuocService = new DonThuocService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            action = "listInvoices";
        }

        switch (action) {
            case "viewInvoice":
                showInvoiceDetails(request, response);
                break;

            // ================== MỚI (Khớp với JSP) ==================
            case "printInvoice":
                showPrintableInvoice(request, response);
                break;
            // ================== HẾT MỚI ==================

            case "listInvoices":
            default:
                listAllInvoices(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        switch (action) {
            case "payInvoice": // (Khớp với JSP)
                processPayment(request, response);
                break;
            case "generateInvoice":
                generateInvoice(request, response);
                break;
        }
    }

    /**
     * ĐÃ CẬP NHẬT: - Lấy cả danh sách Hóa đơn (invoiceList) - Lấy cả danh sách
     * Phiếu khám CHƯA CÓ HÓA ĐƠN (pendingEncounterList)
     */
    private void listAllInvoices(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String searchKeyword = request.getParameter("searchKeyword");

        // 1. Lấy danh sách hóa đơn (như cũ)
        List<HoaDonDTO> invoiceList = hoaDonService.searchInvoices(searchKeyword);

        // 2. MỚI: Lấy danh sách phiếu khám CHƯA có hóa đơn
        List<PhieuKhamBenhDTO> pendingEncounterList = phieuKhamBenhService.getUninvoicedEncounters(searchKeyword);

        // 3. Gửi cả hai danh sách ra view
        request.setAttribute("invoiceList", invoiceList);
        request.setAttribute("pendingEncounterList", pendingEncounterList);

        request.getRequestDispatcher("DanhSachHoaDon.jsp").forward(request, response);
    }

    /**
     * HÀM MỚI: Xử lý việc tạo hóa đơn
     */
    private void generateInvoice(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String urlRedirect;
        try {
            int phieuKhamId = Integer.parseInt(request.getParameter("phieuKhamId"));

            // 1. Gọi Service để thực hiện toàn bộ logic nghiệp vụ
            int newInvoiceId = hoaDonService.generateInvoice(phieuKhamId);

            // 2. Chuyển hướng người dùng đến trang Chi tiết Hóa đơn vừa tạo
            urlRedirect = "MainController?action=viewInvoice&id=" + newInvoiceId + "&genSuccess=true";

        } catch (Exception e) {
            // Nếu có lỗi (ví dụ: Hóa đơn đã tồn tại), quay lại trang danh sách
            urlRedirect = "MainController?action=listInvoices&genError=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8");
            e.printStackTrace();
        }
        response.sendRedirect(urlRedirect);
    }

    private void showInvoiceDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int invoiceId = Integer.parseInt(request.getParameter("id"));

            // 1. Lấy Hóa đơn (DTO phẳng)
            HoaDonDTO invoice = hoaDonService.getHoaDonById(invoiceId);
            if (invoice == null) {
                throw new Exception("Không tìm thấy hóa đơn.");
            }

            // 2. Lấy Lịch sử giao dịch (DTO list)
            List<GiaoDichThanhToanDTO> transactions = giaoDichThanhToanService.getGiaoDichByHoaDon(invoiceId);

            // 3. Lấy ID phiếu khám từ hóa đơn
            int phieuKhamId = invoice.getPhieuKhamBenhId();

            // 4. Lấy danh sách Dịch vụ (DTO list)
            List<ChiDinhDichVuDTO> dichVuList = chiDinhDichVuService.getByPhieuKhamId(phieuKhamId);

            // 5. Lấy danh sách Thuốc (DTO list)
            List<ChiTietDonThuocDTO> thuocList = donThuocService.getChiTietByPhieuKhamId(phieuKhamId);

            // --- Đẩy 4 ĐỐI TƯỢNG ra request ---
            request.setAttribute("invoice", invoice); // (1)
            request.setAttribute("transactions", transactions); // (2)
            request.setAttribute("danhSachDichVu", dichVuList); // (4)
            request.setAttribute("danhSachThuoc", thuocList); // (5)

            request.getRequestDispatcher("ChiTietHoaDon.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("ERROR_MESSAGE", "Không tìm thấy hóa đơn: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    // ================== HÀM MỚI (Khớp với JSP) ==================
    /**
     * HÀM MỚI: Lấy dữ liệu chi tiết hóa đơn và chuyển đến trang in (Logic giống
     * hệt showInvoiceDetails, chỉ khác file JSP đích)
     */
    private void showPrintableInvoice(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int invoiceId = Integer.parseInt(request.getParameter("id"));

            // 1. Lấy Hóa đơn (DTO phẳng)
            HoaDonDTO invoice = hoaDonService.getHoaDonById(invoiceId);
            if (invoice == null) {
                throw new Exception("Không tìm thấy hóa đơn.");
            }

            // 2. Lấy Lịch sử giao dịch (DTO list)
            List<GiaoDichThanhToanDTO> transactions = giaoDichThanhToanService.getGiaoDichByHoaDon(invoiceId);

            // 3. Lấy ID phiếu khám từ hóa đơn
            int phieuKhamId = invoice.getPhieuKhamBenhId();

            // 4. Lấy danh sách Dịch vụ (DTO list)
            List<ChiDinhDichVuDTO> dichVuList = chiDinhDichVuService.getByPhieuKhamId(phieuKhamId);

            // 5. Lấy danh sách Thuốc (DTO list)
            List<ChiTietDonThuocDTO> thuocList = donThuocService.getChiTietByPhieuKhamId(phieuKhamId);

            // --- Đẩy 4 ĐỐI TƯỢNG ra request ---
            request.setAttribute("invoice", invoice); // (1)
            request.setAttribute("transactions", transactions); // (2)
            request.setAttribute("danhSachDichVu", dichVuList); // (4)
            request.setAttribute("danhSachThuoc", thuocList); // (5)

            // --- MỚI: Chuyển đến trang JSP thiết kế cho việc in ---
            request.getRequestDispatcher("InHoaDon.jsp").forward(request, response);

        } catch (Exception e) {
            request.setAttribute("ERROR_MESSAGE", "Không tìm thấy hóa đơn: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
    // ================== HẾT HÀM MỚI ==================

    // ================== HÀM ĐÃ SỬA LỖI ==================
    private void processPayment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Lấy ID hóa đơn từ request (JSP có gửi)
        int invoiceId = Integer.parseInt(request.getParameter("invoiceId"));
        String urlRedirect = "MainController?action=viewInvoice&id=" + invoiceId;

        try {
            // Lấy phương thức từ request (JSP có gửi)
            String phuongThuc = request.getParameter("phuongThuc");

            // *** SỬA LỖI: ***
            // 1. Lấy Hóa đơn (DTO) từ service để biết tổng số tiền
            //    (Vì JSP không gửi 'soTien')
            HoaDonDTO invoice = hoaDonService.getHoaDonById(invoiceId);
            if (invoice == null) {
                throw new Exception("Không tìm thấy hóa đơn ID " + invoiceId + " để thanh toán.");
            }

            // 2. Lấy 'soTien' từ đối tượng hóa đơn đã truy vấn
            BigDecimal soTien = invoice.getTongTien();

            // 3. Gọi service với số tiền chính xác từ CSDL
            hoaDonService.processPayment(invoiceId, soTien, phuongThuc);

            urlRedirect += "&paySuccess=true";

        } catch (Exception e) {
            urlRedirect += "&payError=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8");
            e.printStackTrace();
        }

        response.sendRedirect(urlRedirect);
    }
    // ================== HẾT HÀM SỬA LỖI ==================
}
