package service;

import exception.ValidationException;
import model.Entity.Khoa;
import model.Entity.NhanVien;
import model.Entity.TaiKhoan;
import model.dao.KhoaDAO;
import model.dao.NhanVienDAO;
import model.dao.TaiKhoanDAO;
import model.dto.NhanVienDTO;
import java.time.LocalDateTime;
import java.util.List; // THÊM IMPORT
import java.util.ArrayList; // THÊM IMPORT
import java.util.Collections; // THÊM IMPORT
import java.util.regex.Pattern;
import java.util.stream.Collectors; // THÊM IMPORT

/**
 * Lớp Service chứa logic nghiệp vụ cho NhanVien.
 *
 * @author ADMIN (Đã CẬP NHẬT: Hỗ trợ Phân trang và 'làm phẳng' DTO)
 */
public class NhanVienService {

    // (Các DAO và Hằng số... giữ nguyên)
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final KhoaDAO khoaDAO = new KhoaDAO();
    private static final String PHONE_NUMBER_REGEX = "^(0[3|5|7|8|9])+([0-9]{8})$";
    private static final String NAME_REGEX = "^[\\p{L} .'-]{2,50}$";
    private static final String TRANG_THAI_HOAT_DONG = "HOAT_DONG";
    private static final String TRANG_THAI_BI_KHOA = "BI_KHOA";

    // (Hàm createNhanVien giữ nguyên)
    public NhanVienDTO createNhanVien(NhanVienDTO dto) throws ValidationException, Exception {
        validateNhanVienData(dto, null);

        if (dto.getTaiKhoanId() <= 0) {
            throw new ValidationException("ID Tài khoản không hợp lệ. Phải gán một tài khoản.");
        }

        TaiKhoan taiKhoanEntity = taiKhoanDAO.getById(dto.getTaiKhoanId());
        if (taiKhoanEntity == null) {
            throw new ValidationException("Không tìm thấy Tài khoản với ID: " + dto.getTaiKhoanId());
        }

        if (!TRANG_THAI_HOAT_DONG.equals(taiKhoanEntity.getTrangThai())) {
            throw new ValidationException("Không thể gán tài khoản đã bị khóa.");
        }

        if (nhanVienDAO.isTaiKhoanIdLinked(dto.getTaiKhoanId())) {
            throw new ValidationException("Tài khoản này đã được gán cho một nhân viên khác.");
        }

        Khoa khoaEntity = null;
        if (dto.getKhoaId() != null && dto.getKhoaId() > 0) {
            khoaEntity = khoaDAO.getById(dto.getKhoaId());
            if (khoaEntity == null) {
                throw new ValidationException("Không tìm thấy Khoa với ID: " + dto.getKhoaId());
            }
        }

        NhanVien entity = toEntity(dto, taiKhoanEntity, khoaEntity);
        NhanVien savedEntity = nhanVienDAO.create(entity);

        if (savedEntity != null) {
            NhanVien fullSavedEntity = nhanVienDAO.getByIdWithRelations(savedEntity.getId());
            return toDTO(fullSavedEntity);
        }
        return null;
    }

    // (Hàm updateNhanVien giữ nguyên)
    public NhanVienDTO updateNhanVien(int nhanVienId, NhanVienDTO dto) throws ValidationException, Exception {
        NhanVien existingEntity = nhanVienDAO.getByIdWithRelations(nhanVienId);
        if (existingEntity == null) {
            throw new ValidationException("Không tìm thấy nhân viên với ID: " + nhanVienId);
        }

        if (existingEntity.getTaiKhoan() == null
                || !TRANG_THAI_HOAT_DONG.equals(existingEntity.getTaiKhoan().getTrangThai())) {
            throw new ValidationException("Không thể cập nhật thông tin cho nhân viên có tài khoản bị khóa hoặc không tồn tại.");
        }

        validateNhanVienData(dto, existingEntity);

        existingEntity.setHoTen(dto.getHoTen());
        existingEntity.setNgaySinh(dto.getNgaySinh());
        existingEntity.setGioiTinh(dto.getGioiTinh());
        existingEntity.setDiaChi(dto.getDiaChi());
        existingEntity.setSoDienThoai(dto.getSoDienThoai());
        existingEntity.setChuyenMon(dto.getChuyenMon());
        existingEntity.setBangCap(dto.getBangCap());

        Integer newKhoaId = dto.getKhoaId();
        if (newKhoaId != null && newKhoaId > 0) {
            if (existingEntity.getKhoa() == null || existingEntity.getKhoa().getId() != newKhoaId) {
                Khoa khoaEntity = khoaDAO.getById(newKhoaId);
                if (khoaEntity == null) {
                    throw new ValidationException("Không tìm thấy Khoa mới với ID: " + newKhoaId);
                }
                existingEntity.setKhoa(khoaEntity);
            }
        } else {
            existingEntity.setKhoa(null);
        }

        try {
            nhanVienDAO.update(existingEntity);
        } catch (RuntimeException e) {
            throw new Exception("Cập nhật nhân viên thất bại do lỗi CSDL: " + e.getMessage(), e);
        }

        NhanVien updatedEntity = nhanVienDAO.getByIdWithRelations(nhanVienId);
        return toDTO(updatedEntity);
    }

