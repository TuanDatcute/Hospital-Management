// path: com/model/HoaDon.java
package model.Entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "HoaDon")
public class HoaDon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "ma_hoa_don", length = 20, unique = true, nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String maHoaDon;

    @CreationTimestamp 
    @Column(name = "ngay_tao", updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "tong_tien", precision = 15, scale = 2, nullable = false)
    private BigDecimal tongTien;

    // --- THAY ĐỔI Ở ĐÂY ---
    @Column(name = "trang_thai", length = 50, nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String trangThai; // Đổi từ Enum về String

    // Nhiều hóa đơn có thể thuộc về 1 bệnh nhân
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "benh_nhan_id", nullable = false)
    private BenhNhan benhNhan;

    // Một hóa đơn chỉ thuộc 1 phiếu khám (giả định)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phieu_kham_benh_id", unique = true, nullable = false)
    private PhieuKhamBenh phieuKhamBenh;

    // --- Constructors ---
    public HoaDon() {
    }

    // --- Getters and Setters (Đã cập nhật cho trangThai) ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public LocalDateTime getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDateTime ngayTao) {
        this.ngayTao = ngayTao;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public BenhNhan getBenhNhan() {
        return benhNhan;
    }

    public void setBenhNhan(BenhNhan benhNhan) {
        this.benhNhan = benhNhan;
    }

    public PhieuKhamBenh getPhieuKhamBenh() {
        return phieuKhamBenh;
    }

    public void setPhieuKhamBenh(PhieuKhamBenh phieuKhamBenh) {
        this.phieuKhamBenh = phieuKhamBenh;
    }
}