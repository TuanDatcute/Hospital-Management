// path: com/model/GiaoDichThanhToan.java
package model.Entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "GiaoDichThanhToan")
public class GiaoDichThanhToan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "so_tien", precision = 15, scale = 2, nullable = false)
    private BigDecimal soTien;

    @Column(name = "phuong_thuc", length = 50, nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String phuongThuc; // 'Tiền mặt', 'Chuyển khoản', 'Thẻ'

    @CreationTimestamp // Tự động gán thời gian khi tạo mới
    @Column(name = "thoi_gian_giao_dich", updatable = false)
    private LocalDateTime thoiGianGiaoDich;

    // Mối quan hệ: Nhiều giao dịch thuộc về MỘT hóa đơn
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hoa_don_id", nullable = false)
    private HoaDon hoaDon;

    // --- Constructors ---
    public GiaoDichThanhToan() {
    }

    // --- Getters and Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }
}