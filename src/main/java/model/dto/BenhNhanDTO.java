package model.dto;

import java.io.Serializable; // <-- **THÊM 1: IMPORT**
import java.time.LocalDate; // <-- **THÊM 2: IMPORT (thay cho LocalDateTime)**

/**
 * DTO (Data Transfer Object) cho BenhNhan.
 * Dùng để truyền dữ liệu Bệnh nhân giữa View (JSP) và Controller/Service.
 */
public class BenhNhanDTO implements Serializable { // <-- **SỬA 3: THÊM Serializable**

    private static final long serialVersionUID = 1L; // Bắt buộc phải có

    private int id;
    private String maBenhNhan;
    private String hoTen;
    
    private LocalDate ngaySinh; // <-- **SỬA 4: Đổi kiểu dữ liệu**
    
    private String gioiTinh;
    private String diaChi;
    private String soDienThoai;
    private String nhomMau;
    private String cccd;
    private String tienSuBenh;
    
    private Integer taiKhoanId;

    // Constructors
    public BenhNhanDTO() {
    }

    // Constructor đầy đủ (Đã sửa kiểu ngaySinh)
    public BenhNhanDTO(int id, String maBenhNhan, String hoTen, LocalDate ngaySinh, String gioiTinh, String diaChi, String soDienThoai, String nhomMau, String cccd, String tienSuBenh, Integer taiKhoanId) {
        this.id = id;
        this.maBenhNhan = maBenhNhan;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
        this.nhomMau = nhomMau;
        this.cccd = cccd;
        this.tienSuBenh = tienSuBenh;
        this.taiKhoanId = taiKhoanId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaBenhNhan() {
        return maBenhNhan;
    }

    public void setMaBenhNhan(String maBenhNhan) {
        this.maBenhNhan = maBenhNhan;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    // --- **SỬA 5: ĐỔI KIỂU GETTER/SETTER** ---
    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }
    // --- **KẾT THÚC SỬA ĐỔI** ---

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getNhomMau() {
        return nhomMau;
    }

    public void setNhomMau(String nhomMau) {
        this.nhomMau = nhomMau;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getTienSuBenh() {
        return tienSuBenh;
    }

    public void setTienSuBenh(String tienSuBenh) {
        this.tienSuBenh = tienSuBenh;
    }

    public Integer getTaiKhoanId() {
        return taiKhoanId;
    }

    public void setTaiKhoanId(Integer taiKhoanId) {
        this.taiKhoanId = taiKhoanId;
    }
}