package model.dto;

import java.time.LocalDateTime; 

public class BenhNhanDTO {

    private int id;
    private String maBenhNhan;
    private String hoTen;
    private LocalDateTime ngaySinh; 
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

    public BenhNhanDTO(int id, String maBenhNhan, String hoTen, LocalDateTime ngaySinh, String gioiTinh, String diaChi, String soDienThoai, String nhomMau, String cccd, String tienSuBenh, Integer taiKhoanId) {
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

    public LocalDateTime getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDateTime ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

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