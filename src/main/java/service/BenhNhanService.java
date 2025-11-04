package service;

import exception.ValidationException;
import model.Entity.BenhNhan;
import model.Entity.TaiKhoan;
import model.dao.BenhNhanDAO;
import model.dao.TaiKhoanDAO;
import model.dto.BenhNhanDTO;
// (Xóa import TaiKhoanDTO vì không còn dùng hàm createBenhNhanFromTaiKhoan)
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.regex.Pattern;

/**
 * Lớp Service chứa logic nghiệp vụ cho BenhNhan.
 * @author ADMIN 
 * (Đã cập nhật Giai đoạn 2 - Luồng nghiệp vụ mới)
 * (Đã Refactor - Validation tập trung)
 */
public class BenhNhanService {

    private final BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    
    // (Giữ nguyên các Regex)
    private static final Pattern PHONE_REGEX = Pattern.compile("^0[35789]\\d{8}$");
    private static final Pattern CCCD_REGEX = Pattern.compile("^(\\d{9}|\\d{12})$");
    
    // **THÊM MỚI:** Regex cho Mã Bệnh Nhân (dùng cho Admin)
    private static final Pattern MA_BENHNHAN_REGEX = Pattern.compile("^BN-\\d+$");

    /**
     * **ĐÃ CẬP NHẬT (Refactor):**
     * Hàm này giờ gọi hàm validate tập trung (validateBenhNhanData).
     * Chỉ còn xử lý logic nghiệp vụ riêng của 'create' (sinh mã, liên kết tài khoản).
     */
    public BenhNhanDTO createBenhNhan(BenhNhanDTO dto) throws ValidationException, Exception {
        
        // --- BƯỚC 1: GỌI VALIDATION TẬP TRUNG ---
        // Gửi 'null' vì đây là tạo mới, chưa có 'existingEntity'
        validateBenhNhanData(dto, null); 

        // --- BƯỚC 2: XỬ LÝ MÃ BỆNH NHÂN (Tự động hoặc Thủ công) ---
        String maBenhNhan = dto.getMaBenhNhan();
        
        if (maBenhNhan == null || maBenhNhan.trim().isEmpty()) {
            // **Luồng 2: Bệnh nhân tự tạo (saveProfile)**
            // Tự động sinh mã
            String newMa = benhNhanDAO.generateNewMaBenhNhan();
            if (newMa == null) { newMa = "BN-10001"; } // Failsafe
            dto.setMaBenhNhan(newMa);
            
        } else {
            // **Luồng 1: Admin/Lễ tân tạo**
            // Kiểm tra định dạng mã Admin nhập
            if (!MA_BENHNHAN_REGEX.matcher(maBenhNhan).matches()) {
                 throw new ValidationException("Định dạng Mã Bệnh Nhân không hợp lệ. Phải có dạng 'BN-xxxxx'.");
            }
            // Kiểm tra trùng lặp mã Admin nhập
            if (benhNhanDAO.isMaBenhNhanExisted(maBenhNhan)) {
                throw new ValidationException("Mã bệnh nhân '" + maBenhNhan + "' đã tồn tại.");
            }
        }

        // --- BƯỚC 3: KIỂM TRA TÀI KHOẢN LIÊN KẾT (Nếu có) ---
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

        // --- BƯỚC 4: LƯU VÀO CSDL ---
        BenhNhan entity = new BenhNhan();
        entity = toEntity(dto, entity); // Dùng hàm mapper của bạn
        entity.setTaiKhoan(taiKhoanEntity);
        
        BenhNhan savedEntity = benhNhanDAO.create(entity);
        return toDTO(savedEntity);
    }

