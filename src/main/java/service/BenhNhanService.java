package service;

import exception.ValidationException;
import model.Entity.BenhNhan;
import model.Entity.TaiKhoan;
import model.Entity.Khoa;
import model.dao.BenhNhanDAO;
import model.dao.KhoaDAO;
import model.dao.TaiKhoanDAO;
import model.dto.BenhNhanDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.regex.Pattern;
import java.util.Collections;

/**
 * Lớp Service chứa logic nghiệp vụ cho BenhNhan.
 *
 * @author ADMIN (Đã Refactor - Validation tập trung & Thêm logic "Sửa Khó") (Đã
 * Clean Code - Thêm .trim() vào validation)
 */
public class BenhNhanService {

    private final BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final KhoaDAO khoaDAO = new KhoaDAO();

    // --- Hằng số (Clean Code) ---
    private static final String TRANG_THAI_HOAT_DONG = "HOAT_DONG";
    private static final String TRANG_THAI_BI_KHOA = "BI_KHOA";

    // (Regex)
    private static final Pattern PHONE_REGEX = Pattern.compile("^(0[3|5|7|8|9])+([0-9]{8})$");
    private static final Pattern CCCD_REGEX = Pattern.compile("^(\\d{9}|\\d{12})$");
    private static final Pattern MA_BENHNHAN_REGEX = Pattern.compile("^BN-\\d+$");
    private static final String NAME_REGEX = "^[\\p{L} .'-]{2,50}$";
    private static final String BLOOD_TYPE_REGEX = "^(?i)(A|B|AB|O)[+-]$";

    /**
     * Tạo một Bệnh Nhân mới. (Đã cập nhật .trim())
     */
    public BenhNhanDTO createBenhNhan(BenhNhanDTO dto) throws ValidationException, Exception {

        validateBenhNhanData(dto, null); // 'null' vì đây là tạo mới

        String maBenhNhan = dto.getMaBenhNhan(); // (đã được trim() bởi validate)
        if (maBenhNhan == null || maBenhNhan.isEmpty()) {
            String newMa = benhNhanDAO.generateNewMaBenhNhan();
            if (newMa == null) {
                newMa = "BN-10001";
            }
            dto.setMaBenhNhan(newMa);
        } else {
            if (!MA_BENHNHAN_REGEX.matcher(maBenhNhan).matches()) {
                throw new ValidationException("Định dạng Mã Bệnh Nhân không hợp lệ. Phải có dạng 'BN-xxxxx'.");
            }
            if (benhNhanDAO.isMaBenhNhanExisted(maBenhNhan)) {
                throw new ValidationException("Mã bệnh nhân '" + maBenhNhan + "' đã tồn tại.");
            }
        }

        TaiKhoan taiKhoanEntity = null;
        if (dto.getTaiKhoanId() != null && dto.getTaiKhoanId() > 0) {
            taiKhoanEntity = taiKhoanDAO.getById(dto.getTaiKhoanId());
            if (taiKhoanEntity == null) {
                throw new ValidationException("Không tìm thấy Tài khoản với ID: " + dto.getTaiKhoanId());
            }
            if (!TRANG_THAI_HOAT_DONG.equals(taiKhoanEntity.getTrangThai())) {
                throw new ValidationException("Không thể gán tài khoản đã bị khóa (ID: " + dto.getTaiKhoanId() + ").");
            }
            if (benhNhanDAO.isTaiKhoanIdLinked(dto.getTaiKhoanId())) {
                throw new ValidationException("Tài khoản này đã được gán cho một bệnh nhân khác.");
            }
        }

        BenhNhan entity = new BenhNhan();
        Khoa khoaEntity = null;
        entity = toEntity(dto, entity, taiKhoanEntity, khoaEntity);

        BenhNhan savedEntity = benhNhanDAO.create(entity);
        return toDTO(savedEntity);
    }

