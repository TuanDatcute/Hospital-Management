package service;

import exception.ValidationException;
import model.Entity.TaiKhoan; // Đảm bảo đúng package 'model.Entity' (E hoa)
import model.dao.TaiKhoanDAO;
import model.dto.TaiKhoanDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import util.PasswordHasher;

/**
 * Lớp Service chứa logic nghiệp vụ cho TaiKhoan.
 * **ĐÃ CẬP NHẬT:** Thêm logic Xác thực Email (Verification).
 */
public class TaiKhoanService {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    // (Các hằng số REGEX... giữ nguyên)
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_.-]{4,30}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d).{6,}$";
    
    /**
     * **CẬP NHẬT:** Thêm logic chặn đăng nhập cho tài khoản chưa xác thực.
     */
    public TaiKhoanDTO login(String tenDangNhap, String matKhau) throws ValidationException {
        TaiKhoan entity = taiKhoanDAO.findByTenDangNhap(tenDangNhap);
        if (entity == null) {
            throw new ValidationException("Tài khoản hoặc mật khẩu không đúng");
        }
        if (!PasswordHasher.checkPassword(matKhau, entity.getMatKhau())) {
            throw new ValidationException("Tài khoản hoặc mật khẩu không đúng");
        }

        // --- **BẮT ĐẦU CẬP NHẬT** ---
        if ("CHUA_XAC_THUC".equals(entity.getTrangThai())) {
             throw new ValidationException("Tài khoản này chưa được kích hoạt. Vui lòng kiểm tra email của bạn (cả mục Spam) để xác thực.");
        }
        // --- **KẾT THÚC CẬP NHẬT** ---

        if ("BI_KHOA".equals(entity.getTrangThai())) {
            throw new ValidationException("Tài khoản này đã bị khóa.");
        }
        return toDTO(entity);
    }

    /**
     * Dịch vụ tạo tài khoản mới.
     * (Giữ nguyên logic - Controller sẽ set trạng thái "CHUA_XAC_THUC" trên DTO)
     */
    public TaiKhoanDTO createTaiKhoan(TaiKhoanDTO dto, String matKhau) throws ValidationException, Exception {
        
        // (Toàn bộ logic validation của bạn giữ nguyên...)
        if (dto.getTenDangNhap() == null || dto.getTenDangNhap().trim().isEmpty()) {
            throw new ValidationException("Tên đăng nhập không được để trống.");
        }
        if (!Pattern.matches(USERNAME_REGEX, dto.getTenDangNhap())) {
            throw new ValidationException("Tên đăng nhập không hợp lệ (chỉ gồm chữ, số, '_', '.', '-'; dài 4-30 ký tự).");
        }
        if (matKhau == null || !Pattern.matches(PASSWORD_REGEX, matKhau)) {
            throw new ValidationException("Mật khẩu phải có ít nhất 6 ký tự, bao gồm ít nhất 1 chữ cái và 1 số.");
        }
        if (taiKhoanDAO.isTenDangNhapExisted(dto.getTenDangNhap())) {
            throw new ValidationException("Tên đăng nhập '" + dto.getTenDangNhap() + "' đã tồn tại.");
        }
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            if (!Pattern.matches(EMAIL_REGEX, dto.getEmail())) {
                throw new ValidationException("Định dạng Email không hợp lệ.");
            }
            if (taiKhoanDAO.isEmailExisted(dto.getEmail())) {
                throw new ValidationException("Email '" + dto.getEmail() + "' đã tồn tại.");
            }
        }

        // (Logic chuyển đổi và thêm mới giữ nguyên...)
        String hashedMatKhau = PasswordHasher.hashPassword(matKhau);
        TaiKhoan entity = toEntity(dto, hashedMatKhau);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        
        // **QUAN TRỌNG:** Logic này sẽ tôn trọng trạng thái "CHUA_XAC_THUC"
        // mà Controller gửi xuống trong DTO.
        if (dto.getTrangThai() == null || dto.getTrangThai().isEmpty()) {
            // Nếu Admin tạo mà không set, mặc định là HOAT_DONG (cho nhân viên)
            entity.setTrangThai("HOAT_DONG");
        }

        String vaiTro = dto.getVaiTro();
        if ("BENH_NHAN".equals(vaiTro)) {
            entity.setTrangThaiMatKhau("DA_DOI");
            // Nếu là Bệnh nhân tự đăng ký, Controller đã setTrangThai("CHUA_XAC_THUC")
            // nên code này vẫn chạy đúng.
        } else {
            entity.setTrangThaiMatKhau("CAN_DOI");
        }

        TaiKhoan savedEntity = taiKhoanDAO.create(entity);
        return (savedEntity != null) ? toDTO(savedEntity) : null;
    }

    /**
     * Dịch vụ thay đổi mật khẩu.
     * (Giữ nguyên)
     */
    public void changePassword(int id, String oldPassword, String newPassword) throws ValidationException, Exception {
        TaiKhoan entity = taiKhoanDAO.getById(id);
        if (entity == null) {
            throw new ValidationException("Không tìm thấy tài khoản với ID: " + id);
        }
        if (!"HOAT_DONG".equals(entity.getTrangThai())) {
            throw new ValidationException("Không thể đổi mật khẩu cho tài khoản đang bị khóa.");
        }
        if (!PasswordHasher.checkPassword(oldPassword, entity.getMatKhau())) {
            throw new ValidationException("Mật khẩu cũ không chính xác.");
        }
        if (newPassword == null || !Pattern.matches(PASSWORD_REGEX, newPassword)) {
            throw new ValidationException("Mật khẩu mới phải có ít nhất 6 ký tự, bao gồm ít nhất 1 chữ cái và 1 số.");
        }
        if (PasswordHasher.checkPassword(newPassword, entity.getMatKhau())) {
            throw new ValidationException("Mật khẩu mới không được trùng với mật khẩu cũ.");
        }
        
        String hashedNewPassword = PasswordHasher.hashPassword(newPassword);
        entity.setMatKhau(hashedNewPassword);
        entity.setUpdatedAt(LocalDateTime.now());
        entity.setTrangThaiMatKhau("DA_DOI"); 
        
        if (!taiKhoanDAO.update(entity)) {
            throw new Exception("Cập nhật mật khẩu thất bại.");
        }
    }

    // =================================================================
    // CÁC HÀM MỚI CHO LOGIC XÁC THỰC EMAIL
    // =================================================================

    /**
     * **HÀM MỚI:** Lưu token xác thực và thời gian hết hạn vào tài khoản.
     * @param taiKhoanId ID của tài khoản vừa tạo
     * @param token Mã token (UUID)
     */
    public void saveVerificationToken(int taiKhoanId, String token) throws Exception {
        TaiKhoan entity = taiKhoanDAO.getById(taiKhoanId);
        if (entity == null) {
            // Lỗi này không nên xảy ra, vì chúng ta vừa tạo tài khoản
            throw new Exception("Lỗi nghiêm trọng: Không tìm thấy tài khoản (ID: " + taiKhoanId + ") để lưu token.");
        }
        
        entity.setVerificationToken(token);
        // Đặt thời gian hết hạn là 24 giờ kể từ bây giờ
        entity.setTokenExpiryDate(LocalDateTime.now().plusHours(24));
        
        if (!taiKhoanDAO.update(entity)) {
            throw new Exception("Lỗi hệ thống: Cập nhật token thất bại.");
        }
    }

    /**
     * **HÀM MỚI:** Xác thực token người dùng gửi lên.
     * @param token Mã token từ link email
     */
    public void verifyToken(String token) throws ValidationException, Exception {
        // Dùng hàm DAO mới mà chúng ta đã thêm ở Bước 1
        TaiKhoan entity = taiKhoanDAO.findByVerificationToken(token);
        
        if (entity == null) {
            throw new ValidationException("Link xác thực không hợp lệ, sai token hoặc đã được sử dụng.");
        }
        
        // Kiểm tra xem token đã hết hạn chưa
        if (entity.getTokenExpiryDate() != null && entity.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            // (Nâng cao: Có thể xóa tài khoản này và bắt đăng ký lại)
            throw new ValidationException("Link xác thực đã hết hạn. Vui lòng đăng ký lại.");
        }
        
        // Xác thực thành công!
        entity.setTrangThai("HOAT_DONG");
        entity.setVerificationToken(null); // Vô hiệu hóa token (rất quan trọng)
        entity.setTokenExpiryDate(null);
        entity.setUpdatedAt(LocalDateTime.now());
        
        if (!taiKhoanDAO.update(entity)) {
            throw new Exception("Lỗi hệ thống: Kích hoạt tài khoản thất bại.");
        }
    }

    // =================================================================
    // CÁC HÀM CŨ (GET, UPDATE STATUS, MAPPERS...)
    // =================================================================
    // (Giữ nguyên toàn bộ các hàm này)

    public TaiKhoanDTO getTaiKhoanById(int id) throws ValidationException, Exception {
        TaiKhoan entity = taiKhoanDAO.getById(id);
        if (entity == null) {
            throw new ValidationException("Không tìm thấy tài khoản với ID: " + id);
        }
        return toDTO(entity);
    }

    public List<TaiKhoanDTO> getAllTaiKhoan() {
        List<TaiKhoan> entities = taiKhoanDAO.getAll();
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public TaiKhoanDTO updateTrangThaiTaiKhoan(int id, String newTrangThai) throws ValidationException, Exception {
        if (!"HOAT_DONG".equals(newTrangThai) && !"BI_KHOA".equals(newTrangThai)) {
            throw new ValidationException("Trạng thái mới không hợp lệ.");
        }
        TaiKhoan entity = taiKhoanDAO.getById(id);
        if (entity == null) {
            throw new ValidationException("Không tìm thấy tài khoản với ID: " + id);
        }
        if (!entity.getTrangThai().equals(newTrangThai)) {
            entity.setTrangThai(newTrangThai);
            entity.setUpdatedAt(LocalDateTime.now());
            if (!taiKhoanDAO.update(entity)) {
                throw new Exception("Cập nhật trạng thái tài khoản thất bại.");
            }
        }
        return toDTO(entity);
    }

    public TaiKhoanDTO khoaTaiKhoan(int id) throws Exception {
        return updateTrangThaiTaiKhoan(id, "BI_KHOA");
    }

    public TaiKhoanDTO moKhoaTaiKhoan(int id) throws Exception {
        return updateTrangThaiTaiKhoan(id, "HOAT_DONG");
    }

    public List<TaiKhoanDTO> getActiveAndUnassignedAccounts(String role) {
        List<TaiKhoan> entities = taiKhoanDAO.findActiveAndUnassignedAccounts(role);
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Chuyển Entity sang DTO. 
     * (Giữ nguyên - Không cần trả token về DTO)
     */
    private TaiKhoanDTO toDTO(TaiKhoan entity) {
        if (entity == null) {
            return null;
        }
        TaiKhoanDTO dto = new TaiKhoanDTO();
        dto.setId(entity.getId());
        dto.setTenDangNhap(entity.getTenDangNhap());
        dto.setEmail(entity.getEmail());
        dto.setVaiTro(entity.getVaiTro());
        dto.setTrangThai(entity.getTrangThai());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setTrangThaiMatKhau(entity.getTrangThaiMatKhau());
        
        // Không trả về token hay expiry date
        
        return dto;
    }

    /**
     * Chuyển DTO sang Entity.
     * **CẬP NHẬT:** Chuyển cả 2 trường token mới (mặc định là null).
     */
    private TaiKhoan toEntity(TaiKhoanDTO dto, String hashedMatKhau) {
        TaiKhoan entity = new TaiKhoan();
        entity.setTenDangNhap(dto.getTenDangNhap());
        entity.setEmail(dto.getEmail());
        entity.setVaiTro(dto.getVaiTro());
        entity.setTrangThai(dto.getTrangThai());
        entity.setMatKhau(hashedMatKhau);
        entity.setTrangThaiMatKhau(dto.getTrangThaiMatKhau()); 
        
        // (Hai trường token mới sẽ là null khi tạo, điều này là đúng)
        
        return entity;
    }
    
}