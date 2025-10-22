package model.dto; // Giữ nguyên package theo yêu cầu

import java.math.BigDecimal;
import java.time.LocalDate; // Sử dụng LocalDate thay vì java.sql.Date
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Lớp này là một Entity, đại diện cho bảng 'HoaDon' trong cơ sở dữ liệu.
 *
 * @author quang
 */
@Entity
@Table(name = "HoaDon") // Tên bảng trong CSDL (nên là "HoaDon")
public class HoaDonDTO { // Tên lớp được giữ nguyên

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "HoaDonID")
    private int hoaDonId;

    @Column(name = "MaHoaDon", unique = true, nullable = false, length = 50)
    private String maHoaDon;

    @Column(name = "NgayTao", nullable = false)
    private LocalDate ngayTao; // Đã đổi sang java.time.LocalDate

    @Column(name = "TongTien", nullable = false, precision = 19, scale = 2)
    private BigDecimal tongTien;

    @Column(name = "TrangThai", length = 100)
    private String trangThai;

    // --- Mối quan hệ (Relationships) ---
    // Giả định rằng Entity BenhNhan của bạn cũng tên là BenhNhanDTO
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BenhNhanID", nullable = false)
    private BenhNhanDTO benhNhan;

    // Giả định rằng Entity PhieuKhamBenh của bạn cũng tên là PhieuKhamBenhDTO
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PhieuKhamBenhID", nullable = false, unique = true)
    private PhieuKhamBenhDTO phieuKhamBenh;

    // --- Constructors ---
    
    public HoaDonDTO() {
        // Hibernate yêu cầu một constructor rỗng
    }

    // Constructor đã cập nhật để nhận các đối tượng Entity (mà bạn đang gọi là DTO)
    public HoaDonDTO(String maHoaDon, LocalDate ngayTao, BigDecimal tongTien, String trangThai, BenhNhanDTO benhNhan, PhieuKhamBenhDTO phieuKhamBenh) {
        this.maHoaDon = maHoaDon;
        this.ngayTao = ngayTao;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
        this.benhNhan = benhNhan;
        this.phieuKhamBenh = phieuKhamBenh;
    }

    // --- Getters and Setters ---
    // (Lưu ý: Getters/Setters đã được cập nhật cho các đối tượng quan hệ)

    public int getHoaDonId() {
        return hoaDonId;
    }

    public void setHoaDonId(int hoaDonId) {
        this.hoaDonId = hoaDonId;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public LocalDate getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(LocalDate ngayTao) {
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

    public BenhNhanDTO getBenhNhan() {
        return benhNhan;
    }

    public void setBenhNhan(BenhNhanDTO benhNhan) {
        this.benhNhan = benhNhan;
    }

    public PhieuKhamBenhDTO getPhieuKhamBenh() {
        return phieuKhamBenh;
    }

    public void setPhieuKhamBenh(PhieuKhamBenhDTO phieuKhamBenh) {
        this.phieuKhamBenh = phieuKhamBenh;
    }
}