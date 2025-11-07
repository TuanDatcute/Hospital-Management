package model.Entity; // Gói của bạn

import java.io.Serializable; 
import java.time.LocalDate; 
import java.time.LocalDateTime; 
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne; // <-- **THÊM 1: IMPORT**
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "BenhNhan")
public class BenhNhan implements Serializable { 

    private static final long serialVersionUID = 1L; 

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    
    @Column(name = "ma_benh_nhan", nullable = false, unique = true, columnDefinition = "NVARCHAR(MAX)")
    private String maBenhNhan;

    @Column(name = "ho_ten", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String hoTen;

    @Column(name = "ngay_sinh") 
    private LocalDate ngaySinh; 

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

    // Mối quan hệ với Tài khoản (Giữ nguyên)
    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "tai_khoan_id", referencedColumnName = "id", nullable = true)
    private TaiKhoan taiKhoan;
    
    // --- **THÊM 2: MỐI QUAN HỆ VỚI KHOA** ---
    // Giả định: Nhiều bệnh nhân thuộc về một khoa
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "khoa_id", referencedColumnName = "id", nullable = true)
    private Khoa khoa;
    // --- **KẾT THÚC THÊM MỚI** ---
    
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
    
    // --- **THÊM 3: GETTER/SETTER CHO KHOA** ---
    public Khoa getKhoa() {
        return khoa;
    }

    public void setKhoa(Khoa khoa) {
        this.khoa = khoa;
    }
    // --- **KẾT THÚC THÊM MỚI** ---
}