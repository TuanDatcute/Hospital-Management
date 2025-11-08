package model.Entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "PasswordChangeRequest")
public class PasswordChangeRequest implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    // Token: Chuỗi duy nhất được gửi qua email. Cần là UNIQUE.
    @Column(name = "token", nullable = false, unique = true, length = 36)
    private String token;

    // NewPasswordHash: Mật khẩu mới (đã hash) chờ được xác nhận.
    @Column(name = "new_password_hash", nullable = false, length = 100)
    private String newPasswordHash;

    // TaiKhoan: Liên kết khóa ngoại đến tài khoản người dùng.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tai_khoan_id", nullable = false)
    private TaiKhoan taiKhoan;

    // ExpiryDate: Thời gian token hết hạn.
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    // Constructors
    public PasswordChangeRequest() {
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNewPasswordHash() {
        return newPasswordHash;
    }

    public void setNewPasswordHash(String newPasswordHash) {
        this.newPasswordHash = newPasswordHash;
    }

    public TaiKhoan getTaiKhoan() {
        return taiKhoan;
    }

    public void setTaiKhoan(TaiKhoan taiKhoan) {
        this.taiKhoan = taiKhoan;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }
}
