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

    // Khởi tạo các DAO cần thiết
    private final BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    /**
     * Dịch vụ tạo một Bệnh Nhân mới.
     * @param dto DTO chứa thông tin bệnh nhân mới.
     * @return DTO của bệnh nhân đã được tạo (có ID).
     * @throws Exception ném ra nếu logic validation thất bại.
     */
    public BenhNhanDTO createBenhNhan(BenhNhanDTO dto) throws Exception {
        
        // --- BƯỚC 1: LOGIC NGHIỆP VỤ (VALIDATION) ---
        
        if (dto.getHoTen() == null || dto.getHoTen().trim().isEmpty()) {
            throw new Exception("Họ tên bệnh nhân không được để trống.");
        }
        if (dto.getMaBenhNhan() == null || dto.getMaBenhNhan().trim().isEmpty()) {
            throw new Exception("Mã bệnh nhân không được để trống.");
        }
        
        // Kiểm tra Mã bệnh nhân trùng
        if (benhNhanDAO.isMaBenhNhanExisted(dto.getMaBenhNhan())) {
            throw new Exception("Mã bệnh nhân '" + dto.getMaBenhNhan() + "' đã tồn tại.");
        }
        
        // --- BƯỚC 2: KIỂM TRA & LẤY CÁC ENTITY LIÊN QUAN ---
        
        TaiKhoan taiKhoanEntity = null;
        if (dto.getTaiKhoanId() != null && dto.getTaiKhoanId() > 0) {
            taiKhoanEntity = taiKhoanDAO.getById(dto.getTaiKhoanId());
            if (taiKhoanEntity == null) {
                throw new Exception("Không tìm thấy Tài khoản với ID: " + dto.getTaiKhoanId());
            }
            
            // Kiểm tra tài khoản đã được gán cho bệnh nhân khác chưa (UNIQUE)
            if (benhNhanDAO.isTaiKhoanIdLinked(dto.getTaiKhoanId())) {
                 throw new Exception("Tài khoản này đã được gán cho một bệnh nhân khác.");
            }
            
            // (Bạn cũng có thể muốn kiểm tra xem tài khoản này có vai trò 'BENH_NHAN' không)
            // if (!"BENH_NHAN".equals(taiKhoanEntity.getVaiTro())) {
            //     throw new Exception("Tài khoản này không có vai trò là Bệnh nhân.");
            // }
        }

        // --- BƯỚC 3: CHUYỂN ĐỔI (MAP) ---
        BenhNhan entity = toEntity(dto, taiKhoanEntity);

        // --- BƯỚC 4: GỌI DAO ĐỂ LƯU ---
        BenhNhan savedEntity = benhNhanDAO.create(entity);

        // --- BƯỚC 5: TRẢ VỀ DTO ---
        if (savedEntity != null) {
            return toDTO(savedEntity);
        }
        return null;
    }

    /**
     * Dịch vụ cập nhật thông tin Bệnh Nhân.
     */
    public BenhNhanDTO updateBenhNhan(int benhNhanId, BenhNhanDTO dto) throws Exception {
        
        // --- BƯỚC 1: LẤY ENTITY GỐC ---
        BenhNhan existingEntity = benhNhanDAO.getById(benhNhanId);
        if (existingEntity == null) {
            throw new Exception("Không tìm thấy bệnh nhân với ID: " + benhNhanId);
        }

        // --- BƯỚC 2: VALIDATION ---
        if (dto.getHoTen() == null || dto.getHoTen().trim().isEmpty()) {
            throw new Exception("Họ tên không được để trống.");
        }
        
        // Kiểm tra Mã bệnh nhân nếu có thay đổi
        String newMaBenhNhan = dto.getMaBenhNhan();
        if (newMaBenhNhan != null && !newMaBenhNhan.equals(existingEntity.getMaBenhNhan())) {
            if (benhNhanDAO.isMaBenhNhanExisted(newMaBenhNhan)) {
                throw new Exception("Mã bệnh nhân '" + newMaBenhNhan + "' đã tồn tại.");
            }
            existingEntity.setMaBenhNhan(newMaBenhNhan);
        }
        
        // --- BƯỚC 3: CẬP NHẬT CÁC TRƯỜNG ---
        existingEntity.setHoTen(dto.getHoTen());
        existingEntity.setNgaySinh(dto.getNgaySinh());
        existingEntity.setGioiTinh(dto.getGioiTinh());
        existingEntity.setDiaChi(dto.getDiaChi());
        existingEntity.setSoDienThoai(dto.getSoDienThoai());
        existingEntity.setNhomMau(dto.getNhomMau());
        existingEntity.setTienSuBenh(dto.getTienSuBenh());

        // Cập nhật liên kết Tài Khoản (phức tạp hơn)
        Integer newTaiKhoanId = dto.getTaiKhoanId();
        if (newTaiKhoanId != null && newTaiKhoanId > 0) {
            // Nếu bệnh nhân chưa có tài khoản, hoặc có tài khoản mới
            if (existingEntity.getTaiKhoan() == null || existingEntity.getTaiKhoan().getId() != newTaiKhoanId) {
                TaiKhoan taiKhoanEntity = taiKhoanDAO.getById(newTaiKhoanId);
                if (taiKhoanEntity == null) {
                    throw new Exception("Không tìm thấy Tài khoản mới với ID: " + newTaiKhoanId);
                }
                // Kiểm tra tài khoản mới đã bị gán chưa
                if (benhNhanDAO.isTaiKhoanIdLinked(newTaiKhoanId)) {
                    throw new Exception("Tài khoản mới (ID: " + newTaiKhoanId + ") đã được gán cho bệnh nhân khác.");
                }
                existingEntity.setTaiKhoan(taiKhoanEntity);
            }
        } else {
            // Nếu DTO gửi lên taiKhoanId = null, nghĩa là muốn xóa liên kết
            existingEntity.setTaiKhoan(null);
        }
        
        // --- BƯỚC 4: GỌI DAO ĐỂ CẬP NHẬT ---
        boolean success = benhNhanDAO.update(existingEntity);
        if (!success) {
            throw new Exception("Cập nhật bệnh nhân thất bại.");
        }

        // --- BƯỚC 5: TRẢ VỀ DTO (ĐÃ CẬP NHẬT) ---
        return toDTO(existingEntity);
    }


    /**
     * Lấy bệnh nhân bằng ID.
     */
    public BenhNhanDTO getBenhNhanById(int id) throws Exception {
        // Dùng ...WithRelations để tải cả TaiKhoan (nếu có)
        BenhNhan entity = benhNhanDAO.getByIdWithRelations(id); 
        if (entity == null) {
            throw new Exception("Không tìm thấy bệnh nhân với ID: " + id);
        }
        return toDTO(entity);
    }
    
    /**
     * Lấy tất cả bệnh nhân.
     */
    public List<BenhNhanDTO> getAllBenhNhan() {
        // Dùng ...WithRelations để tải cả TaiKhoan, tránh lỗi N+1
        List<BenhNhan> entities = benhNhanDAO.getAllWithRelations();
        
        return entities.stream()
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

    // --- CÁC HÀM MAPPER (Chuyển đổi DTO <-> Entity) ---

    /**
     * Chuyển BenhNhan (Entity) sang BenhNhanDTO.
     */
    private BenhNhanDTO toDTO(BenhNhan entity) {
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

        // "Làm phẳng" liên kết (kiểm tra null vì taiKhoanId là optional)
        if (entity.getTaiKhoan() != null) {
            dto.setTaiKhoanId(entity.getTaiKhoan().getId());
        }
        
        return dto;
    }

    /**
     * Chuyển BenhNhanDTO sang BenhNhan (Entity).
     * Cần đối tượng TaiKhoan (có thể null) đã được Service lấy từ CSDL.
     */
    private BenhNhan toEntity(BenhNhanDTO dto, TaiKhoan taiKhoan) {
        BenhNhan entity = new BenhNhan();
        
        // Không set ID (vì là tạo mới)
        entity.setMaBenhNhan(dto.getMaBenhNhan());
        entity.setHoTen(dto.getHoTen());
        entity.setNgaySinh(dto.getNgaySinh());
        entity.setGioiTinh(dto.getGioiTinh());
        entity.setDiaChi(dto.getDiaChi());
        entity.setSoDienThoai(dto.getSoDienThoai());
        entity.setNhomMau(dto.getNhomMau());
        entity.setTienSuBenh(dto.getTienSuBenh());
        
        // Gán đối tượng Entity liên quan (có thể null)
        entity.setTaiKhoan(taiKhoan);
        
        return entity;
    }
}