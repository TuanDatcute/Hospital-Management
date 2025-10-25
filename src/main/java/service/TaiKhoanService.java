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
 *
 * @author ADMIN
 */
public class TaiKhoanService {

    // Khởi tạo DAO để Service có thể sử dụng
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    /**
     * Dịch vụ kiểm tra đăng nhập.
     *
     * @param tenDangNhap Tên đăng nhập
     * @param matKhau Mật khẩu (chưa mã hóa)
     * @return TaiKhoanDTO nếu thành công
     * @throws Exception nếu sai tên, sai mật khẩu, hoặc tài khoản bị khóa
     */
    public TaiKhoanDTO login(String tenDangNhap, String matKhau) throws Exception {
        // Bước 1: Lấy entity từ CSDL
        TaiKhoan entity = taiKhoanDAO.findByTenDangNhap(tenDangNhap);

        // Bước 2: Validation
        if (entity == null) {
            throw new Exception("Tên đăng nhập không tồn tại.");
        }

        // Bước 3: Xác thực mật khẩu (RẤT QUAN TRỌNG)
        // ⚠️ TODO: Bạn BẮT BUỘC phải thay thế bằng hàm so sánh mật khẩu đã mã hóa
        // boolean passwordMatches = PasswordHasher.check(matKhau, entity.getMatKhau());
        // Tạm thời so sánh thẳng (KHÔNG AN TOÀN - CHỈ ĐỂ TEST)
        boolean passwordMatches = entity.getMatKhau().equals(matKhau);

        if (!passwordMatches) {
            throw new Exception("Mật khẩu không chính xác.");
        }

        // Bước 4: Kiểm tra trạng thái
        if ("BI_KHOA".equals(entity.getTrangThai())) {
            throw new Exception("Tài khoản này đã bị khóa.");
        }

        // Bước 5: Chuyển đổi và trả về DTO (không chứa mật khẩu)
        return toDTO(entity);
    }

    /**
     * Dịch vụ tạo tài khoản mới.
     *
     * @param dto Thông tin tài khoản (trừ mật khẩu)
     * @param matKhau Mật khẩu (dạng thô, chưa mã hóa)
     * @return TaiKhoanDTO của tài khoản mới
     * @throws Exception nếu validation thất bại (trùng tên, trùng email...)
     */
    public TaiKhoanDTO createTaiKhoan(TaiKhoanDTO dto, String matKhau) throws Exception {

        // --- BƯỚC 1: LOGIC NGHIỆP VỤ (VALIDATION) ---
        if (dto.getTenDangNhap() == null || dto.getTenDangNhap().trim().isEmpty()) {
            throw new Exception("Tên đăng nhập không được để trống.");
        }
        if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
            throw new Exception("Email không được để trống.");
        }
        if (matKhau == null || matKhau.length() < 6) {
            throw new Exception("Mật khẩu phải có ít nhất 6 ký tự.");
        }

        // Dùng DAO để kiểm tra
        if (taiKhoanDAO.isTenDangNhapExisted(dto.getTenDangNhap())) {
            throw new Exception("Tên đăng nhập '" + dto.getTenDangNhap() + "' đã tồn tại.");
        }
        if (taiKhoanDAO.isEmailExisted(dto.getEmail())) {
            throw new Exception("Email '" + dto.getEmail() + "' đã tồn tại.");
        }

        // --- BƯỚC 2: MÃ HÓA MẬT KHẨU ---
        // ⚠️ TODO: Bạn BẮT BUỘC phải mã hóa mật khẩu trước khi lưu
        // String hashedMatKhau = PasswordHasher.hash(matKhau);
        // Tạm thời lưu mật khẩu thô (KHÔNG AN TOÀN - CHỈ ĐỂ TEST)
        String hashedMatKhau = matKhau;

        // --- BƯỚC 3: CHUYỂN ĐỔI (MAP) ---
        TaiKhoan entity = toEntity(dto, hashedMatKhau);

        // Set giá trị mặc định khi tạo mới
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        if (dto.getTrangThai() == null || dto.getTrangThai().isEmpty()) {
            entity.setTrangThai("HOAT_DONG"); // Giá trị mặc định
        }

        // --- BƯỚC 4: GỌI DAO ĐỂ LƯU ---
        TaiKhoan savedEntity = taiKhoanDAO.create(entity);

        // --- BƯỚC 5: TRẢ VỀ DTO ---
        if (savedEntity != null) {
            return toDTO(savedEntity); // Trả về DTO (đã có ID)
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

        // Dùng Stream API để chuyển đổi cả danh sách
        return entities.stream()
                .map(this::toDTO) // Gọi hàm toDTO cho từng item
                .collect(Collectors.toList());
    }

    // --- CÁC HÀM MAPPER (Chuyển đổi DTO <-> Entity) ---
    /**
     * Chuyển TaiKhoan (Entity) sang TaiKhoanDTO (Ẩn mật khẩu).
     */
    private TaiKhoanDTO toDTO(TaiKhoan entity) {
        TaiKhoanDTO dto = new TaiKhoanDTO();
        dto.setId(entity.getId());
        dto.setTenDangNhap(entity.getTenDangNhap());
        dto.setEmail(entity.getEmail());
        dto.setVaiTro(entity.getVaiTro());
        dto.setTrangThai(entity.getTrangThai());
        dto.setCreatedAt(entity.getCreatedAt());

        // Tuyệt đối KHÔNG set matKhau cho DTO
        return dto;
    }

    /**
     * Chuyển TaiKhoanDTO sang TaiKhoan (Entity) để lưu vào CSDL. Cần mật khẩu
     * đã được xử lý (mã hóa).
     */
    private TaiKhoan toEntity(TaiKhoanDTO dto, String hashedMatKhau) {
        TaiKhoan entity = new TaiKhoan();

        // Không set ID vì đây là tạo mới (ID sẽ tự tăng)
        entity.setTenDangNhap(dto.getTenDangNhap());
        entity.setEmail(dto.getEmail());
        entity.setVaiTro(dto.getVaiTro());
        entity.setTrangThai(dto.getTrangThai());

        // Set mật khẩu đã mã hóa
        entity.setMatKhau(hashedMatKhau);

        // createdAt và updatedAt sẽ được set ở logic nghiệp vụ (trong hàm create)
        return entity;
    }
}
