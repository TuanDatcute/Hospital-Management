/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Date;

/**
 *
 * @author ADMIN
 */
public class TaiKhoanDTO {
    private int taiKhoanId;
    private String tenDangNhap;
    private String matKhau;
    private String email;
    private String role;
    private boolean tranThaiTaiKhoan;
    private Date taoTaiKhoan;
    private Date capNhatTaiKhoan;

    public TaiKhoanDTO() {
    }

    public TaiKhoanDTO(int taiKhoanId, String tenDangNhap, String matKhau, String email, String role, boolean tranThaiTaiKhoan, Date taoTaiKhoan, Date capNhatTaiKhoan) {
        this.taiKhoanId = taiKhoanId;
        this.tenDangNhap = tenDangNhap;
        this.matKhau = matKhau;
        this.email = email;
        this.role = role;
        this.tranThaiTaiKhoan = tranThaiTaiKhoan;
        this.taoTaiKhoan = taoTaiKhoan;
        this.capNhatTaiKhoan = capNhatTaiKhoan;
    }

    public int getTaiKhoanId() {
        return taiKhoanId;
    }

    public void setTaiKhoanId(int taiKhoanId) {
        this.taiKhoanId = taiKhoanId;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public boolean isTrangThaiTaiKhoan() {
        return tranThaiTaiKhoan;
    }

    public void setTrangThaiTaiKhoan(boolean tranThaiTaiKhoan) {
        this.tranThaiTaiKhoan = tranThaiTaiKhoan;
    }

    public Date getTaoTaiKhoan() {
        return taoTaiKhoan;
    }

    public void setTaoTaiKhoan(Date taoTaiKhoan) {
        this.taoTaiKhoan = taoTaiKhoan;
    }

    public Date getCapNhatTaiKhoan() {
        return capNhatTaiKhoan;
    }

    public void setCapNhatTaiKhoan(Date capNhatTaiKhoan) {
        this.capNhatTaiKhoan = capNhatTaiKhoan;
    }
    
    
}
