// path: com/dto/PhongBenhDTO.java
package model.dto;

public class PhongBenhDTO {
    
    private Long id;
    private String tenPhong;
    private String loaiPhong;
    private Integer sucChua;
    private Long khoaId; // Chỉ lưu ID của Khoa
    private String tenKhoa; // (Thêm) Thường hữu ích để hiển thị

    // --- Constructors ---
    public PhongBenhDTO() {
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getKhoaId() {
        return khoaId;
    }

    public void setKhoaId(Long khoaId) {
        this.khoaId = khoaId;
    }

    public String getTenKhoa() {
        return tenKhoa;
    }

    public void setTenKhoa(String tenKhoa) {
        this.tenKhoa = tenKhoa;
    }
}