package service;

import exception.ValidationException;
import model.Entity.Khoa;
import model.dao.KhoaDAO;
import model.dao.NhanVienDAO; // <-- THÊM IMPORT MỚI
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

    /**
     * (Hàm createKhoa giữ nguyên)
     */
    public KhoaDTO createKhoa(KhoaDTO dto) throws ValidationException, Exception {
        validateKhoaData(dto, null);
        Khoa entity = toEntity(dto, new Khoa());
        entity.setTrangThai("HOAT_DONG");
        Khoa savedEntity = khoaDAO.create(entity);
        if (savedEntity == null) {
            throw new Exception("Tạo khoa thất bại (DAO trả về null).");
        }
        return toDTO(savedEntity);
    }

    /**
     * (Hàm updateKhoa giữ nguyên)
     */
    public KhoaDTO updateKhoa(int khoaId, KhoaDTO dto) throws ValidationException, Exception {
        Khoa existingEntity = khoaDAO.getById(khoaId);
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
     * === BẮT ĐẦU SỬA (THÊM ĐIỀU KIỆN KÍCH HOẠT) === Dịch vụ "xóa" (vô hiệu
     * hóa) một Khoa, có kiểm tra nghiệp vụ.
     */
    public void softDeleteKhoa(int khoaId) throws ValidationException, Exception {
        // 1. Kiểm tra khoa tồn tại
        Khoa existingEntity = khoaDAO.getById(khoaId);
        if (existingEntity == null) {
            throw new ValidationException("Không tìm thấy khoa với ID: " + khoaId + " để vô hiệu hóa.");
        }

        // 2. "ĐIỀU KIỆN KÍCH HOẠT" (Bảo vệ nghiệp vụ)
        // Gọi hàm DAO mới mà chúng ta vừa thêm vào NhanVienDAO
        long activeStaffCount = nhanVienDAO.countActiveNhanVienByKhoaId(khoaId);
        if (activeStaffCount > 0) {
            throw new ValidationException("Không thể vô hiệu hóa khoa. Vẫn còn " + activeStaffCount + " nhân viên đang hoạt động trong khoa này.");
        }
        // (Bạn cũng có thể thêm logic kiểm tra Bệnh nhân đang nội trú ở đây)

        // 3. Thực hiện Xóa Mềm
        try {
            boolean success = khoaDAO.delete(khoaId); // Hàm này đã là Xóa Mềm
            if (!success) {
                throw new Exception("Vô hiệu hóa khoa thất bại (DAO trả về false).");
            }
        } catch (Exception e) {
            throw new Exception("Lỗi hệ thống khi vô hiệu hóa khoa: " + e.getMessage());
        }
    }
    // === KẾT THÚC SỬA ===

    /**
     * (Hàm getKhoaById giữ nguyên)
     */
    public KhoaDTO getKhoaById(int id) throws ValidationException {
        Khoa entity = khoaDAO.getById(id);
        if (entity == null) {
            throw new ValidationException("Không tìm thấy khoa đang hoạt động với ID: " + id);
        }
        return toDTO(entity);
    }

    /**
     * (Hàm getAllKhoa giữ nguyên - Dùng cho Dropdowns)
     */
    public List<KhoaDTO> getAllKhoa() {
        List<Khoa> entities = khoaDAO.getAll(); // DAO đã lọc HOAT_DONG
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // (Các hàm Phân trang & Tìm kiếm giữ nguyên)
    public List<KhoaDTO> getAllKhoaPaginated(int page, int pageSize) {
        List<Khoa> entities = khoaDAO.getAllKhoa(page, pageSize);
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public long getKhoaCount() {
        return khoaDAO.getTotalKhoaCount();
    }

    public List<KhoaDTO> searchKhoaPaginated(String keyword, int page, int pageSize) {
        List<Khoa> entities = khoaDAO.searchKhoaPaginated(keyword, page, pageSize);
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public long getKhoaSearchCount(String keyword) {
        return khoaDAO.getKhoaSearchCount(keyword);
    }

    // (Các hàm Mapper và Validate giữ nguyên)
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
        entity.setTenKhoa(dto.getTenKhoa());
        entity.setMoTa(dto.getMoTa());
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
        Khoa khoaByTen = khoaDAO.findByTenKhoa(tenKhoa);
        if (existingEntity == null) {
            if (khoaByTen != null) {
                throw new ValidationException("Tên khoa '" + tenKhoa + "' đã tồn tại.");
            }
        } else {
            if (khoaByTen != null && khoaByTen.getId() != existingEntity.getId()) {
                throw new ValidationException("Tên khoa '" + tenKhoa + "' đã tồn tại.");
            }
        }
    }
}
