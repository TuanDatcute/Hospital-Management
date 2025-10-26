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
    private PhongBenhService phongBenhService;

    @Override
    public void init() {
        this.giuongBenhService = new GiuongBenhService();
        this.benhNhanService = new BenhNhanService();
        this.phongBenhService = new PhongBenhService();
    }

    /**
     * Cập nhật doGet:
     * - Nhận 'searchKeyword' từ request.
     * - Gọi service tìm kiếm (thay vì getAll) để lấy 'bedList' đã lọc.
     * - Tải dữ liệu cho các form như cũ.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8"); // Đảm bảo đọc UTF-8

        try {
            // =============================================
            //        THAY ĐỔI BẮT ĐẦU TỪ ĐÂY
            // =============================================
            
            // 1. Lấy từ khóa tìm kiếm từ parameter (form GET)
            String searchKeyword = request.getParameter("searchKeyword");

            // 2. Lấy danh sách giường (ĐÃ LỌC)
            // Giả sử bạn tạo một service method mới tên là searchGiuong(keyword)
            // Nếu keyword là null hoặc rỗng, service này nên trả về tất cả
            List<GiuongBenhDTO> bedList = giuongBenhService.searchGiuong(searchKeyword);
            
            // =============================================
            //        THAY ĐỔI KẾT THÚC
            // =============================================

            // 3. Lấy danh sách bệnh nhân (cho form gán) - Giữ nguyên
            List<BenhNhanDTO> patientList = benhNhanService.getBenhNhanChuaCoGiuong();

            // 4. Lấy danh sách phòng (cho form tạo giường) - Giữ nguyên
            List<PhongBenhDTO> roomList = phongBenhService.getAllPhongBenh();

            // 5. Gửi tất cả dữ liệu sang JSP
            request.setAttribute("bedList", bedList);
            request.setAttribute("patientList", patientList);
            request.setAttribute("roomList", roomList); 
            
            // Không cần setAttribute cho searchKeyword vì JSP có thể
            // lấy trực tiếp từ ${param.searchKeyword}

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
        // ... (GIỮ NGUYÊN TOÀN BỘ PHẦN doPost CỦA BẠN, KHÔNG CẦN SỬA) ...
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        switch (action) {
            case "createBed":
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

    // ... (CÁC HÀM createBed, assignPatientToBed, releasePatientFromBed GIỮ NGUYÊN) ...
    private void createBed(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String urlRedirect = "MainController?action=listBeds";
        try {
            String tenGiuong = request.getParameter("tenGiuong");
            String phongBenhIdStr = request.getParameter("phongBenhId");
            if (tenGiuong == null || tenGiuong.trim().isEmpty()) {
                throw new Exception("Tên giường không được để trống.");
            }
            if (phongBenhIdStr == null || phongBenhIdStr.trim().isEmpty()) {
                throw new Exception("Vui lòng chọn một phòng bệnh.");
            }
            int phongBenhId = Integer.parseInt(phongBenhIdStr);
            GiuongBenhDTO newBed = new GiuongBenhDTO();
            newBed.setTenGiuong(tenGiuong.trim());
            newBed.setPhongBenhId(phongBenhId);
            giuongBenhService.createGiuong(newBed);
            urlRedirect += "&createBedSuccess=true";
        } catch (Exception e) {
            urlRedirect += "&createBedError=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8");
            e.printStackTrace();
        }
        response.sendRedirect(urlRedirect);
    }

    private void assignPatientToBed(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String urlRedirect = "MainController?action=listBeds";
        try {
            String bedIdStr = request.getParameter("bedId");
            String patientIdStr = request.getParameter("patientId");
            if (patientIdStr == null || patientIdStr.trim().isEmpty()) {
                throw new Exception("Vui lòng chọn một bệnh nhân.");
            }
            int bedId = Integer.parseInt(bedIdStr);
            int patientId = Integer.parseInt(patientIdStr);
            boolean assignSuccess = giuongBenhService.assignBenhNhanToGiuong(bedId, patientId);
            if (assignSuccess) {
                urlRedirect += "&assignSuccess=true";
            } else {
                throw new Exception("Không thể gán giường. Giường không trống hoặc có lỗi xảy ra.");
            }
        } catch (Exception e) {
            urlRedirect += "&assignError=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8");
        }
        response.sendRedirect(urlRedirect);
    }

    private void releasePatientFromBed(HttpServletRequest request, HttpServletResponse response) throws IOException {
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