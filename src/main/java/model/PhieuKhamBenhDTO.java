/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

/**
 *
 * @author SunnyU
 */
@Entity
public class PhieuKhamBenhDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int phieuKhamBenhId;

    @Column(name = "ma_phieu_kham")
    private String maPhieuKham;

    @Column(name = "thoi_gian_kham")
    private LocalDateTime thoiGianKham;

    @Column(name = "trieu_chung")
    private String trieuChung;

    @Column(name = "nhiet_do")
    private BigDecimal nhietDo;

    @Column(name = "huyet_ap")
    private String huyetAp;

    @Column(name = "nhip_tim")
    private int nhipTim;

    @Column(name = "nhip_tho")
    private int nhipTho;

    @Column(name = "chan_doan")
    private String chanDoan;
    
    @Column(name = "ket_luan")
    private String ketLuan;
    
    @Column(name = "ngay_tai_kham")
    private LocalDateTime ngayTaiKham; // Ngày hẹn tái khám

    @OneToOne
    @JoinColumn(name = "benh_nhan_id")
    private BenhNhanDTO benhNhanId;
    
    @ManyToOne 
    @JoinColumn(name = "nhan_vien_id")
    private NhanVienDTO nhanVienId;
    
    @OneToOne
    @JoinColumn(name = "lich_hen_id")
    private LichHenDTO lichHenId; 

    // Constructors
    public PhieuKhamBenhDTO() {
    }

    public PhieuKhamBenhDTO(int phieuKhamBenhId, String maPhieuKham, LocalDateTime thoiGianKham, String trieuChung, BigDecimal nhietDo, String huyetAp, int nhipTim, int nhipTho, String chanDoan, String ketLuan, LocalDateTime ngayTaiKham, BenhNhanDTO benhNhanId, NhanVienDTO nhanVienId, LichHenDTO lichHenId) {
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

    public LocalDateTime getNgayTaiKham() {
        return ngayTaiKham;
    }

    public void setNgayTaiKham(LocalDateTime ngayTaiKham) {
        this.ngayTaiKham = ngayTaiKham;
    }

    public BenhNhanDTO getBenhNhanId() {
        return benhNhanId;
    }

    public void setBenhNhanId(BenhNhanDTO benhNhanId) {
        this.benhNhanId = benhNhanId;
    }

    public NhanVienDTO getNhanVienId() {
        return nhanVienId;
    }

    public void setNhanVienId(NhanVienDTO nhanVienId) {
        this.nhanVienId = nhanVienId;
    }

    public LichHenDTO getLichHenId() {
        return lichHenId;
    }

    public void setLichHenId(LichHenDTO lichHenId) {
        this.lichHenId = lichHenId;
    };

}
