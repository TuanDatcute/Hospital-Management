// path: com/model/ThongBao.java
package model.Entity;

// import org.hibernate.annotations.CreationTimestamp; // <-- KHÔNG DÙNG NỮA
import org.hibernate.annotations.ColumnDefault;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ThongBao")
public class ThongBao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    // SỬA LẠI: columnDefinition phải khớp với DB (NVARCHAR(255))
    @Column(name = "tieu_de", length = 255, nullable = false, columnDefinition = "NVARCHAR(255)")
    private String tieuDe;

    @Lob
    @Column(name = "noi_dung", columnDefinition = "NVARCHAR(MAX)")
    private String noiDung;

    @Column(name = "da_doc", nullable = false, columnDefinition = "bit default 0")
    private boolean daDoc = false;

    // === LỖI NẰM Ở ĐÂY ===
    // @CreationTimestamp // <-- XÓA DÒNG NÀY ĐI
    // ======================
    @Column(name = "thoi_gian_gui", updatable = false, nullable = false) // Đảm bảo NOT NULL
    private LocalDateTime thoiGianGui;

    // SỬA LẠI: columnDefinition cho trạng thái nên là NVARCHAR(50)
    @Column(name = "trang_thai", length = 50, nullable = false, columnDefinition = "NVARCHAR(50)")
    @ColumnDefault("'HOAT_DONG'")
    private String trangThai; // 'HOAT_DONG', 'NGUNG_HOAT_DON'

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tai_khoan_id", nullable = false)
    private TaiKhoan taiKhoan;

    // --- Constructors ---
    public ThongBao() {
    }

    // --- Getters and Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

}
