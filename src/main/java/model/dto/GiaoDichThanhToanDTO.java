package model.dto; // Giữ nguyên package

import java.math.BigDecimal;
import java.time.LocalDateTime; // Đã đổi từ java.sql.Date sang LocalDateTime
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * Lớp này là một Entity, đại diện cho bảng 'GiaoDichThanhToan' trong CSDL.
 * Tên lớp được giữ là DTO theo yêu cầu.
 *
 * @author quang
 */
@Entity
@Table(name = "GiaoDichThanhToan") // Ánh xạ tới bảng GiaoDichThanhToan
public class GiaoDichThanhToanDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GiaoDichThanhToanID")
    private int giaoDichThanhToanId;

    @Column(name = "SoTien", nullable = false, precision = 19, scale = 2)
    private BigDecimal soTien; // precision và scale rất quan trọng cho tiền tệ

    @Column(name = "PhuongThuc", length = 100)
    private String phuongThuc; // Ví dụ: "Tiền mặt", "Chuyển khoản"

    @Column(name = "ThoiGianGiaoDich", nullable = false)
    private LocalDateTime thoiGianGiaoDich; // Đổi sang LocalDateTime để lưu cả ngày và giờ

    // --- Mối quanệ (Relationship) ---
    // Nhiều Giao Dịch (GiaoDichThanhToan) thuộc về một Hóa Đơn (HoaDon)
    // (Cho phép một hóa đơn được thanh toán nhiều lần/từng phần)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "HoaDonID", nullable = false) // Tên cột khóa ngoại
    private HoaDonDTO hoaDon; // Thay thế cho int hoaDonId

    // --- Constructors ---
    
    public GiaoDichThanhToanDTO() {
        // Constructor rỗng bắt buộc cho Hibernate
    }

    // Constructor đã cập nhật
    public GiaoDichThanhToanDTO(BigDecimal soTien, String phuongThuc, LocalDateTime thoiGianGiaoDich, HoaDonDTO hoaDon) {
        this.soTien = soTien;
        this.phuongThuc = phuongThuc;
        this.thoiGianGiaoDich = thoiGianGiaoDich;
        this.hoaDon = hoaDon;
    }

    // --- Getters and Setters ---
    // (Đã cập nhật getters/setters cho các trường quan hệ và thời gian)

    public int getGiaoDichThanhToanId() {
        return giaoDichThanhToanId;
    }

    public void setGiaoDichThanhToanId(int giaoDichThanhToanId) {
        this.giaoDichThanhToanId = giaoDichThanhToanId;
    }

    public BigDecimal getSoTien() {
        return soTien;
    }

    public void setSoTien(BigDecimal soTien) {
        this.soTien = soTien;
    }

    public String getPhuongThuc() {
        return phuongThuc;
    }

    public void setPhuongThuc(String phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public LocalDateTime getThoiGianGiaoDich() {
        return thoiGianGiaoDich;
    }

    public void setThoiGianGiaoDich(LocalDateTime thoiGianGiaoDich) {
        this.thoiGianGiaoDich = thoiGianGiaoDich;
    }

    public HoaDonDTO getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDonDTO hoaDon) {
        this.hoaDon = hoaDon;
    }
}