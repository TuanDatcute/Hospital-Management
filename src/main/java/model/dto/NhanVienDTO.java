/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dto;

import java.time.LocalDate;


/**
 *
 * @author ADMIN
 */
public class NhanVienDTO {
    private int nhanVienId;
    private String hoTenNhanVien;
    private LocalDate ngaySinhNhanVien;
    private String gioiTinh;
    private String diaChiNhanVien;
    private String soDienThoaiNhanVien;
    private String chuyenMon;
    private String bangCap;
    private String taiKhoanId;
    private int khoaId;

    public NhanVienDTO() {
    }

    public NhanVienDTO(int nhanVienId, String hoTenNhanVien, LocalDate ngaySinhNhanVien, String gioiTinh, String diaChiNhanVien, String soDienThoaiNhanVien, String chuyenMon, String bangCap, String taiKhoanId, int khoaId) {
        this.nhanVienId = nhanVienId;
        this.hoTenNhanVien = hoTenNhanVien;
        this.ngaySinhNhanVien = ngaySinhNhanVien;
        this.gioiTinh = gioiTinh;
        this.diaChiNhanVien = diaChiNhanVien;
        this.soDienThoaiNhanVien = soDienThoaiNhanVien;
        this.chuyenMon = chuyenMon;
        this.bangCap = bangCap;
        this.taiKhoanId = taiKhoanId;
        this.khoaId = khoaId;
    }

    public int getNhanVienId() {
        return nhanVienId;
    }

    public void setNhanVienId(int nhanVienId) {
        this.nhanVienId = nhanVienId;
    }

    public String getHoTenNhanVien() {
        return hoTenNhanVien;
    }

    public void setHoTenNhanVien(String hoTenNhanVien) {
        this.hoTenNhanVien = hoTenNhanVien;
    }

    public LocalDate getNgaySinh() {
        return ngaySinhNhanVien;
    }

    public void setNgaySinh(LocalDate ngaySinhNhanVien) {
        this.ngaySinhNhanVien = ngaySinhNhanVien;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getDiaChi() {
        return diaChiNhanVien;
    }

    public void setDiaChi(String diaChiNhanVien) {
        this.diaChiNhanVien = diaChiNhanVien;
    }

    public String getSoDienThoai() {
        return soDienThoaiNhanVien;
    }

    public void setSoDienThoai(String soDienThoaiNhanVien) {
        this.soDienThoaiNhanVien = soDienThoaiNhanVien;
    }

    public String getChuyenMon() {
        return chuyenMon;
    }

    public void setChuyenMon(String chuyenMon) {
        this.chuyenMon = chuyenMon;
    }

    public String getBangCap() {
        return bangCap;
    }

    public void setBangCap(String bangCap) {
        this.bangCap = bangCap;
    }

    public String getTaiKhoanId() {
        return taiKhoanId;
    }

    public void setTaiKhoanId(String taiKhoanId) {
        this.taiKhoanId = taiKhoanId;
    }

    public int getKhoaId() {
        return khoaId;
    }

    public void setKhoaId(int khoaId) {
        this.khoaId = khoaId;
    }
    
    
}
