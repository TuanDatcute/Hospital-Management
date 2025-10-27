package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import service.HoaDonService; 
import service.GiaoDichThanhToanService; 
import model.dto.HoaDonDTO;
import model.dto.GiaoDichThanhToanDTO;

@WebServlet(name = "HoaDon_GiaoDichThanhToanController", urlPatterns = {"/HoaDon_GiaoDichThanhToanController"})
public class HoaDon_GiaoDichThanhToanController extends HttpServlet {

    private HoaDonService hoaDonService;
    private GiaoDichThanhToanService giaoDichThanhToanService;

    @Override
    public void init() {
        this.hoaDonService = new HoaDonService();
        this.giaoDichThanhToanService = new GiaoDichThanhToanService();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8"); // Thêm để hỗ trợ tìm kiếm
        
        String action = request.getParameter("action");
        if (action == null) {
            action = "listInvoices";
        }

        switch (action) {
            case "viewInvoice":
                showInvoiceDetails(request, response);
                break;
            case "listInvoices":
            default:
                listAllInvoices(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8"); // Thêm để hỗ trợ form
        
        String action = request.getParameter("action");
        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        // (switch...case cho doPost giữ nguyên)
// ... (Nội dung doPost giữ nguyên)
        switch (action) {
            case "payInvoice":
                processPayment(request, response);
                break;
        }
    }

    /**
     * ĐÃ CẬP NHẬT:
     * - Thêm logic đọc 'searchKeyword'.
     * - Gọi hàm service mới 'searchInvoices'.
     */
    private void listAllInvoices(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Lấy từ khóa tìm kiếm từ request
        String searchKeyword = request.getParameter("searchKeyword");

        // 2. Gọi service tìm kiếm (thay vì getAll...)
        // Service sẽ tự xử lý nếu keyword là null hoặc rỗng
        List<HoaDonDTO> invoiceList = hoaDonService.searchInvoices(searchKeyword);
        
        // 3. Gửi danh sách đã lọc (hoặc tất cả) ra view
        request.setAttribute("invoiceList", invoiceList);
        request.getRequestDispatcher("DanhSachHoaDon.jsp").forward(request, response);
    }

    // (Hàm showInvoiceDetails giữ nguyên)
    private void showInvoiceDetails(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            int invoiceId = Integer.parseInt(request.getParameter("id"));
            HoaDonDTO invoice = hoaDonService.getHoaDonById(invoiceId);
            List<GiaoDichThanhToanDTO> transactions = giaoDichThanhToanService.getGiaoDichByHoaDon(invoiceId);

            request.setAttribute("invoice", invoice);
            request.setAttribute("transactions", transactions);

            request.getRequestDispatcher("ChiTietHoaDon.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("ERROR_MESSAGE", "Không tìm thấy hóa đơn: " + e.getMessage());
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }
    
    // (Hàm processPayment giữ nguyên)
    private void processPayment(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        int invoiceId = Integer.parseInt(request.getParameter("invoiceId"));
        String urlRedirect = "MainController?action=viewInvoice&id=" + invoiceId;
        try {
            BigDecimal soTien = new BigDecimal(request.getParameter("soTien"));
            String phuongThuc = request.getParameter("phuongThuc");
            hoaDonService.processPayment(invoiceId, soTien, phuongThuc);
            urlRedirect += "&paySuccess=true";
        } catch (Exception e) {
            urlRedirect += "&payError=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8");
            e.printStackTrace(); 
        }
        response.sendRedirect(urlRedirect);
    }
}