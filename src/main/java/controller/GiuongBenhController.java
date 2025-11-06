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
     * CẬP NHẬT doGet:
     * - Tách biệt logic để forward đến 2 JSP khác nhau.
     * - 'listBeds' -> soDoGiuongBenh.jsp
     * - 'showCreateBedForm' (MỚI) -> giuongBenhForm.jsp
     * - 'getBedForUpdate' -> giuongBenhForm.jsp
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) action = "listBeds"; // Mặc định là list

        String url = "soDoGiuongBenh.jsp"; // URL mặc định

        try {
            switch (action) {
                case "showCreateBedForm":
                    // Chỉ cần tải roomList cho form thêm mới
                    List<PhongBenhDTO> roomListCreate = phongBenhService.getAllPhongBenh();
                    request.setAttribute("roomList", roomListCreate);
                    url = "giuongBenhForm.jsp";
                    break;

                case "getBedForUpdate":
                    // Tải roomList và giường cần sửa cho form cập nhật
                    int bedId = Integer.parseInt(request.getParameter("bedId"));
                    GiuongBenhDTO bedToUpdate = giuongBenhService.getGiuongById(bedId);
                    List<PhongBenhDTO> roomListUpdate = phongBenhService.getAllPhongBenh();
                    
                    request.setAttribute("bedToUpdate", bedToUpdate);
                    request.setAttribute("roomList", roomListUpdate);
                    url = "giuongBenhForm.jsp";
                    break;

                case "listBeds":
                default:
                    // Tải dữ liệu cho trang danh sách
                    String searchKeyword = request.getParameter("searchKeyword");
                    List<GiuongBenhDTO> bedList = giuongBenhService.searchGiuong(searchKeyword);
                    List<BenhNhanDTO> patientList = benhNhanService.getBenhNhanChuaCoGiuong();
                    
                    request.setAttribute("bedList", bedList);
                    request.setAttribute("patientList", patientList);
                    // Không cần roomList ở trang danh sách nữa
                    url = "GiuongBenh.jsp";
                    break;
            }
        } catch (Exception e) {
            request.setAttribute("error", "Lỗi tải dữ liệu: " + e.getMessage());
            e.printStackTrace(); // In lỗi ra console
            // Nếu lỗi nặng, chuyển về trang danh sách chính với thông báo lỗi
            url = "soDoGiuongBenh.jsp"; 
        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    /**
     * CẬP NHẬT doPost:
     * - Sửa URL redirect từ 'MainController' -> 'GiuongBenhController'.
     * - Cập nhật 'createBed' và 'updateBed' để 'forward' về form nếu có lỗi,
     * thay vì 'redirect' (để giữ lại dữ liệu người dùng đã nhập).
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
            case "updateBed":
                updateBed(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Action không hợp lệ: " + action);
        }
    }
    
    /**
     * CẬP NHẬT: Xử lý cập nhật thông tin giường
     * - Khi lỗi: Forward về form.jsp với lỗi và dữ liệu đã nhập.
     * - Khi thành công: Redirect về listBeds.
     */
    private void updateBed(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // === SỬA LỖI: URL Redirect phải trỏ về Servlet này ===
        String urlRedirect = "GiuongBenhController?action=listBeds";
        
        int bedId = 0;
        String tenGiuong = request.getParameter("tenGiuong");
        String phongBenhIdStr = request.getParameter("phongBenhId");
        int phongBenhId = 0;

        try {
            bedId = Integer.parseInt(request.getParameter("bedId"));
            
            // --- VALIDATION ---
            if (tenGiuong == null || tenGiuong.trim().isEmpty()) {
                throw new IllegalArgumentException("Tên giường không được để trống.");
            }
            if (phongBenhIdStr == null || phongBenhIdStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng chọn một phòng bệnh.");
            }
            // --- KẾT THÚC VALIDATION ---

            phongBenhId = Integer.parseInt(phongBenhIdStr);

            GiuongBenhDTO bedToUpdate = new GiuongBenhDTO();
            bedToUpdate.setId(bedId);
            bedToUpdate.setTenGiuong(tenGiuong.trim());
            bedToUpdate.setPhongBenhId(phongBenhId);

            giuongBenhService.updateGiuong(bedToUpdate);

            urlRedirect += "&updateSuccess=true"; // Thêm param thành công
            response.sendRedirect(urlRedirect); // === CHỈ REDIRECT KHI THÀNH CÔNG ===

        } catch (Exception e) { 
            // === XỬ LÝ LỖI: FORWARD VỀ FORM ===
            e.printStackTrace();
            
            // 1. Set thông báo lỗi
            request.setAttribute("error", e.getMessage());
            
            // 2. Tải lại roomList để hiển thị dropdown
            try {
                 List<PhongBenhDTO> roomList = phongBenhService.getAllPhongBenh();
                 request.setAttribute("roomList", roomList);
            } catch (Exception serviceEx) {
                 request.setAttribute("error", "Lỗi khi cập nhật: " + e.getMessage() + ". Lỗi khi tải lại phòng: " + serviceEx.getMessage());
            }

            // 3. Gửi lại đối tượng "giả" bedToUpdate với dữ liệu người dùng đã nhập
            GiuongBenhDTO failedBedData = new GiuongBenhDTO();
            failedBedData.setId(bedId);
            failedBedData.setTenGiuong(tenGiuong); // Giữ lại tên đã nhập
            failedBedData.setPhongBenhId(phongBenhId); // Giữ lại phòng đã chọn
            request.setAttribute("bedToUpdate", failedBedData);
            
            // 4. Forward về trang form
            request.getRequestDispatcher("giuongBenhForm.jsp").forward(request, response);
        }
    }


    /**
     * CẬP NHẬT: Xử lý thêm mới
     * - Khi lỗi: Forward về form.jsp với lỗi và dữ liệu đã nhập.
     * - Khi thành công: Redirect về listBeds.
     */
    private void createBed(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
            
        // === SỬA LỖI: URL Redirect phải trỏ về Servlet này ===
        String urlRedirect = "GiuongBenhController?action=listBeds";
        
        String tenGiuong = request.getParameter("tenGiuong");
        String phongBenhIdStr = request.getParameter("phongBenhId");
        
        try {
            if (tenGiuong == null || tenGiuong.trim().isEmpty()) {
                throw new IllegalArgumentException("Tên giường không được để trống.");
            }
            if (phongBenhIdStr == null || phongBenhIdStr.trim().isEmpty()) {
                throw new IllegalArgumentException("Vui lòng chọn một phòng bệnh.");
            }
            int phongBenhId = Integer.parseInt(phongBenhIdStr);
            
            GiuongBenhDTO newBed = new GiuongBenhDTO();
            newBed.setTenGiuong(tenGiuong.trim());
            newBed.setPhongBenhId(phongBenhId);
            
            giuongBenhService.createGiuong(newBed);
            
            urlRedirect += "&createBedSuccess=true";
            response.sendRedirect(urlRedirect); // === CHỈ REDIRECT KHI THÀNH CÔNG ===
            
        } catch (Exception e) {
            // === XỬ LÝ LỖI: FORWARD VỀ FORM ===
            e.printStackTrace();
            
            // 1. Set thông báo lỗi
            request.setAttribute("error", e.getMessage());
            
            // 2. Gửi lại dữ liệu đã nhập (để JSTL có thể dùng ${param.tenGiuong})
            //    (Phần này JSP đã tự xử lý qua ${param.tenGiuong} 
            //     nhưng chúng ta cần tải lại roomList)

            // 3. Tải lại roomList để hiển thị dropdown
            try {
                 List<PhongBenhDTO> roomList = phongBenhService.getAllPhongBenh();
                 request.setAttribute("roomList", roomList);
            } catch (Exception serviceEx) {
                 request.setAttribute("error", "Lỗi khi thêm: " + e.getMessage() + ". Lỗi khi tải lại phòng: " + serviceEx.getMessage());
            }
            
            // 4. Forward về trang form
            request.getRequestDispatcher("giuongBenhForm.jsp").forward(request, response);
        }
    }

    private void assignPatientToBed(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // === SỬA LỖI: URL Redirect ===
        String urlRedirect = "GiuongBenhController?action=listBeds";
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
        // === SỬA LỖI: URL Redirect ===
        String urlRedirect = "GiuongBenhController?action=listBeds";
        try {
            int bedId = Integer.parseInt(request.getParameter("bedId"));
            giuongBenhService.releaseGiuong(bedId);
            urlRedirect += "&releaseSuccess=true";
        } catch (Exception e) {
            urlRedirect += "&releaseError=" + java.net.URLEncoder.encode(e.getMessage(), "UTF-8");
        }
        response.sendRedirect(urlRedirect);
    }
    
    private void deleteBed(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // === SỬA LỖI: URL Redirect ===
        String urlRedirect = "GiuongBenhController?action=listBeds";
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