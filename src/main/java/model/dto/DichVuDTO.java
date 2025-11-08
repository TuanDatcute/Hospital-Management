package model.dto; // DTO nên nằm trong package riêng

import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object)
 */
public class DichVuDTO {

    private Integer id;
    private String tenDichVu;
    private String moTa;
    private BigDecimal donGia;
    private String trangThai;

    // --- Constructors ---
    public DichVuDTO() {
    }

    public DichVuDTO(Integer id, String tenDichVu, String moTa, BigDecimal donGia) {
        this.id = id;
        this.tenDichVu = tenDichVu;
        this.moTa = moTa;
        this.donGia = donGia;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    // --- Getters and Setters ---
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTenDichVu() {
        return tenDichVu;
    }

    public void setTenDichVu(String tenDichVu) {
        this.tenDichVu = tenDichVu;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }
}