    // (Hàm softDeleteNhanVien giữ nguyên)
    public void softDeleteNhanVien(int nhanVienId) throws ValidationException, Exception {
        NhanVien nhanVien = nhanVienDAO.getByIdWithRelations(nhanVienId);
        if (nhanVien == null) {
            throw new ValidationException("Không tìm thấy nhân viên với ID: " + nhanVienId + " để xóa.");
        }

        TaiKhoan taiKhoan = nhanVien.getTaiKhoan();
        if (taiKhoan == null) {
            throw new ValidationException("Nhân viên (ID: " + nhanVienId + ") không có tài khoản liên kết.");
        }

        if (!TRANG_THAI_BI_KHOA.equals(taiKhoan.getTrangThai())) {
            taiKhoan.setTrangThai(TRANG_THAI_BI_KHOA);
            taiKhoan.setUpdatedAt(LocalDateTime.now());
            try {
                taiKhoanDAO.update(taiKhoan);
            } catch (RuntimeException e) {
                throw new Exception("Không thể cập nhật trạng thái tài khoản cho nhân viên ID: " + nhanVienId, e);
            }
        }
    }

    // (Hàm getNhanVienById giữ nguyên)
    public NhanVienDTO getNhanVienById(int id) throws ValidationException {
        NhanVien entity = nhanVienDAO.getByIdWithRelations(id);
        if (entity == null || entity.getTaiKhoan() == null
                || !TRANG_THAI_HOAT_DONG.equals(entity.getTaiKhoan().getTrangThai())) {
            throw new ValidationException("Không tìm thấy nhân viên đang hoạt động với ID: " + id);
        }
        return toDTO(entity);
    }

