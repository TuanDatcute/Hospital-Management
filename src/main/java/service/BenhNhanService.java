package service;

import exception.ValidationException; // <-- Giữ import của bạn
import model.Entity.BenhNhan;
import model.Entity.TaiKhoan;
import model.dao.BenhNhanDAO;
import model.dao.TaiKhoanDAO;
import model.dto.BenhNhanDTO;
import model.dto.TaiKhoanDTO; // <-- Thêm import này
import java.time.LocalDate; // <-- SỬA: Dùng LocalDate cho Ngày sinh
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp Service chứa logic nghiệp vụ cho BenhNhan.
 * @author ADMIN (Đã cập nhật)
 */
public class BenhNhanService {

    private final BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    /**
     * Dịch vụ tạo một Bệnh Nhân mới (do Admin/Lễ tân tạo).
     */
    public BenhNhanDTO createBenhNhan(BenhNhanDTO dto) throws ValidationException, Exception {
        // --- VALIDATION (Sử dụng ValidationException) ---
        if (dto.getHoTen() == null || dto.getHoTen().trim().isEmpty()) {
            throw new ValidationException("Họ tên bệnh nhân không được để trống.");
        }
        if (dto.getMaBenhNhan() == null || dto.getMaBenhNhan().trim().isEmpty()) {
            throw new ValidationException("Mã bệnh nhân không được để trống.");
        }
        if (benhNhanDAO.isMaBenhNhanExisted(dto.getMaBenhNhan())) {
            throw new ValidationException("Mã bệnh nhân '" + dto.getMaBenhNhan() + "' đã tồn tại.");
        }

        // --- KIỂM TRA TÀI KHOẢN LIÊN KẾT ---
        TaiKhoan taiKhoanEntity = null;
        if (dto.getTaiKhoanId() != null && dto.getTaiKhoanId() > 0) {
            taiKhoanEntity = taiKhoanDAO.getById(dto.getTaiKhoanId());
            if (taiKhoanEntity == null) {
                throw new ValidationException("Không tìm thấy Tài khoản với ID: " + dto.getTaiKhoanId());
            }
            if (!"HOAT_DONG".equals(taiKhoanEntity.getTrangThai())) {
                 throw new ValidationException("Không thể gán tài khoản đã bị khóa (ID: " + dto.getTaiKhoanId() + ").");
            }
            if (benhNhanDAO.isTaiKhoanIdLinked(dto.getTaiKhoanId())) {
                 throw new ValidationException("Tài khoản này đã được gán cho một bệnh nhân khác.");
            }
        }

        // --- MAP & LƯU ---
        BenhNhan entity = new BenhNhan();
        entity = toEntity(dto, entity); // Dùng hàm mapper mới
        entity.setTaiKhoan(taiKhoanEntity);
        
        BenhNhan savedEntity = benhNhanDAO.create(entity);
        return toDTO(savedEntity);
    }

    /**
     * Dịch vụ cập nhật thông tin Bệnh Nhân.
     */
    public BenhNhanDTO updateBenhNhan(int benhNhanId, BenhNhanDTO dto) throws ValidationException, Exception {
        BenhNhan existingEntity = benhNhanDAO.getByIdWithRelations(benhNhanId);
        if (existingEntity == null) {
            throw new ValidationException("Không tìm thấy bệnh nhân với ID: " + benhNhanId);
        }
        if (existingEntity.getTaiKhoan() != null && !"HOAT_DONG".equals(existingEntity.getTaiKhoan().getTrangThai())) {
             throw new ValidationException("Không thể cập nhật thông tin cho bệnh nhân có tài khoản bị khóa.");
        }

        if (dto.getHoTen() == null || dto.getHoTen().trim().isEmpty()) {
            throw new ValidationException("Họ tên không được để trống.");
        }
        String newMaBenhNhan = dto.getMaBenhNhan();
        if (newMaBenhNhan != null && !newMaBenhNhan.equals(existingEntity.getMaBenhNhan())) {
            if (benhNhanDAO.isMaBenhNhanExisted(newMaBenhNhan)) {
                throw new ValidationException("Mã bệnh nhân '" + newMaBenhNhan + "' đã tồn tại.");
            }
        }

        // Cập nhật Entity từ DTO (dùng hàm toEntity mới)
        existingEntity = toEntity(dto, existingEntity);

        // Xử lý cập nhật tài khoản (logic cũ của bạn đã đúng)
        Integer newTaiKhoanId = dto.getTaiKhoanId();
        TaiKhoan newTaiKhoanEntity = null;
        if (newTaiKhoanId != null && newTaiKhoanId > 0) {
            if (existingEntity.getTaiKhoan() == null || existingEntity.getTaiKhoan().getId() != newTaiKhoanId) {
                newTaiKhoanEntity = taiKhoanDAO.getById(newTaiKhoanId);
                if (newTaiKhoanEntity == null) {
                    throw new ValidationException("Không tìm thấy Tài khoản mới với ID: " + newTaiKhoanId);
                }
                if (!"HOAT_DONG".equals(newTaiKhoanEntity.getTrangThai())) {
                     throw new ValidationException("Không thể gán tài khoản mới đã bị khóa (ID: " + newTaiKhoanId + ").");
                }
                if (benhNhanDAO.isTaiKhoanIdLinked(newTaiKhoanId)) {
                    throw new ValidationException("Tài khoản mới (ID: " + newTaiKhoanId + ") đã được gán cho bệnh nhân khác.");
                }
                existingEntity.setTaiKhoan(newTaiKhoanEntity);
            }
        } else {
            existingEntity.setTaiKhoan(null);
        }

        boolean success = benhNhanDAO.update(existingEntity);
        if (!success) {
            throw new Exception("Cập nhật bệnh nhân thất bại."); // Lỗi hệ thống
        }

        BenhNhan updatedEntity = benhNhanDAO.getByIdWithRelations(benhNhanId);
        return toDTO(updatedEntity);
    }