    /**
     * Cập nhật thông tin "Sửa Dễ" (Địa chỉ, Giới tính, v.v.) (Đã cập nhật
     * .trim())
     */
    public BenhNhanDTO updateBenhNhan(int benhNhanId, BenhNhanDTO dto) throws ValidationException, Exception {
        BenhNhan existingEntity = benhNhanDAO.getByIdWithRelations(benhNhanId);
        if (existingEntity == null) {
            throw new ValidationException("Không tìm thấy bệnh nhân với ID: " + benhNhanId);
        }
        if (existingEntity.getTaiKhoan() != null && !TRANG_THAI_HOAT_DONG.equals(existingEntity.getTaiKhoan().getTrangThai())) {
            throw new ValidationException("Không thể cập nhật thông tin cho bệnh nhân có tài khoản bị khóa.");
        }

        validateBenhNhanData(dto, existingEntity);

        String newMaBenhNhan = dto.getMaBenhNhan(); // (đã được trim() bởi validate)
        if (newMaBenhNhan != null && !newMaBenhNhan.equals(existingEntity.getMaBenhNhan())) {
            if (benhNhanDAO.isMaBenhNhanExisted(newMaBenhNhan)) {
                throw new ValidationException("Mã bệnh nhân '" + newMaBenhNhan + "' đã tồn tại.");
            }
        }

        TaiKhoan taiKhoanEntity = existingEntity.getTaiKhoan();
        Khoa khoaEntity = existingEntity.getKhoa();

        Integer newKhoaId = dto.getKhoaId();
        if (newKhoaId != null && newKhoaId > 0) {
            if (existingEntity.getKhoa() == null || existingEntity.getKhoa().getId() != newKhoaId) {
                khoaEntity = khoaDAO.getById(newKhoaId);
                if (khoaEntity == null) {
                    throw new ValidationException("Không tìm thấy Khoa mới với ID: " + newKhoaId);
                }
            }
        } else {
            khoaEntity = null;
        }

        Integer newTaiKhoanId = dto.getTaiKhoanId();
        if (newTaiKhoanId != null && newTaiKhoanId > 0) {
            if (existingEntity.getTaiKhoan() == null || existingEntity.getTaiKhoan().getId() != newTaiKhoanId) {
                taiKhoanEntity = taiKhoanDAO.getById(newTaiKhoanId);
                if (taiKhoanEntity == null) {
                    throw new ValidationException("Không tìm thấy Tài khoản mới với ID: " + newTaiKhoanId);
                }
                if (!TRANG_THAI_HOAT_DONG.equals(taiKhoanEntity.getTrangThai())) {
                    throw new ValidationException("Không thể gán tài khoản mới đã bị khóa.");
                }
                if (benhNhanDAO.isTaiKhoanIdLinked(newTaiKhoanId)) {
                    throw new ValidationException("Tài khoản mới đã được gán cho bệnh nhân khác.");
                }
            }
        } else {
            taiKhoanEntity = null;
        }

        existingEntity = toEntity(dto, existingEntity, taiKhoanEntity, khoaEntity);

        try {
            benhNhanDAO.update(existingEntity);
        } catch (RuntimeException e) {
            throw new Exception("Cập nhật bệnh nhân thất bại: " + e.getMessage(), e);
        }

        BenhNhan updatedEntity = benhNhanDAO.getByIdWithRelations(benhNhanId);
        return toDTO(updatedEntity);
    }

    // =================================================================
    // CÁC HÀM "SỬA KHÓ" (updateSoDienThoai, updateCCCD, v.v...)
    // (Đã cập nhật .trim())
    // =================================================================
    public void updateSoDienThoai(int benhNhanId, String newPhone) throws ValidationException, Exception {
        BenhNhan existingEntity = benhNhanDAO.getById(benhNhanId);
        if (existingEntity == null) {
            throw new ValidationException("Không tìm thấy hồ sơ bệnh nhân để cập nhật SĐT.");
        }

        if (newPhone == null || newPhone.trim().isEmpty()) {
            throw new ValidationException("Số điện thoại mới không được để trống.");
        }
        String trimmedPhone = newPhone.trim(); // Trim
        if (!PHONE_REGEX.matcher(trimmedPhone).matches()) {
            throw new ValidationException("Số điện thoại không hợp lệ (phải là 10 số, bắt đầu bằng 03, 05, 07, 08, 09).");
        }

        BenhNhan patientBySdt = benhNhanDAO.findBySoDienThoai(trimmedPhone);
        if (patientBySdt != null && patientBySdt.getId() != existingEntity.getId()) {
            throw new ValidationException("Số điện thoại '" + trimmedPhone + "' đã được sử dụng bởi một hồ sơ khác.");
        }

        existingEntity.setSoDienThoai(trimmedPhone); // Set data đã trim

        try {
            benhNhanDAO.update(existingEntity);
        } catch (RuntimeException e) {
            throw new Exception("Lỗi CSDL khi cập nhật SĐT: " + e.getMessage(), e);
        }
    }

