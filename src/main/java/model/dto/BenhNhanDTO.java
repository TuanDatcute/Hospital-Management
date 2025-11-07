package model.dto;

import java.io.Serializable;
import java.time.LocalDate;

/**
 * DTO (Data Transfer Object) cho BenhNhan. **ĐÃ CẬP NHẬT:** Thêm tenKhoa để
 * Service "làm phẳng" dữ liệu.
 */
public class BenhNhanDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private int id;
    private String maBenhNhan;
    private String hoTen;
    private LocalDate ngaySinh;
    private String gioiTinh;
    private String diaChi;
    private String soDienThoai;
    private String nhomMau;
    private String cccd;
    private String tienSuBenh;

    private Integer taiKhoanId;
    private Integer khoaId;

    // === BẮT ĐẦU THÊM MỚI (Từ hướng dẫn) ===
    /**
     * Tên khoa (đã được "làm phẳng" từ Service)
     */
    private String tenKhoa;
    // === KẾT THÚC THÊM MỚI ===

    // Constructors
    public BenhNhanDTO() {
    }

    // (Bạn có thể giữ nguyên constructor cũ, trường 'tenKhoa' sẽ được
    // gán qua setter bởi Service, không cần gán trong constructor)
    public BenhNhanDTO(int id, String maBenhNhan, String hoTen, LocalDate ngaySinh, String gioiTinh, String diaChi, String soDienThoai, String nhomMau, String cccd, String tienSuBenh, Integer taiKhoanId, Integer khoaId) {
        this.id = id;
        this.maBenhNhan = maBenhNhan;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
        this.nhomMau = nhomMau;
        this.cccd = cccd;
        this.tienSuBenh = tienSuBenh;
        this.taiKhoanId = taiKhoanId;
        this.khoaId = khoaId;
        // 'tenKhoa' sẽ được gán sau
    }

    // --- Getter/Setter ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaBenhNhan() {
        return maBenhNhan;
    }

    public void setMaBenhNhan(String maBenhNhan) {
        this.maBenhNhan = maBenhNhan;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getNhomMau() {
        return nhomMau;
    }

    public void setNhomMau(String nhomMau) {
        this.nhomMau = nhomMau;
    }

    public String getCccd() {
        return cccd;
    }

    public void setCccd(String cccd) {
        this.cccd = cccd;
    }

    public String getTienSuBenh() {
        return tienSuBenh;
    }

    public void setTienSuBenh(String tienSuBenh) {
        this.tienSuBenh = tienSuBenh;
    }

    public Integer getTaiKhoanId() {
        return taiKhoanId;
    }

    public void setTaiKhoanId(Integer taiKhoanId) {
        this.taiKhoanId = taiKhoanId;
    }

    public Integer getKhoaId() {
        return khoaId;
    }

    public void setKhoaId(Integer khoaId) {
        this.khoaId = khoaId;
    }

    // === BẮT ĐẦU THÊM MỚI (Getter/Setter cho tenKhoa) ===
    public String getTenKhoa() {
        return tenKhoa;
    }

    public void setTenKhoa(String tenKhoa) {
        this.tenKhoa = tenKhoa;
    }
    // === KẾT THÚC THÊM MỚI ===
}
