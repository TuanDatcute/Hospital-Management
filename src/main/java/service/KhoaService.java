package service;

import exception.ValidationException;
import model.Entity.Khoa;
import model.dao.KhoaDAO;
import model.dao.NhanVienDAO; // <-- THÊM IMPORT
import model.dto.KhoaDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Lớp Service chứa logic nghiệp vụ cho Khoa. (ĐÃ NÂNG CẤP: Thêm Điều kiện kích
 * hoạt Xóa Mềm)
 */
public class KhoaService {

    private final KhoaDAO khoaDAO = new KhoaDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO(); // <-- THÊM MỚI

    private static final String NAME_REGEX = "^[\\p{L} .'-]{2,50}$";

    public KhoaDTO createKhoa(KhoaDTO dto) throws ValidationException, Exception {
        validateKhoaData(dto, null);
        Khoa entity = toEntity(dto, new Khoa());
        entity.setTrangThai("HOAT_DONG"); // <-- Đặt trạng thái
        Khoa savedEntity = khoaDAO.create(entity);
        if (savedEntity == null) {
            throw new Exception("Tạo khoa thất bại (DAO trả về null).");
        }
        return toDTO(savedEntity);
    }

    public KhoaDTO updateKhoa(int khoaId, KhoaDTO dto) throws ValidationException, Exception {
        Khoa existingEntity = khoaDAO.getById(khoaId); // DAO đã lọc
        if (existingEntity == null) {
            throw new ValidationException("Không tìm thấy khoa đang hoạt động với ID: " + khoaId);
        }
        validateKhoaData(dto, existingEntity);
        existingEntity = toEntity(dto, existingEntity);
        if (!khoaDAO.update(existingEntity)) {
            throw new Exception("Cập nhật khoa thất bại (DAO trả về false).");
        }
        return toDTO(existingEntity);
    }

    /**
     * === SỬA (THÊM ĐIỀU KIỆN KÍCH HOẠT) === (Đổi tên thành softDeleteKhoa)
     */
    public void softDeleteKhoa(int khoaId) throws ValidationException, Exception {
        // 1. Kiểm tra khoa tồn tại (DAO.getById đã lọc HOAT_DONG)
        Khoa existingEntity = khoaDAO.getById(khoaId);
        if (existingEntity == null) {
            throw new ValidationException("Không tìm thấy khoa với ID: " + khoaId + " để vô hiệu hóa.");
        }

        // 2. "ĐIỀU KIỆN KÍCH HOẠT" (Bảo vệ nghiệp vụ)
        long activeStaffCount = nhanVienDAO.countActiveNhanVienByKhoaId(khoaId);
        if (activeStaffCount > 0) {
            throw new ValidationException("Không thể vô hiệu hóa khoa. Vẫn còn " + activeStaffCount + " nhân viên đang hoạt động trong khoa này.");
        }

        // 3. Thực hiện Xóa Mềm
        try {
            boolean success = khoaDAO.delete(khoaId); // Gọi hàm delete(id) của DAO (đã là Xóa Mềm)
            if (!success) {
                throw new Exception("Vô hiệu hóa khoa thất bại (DAO trả về false).");
            }
        } catch (Exception e) {
            throw new Exception("Lỗi hệ thống khi vô hiệu hóa khoa: " + e.getMessage());
        }
    }

    public KhoaDTO getKhoaById(int id) throws ValidationException {
        Khoa entity = khoaDAO.getById(id);
        if (entity == null) {
            throw new ValidationException("Không tìm thấy khoa đang hoạt động với ID: " + id);
        }
        return toDTO(entity);
    }

    public List<KhoaDTO> getAllKhoa() {
        List<Khoa> entities = khoaDAO.getAll(); // DAO đã lọc HOAT_DONG
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // === CÁC HÀM PHÂN TRANG & TÌM KIẾM MỚI ===
    public List<KhoaDTO> getAllKhoaPaginated(int page, int pageSize) {
        List<Khoa> entities = khoaDAO.getAllKhoa(page, pageSize); // DAO đã lọc
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public long getKhoaCount() {
        return khoaDAO.getTotalKhoaCount();
    }

    public List<KhoaDTO> searchKhoaPaginated(String keyword, int page, int pageSize) {
        List<Khoa> entities = khoaDAO.searchKhoaPaginated(keyword, page, pageSize); // DAO đã lọc
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public long getKhoaSearchCount(String keyword) {
        return khoaDAO.getKhoaSearchCount(keyword);
    }

    // --- CÁC HÀM MAPPER VÀ VALIDATE (Đã Sửa) ---
    private KhoaDTO toDTO(Khoa entity) {
        if (entity == null) {
            return null;
        }
        KhoaDTO dto = new KhoaDTO();
        dto.setId(entity.getId());
        dto.setTenKhoa(entity.getTenKhoa());
        dto.setMoTa(entity.getMoTa());
        return dto;
    }

    private Khoa toEntity(KhoaDTO dto, Khoa entity) {
        entity.setTenKhoa(dto.getTenKhoa()); // Đã trim từ validate
        entity.setMoTa(dto.getMoTa()); // Đã trim từ validate
        return entity;
    }

    private void validateKhoaData(KhoaDTO dto, Khoa existingEntity) throws ValidationException {
        if (dto.getTenKhoa() == null || dto.getTenKhoa().trim().isEmpty()) {
            throw new ValidationException("Tên khoa không được để trống.");
        }
        String tenKhoa = dto.getTenKhoa().trim();
        if (!Pattern.matches(NAME_REGEX, tenKhoa)) {
            throw new ValidationException("Tên khoa không hợp lệ (chỉ chứa chữ cái, dấu cách, và dài 2-50 ký tự).");
        }
        dto.setTenKhoa(tenKhoa);
        if (dto.getMoTa() != null) {
            dto.setMoTa(dto.getMoTa().trim());
        }
        Khoa khoaByTen = khoaDAO.findByTenKhoa(tenKhoa); // <-- Gọi hàm DAO đã thêm
        if (existingEntity == null) {
            // Kịch bản TẠO MỚI
            if (khoaByTen != null) {
                throw new ValidationException("Tên khoa '" + tenKhoa + "' đã tồn tại.");
            }
        } else {
            // Kịch bản CẬP NHẬT
            if (khoaByTen != null && khoaByTen.getId() != existingEntity.getId()) {
                throw new ValidationException("Tên khoa '" + tenKhoa + "' đã tồn tại.");
            }
        }
    }
}