    /**
     * **ĐÃ CẬP NHẬT (Refactor):**
     * Hàm này giờ gọi hàm validate tập trung (validateBenhNhanData).
     * Đảm bảo các trường bắt buộc không bị xóa rỗng khi update.
     */
    public BenhNhanDTO updateBenhNhan(int benhNhanId, BenhNhanDTO dto) throws ValidationException, Exception {
        BenhNhan existingEntity = benhNhanDAO.getByIdWithRelations(benhNhanId);
        if (existingEntity == null) {
            throw new ValidationException("Không tìm thấy bệnh nhân với ID: " + benhNhanId);
        }
        if (existingEntity.getTaiKhoan() != null && !"HOAT_DONG".equals(existingEntity.getTaiKhoan().getTrangThai())) {
             throw new ValidationException("Không thể cập nhật thông tin cho bệnh nhân có tài khoản bị khóa.");
        }

        // --- BƯỚC 1: GỌI VALIDATION TẬP TRUNG ---
        // Gửi 'existingEntity' để hàm validate biết đây là 'update'
        validateBenhNhanData(dto, existingEntity);

        // --- BƯỚC 2: KIỂM TRA LOGIC UPDATE RIÊNG (Mã Bệnh Nhân) ---
        String newMaBenhNhan = dto.getMaBenhNhan();
        if (newMaBenhNhan != null && !newMaBenhNhan.equals(existingEntity.getMaBenhNhan())) {
            if (benhNhanDAO.isMaBenhNhanExisted(newMaBenhNhan)) {
                throw new ValidationException("Mã bệnh nhân '" + newMaBenhNhan + "' đã tồn tại.");
            }
        }

        // --- BƯỚC 3: MAP DỮ LIỆU MỚI VÀO ENTITY CŨ ---
        existingEntity = toEntity(dto, existingEntity);

        // --- BƯỚC 4: XỬ LÝ CẬP NHẬT TÀI KHOẢN (Logic cũ giữ nguyên) ---
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
            // Cho phép Admin gỡ liên kết tài khoản
            existingEntity.setTaiKhoan(null);
        }

        benhNhanDAO.update(existingEntity); // DAO sẽ ném lỗi nếu thất bại
        
