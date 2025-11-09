/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.dto.KhoaDTO;
import model.dto.NhanVienDTO;
import service.KhoaService;
import service.NhanVienService;

/**
 *
 * @author SunnyU
 */
@WebServlet(name = "PublicController", urlPatterns = {"/PublicController"})
public class PublicController extends HttpServlet {

    private final NhanVienService nhanVienService = new NhanVienService();
    private final KhoaService khoaService = new KhoaService(); // Cần để lấy danh sách khoa

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        String url = "index.jsp"; // Trang chủ mặc định

        try {
            if ("viewDoctors".equals(action)) {
                url = loadDoctorsPage(request);
            }

        } catch (Exception e) {
            
             }

        request.getRequestDispatcher(url).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {    
    }

    /**
     * Tải dữ liệu cho trang danh sách bác sĩ.
     */
    private String loadDoctorsPage(HttpServletRequest request) throws Exception {
        // 1. Lấy các tham số lọc từ URL
        String keyword = request.getParameter("keyword");
        String khoaIdStr = request.getParameter("khoaId");
        int khoaId = 0;

        if (khoaIdStr != null && !khoaIdStr.isEmpty()) {
            khoaId = Integer.parseInt(khoaIdStr);
        }

        // 2. Lấy danh sách khoa để điền vào dropdown lọc
        List<KhoaDTO> danhSachKhoa = khoaService.getAllKhoa();

        // 3. Gọi Service để tìm bác sĩ theo tiêu chí
        List<NhanVienDTO> danhSachBacSi = nhanVienService.searchDoctors(keyword, khoaId);

        // 4. Gửi dữ liệu đến JSP
        request.setAttribute("danhSachBacSi", danhSachBacSi);
        request.setAttribute("danhSachKhoa", danhSachKhoa);

        // 5. Gửi lại các giá trị lọc để giữ trên form
        request.setAttribute("selectedKhoaId", khoaId);
        request.setAttribute("searchKeyword", keyword);

        return "/doctors.jsp";
    }
}
