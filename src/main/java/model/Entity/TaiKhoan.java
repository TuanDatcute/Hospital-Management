package model.Entity; // Giữ nguyên package của bạn (model.Entity)

import java.io.Serializable; // Import Serializable (rất quan trọng cho Session)
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "TaiKhoan")
public class TaiKhoan implements Serializable { // Thêm 'implements Serializable'

    private static final long serialVersionUID = 1L; // Thêm dòng này

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "ten_dang_nhap", nullable = false, unique = true, columnDefinition = "NVARCHAR(MAX)")
    private String tenDangNhap;

    @Column(name = "mat_khau", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String matKhau;

    @Column(name = "email", nullable = true, unique = false, columnDefinition = "NVARCHAR(MAX)")
    private String email;

    @Column(name = "vai_tro", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String vaiTro;

    @Column(name = "trang_thai", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String trangThai;
    
    @Column(name = "trang_thai_mat_khau", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String trangThaiMatKhau;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // --- BẮT ĐẦU THÊM MỚI (CHO XÁC THỰC EMAIL) ---
    
    /**
     * Lưu mã token duy nhất (UUID) gửi cho người dùng.
     * Sẽ là null nếu tài khoản đã được xác thực.
     */
    @Column(name = "verification_token", nullable = true)
    private String verificationToken;

    /**
     * Lưu thời điểm mà token này hết hạn (ví dụ: 24 giờ sau khi tạo).
     */
    @Column(name = "token_expiry_date", nullable = true)
    private LocalDateTime tokenExpiryDate; 
    
    // --- KẾT THÚC THÊM MỚI ---

    // Constructors
    public TaiKhoan() {
    }

    // Getters and Setters (Giữ nguyên toàn bộ getters/setters của bạn)
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTenDangNhap() {
        return tenDangNhap;
    }

    public void setTenDangNhap(String tenDangNhap) {
        this.tenDangNhap = tenDangNhap;
    }

    public String getMatKhau() {
        return matKhau;
    }

    public void setMatKhau(String matKhau) {
        this.matKhau = matKhau;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public String getTrangThaiMatKhau() {
        return trangThaiMatKhau;
    }

    public void setTrangThaiMatKhau(String trangThaiMatKhau) {
        this.trangThaiMatKhau = trangThaiMatKhau;
    }
    
    // --- THÊM GETTERS/SETTERS MỚI ---
    
    public String getVerificationToken() {
        return verificationToken;
    }

    public void setVerificationToken(String verificationToken) {
        this.verificationToken = verificationToken;
    }

    public LocalDateTime getTokenExpiryDate() {
        return tokenExpiryDate;
    }

    public void setTokenExpiryDate(LocalDateTime tokenExpiryDate) {
        this.tokenExpiryDate = tokenExpiryDate;
    }
    // --- KẾT THÚC THÊM MỚI ---
}