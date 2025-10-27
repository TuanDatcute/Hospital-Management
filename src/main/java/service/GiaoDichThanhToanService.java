// path: com/service/GiaoDichThanhToanService.java
package service;

import model.dao.GiaoDichThanhToanDAO;
import model.dao.HoaDonDAO;
import model.dto.GiaoDichThanhToanDTO;
import model.Entity.GiaoDichThanhToan;
import model.Entity.HoaDon;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;


public class GiaoDichThanhToanService {

    private final HoaDonDAO hoaDonDAO;
    private final GiaoDichThanhToanDAO giaoDichThanhToanDAO;

    public GiaoDichThanhToanService() {
        this.giaoDichThanhToanDAO = new GiaoDichThanhToanDAO();
        this.hoaDonDAO = new HoaDonDAO();
    }

    /**
     * Tạo một giao dịch thanh toán mới
     */
    public boolean createGiaoDich(GiaoDichThanhToanDTO dto) {
        try {
            // Bước 1: Chuyển DTO sang Entity
            GiaoDichThanhToan giaoDich = convertToEntity(dto);
            if (giaoDich == null) {
                System.err.println("Không thể tạo giao dịch do dữ liệu DTO không hợp lệ (ví dụ: hoaDonId = 0).");
                return false;
            }

            // Bước 2: Lưu giao dịch mới
            giaoDichThanhToanDAO.addGiaoDich(giaoDich);

            // Bước 3: (Logic nghiệp vụ) Cập nhật trạng thái hóa đơn liên quan
            // dto.getHoaDonId() bây giờ trả về int
            checkAndUpdateHoaDonStatus(dto.getHoaDonId());

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy tất cả giao dịch của một hóa đơn
     */
    public List<GiaoDichThanhToanDTO> getGiaoDichByHoaDon(int hoaDonId) {
        List<GiaoDichThanhToan> entities = giaoDichThanhToanDAO.getGiaoDichByHoaDonId(hoaDonId);

        // **SỬ DỤNG VÒNG LẶP FOR ĐỂ TRÁNH LỖI JAVA 8**
        List<GiaoDichThanhToanDTO> dtos = new ArrayList<>();
        for (GiaoDichThanhToan entity : entities) {
            if (entity != null) {
                dtos.add(convertToDTO(entity));
            }
        }
        return dtos;
    }

    /**
     * Logic kiểm tra và cập nhật trạng thái hóa đơn sau khi có giao dịch mới.
     */
    private void checkAndUpdateHoaDonStatus(int hoaDonId) {
        HoaDon hoaDon = hoaDonDAO.getHoaDonById(hoaDonId);
        if (hoaDon == null || hoaDon.getTrangThai().equals("DA_THANH_TOAN")) {
            return; // Hóa đơn không tồn tại hoặc đã thanh toán xong
        }

        // Lấy tất cả giao dịch của hóa đơn này
        List<GiaoDichThanhToan> allTransactions = giaoDichThanhToanDAO.getGiaoDichByHoaDonId(hoaDonId);

        // Tính tổng số tiền đã thanh toán
        BigDecimal tongDaThanhToan = BigDecimal.ZERO;
        for (GiaoDichThanhToan gd : allTransactions) {
            tongDaThanhToan = tongDaThanhToan.add(gd.getSoTien());
        }

        // So sánh tổng đã thanh toán với tổng tiền của hóa đơn
        // compareTo: 0 (bằng), 1 (lớn hơn), -1 (nhỏ hơn)
        if (tongDaThanhToan.compareTo(hoaDon.getTongTien()) >= 0) {
            // Nếu đã trả đủ (hoặc dư), cập nhật trạng thái
            hoaDon.setTrangThai("DA_THANH_TOAN");
            hoaDonDAO.updateHoaDon(hoaDon);
        }
    }

    /**
     * Xử lý một giao dịch thanh toán hoàn chỉnh. Hàm này quản lý một
     * transaction duy nhất để đảm bảo tính toàn vẹn: hoặc cả hai hành động (tạo
     * giao dịch, cập nhật hóa đơn) đều thành công, hoặc cả hai đều thất bại.
     *
     * @param invoiceId ID của hóa đơn cần thanh toán.
     * @param soTienThanhToan Số tiền khách hàng trả.
     * @param phuongThuc Phương thức thanh toán (vd: 'TIEN_MAT').
     * @throws Exception Nếu có lỗi xảy ra (vd: hóa đơn không tồn tại, đã thanh
     * toán, v.v.).
     */
    

    // --- Phương thức chuyển đổi (Helper Methods) ---
    // Chuyển Entity sang DTO
    private GiaoDichThanhToanDTO convertToDTO(GiaoDichThanhToan entity) {
        if (entity == null) {
            return null;
        }

        GiaoDichThanhToanDTO dto = new GiaoDichThanhToanDTO();
        dto.setId(entity.getId()); // int sang int
        dto.setSoTien(entity.getSoTien());
        dto.setPhuongThuc(entity.getPhuongThuc());
        dto.setThoiGianGiaoDich(entity.getThoiGianGiaoDich());

        if (entity.getHoaDon() != null) {
            dto.setHoaDonId(entity.getHoaDon().getId()); // int sang int
        }

        return dto;
    }

    // Chuyển DTO sang Entity
    private GiaoDichThanhToan convertToEntity(GiaoDichThanhToanDTO dto) {
        if (dto == null) {
            return null;
        }

        GiaoDichThanhToan entity = new GiaoDichThanhToan();

        // THAY ĐỔI: Kiểm tra != 0 thay vì != null
        // Giả định ID 0 nghĩa là "mới" và không cần set
        // (sẽ được CSDL tự động tạo)
        if (dto.getId() != 0) {
            entity.setId(dto.getId());
        }

        entity.setSoTien(dto.getSoTien());
        entity.setPhuongThuc(dto.getPhuongThuc());
        // thoiGianGiaoDich sẽ được @CreationTimestamp tự động gán

        // Lấy Hóa Đơn
        // THAY ĐỔI: Kiểm tra == 0 thay vì == null
        // Vì dto.getHoaDonId() là int, nó không thể là null, giá trị mặc định là 0.
        if (dto.getHoaDonId() == 0) {
            System.err.println("Không thể tạo giao dịch: hoaDonId không được cung cấp (bằng 0).");
            return null; // Không thể tạo giao dịch mà không có hóa đơn
        }

        HoaDon hoaDon = hoaDonDAO.getHoaDonById(dto.getHoaDonId()); // Gọi DAO với int
        if (hoaDon == null) {
            System.err.println("Không tìm thấy Hóa Đơn ID: " + dto.getHoaDonId());
            return null; // Hóa đơn không tồn tại
        }

        entity.setHoaDon(hoaDon);
        return entity;
    }
}
