package model.Entity; // Gói của bạn

import java.io.Serializable; // <-- **THÊM 1: IMPORT**
import java.time.LocalDate; // <-- **THÊM 2: IMPORT (thay cho LocalDateTime)**
import java.time.LocalDateTime; // (Giữ lại nếu bạn dùng cho các trường khác)
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "BenhNhan")
public class BenhNhan implements Serializable { // <-- **SỬA 3: THÊM Serializable**

    private static final long serialVersionUID = 1L; // Thêm dòng này

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    
    @Column(name = "ma_benh_nhan", nullable = false, unique = true, columnDefinition = "NVARCHAR(MAX)")
    private String maBenhNhan;

    @Column(name = "ho_ten", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String hoTen;

    // --- **SỬA 4: ĐỔI KIỂU DỮ LIỆU** ---
    @Column(name = "ngay_sinh") // Sẽ được map sang kiểu DATE trong CSDL
    private LocalDate ngaySinh; 
    // --- **KẾT THÚC SỬA** ---

    @Column(name = "gioi_tinh", columnDefinition = "NVARCHAR(MAX)")
    private String gioiTinh;

    @Column(name = "dia_chi", columnDefinition = "NVARCHAR(MAX)")
    private String diaChi;

    @Column(name = "so_dien_thoai", columnDefinition = "NVARCHAR(MAX)")
    private String soDienThoai;
    
    @Column(name = "cccd", length = 20, unique = true, columnDefinition = "NVARCHAR(MAX)")
    private String cccd;

    @Column(name = "nhom_mau", columnDefinition = "NVARCHAR(MAX)", nullable = true)
    private String nhomMau;

    @Column(name = "tien_su_benh", columnDefinition = "NVARCHAR(MAX)", nullable = true)
    private String tienSuBenh;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "tai_khoan_id", referencedColumnName = "id", nullable = true)
    private TaiKhoan taiKhoan;
    
    // Constructors
    public BenhNhan() {
    }

    // Getters and Setters
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

    // --- **SỬA 5: ĐỔI KIỂU GETTER/SETTER** ---
    public LocalDate getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDate ngaySinh) {
        this.ngaySinh = ngaySinh;
    }
    // --- **KẾT THÚC SỬA** ---

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

    public String getTienSuBenh() {
        return tienSuBenh;
    }

    public void setTienSuBenh(String tienSuBenh) {
        this.tienSuBenh = tienSuBenh;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }
    
    public String getCccd(){
        return cccd;
    }
    
    public void setCccd(String cccd){
        this.cccd = cccd;
    }
}