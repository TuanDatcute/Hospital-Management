package service;

import exception.ValidationException;
import model.Entity.TaiKhoan; // Đảm bảo đúng package 'model.Entity' (E hoa)
import model.dao.TaiKhoanDAO;
import model.dto.TaiKhoanDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional; // --- THÊM IMPORT MỚI ---
import java.util.UUID; // --- THÊM IMPORT MỚI ---
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import util.PasswordHasher;

/**
 * Lớp Service chứa logic nghiệp vụ cho TaiKhoan. Đã cập nhật: Thêm Regex cho
 * validation.
 * Lớp Service chứa logic nghiệp vụ cho TaiKhoan.
 * **ĐÃ CẬP NHẬT:** Kích hoạt Logic Xác thực Email & Quên Mật khẩu.
 */
public class TaiKhoanService {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    // --- THÊM MỚI: Định nghĩa các hằng số REGEX ---
    /**
     * Regex cho Tên đăng nhập: - ^[a-zA-Z0-9_.-]{4,30}$ - Chỉ cho phép chữ cái
     * (hoa, thường), số, dấu gạch dưới, dấu chấm, gạch ngang. - Độ dài từ 4 đến
     * 30 ký tự.
     */
    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_.-]{4,30}$";

    /**
     * Regex cho Email (một dạng phổ biến, đủ dùng): -
     * ^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,6}$
     */
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";

    /**
     * Regex cho Mật khẩu: - ^(?=.*[A-Za-z])(?=.*\d).{6,}$ - Ít nhất 6 ký tự. -
     * Ít nhất 1 chữ cái. - Ít nhất 1 số.
     */
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d).{6,}$";
    // --- KẾT THÚC THÊM MỚI ---

    // --- THÊM HẰNG SỐ TRẠNG THÁI ---
    private static final String TRANG_THAI_CHUA_XAC_THUC = "CHUA_XAC_THUC";
    private static final String TRANG_THAI_HOAT_DONG = "HOAT_DONG";
    private static final String TRANG_THAI_BI_KHOA = "BI_KHOA";
    // -----------------------------

    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_.-]{4,30}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d).{6,}$";
    
    /**
     * **CẬP NHẬT:** ĐÃ KÍCH HOẠT logic chặn đăng nhập.
     */
    public TaiKhoanDTO login(String tenDangNhap, String matKhau) throws ValidationException {
        TaiKhoan entity = taiKhoanDAO.findByTenDangNhap(tenDangNhap);
        if (entity == null) {
            throw new ValidationException("Tài khoản hoặc mật khẩu không đúng");
        }
        if (!PasswordHasher.checkPassword(matKhau, entity.getMatKhau())) {
            throw new ValidationException("Tài khoản hoặc mật khẩu không đúng");
        }

        // --- **BẮT ĐẦU KÍCH HOẠT** ---
        if (TRANG_THAI_CHUA_XAC_THUC.equals(entity.getTrangThai())) {
             throw new ValidationException("Tài khoản này chưa được kích hoạt. Vui lòng kiểm tra email của bạn (cả mục Spam) để xác thực.");
        }
        // --- **KẾT THÚC KÍCH HOẠT** ---

        if (TRANG_THAI_BI_KHOA.equals(entity.getTrangThai())) {
            throw new ValidationException("Tài khoản này đã bị khóa.");
        }
        return toDTO(entity);
    }

    /**
     * Dịch vụ tạo tài khoản mới. Đã cập nhật: Thêm Regex validation.
     */
    public TaiKhoanDTO createTaiKhoan(TaiKhoanDTO dto, String matKhau) throws ValidationException, Exception {

        // --- BƯỚC 1: VALIDATION ---
     * Dịch vụ tạo tài khoản mới.
     * **CẬP NHẬT:** Set trạng thái 'CHUA_XAC_THUC' và tạo token.
     */
    public TaiKhoanDTO createTaiKhoan(TaiKhoanDTO dto, String matKhau) throws ValidationException, Exception {
        
        // (Toàn bộ logic validation của bạn giữ nguyên...)
        if (dto.getTenDangNhap() == null || dto.getTenDangNhap().trim().isEmpty()) {
            throw new ValidationException("Tên đăng nhập không được để trống.");
        }
        if (!Pattern.matches(USERNAME_REGEX, dto.getTenDangNhap())) {
            throw new ValidationException("Tên đăng nhập không hợp lệ (chỉ gồm chữ, số, '_', '.', '-'; dài 4-30 ký tự).");
        }
        // --- KẾT THÚC CẬP NHẬT ---

        // --- CẬP NHẬT: Thêm Regex cho Mật khẩu ---
        if (matKhau == null || !Pattern.matches(PASSWORD_REGEX, matKhau)) {
            throw new ValidationException("Mật khẩu phải có ít nhất 6 ký tự, bao gồm ít nhất 1 chữ cái và 1 số.");
        }
        if (taiKhoanDAO.isTenDangNhapExisted(dto.getTenDangNhap())) {
            throw new ValidationException("Tên đăng nhập '" + dto.getTenDangNhap() + "' đã tồn tại.");
        }
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {

            // --- CẬP NHẬT: Thêm Regex cho Email ---
            if (!Pattern.matches(EMAIL_REGEX, dto.getEmail())) {
                throw new ValidationException("Định dạng Email không hợp lệ.");
            }
            if (taiKhoanDAO.isEmailExisted(dto.getEmail())) {
                throw new ValidationException("Email '" + dto.getEmail() + "' đã tồn tại.");
            }
        } else {
             throw new ValidationException("Email là bắt buộc để đăng ký.");
        }

        // (Logic chuyển đổi và thêm mới)
        String hashedMatKhau = PasswordHasher.hashPassword(matKhau);
        TaiKhoan entity = toEntity(dto, hashedMatKhau);
        
        // --- **BẮT ĐẦU CẬP NHẬT LOGIC TOKEN** ---
        String token = UUID.randomUUID().toString();
        entity.setVerificationToken(token);
        entity.setTokenExpiryDate(LocalDateTime.now().plusHours(24)); // Hết hạn sau 24h
        
        // Set trạng thái mặc định là CHUA_XAC_THUC
        entity.setTrangThai(TRANG_THAI_CHUA_XAC_THUC); 
        // --- **KẾT THÚC CẬP NHẬT LOGIC TOKEN** ---

        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        
        String vaiTro = dto.getVaiTro();
        if ("BENH_NHAN".equals(vaiTro)) {
            entity.setTrangThaiMatKhau("DA_DOI");
        } else {
            entity.setTrangThaiMatKhau("CAN_DOI");
        }

        try {
            TaiKhoan savedEntity = taiKhoanDAO.create(entity);
            return (savedEntity != null) ? toDTO(savedEntity) : null;
        } catch (RuntimeException e) {
            // Bắt lỗi từ DAO (ví dụ: unique constraint)
            throw new Exception("Lỗi CSDL khi tạo tài khoản: " + e.getMessage(), e);
        }
    }

    /**
     * Dịch vụ thay đổi mật khẩu. Đã cập nhật: Thêm Regex validation cho mật
     * khẩu mới.
     * Dịch vụ thay đổi mật khẩu (cho người đã đăng nhập).
     */
    public void changePassword(int id, String oldPassword, String newPassword) throws ValidationException, Exception {
        TaiKhoan entity = taiKhoanDAO.getById(id);
        if (entity == null) {
            throw new ValidationException("Không tìm thấy tài khoản với ID: " + id);
        }
        if (!TRANG_THAI_HOAT_DONG.equals(entity.getTrangThai())) {
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
        entity.setTrangThaiMatKhau("DA_DOI"); 
        
        try {
            taiKhoanDAO.update(entity); // Hàm update mới là void
        } catch (RuntimeException e) {
            // Bắt lỗi RuntimeException từ DAO
            throw new Exception("Cập nhật mật khẩu thất bại do lỗi CSDL: " + e.getMessage(), e);
        }
    }

    // =================================================================
    // CÁC HÀM XÁC THỰC EMAIL (Giữ nguyên)
    // =================================================================

    public String findVerificationTokenByEmail(String email) throws ValidationException {
        return taiKhoanDAO.findVerificationTokenByEmail(email)
                .orElseThrow(() -> new ValidationException("Không tìm thấy token cho email: " + email));
    }
    
    public void verifyToken(String token) throws ValidationException, Exception {
        if (token == null || token.isEmpty()) {
            throw new ValidationException("Token không hợp lệ.");
        }

        Optional<TaiKhoan> optEntity = taiKhoanDAO.findByVerificationToken(token);
        
        if (!optEntity.isPresent()) {
            throw new ValidationException("Link xác thực không hợp lệ hoặc đã được sử dụng.");
        }
        
        TaiKhoan entity = optEntity.get();
        
        if (entity.getTokenExpiryDate() != null && entity.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            try {
                 taiKhoanDAO.delete(entity); 
            } catch (RuntimeException e) {
                 throw new Exception("Lỗi khi xóa tài khoản hết hạn: " + e.getMessage(), e);
            }
            throw new ValidationException("Link xác thực đã hết hạn. Vui lòng đăng ký lại.");
        }
        
        entity.setTrangThai(TRANG_THAI_HOAT_DONG);
        entity.setVerificationToken(null); 
        entity.setTokenExpiryDate(null);
        entity.setUpdatedAt(LocalDateTime.now());
        
        try {
            taiKhoanDAO.update(entity);
        } catch (RuntimeException e) {
            throw new Exception("Lỗi hệ thống: Kích hoạt tài khoản thất bại: " + e.getMessage(), e);
        }
    }
    
    // =================================================================
    // **BẮT ĐẦU THÊM MỚI: CÁC HÀM QUÊN MẬT KHẨU**
    // =================================================================

    /**
     * **HÀM MỚI (Quên Mật khẩu - Giai đoạn 1):**
     * Tạo và lưu token reset khi người dùng yêu cầu.
     * @param email Email người dùng nhập
     * @return Toàn bộ Entity TaiKhoan (để Controller lấy tên và token)
     * @throws ValidationException nếu email rỗng
     */
    public TaiKhoan generatePasswordResetToken(String email) throws ValidationException, Exception {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Vui lòng nhập email.");
        }
        if (!Pattern.matches(EMAIL_REGEX, email)) {
            throw new ValidationException("Định dạng email không hợp lệ.");
        }

        Optional<TaiKhoan> optEntity = taiKhoanDAO.findByEmail(email);
        
        if (!optEntity.isPresent()) {
            // MẸO BẢO MẬT: Không báo lỗi "Email không tồn tại".
            // Chúng ta chỉ âm thầm không làm gì cả và trả về null.
            // Controller sẽ luôn báo "Nếu email tồn tại..."
            return null; 
        }

        TaiKhoan entity = optEntity.get();
        
        // Tạo token và đặt hạn 1 giờ
        String token = UUID.randomUUID().toString();
        entity.setVerificationToken(token);
        entity.setTokenExpiryDate(LocalDateTime.now().plusHours(1)); // Chỉ cho 1 giờ
        entity.setUpdatedAt(LocalDateTime.now());

        try {
            taiKhoanDAO.update(entity);
            return entity; // Trả về entity đã được cập nhật
        } catch (RuntimeException e) {
            throw new Exception("Lỗi CSDL khi tạo token reset: " + e.getMessage(), e);
        }
    }
    
    /**
     * **HÀM MỚI (Quên Mật khẩu - Giai đoạn 2, GET):**
     * Chỉ kiểm tra token có hợp lệ để hiển thị form hay không.
     * @param token Token từ link email
     * @throws ValidationException nếu token/hết hạn
     */
    public void validatePasswordResetToken(String token) throws ValidationException {
         if (token == null || token.isEmpty()) {
            throw new ValidationException("Token không hợp lệ.");
        }
        
        Optional<TaiKhoan> optEntity = taiKhoanDAO.findByVerificationToken(token);
        if (!optEntity.isPresent()) {
            throw new ValidationException("Link đặt lại mật khẩu không hợp lệ hoặc đã được sử dụng.");
        }
        
        TaiKhoan entity = optEntity.get();
        if (entity.getTokenExpiryDate() != null && entity.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Link đặt lại mật khẩu đã hết hạn. Vui lòng yêu cầu lại.");
        }
        // Nếu không ném lỗi -> Token hợp lệ
    }

    /**
     * **HÀM MỚI (Quên Mật khẩu - Giai đoạn 2, POST):**
     * Xác thực token và đặt mật khẩu mới.
     * @param token Token từ link email
     * @param newPassword Mật khẩu mới (dạng thô)
     * @param confirmPassword Mật khẩu xác nhận (dạng thô)
     * @throws ValidationException nếu token/mật khẩu không hợp lệ
     */
    public void performPasswordReset(String token, String newPassword, String confirmPassword) throws ValidationException, Exception {
        
        // 1. Validate mật khẩu
        if (newPassword == null || newPassword.isEmpty()) {
            throw new ValidationException("Mật khẩu mới không được để trống.");
        }
         if (!newPassword.equals(confirmPassword)) {
            throw new ValidationException("Mật khẩu mới và xác nhận không khớp.");
        }
        if (!Pattern.matches(PASSWORD_REGEX, newPassword)) {
            throw new ValidationException("Mật khẩu mới phải có ít nhất 6 ký tự, bao gồm ít nhất 1 chữ cái và 1 số.");
        }

        // 2. Validate token (Kiểm tra lại 1 lần nữa)
        Optional<TaiKhoan> optEntity = taiKhoanDAO.findByVerificationToken(token);
        if (!optEntity.isPresent()) {
            throw new ValidationException("Link đặt lại mật khẩu không hợp lệ hoặc đã được sử dụng.");
        }

        TaiKhoan entity = optEntity.get();

        // 3. Check hết hạn (Kiểm tra lại 1 lần nữa)
        if (entity.getTokenExpiryDate() != null && entity.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Link đặt lại mật khẩu đã hết hạn. Vui lòng yêu cầu lại.");
        }

        // 4. Mọi thứ hợp lệ -> Đặt mật khẩu mới
        entity.setMatKhau(PasswordHasher.hashPassword(newPassword));
        
        // 5. VÔ HIỆU HÓA token (Rất quan trọng - dùng 1 lần)
        entity.setVerificationToken(null);
        entity.setTokenExpiryDate(null);
        entity.setUpdatedAt(LocalDateTime.now());
        
        // 6. Cập nhật CSDL
        try {
            taiKhoanDAO.update(entity);
        } catch (RuntimeException e) {
            throw new Exception("Lỗi CSDL khi reset mật khẩu: " + e.getMessage(), e);
        }
    }
    
    // =================================================================
    // CÁC HÀM CŨ (GET, UPDATE STATUS, MAPPERS...)
    // =================================================================
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
        if (!TRANG_THAI_HOAT_DONG.equals(newTrangThai) && !TRANG_THAI_BI_KHOA.equals(newTrangThai)) {
            throw new ValidationException("Trạng thái mới không hợp lệ.");
        }
        TaiKhoan entity = taiKhoanDAO.getById(id);
        if (entity == null) {
            throw new ValidationException("Không tìm thấy tài khoản với ID: " + id);
        }
        if (!entity.getTrangThai().equals(newTrangThai)) {
            entity.setTrangThai(newTrangThai);
            entity.setUpdatedAt(LocalDateTime.now());
            
            try {
                taiKhoanDAO.update(entity);
            } catch (RuntimeException e) {
                throw new Exception("Cập nhật trạng thái tài khoản thất bại: " + e.getMessage(), e);
            }
        }
        return toDTO(entity);
    }

    public TaiKhoanDTO khoaTaiKhoan(int id) throws Exception {
        return updateTrangThaiTaiKhoan(id, TRANG_THAI_BI_KHOA);
    }

    public TaiKhoanDTO moKhoaTaiKhoan(int id) throws Exception {
        return updateTrangThaiTaiKhoan(id, TRANG_THAI_HOAT_DONG);
    }

    public List<TaiKhoanDTO> getActiveAndUnassignedAccounts(String role) {
        List<TaiKhoan> entities = taiKhoanDAO.findActiveAndUnassignedAccounts(role);
        return entities.stream()
                        .map(this::toDTO)
                        .collect(Collectors.toList());
    }

    // === THÊM HÀM MỚI ===
    /**
     * Lấy danh sách các vai trò duy nhất từ CSDL.
     *
     * @return List<String> các tên vai trò.
     */
    public List<String> getDistinctRoles() {
        return taiKhoanDAO.getDistinctVaiTro();
    }
    // ======================

    /**
     * Chuyển Entity sang DTO. 
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

        
        // Không map verificationToken và tokenExpiryDate ra DTO
        
        return dto;
    }

    /**
     * Chuyển DTO sang Entity.
     */
    private TaiKhoan toEntity(TaiKhoanDTO dto, String hashedMatKhau) {
        TaiKhoan entity = new TaiKhoan();
        entity.setTenDangNhap(dto.getTenDangNhap());
        entity.setEmail(dto.getEmail());
        entity.setVaiTro(dto.getVaiTro());
        
        entity.setTrangThai(dto.getTrangThai()); 
        
        entity.setMatKhau(hashedMatKhau);
        entity.setTrangThaiMatKhau(dto.getTrangThaiMatKhau());

        return entity;
    }

}
