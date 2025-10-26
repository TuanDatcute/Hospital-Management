// path: com/model/PhongBenh.java
package model.Entity;

import javax.persistence.*;

@Entity
@Table(name = "PhongBenh")
public class PhongBenh {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "ten_phong", length = 100, unique = true, nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String tenPhong;

    @Column(name = "loai_phong", length = 100, columnDefinition = "NVARCHAR(MAX)")
    private String loaiPhong; // 'Phòng thường', 'Phòng VIP', 'Phòng cấp cứu'

    @Column(name = "suc_chua")
    private Integer sucChua; // Dùng Integer để có thể null nếu muốn

    // Mối quan hệ: Nhiều phòng bệnh thuộc MỘT khoa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "khoa_id", nullable = false)
    private Khoa khoa;

    // --- Constructors ---
    public PhongBenh() {
    }

    // --- Getters and Setters ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public Integer getSucChua() {
        return sucChua;
    }

    public void setSucChua(Integer sucChua) {
        this.sucChua = sucChua;
    }

    public Khoa getKhoa() {
        return khoa;
    }

    public void setKhoa(Khoa khoa) {
        this.khoa = khoa;
    }
}