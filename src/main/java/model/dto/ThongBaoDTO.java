package model.dto; // Giữ nguyên package

import java.time.LocalDateTime; // Đã đổi từ Timestamp sang LocalDateTime
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
 * Lớp này là một Entity, đại diện cho bảng 'ThongBao' trong cơ sở dữ liệu.
 * Tên lớp được giữ là DTO theo yêu cầu.
 *
 * @author quang
 */
@Entity
@Table(name = "ThongBao") // Ánh xạ tới bảng ThongBao
public class ThongBaoDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ThongBaoID")
    private int thongBaoId;

    @Column(name = "TieuDe", nullable = false, length = 255)
    private String tieuDe;

    @Column(name = "NoiDung", columnDefinition = "NVARCHAR(MAX)") // Dùng cho SQL Server (hoặc dùng @Lob)
    private String noiDung;

    @Column(name = "DaDoc", nullable = false)
    private boolean daDoc;

    @Column(name = "ThoiGianGui", nullable = false)
    private LocalDateTime thoiGianGui; // Đổi sang java.time.LocalDateTime

    // --- Mối quan hệ (Relationship) ---
    // Nhiều Thông Báo (ThongBao) thuộc về một Tài Khoản (TaiKhoan)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "TaiKhoanID", nullable = false) // Tên cột khóa ngoại
    private TaiKhoanDTO taiKhoan; // Thay thế cho int taiKhoanId

    // --- Constructors ---
    
    public ThongBaoDTO() {
        // Constructor rỗng bắt buộc cho Hibernate
    }

    // Constructor đã cập nhật
    public ThongBaoDTO(String tieuDe, String noiDung, boolean daDoc, LocalDateTime thoiGianGui, TaiKhoanDTO taiKhoan) {
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
        this.daDoc = daDoc;
        this.thoiGianGui = thoiGianGui;
        this.taiKhoan = taiKhoan;
    }

    // --- Getters and Setters ---
    // (Đã cập nhật getters/setters cho các trường quan hệ và thời gian)

    public int getThongBaoId() {
        return thongBaoId;
    }

    public void setThongBaoId(int thongBaoId) {
        this.thongBaoId = thongBaoId;
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

    public TaiKhoanDTO getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoanDTO taiKhoan) {
        this.taiKhoan = taiKhoan;
    }
}