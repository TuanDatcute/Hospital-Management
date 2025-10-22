package model.dto; // Giữ nguyên package

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Lớp này là một Entity, đại diện cho bảng 'PhongBenh' trong cơ sở dữ liệu.
 *
 * @author quang
 */
@Entity

@Table(name = "PhongBenh") // Ánh xạ tới bảng PhongBenh
public class PhongBenhDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PhongBenhID")
    private int phongBenhId;

    @Column(name = "TenPhong", nullable = false, length = 100)
    private String tenPhong;

    @Column(name = "LoaiPhong", length = 50)
    private String loaiPhong;

    @Column(name = "SucChua", nullable = false)
    private int sucChua;

    // --- Mối quan hệ (Relationship) ---
    // Thay vì int khoaId, ta dùng đối tượng KhoaDTO
    // Nhiều Phòng Bệnh (PhongBenh) thuộc về một Khoa (Khoa)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "KhoaID", nullable = false) // Tên cột khóa ngoại trong bảng PhongBenh
    private KhoaDTO khoa; // Giả định Entity Khoa của bạn tên là KhoaDTO

    // --- Constructors ---
    public PhongBenhDTO() {
        // Constructor rỗng bắt buộc cho Hibernate
    }

    // Constructor đã cập nhật để nhận đối tượng KhoaDTO
    public PhongBenhDTO(String tenPhong, String loaiPhong, int sucChua, KhoaDTO khoa) {
        this.tenPhong = tenPhong;
        this.loaiPhong = loaiPhong;
        this.sucChua = sucChua;
        this.khoa = khoa;
    }

    // --- Getters and Setters ---
    // (Getters/Setters đã được cập nhật cho đối tượng 'khoa')
    public int getPhongBenhId() {
        return phongBenhId;
    }

    public void setPhongBenhId(int phongBenhId) {
        this.phongBenhId = phongBenhId;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public String getLoaiPhong() {
        return loaiPhong;
    }

    public void setLoaiPhong(String loaiPhong) {
        this.loaiPhong = loaiPhong;
    }

    public int getSucChua() {
        return sucChua;
    }

    public void setSucChua(int sucChua) {
        this.sucChua = sucChua;
    }

    public KhoaDTO getKhoa() {
        return khoa;
    }

    public void setKhoa(KhoaDTO khoa) {
        this.khoa = khoa;
    }
}