    // --- **CÁC HÀM MỚI CHO LUỒNG ĐĂNG KÝ (USER TỰ LÀM)** ---

    /**
     * **HÀM MỚI (Bước 3.1):** Tạo một hồ sơ Bệnh nhân rỗng liên kết với tài khoản.
     * Được gọi bởi UserController ngay sau khi đăng ký.
     */
    public BenhNhanDTO createBenhNhanFromTaiKhoan(TaiKhoanDTO taiKhoanDTO) throws ValidationException, Exception {
        if (taiKhoanDTO == null || taiKhoanDTO.getId() == 0) {
            throw new ValidationException("Tài khoản DTO không hợp lệ.");
        }
        
        TaiKhoan taiKhoanEntity = taiKhoanDAO.getById(taiKhoanDTO.getId());
        if (taiKhoanEntity == null) {
            throw new Exception("Không tìm thấy tài khoản (ID: " + taiKhoanDTO.getId() + ") để liên kết.");
        }

        BenhNhan newBenhNhan = new BenhNhan();
        newBenhNhan.setTaiKhoan(taiKhoanEntity);
        
        // Đặt các giá trị bắt buộc (NOT NULL)
        newBenhNhan.setHoTen(taiKhoanDTO.getTenDangNhap()); // Dùng Tên đăng nhập làm Họ tên tạm thời
        newBenhNhan.setMaBenhNhan("BN-" + taiKhoanDTO.getId() + System.currentTimeMillis() % 10000); // Mã tạm thời
        
        BenhNhan savedEntity = benhNhanDAO.create(newBenhNhan);
        return toDTO(savedEntity);
    }
    
    /**
     * **HÀM MỚI (Bước 3.2):** Cập nhật hồ sơ cá nhân (sau khi đăng ký).
     * Được gọi bởi BenhNhanController (action 'updateProfile').
     */
    public BenhNhanDTO updateProfile(int benhNhanId, BenhNhanDTO dto) throws ValidationException, Exception {
        BenhNhan existingBenhNhan = benhNhanDAO.getById(benhNhanId);
        if (existingBenhNhan == null) {
            throw new ValidationException("Không tìm thấy hồ sơ bệnh nhân (ID: " + benhNhanId + ") để cập nhật.");
        }

        // Validation (Các trường bắt buộc)
        if (dto.getHoTen() == null || dto.getHoTen().trim().isEmpty()) { throw new ValidationException("Họ tên không được để trống."); }
        if (dto.getCccd() == null || dto.getCccd().trim().isEmpty()) { throw new ValidationException("CCCD không được để trống."); }
        if (dto.getNgaySinh() == null) { throw new ValidationException("Ngày sinh không được để trống."); }
        if (dto.getGioiTinh() == null || dto.getGioiTinh().trim().isEmpty()) { throw new ValidationException("Giới tính không được để trống."); }
        if (dto.getSoDienThoai() == null || dto.getSoDienThoai().trim().isEmpty()) { throw new ValidationException("Số điện thoại không được để trống."); }
        if (dto.getDiaChi() == null || dto.getDiaChi().trim().isEmpty()) { throw new ValidationException("Địa chỉ không được để trống."); }
        // (Nhóm máu, Tiền sử bệnh là tùy chọn, không cần validate)

        // Map dữ liệu mới từ DTO vào Entity
        existingBenhNhan = toEntity(dto, existingBenhNhan);
        
        // Cập nhật CSDL
        boolean success = benhNhanDAO.update(existingBenhNhan);
        if (!success) {
            throw new Exception("Cập nhật hồ sơ thất bại."); // Lỗi hệ thống
        }
        return toDTO(existingBenhNhan);
    }