    public void updateCCCD(int benhNhanId, String newCCCD) throws ValidationException, Exception {
        BenhNhan existingEntity = benhNhanDAO.getById(benhNhanId);
        if (existingEntity == null) {
            throw new ValidationException("Không tìm thấy hồ sơ bệnh nhân để cập nhật CCCD.");
        }

        if (newCCCD == null || newCCCD.trim().isEmpty()) {
            throw new ValidationException("CCCD mới không được để trống.");
        }
        String trimmedCccd = newCCCD.trim(); // Trim
        if (!CCCD_REGEX.matcher(trimmedCccd).matches()) {
            throw new ValidationException("Định dạng CCCD không hợp lệ. Phải là 9 hoặc 12 chữ số.");
        }

        BenhNhan patientByCccd = benhNhanDAO.findByCccd(trimmedCccd);
        if (patientByCccd != null && patientByCccd.getId() != existingEntity.getId()) {
            throw new ValidationException("Số CCCD '" + trimmedCccd + "' đã được sử dụng bởi một hồ sơ khác.");
        }

        existingEntity.setCccd(trimmedCccd); // Set data đã trim

        try {
            benhNhanDAO.update(existingEntity);
        } catch (RuntimeException e) {
            throw new Exception("Lỗi CSDL khi cập nhật CCCD: " + e.getMessage(), e);
        }
    }

    public void updateHoTen(int benhNhanId, String newHoTen) throws ValidationException, Exception {
        BenhNhan existingEntity = benhNhanDAO.getById(benhNhanId);
        if (existingEntity == null) {
            throw new ValidationException("Không tìm thấy hồ sơ bệnh nhân để cập nhật Họ Tên.");
        }

        if (newHoTen == null || newHoTen.trim().isEmpty()) {
            throw new ValidationException("Họ tên mới không được để trống.");
        }
        String trimmedName = newHoTen.trim(); // Trim
        if (!Pattern.matches(NAME_REGEX, trimmedName)) {
            throw new ValidationException("Họ tên không hợp lệ (chỉ chứa chữ cái, dấu cách, và dài 2-50 ký tự).");
        }

        existingEntity.setHoTen(trimmedName); // Set data đã trim

        try {
            benhNhanDAO.update(existingEntity);
        } catch (RuntimeException e) {
            throw new Exception("Lỗi CSDL khi cập nhật Họ Tên: " + e.getMessage(), e);
        }
    }

    public void updateNgaySinh(int benhNhanId, LocalDate newDOB) throws ValidationException, Exception {
        BenhNhan existingEntity = benhNhanDAO.getById(benhNhanId);
        if (existingEntity == null) {
            throw new ValidationException("Không tìm thấy hồ sơ bệnh nhân để cập nhật Ngày sinh.");
        }
        if (newDOB == null) {
            throw new ValidationException("Ngày sinh mới không được để trống.");
        }
        if (newDOB.isAfter(LocalDate.now())) {
            throw new ValidationException("Ngày sinh không thể là một ngày trong tương lai.");
        }
        existingEntity.setNgaySinh(newDOB);
        try {
            benhNhanDAO.update(existingEntity);
        } catch (RuntimeException e) {
            throw new Exception("Lỗi CSDL khi cập nhật Ngày sinh: " + e.getMessage(), e);
        }
    }

    // =================================================================
    // CÁC HÀM GET, FIND, LINK... (Đã cập nhật .trim())
    // =================================================================
    public BenhNhanDTO getBenhNhanByTaiKhoanId(int taiKhoanId) {
        BenhNhan entity = benhNhanDAO.findByTaiKhoanId(taiKhoanId);
        return toDTO(entity);
    }

    public BenhNhanDTO findByCccd(String cccd) throws ValidationException {
        if (cccd == null || cccd.trim().isEmpty()) {
            throw new ValidationException("CCCD không được để trống.");
        }
        String trimmedCccd = cccd.trim(); // Trim
        if (!CCCD_REGEX.matcher(trimmedCccd).matches()) {
            throw new ValidationException("Định dạng CCCD không hợp lệ. Phải là 9 hoặc 12 chữ số.");
        }

        BenhNhan entity = benhNhanDAO.findByCccd(trimmedCccd);
        return toDTO(entity);
    }

