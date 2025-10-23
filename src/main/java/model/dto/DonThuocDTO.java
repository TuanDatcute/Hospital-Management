package model.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO cho Đơn Thuốc (Prescription). Chứa thông tin chung và danh sách chi tiết
 * các loại thuốc được kê.
 */
public class DonThuocDTO {

    private Integer id;
    private LocalDateTime ngayKeDon;
    private String loiDan;
    private int phieuKhamId; // Chỉ cần ID của phiếu khám liên quan

    // Một đơn thuốc DTO sẽ chứa một danh sách các chi tiết đơn thuốc DTO
    private List<ChiTietDonThuocDTO> chiTietDonThuoc;

    // Constructors
    public DonThuocDTO() {
    }

    public DonThuocDTO(Integer id, LocalDateTime ngayKeDon, String loiDan, int phieuKhamId, List<ChiTietDonThuocDTO> chiTietDonThuoc) {
        this.id = id;
        this.ngayKeDon = ngayKeDon;
        this.loiDan = loiDan;
        this.phieuKhamId = phieuKhamId;
        this.chiTietDonThuoc = chiTietDonThuoc;
    }

   
    // Getters and Setters...
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDateTime getNgayKeDon() {
        return ngayKeDon;
    }

    public void setNgayKeDon(LocalDateTime ngayKeDon) {
        this.ngayKeDon = ngayKeDon;
    }

    public String getLoiDan() {
        return loiDan;
    }

    public void setLoiDan(String loiDan) {
        this.loiDan = loiDan;
    }

    public int getPhieuKhamId() {
        return phieuKhamId;
    }

    public void setPhieuKhamId(int phieuKhamId) {
        this.phieuKhamId = phieuKhamId;
    }

    public List<ChiTietDonThuocDTO> getChiTietDonThuoc() {
        return chiTietDonThuoc;
    }

    public void setChiTietDonThuoc(List<ChiTietDonThuocDTO> chiTietDonThuoc) {
        this.chiTietDonThuoc = chiTietDonThuoc;
    }
}