        BenhNhan updatedEntity = benhNhanDAO.getByIdWithRelations(benhNhanId);
        return toDTO(updatedEntity);
    }

    // --- **XÓA 2 HÀM CŨ** (Thuộc luồng "bệnh nhân ảo") ---
    // public BenhNhanDTO createBenhNhanFromTaiKhoan(...) { ... }
    // public BenhNhanDTO updateProfile(...) { ... }

    
    // --- **CÁC HÀM MỚI** (Cho luồng nghiệp vụ mới - Giữ nguyên) ---

    /**
     * (HÀM MỚI - Giai đoạn 2): Tìm BenhNhan DTO bằng TaiKhoan ID
     */
    public BenhNhanDTO getBenhNhanByTaiKhoanId(int taiKhoanId) {
        BenhNhan entity = benhNhanDAO.findByTaiKhoanId(taiKhoanId);
        return toDTO(entity); // Trả về null nếu không tìm thấy
    }

    /**
     * (HÀM MỚI - Giai đoạn 2): Tìm Bệnh Nhân DTO bằng CCCD (Kịch bản A)
     */
    public BenhNhanDTO findByCccd(String cccd) throws ValidationException {
        // Validation cơ bản
        if (cccd == null || cccd.trim().isEmpty()) {
            throw new ValidationException("CCCD không được để trống.");
        }
        if (!CCCD_REGEX.matcher(cccd).matches()) {
            throw new ValidationException("Định dạng CCCD không hợp lệ. Phải là 9 hoặc 12 chữ số.");
        }
        
        BenhNhan entity = benhNhanDAO.findByCccd(cccd);
        return toDTO(entity); // Trả về null nếu không tìm thấy
    }

    /**
     * (HÀM MỚI - Giai đoạn 2): Liên kết một TaiKhoan vào một BenhNhan (Kịch bản A)
     */
    public void linkAccountToPatient(int benhNhanId, int taiKhoanId) throws ValidationException, Exception {
        // 1. Lấy Bệnh nhân
        BenhNhan benhNhanEntity = benhNhanDAO.getById(benhNhanId);
        if (benhNhanEntity == null) {
            throw new ValidationException("Không tìm thấy hồ sơ bệnh nhân (ID: " + benhNhanId + ") để liên kết.");
        }
        
        // 2. Lấy Tài khoản
        TaiKhoan taiKhoanEntity = taiKhoanDAO.getById(taiKhoanId);
        if (taiKhoanEntity == null) {
            throw new ValidationException("Không tìm thấy tài khoản (ID: " + taiKhoanId + ") để liên kết.");
        }
        
        // 3. Kiểm tra xem tài khoản này đã liên kết với ai khác chưa
        if (benhNhanDAO.isTaiKhoanIdLinked(taiKhoanId)) {
             throw new ValidationException("Tài khoản này đã được liên kết với một hồ sơ bệnh nhân khác.");
        }
        
        // 4. Kiểm tra xem bệnh nhân này đã liên kết với tài khoản khác chưa
        if (benhNhanEntity.getTaiKhoan() != null && benhNhanEntity.getTaiKhoan().getId() != taiKhoanId) {
            throw new ValidationException("Hồ sơ bệnh nhân này (CCCD) đã được liên kết với một tài khoản khác.");
        }

        // 5. Liên kết
        benhNhanEntity.setTaiKhoan(taiKhoanEntity);
        
        // 6. Cập nhật
        benhNhanDAO.update(benhNhanEntity); // DAO sẽ ném lỗi nếu thất bại
    }

    // --- CÁC HÀM GET KHÁC (Giữ nguyên) ---

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

    // --- CÁC HÀM MAPPER (Giữ nguyên) ---

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
    
    // =================================================================
    // **** HÀM HELPER VALIDATION MỚI ****
    // =================================================================
    
    /**
     * Hàm validate TẬP TRUNG, được gọi bởi CẢ create và update.
     * Đảm bảo các quy tắc nghiệp vụ (bắt buộc, định dạng, duy nhất)
     * được áp dụng nhất quán.
     * * @param dto Dữ liệu từ form
     * @param existingEntity Entity đang tồn tại (hoặc 'null' nếu là tạo mới)
     */
    private void validateBenhNhanData(BenhNhanDTO dto, BenhNhan existingEntity) throws ValidationException {
        
        // --- BƯỚC 1: VALIDATION CÁC TRƯỜNG BẮT BUỘC (Required) ---
        // Lấy từ hàm createBenhNhan cũ của bạn
        if (dto.getHoTen() == null || dto.getHoTen().trim().isEmpty()) {
            throw new ValidationException("Họ tên không được để trống.");
        }
        if (dto.getNgaySinh() == null) {
            throw new ValidationException("Ngày sinh không được để trống.");
        }
         if (dto.getNgaySinh() != null && dto.getNgaySinh().isAfter(LocalDate.now())) {
            throw new ValidationException("Ngày sinh không thể là một ngày trong tương lai.");
        }
        if (dto.getGioiTinh() == null || dto.getGioiTinh().trim().isEmpty()) {
            throw new ValidationException("Giới tính không được để trống.");
        }
        if (dto.getDiaChi() == null || dto.getDiaChi().trim().isEmpty()) {
            throw new ValidationException("Địa chỉ không được để trống.");
        }

        // --- BƯỚC 2: VALIDATION TRƯỜNG CÓ LOGIC PHỨC TẠP (CCCD, SDT) ---
        
        // --- CCCD ---
        String cccd = dto.getCccd();
        if (cccd == null || cccd.trim().isEmpty()) {
            throw new ValidationException("CCCD không được để trống.");
        }
        if (!CCCD_REGEX.matcher(cccd).matches()) {
            throw new ValidationException("Định dạng CCCD không hợp lệ. Phải là 9 hoặc 12 chữ số.");
        }
        
        // --- SDT ---
        String sdt = dto.getSoDienThoai();
        if (sdt == null || sdt.trim().isEmpty()) {
            throw new ValidationException("Số điện thoại không được để trống.");
        }
        if (!PHONE_REGEX.matcher(sdt).matches()) {
            throw new ValidationException("Định dạng số điện thoại không hợp lệ.");
        }

        // --- BƯỚC 3: VALIDATION TÍNH DUY NHẤT (UNIQUE) ---
        // Sử dụng logic "chỉ kiểm tra nếu thay đổi" từ hàm update cũ của bạn,
        // nhưng áp dụng cho cả 'create' (khi existingEntity == null)
        
        if (existingEntity == null) {
            // === KỊCH BẢN TẠO MỚI ===
            if (benhNhanDAO.isCccdExisted(cccd)) {
                throw new ValidationException("Số CCCD '" + cccd + "' đã tồn tại trong hệ thống.");
            }
            if (benhNhanDAO.isSoDienThoaiExisted(sdt)) {
                throw new ValidationException("Số điện thoại '" + sdt + "' đã tồn tại trong hệ thống.");
            }
        } else {
            // === KỊCH BẢN CẬP NHẬT ===
            // Chỉ check unique nếu giá trị MỚI khác giá trị CŨ
            if (!cccd.equals(existingEntity.getCccd()) && benhNhanDAO.isCccdExisted(cccd)) {
                throw new ValidationException("Số CCCD này đã được sử dụng bởi một bệnh nhân khác.");
            }
            if (!sdt.equals(existingEntity.getSoDienThoai()) && benhNhanDAO.isSoDienThoaiExisted(sdt)) {
                throw new ValidationException("Số điện thoại này đã được sử dụng bởi một bệnh nhân khác.");
            }
        }
    }
}