/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author SunnyU
 */
@Entity
public class ThuocDTO {
    
    @Id
    private int thuocId;
    
    @Column(name = "ten_thuoc")
    private String tenThuoc;
    
    @Column(name = "hoat_chat")
    private String hoatChat;
    
    @Column(name = "don_vi_tinh")
    private String donViTinh;
    
    @Column(name = "don_gia")
    private BigDecimal donGia;
    
    @Column(name = "so_luong_ton_kho")
    private int soLuongTonKho;

    // Constructors
    public ThuocDTO() {
    }

    public ThuocDTO(String tenThuoc, String hoatChat, String donViTinh, BigDecimal donGia, int soLuongTonKho) {
        this.tenThuoc = tenThuoc;
        this.hoatChat = hoatChat;
        this.donViTinh = donViTinh;
        this.donGia = donGia;
        this.soLuongTonKho = soLuongTonKho;
    }

    public ThuocDTO(int id, String tenThuoc, String hoatChat, String donViTinh, BigDecimal donGia, int soLuongTonKho) {
        this.thuocId = id;
        this.tenThuoc = tenThuoc;
        this.hoatChat = hoatChat;
        this.donViTinh = donViTinh;
        this.donGia = donGia;
        this.soLuongTonKho = soLuongTonKho;
    }

    // Getters and Setters
    public int getThuocId() {
        return thuocId;
    }

    public void setThuocId(int thuocId) {
        this.thuocId = thuocId;
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
