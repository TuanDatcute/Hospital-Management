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
import java.util.List;
import java.util.ArrayList; // Giữ lại import này cho các hàm phân trang
import java.util.Collections;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Lớp Service chứa logic nghiệp vụ cho NhanVien. (ĐÃ GỘP LẠI)
 *
 * - Giữ lại Phân trang, Tìm kiếm, và 'tenKhoa' trong toDTO từ phiên bản 2. -
 * Giữ lại khối code 'Dat' từ phiên bản 1. - Thêm lại hàm getAllNhanVien()
 * (không phân trang) từ phiên bản 1.
 */
public class NhanVienService {

    // (Các DAO và Hằng số)
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final KhoaDAO khoaDAO = new KhoaDAO();

    private static final String PHONE_NUMBER_REGEX = "^(0[3|5|7|8|9])+([0-9]{8})$";
    // **KẾT THÚC SỬA**

    private static final String NAME_REGEX = "^[\\p{L} .'-]{2,50}$";
    private static final String TRANG_THAI_HOAT_DONG = "HOAT_DONG";
    private static final String TRANG_THAI_BI_KHOA = "BI_KHOA";
    // --- KẾT THÚC THÊM MỚI ---

    /**
     * Dịch vụ tạo một Nhân Viên mới. (Sử dụng logic từ phiên bản 2 - Lấy lại
     * entity đầy đủ)
     */
    public NhanVienDTO createNhanVien(NhanVienDTO dto) throws ValidationException, Exception {
        // --- BƯỚC 1: VALIDATION ---
        validateNhanVienData(dto, null);

        // Validation riêng cho 'create' (Kiểm tra Tài khoản)
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
            // SỬA: Lấy lại entity đầy đủ để toDTO (từ phiên bản 2)
            NhanVien fullSavedEntity = nhanVienDAO.getByIdWithRelations(savedEntity.getId());
            return toDTO(fullSavedEntity);
        }
        return null;
    }

    /**
     * Dịch vụ cập nhật thông tin Nhân Viên. (Sử dụng logic từ phiên bản 2)
     */
    public NhanVienDTO updateNhanVien(int nhanVienId, NhanVienDTO dto) throws ValidationException, Exception {
        NhanVien existingEntity = nhanVienDAO.getByIdWithRelations(nhanVienId);
        if (existingEntity == null) {
            throw new ValidationException("Không tìm thấy nhân viên với ID: " + nhanVienId);
        }

        if (existingEntity.getTaiKhoan() == null
                || !TRANG_THAI_HOAT_DONG.equals(existingEntity.getTaiKhoan().getTrangThai())) {
            throw new ValidationException("Không thể cập nhật thông tin cho nhân viên có tài khoản bị khóa hoặc không tồn tại.");
        }

        // --- BƯỚC 2: VALIDATION ---
        // Gọi hàm helper, truyền 'existingEntity' để check logic update
        validateNhanVienData(dto, existingEntity);

        // (Cập nhật các trường...)
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

        // Tải lại để chắc chắn lấy dữ liệu mới nhất
        NhanVien updatedEntity = nhanVienDAO.getByIdWithRelations(nhanVienId);
        return toDTO(updatedEntity);
    }

    /**
     * Dịch vụ thực hiện Soft Delete cho Nhân Viên. (Giữ logic từ phiên bản 2 -
     * giống hệt phiên bản 1)
     */
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

    /**
     * Lấy nhân viên bằng ID (chỉ trả về nếu tài khoản đang hoạt động). (Giữ
     * logic từ phiên bản 2 - giống hệt phiên bản 1)
     */
    public NhanVienDTO getNhanVienById(int id) throws ValidationException {
        NhanVien entity = nhanVienDAO.getByIdWithRelations(id);
        if (entity == null || entity.getTaiKhoan() == null
                || !TRANG_THAI_HOAT_DONG.equals(entity.getTaiKhoan().getTrangThai())) {
            throw new ValidationException("Không tìm thấy nhân viên đang hoạt động với ID: " + id);
        }
        return toDTO(entity);
    }

    /**
     * Lấy tất cả nhân viên đang hoạt động. (ĐƯỢC THÊM LẠI TỪ PHIÊN BẢN 1)
     */
    public List<NhanVienDTO> getAllNhanVien() {
        List<NhanVien> entities = nhanVienDAO.getAllWithRelations();

        return entities.stream()
                .filter(nv -> nv.getTaiKhoan() != null && TRANG_THAI_HOAT_DONG.equals(nv.getTaiKhoan().getTrangThai()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    // === CÁC HÀM TỪ PHIÊN BẢN 2 (PHÂN TRANG & TÌM KIẾM) ===
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

    public List<NhanVienDTO> searchNhanVienPaginated(String keyword, int page, int pageSize) {
        List<NhanVien> entities = nhanVienDAO.searchNhanVienPaginated(keyword, page, pageSize);
        return entities.stream()
                .map(this::toDTO) // Dùng lại hàm toDTO đã sửa (có tenKhoa)
                .collect(Collectors.toList());
    }

    public long getNhanVienSearchCount(String keyword) {
        return nhanVienDAO.getNhanVienSearchCount(keyword);
    }
    // === KẾT THÚC HÀM TỪ PHIÊN BẢN 2 ===

    /**
     * Dịch vụ tìm tất cả bác sĩ đang hoạt động. (Giữ logic từ phiên bản 2 -
     * giống hệt phiên bản 1)
     */
    public List<NhanVienDTO> findDoctorsBySpecialty() {
        List<NhanVien> entities = nhanVienDAO.findDoctorsBySpecialty();
        if (entities == null) {
            return Collections.emptyList();
        }
        // Lọc thêm trạng thái HOAT_DONG
        return entities.stream()
                .filter(nv -> nv.getTaiKhoan() != null && TRANG_THAI_HOAT_DONG.equals(nv.getTaiKhoan().getTrangThai()))
                .map(this::toDTODat)
                .collect(Collectors.toList());
    }

    private NhanVienDTO toDTODat(NhanVien entity) {
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
            dto.setVaiTro(entity.getTaiKhoan().getVaiTro());
        }
        return dto;
    }

    /**
     * HÀM MỚI: Lấy danh sách Bác Sĩ (DTO) theo Khoa ID (Chỉ lấy bác sĩ đang
     * HOAT_DONG) (Giữ logic từ phiên bản 2 - giống hệt phiên bản 1, bao gồm cả
     * System.out)
     */
    public List<NhanVienDTO> getBacSiByKhoa(int khoaId) {
        System.out.println("Service: Bắt đầu gọi DAO với khoaId: " + khoaId);
        List<NhanVien> entities = nhanVienDAO.findBacSiByKhoaId(khoaId);
        System.out.println("Service: DAO đã trả về " + entities.size() + " entities.");
        // Lọc lại (mặc dù DAO đã lọc, nhưng để đảm bảo)
        return entities.stream()
                .filter(nv -> nv.getTaiKhoan() != null && TRANG_THAI_HOAT_DONG.equals(nv.getTaiKhoan().getTrangThai()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Chuyển NhanVien (Entity) sang NhanVienDTO (SỬA: Giữ lại logic 'tenKhoa'
     * từ phiên bản 2)
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
            // Giữ lại logic "làm phẳng" DTO từ phiên bản 2
            dto.setTenKhoa(entity.getKhoa().getTenKhoa());
        }
        return dto;
    }

    /**
     * Chuyển NhanVienDTO sang NhanVien (Entity) (Giữ logic từ phiên bản 2 -
     * giống hệt phiên bản 1)
     */
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

    /**
     * HÀM VALIDATE TẬP TRUNG (DRY) (Giữ logic từ phiên bản 2 - giống hệt phiên
     * bản 1)
     */
    private void validateNhanVienData(NhanVienDTO dto, NhanVien existingEntity) throws ValidationException {
        if (dto.getHoTen() == null || dto.getHoTen().trim().isEmpty()) {
            throw new ValidationException("Họ tên nhân viên không được để trống.");
        }

        if (!Pattern.matches(NAME_REGEX, dto.getHoTen())) {
            throw new ValidationException("Họ tên không hợp lệ (chỉ chứa chữ cái, dấu cách, và dài 2-50 ký tự).");
        }

        // 2. Kiểm tra Số điện thoại (chỉ check nếu có nhập)
        String newSdt = dto.getSoDienThoai();
        if (newSdt != null && !newSdt.trim().isEmpty()) {
            // 2a. Check định dạng (Regex)
            // Dòng này giờ đã đúng vì PHONE_NUMBER_REGEX là String
            if (!Pattern.matches(PHONE_NUMBER_REGEX, newSdt)) {
                throw new ValidationException("Số điện thoại không hợp lệ (phải là 10 số, bắt đầu bằng 03, 05, 07, 08, 09).");
            }

            // 2b. Check tính duy nhất (Unique)
            boolean isCreating = (existingEntity == null);
            boolean isUpdatingAndChanged = (!isCreating && !newSdt.equals(existingEntity.getSoDienThoai()));

            if ((isCreating || isUpdatingAndChanged) && nhanVienDAO.isSoDienThoaiExisted(newSdt)) {
                throw new ValidationException("Số điện thoại '" + newSdt + "' đã tồn tại.");
            }
        }

        // (Bạn có thể thêm các validation khác cho chuyenMon, bangCap... ở đây nếu cần)
    }

    //=======================================================Dat==============================================
    public NhanVienDTO getNhanVienByTaiKhoanId(int taiKhoanId) {
        NhanVien entity = nhanVienDAO.findByTaiKhoanId(taiKhoanId);
        return toDTO(entity); // <-- SỬA: Dùng hàm 'toDTO' chính (sẽ dùng hàm toDTO có tenKhoa ở trên)
    }

    private NhanVienDTO toDTOFotGetByTaiKhoan(NhanVien entity) {
        if (entity == null) {
            return null;
        }

        NhanVienDTO dto = new NhanVienDTO();
        dto.setId(entity.getId());
        dto.setHoTen(entity.getHoTen());
        dto.setChuyenMon(entity.getChuyenMon());

        // Lấy thông tin từ đối tượng TaiKhoan đã được JOIN FETCH
        if (entity.getTaiKhoan() != null) {
            dto.setTaiKhoanId(entity.getTaiKhoan().getId());
            dto.setVaiTro(entity.getTaiKhoan().getVaiTro());
        }

        return dto;
    }

    public List<NhanVienDTO> getDoctorsByKhoaId(int khoaId) {
        // Sử dụng findDoctorsByKhoaId (từ phiên bản 1)
        return nhanVienDAO.findDoctorsByKhoaId(khoaId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

}
