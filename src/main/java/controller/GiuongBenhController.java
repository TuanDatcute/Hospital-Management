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
     * - Thêm xử lý cho action 'getBedForUpdate'.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8"); 
        
        // === THÊM MỚI: Lấy action ===
        String action = request.getParameter("action");
        if (action == null) action = "listBeds"; // Mặc định là list
        // ============================

        try {
            String searchKeyword = request.getParameter("searchKeyword");
            
            // 1. Lấy danh sách giường (ĐÃ LỌC, loại bỏ giường đã xóa)
            List<GiuongBenhDTO> bedList = giuongBenhService.searchGiuong(searchKeyword);
            
            // 2. Lấy danh sách bệnh nhân (cho form gán)
            List<BenhNhanDTO> patientList = benhNhanService.getBenhNhanChuaCoGiuong();

            // 3. Lấy danh sách phòng (cho form thêm/sửa giường)
            List<PhongBenhDTO> roomList = phongBenhService.getAllPhongBenh(); // Lấy phòng còn hoạt động

            // 4. Gửi danh sách ra JSP
            request.setAttribute("bedList", bedList);
            request.setAttribute("patientList", patientList);
            request.setAttribute("roomList", roomList); 

            // === THÊM MỚI: Xử lý lấy giường để cập nhật ===
            if ("getBedForUpdate".equals(action)) {
                 int bedId = Integer.parseInt(request.getParameter("bedId"));
                 // Giả sử service có hàm getById trả về DTO
                 GiuongBenhDTO bedToUpdate = giuongBenhService.getGiuongById(bedId); 
                 request.setAttribute("bedToUpdate", bedToUpdate);
            }
            // ============================================

        } catch (Exception e) {
            request.setAttribute("error", "Lỗi tải dữ liệu giường bệnh: " + e.getMessage());
            e.printStackTrace(); // In lỗi ra console
        } finally {
            request.getRequestDispatcher("GiuongBenh.jsp").forward(request, response);
        }
    }

    /**
     * CẬP NHẬT: Thêm case 'updateBed'
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action không được cung cấp.");
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
            case "deleteBed":
                deleteBed(request, response);
                break;
            // === THÊM MỚI ===
            case "updateBed":
                 updateBed(request, response);
                 break;
            default:
                 response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action không hợp lệ: " + action);
        }
    }
    
    /**
     * HÀM MỚI: Xử lý cập nhật thông tin giường
     */
    private void updateBed(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String urlRedirect = "MainController?action=listBeds";
        int bedId = 0; // Khởi tạo để dùng trong catch
        try {
            bedId = Integer.parseInt(request.getParameter("bedId"));
            String tenGiuong = request.getParameter("tenGiuong");
            String phongBenhIdStr = request.getParameter("phongBenhId");

            // --- VALIDATION ---
            if (tenGiuong == null || tenGiuong.trim().isEmpty()) {
                throw new IllegalArgumentException("Tên giường không được để trống.");
            }
            if (phongBenhIdStr == null || phongBenhIdStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng chọn một phòng bệnh.");
            }
            // --- KẾT THÚC VALIDATION ---

            int phongBenhId = Integer.parseInt(phongBenhIdStr);

            GiuongBenhDTO bedToUpdate = new GiuongBenhDTO();
            bedToUpdate.setId(bedId);
            bedToUpdate.setTenGiuong(tenGiuong.trim());
            bedToUpdate.setPhongBenhId(phongBenhId);
            // Trạng thái không cần set, Service sẽ tự kiểm tra và giữ nguyên nếu hợp lệ

            // Gọi service để cập nhật (Service sẽ kiểm tra trạng thái)
            giuongBenhService.updateGiuong(bedToUpdate);

            urlRedirect += "&updateSuccess=true"; // Thêm param thành công

        } catch (NumberFormatException e) {
             urlRedirect += "&updateError=" + java.net.URLEncoder.encode("ID giường hoặc ID phòng không hợp lệ.", "UTF-8");
             if (bedId > 0) urlRedirect += "&bedId=" + bedId; // Giữ lại ID nếu có thể
             e.printStackTrace();
        } catch (IllegalArgumentException e) { // Lỗi validation
             urlRedirect += "&updateError=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8");
             if (bedId > 0) urlRedirect += "&bedId=" + bedId;
             e.printStackTrace();
        } catch (IllegalStateException e) { // Lỗi nghiệp vụ từ Service (vd: giường đang dùng)
             urlRedirect += "&updateError=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8");
             if (bedId > 0) urlRedirect += "&bedId=" + bedId;
             e.printStackTrace();
        } catch (Exception e) { // Lỗi khác
            urlRedirect += "&updateError=" + java.net.URLEncoder.encode("Lỗi hệ thống khi cập nhật giường: " + e.getMessage(), "UTF-8");
            if (bedId > 0) urlRedirect += "&bedId=" + bedId;
            e.printStackTrace();
        }
        response.sendRedirect(urlRedirect);
    }


    // (Các hàm createBed, assignPatientToBed, releasePatientFromBed, deleteBed giữ nguyên)
    // ...
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
    
    private void deleteBed(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String urlRedirect = "MainController?action=listBeds";
        try {
            int bedId = Integer.parseInt(request.getParameter("bedId"));
            giuongBenhService.softDeleteGiuong(bedId); 
            urlRedirect += "&deleteSuccess=true";
        } catch (Exception e) {
            urlRedirect += "&deleteError=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8");
            e.printStackTrace();
        }
        response.sendRedirect(urlRedirect);
    }
}