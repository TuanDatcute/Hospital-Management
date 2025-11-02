package model.dto;

import java.io.Serializable; // Bắt buộc phải import
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) cho TaiKhoan.
 * Triển khai Serializable để có thể lưu trữ an toàn trong HttpSession.
 */
public class TaiKhoanDTO implements Serializable { // <<< ĐÃ THÊM SERIALIZABLE
    
    private static final long serialVersionUID = 1L; // Thêm serialVersionUID (khuyến nghị)

    private int id;
    private String tenDangNhap;
    // Không có matKhau
    private String email;
    private String vaiTro;
    private String trangThai;
    private String trangThaiMatKhau;
    private LocalDateTime createdAt;

    // Constructors
    public TaiKhoanDTO() {
    }

    public TaiKhoanDTO(int id, String tenDangNhap, String email, String vaiTro, String trangThai, String trangThaiMatKhau, LocalDateTime createdAt) {
        this.id = id;
        this.tenDangNhap = tenDangNhap;
        this.email = email;
        this.vaiTro = vaiTro;
        this.trangThai = trangThai;
        this.trangThaiMatKhau = trangThaiMatKhau;
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
    
    public String getTrangThaiMatKhau() {
        return trangThaiMatKhau;
    }

    public void setTrangThaiMatKhau(String trangThaiMatKhau) {
        this.trangThaiMatKhau = trangThaiMatKhau;
    }
}