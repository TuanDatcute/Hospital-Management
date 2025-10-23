/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import model.dto.BenhNhanDTO;
import model.dto.LichHenDTO;
import model.dto.NhanVienDTO;

/**
 *
 * @author SunnyU
 */
@Entity
@Table(name = "PhieuKhamBenh")
public class PhieuKhamBenh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "ma_phieu_kham", nullable = false, unique = true)
    private String maPhieuKham;

    @Column(name = "thoi_gian_kham", nullable = false)
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "benh_nhan_id", nullable = false)
    private BenhNhan benhNhan;
    
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "nhan_vien_id", nullable = false)
    private NhanVien bacSi;
    
    @OneToOne
    @JoinColumn(name = "lich_hen_id")
    private LichHen lichHen ; 
    
    @OneToOne(mappedBy = "phieuKham", cascade = CascadeType.ALL)
    private DonThuoc donThuoc;
    
    @OneToMany(mappedBy = "phieuKham", cascade = CascadeType.ALL)
    private Set<ChiDinhDichVu> danhSachChiDinh;

    public PhieuKhamBenh() {
    }

    public PhieuKhamBenh(String maPhieuKham, LocalDateTime thoiGianKham, String trieuChung, BigDecimal nhietDo, String huyetAp, int nhipTim, int nhipTho, String chanDoan, String ketLuan, LocalDateTime ngayTaiKham, BenhNhan benhNhan, NhanVien bacSi, LichHen lichHen) {
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
        this.benhNhan = benhNhan;
        this.bacSi = bacSi;
        this.lichHen = lichHen;
    }

    public int getId() {
        return id;
    }

    public void setId(int phieuKhamBenhId) {
        this.id = phieuKhamBenhId;
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

    public BenhNhan getBenhNhan() {
        return benhNhan;
    }

    public void setBenhNhan(BenhNhan benhNhan) {
        this.benhNhan = benhNhan;
    }

    public NhanVien getBacSi() {
        return bacSi;
    }

    public void setBacSi(NhanVien bacSi) {
        this.bacSi = bacSi;
    }

    public LichHen getLichHen() {
        return lichHen;
    }

    public void setLichHen(LichHen lichHen) {
        this.lichHen = lichHen;
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
