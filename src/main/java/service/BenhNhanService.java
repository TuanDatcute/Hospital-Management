package service;

import exception.ValidationException; // <-- Giữ import của bạn
import model.Entity.BenhNhan;
import model.Entity.TaiKhoan;
import model.dao.BenhNhanDAO;
import model.dao.TaiKhoanDAO;
import model.dto.BenhNhanDTO;
import model.dto.TaiKhoanDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.regex.Pattern; // <-- **THÊM IMPORT REGEX**

/**
 * Lớp Service chứa logic nghiệp vụ cho BenhNhan.
 * @author ADMIN (Đã cập nhật)
 */
public class BenhNhanService {

    private final BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    
    // **ĐỊNH NGHĨA REGEX CHO SỐ ĐIỆN THOẠI VIỆT NAM**
    private static final Pattern PHONE_REGEX = Pattern.compile("^0[35789]\\d{8}$");
    // **ĐỊNH NGHĨA REGEX CHO CCCD**
    private static final Pattern CCCD_REGEX = Pattern.compile("^(\\d{9}|\\d{12})$");

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
        
        // --- VALIDATION REGEX SĐT (Nếu có nhập) ---
        if (dto.getSoDienThoai() != null && !dto.getSoDienThoai().trim().isEmpty()) {
            if (!PHONE_REGEX.matcher(dto.getSoDienThoai()).matches()) {
                throw new ValidationException("Định dạng số điện thoại không hợp lệ. Phải là 10 số (ví dụ: 0912345678).");
            }
        }
        // --- VALIDATION REGEX CCCD (Nếu có nhập) ---
        if (dto.getCccd() != null && !dto.getCccd().trim().isEmpty()) {
            if (!CCCD_REGEX.matcher(dto.getCccd()).matches()) {
                throw new ValidationException("Định dạng CCCD không hợp lệ. Phải là 9 hoặc 12 chữ số.");
            }
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

        BenhNhan entity = new BenhNhan();
        entity = toEntity(dto, entity);
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

        // --- VALIDATION REGEX SĐT (Nếu có nhập) ---
        if (dto.getSoDienThoai() != null && !dto.getSoDienThoai().trim().isEmpty()) {
            if (!PHONE_REGEX.matcher(dto.getSoDienThoai()).matches()) {
                throw new ValidationException("Định dạng số điện thoại không hợp lệ. Phải là 10 số (ví dụ: 0912345678).");
            }
        }
        // --- VALIDATION REGEX CCCD (Nếu có nhập) ---
        if (dto.getCccd() != null && !dto.getCccd().trim().isEmpty()) {
            if (!CCCD_REGEX.matcher(dto.getCccd()).matches()) {
                throw new ValidationException("Định dạng CCCD không hợp lệ. Phải là 9 hoặc 12 chữ số.");
            }
        }

        existingEntity = toEntity(dto, existingEntity);

        // Xử lý cập nhật tài khoản (logic cũ của bạn đã đúng)
        Integer newTaiKhoanId = dto.getTaiKhoanId();
        TaiKhoan newTaiKhoanEntity = null;
        if (newTaiKhoanId != null && newTaiKhoanId > 0) {
            if (existingEntity.getTaiKhoan() == null || existingEntity.getTaiKhoan().getId() != newTaiKhoanId) {
                newTaiKhoanEntity = taiKhoanDAO.getById(newTaiKhoanId);
                if (newTaiKhoanEntity == null) { throw new ValidationException("Không tìm thấy Tài khoản mới với ID: " + newTaiKhoanId); }
                if (!"HOAT_DONG".equals(newTaiKhoanEntity.getTrangThai())) { throw new ValidationException("Không thể gán tài khoản mới đã bị khóa."); }
                if (benhNhanDAO.isTaiKhoanIdLinked(newTaiKhoanId)) { throw new ValidationException("Tài khoản mới đã được gán cho bệnh nhân khác."); }
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

    // --- CÁC HÀM MỚI CHO LUỒNG ĐĂNG KÝ (USER TỰ LÀM) ---

    /**
     * **HÀM MỚI (Bước 3.1):** Tạo một hồ sơ Bệnh nhân rỗng liên kết với tài khoản.
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
        
        newBenhNhan.setHoTen(taiKhoanDTO.getTenDangNhap()); // Dùng Tên đăng nhập làm Họ tên tạm thời
        newBenhNhan.setMaBenhNhan("BN-" + taiKhoanDTO.getId() + System.currentTimeMillis() % 10000); // Mã tạm thời
        
        BenhNhan savedEntity = benhNhanDAO.create(newBenhNhan);
        return toDTO(savedEntity);
    }
    
    /**
     * **HÀM MỚI (Bước 3.2):** Cập nhật hồ sơ cá nhân (sau khi đăng ký).
     */
    public BenhNhanDTO updateProfile(int benhNhanId, BenhNhanDTO dto) throws ValidationException, Exception {
        
        BenhNhan existingBenhNhan = benhNhanDAO.getByIdWithRelations(benhNhanId);
        
        if (existingBenhNhan == null) {
            throw new Exception("Không tìm thấy hồ sơ bệnh nhân (ID: " + benhNhanId + ") để cập nhật.");
        }

        // --- VALIDATION (Các trường bắt buộc - Dùng ValidationException) ---
        
        if (dto.getHoTen() == null || dto.getHoTen().trim().isEmpty()) { 
            throw new ValidationException("Họ tên không được để trống."); 
        }
        
        String cccd = dto.getCccd();
        if (cccd == null || cccd.trim().isEmpty()) {
            throw new ValidationException("CCCD không được để trống.");
        }
        if (!CCCD_REGEX.matcher(cccd).matches()) {
            throw new ValidationException("Định dạng CCCD không hợp lệ. Phải là 9 hoặc 12 chữ số.");
        }
        
        if (dto.getNgaySinh() == null) { 
            throw new ValidationException("Ngày sinh không được để trống."); 
        }
        if (dto.getGioiTinh() == null || dto.getGioiTinh().trim().isEmpty()) { 
            throw new ValidationException("Giới tính không được để trống."); 
        }
        
        String sdt = dto.getSoDienThoai();
        if (sdt == null || sdt.trim().isEmpty()) { 
            throw new ValidationException("Số điện thoại không được để trống."); 
        }
        if (!PHONE_REGEX.matcher(sdt).matches()) {
            throw new ValidationException("Định dạng số điện thoại không hợp lệ. Phải là 10 số (ví dụ: 0912345678).");
        }
        
        if (dto.getDiaChi() == null || dto.getDiaChi().trim().isEmpty()) { 
            throw new ValidationException("Địa chỉ không được để trống."); 
        }
        // --- KẾT THÚC VALIDATION ---

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
    
    // --- CÁC HÀM GET KHÁC ---

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
     * **CẬP NHẬT:** Chuyển Entity sang DTO
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
        dto.setCccd(entity.getCccd());
        dto.setNhomMau(entity.getNhomMau());
        dto.setTienSuBenh(entity.getTienSuBenh());

        if (entity.getTaiKhoan() != null) {
            dto.setTaiKhoanId(entity.getTaiKhoan().getId());
        }
        return dto;
    }

    /**
     * **CẬP NHẬT:** Chuyển DTO sang Entity (để Cập nhật)
     */
    private BenhNhan toEntity(BenhNhanDTO dto, BenhNhan entity) {
        if (entity == null) {
            entity = new BenhNhan();
        }
        
        entity.setHoTen(dto.getHoTen());
        entity.setNgaySinh(dto.getNgaySinh());
        entity.setGioiTinh(dto.getGioiTinh());
        entity.setDiaChi(dto.getDiaChi());
        entity.setSoDienThoai(dto.getSoDienThoai());
        entity.setCccd(dto.getCccd());
        entity.setMaBenhNhan(dto.getMaBenhNhan());
        entity.setNhomMau(dto.getNhomMau());
        entity.setTienSuBenh(dto.getTienSuBenh());
        
        return entity;
    }
}