    public void linkAccountToPatient(int benhNhanId, int taiKhoanId) throws ValidationException, Exception {
        BenhNhan benhNhanEntity = benhNhanDAO.getById(benhNhanId);
        if (benhNhanEntity == null) {
            throw new ValidationException("Không tìm thấy hồ sơ bệnh nhân (ID: " + benhNhanId + ") để liên kết.");
        }

        TaiKhoan taiKhoanEntity = taiKhoanDAO.getById(taiKhoanId);
        if (taiKhoanEntity == null) {
            throw new ValidationException("Không tìm thấy tài khoản (ID: " + taiKhoanId + ") để liên kết.");
        }

        if (benhNhanDAO.isTaiKhoanIdLinked(taiKhoanId)) {
            throw new ValidationException("Tài khoản này đã được liên kết với một hồ sơ bệnh nhân khác.");
        }

        if (benhNhanEntity.getTaiKhoan() != null && benhNhanEntity.getTaiKhoan().getId() != taiKhoanId) {
            throw new ValidationException("Hồ sơ bệnh nhân này (CCCD) đã được liên kết với một tài khoản khác.");
        }

        benhNhanEntity.setTaiKhoan(taiKhoanEntity);

        try {
            benhNhanDAO.update(benhNhanEntity);
        } catch (RuntimeException e) {
            throw new Exception("Lỗi CSDL khi liên kết tài khoản: " + e.getMessage(), e);
        }
    }

