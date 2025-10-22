// path: com/model/ThongBao.java
package model.Entity;

import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ThongBao")
public class ThongBao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "tieu_de", length = 255, nullable = false)
    private String tieuDe;

    @Lob // Dùng @Lob cho kiểu TEXT để hỗ trợ nội dung dài
    @Column(name = "noi_dung")
    private String noiDung;

    // Đặt giá trị mặc định trực tiếp trong CSDL
    @Column(name = "da_doc", nullable = false, columnDefinition = "bit default 0")
    private boolean daDoc = false; // Giá trị mặc định trong Java

    @CreationTimestamp // Tự động gán thời gian khi tạo mới
    @Column(name = "thoi_gian_gui", updatable = false)
    private LocalDateTime thoiGianGui;

    // Mối quan hệ: Nhiều thông báo thuộc về MỘT tài khoản
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tai_khoan_id", nullable = false)
    private TaiKhoan taiKhoan;

    // --- Constructors ---
    public ThongBao() {
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public boolean isDaDoc() {
        return daDoc;
    }

    public void setDaDoc(boolean daDoc) {
        this.daDoc = daDoc;
    }

    public LocalDateTime getThoiGianGui() {
        return thoiGianGui;
    }

    public void setThoiGianGui(LocalDateTime thoiGianGui) {
        this.thoiGianGui = thoiGianGui;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }
}