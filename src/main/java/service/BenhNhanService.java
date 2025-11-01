package service;

import exception.ValidationException;
import model.Entity.BenhNhan;
import model.Entity.TaiKhoan;
import model.dao.BenhNhanDAO;
import model.dao.TaiKhoanDAO;
import model.dto.BenhNhanDTO;
import model.dto.TaiKhoanDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

/**
 * Lớp Service chứa logic nghiệp vụ cho BenhNhan.
 * @author ADMIN (Đã cập nhật logic unique check)
 */
public class BenhNhanService {

    private final BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    
    private static final Pattern PHONE_REGEX = Pattern.compile("^0[35789]\\d{8}$");
    private static final Pattern CCCD_REGEX = Pattern.compile("^(\\d{9}|\\d{12})$");
    private static final Pattern MA_BENHNHAN_REGEX = Pattern.compile("^BN-\\d+$");

    /**
     * Dịch vụ tạo một Bệnh Nhân mới.
     * **ĐÃ CẬP NHẬT:** Thêm kiểm tra unique cho CCCD và SĐT.
     */
    public BenhNhanDTO createBenhNhan(BenhNhanDTO dto) throws ValidationException, Exception {
        if (dto.getHoTen() == null || dto.getHoTen().trim().isEmpty()) {
            throw new ValidationException("Họ tên bệnh nhân không được để trống.");
        }

        String maBenhNhan = dto.getMaBenhNhan();
        
        if (maBenhNhan == null || maBenhNhan.trim().isEmpty()) {
            // Luồng Bệnh nhân tự đăng ký (tự động tạo mã)
            String newMa = benhNhanDAO.generateNewMaBenhNhan();
            if (newMa == null) {
                newMa = "BN-10001";
            }
            dto.setMaBenhNhan(newMa);
            
        } else {
            // Luồng Admin/Lễ tân tạo (phải nhập mã)
            if (!MA_BENHNHAN_REGEX.matcher(maBenhNhan).matches()) {
                throw new ValidationException("Định dạng Mã Bệnh Nhân không hợp lệ. Phải có dạng 'BN-xxxxx' (ví dụ: BN-10001).");
            }
            if (benhNhanDAO.isMaBenhNhanExisted(maBenhNhan)) {
                throw new ValidationException("Mã bệnh nhân '" + maBenhNhan + "' đã tồn tại.");
            }
        }
        
        // --- **BẮT ĐẦU CẬP NHẬT (KIỂM TRA UNIQUE)** ---
        if (dto.getSoDienThoai() != null && !dto.getSoDienThoai().trim().isEmpty()) {
            if (!PHONE_REGEX.matcher(dto.getSoDienThoai()).matches()) {
                throw new ValidationException("Định dạng số điện thoại không hợp lệ. Phải là 10 số (ví dụ: 0912345678).");
            }
            if (benhNhanDAO.isSoDienThoaiExisted(dto.getSoDienThoai())) {
                throw new ValidationException("Số điện thoại này đã được sử dụng bởi một bệnh nhân khác.");
            }
        }
        if (dto.getCccd() != null && !dto.getCccd().trim().isEmpty()) {
            if (!CCCD_REGEX.matcher(dto.getCccd()).matches()) {
                throw new ValidationException("Định dạng CCCD không hợp lệ. Phải là 9 hoặc 12 chữ số.");
            }
            if (benhNhanDAO.isCccdExisted(dto.getCccd())) {
                throw new ValidationException("Số CCCD này đã được sử dụng bởi một bệnh nhân khác.");
            }
        }
        // --- **KẾT THÚC CẬP NHẬT** ---

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
        if (savedEntity == null) {
            throw new Exception("Tạo bệnh nhân thất bại (DAO trả về null).");
        }
        return toDTO(savedEntity);
    }

