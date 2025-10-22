package service;

import model.Entity.BenhNhan;
import model.Entity.LichHen;
import model.Entity.NhanVien;
import model.dao.BenhNhanDAO;
import model.dao.LichHenDAO;
import model.dao.NhanVienDAO;
import model.dto.LichHenDTO;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp Service chứa logic nghiệp vụ cho LichHen.
 * @author ADMIN
 */
public class LichHenService {

    // Khởi tạo các DAO cần thiết
    private final LichHenDAO lichHenDAO = new LichHenDAO();
    private final BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    /**
     * Dịch vụ tạo một Lịch hẹn mới.
     * STT sẽ được tự động gán bởi Trigger CSDL.
     * @param dto DTO chứa thông tin lịch hẹn mới.
     * @return DTO của lịch hẹn đã được tạo (có ID và STT).
     * @throws Exception ném ra nếu logic validation thất bại.
     */
    public LichHenDTO createLichHen(LichHenDTO dto) throws Exception {
        
        // --- BƯỚC 1: LOGIC NGHIỆP VỤ (VALIDATION) ---
        if (dto.getBenhNhanId() <= 0) {
            throw new Exception("ID Bệnh nhân không hợp lệ.");
        }
        if (dto.getBacSiId() <= 0) {
            throw new Exception("ID Bác sĩ không hợp lệ.");
        }
        if (dto.getThoiGianHen() == null || dto.getThoiGianHen().isBefore(OffsetDateTime.now())) {
            throw new Exception("Thời gian hẹn phải là một thời điểm trong tương lai.");
        }

        // --- BƯỚC 2: KIỂM TRA & LẤY CÁC ENTITY LIÊN QUAN ---
        BenhNhan benhNhanEntity = benhNhanDAO.getById(dto.getBenhNhanId());
        if (benhNhanEntity == null) {
            throw new Exception("Không tìm thấy Bệnh nhân với ID: " + dto.getBenhNhanId());
        }

        NhanVien bacSiEntity = nhanVienDAO.getById(dto.getBacSiId());
        if (bacSiEntity == null) {
            throw new Exception("Không tìm thấy Bác sĩ (Nhân viên) với ID: " + dto.getBacSiId());
        }
        
        // (Bạn có thể kiểm tra thêm vai trò của bác sĩ nếu cần)
        // TaiKhoan tkBacSi = bacSiEntity.getTaiKhoan(); // Cần DAO tải lazy
        // if (tkBacSi == null || !"BAC_SI".equals(tkBacSi.getVaiTro())) {
        //     throw new Exception("Nhân viên này không có vai trò là Bác sĩ.");
        // }

        // --- BƯỚC 3: CHUYỂN ĐỔI (MAP) ---
        LichHen entity = toEntity(dto, benhNhanEntity, bacSiEntity);
        
        // Gán trạng thái mặc định khi tạo mới
        entity.setTrangThai("CHO_XAC_NHAN");
        // 'stt' sẽ do Trigger trong CSDL tự gán

        // --- BƯỚC 4: GỌI DAO ĐỂ LƯU ---
        LichHen savedEntity = lichHenDAO.create(entity);

        // --- BƯỚC 5: TRẢ VỀ DTO ---
        if (savedEntity != null) {
            // Sau khi lưu, entity đã có ID và STT, chúng ta cần
            // tải lại nó với các quan hệ để toDTO cho chuẩn
            LichHen fullSavedEntity = lichHenDAO.getByIdWithRelations(savedEntity.getId());
            return toDTO(fullSavedEntity);
        }
        return null;
    }

    /**
     * Dịch vụ cập nhật trạng thái của một Lịch hẹn.
     */
    public LichHenDTO updateTrangThaiLichHen(int lichHenId, String newTrangThai, String ghiChu) throws Exception {
        
        // --- BƯỚC 1: VALIDATION ---
        if (newTrangThai == null || newTrangThai.trim().isEmpty()) {
            throw new Exception("Trạng thái mới không được để trống.");
        }
        // (Bạn nên có một Enum hoặc List để kiểm tra newTrangThai có hợp lệ không)
        
        // --- BƯỚC 2: LẤY ENTITY GỐC ---
        // Chỉ cần lấy entity gốc, không cần relations vì chỉ update
        LichHen existingEntity = lichHenDAO.getById(lichHenId); 
        if (existingEntity == null) {
            throw new Exception("Không tìm thấy lịch hẹn với ID: " + lichHenId);
        }
        
        // --- BƯỚC 3: CẬP NHẬT ---
        existingEntity.setTrangThai(newTrangThai);
        if (ghiChu != null) {
            existingEntity.setGhiChu(ghiChu);
        }

        // --- BƯỚC 4: GỌI DAO ĐỂ CẬP NHẬT ---
        boolean success = lichHenDAO.update(existingEntity);
        if (!success) {
            throw new Exception("Cập nhật trạng thái lịch hẹn thất bại.");
        }

        // --- BƯỚC 5: TRẢ VỀ DTO (ĐÃ CẬP NHẬT) ---
        // Tải lại bản đầy đủ để trả về
        LichHen updatedEntity = lichHenDAO.getByIdWithRelations(lichHenId);
        return toDTO(updatedEntity);
    }

