/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

/**
 *
 * @author SunnyU
 */
public class PhieuKhamBenhDTO {

    private int phieuKhamBenhId;
    private String maPhieuKham;
    private Timestamp thoiGianKham;
    private String trieuChung;
    private BigDecimal nhietDo;
    private String huyetAp;
    private int nhipTim;
    private int nhipTho;
    private String chanDoan;
    private String ketLuan;
    private Timestamp ngayTaiKham; // Ngày hẹn tái khám
    
    private int benhNhanId;
    private int nhanVienId;
    private Integer lichHenId; // Dùng Integer vì có thể null

    // Constructors
    public PhieuKhamBenhDTO() {
    }

    public PhieuKhamBenhDTO(int phieuKhamBenhId, String maPhieuKham, Timestamp thoiGianKham, String trieuChung, BigDecimal nhietDo, String huyetAp, int nhipTim, int nhipTho, String chanDoan, String ketLuan, Timestamp ngayTaiKham, int benhNhanId, int nhanVienId, Integer lichHenId) {
        this.phieuKhamBenhId = phieuKhamBenhId;
        this.maPhieuKham = maPhieuKham;
        this.thoiGianKham = thoiGianKham;
        this.trieuChung = trieuChung;
        this.nhietDo = nhietDo;
        this.huyetAp = huyetAp;
        this.nhipTim = nhipTim;
        this.nhipTho = nhipTho;
        this.chanDoan = chanDoan;
        this.ketLuan = ketLuan;
        this.ngayTaiKham = ngayTaiKham;
        this.benhNhanId = benhNhanId;
        this.nhanVienId = nhanVienId;
        this.lichHenId = lichHenId;
    }

    public int getPhieuKhamBenhId() {
        return phieuKhamBenhId;
    }

    public void setPhieuKhamBenhId(int phieuKhamBenhId) {
        this.phieuKhamBenhId = phieuKhamBenhId;
    }

    public String getMaPhieuKham() {
        return maPhieuKham;
    }

    public void setMaPhieuKham(String maPhieuKham) {
        this.maPhieuKham = maPhieuKham;
    }

    public Timestamp getThoiGianKham() {
        return thoiGianKham;
    }

    public void setThoiGianKham(Timestamp thoiGianKham) {
        this.thoiGianKham = thoiGianKham;
    }

    public String getTrieuChung() {
        return trieuChung;
    }

    public void setTrieuChung(String trieuChung) {
        this.trieuChung = trieuChung;
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

    public int getNhipTim() {
        return nhipTim;
    }

    public void setNhipTim(int nhipTim) {
        this.nhipTim = nhipTim;
    }

    public int getNhipTho() {
        return nhipTho;
    }

    public void setNhipTho(int nhipTho) {
        this.nhipTho = nhipTho;
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

    public Timestamp getNgayTaiKham() {
        return ngayTaiKham;
    }

    public void setNgayTaiKham(Timestamp ngayTaiKham) {
        this.ngayTaiKham = ngayTaiKham;
    }

    public int getBenhNhanId() {
        return benhNhanId;
    }

    public void setBenhNhanId(int benhNhanId) {
        this.benhNhanId = benhNhanId;
    }

    public int getNhanVienId() {
        return nhanVienId;
    }

    public void setNhanVienId(int nhanVienId) {
        this.nhanVienId = nhanVienId;
    }

    public Integer getLichHenId() {
        return lichHenId;
    }

    public void setLichHenId(Integer lichHenId) {
        this.lichHenId = lichHenId;
    }

  
}
