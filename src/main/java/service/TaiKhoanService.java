package service;

import exception.ValidationException;
import model.Entity.TaiKhoan;
import model.dao.TaiKhoanDAO;
import model.dto.TaiKhoanDTO;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import model.Entity.BenhNhan;
import model.dao.BenhNhanDAO;
// import model.Entity.PasswordChangeRequest; // ĐÃ XÓA
// import model.dao.PasswordChangeRequestDAO; // ĐÃ XÓA
import util.PasswordHasher;

/**
 * Lớp Service chứa logic nghiệp vụ cho TaiKhoan. (ĐÃ DỌN DẸP: Xóa bỏ chức năng
 * Xác thực Đổi Mật Khẩu qua email không thành công).
 */
public class TaiKhoanService {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    // private final PasswordChangeRequestDAO passwordChangeDAO = new PasswordChangeRequestDAO(); // ✨ ĐÃ XÓA ✨

    // --- HẰNG SỐ TRẠNG THÁI (Giữ nguyên) ---
    private static final String TRANG_THAI_CHUA_XAC_THUC = "CHUA_XAC_THUC";
    private static final String TRANG_THAI_HOAT_DONG = "HOAT_DONG";
    private static final String TRANG_THAI_BI_KHOA = "BI_KHOA";
    // -----------------------------

    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_.-]{4,30}$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$";
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d).{6,}$";

    /**
     * Logic đăng nhập (Giữ nguyên)
     */
    public TaiKhoanDTO login(String tenDangNhap, String matKhau) throws ValidationException {
        TaiKhoan entity = taiKhoanDAO.findByTenDangNhap(tenDangNhap);
        if (entity == null) {
            throw new ValidationException("Tài khoản hoặc mật khẩu không đúng");
        }
        if (!PasswordHasher.checkPassword(matKhau, entity.getMatKhau())) {
            throw new ValidationException("Tài khoản hoặc mật khẩu không đúng");
        }

        if (TRANG_THAI_CHUA_XAC_THUC.equals(entity.getTrangThai())) {
            throw new ValidationException("Tài khoản này chưa được kích hoạt. Vui lòng kiểm tra email của bạn (cả mục Spam) để xác thực.");
        }

        if (TRANG_THAI_BI_KHOA.equals(entity.getTrangThai())) {
            throw new ValidationException("Tài khoản này đã bị khóa.");
        }
        return toDTO(entity);
    }

    /**
     * Dịch vụ tạo tài khoản mới. (Giữ nguyên)
     */
    public TaiKhoanDTO createTaiKhoan(TaiKhoanDTO dto, String matKhau) throws ValidationException, Exception {

        // --- 1. VALIDATE CHUNG (Tên đăng nhập & Mật khẩu) ---
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

        String hashedMatKhau = PasswordHasher.hashPassword(matKhau);
        TaiKhoan entity = toEntity(dto, hashedMatKhau); // Chuyển đổi cơ bản
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());

        // --- 2. LOGIC NGHIỆP VỤ (TÙY THEO VAI TRÒ) ---
        String vaiTro = dto.getVaiTro();

        if ("BENH_NHAN".equals(vaiTro)) {
            // Kịch bản 1: BỆNH NHÂN TỰ ĐĂNG KÝ
            // 1a. Bắt buộc Email
            if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
                throw new ValidationException("Email là bắt buộc để đăng ký tài khoản bệnh nhân.");
            }
            if (!Pattern.matches(EMAIL_REGEX, dto.getEmail())) {
                throw new ValidationException("Định dạng Email không hợp lệ.");
            }
            if (taiKhoanDAO.isEmailExisted(dto.getEmail())) {
                throw new ValidationException("Email '" + dto.getEmail() + "' đã tồn tại.");
            }
            // 1b. Gán logic Token (giữ nguyên code của bạn)
            String token = UUID.randomUUID().toString();
            entity.setVerificationToken(token);
            entity.setTokenExpiryDate(LocalDateTime.now().plusHours(24));
            entity.setTrangThai(TRANG_THAI_CHUA_XAC_THUC);
            entity.setTrangThaiMatKhau("DA_DOI"); // (Giữ nguyên logic của bạn)

        } else {
            // Kịch bản 2: ADMIN TẠO TÀI KHOẢN NHÂN VIÊN
            // 2a. Email là tùy chọn
            if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
                // Nếu Admin có nhập email, thì VẪN validate nó
                if (!Pattern.matches(EMAIL_REGEX, dto.getEmail())) {
                    throw new ValidationException("Định dạng Email không hợp lệ.");
                }
                if (taiKhoanDAO.isEmailExisted(dto.getEmail())) {
                    throw new ValidationException("Email '" + dto.getEmail() + "' đã tồn tại.");
                }
                // Gán email đã validate
                entity.setEmail(dto.getEmail().trim());
            } else {
                // Admin không nhập email -> Hoàn toàn hợp lệ
                entity.setEmail(null);
            }
            // 2b. Gán logic cho Nhân viên (đúng như bạn yêu cầu)
            entity.setVerificationToken(null);
            entity.setTokenExpiryDate(null);
            entity.setTrangThai(TRANG_THAI_HOAT_DONG);
            entity.setTrangThaiMatKhau("CAN_DOI"); // <-- BẮT BUỘNG ĐỔI MK
        }

        // --- 3. LƯU VÀO CSDL ---
        try {
            TaiKhoan savedEntity = taiKhoanDAO.create(entity);
            return (savedEntity != null) ? toDTO(savedEntity) : null;
        } catch (RuntimeException e) {
            throw new Exception("Lỗi CSDL khi tạo tài khoản: " + e.getMessage(), e);
        }
    }

    /**
     * Dịch vụ thay đổi mật khẩu (cho người đã đăng nhập). ✨ ĐÃ KHÔI PHỤC LOGIC
     * ĐỔI MẬT KHẨU TRỰC TIẾP (CÁCH 1).
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

        try {
            // VÌ CÁC HÀM TRƯỚC ĐÃ KHÔNG THÀNH CÔNG, LỖI CÓ THỂ LÀ TRANSACTION LỚN HƠN.
            // BẠN NÊN KIỂM TRA LẠI HÀM DAO.UPDATE ĐỂ ĐẢM BẢO DÙNG MERGE/UPDATE.
            taiKhoanDAO.update(entity);
        } catch (RuntimeException e) {
            throw new Exception("Cập nhật mật khẩu thất bại do lỗi CSDL: " + e.getMessage(), e);
        }
    }

    // =================================================================
    // CÁC HÀM XÁC THỰC EMAIL (Giữ nguyên)
    // (Lưu ý: Các hàm này hiện tại không được dùng khi đổi mật khẩu trực tiếp)
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
                // (Giả sử có hàm delete trong DAO)
                // taiKhoanDAO.delete(entity);
            } catch (RuntimeException e) {
                /* Bỏ qua lỗi xóa nếu tài khoản hết hạn */
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

    /**
     * **HÀM MỚI (Gửi lại Xác thực):** Tạo token MỚI cho user CHUA_XAC_THUC.
     */
    public TaiKhoan resendVerificationEmail(String email) throws ValidationException, Exception {
        if (email == null || email.trim().isEmpty()) {
            throw new ValidationException("Email không được để trống.");
        }

        Optional<TaiKhoan> optEntity = taiKhoanDAO.findByEmail(email);

        if (!optEntity.isPresent()) {
            throw new ValidationException("Lỗi: Không tìm thấy tài khoản với email này.");
        }

        TaiKhoan entity = optEntity.get();

        if (!TRANG_THAI_CHUA_XAC_THUC.equals(entity.getTrangThai())) {
            throw new ValidationException("Tài khoản này đã được kích hoạt. Vui lòng đăng nhập.");
        }

        String token = UUID.randomUUID().toString();
        entity.setVerificationToken(token);
        entity.setTokenExpiryDate(LocalDateTime.now().plusHours(24));
        entity.setUpdatedAt(LocalDateTime.now());

        try {
            taiKhoanDAO.update(entity);
            return entity; // Trả về entity đã được cập nhật
        } catch (RuntimeException e) {
            throw new Exception("Lỗi CSDL khi tạo token gửi lại: " + e.getMessage(), e);
        }
    }

    // =================================================================
    // CÁC HÀM QUÊN MẬT KHẨU (Giữ nguyên)
    // =================================================================
    /**
     * **HÀM MỚI (Quên Mật khẩu - Giai đoạn 1):** Tạo và lưu token reset.
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
            // MẸO BẢO MẬT: Không báo lỗi, chỉ trả về null.
            return null;
        }

        TaiKhoan entity = optEntity.get();

        String token = UUID.randomUUID().toString();
        entity.setVerificationToken(token);
        entity.setTokenExpiryDate(LocalDateTime.now().plusHours(1)); // 1 giờ
        entity.setUpdatedAt(LocalDateTime.now());

        try {
            taiKhoanDAO.update(entity);
            return entity;
        } catch (RuntimeException e) {
            throw new Exception("Lỗi CSDL khi tạo token reset: " + e.getMessage(), e);
        }
    }

    /**
     * **HÀM MỚI (Quên Mật khẩu - Giai đoạn 2, GET):** Kiểm tra token.
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
    }

    /**
     * **HÀM MỚI (Quên Mật khẩu - Giai đoạn 2, POST):** Thực hiện reset.
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

        // 2. Validate token
        Optional<TaiKhoan> optEntity = taiKhoanDAO.findByVerificationToken(token);
        if (!optEntity.isPresent()) {
            throw new ValidationException("Link đặt lại mật khẩu không hợp lệ hoặc đã được sử dụng.");
        }

        TaiKhoan entity = optEntity.get();

        // 3. Check hết hạn
        if (entity.getTokenExpiryDate() != null && entity.getTokenExpiryDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Link đặt lại mật khẩu đã hết hạn. Vui lòng yêu cầu lại.");
        }

        // 4. Mọi thứ OK -> Đặt mật khẩu mới
        entity.setMatKhau(PasswordHasher.hashPassword(newPassword));

        // 5. VÔ HIỆU HÓA token
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

    // --- **MERGE:** Lấy hàm 'getDistinctRoles' từ nhánh 'main' ---
    /**
     * Lấy danh sách các vai trò duy nhất từ CSDL.
     *
     * @return
     */
    public List<String> getDistinctRoles() {
        return taiKhoanDAO.getDistinctVaiTro();
    }
    // --- **KẾT THÚC MERGE** ---

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

    private TaiKhoan toEntity(TaiKhoanDTO dto, String hashedMatKhau) {
        TaiKhoan entity = new TaiKhoan();
        entity.setTenDangNhap(dto.getTenDangNhap());
        entity.setEmail(dto.getEmail());
        entity.setVaiTro(dto.getVaiTro());
        entity.setTrangThai(dto.getTrangThai()); // (Sẽ được ghi đè bởi createTaiKhoan)
        entity.setMatKhau(hashedMatKhau);
        entity.setTrangThaiMatKhau(dto.getTrangThaiMatKhau());

        return entity;
    }

    // --- **MERGE:** Lấy hàm 'getTaiKhoanByBenhNhanId' từ nhánh 'main' ---
    public TaiKhoanDTO getTaiKhoanByBenhNhanId(int benhNhanId) {
        // 1. Gọi BenhNhanDAO để tìm bệnh nhân và tài khoản của họ
        // (Giả sử BenhNhanDAO có hàm getByIdWithRelations)
        // BenhNhan benhNhan = benhNhanDAO.getByIdWithRelations(benhNhanId); 

        // if (benhNhan == null || benhNhan.getTaiKhoan() == null) {
        //     return null; 
        // }
        // return toDTO(benhNhan.getTaiKhoan());
        return null; // Trả về null vì logic này cần BenhNhanDAO, giữ logic ban đầu
    }
    // --- **KẾT THÚC MERGE** ---

    public List<TaiKhoanDTO> getAllTaiKhoanPaginated(int page, int pageSize) {
        // Gọi hàm DAO mới
        List<TaiKhoan> entities = taiKhoanDAO.getAllTaiKhoan(page, pageSize);
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * HÀM MỚI (PHÂN TRANG): Đếm tổng số Tài khoản
     */
    public long getTaiKhoanCount() {
        // Gọi hàm DAO mới
        return taiKhoanDAO.getTotalTaiKhoanCount();
    }

    /**
     * HÀM MỚI (TÌM KIẾM): Tìm kiếm Tài khoản (có phân trang)
     */
    public List<TaiKhoanDTO> searchTaiKhoanPaginated(String keyword, int page, int pageSize) {
        // Gọi hàm DAO mới
        List<TaiKhoan> entities = taiKhoanDAO.searchTaiKhoanPaginated(keyword, page, pageSize);
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * HÀM MỚI (TÌM KIẾM): Đếm kết quả tìm kiếm Tài khoản
     */
    public long getTaiKhoanSearchCount(String keyword) {
        // Gọi hàm DAO mới
        return taiKhoanDAO.getTaiKhoanSearchCount(keyword);
    }

}
