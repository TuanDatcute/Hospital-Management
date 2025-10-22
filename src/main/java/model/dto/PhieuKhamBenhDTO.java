/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dto;

import model.Entity.DonThuoc;
import model.Entity.ChiDinhDichVu;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

public class PhieuKhamBenhDTO {

    // --- Thông tin cơ bản của Phiếu Khám ---
    private int id;
    private String maPhieuKham;
    private LocalDateTime thoiGianKham;
    private String trieuChung;
    private String chanDoan;
    private String ketLuan;
    private LocalDate ngayTaiKham;

    // --- Chỉ số sinh tồn ---
    private BigDecimal nhietDo;
    private String huyetAp;
    private Integer nhipTim;
    private Integer nhipTho;

    private int benhNhanId;
    private String tenBenhNhan; 
    private int bacSiId;
    private String tenBacSi;    
    private Integer lichHenId; 

    private DonThuoc donThuoc;
    private Set<ChiDinhDichVu> danhSachChiDinh;

 
    public PhieuKhamBenhDTO() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaPhieuKham() {
        return maPhieuKham;
    }

    public void setMaPhieuKham(String maPhieuKham) {
        this.maPhieuKham = maPhieuKham;
    }

    public LocalDateTime getThoiGianKham() {
        return thoiGianKham;
    }

    public void setThoiGianKham(LocalDateTime thoiGianKham) {
        this.thoiGianKham = thoiGianKham;
    }

    public String getTrieuChung() {
        return trieuChung;
    }

    public void setTrieuChung(String trieuChung) {
        this.trieuChung = trieuChung;
    }

    public String getChanDoan() {
        return chanDoan;
    }

    public void setChanDoan(String chanDoan) {
        this.chanDoan = chanDoan;
    }

    public String getKetLuan() {
        return ketLuan;
    }

    public void setKetLuan(String ketLuan) {
        this.ketLuan = ketLuan;
    }

    public LocalDate getNgayTaiKham() {
        return ngayTaiKham;
    }

    public void setNgayTaiKham(LocalDate ngayTaiKham) {
        this.ngayTaiKham = ngayTaiKham;
    }

    public BigDecimal getNhietDo() {
        return nhietDo;
    }

    public void setNhietDo(BigDecimal nhietDo) {
        this.nhietDo = nhietDo;
    }

    public String getHuyetAp() {
        return huyetAp;
    }

    public void setHuyetAp(String huyetAp) {
        this.huyetAp = huyetAp;
    }

    public Integer getNhipTim() {
        return nhipTim;
    }

    public void setNhipTim(Integer nhipTim) {
        this.nhipTim = nhipTim;
    }

    public Integer getNhipTho() {
        return nhipTho;
    }

    public void setNhipTho(Integer nhipTho) {
        this.nhipTho = nhipTho;
    }

    public int getBenhNhanId() {
        return benhNhanId;
    }

    public void setBenhNhanId(int benhNhanId) {
        this.benhNhanId = benhNhanId;
    }

    public String getTenBenhNhan() {
        return tenBenhNhan;
    }

    public void setTenBenhNhan(String tenBenhNhan) {
        this.tenBenhNhan = tenBenhNhan;
    }

    public int getBacSiId() {
        return bacSiId;
    }

    public void setBacSiId(int bacSiId) {
        this.bacSiId = bacSiId;
    }

    public String getTenBacSi() {
        return tenBacSi;
    }

    public void setTenBacSi(String tenBacSi) {
        this.tenBacSi = tenBacSi;
    }

    public Integer getLichHenId() {
        return lichHenId;
    }

    public void setLichHenId(Integer lichHenId) {
        this.lichHenId = lichHenId;
    }

    public DonThuoc getDonThuoc() {
        return donThuoc;
    }

    public void setDonThuoc(DonThuoc donThuoc) {
        this.donThuoc = donThuoc;
    }

    public Set<ChiDinhDichVu> getDanhSachChiDinh() {
        return danhSachChiDinh;
    }

    public void setDanhSachChiDinh(Set<ChiDinhDichVu> danhSachChiDinh) {
        this.danhSachChiDinh = danhSachChiDinh;
    }
}