    /**
     * Dịch vụ cập nhật thông tin Bệnh Nhân. (Admin/Lễ tân dùng)
     * **ĐÃ CẬP NHẬT:** Thêm kiểm tra unique cho CCCD và SĐT.
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
        if (newMaBenhNhan == null || newMaBenhNhan.trim().isEmpty()) {
             throw new ValidationException("Mã bệnh nhân không được để trống khi cập nhật.");
        }
        if (!MA_BENHNHAN_REGEX.matcher(newMaBenhNhan).matches()) {
            throw new ValidationException("Định dạng Mã Bệnh Nhân không hợp lệ. Phải có dạng 'BN-xxxxx'.");
        }
        if (!newMaBenhNhan.equals(existingEntity.getMaBenhNhan())) {
            if (benhNhanDAO.isMaBenhNhanExisted(newMaBenhNhan)) {
                throw new ValidationException("Mã bệnh nhân '" + newMaBenhNhan + "' đã tồn tại.");
            }
        }

        // --- **BẮT ĐẦU CẬP NHẬT (KIỂM TRA UNIQUE)** ---
        if (dto.getSoDienThoai() != null && !dto.getSoDienThoai().trim().isEmpty()) {
            if (!PHONE_REGEX.matcher(dto.getSoDienThoai()).matches()) {
                throw new ValidationException("Định dạng số điện thoại không hợp lệ.");
            }
            // Chỉ kiểm tra SĐT nếu nó thay đổi
            if (!dto.getSoDienThoai().equals(existingEntity.getSoDienThoai()) && benhNhanDAO.isSoDienThoaiExisted(dto.getSoDienThoai())) {
                throw new ValidationException("Số điện thoại này đã được sử dụng bởi một bệnh nhân khác.");
            }
        }
        if (dto.getCccd() != null && !dto.getCccd().trim().isEmpty()) {
            if (!CCCD_REGEX.matcher(dto.getCccd()).matches()) {
                throw new ValidationException("Định dạng CCCD không hợp lệ.");
            }
            // Chỉ kiểm tra CCCD nếu nó thay đổi
            if (!dto.getCccd().equals(existingEntity.getCccd()) && benhNhanDAO.isCccdExisted(dto.getCccd())) {
                throw new ValidationException("Số CCCD này đã được sử dụng bởi một bệnh nhân khác.");
            }
        }
        // --- **KẾT THÚC CẬP NHẬT** ---

        existingEntity = toEntity(dto, existingEntity);

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
            throw new Exception("Cập nhật bệnh nhân thất bại.");
        }
        BenhNhan updatedEntity = benhNhanDAO.getByIdWithRelations(benhNhanId);
        return toDTO(updatedEntity);
    }
    
    /**
     * Cập nhật hồ sơ cá nhân (sau khi đăng ký).
     * **ĐÃ CẬP NHẬT:** Thêm kiểm tra unique cho CCCD và SĐT.
     */
    public BenhNhanDTO updateProfile(int benhNhanId, BenhNhanDTO dto) throws ValidationException, Exception {
        
        BenhNhan existingBenhNhan = benhNhanDAO.getByIdWithRelations(benhNhanId);
        
        if (existingBenhNhan == null) {
            throw new ValidationException("Không tìm thấy hồ sơ bệnh nhân (ID: " + benhNhanId + ") để cập nhật.");
        }

        // --- VALIDATION (Các trường bắt buộc) ---
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
        // --- **CẬP NHẬT: Chỉ kiểm tra CCCD nếu nó thay đổi** ---
        if (!cccd.equals(existingBenhNhan.getCccd()) && benhNhanDAO.isCccdExisted(cccd)) {
             throw new ValidationException("Số CCCD này đã tồn tại.");
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
            throw new ValidationException("Định dạng số điện thoại không hợp lệ.");
        }
        // --- **CẬP NHẬT: Chỉ kiểm tra SĐT nếu nó thay đổi** ---
        if (!sdt.equals(existingBenhNhan.getSoDienThoai()) && benhNhanDAO.isSoDienThoaiExisted(sdt)) {
            throw new ValidationException("Số điện thoại này đã tồn tại.");
        }
        
        if (dto.getDiaChi() == null || dto.getDiaChi().trim().isEmpty()) { 
            throw new ValidationException("Địa chỉ không được để trống."); 
        }
        
        // --- **SỬA LỖI LẦN TRƯỚC (Map thủ công)** ---
        existingBenhNhan.setHoTen(dto.getHoTen());
        existingBenhNhan.setCccd(dto.getCccd());
        existingBenhNhan.setNgaySinh(dto.getNgaySinh());
        existingBenhNhan.setGioiTinh(dto.getGioiTinh());
        existingBenhNhan.setSoDienThoai(dto.getSoDienThoai());
        existingBenhNhan.setDiaChi(dto.getDiaChi());
        existingBenhNhan.setNhomMau(dto.getNhomMau());
        existingBenhNhan.setTienSuBenh(dto.getTienSuBenh());
        // (Không map maBenhNhan)

        // Cập nhật CSDL
        boolean success = benhNhanDAO.update(existingBenhNhan);
        if (!success) {
            throw new Exception("Cập nhật hồ sơ thất bại do lỗi hệ thống CSDL.");
        }
        return toDTO(existingBenhNhan);
    }

    /**
     * Tìm BenhNhan DTO bằng TaiKhoan ID
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