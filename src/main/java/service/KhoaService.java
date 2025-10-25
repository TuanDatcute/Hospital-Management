package service;

import model.Entity.Khoa; 
import model.dao.KhoaDAO;
import model.dto.KhoaDTO;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp Service chứa logic nghiệp vụ cho Khoa.
 * @author ADMIN
 */
public class KhoaService {

    private final KhoaDAO khoaDAO = new KhoaDAO();

    /**
     * Dịch vụ tạo một Khoa mới.
     * @param dto DTO chứa thông tin khoa mới.
     * @return DTO của khoa đã được tạo (có ID).
     * @throws Exception ném ra nếu tên khoa bị trùng hoặc để trống.
     */
    public KhoaDTO createKhoa(KhoaDTO dto) throws Exception {
        
        // --- BƯỚC 1: LOGIC NGHIỆP VỤ (VALIDATION) ---
        if (dto.getTenKhoa() == null || dto.getTenKhoa().trim().isEmpty()) {
            throw new Exception("Tên khoa không được để trống.");
        }
        
        String tenKhoa = dto.getTenKhoa().trim();
        
        if (khoaDAO.isTenKhoaExisted(tenKhoa)) {
            throw new Exception("Tên khoa '" + tenKhoa + "' đã tồn tại.");
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
        return null;
    }

    /**
     * Dịch vụ cập nhật thông tin Khoa.
     */
    public KhoaDTO updateKhoa(int khoaId, KhoaDTO dto) throws Exception {
        
        // --- BƯỚC 1: LẤY ENTITY GỐC ---
        Khoa existingEntity = khoaDAO.getById(khoaId);
        if (existingEntity == null) {
            throw new Exception("Không tìm thấy khoa với ID: " + khoaId);
        }

        // --- BƯỚC 2: VALIDATION ---
        if (dto.getTenKhoa() == null || dto.getTenKhoa().trim().isEmpty()) {
            throw new Exception("Tên khoa không được để trống.");
        }
        
        String newTenKhoa = dto.getTenKhoa().trim();
        
        // Chỉ kiểm tra trùng tên NẾU tên mới khác tên cũ
        if (!newTenKhoa.equalsIgnoreCase(existingEntity.getTenKhoa())) {
            if (khoaDAO.isTenKhoaExisted(newTenKhoa)) {
                throw new Exception("Tên khoa '" + newTenKhoa + "' đã tồn tại.");
            }
        }
        
        // --- BƯỚC 3: CẬP NHẬT CÁC TRƯỜNG ---
        existingEntity.setTenKhoa(newTenKhoa);
        existingEntity.setMoTa(dto.getMoTa());

        // --- BƯỚC 4: GỌI DAO ĐỂ CẬP NHẬT ---
        boolean success = khoaDAO.update(existingEntity);
        if (!success) {
            throw new Exception("Cập nhật khoa thất bại.");
        }

        // --- BƯỚC 5: TRẢ VỀ DTO (ĐÃ CẬP NHẬT) ---
        return toDTO(existingEntity);
    }

    /**
     * Dịch vụ xóa một Khoa.
     * (Lưu ý: CSDL của bạn có 'ON DELETE SET NULL', 
     * nên việc xóa Khoa sẽ tự động gán KhoaId = NULL cho các Nhân Viên liên quan)
     */
    public void deleteKhoa(int khoaId) throws Exception {
        Khoa existingEntity = khoaDAO.getById(khoaId);
        if (existingEntity == null) {
            throw new Exception("Không tìm thấy khoa với ID: " + khoaId + " để xóa.");
        }
        
        boolean success = khoaDAO.delete(khoaId);
        if (!success) {
            throw new Exception("Xóa khoa thất bại.");
        }
    }

    /**
     * Lấy khoa bằng ID.
     */
    public KhoaDTO getKhoaById(int id) throws Exception {
        Khoa entity = khoaDAO.getById(id);
        if (entity == null) {
            throw new Exception("Không tìm thấy khoa với ID: " + id);
        }
        return toDTO(entity);
    }
    
    /**
     * Lấy tất cả các khoa.
     */
    public List<KhoaDTO> getAllKhoa() {
        List<Khoa> entities = khoaDAO.getAll();
        
        // Dùng Java 8 Stream (vì dự án của bạn là 1.8)
        return entities.stream()
                       .map(this::toDTO)
                       .collect(Collectors.toList());
    }

    // --- CÁC HÀM MAPPER (Chuyển đổi DTO <-> Entity) ---

    /**
     * Chuyển Khoa (Entity) sang KhoaDTO.
     */
    private KhoaDTO toDTO(Khoa entity) {
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