package service;

import exception.ValidationException; // --- THÊM MỚI ---
import model.Entity.Khoa;
import model.dao.KhoaDAO;
import model.dto.KhoaDTO;
import java.util.List;
import java.util.regex.Pattern; // --- THÊM MỚI ---
import java.util.stream.Collectors;

/**
 * Lớp Service chứa logic nghiệp vụ cho Khoa.
 *
 * @author ADMIN
 */
public class KhoaService {

    private final KhoaDAO khoaDAO = new KhoaDAO();

    // --- THÊM MỚI: Regex cho Tên (Giống NhanVienService) ---
    /**
     * Regex cho Tên:
     * - \p{L} : Bất kỳ chữ cái Unicode nào (bao gồm tiếng Việt).
     * - Dấu cách, '.', ''', '-'.
     * - Độ dài từ 2 đến 50 ký tự. (Bạn có thể tăng lên nếu tên khoa dài hơn)
     */
    private static final String NAME_REGEX = "^[\\p{L} .'-]{2,50}$";
    // --- KẾT THÚC THÊM MỚI ---

    /**
     * Dịch vụ tạo một Khoa mới.
     *
     * @param dto DTO chứa thông tin khoa mới.
     * @return DTO của khoa đã được tạo (có ID).
     * @throws ValidationException ném ra nếu logic validation thất bại.
     * @throws Exception ném ra nếu có lỗi hệ thống.
     */
    public KhoaDTO createKhoa(KhoaDTO dto) throws ValidationException, Exception {

        // --- BƯỚC 1: LOGIC NGHIỆP VỤ (VALIDATION) ---
        if (dto.getTenKhoa() == null || dto.getTenKhoa().trim().isEmpty()) {
            // --- CẬP NHẬT ---
            throw new ValidationException("Tên khoa không được để trống.");
            // --- KẾT THÚC CẬP NHẬT ---
        }

        String tenKhoa = dto.getTenKhoa().trim();
        
        // --- THÊM MỚI: Regex Validation ---
        if (!Pattern.matches(NAME_REGEX, tenKhoa)) {
            throw new ValidationException("Tên khoa không hợp lệ (chỉ chứa chữ cái, dấu cách, và dài 2-50 ký tự).");
        }
        // --- KẾT THÚC THÊM MỚI ---

        if (khoaDAO.isTenKhoaExisted(tenKhoa)) {
            // --- CẬP NHẬT ---
            throw new ValidationException("Tên khoa '" + tenKhoa + "' đã tồn tại.");
            // --- KẾT THÚC CẬP NHẬT ---
        }

        // --- BƯỚC 2: CHUYỂN ĐỔI (MAP) ---
        Khoa entity = toEntity(dto);
        entity.setTenKhoa(tenKhoa); // Đảm bảo lưu tên đã được trim()

        // --- BƯỚC 3: GỌI DAO ĐỂ LƯU ---
        Khoa savedEntity = khoaDAO.create(entity);

        // --- BƯỚC 4: TRẢ VỀ DTO ---
        if (savedEntity != null) {
            return toDTO(savedEntity);
        }
        // Trả về null nếu DAO thất bại (hoặc ném Exception nếu create() ném lỗi)
        return null;
    }

    /**
     * Dịch vụ cập nhật thông tin Khoa.
     */
    public KhoaDTO updateKhoa(int khoaId, KhoaDTO dto) throws ValidationException, Exception {

        // --- BƯỚC 1: LẤY ENTITY GỐC ---
        Khoa existingEntity = khoaDAO.getById(khoaId);
        if (existingEntity == null) {
            // --- CẬP NHẬT ---
            throw new ValidationException("Không tìm thấy khoa với ID: " + khoaId);
            // --- KẾT THÚC CẬP NHẬT ---
        }

        // --- BƯỚC 2: VALIDATION ---
        if (dto.getTenKhoa() == null || dto.getTenKhoa().trim().isEmpty()) {
            // --- CẬP NHẬT ---
            throw new ValidationException("Tên khoa không được để trống.");
            // --- KẾT THÚC CẬP NHẬT ---
        }

        String newTenKhoa = dto.getTenKhoa().trim();

        // --- THÊM MỚI: Regex Validation ---
        if (!Pattern.matches(NAME_REGEX, newTenKhoa)) {
            throw new ValidationException("Tên khoa không hợp lệ (chỉ chứa chữ cái, dấu cách, và dài 2-50 ký tự).");
        }
        // --- KẾT THÚC THÊM MỚI ---

        // Chỉ kiểm tra trùng tên NẾU tên mới khác tên cũ (Bỏ qua trường hợp hoa/thường)
        if (!newTenKhoa.equalsIgnoreCase(existingEntity.getTenKhoa())) {
            if (khoaDAO.isTenKhoaExisted(newTenKhoa)) {
                // --- CẬP NHẬT ---
                throw new ValidationException("Tên khoa '" + newTenKhoa + "' đã tồn tại.");
                // --- KẾT THÚC CẬP NHẬT ---
            }
        }

        // --- BƯỚC 3: CẬP NHẬT CÁC TRƯỜNG ---
        existingEntity.setTenKhoa(newTenKhoa);
        existingEntity.setMoTa(dto.getMoTa());

        // --- BƯỚC 4: GỌI DAO ĐỂ CẬP NHẬT ---
        boolean success = khoaDAO.update(existingEntity);
        if (!success) {
            // Giữ nguyên Exception vì đây là lỗi hệ thống
            throw new Exception("Cập nhật khoa thất bại.");
        }

        // --- BƯỚC 5: TRẢ VỀ DTO (ĐÃ CẬP NHẬT) ---
        return toDTO(existingEntity);
    }

    /**
     * Dịch vụ xóa một Khoa.
     */
    public void deleteKhoa(int khoaId) throws ValidationException, Exception {
        Khoa existingEntity = khoaDAO.getById(khoaId);
        if (existingEntity == null) {
            // --- CẬP NHẬT ---
            throw new ValidationException("Không tìm thấy khoa với ID: " + khoaId + " để xóa.");
            // --- KẾT THÚC CẬP NHẬT ---
        }

        boolean success = khoaDAO.delete(khoaId);
        if (!success) {
            // Giữ nguyên Exception vì đây là lỗi hệ thống
            throw new Exception("Xóa khoa thất bại.");
        }
    }

    /**
     * Lấy khoa bằng ID.
     */
    public KhoaDTO getKhoaById(int id) throws ValidationException {
        Khoa entity = khoaDAO.getById(id);
        if (entity == null) {
            // --- CẬP NHẬT ---
            throw new ValidationException("Không tìm thấy khoa với ID: " + id);
            // --- KẾT THÚC CẬP NHẬT ---
        }
        return toDTO(entity);
    }

    /**
     * Lấy tất cả các khoa.
     * (Giữ nguyên)
     */
    public List<KhoaDTO> getAllKhoa() {
        List<Khoa> entities = khoaDAO.getAll();

        // Dùng Java 8 Stream (vì dự án của bạn là 1.8)
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // --- CÁC HÀM MAPPER (Chuyển đổi DTO <-> Entity) ---
    // (Giữ nguyên)
    
    /**
     * Chuyển Khoa (Entity) sang KhoaDTO.
     */
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

    /**
     * Chuyển KhoaDTO sang Khoa (Entity).
     */
    private Khoa toEntity(KhoaDTO dto) {
        Khoa entity = new Khoa();
        // Không set ID (vì là tạo mới)
        entity.setTenKhoa(dto.getTenKhoa());
        entity.setMoTa(dto.getMoTa());
        return entity;
    }
}