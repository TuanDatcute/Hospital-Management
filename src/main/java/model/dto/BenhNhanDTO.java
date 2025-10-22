/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dto;

import java.time.LocalDate;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author ADMIN
 */
@Entity
public class BenhNhanDTO {
    @Id
    private int benhNhanId;
    private String maBenhNhan;
    private String hoTenBenhNhan;
    private LocalDate ngaySinhBenhNhan;
    private String gioiTinh;
    private String diaChiBenhNhan;
    private String soDienThoaiBenhNhan;
    private String nhomMau;
    private String tienSuBenh;
    private Integer taiKhoanId; // co the NULL

    public BenhNhanDTO() {
    }

    
    public BenhNhanDTO(int benhNhanId, String maBenhNhan, String hoTenBenhNhan, LocalDate ngaySinhBenhNhan, String gioiTinh, String diaChiBenhNhan, String soDienThoaiBenhNhan, String nhomMau, String tienSuBenh, Integer taiKhoanId) {
        this.benhNhanId = benhNhanId;
        this.maBenhNhan = maBenhNhan;
        this.hoTenBenhNhan = hoTenBenhNhan;
        this.ngaySinhBenhNhan = ngaySinhBenhNhan;
        this.gioiTinh = gioiTinh;
        this.diaChiBenhNhan = diaChiBenhNhan;
        this.soDienThoaiBenhNhan = soDienThoaiBenhNhan;
        this.nhomMau = nhomMau;
        this.tienSuBenh = tienSuBenh;
        this.taiKhoanId = taiKhoanId;
    }

    public int getBenhNhanId() {
        return benhNhanId;
    }

    public void setBenhNhanId(int benhNhanId) {
        this.benhNhanId = benhNhanId;
    }

    public String getMaBenhNhan() {
        return maBenhNhan;
    }

    public void setMaBenhNhan(String maBenhNhan) {
        this.maBenhNhan = maBenhNhan;
    }

    public String getHoTenBenhNhan() {
        return hoTenBenhNhan;
    }

    public void setHoTenBenhNhan(String hoTenBenhNhan) {
        this.hoTenBenhNhan = hoTenBenhNhan;
    }

    public LocalDate getNgaySinhBenhNhan() {
        return ngaySinhBenhNhan;
    }

    public void setNgaySinhBenhNhan(LocalDate ngaySinhBenhNhan) {
        this.ngaySinhBenhNhan = ngaySinhBenhNhan;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getDiaChiBenhNhan() {
        return diaChiBenhNhan;
    }

    public void setDiaChiBenhNhan(String diaChiBenhNhan) {
        this.diaChiBenhNhan = diaChiBenhNhan;
    }

    public String getSoDienThoaiBenhNhan() {
        return soDienThoaiBenhNhan;
    }

    public void setSoDienThoaiBenhNhan(String soDienThoaiBenhNhan) {
        this.soDienThoaiBenhNhan = soDienThoaiBenhNhan;
    }

    public String getNhomMau() {
        return nhomMau;
    }

    public void setNhomMau(String nhomMau) {
        this.nhomMau = nhomMau;
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
