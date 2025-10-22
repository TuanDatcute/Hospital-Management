// path: com/dto/HoaDonDTO.java
package model.dto;

// import com.model.TrangThaiHoaDon; // Không cần import Enum nữa
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HoaDonDTO {
    private Long id;
    private String maHoaDon;
    private LocalDateTime ngayTao;
    private BigDecimal tongTien;
    
    // --- THAY ĐỔI Ở ĐÂY ---
    private String trangThai; // Dùng String
    
    private Long benhNhanId;
    private Long phieuKhamId;

    // Constructors
    public HoaDonDTO() {
    }

    // Getters and Setters (Đã cập nhật cho trangThai)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public Long getBenhNhanId() {
        return benhNhanId;
    }

    public void setBenhNhanId(Long benhNhanId) {
        this.benhNhanId = benhNhanId;
    }

    public Long getPhieuKhamId() {
        return phieuKhamId;
    }

    public void setPhieuKhamId(Long phieuKhamId) {
        this.phieuKhamId = phieuKhamId;
    }
}