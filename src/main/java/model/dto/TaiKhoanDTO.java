package model.dto;

import java.time.LocalDateTime;

public class TaiKhoanDTO {

    private int id;
    private String tenDangNhap;
    // Không có matKhau
    private String email;
    private String vaiTro;
    private String trangThai;
    private LocalDateTime createdAt;

    // Constructors
    public TaiKhoanDTO() {
    }

    public TaiKhoanDTO(int id, String tenDangNhap, String email, String vaiTro, String trangThai, LocalDateTime createdAt) {
        this.id = id;
        this.tenDangNhap = tenDangNhap;
        this.email = email;
        this.vaiTro = vaiTro;
        this.trangThai = trangThai;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}