/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author SunnyU
 */
@Entity
@Table(name = "DichVu")
public class DichVuDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int dichVuId;

    @Column(name = "ten_dich_vu", nullable = false, unique = true)
    private String tenDichVu;

    @Lob
    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "don_gia", nullable = false)
    private BigDecimal donGia;

    @OneToMany(mappedBy = "dichVu")
    private Set<ChiDinhDichVuDTO> danhSachChiDinh;

    public DichVuDTO() {
    }

    public DichVuDTO(int dichVuId, String tenDichVu, String moTa, BigDecimal donGia) {
        this.dichVuId = dichVuId;
        this.tenDichVu = tenDichVu;
        this.moTa = moTa;
        this.donGia = donGia;
    }

    public int getDichVuId() {
        return dichVuId;
    }

    public void setDichVuId(int dichVuId) {
        this.dichVuId = dichVuId;
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

    public Set<ChiDinhDichVuDTO> getDanhSachChiDinh() {
        return danhSachChiDinh;
    }

    public void setDanhSachChiDinh(Set<ChiDinhDichVuDTO> danhSachChiDinh) {
        this.danhSachChiDinh = danhSachChiDinh;
    }

    
}