    public BenhNhanDTO getBenhNhanById(int id) throws ValidationException, Exception {
        BenhNhan entity = benhNhanDAO.getByIdWithRelations(id);
        if (entity == null) {
            throw new ValidationException("Không tìm thấy bệnh nhân với ID: " + id);
        }
        if (entity.getTaiKhoan() != null && !TRANG_THAI_HOAT_DONG.equals(entity.getTaiKhoan().getTrangThai())) {
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
                .filter(bn -> bn.getTaiKhoan() == null || TRANG_THAI_HOAT_DONG.equals(bn.getTaiKhoan().getTrangThai()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<BenhNhanDTO> getBenhNhanChuaCoGiuong() {
        List<BenhNhan> entities = benhNhanDAO.getBenhNhanChuaCoGiuongWithRelations();
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // --- CÁC HÀM MAPPER (Đã cập nhật toEntity) ---
    private BenhNhanDTO toDTO(BenhNhan entity) {
        if (entity == null) {
            return null;
        }

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
        if (entity.getKhoa() != null) {
            dto.setKhoaId(entity.getKhoa().getId());
        }
        return dto;
    }

    /**
     * Chuyển DTO sang Entity. (Đã cập nhật .trim() và .toUpperCase())
     */
    private BenhNhan toEntity(BenhNhanDTO dto, BenhNhan entity, TaiKhoan taiKhoan, Khoa khoa) {
        if (entity == null) {
            entity = new BenhNhan();
        }

        // Cập nhật các trường, đã được 'trim' từ validateBenhNhanData
        entity.setHoTen(dto.getHoTen());
        entity.setNgaySinh(dto.getNgaySinh());
        entity.setCccd(dto.getCccd());
        entity.setSoDienThoai(dto.getSoDienThoai());
        entity.setGioiTinh(dto.getGioiTinh());
        entity.setDiaChi(dto.getDiaChi());
        entity.setNhomMau(dto.getNhomMau()); // (Đã được trim/toUpperCase từ validate)
        entity.setTienSuBenh(dto.getTienSuBenh()); // (Đã được trim từ validate)

        // Chỉ cập nhật maBenhNhan nếu DTO có nó
        if (dto.getMaBenhNhan() != null && !dto.getMaBenhNhan().isEmpty()) {
            entity.setMaBenhNhan(dto.getMaBenhNhan());
        }

        // Cập nhật các liên kết
        if (entity.getId() == 0) { // Đang tạo mới
            entity.setTaiKhoan(taiKhoan);
            entity.setKhoa(khoa);
        } else { // Đang cập nhật
            if (taiKhoan != null || dto.getTaiKhoanId() == null) {
                entity.setTaiKhoan(taiKhoan);
            }
            if (khoa != null || dto.getKhoaId() == null) {
                entity.setKhoa(khoa);
            }
        }

        return entity;
    }

    // =================================================================
    // **** HÀM HELPER VALIDATION (ĐÃ CẬP NHẬT .trim()) ****
    // =================================================================
    private void validateBenhNhanData(BenhNhanDTO dto, BenhNhan existingEntity) throws ValidationException {

        // --- BƯỚC 1: VALIDATION CÁC TRƯỜNG BẮT BUỘC (Required) ---
        if (dto.getHoTen() == null || dto.getHoTen().trim().isEmpty()) {
            throw new ValidationException("Họ tên không được để trống.");
        }
        String trimmedHoTen = dto.getHoTen().trim();
        if (!Pattern.matches(NAME_REGEX, trimmedHoTen)) {
            throw new ValidationException("Họ tên không hợp lệ (chỉ chứa chữ cái, dấu cách, và dài 2-50 ký tự).");
        }
        dto.setHoTen(trimmedHoTen); // Cập nhật DTO

        if (dto.getNgaySinh() == null) {
            throw new ValidationException("Ngày sinh không được để trống.");
        }
        if (dto.getNgaySinh() != null && dto.getNgaySinh().isAfter(LocalDate.now())) {
            throw new ValidationException("Ngày sinh không thể là một ngày trong tương lai.");
        }

        if (dto.getGioiTinh() == null || dto.getGioiTinh().trim().isEmpty()) {
            throw new ValidationException("Giới tính không được để trống.");
        }
        dto.setGioiTinh(dto.getGioiTinh().trim()); // Cập nhật DTO

        if (dto.getDiaChi() == null || dto.getDiaChi().trim().isEmpty()) {
            throw new ValidationException("Địa chỉ không được để trống.");
        }
        dto.setDiaChi(dto.getDiaChi().trim()); // Cập nhật DTO

        // Cập nhật trường tùy chọn (để toEntity không cần trim)
        if (dto.getTienSuBenh() != null) {
            dto.setTienSuBenh(dto.getTienSuBenh().trim());
        }

        // --- BƯỚC 2: VALIDATION TRƯỜNG CÓ LOGIC PHỨC TẠP (CCCD, SDT) ---
        String cccd = dto.getCccd();
        if (cccd == null || cccd.trim().isEmpty()) {
            throw new ValidationException("CCCD không được để trống.");
        }
        String trimmedCccd = cccd.trim();
        if (!CCCD_REGEX.matcher(trimmedCccd).matches()) {
            throw new ValidationException("Định dạng CCCD không hợp lệ. Phải là 9 hoặc 12 chữ số.");
        }
        dto.setCccd(trimmedCccd); // Cập nhật DTO

        String sdt = dto.getSoDienThoai();
        if (sdt == null || sdt.trim().isEmpty()) {
            throw new ValidationException("Số điện thoại không được để trống.");
        }
        String trimmedSdt = sdt.trim();
        if (!PHONE_REGEX.matcher(trimmedSdt).matches()) {
            throw new ValidationException("Định dạng số điện thoại không hợp lệ.");
        }
        dto.setSoDienThoai(trimmedSdt); // Cập nhật DTO

        // --- BƯỚC 3: VALIDATION TRƯỜNG TÙY CHỌN (NHÓM MÁU) ---
        String nhomMau = dto.getNhomMau();
        if (nhomMau != null && !nhomMau.trim().isEmpty()) {
            String trimmedNhomMau = nhomMau.trim().toUpperCase(); // Trim và chuẩn hóa
            if (!Pattern.matches(BLOOD_TYPE_REGEX, trimmedNhomMau)) {
                throw new ValidationException("Nhóm máu không hợp lệ. (Ví dụ: O+, AB-, B+)");
            }
            dto.setNhomMau(trimmedNhomMau); // Cập nhật DTO
        }

        // --- BƯỚC 4: VALIDATION TÍNH DUY NHẤT (UNIQUE) ---
        if (existingEntity == null) {
            // === KỊCH BẢN TẠO MỚI ===
            if (benhNhanDAO.isCccdExisted(trimmedCccd)) {
                throw new ValidationException("Số CCCD '" + trimmedCccd + "' đã tồn tại trong hệ thống.");
            }
            if (benhNhanDAO.isSoDienThoaiExisted(trimmedSdt)) {
                throw new ValidationException("Số điện thoại '" + trimmedSdt + "' đã tồn tại trong hệ thống.");
            }
        } else {
            // === KỊCH BẢN CẬP NHẬT ===
            if (!trimmedCccd.equals(existingEntity.getCccd()) && benhNhanDAO.isCccdExisted(trimmedCccd)) {
                throw new ValidationException("Số CCCD này đã được sử dụng bởi một bệnh nhân khác.");
            }
            if (!trimmedSdt.equals(existingEntity.getSoDienThoai()) && benhNhanDAO.isSoDienThoaiExisted(trimmedSdt)) {
                throw new ValidationException("Số điện thoại này đã được sử dụng bởi một bệnh nhân khác.");
            }
        }
    }
}
