package service;

import model.Entity.TaiKhoan;
import model.dao.TaiKhoanDAO;
import model.dto.TaiKhoanDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

// TODO: Import thư viện mã hóa mật khẩu của bạn ở đây
// import util.PasswordHasher;

/**
 * Lớp Service chứa logic nghiệp vụ cho TaiKhoan.
 * @author ADMIN
 */
public class TaiKhoanService {

    // Khởi tạo DAO để Service có thể sử dụng
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    /**
     * Dịch vụ kiểm tra đăng nhập.
     * @param tenDangNhap Tên đăng nhập
     * @param matKhau Mật khẩu (chưa mã hóa)
     * @return TaiKhoanDTO nếu thành công
     * @throws Exception nếu sai tên, sai mật khẩu, hoặc tài khoản bị khóa
     */
    public TaiKhoanDTO login(String tenDangNhap, String matKhau) throws Exception {
        TaiKhoan entity = taiKhoanDAO.findByTenDangNhap(tenDangNhap);

        if (entity == null) {
            throw new Exception("Tên đăng nhập không tồn tại.");
        }

        // ⚠️ TODO: BẮT BUỘC thay thế bằng hàm so sánh mật khẩu đã mã hóa
        // boolean passwordMatches = PasswordHasher.check(matKhau, entity.getMatKhau());
        boolean passwordMatches = entity.getMatKhau().equals(matKhau); // Tạm thời (KHÔNG AN TOÀN)

        if (!passwordMatches) {
            throw new Exception("Mật khẩu không chính xác.");
        }

        if ("BI_KHOA".equals(entity.getTrangThai())) {
            throw new Exception("Tài khoản này đã bị khóa.");
        }

        return toDTO(entity);
    }

    /**
     * Dịch vụ tạo tài khoản mới.
     * @param dto Thông tin tài khoản (trừ mật khẩu)
     * @param matKhau Mật khẩu (dạng thô, chưa mã hóa)
     * @return TaiKhoanDTO của tài khoản mới
     * @throws Exception nếu validation thất bại (trùng tên, trùng email...)
     */
    public TaiKhoanDTO createTaiKhoan(TaiKhoanDTO dto, String matKhau) throws Exception {

        // --- VALIDATION ---
        if (dto.getTenDangNhap() == null || dto.getTenDangNhap().trim().isEmpty()) {
            throw new Exception("Tên đăng nhập không được để trống.");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new Exception("Email không được để trống.");
        }
        if (matKhau == null || matKhau.length() < 6) {
            throw new Exception("Mật khẩu phải có ít nhất 6 ký tự.");
        }
        if (taiKhoanDAO.isTenDangNhapExisted(dto.getTenDangNhap())) {
            throw new Exception("Tên đăng nhập '" + dto.getTenDangNhap() + "' đã tồn tại.");
        }
        if (taiKhoanDAO.isEmailExisted(dto.getEmail())) {
            throw new Exception("Email '" + dto.getEmail() + "' đã tồn tại.");
        }

        // --- MÃ HÓA MẬT KHẨU ---
        // ⚠️ TODO: BẮT BUỘC mã hóa mật khẩu
        // String hashedMatKhau = PasswordHasher.hash(matKhau);
        String hashedMatKhau = matKhau; // Tạm thời (KHÔNG AN TOÀN)

        // --- MAP DTO -> ENTITY ---
        TaiKhoan entity = toEntity(dto, hashedMatKhau);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        if (dto.getTrangThai() == null || dto.getTrangThai().isEmpty()) {
            entity.setTrangThai("HOAT_DONG"); // Mặc định
        }

        // --- GỌI DAO ---
        TaiKhoan savedEntity = taiKhoanDAO.create(entity);

        // --- TRẢ VỀ DTO ---
        if (savedEntity != null) {
            return toDTO(savedEntity);
        }
        return null;
    }

    /**
     * Lấy tài khoản bằng ID.
     */
    public TaiKhoanDTO getTaiKhoanById(int id) throws Exception {
        TaiKhoan entity = taiKhoanDAO.getById(id);
        if (entity == null) {
            throw new Exception("Không tìm thấy tài khoản với ID: " + id);
        }
        return toDTO(entity);
    }