    /**
     * Lấy lịch hẹn bằng ID (tải đủ quan hệ).
     */
    public LichHenDTO getLichHenById(int id) throws Exception {
        // Luôn dùng ...WithRelations để map sang DTO
        LichHen entity = lichHenDAO.getByIdWithRelations(id); 
        if (entity == null) {
            throw new Exception("Không tìm thấy lịch hẹn với ID: " + id);
        }
        return toDTO(entity);
    }
    
    /**
     * Lấy tất cả lịch hẹn (tải đủ quan hệ).
     */
    public List<LichHenDTO> getAllLichHen() {
        List<LichHen> entities = lichHenDAO.getAllWithRelations();
        return entities.stream()
                       .map(this::toDTO)
                       .collect(Collectors.toList());
    }
    
    /**
     * Lấy tất cả lịch hẹn của một bác sĩ (tải đủ quan hệ).
     */
    public List<LichHenDTO> getLichHenByBacSi(int bacSiId) throws Exception {
        if (nhanVienDAO.getById(bacSiId) == null) {
             throw new Exception("Không tìm thấy Bác sĩ với ID: " + bacSiId);
        }
        List<LichHen> entities = lichHenDAO.findByBacSiId(bacSiId);
        return entities.stream()
                       .map(this::toDTO)
                       .collect(Collectors.toList());
    }
    
    /**
     * Lấy tất cả lịch hẹn của một bệnh nhân (tải đủ quan hệ).
     */
    public List<LichHenDTO> getLichHenByBenhNhan(int benhNhanId) throws Exception {
        if (benhNhanDAO.getById(benhNhanId) == null) {
             throw new Exception("Không tìm thấy Bệnh nhân với ID: " + benhNhanId);
        }
        List<LichHen> entities = lichHenDAO.findByBenhNhanId(benhNhanId);
        return entities.stream()
                       .map(this::toDTO)
                       .collect(Collectors.toList());
    }

    // --- CÁC HÀM MAPPER (Chuyển đổi DTO <-> Entity) ---

    /**
     * Chuyển LichHen (Entity) sang LichHenDTO.
     */
    private LichHenDTO toDTO(LichHen entity) {
        LichHenDTO dto = new LichHenDTO();
        dto.setId(entity.getId());
        dto.setStt(entity.getStt());
        dto.setThoiGianHen(entity.getThoiGianHen());
        dto.setLyDoKham(entity.getLyDoKham());
        dto.setTrangThai(entity.getTrangThai());
        dto.setGhiChu(entity.getGhiChu());

        // "Làm phẳng" (Vì DAO đã dùng JOIN FETCH nên không sợ Lazy)
        if (entity.getBenhNhan() != null) {
            dto.setBenhNhanId(entity.getBenhNhan().getId());
        }
        if (entity.getBacSi() != null) {
            dto.setBacSiId(entity.getBacSi().getId());
        }
        
        return dto;
    }

    /**
     * Chuyển LichHenDTO sang LichHen (Entity).
     * Cần các đối tượng BenhNhan và NhanVien (BacSi) đã được Service lấy từ CSDL.
     */
    private LichHen toEntity(LichHenDTO dto, BenhNhan benhNhan, NhanVien bacSi) {
        LichHen entity = new LichHen();
        
        // Không set ID (tạo mới)
        // Không set STT (trigger tự gán)
        entity.setThoiGianHen(dto.getThoiGianHen());
        entity.setLyDoKham(dto.getLyDoKham());
        entity.setTrangThai(dto.getTrangThai());
        entity.setGhiChu(dto.getGhiChu());
        
        // Gán các đối tượng Entity liên quan
        entity.setBenhNhan(benhNhan);
        entity.setBacSi(bacSi);
        
        return entity;
    }
}