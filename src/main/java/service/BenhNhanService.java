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
import java.util.ArrayList;

/**
 * Lớp Service chứa logic nghiệp vụ cho BenhNhan. (ĐÃ NÂNG CẤP: Thêm Xóa Mềm &
 * Tìm kiếm)
 */
public class BenhNhanService {

    private final BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final KhoaDAO khoaDAO = new KhoaDAO();

    // === THÊM MỚI (ĐỂ GỌI HÀM KHOA_TAIKHOAN) ===
    private final TaiKhoanService taiKhoanService = new TaiKhoanService();
    // === KẾT THÚC THÊM MỚI ===

    // (Các hằng số giữ nguyên)
    private static final String TRANG_THAI_HOAT_DONG = "HOAT_DONG";
    private static final String TRANG_THAI_BI_KHOA = "BI_KHOA";
    private static final Pattern PHONE_REGEX = Pattern.compile("^(0[3|5|7|8|9])+([0-9]{8})$");
    private static final Pattern CCCD_REGEX = Pattern.compile("^(\\d{9}|\\d{12})$");
    private static final Pattern MA_BENHNHAN_REGEX = Pattern.compile("^BN-\\d{5,}$"); // Sửa: Chấp nhận 5 số trở lên
    private static final String NAME_REGEX = "^[\\p{L} .'-]{2,50}$";
    private static final String BLOOD_TYPE_REGEX = "^(?i)(A|B|AB|O)[+-]$";

    /**
     * === SỬA (XÓA MỀM & LOGIC FORM MỚI) ===
     */
    public BenhNhanDTO createBenhNhan(BenhNhanDTO dto) throws ValidationException, Exception {
        validateBenhNhanData(dto, null);
        String maBenhNhan = dto.getMaBenhNhan();
        if (maBenhNhan == null || maBenhNhan.isEmpty()) {
            String newMa = benhNhanDAO.generateNewMaBenhNhan(); // Đã sửa (5 số 0)
            dto.setMaBenhNhan(newMa);
        } else {
            if (!MA_BENHNHAN_REGEX.matcher(maBenhNhan).matches()) {
                throw new ValidationException("Định dạng Mã Bệnh Nhân không hợp lệ. Phải có dạng 'BN-xxxxx'.");
            }
            if (benhNhanDAO.isMaBenhNhanExisted(maBenhNhan)) {
                throw new ValidationException("Mã bệnh nhân '" + maBenhNhan + "' đã tồn tại.");
            }
        }
        // (Đã xóa logic gán TaiKhoanId - Theo yêu cầu)
        TaiKhoan taiKhoanEntity = null;
        Khoa khoaEntity = null;
        BenhNhan entity = new BenhNhan();
        entity = toEntity(dto, entity, taiKhoanEntity, khoaEntity);
        entity.setTrangThai("HOAT_DONG"); // <-- THÊM MỚI
        BenhNhan savedEntity = benhNhanDAO.create(entity);
        return toDTO(savedEntity);
    }

