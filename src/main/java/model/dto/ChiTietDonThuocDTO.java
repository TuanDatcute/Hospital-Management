package model.dto;

/**
 * DTO cho Chi Tiết Đơn Thuốc. Vận chuyển thông tin về một loại thuốc cụ thể
 * trong một đơn thuốc.
 */
public class ChiTietDonThuocDTO {

    private Integer id;
    private int soLuong;
    private String lieuDung;

    // ID của các đối tượng cha
    private int donThuocId;
    private int thuocId;

    // Thông tin "làm phẳng" từ Thuoc để tiện hiển thị
    private String tenThuoc;
    private String donViTinh;

    // Constructors
    public ChiTietDonThuocDTO() {
    }

    public ChiTietDonThuocDTO(Integer id, int soLuong, String lieuDung, int donThuocId, int thuocId, String tenThuoc, String donViTinh) {
        this.id = id;
        this.soLuong = soLuong;
        this.lieuDung = lieuDung;
        this.donThuocId = donThuocId;
        this.thuocId = thuocId;
        this.tenThuoc = tenThuoc;
        this.donViTinh = donViTinh;
    }

    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getLieuDung() {
        return lieuDung;
    }

    public void setLieuDung(String lieuDung) {
        this.lieuDung = lieuDung;
    }

    public int getDonThuocId() {
        return donThuocId;
    }

    public void setDonThuocId(int donThuocId) {
        this.donThuocId = donThuocId;
    }

    public int getThuocId() {
        return thuocId;
    }

    public void setThuocId(int thuocId) {
        this.thuocId = thuocId;
    }

    public String getTenThuoc() {
        return tenThuoc;
    }

    public void setTenThuoc(String tenThuoc) {
        this.tenThuoc = tenThuoc;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }
}
