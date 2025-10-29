package service;

import exception.ValidationException;
import model.Entity.TaiKhoan; // Đảm bảo đúng package 'model.Entity'
import model.dao.TaiKhoanDAO;
import model.dto.TaiKhoanDTO;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors; // Import của Java 8
import util.PasswordHasher;

public class TaiKhoanService {

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    public TaiKhoanDTO login(String tenDangNhap, String matKhau) throws ValidationException {
        TaiKhoan entity = taiKhoanDAO.findByTenDangNhap(tenDangNhap);
        if (entity == null) {
            throw new ValidationException("Tên đăng nhập không tồn tại.");
        }
        if (!PasswordHasher.checkPassword(matKhau, entity.getMatKhau())) {
            throw new ValidationException("Mật khẩu không chính xác.");
        }
        if ("BI_KHOA".equals(entity.getTrangThai())) {
            throw new ValidationException("Tài khoản này đã bị khóa.");
        }
        return toDTO(entity);
    }

    public TaiKhoanDTO createTaiKhoan(TaiKhoanDTO dto, String matKhau) throws Exception {
        // ... (Validation logic) ...
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

        String hashedMatKhau = PasswordHasher.hashPassword(matKhau);
        TaiKhoan entity = toEntity(dto, hashedMatKhau);
        entity.setCreatedAt(LocalDateTime.now());
        entity.setUpdatedAt(LocalDateTime.now());
        if (dto.getTrangThai() == null || dto.getTrangThai().isEmpty()) {
            entity.setTrangThai("HOAT_DONG");
        }
        TaiKhoan savedEntity = taiKhoanDAO.create(entity);
        return (savedEntity != null) ? toDTO(savedEntity) : null;
    }

    public void changePassword(int id, String oldPassword, String newPassword) throws Exception {
        TaiKhoan entity = taiKhoanDAO.getById(id);
        if (entity == null) {
            throw new Exception("Không tìm thấy tài khoản với ID: " + id);
        }
        if (!"HOAT_DONG".equals(entity.getTrangThai())) {
            throw new Exception("Không thể đổi mật khẩu cho tài khoản đang bị khóa.");
        }
        if (!PasswordHasher.checkPassword(oldPassword, entity.getMatKhau())) {
            throw new Exception("Mật khẩu cũ không chính xác.");
        }
        if (newPassword == null || newPassword.length() < 6) {
            throw new Exception("Mật khẩu mới phải có ít nhất 6 ký tự.");
        }
        if (PasswordHasher.checkPassword(newPassword, entity.getMatKhau())) {
            throw new Exception("Mật khẩu mới không được trùng với mật khẩu cũ.");
        }
        String hashedNewPassword = PasswordHasher.hashPassword(newPassword);
        entity.setMatKhau(hashedNewPassword);
        entity.setUpdatedAt(LocalDateTime.now());
        if (!taiKhoanDAO.update(entity)) {
            throw new Exception("Cập nhật mật khẩu thất bại.");
        }
    }

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
                .map(this::toDTO) // Sử dụng Method Reference
                .collect(Collectors.toList());
    }

    public TaiKhoanDTO updateTrangThaiTaiKhoan(int id, String newTrangThai) throws Exception {
        if (!"HOAT_DONG".equals(newTrangThai) && !"BI_KHOA".equals(newTrangThai)) {
            throw new Exception("Trạng thái mới không hợp lệ.");
        }
        TaiKhoan entity = taiKhoanDAO.getById(id);
        if (entity == null) {
            throw new Exception("Không tìm thấy tài khoản với ID: " + id);
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

    /**
     * Lấy danh sách tài khoản hoạt động và chưa được gán. (NÂNG CẤP JAVA 8)
     */
    public List<TaiKhoanDTO> getActiveAndUnassignedAccounts(String role) {
        List<TaiKhoan> entities = taiKhoanDAO.findActiveAndUnassignedAccounts(role);
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

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
        return dto;
    }

    private TaiKhoan toEntity(TaiKhoanDTO dto, String hashedMatKhau) {
        TaiKhoan entity = new TaiKhoan();
        entity.setTenDangNhap(dto.getTenDangNhap());
        entity.setEmail(dto.getEmail());
        entity.setVaiTro(dto.getVaiTro());
        entity.setTrangThai(dto.getTrangThai());
        entity.setMatKhau(hashedMatKhau);
        return entity;
    }
    
}
