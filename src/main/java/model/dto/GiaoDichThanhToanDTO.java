// path: com/dto/GiaoDichThanhToanDTO.java
package model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GiaoDichThanhToanDTO {

    private Long id;
    private BigDecimal soTien;
    private String phuongThuc;
    private LocalDateTime thoiGianGiaoDich;
    private Long hoaDonId; // Chỉ lưu ID

    // --- Constructors ---
    public GiaoDichThanhToanDTO() {
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getSoTien() {
        return soTien;
    }

    public void setSoTien(BigDecimal soTien) {
        this.soTien = soTien;
    }

    public String getPhuongThuc() {
        return phuongThuc;
    }

    public void setPhuongThuc(String phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public LocalDateTime getThoiGianGiaoDich() {
        return thoiGianGiaoDich;
    }

    public void setThoiGianGiaoDich(LocalDateTime thoiGianGiaoDich) {
        this.thoiGianGiaoDich = thoiGianGiaoDich;
    }

    public Long getHoaDonId() {
        return hoaDonId;
    }

    public void setHoaDonId(Long hoaDonId) {
        this.hoaDonId = hoaDonId;
    }
}