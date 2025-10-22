package model.dto;

import java.math.BigDecimal;

/**
 * DTO cho Chỉ Định Dịch Vụ. Vận chuyển thông tin về một dịch vụ được chỉ định
 * trong lần khám.
 */
public class ChiDinhDichVuDTO {

    private int id;
    private String ketQua;
    private String trangThai;

    // Thông tin "làm phẳng" từ các đối tượng liên quan
    private int phieuKhamId;
    private int dichVuId;
    private String tenDichVu; // Thêm tên dịch vụ để tiện hiển thị
    private BigDecimal donGia;  // Thêm đơn giá để tiện tính tiền

    // Constructors
    public ChiDinhDichVuDTO() {
    }

    public ChiDinhDichVuDTO(int id, String ketQua, String trangThai, int phieuKhamId, int dichVuId, String tenDichVu, BigDecimal donGia) {
        this.id = id;
        this.ketQua = ketQua;
        this.trangThai = trangThai;
        this.phieuKhamId = phieuKhamId;
        this.dichVuId = dichVuId;
        this.tenDichVu = tenDichVu;
        this.donGia = donGia;
    }
    
    

    // Getters and Setters...
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKetQua() {
        return ketQua;
    }

    public void setKetQua(String ketQua) {
        this.ketQua = ketQua;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public int getPhieuKhamId() {
        return phieuKhamId;
    }

    public void setPhieuKhamId(int phieuKhamId) {
        this.phieuKhamId = phieuKhamId;
    }

    public int getDichVuId() {
        return dichVuId;
    }

    public void setDichVuId(int dichVuId) {
        this.dichVuId = dichVuId;
    }

    public String getTenDichVu() {
        return tenDichVu;
    }

    public void setTenDichVu(String tenDichVu) {
        this.tenDichVu = tenDichVu;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }
}