    /**
     * **HÀM MỚI (Bước 3.3):** Tìm BenhNhan DTO bằng TaiKhoan ID
     */
    public BenhNhanDTO getBenhNhanByTaiKhoanId(int taiKhoanId) {
        BenhNhan entity = benhNhanDAO.findByTaiKhoanId(taiKhoanId);
        return toDTO(entity);
    }
    
    // --- CÁC HÀM GET KHÁC (Đã sửa lỗi ném Exception) ---

    public BenhNhanDTO getBenhNhanById(int id) throws ValidationException, Exception {
        BenhNhan entity = benhNhanDAO.getByIdWithRelations(id);
        if (entity == null) {
            throw new ValidationException("Không tìm thấy bệnh nhân với ID: " + id);
        }
        if (entity.getTaiKhoan() != null && !"HOAT_DONG".equals(entity.getTaiKhoan().getTrangThai())) {
             throw new ValidationException("Bệnh nhân với ID: " + id + " có tài khoản bị khóa.");
        }
        return toDTO(entity);
    }

    public BenhNhanDTO getBenhNhanByIdEvenIfInactive(int id) throws ValidationException, Exception {
        BenhNhan entity = benhNhanDAO.getByIdWithRelations(id);
        if (entity == null) {
            throw new ValidationException("Không tìm thấy bệnh nhân với ID: " + id);
        }
        return toDTO(entity);
    }

    public List<BenhNhanDTO> getAllBenhNhan() {
        List<BenhNhan> entities = benhNhanDAO.getAllWithRelations();
        return entities.stream()
                       .filter(bn -> bn.getTaiKhoan() == null || "HOAT_DONG".equals(bn.getTaiKhoan().getTrangThai()))
                       .map(this::toDTO)
                       .collect(Collectors.toList());
    }
    
    public List<BenhNhanDTO> getBenhNhanChuaCoGiuong() {
        List<BenhNhan> entities = benhNhanDAO.getBenhNhanChuaCoGiuongWithRelations();
        return entities.stream()
                       .map(this::toDTO)
                       .collect(Collectors.toList());
    }

    // --- CÁC HÀM MAPPER (Chuyển đổi DTO <-> Entity) ---

    /**
     * **CẬP NHẬT:** Chuyển Entity sang DTO (Thêm các trường mới)
     */
    private BenhNhanDTO toDTO(BenhNhan entity) {
        if (entity == null) return null;
        
        BenhNhanDTO dto = new BenhNhanDTO();
        dto.setId(entity.getId());
        dto.setMaBenhNhan(entity.getMaBenhNhan());
        
        // Các trường mới
        dto.setHoTen(entity.getHoTen());
        dto.setNgaySinh(entity.getNgaySinh()); // LocalDate
        dto.setGioiTinh(entity.getGioiTinh());
        dto.setDiaChi(entity.getDiaChi());
        dto.setSoDienThoai(entity.getSoDienThoai());
        dto.setCccd(entity.getCccd());
        
        dto.setNhomMau(entity.getNhomMau());
        dto.setTienSuBenh(entity.getTienSuBenh());

        if (entity.getTaiKhoan() != null) {
            dto.setTaiKhoanId(entity.getTaiKhoan().getId());
        }
        return dto;
    }

    /**
     * **CẬP NHẬT:** Thay thế hàm toEntity cũ.
     * Hàm này CẬP NHẬT một entity đã tồn tại (existingEntity) 
     * từ một DTO chứa đầy đủ thông tin.
     */
    private BenhNhan toEntity(BenhNhanDTO dto, BenhNhan entity) {
        if (entity == null) {
            entity = new BenhNhan();
        }
        
        // Các trường mới
        entity.setHoTen(dto.getHoTen());
        entity.setNgaySinh(dto.getNgaySinh()); // LocalDate
        entity.setGioiTinh(dto.getGioiTinh());
        entity.setDiaChi(dto.getDiaChi());
        entity.setSoDienThoai(dto.getSoDienThoai());
        entity.setCccd(dto.getCccd());
        
        entity.setMaBenhNhan(dto.getMaBenhNhan());
        entity.setNhomMau(dto.getNhomMau());
        entity.setTienSuBenh(dto.getTienSuBenh());
        
        // Không map ID (vì nó được dùng để tìm)
        // Không map TaiKhoan (TaiKhoan được xử lý riêng trong các hàm service)
        return entity;
    }
}