// path: com/dto/GiuongBenhDTO.java
package model.dto;

public class GiuongBenhDTO {
    
    private int id;
    private String tenGiuong;
    private String trangThai;
    
    private int phongBenhId;
    private String tenPhong; // (Thêm) Để hiển thị
    
    private int benhNhanId; // Có thể null
    private String tenBenhNhan; // (Thêm) Để hiển thị, có thể null

    // --- Constructors ---
    public GiuongBenhDTO() {
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

    public int getBenhNhanId() {
        return benhNhanId;
    }

    public void setBenhNhanId(int benhNhanId) {
        this.benhNhanId = benhNhanId;
    }

    public String getTenBenhNhan() {
        return tenBenhNhan;
    }

    public void setTenBenhNhan(String tenBenhNhan) {
        this.tenBenhNhan = tenBenhNhan;
    }
}