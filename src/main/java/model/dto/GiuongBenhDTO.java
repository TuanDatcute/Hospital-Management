// path: com/dto/GiuongBenhDTO.java
package model.dto;

public class GiuongBenhDTO {
    
    private Long id;
    private String tenGiuong;
    private String trangThai;
    
    private Long phongBenhId;
    private String tenPhong; // (Thêm) Để hiển thị
    
    private Long benhNhanId; // Có thể null
    private String tenBenhNhan; // (Thêm) Để hiển thị, có thể null

    // --- Constructors ---
    public GiuongBenhDTO() {
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
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

    public Long getPhongBenhId() {
        return phongBenhId;
    }

    public void setPhongBenhId(Long phongBenhId) {
        this.phongBenhId = phongBenhId;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public Long getBenhNhanId() {
        return benhNhanId;
    }

    public void setBenhNhanId(Long benhNhanId) {
        this.benhNhanId = benhNhanId;
    }

    public String getTenBenhNhan() {
        return tenBenhNhan;
    }

    public void setTenBenhNhan(String tenBenhNhan) {
        this.tenBenhNhan = tenBenhNhan;
    }
}