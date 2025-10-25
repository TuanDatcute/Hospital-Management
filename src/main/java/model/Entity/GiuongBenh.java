// path: com/model/GiuongBenh.java
package model.Entity;

import javax.persistence.*;

@Entity
@Table(name = "GiuongBenh")
public class GiuongBenh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "ten_giuong", length = 100, nullable = false)
    private String tenGiuong; // 'Giường 01'

    @Column(name = "trang_thai", length = 50, nullable = false)
    private String trangThai; // 'TRONG', 'DANG_SU_DUNG', 'DANG_DON_DEP', 'BAO_TRI'

    // Nhiều giường bệnh thuộc MỘT phòng bệnh
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phong_benh_id", nullable = false)
    private PhongBenh phongBenh;

    // Nhiều giường (trong lịch sử) có thể thuộc MỘT bệnh nhân,
    // nhưng tại một thời điểm, một bệnh nhân chỉ ở 1 giường.
    // Hoặc một giường có thể không có bệnh nhân (NULL).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "benh_nhan_id", nullable = true) // Cho phép giá trị NULL
    private BenhNhan benhNhan;

    // --- Constructors ---
    public GiuongBenh() {
    }

    // --- Getters and Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public PhongBenh getPhongBenh() {
        return phongBenh;
    }

    public void setPhongBenh(PhongBenh phongBenh) {
        this.phongBenh = phongBenh;
    }

    public BenhNhan getBenhNhan() {
        return benhNhan;
    }

    public void setBenhNhan(BenhNhan benhNhan) {
        this.benhNhan = benhNhan;
    }
}