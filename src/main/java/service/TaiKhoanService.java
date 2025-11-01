package service;

import exception.ValidationException;
import model.Entity.TaiKhoan; // Đảm bảo đúng package 'model.Entity' (E hoa)
import model.dao.TaiKhoanDAO;
import model.dto.TaiKhoanDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import util.PasswordHasher;

/**
 * Lớp Service chứa logic nghiệp vụ cho TaiKhoan.
 */
public class TaiKhoanService {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    /**
     * Hàm login đã đúng, ném ValidationException
     */
    public TaiKhoanDTO login(String tenDangNhap, String matKhau) throws ValidationException {
        TaiKhoan entity = taiKhoanDAO.findByTenDangNhap(tenDangNhap);
        if (entity == null) {
            throw new ValidationException("Tài khoản hoặc mật khẩu không đúng");
        }
        if (!PasswordHasher.checkPassword(matKhau, entity.getMatKhau())) {
            throw new ValidationException("Tài khoản hoặc mật khẩu không đúng");
        }
        if ("BI_KHOA".equals(entity.getTrangThai())) {
            throw new ValidationException("Tài khoản này đã bị khóa.");
        }
        return toDTO(entity);
    }

    /**
     * Dịch vụ tạo tài khoản mới.
     * Đã cập nhật: Bỏ bắt buộc Email.
     */
    public TaiKhoanDTO createTaiKhoan(TaiKhoanDTO dto, String matKhau) throws ValidationException, Exception {
        
        // --- BƯỚC 1: VALIDATION (Đã xóa validation Email) ---
        if (dto.getTenDangNhap() == null || dto.getTenDangNhap().trim().isEmpty()) {
            throw new ValidationException("Tên đăng nhập không được để trống.");
        }
        
        // **ĐÃ XÓA** Yêu cầu Email bắt buộc
        // if (dto.getEmail() == null || dto.getEmail().trim().isEmpty()) {
        //     throw new ValidationException("Email không được để trống.");
        // }
        
        if (matKhau == null || matKhau.length() < 6) {
            throw new ValidationException("Mật khẩu phải có ít nhất 6 ký tự.");
        }
        if (taiKhoanDAO.isTenDangNhapExisted(dto.getTenDangNhap())) {
            throw new ValidationException("Tên đăng nhập '" + dto.getTenDangNhap() + "' đã tồn tại.");
        }

        // **SỬA LẠI:** Chỉ kiểm tra Email tồn tại NẾU Admin có nhập Email
        if (dto.getEmail() != null && !dto.getEmail().trim().isEmpty()) {
            if (taiKhoanDAO.isEmailExisted(dto.getEmail())) {
                throw new ValidationException("Email '" + dto.getEmail() + "' đã tồn tại.");
            }
        }
        // --- KẾT THÚC SỬA ---

        // --- BƯỚC 2: CHUYỂN ĐỔI VÀ THÊM LOGIC MỚI ---
        String hashedMatKhau = PasswordHasher.hashPassword(matKhau);
        TaiKhoan entity = toEntity(dto, hashedMatKhau);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        if (dto.getTrangThai() == null || dto.getTrangThai().isEmpty()) {
            entity.setTrangThai("HOAT_DONG");
        }

        // Logic ép đổi mật khẩu (Giữ nguyên)
        String vaiTro = dto.getVaiTro();
        if ("BENH_NHAN".equals(vaiTro)) {
            entity.setTrangThaiMatKhau("DA_DOI");
        } else {
            entity.setTrangThaiMatKhau("CAN_DOI");
        }

        TaiKhoan savedEntity = taiKhoanDAO.create(entity);
        return (savedEntity != null) ? toDTO(savedEntity) : null;
    }

    /**
     * Dịch vụ thay đổi mật khẩu.
     */
    public void changePassword(int id, String oldPassword, String newPassword) throws ValidationException, Exception {
        TaiKhoan entity = taiKhoanDAO.getById(id);
        if (entity == null) {
            throw new Exception("Không tìm thấy tài khoản với ID: " + id);
        }
        if (!"HOAT_DONG".equals(entity.getTrangThai())) {
            throw new ValidationException("Không thể đổi mật khẩu cho tài khoản đang bị khóa.");
        }
        if (!PasswordHasher.checkPassword(oldPassword, entity.getMatKhau())) {
            throw new ValidationException("Mật khẩu cũ không chính xác.");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new ValidationException("Mật khẩu mới phải có ít nhất 6 ký tự.");
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