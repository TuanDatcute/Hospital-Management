/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import exception.ValidationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.dto.ChiTietDonThuocDTO;
import model.dto.DonThuocDTO;
import model.dto.ThuocDTO;
import service.ChiTietDonThuocService;
import service.DonThuocService;
import service.ThuocService;

/**
 *
 * @author SunnyU
 */
@WebServlet(name = "DonThuocController", urlPatterns = {"/DonThuocController"})
public class DonThuocController extends HttpServlet {

    private static final String ERROR_PAGE = "error.jsp";
    private static final String DON_THUOC_PAGE = "QuanLyDonThuoc.jsp";
    private static final String DON_THUOC_LIST_PAGE = "DanhSachDonThuoc.jsp";
    private static final String DON_THUOC_FORM_PAGE = "TaoDonThuoc.jsp";

    private final DonThuocService donThuocService = new DonThuocService();
    private final ChiTietDonThuocService chiTietService = new ChiTietDonThuocService();
    private final ThuocService thuocService = new ThuocService();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        String url = ERROR_PAGE;

        try {

            if (action == null) {
                action = "listAll"; // Mặc định là hiển thị danh sách
            }
            switch (action) {
                case "listAll":
                    url = listAll(request);
                    break;
                case "viewDetails":
                    url = viewDetails(request);
                    break;
                case "showCreateDonThuocForm":
                    url = showCreateForm(request);
                    break;
                default:
                    throw new Exception("Hành động GET không hợp lệ.");
            }
        } catch (Exception e) {
            log("Lỗi trong doGet của DonThuocController: ", e);
            request.setAttribute("ERROR_MESSAGE", "Đã có lỗi xảy ra: " + e.getMessage());
        } finally {
            request.getRequestDispatcher(url).forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        String url = ERROR_PAGE; // Mặc định là trang lỗi

        try {
            switch (action) {
                case "createPrescription":
                    url = createPrescription(request);
                    break;
                case "addDetail":
                    url = addDetail(request);
                    break;
                case "updateDetail":
                    url = updateDetail(request);
                    break;
                case "deleteDetail":
                    url = deleteDetail(request);
                    break;
                default:
                    throw new Exception("Hành động POST không hợp lệ: " + action);
            }

            if (url.startsWith("redirect:")) {
                // Nếu URL bắt đầu bằng "redirect:", thực hiện redirect
                // Cắt bỏ tiền tố "redirect:"
                String redirectUrl = url.substring("redirect:".length());
                response.sendRedirect(request.getContextPath() + redirectUrl);
            } else {
                // Nếu không, thực hiện forward
                request.getRequestDispatcher(url).forward(request, response);
            }

        } catch (Exception e) {
            log("Lỗi hệ thống nghiêm trọng trong doPost của DonThuocController: ", e);
            request.setAttribute("ERROR_MESSAGE", "Đã xảy ra lỗi hệ thống nghiêm trọng.");
            request.getRequestDispatcher(ERROR_PAGE).forward(request, response);
        }
    }

    private String showCreateForm(HttpServletRequest request) {
        List<ThuocDTO> danhSachThuoc = thuocService.getAllMedications();
        request.setAttribute("danhSachThuoc", danhSachThuoc);
        return DON_THUOC_FORM_PAGE;
    }

    private String listAll(HttpServletRequest request) {
        String keyword = request.getParameter("keyword"); // Lấy từ khóa từ URL
        List<DonThuocDTO> danhSachDonThuoc;

        try {
            if (keyword != null && !keyword.trim().isEmpty()) {
                danhSachDonThuoc = donThuocService.searchPrescriptionsByPatientName(keyword);
                request.setAttribute("searchKeyword", keyword); // Gửi lại từ khóa để hiển thị
            } else {
                // Nếu không có từ khóa -> gọi hàm lấy tất cả
                danhSachDonThuoc = donThuocService.getAllPrescriptions();
            }
            request.setAttribute("danhSachDonThuoc", danhSachDonThuoc);
        } catch (Exception e) {
            log("Lỗi khi lấy danh sách đơn thuốc: ", e);
            request.setAttribute("ERROR_MESSAGE", "Không thể tải danh sách đơn thuốc.");
        }
        return DON_THUOC_LIST_PAGE;
    }

