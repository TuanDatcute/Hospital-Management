/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.Entity;

import java.math.BigDecimal;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author SunnyU
 */
@Entity
@Table(name = "DichVu")
public class DichVu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ten_dich_vu", nullable = false, unique = true, columnDefinition = "NVARCHAR(255)")
    private String tenDichVu;

    @Column(name = "mo_ta", columnDefinition = "NVARCHAR(MAX)")
    private String moTa;

    @Column(name = "don_gia", nullable = false)
    private BigDecimal donGia;

    @OneToMany(mappedBy = "dichVu")
    private Set<ChiDinhDichVu> danhSachChiDinh;
    @Column(name = "trang_thai", nullable = true, columnDefinition = "NVARCHAR(50)")
    private String trangThai;

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public DichVu() {
        this.trangThai = "SU_DUNG";
    }

    public DichVu(String tenDichVu, String moTa, BigDecimal donGia) {
        this.tenDichVu = tenDichVu;
        this.moTa = moTa;
        this.donGia = donGia;
    }

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

    public Set<ChiDinhDichVu> getDanhSachChiDinh() {
        return danhSachChiDinh;
    }

    public void setDanhSachChiDinh(Set<ChiDinhDichVu> danhSachChiDinh) {
        this.danhSachChiDinh = danhSachChiDinh;
    }

}
