/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dto;

import java.math.BigDecimal;

/**
 * DTO cho Thuốc (Medication). Dùng để vận chuyển dữ liệu về thuốc một cách an
 * toàn và gọn gàng.
 */
public class ThuocDTO {

    private int id;
    private String tenThuoc;
    private String hoatChat;
    private String donViTinh;
    private BigDecimal donGia;
    private int soLuongTonKho;
    private String trangThai;    
// Constructors

    public ThuocDTO() {
    }

    public ThuocDTO(int id, String tenThuoc, String hoatChat, String donViTinh, BigDecimal donGia, int soLuongTonKho) {
        this.id = id;
        this.tenThuoc = tenThuoc;
        this.hoatChat = hoatChat;
        this.donViTinh = donViTinh;
        this.donGia = donGia;
        this.soLuongTonKho = soLuongTonKho;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    // Getters and Setters...
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
}
