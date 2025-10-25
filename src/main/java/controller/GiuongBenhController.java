package controller;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

// Import các service
import service.GiuongBenhService;
import service.BenhNhanService;
import service.PhongBenhService; 

// Import các DTO
import model.dto.GiuongBenhDTO;
import model.dto.BenhNhanDTO;
import model.dto.PhongBenhDTO;

@WebServlet(name = "GiuongBenhController", urlPatterns = {"/GiuongBenhController"})
public class GiuongBenhController extends HttpServlet {

    private GiuongBenhService giuongBenhService;
    private BenhNhanService benhNhanService;
    private PhongBenhService phongBenhService; // MỚI

    @Override
    public void init() {
        this.giuongBenhService = new GiuongBenhService();
        this.benhNhanService = new BenhNhanService();
        this.phongBenhService = new PhongBenhService(); // MỚI
    }

    /**
     * Refactor doGet:
     * - Tải tất cả dữ liệu cần thiết cho View (danh sách giường, 
     * danh sách bệnh nhân, danh sách phòng)
     * - Forward đến JSP một lần duy nhất.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            // 1. Lấy danh sách giường (cho bảng sơ đồ)
            List<GiuongBenhDTO> bedList = giuongBenhService.getAllGiuong(); 
            
            // 2. Lấy danh sách bệnh nhân chưa có giường (cho form gán)
            List<BenhNhanDTO> patientList = benhNhanService.getBenhNhanChuaCoGiuong();

            // 3. MỚI: Lấy danh sách phòng (cho form tạo giường)
            // Giả sử service của bạn có method getAllPhongBenh()
            List<PhongBenhDTO> roomList = phongBenhService.getAllPhongBenh(); 

            request.setAttribute("bedList", bedList);
            request.setAttribute("patientList", patientList);
            request.setAttribute("roomList", roomList); // MỚI

        } catch (Exception e) {
            request.setAttribute("error", "Lỗi tải dữ liệu giường bệnh: " + e.getMessage());
        } finally {
            // Forward 1 lần duy nhất đến JSP
            request.getRequestDispatcher("GiuongBenh.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8"); // Đảm bảo hỗ trợ Tiếng Việt
        String action = request.getParameter("action");
        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        switch (action) {
            case "createBed": // MỚI
                createBed(request, response);
                break;
            case "assignBed":
                assignPatientToBed(request, response);
                break;
            case "releaseBed":
                releasePatientFromBed(request, response);
                break;
        }
    }

    /**
     * MỚI: Hàm tạo giường
     */
    private void createBed(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String urlRedirect = "MainController?action=listBeds";
        try {
            String tenGiuong = request.getParameter("tenGiuong");
            int phongBenhId = Integer.parseInt(request.getParameter("phongBenhId"));

            GiuongBenhDTO newBed = new GiuongBenhDTO();
            newBed.setTenGiuong(tenGiuong);
            newBed.setPhongBenhId(phongBenhId);

            // Giả sử service của bạn có method createBed(GiuongBenhDTO)
            giuongBenhService.createGiuong(newBed); 

            urlRedirect += "&createBedSuccess=true";
        } catch (Exception e) {
            urlRedirect += "&createBedError=" + e.getMessage();
            e.printStackTrace(); // Giúp debug
        }
        response.sendRedirect(urlRedirect);
    }

    // (Các hàm assignPatientToBed và releasePatientFromBed giữ nguyên như cũ)
    
    private void assignPatientToBed(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String urlRedirect = "MainController?action=listBeds";
        try {
            int bedId = Integer.parseInt(request.getParameter("bedId"));
            int patientId = Integer.parseInt(request.getParameter("patientId"));
            giuongBenhService.assignBenhNhanToGiuong(bedId, patientId);
            urlRedirect += "&assignSuccess=true";
        } catch (Exception e) {
            urlRedirect += "&assignError=" + e.getMessage();
        }
        response.sendRedirect(urlRedirect);
    }

    private void releasePatientFromBed(HttpServletRequest request, HttpServletResponse response) 
            throws IOException {
        String urlRedirect = "MainController?action=listBeds";
        try {
            int bedId = Integer.parseInt(request.getParameter("bedId"));
            giuongBenhService.releaseGiuong(bedId);
            urlRedirect += "&releaseSuccess=true";
        } catch (Exception e) {
            urlRedirect += "&releaseError=" + e.getMessage();
        }
        response.sendRedirect(urlRedirect);
    }
}