    /**
     * SỬA (XÓA MỀM): Lọc bằng DAO đã nâng cấp
     */
    public BenhNhanDTO updateBenhNhan(int benhNhanId, BenhNhanDTO dto) throws ValidationException, Exception {
        // DAO.getByIdWithRelations() đã lọc HOAT_DONG
        BenhNhan existingEntity = benhNhanDAO.getByIdWithRelations(benhNhanId);
        if (existingEntity == null) {
            throw new ValidationException("Không tìm thấy bệnh nhân với ID: " + benhNhanId);
        }

        validateBenhNhanData(dto, existingEntity);
        String newMaBenhNhan = dto.getMaBenhNhan();
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
                khoaEntity = khoaDAO.getById(newKhoaId); // DAO đã lọc HOAT_DONG
                if (khoaEntity == null) {
                    throw new ValidationException("Không tìm thấy Khoa mới (đang hoạt động) với ID: " + newKhoaId);
                }
            }
        } else {
            khoaEntity = null;
        }
        // (Đã xóa logic gán TaiKhoanId trong Admin Sửa)
        existingEntity = toEntity(dto, existingEntity, taiKhoanEntity, khoaEntity);
        try {
            benhNhanDAO.update(existingEntity);
        } catch (RuntimeException e) {
            throw new Exception("Cập nhật bệnh nhân thất bại: " + e.getMessage(), e);
        }
        BenhNhan updatedEntity = benhNhanDAO.getByIdWithRelations(benhNhanId);
        return toDTO(updatedEntity);
    }

    /**
     * === BẮT ĐẦU SỬA (LOGIC XÓA MỀM) === Dịch vụ "xóa" (vô hiệu hóa) một Bệnh
     * nhân.
     */
    public void softDeleteBenhNhan(int benhNhanId) throws ValidationException, Exception {
        // 1. Lấy bệnh nhân (kể cả đã bị khóa)
        BenhNhan benhNhan = benhNhanDAO.getByIdEvenIfInactive(benhNhanId);
        if (benhNhan == null) {
            throw new ValidationException("Không tìm thấy bệnh nhân với ID: " + benhNhanId + ".");
        }

        // 2. Vô hiệu hóa bản ghi Bệnh Nhân
        if (benhNhan.getTrangThai().equals("HOAT_DONG")) {
            if (!benhNhanDAO.softDelete(benhNhanId)) { // Gọi hàm DAO Xóa Mềm
                throw new Exception("Vô hiệu hóa bệnh nhân thất bại (DAO trả về false).");
            }
        }

        // 3. Khóa tài khoản liên kết (logic cũ của bạn)
        if (benhNhan.getTaiKhoan() != null) {
            TaiKhoan taiKhoan = benhNhan.getTaiKhoan();
            if (taiKhoan.getTrangThai().equals(TRANG_THAI_HOAT_DONG)) {
                // SỬA: Gọi TaiKhoanService để khóa
                taiKhoanService.khoaTaiKhoan(taiKhoan.getId());
            }
        }
    }
    // === KẾT THÚC SỬA (LOGIC XÓA MỀM) ===

    // (Các hàm "Sửa Khó" của bạn - SỬA: Đã lọc Xóa Mềm)
    public void updateSoDienThoai(int benhNhanId, String newPhone) throws ValidationException, Exception {
        BenhNhan existingEntity = benhNhanDAO.getById(benhNhanId); // Đã lọc
        if (existingEntity == null) {
            throw new ValidationException("Không tìm thấy hồ sơ bệnh nhân để cập nhật SĐT.");
        }
        if (newPhone == null || newPhone.trim().isEmpty()) {
            throw new ValidationException("Số điện thoại mới không được để trống.");
        }
        String trimmedPhone = newPhone.trim();
        if (!PHONE_REGEX.matcher(trimmedPhone).matches()) {
            throw new ValidationException("Số điện thoại không hợp lệ (phải là 10 số, bắt đầu bằng 03, 05, 07, 08, 09).");
        }
        BenhNhan patientBySdt = benhNhanDAO.findBySoDienThoai(trimmedPhone); // Đã lọc
        if (patientBySdt != null && patientBySdt.getId() != existingEntity.getId()) {
            throw new ValidationException("Số điện thoại '" + trimmedPhone + "' đã được sử dụng bởi một hồ sơ khác.");
        }
        existingEntity.setSoDienThoai(trimmedPhone);
        try {
            benhNhanDAO.update(existingEntity);
        } catch (RuntimeException e) {
            throw new Exception("Lỗi CSDL khi cập nhật SĐT: " + e.getMessage(), e);
        }
    }

    public void updateCCCD(int benhNhanId, String newCCCD) throws ValidationException, Exception {
        BenhNhan existingEntity = benhNhanDAO.getById(benhNhanId); // Đã lọc
        if (existingEntity == null) {
            throw new ValidationException("Không tìm thấy hồ sơ bệnh nhân để cập nhật CCCD.");
        }
        if (newCCCD == null || newCCCD.trim().isEmpty()) {
            throw new ValidationException("CCCD mới không được để trống.");
        }
        String trimmedCccd = newCCCD.trim();
        if (!CCCD_REGEX.matcher(trimmedCccd).matches()) {
            throw new ValidationException("Định dạng CCCD không hợp lệ. Phải là 9 hoặc 12 chữ số.");
        }
        BenhNhan patientByCccd = benhNhanDAO.findByCccd(trimmedCccd); // Đã lọc
        if (patientByCccd != null && patientByCccd.getId() != existingEntity.getId()) {
            throw new ValidationException("Số CCCD '" + trimmedCccd + "' đã được sử dụng bởi một hồ sơ khác.");
        }
        existingEntity.setCccd(trimmedCccd);
        try {
            benhNhanDAO.update(existingEntity);
        } catch (RuntimeException e) {
            throw new Exception("Lỗi CSDL khi cập nhật CCCD: " + e.getMessage(), e);
        }
    }

    public void updateHoTen(int benhNhanId, String newHoTen) throws ValidationException, Exception {
        BenhNhan existingEntity = benhNhanDAO.getById(benhNhanId); // Đã lọc
        if (existingEntity == null) {
            throw new ValidationException("Không tìm thấy hồ sơ bệnh nhân để cập nhật Họ Tên.");
        }
        if (newHoTen == null || newHoTen.trim().isEmpty()) {
            throw new ValidationException("Họ tên mới không được để trống.");
        }
        String trimmedName = newHoTen.trim();
        if (!Pattern.matches(NAME_REGEX, trimmedName)) {
            throw new ValidationException("Họ tên không hợp lệ (chỉ chứa chữ cái, dấu cách, và dài 2-50 ký tự).");
        }
        existingEntity.setHoTen(trimmedName);
        try {
            benhNhanDAO.update(existingEntity);
        } catch (RuntimeException e) {
            throw new Exception("Lỗi CSDL khi cập nhật Họ Tên: " + e.getMessage(), e);
        }
    }

    public void updateNgaySinh(int benhNhanId, LocalDate newDOB) throws ValidationException, Exception {
        BenhNhan existingEntity = benhNhanDAO.getById(benhNhanId); // Đã lọc
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

    public List<BenhNhanDTO> getAllBenhNhan() {
        List<BenhNhan> entities = benhNhanDAO.getAllWithRelations();
        return entities.stream()
                .filter(bn -> bn.getTaiKhoan() == null || TRANG_THAI_HOAT_DONG.equals(bn.getTaiKhoan().getTrangThai()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // (Các hàm get, find, link... giữ nguyên, DAO đã lọc)
    public BenhNhanDTO getBenhNhanByTaiKhoanId(int taiKhoanId) {
        BenhNhan entity = benhNhanDAO.findByTaiKhoanId(taiKhoanId);
        return toDTO(entity);
    }

    public BenhNhanDTO findByCccd(String cccd) throws ValidationException {
        if (cccd == null || cccd.trim().isEmpty()) {
            throw new ValidationException("CCCD không được để trống.");
        }
        String trimmedCccd = cccd.trim();
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
        return toDTO(entity);
    }

    public BenhNhanDTO getBenhNhanByIdEvenIfInactive(int id) throws ValidationException, Exception {
        BenhNhan entity = benhNhanDAO.getByIdEvenIfInactive(id); // Gọi hàm DAO mới
        if (entity == null) {
            throw new ValidationException("Không tìm thấy bệnh nhân với ID: " + id);
        }
        return toDTO(entity);
    }

    public List<BenhNhanDTO> getAllActiveBenhNhan() {
        List<BenhNhan> entities = benhNhanDAO.getAllActiveWithRelations();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public List<BenhNhanDTO> getAllBenhNhanPaginated(int page, int pageSize) {
        List<BenhNhan> entities = benhNhanDAO.getAllActiveWithRelations(page, pageSize);
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public long getBenhNhanCount() {
        return benhNhanDAO.getTotalActiveBenhNhanCount();
    }

    public List<BenhNhanDTO> getBenhNhanChuaCoGiuong() {
        List<BenhNhan> entities = benhNhanDAO.getBenhNhanChuaCoGiuongWithRelations();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    // === BẮT ĐẦU THÊM MỚI (TÌM KIẾM) ===
    /**
     * HÀM MỚI (TÌM KIẾM): Tìm kiếm Bệnh nhân (có phân trang)
     */
    public List<BenhNhanDTO> searchBenhNhanPaginated(String keyword, int page, int pageSize) {
        List<BenhNhan> entities = benhNhanDAO.searchBenhNhanPaginated(keyword, page, pageSize);
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * HÀM MỚI (TÌM KIẾM): Đếm kết quả tìm kiếm Bệnh nhân
     */
    public long getBenhNhanSearchCount(String keyword) {
        return benhNhanDAO.getBenhNhanSearchCount(keyword);
    }

    /**
     * HÀM MỚI (CHO GIAO DIỆN): "Nhìn trộm" mã bệnh nhân tiếp theo sẽ là gì.
     */
    public String getNextMaBenhNhan() {
        try {
            return benhNhanDAO.generateNewMaBenhNhan();
        } catch (Exception e) {
            e.printStackTrace();
            return "BN-ERROR";
        }
    }
    // === KẾT THÚC THÊM MỚI (TÌM KIẾM) ===

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
            dto.setTenKhoa(entity.getKhoa().getTenKhoa());
        }
        return dto;
    }

    private BenhNhan toEntity(BenhNhanDTO dto, BenhNhan entity, TaiKhoan taiKhoan, Khoa khoa) {
        if (entity == null) {
            entity = new BenhNhan();
        }
        entity.setHoTen(dto.getHoTen());
        entity.setNgaySinh(dto.getNgaySinh());
        entity.setCccd(dto.getCccd());
        entity.setSoDienThoai(dto.getSoDienThoai());
        entity.setGioiTinh(dto.getGioiTinh());
        entity.setDiaChi(dto.getDiaChi());
        entity.setNhomMau(dto.getNhomMau());
        entity.setTienSuBenh(dto.getTienSuBenh());
        if (dto.getMaBenhNhan() != null && !dto.getMaBenhNhan().isEmpty()) {
            entity.setMaBenhNhan(dto.getMaBenhNhan());
        }
        if (entity.getId() == 0) { // Chỉ gán khi Tạo mới
            entity.setTaiKhoan(taiKhoan);
            entity.setKhoa(khoa);
        } else { // Khi Cập nhật
            if (taiKhoan != null || dto.getTaiKhoanId() == null) {
                entity.setTaiKhoan(taiKhoan);
            }
            if (khoa != null || dto.getKhoaId() == null) {
                entity.setKhoa(khoa);
            }
        }
        return entity;
    }

    private void validateBenhNhanData(BenhNhanDTO dto, BenhNhan existingEntity) throws ValidationException {
        if (dto.getHoTen() == null || dto.getHoTen().trim().isEmpty()) {
            throw new ValidationException("Họ tên không được để trống.");
        }
        String trimmedHoTen = dto.getHoTen().trim();
        if (!Pattern.matches(NAME_REGEX, trimmedHoTen)) {
            throw new ValidationException("Họ tên không hợp lệ (chỉ chứa chữ cái, dấu cách, và dài 2-50 ký tự).");
        }
        dto.setHoTen(trimmedHoTen);

        if (dto.getNgaySinh() == null) {
            throw new ValidationException("Ngày sinh không được để trống.");
        }
        if (dto.getNgaySinh() != null && dto.getNgaySinh().isAfter(LocalDate.now())) {
            throw new ValidationException("Ngày sinh không thể là một ngày trong tương lai.");
        }
        if (dto.getGioiTinh() == null || dto.getGioiTinh().trim().isEmpty()) {
            throw new ValidationException("Giới tính không được để trống.");
        }
        dto.setGioiTinh(dto.getGioiTinh().trim());
        if (dto.getDiaChi() == null || dto.getDiaChi().trim().isEmpty()) {
            throw new ValidationException("Địa chỉ không được để trống.");
        }
        dto.setDiaChi(dto.getDiaChi().trim());
        if (dto.getTienSuBenh() != null) {
            dto.setTienSuBenh(dto.getTienSuBenh().trim());
        }

        String cccd = dto.getCccd();
        if (cccd == null || cccd.trim().isEmpty()) {
            throw new ValidationException("CCCD không được để trống.");
        }
        String trimmedCccd = cccd.trim();
        if (!CCCD_REGEX.matcher(trimmedCccd).matches()) {
            throw new ValidationException("Định dạng CCCD không hợp lệ. Phải là 9 hoặc 12 chữ số.");
        }
        dto.setCccd(trimmedCccd);

        String sdt = dto.getSoDienThoai();
        if (sdt == null || sdt.trim().isEmpty()) {
            throw new ValidationException("Số điện thoại không được để trống.");
        }
        String trimmedSdt = sdt.trim();
        if (!PHONE_REGEX.matcher(trimmedSdt).matches()) {
            throw new ValidationException("Định dạng số điện thoại không hợp lệ.");
        }
        dto.setSoDienThoai(trimmedSdt);

        String nhomMau = dto.getNhomMau();
        if (nhomMau != null && !nhomMau.trim().isEmpty()) {
            String trimmedNhomMau = nhomMau.trim().toUpperCase();
            if (!Pattern.matches(BLOOD_TYPE_REGEX, trimmedNhomMau)) {
                throw new ValidationException("Nhóm máu không hợp lệ. (Ví dụ: O+, AB-, B+)");
            }
            dto.setNhomMau(trimmedNhomMau);
        }

        // Kiểm tra trùng lặp (DAO đã lọc 'HOAT_DONG')
        if (existingEntity == null) { // Khi Tạo mới
            if (benhNhanDAO.isCccdExisted(trimmedCccd)) {
                throw new ValidationException("Số CCCD '" + trimmedCccd + "' đã tồn tại trong hệ thống.");
            }
            if (benhNhanDAO.isSoDienThoaiExisted(trimmedSdt)) {
                throw new ValidationException("Số điện thoại '" + trimmedSdt + "' đã tồn tại trong hệ thống.");
            }
        } else { // Khi Cập nhật
            if (!trimmedCccd.equals(existingEntity.getCccd()) && benhNhanDAO.isCccdExisted(trimmedCccd)) {
                throw new ValidationException("Số CCCD này đã được sử dụng bởi một bệnh nhân khác.");
            }
            if (!trimmedSdt.equals(existingEntity.getSoDienThoai()) && benhNhanDAO.isSoDienThoaiExisted(trimmedSdt)) {
                throw new ValidationException("Số điện thoại này đã được sử dụng bởi một bệnh nhân khác.");
            }
        }
    }

}
