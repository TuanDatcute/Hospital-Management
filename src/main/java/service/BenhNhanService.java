package service;

import model.Entity.BenhNhan;
import model.Entity.TaiKhoan;
import model.dao.BenhNhanDAO;
import model.dao.TaiKhoanDAO;
import model.dto.BenhNhanDTO;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp Service chứa logic nghiệp vụ cho BenhNhan.
 * @author ADMIN
 */
public class BenhNhanService {

    private final BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    /**
     * Dịch vụ tạo một Bệnh Nhân mới.
     * @param dto DTO chứa thông tin bệnh nhân mới.
     * @return DTO của bệnh nhân đã được tạo (có ID).
     * @throws Exception ném ra nếu logic validation thất bại.
     */
    public BenhNhanDTO createBenhNhan(BenhNhanDTO dto) throws Exception {
        // --- VALIDATION ---
        if (dto.getHoTen() == null || dto.getHoTen().trim().isEmpty()) {
            throw new Exception("Họ tên bệnh nhân không được để trống.");
        }
        if (dto.getMaBenhNhan() == null || dto.getMaBenhNhan().trim().isEmpty()) {
            throw new Exception("Mã bệnh nhân không được để trống.");
        }
        if (benhNhanDAO.isMaBenhNhanExisted(dto.getMaBenhNhan())) {
            throw new Exception("Mã bệnh nhân '" + dto.getMaBenhNhan() + "' đã tồn tại.");
        }

        // --- KIỂM TRA TÀI KHOẢN LIÊN KẾT ---
        TaiKhoan taiKhoanEntity = null;
        if (dto.getTaiKhoanId() != null && dto.getTaiKhoanId() > 0) {
            taiKhoanEntity = taiKhoanDAO.getById(dto.getTaiKhoanId());
            if (taiKhoanEntity == null) {
                throw new Exception("Không tìm thấy Tài khoản với ID: " + dto.getTaiKhoanId());
            }
            if (!"HOAT_DONG".equals(taiKhoanEntity.getTrangThai())) {
                 throw new Exception("Không thể gán tài khoản đã bị khóa (ID: " + dto.getTaiKhoanId() + ").");
            }
            if (benhNhanDAO.isTaiKhoanIdLinked(dto.getTaiKhoanId())) {
                 throw new Exception("Tài khoản này đã được gán cho một bệnh nhân khác.");
            }
        }

        // --- MAP & LƯU ---
        BenhNhan entity = toEntity(dto, taiKhoanEntity);
        BenhNhan savedEntity = benhNhanDAO.create(entity);

        // --- TRẢ VỀ DTO ---
        if (savedEntity != null) {
            BenhNhan fullSavedEntity = benhNhanDAO.getByIdWithRelations(savedEntity.getId());
            return toDTO(fullSavedEntity);
        }
        return null;
    }

    /**
     * Dịch vụ cập nhật thông tin Bệnh Nhân.
     */
    public BenhNhanDTO updateBenhNhan(int benhNhanId, BenhNhanDTO dto) throws Exception {
        BenhNhan existingEntity = benhNhanDAO.getByIdWithRelations(benhNhanId);
        if (existingEntity == null) {
            throw new Exception("Không tìm thấy bệnh nhân với ID: " + benhNhanId);
        }
        if (existingEntity.getTaiKhoan() != null && !"HOAT_DONG".equals(existingEntity.getTaiKhoan().getTrangThai())) {
             throw new Exception("Không thể cập nhật thông tin cho bệnh nhân có tài khoản bị khóa.");
        }

        if (dto.getHoTen() == null || dto.getHoTen().trim().isEmpty()) {
            throw new Exception("Họ tên không được để trống.");
        }
        String newMaBenhNhan = dto.getMaBenhNhan();
        if (newMaBenhNhan != null && !newMaBenhNhan.equals(existingEntity.getMaBenhNhan())) {
            if (benhNhanDAO.isMaBenhNhanExisted(newMaBenhNhan)) {
                throw new Exception("Mã bệnh nhân '" + newMaBenhNhan + "' đã tồn tại.");
            }
            existingEntity.setMaBenhNhan(newMaBenhNhan);
        }

        existingEntity.setHoTen(dto.getHoTen());
        existingEntity.setNgaySinh(dto.getNgaySinh());
        existingEntity.setGioiTinh(dto.getGioiTinh());
        existingEntity.setDiaChi(dto.getDiaChi());
        existingEntity.setSoDienThoai(dto.getSoDienThoai());
        existingEntity.setNhomMau(dto.getNhomMau());
        existingEntity.setTienSuBenh(dto.getTienSuBenh());

        Integer newTaiKhoanId = dto.getTaiKhoanId();
        TaiKhoan newTaiKhoanEntity = null;
        if (newTaiKhoanId != null && newTaiKhoanId > 0) {
            if (existingEntity.getTaiKhoan() == null || existingEntity.getTaiKhoan().getId() != newTaiKhoanId) {
                newTaiKhoanEntity = taiKhoanDAO.getById(newTaiKhoanId);
                if (newTaiKhoanEntity == null) {
                    throw new Exception("Không tìm thấy Tài khoản mới với ID: " + newTaiKhoanId);
                }
                if (!"HOAT_DONG".equals(newTaiKhoanEntity.getTrangThai())) {
                     throw new Exception("Không thể gán tài khoản mới đã bị khóa (ID: " + newTaiKhoanId + ").");
                }
                if (benhNhanDAO.isTaiKhoanIdLinked(newTaiKhoanId)) {
                    throw new Exception("Tài khoản mới (ID: " + newTaiKhoanId + ") đã được gán cho bệnh nhân khác.");
                }
                existingEntity.setTaiKhoan(newTaiKhoanEntity);
            }
        } else {
            existingEntity.setTaiKhoan(null);
        }

        boolean success = benhNhanDAO.update(existingEntity);
        if (!success) {
            throw new Exception("Cập nhật bệnh nhân thất bại.");
        }

        BenhNhan updatedEntity = benhNhanDAO.getByIdWithRelations(benhNhanId);
        return toDTO(updatedEntity);
    }