    /**
     * === BẮT ĐẦU SỬA (PHÂN TRANG) ===
     */
    public List<NhanVienDTO> getAllNhanVienPaginated(int page, int pageSize) {
        List<NhanVien> entities = nhanVienDAO.getAllWithRelations(page, pageSize);
        return entities.stream()
                .filter(nv -> nv.getTaiKhoan() != null && TRANG_THAI_HOAT_DONG.equals(nv.getTaiKhoan().getTrangThai()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public long getNhanVienCount() {
        return nhanVienDAO.getTotalNhanVienCount();
    }

    /**
     * Dịch vụ tìm tất cả bác sĩ đang hoạt động.
     */
    public List<NhanVienDTO> findDoctorsBySpecialty() {
        List<NhanVien> entities = nhanVienDAO.findDoctorsBySpecialty();
        if (entities == null) {
            return Collections.emptyList();
        }
        return entities.stream()
                .filter(nv -> nv.getTaiKhoan() != null && TRANG_THAI_HOAT_DONG.equals(nv.getTaiKhoan().getTrangThai()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * === BẮT ĐẦU SỬA (HIỂN THỊ TÊN) ===
     */
    private NhanVienDTO toDTO(NhanVien entity) {
        if (entity == null) {
            return null;
        }

        NhanVienDTO dto = new NhanVienDTO();
        dto.setId(entity.getId());
        dto.setHoTen(entity.getHoTen());
        dto.setNgaySinh(entity.getNgaySinh());
        dto.setGioiTinh(entity.getGioiTinh());
        dto.setDiaChi(entity.getDiaChi());
        dto.setSoDienThoai(entity.getSoDienThoai());
        dto.setChuyenMon(entity.getChuyenMon());
        dto.setBangCap(entity.getBangCap());

        if (entity.getTaiKhoan() != null) {
            dto.setTaiKhoanId(entity.getTaiKhoan().getId());
        }

        if (entity.getKhoa() != null) {
            dto.setKhoaId(entity.getKhoa().getId());
            dto.setTenKhoa(entity.getKhoa().getTenKhoa());
        }

        return dto;
    }

    private NhanVien toEntity(NhanVienDTO dto, TaiKhoan taiKhoan, Khoa khoa) {
        NhanVien entity = new NhanVien();
        entity.setHoTen(dto.getHoTen());
        entity.setNgaySinh(dto.getNgaySinh());
        entity.setGioiTinh(dto.getGioiTinh());
        entity.setDiaChi(dto.getDiaChi());
        entity.setSoDienThoai(dto.getSoDienThoai());
        entity.setChuyenMon(dto.getChuyenMon());
        entity.setBangCap(dto.getBangCap());
        entity.setTaiKhoan(taiKhoan);
        entity.setKhoa(khoa);
        return entity;
    }

    private void validateNhanVienData(NhanVienDTO dto, NhanVien existingEntity) throws ValidationException {
        if (dto.getHoTen() == null || dto.getHoTen().trim().isEmpty()) {
            throw new ValidationException("Họ tên nhân viên không được để trống.");
        }

        if (!Pattern.matches(NAME_REGEX, dto.getHoTen())) {
            throw new ValidationException("Họ tên không hợp lệ (chỉ chứa chữ cái, dấu cách, và dài 2-50 ký tự).");
        }

        String newSdt = dto.getSoDienThoai();
        if (newSdt != null && !newSdt.trim().isEmpty()) {
            if (!Pattern.matches(PHONE_NUMBER_REGEX, newSdt)) {
                throw new ValidationException("Số điện thoại không hợp lệ (phải là 10 số, bắt đầu bằng 03, 05, 07, 08, 09).");
            }

            boolean isCreating = (existingEntity == null);
            boolean isUpdatingAndChanged = (!isCreating && !newSdt.equals(existingEntity.getSoDienThoai()));

            if ((isCreating || isUpdatingAndChanged) && nhanVienDAO.isSoDienThoaiExisted(newSdt)) {
                throw new ValidationException("Số điện thoại '" + newSdt + "' đã tồn tại.");
            }
        }
    }

    public NhanVienDTO getNhanVienByTaiKhoanId(int taiKhoanId) {
        NhanVien entity = nhanVienDAO.findByTaiKhoanId(taiKhoanId);
        return toDTOFotGetByTaiKhoan(entity);
    }

    private NhanVienDTO toDTOFotGetByTaiKhoan(NhanVien entity) {
        if (entity == null) {
            return null;
        }

        NhanVienDTO dto = new NhanVienDTO();
        dto.setId(entity.getId());
        dto.setHoTen(entity.getHoTen());
        dto.setChuyenMon(entity.getChuyenMon());
        if (entity.getTaiKhoan() != null) {
            dto.setTaiKhoanId(entity.getTaiKhoan().getId());
            dto.setVaiTro(entity.getTaiKhoan().getVaiTro());
        }
        return dto;
    }

    /**
     * HÀM MỚI: Tìm kiếm nhân viên (đã phân trang)
     */
    public List<NhanVienDTO> searchNhanVienPaginated(String keyword, int page, int pageSize) {
        List<NhanVien> entities = nhanVienDAO.searchNhanVienPaginated(keyword, page, pageSize);
        return entities.stream()
                .map(this::toDTO) // Dùng lại hàm toDTO đã sửa (có tenKhoa)
                .collect(Collectors.toList());
    }

    /**
     * HÀM MỚI: Đếm kết quả tìm kiếm nhân viên
     */
    public long getNhanVienSearchCount(String keyword) {
        return nhanVienDAO.getNhanVienSearchCount(keyword);
    }
}
