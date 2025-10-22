package model.dto; // Giữ nguyên package

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
 * Lớp này là một Entity, đại diện cho bảng 'GiuongBenh' trong cơ sở dữ liệu.
 * Tên lớp được giữ là DTO theo yêu cầu.
 *
 * @author quang
 */
@Entity
@Table(name = "GiuongBenh") // Ánh xạ tới bảng GiuongBenh
public class GiuongBenhDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "GiuongBenhID")
    private int giuongBenhId;

    @Column(name = "TenGiuong", nullable = false, length = 100)
    private String tenGiuong;

    @Column(name = "TrangThai", length = 50)
    private String trangThai; // Ví dụ: "Trống", "Đang sử dụng"

    // --- Mối quan hệ (Relationships) ---

    // 1. Quan hệ với PhongBenh: Nhiều Giường Bệnh thuộc một Phòng Bệnh
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PhongBenhID", nullable = false) // Một giường bệnh phải thuộc về một phòng
    private PhongBenhDTO phongBenh; // Thay thế cho int phongBenhId

    // 2. Quan hệ với BenhNhan: Một Giường Bệnh có một Bệnh Nhân
    // (Và một Bệnh Nhân cũng chỉ ở một Giường Bệnh)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "BenhNhanID", nullable = true, unique = true)
    // nullable = true: Vì giường có thể trống, không có bệnh nhân.
    // unique = true: Đảm bảo một bệnh nhân không thể ở 2 giường cùng lúc.
    private BenhNhanDTO benhNhan; // Thay thế cho int benhNhanId

    // --- Constructors ---
    
    public GiuongBenhDTO() {
        // Constructor rỗng bắt buộc cho Hibernate
    }

    // Constructor đã cập nhật để nhận các đối tượng Entity
    public GiuongBenhDTO(String tenGiuong, String trangThai, PhongBenhDTO phongBenh, BenhNhanDTO benhNhan) {
        this.tenGiuong = tenGiuong;
        this.trangThai = trangThai;
        this.phongBenh = phongBenh;
        this.benhNhan = benhNhan;
    }

    // --- Getters and Setters ---
    // (Đã cập nhật getters/setters cho các đối tượng quan hệ)

    public int getGiuongBenhId() {
        return giuongBenhId;
    }

    public void setGiuongBenhId(int giuongBenhId) {
        this.giuongBenhId = giuongBenhId;
    }

    public String getTenGiuong() {
        return tenGiuong;
    }

    public void setTenGiuong(String tenGiuong) {
        this.tenGiuong = tenGiuong;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public PhongBenhDTO getPhongBenh() {
        return phongBenh;
    }

    public void setPhongBenh(PhongBenhDTO phongBenh) {
        this.phongBenh = phongBenh;
    }

    public BenhNhanDTO getBenhNhan() {
        return benhNhan;
    }

    public void setBenhNhan(BenhNhanDTO benhNhan) {
        this.benhNhan = benhNhan;
    }
}