    /**
     * Lấy tất cả tài khoản.
     */
    public List<TaiKhoanDTO> getAllTaiKhoan() {
        List<TaiKhoan> entities = taiKhoanDAO.getAll();
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // --- CÁC HÀM CẬP NHẬT TRẠNG THÁI ---

    /**
     * Dịch vụ cập nhật trạng thái của một Tài khoản (Khóa hoặc Mở khóa).
     * @param id ID của tài khoản cần cập nhật.
     * @param newTrangThai Trạng thái mới ("HOAT_DONG" hoặc "BI_KHOA").
     * @return TaiKhoanDTO đã được cập nhật.
     * @throws Exception nếu ID không tồn tại, trạng thái không hợp lệ, hoặc lỗi cập nhật.
     */
    public TaiKhoanDTO updateTrangThaiTaiKhoan(int id, String newTrangThai) throws Exception {
        if (!"HOAT_DONG".equals(newTrangThai) && !"BI_KHOA".equals(newTrangThai)) {
            throw new Exception("Trạng thái mới không hợp lệ. Chỉ chấp nhận 'HOAT_DONG' hoặc 'BI_KHOA'.");
        }

        TaiKhoan entity = taiKhoanDAO.getById(id);
        if (entity == null) {
            throw new Exception("Không tìm thấy tài khoản với ID: " + id);
        }

        if (!entity.getTrangThai().equals(newTrangThai)) {
            entity.setTrangThai(newTrangThai);
            entity.setUpdatedAt(LocalDateTime.now());

            boolean success = taiKhoanDAO.update(entity);
            if (!success) {
                throw new Exception("Cập nhật trạng thái tài khoản thất bại.");
            }
        }
        return toDTO(entity);
    }

    /**
     * Hàm tiện ích: Khóa tài khoản. Chính là Soft Delete.
     */
    public TaiKhoanDTO khoaTaiKhoan(int id) throws Exception {
        return updateTrangThaiTaiKhoan(id, "BI_KHOA");
    }

    /**
     * Hàm tiện ích: Mở khóa tài khoản.
     */
    public TaiKhoanDTO moKhoaTaiKhoan(int id) throws Exception {
        return updateTrangThaiTaiKhoan(id, "HOAT_DONG");
    }

    // --- CÁC HÀM MAPPER (Chuyển đổi DTO <-> Entity) ---

    /**
     * Chuyển TaiKhoan (Entity) sang TaiKhoanDTO (Ẩn mật khẩu).
     */
    private TaiKhoanDTO toDTO(TaiKhoan entity) {
        if (entity == null) return null; // Thêm kiểm tra null
        TaiKhoanDTO dto = new TaiKhoanDTO();
        dto.setId(entity.getId());
        dto.setTenDangNhap(entity.getTenDangNhap());
        dto.setEmail(entity.getEmail());
        dto.setVaiTro(entity.getVaiTro());
        dto.setTrangThai(entity.getTrangThai());
        dto.setCreatedAt(entity.getCreatedAt());
        return dto;
    }

    /**
     * Chuyển TaiKhoanDTO sang TaiKhoan (Entity) để lưu vào CSDL.
     * Cần mật khẩu đã được xử lý (mã hóa).
     */
    private TaiKhoan toEntity(TaiKhoanDTO dto, String hashedMatKhau) {
        TaiKhoan entity = new TaiKhoan();
        entity.setTenDangNhap(dto.getTenDangNhap());
        entity.setEmail(dto.getEmail());
        entity.setVaiTro(dto.getVaiTro());
        entity.setTrangThai(dto.getTrangThai());
        entity.setMatKhau(hashedMatKhau);
        // createdAt và updatedAt sẽ được set ở logic nghiệp vụ
        return entity;
    }
    
    /**
     * Lấy danh sách các tài khoản đang hoạt động và chưa được gán.
     * @param role (Tùy chọn) Lọc thêm theo vai trò (VD: "BENH_NHAN"). Bỏ trống để lấy tất cả.
     * @return Danh sách TaiKhoanDTO phù hợp.
     */
    public List<TaiKhoanDTO> getActiveAndUnassignedAccounts(String role) {
        List<TaiKhoan> entities = taiKhoanDAO.findActiveAndUnassignedAccounts(role);
        return entities.stream()
                       .map(this::toDTO) // Dùng lại hàm toDTO đã có
                       .collect(Collectors.toList());
    }
} 