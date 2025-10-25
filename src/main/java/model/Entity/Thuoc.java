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
@Table(name = "Thuoc")
public class Thuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "ten_thuoc", nullable = false, unique = true,columnDefinition = "NVARCHAR(255)")
    private String tenThuoc;

    @Column(name = "hoat_chat",columnDefinition = "NVARCHAR(255)")
    private String hoatChat;

    @Column(name = "don_vi_tinh", nullable = false,columnDefinition = "NVARCHAR(50)")
    private String donViTinh;

    @Column(name = "don_gia", nullable = false)
    private BigDecimal donGia;

    @Column(name = "so_luong_ton_kho", nullable = false)
    private int soLuongTonKho;

    @OneToMany(mappedBy = "thuoc")
    private Set<ChiTietDonThuoc> danhSachChiTietDonThuoc;

    public Thuoc() {
    }

    public Thuoc(String tenThuoc, String hoatChat, String donViTinh, BigDecimal donGia, int soLuongTonKho) {
        this.tenThuoc = tenThuoc;
        this.hoatChat = hoatChat;
        this.donViTinh = donViTinh;
        this.donGia = donGia;
        this.soLuongTonKho = soLuongTonKho;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTenThuoc() {
        return tenThuoc;
    }

    public void setTenThuoc(String tenThuoc) {
        this.tenThuoc = tenThuoc;
    }

    public String getHoatChat() {
        return hoatChat;
    }

    public void setHoatChat(String hoatChat) {
        this.hoatChat = hoatChat;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public int getSoLuongTonKho() {
        return soLuongTonKho;
    }

    public void setSoLuongTonKho(int soLuongTonKho) {
        this.soLuongTonKho = soLuongTonKho;
    }

    public Set<ChiTietDonThuoc> getDanhSachChiTietDonThuoc() {
        return danhSachChiTietDonThuoc;
    }

    public void setDanhSachChiTietDonThuoc(Set<ChiTietDonThuoc> danhSachChiTietDonThuoc) {
        this.danhSachChiTietDonThuoc = danhSachChiTietDonThuoc;
    }

    
}