    /**
     * Hiển thị trang chi tiết đơn thuốc, bao gồm danh sách thuốc và form
     * thêm/sửa.
     */
    private String viewDetails(HttpServletRequest request) throws Exception {
        int donThuocId = Integer.parseInt(request.getParameter("id"));
        DonThuocDTO donThuoc = donThuocService.getPrescriptionDetails(donThuocId);
        if (donThuoc == null) {
            throw new Exception("Không tìm thấy đơn thuốc.");
        }

        // Lấy danh sách tất cả các loại thuốc để điền vào dropdown
        List<ThuocDTO> danhSachThuoc = thuocService.getAllMedications();

        request.setAttribute("donThuoc", donThuoc);
        request.setAttribute("danhSachThuoc", danhSachThuoc);

        // Kiểm tra xem có yêu cầu sửa một chi tiết cụ thể không
        String editIdStr = request.getParameter("editId");
        if (editIdStr != null) {
            int editId = Integer.parseInt(editIdStr);
            // Tìm chi tiết cần sửa trong danh sách đã có của đơn thuốc
            donThuoc.getChiTietDonThuoc().stream()
                    .filter(ct -> ct.getId() == editId)
                    .findFirst()
                    .ifPresent(chiTietCanSua -> request.setAttribute("chiTietCanSua", chiTietCanSua));
        }

        return DON_THUOC_PAGE;
    }

    /**
     * Xử lý thêm một dòng chi tiết vào đơn thuốc.
     */
    private String addDetail(HttpServletRequest request) throws ValidationException {
        ChiTietDonThuocDTO dto = new ChiTietDonThuocDTO();
        dto.setDonThuocId(Integer.parseInt(request.getParameter("donThuocId")));
        dto.setThuocId(Integer.parseInt(request.getParameter("thuocId")));
        dto.setSoLuong(Integer.parseInt(request.getParameter("soLuong")));
        dto.setLieuDung(request.getParameter("lieuDung"));
        chiTietService.addMedicationToPrescription(dto);

        request.getSession().setAttribute("SUCCESS_MESSAGE", "Đã thêm thuốc vào đơn thành công!");

        return "redirect:/MainController?action=viewDetails&id=" + dto.getDonThuocId();
    }

    private String updateDetail(HttpServletRequest request) throws ValidationException {
        ChiTietDonThuocDTO dto = new ChiTietDonThuocDTO();
        dto.setId(Integer.parseInt(request.getParameter("chiTietId")));
        dto.setSoLuong(Integer.parseInt(request.getParameter("soLuong")));
        dto.setLieuDung(request.getParameter("lieuDung"));
        chiTietService.updatePrescriptionDetail(dto);

        request.getSession().setAttribute("SUCCESS_MESSAGE", "Đã cập nhật chi tiết thành công!");

        return "redirect:/MainController?action=viewDetails&id=" + request.getParameter("donThuocId");
    }

    private String deleteDetail(HttpServletRequest request) throws ValidationException {
        int chiTietId = Integer.parseInt(request.getParameter("chiTietId"));
        chiTietService.removeMedicationFromPrescription(chiTietId);

        request.getSession().setAttribute("SUCCESS_MESSAGE", "Đã xóa thuốc khỏi đơn thành công!");

        return "redirect:/MainController?action=viewDetails&id=" + request.getParameter("donThuocId");
    }

    private String createPrescription(HttpServletRequest request) throws ServletException, IOException, ValidationException {
        DonThuocDTO dto = new DonThuocDTO();
        String phieuKhamIdStr = request.getParameter("phieuKhamId");

        try {
            String[] thuocIds = request.getParameterValues("thuocId");
            String[] soLuongs = request.getParameterValues("soLuong");
            String[] lieuDungs = request.getParameterValues("lieuDung");
            dto.setPhieuKhamId(Integer.parseInt(phieuKhamIdStr));
            dto.setLoiDan(request.getParameter("loiDan"));
           
            List<ChiTietDonThuocDTO> chiTietList = new ArrayList<>();
            for (int i = 0; i < thuocIds.length; i++) {
                ChiTietDonThuocDTO chiTietDTO = new ChiTietDonThuocDTO();
                chiTietDTO.setThuocId(Integer.parseInt(thuocIds[i]));
                chiTietDTO.setSoLuong(Integer.parseInt(soLuongs[i]));
                chiTietDTO.setLieuDung(lieuDungs[i]);
                chiTietList.add(chiTietDTO);
            }
            dto.setChiTietDonThuoc(chiTietList);

           DonThuocDTO result= donThuocService.createPrescription(dto);

            request.getSession().setAttribute("SUCCESS_MESSAGE", "Đã kê đơn thuốc thành công!");

            // ✨ THAY ĐỔI: Trả về URL để redirect
            return "redirect:/MainController?action=viewEncounterDetails&id=" + result.getPhieuKhamId();

        } catch (ValidationException | NumberFormatException e) {
            log("Lỗi khi tạo đơn thuốc: " + e.getMessage());
            request.setAttribute("ERROR_MESSAGE", "Lỗi: " + e.getMessage());

            // Tải lại các dữ liệu cần thiết cho form
            List<ThuocDTO> danhSachThuoc = thuocService.getAllMedications();
            request.setAttribute("danhSachThuoc", danhSachThuoc);

            // ✨ THAY ĐỔI: Trả về URL của trang form để forward
            return DON_THUOC_FORM_PAGE;
        }
        // Khối catch(Exception e) chung sẽ được xử lý ở doPost
    }

}