    /**
     * Lấy bệnh nhân bằng ID (chỉ trả về nếu bệnh nhân không có tài khoản, hoặc tài khoản đang hoạt động).
     */
    public BenhNhanDTO getBenhNhanById(int id) throws Exception {
        BenhNhan entity = benhNhanDAO.getByIdWithRelations(id);
        if (entity == null) {
            throw new Exception("Không tìm thấy bệnh nhân với ID: " + id);
        }
        if (entity.getTaiKhoan() != null && !"HOAT_DONG".equals(entity.getTaiKhoan().getTrangThai())) {
             throw new Exception("Bệnh nhân với ID: " + id + " có tài khoản bị khóa.");
        }
        return toDTO(entity);
    }

    /**
     * Lấy bệnh nhân bằng ID, bất kể trạng thái tài khoản.
     */
    public BenhNhanDTO getBenhNhanByIdEvenIfInactive(int id) throws Exception {
        BenhNhan entity = benhNhanDAO.getByIdWithRelations(id);
        if (entity == null) {
            throw new Exception("Không tìm thấy bệnh nhân với ID: " + id);
        }
        return toDTO(entity);
    }

    /**
     * Lấy tất cả bệnh nhân (chỉ lấy những người không có tài khoản, hoặc tài khoản đang hoạt động).
     */
    public List<BenhNhanDTO> getAllBenhNhan() {
        List<BenhNhan> entities = benhNhanDAO.getAllWithRelations();
        return entities.stream()
                       .filter(bn -> bn.getTaiKhoan() == null || "HOAT_DONG".equals(bn.getTaiKhoan().getTrangThai()))
                       .map(this::toDTO)
                       .collect(Collectors.toList());
    }
    
    /**
     * Lấy tất cả bệnh nhân CHƯA được gán giường.
     */
    public List<BenhNhanDTO> getBenhNhanChuaCoGiuong() {
        // 1. Gọi DAO
        // (Hàm này cần LEFT JOIN FETCH TaiKhoan để toDTO hoạt động)
        List<BenhNhan> entities = benhNhanDAO.getBenhNhanChuaCoGiuongWithRelations();
        
        // 2. Chuyển đổi sang DTO
        return entities.stream()
                       .map(this::toDTO)
                       .collect(Collectors.toList());
    }

    /**
     * Chuyển BenhNhan (Entity) sang BenhNhanDTO.
     */
    private BenhNhanDTO toDTO(BenhNhan entity) {
        if (entity == null) return null;
        BenhNhanDTO dto = new BenhNhanDTO();
        dto.setId(entity.getId());
        dto.setMaBenhNhan(entity.getMaBenhNhan());
        dto.setHoTen(entity.getHoTen());
        dto.setNgaySinh(entity.getNgaySinh());
        dto.setGioiTinh(entity.getGioiTinh());
        dto.setDiaChi(entity.getDiaChi());
        dto.setSoDienThoai(entity.getSoDienThoai());
        dto.setNhomMau(entity.getNhomMau());
        dto.setTienSuBenh(entity.getTienSuBenh());
        if (entity.getTaiKhoan() != null) {
            dto.setTaiKhoanId(entity.getTaiKhoan().getId());
        }
        return dto;
    }

    /**
     * Chuyển BenhNhanDTO sang BenhNhan (Entity).
     */
    private BenhNhan toEntity(BenhNhanDTO dto, TaiKhoan taiKhoan) {
        BenhNhan entity = new BenhNhan();
        entity.setMaBenhNhan(dto.getMaBenhNhan());
        entity.setHoTen(dto.getHoTen());
        entity.setNgaySinh(dto.getNgaySinh());
        entity.setGioiTinh(dto.getGioiTinh());
        entity.setDiaChi(dto.getDiaChi());
        entity.setSoDienThoai(dto.getSoDienThoai());
        entity.setNhomMau(dto.getNhomMau());
        entity.setTienSuBenh(dto.getTienSuBenh());
        entity.setTaiKhoan(taiKhoan);
        return entity;
    }
}