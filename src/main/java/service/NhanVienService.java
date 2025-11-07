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
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.Collections;

/**
 * Lớp Service chứa logic nghiệp vụ cho NhanVien.
 */
public class NhanVienService {

    // Khởi tạo các DAO cần thiết
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final KhoaDAO khoaDAO = new KhoaDAO();

    // --- CẬP NHẬT: Định nghĩa các hằng số REGEX ---
    private static final String PHONE_NUMBER_REGEX = "^(0[3|5|7|8|9])+([0-9]{8})$";
    private static final String NAME_REGEX = "^[\\p{L} .'-]{2,50}$";

    // --- THÊM MỚI: Hằng số trạng thái (Clean Code) ---
    private static final String TRANG_THAI_HOAT_DONG = "HOAT_DONG";
    private static final String TRANG_THAI_BI_KHOA = "BI_KHOA";

    /**
     * Dịch vụ tạo một Nhân Viên mới.
     */
    public NhanVienDTO createNhanVien(NhanVienDTO dto) throws ValidationException, Exception {

        // --- BƯỚC 1: LOGIC NGHIỆP VỤ (VALIDATION) ---
        validateNhanVienData(dto, null);

        if (dto.getTaiKhoanId() <= 0) {
            throw new ValidationException("ID Tài khoản không hợp lệ. Phải gán một tài khoản.");
        }

        // --- BƯỚC 2: KIỂM TRA & LẤY CÁC ENTITY LIÊN QUAN ---
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

        // --- BƯỚC 3: CHUYỂN ĐỔI (MAP) ---
        NhanVien entity = toEntity(dto, taiKhoanEntity, khoaEntity);

        // --- BƯỚC 4: GỌI DAO ĐỂ LƯU ---
        NhanVien savedEntity = nhanVienDAO.create(entity);

        // --- BƯỚC 5: TRẢ VỀ DTO ---
        if (savedEntity != null) {
            // Gán lại các entity vừa dùng để đảm bảo toDTO có đủ thông tin
            // (vì savedEntity có thể chưa load lazy relations)
            savedEntity.setTaiKhoan(taiKhoanEntity);
            savedEntity.setKhoa(khoaEntity);
            return toDTO(savedEntity);
        }
        return null;
    }

    /**
     * Dịch vụ cập nhật thông tin Nhân Viên.
     */
    public NhanVienDTO updateNhanVien(int nhanVienId, NhanVienDTO dto) throws ValidationException, Exception {

        // --- BƯỚC 1: LẤY ENTITY GỐC (Kèm relations để kiểm tra) ---
        NhanVien existingEntity = nhanVienDAO.getByIdWithRelations(nhanVienId);
        if (existingEntity == null) {
            throw new ValidationException("Không tìm thấy nhân viên với ID: " + nhanVienId);
        }
        if (existingEntity.getTaiKhoan() == null || !TRANG_THAI_HOAT_DONG.equals(existingEntity.getTaiKhoan().getTrangThai())) {
            throw new ValidationException("Không thể cập nhật thông tin cho nhân viên có tài khoản bị khóa hoặc không tồn tại.");
        }

        // --- BƯỚC 2: VALIDATION ---
        validateNhanVienData(dto, existingEntity);

        // --- BƯỚC 3: CẬP NHẬT CÁC TRƯỜNG ---
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

        // --- BƯỚC 4: GỌI DAO ĐỂ CẬP NHẬT ---
        try {
            nhanVienDAO.update(existingEntity);
        } catch (RuntimeException e) {
            throw new Exception("Cập nhật nhân viên thất bại do lỗi CSDL: " + e.getMessage(), e);
        }

        // --- BƯỚC 5: TRẢ VỀ DTO (ĐÃ CẬP NHẬT) ---
        // Tải lại để chắc chắn lấy dữ liệu mới nhất
        NhanVien updatedEntity = nhanVienDAO.getByIdWithRelations(nhanVienId);
        return toDTO(updatedEntity);
    }

    /**
     * Dịch vụ thực hiện Soft Delete cho Nhân Viên. (Bằng cách khóa tài khoản
     * liên kết)
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
            taiKhoan.setUpdatedAt(LocalDateTime.now()); // Giả sử Entity TaiKhoan có trường này

            try {
                taiKhoanDAO.update(taiKhoan);
            } catch (RuntimeException e) {
                throw new Exception("Không thể cập nhật trạng thái tài khoản cho nhân viên ID: " + nhanVienId, e);
            }
        }
    }

    /**
     * Lấy nhân viên bằng ID (chỉ trả về nếu tài khoản đang hoạt động).
     */
    public NhanVienDTO getNhanVienById(int id) throws ValidationException {
        NhanVien entity = nhanVienDAO.getByIdWithRelations(id);
        if (entity == null || entity.getTaiKhoan() == null || !TRANG_THAI_HOAT_DONG.equals(entity.getTaiKhoan().getTrangThai())) {
            throw new ValidationException("Không tìm thấy nhân viên đang hoạt động với ID: " + id);
        }
        return toDTO(entity);
    }

    /**
     * Lấy tất cả nhân viên đang hoạt động.
     */
    public List<NhanVienDTO> getAllNhanVien() {
        List<NhanVien> entities = nhanVienDAO.getAllWithRelations();

        return entities.stream()
                .filter(nv -> nv.getTaiKhoan() != null && TRANG_THAI_HOAT_DONG.equals(nv.getTaiKhoan().getTrangThai()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Dịch vụ tìm tất cả bác sĩ đang hoạt động.
     */
    public List<NhanVienDTO> findDoctorsBySpecialty() {
        // Giả sử DAO findDoctorsBySpecialty() chỉ lấy vai trò BAC_SI
        List<NhanVien> entities = nhanVienDAO.findDoctorsBySpecialty();

        if (entities == null) {
            return Collections.emptyList();
        }

        // Lọc thêm trạng thái HOAT_DONG
        return entities.stream()
                .filter(nv -> nv.getTaiKhoan() != null && TRANG_THAI_HOAT_DONG.equals(nv.getTaiKhoan().getTrangThai()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * HÀM MỚI: Lấy danh sách Bác Sĩ (DTO) theo Khoa ID (Chỉ lấy bác sĩ đang
     * HOAT_DONG)
     */
    public List<NhanVienDTO> getBacSiByKhoa(int khoaId) {
        // Giả sử DAO findByKhoaId đã lọc vai trò BAC_SI
        System.out.println("Service: Bắt đầu gọi DAO với khoaId: " + khoaId); // <-- THÊM DÒNG NÀY
        List<NhanVien> entities = nhanVienDAO.findBacSiByKhoaId(khoaId);
        System.out.println("Service: DAO đã trả về " + entities.size() + " entities."); // Lọc lại chỉ những người HOAT_DONG
        return entities.stream()
                .filter(nv -> nv.getTaiKhoan() != null && TRANG_THAI_HOAT_DONG.equals(nv.getTaiKhoan().getTrangThai()))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Chuyển NhanVien (Entity) sang NhanVienDTO CẬP NHẬT: Tuân thủ DTO phẳng
     * (chỉ chứa khoaId)
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
            dto.setVaiTro(entity.getTaiKhoan().getVaiTro());
        }

        if (entity.getKhoa() != null) {
            dto.setKhoaId(entity.getKhoa().getId());
            // Đã xóa: dto.setTenKhoa(...)
        }

        return dto;
    }

    /**
     * Chuyển NhanVienDTO sang NhanVien (Entity)
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

    // --- HÀM VALIDATE TẬP TRUNG (DRY) ---
    private void validateNhanVienData(NhanVienDTO dto, NhanVien existingEntity) throws ValidationException {
        // 1. Kiểm tra Họ tên
        if (dto.getHoTen() == null || dto.getHoTen().trim().isEmpty()) {
            throw new ValidationException("Họ tên nhân viên không được để trống.");
        }
        if (!Pattern.matches(NAME_REGEX, dto.getHoTen())) {
            throw new ValidationException("Họ tên không hợp lệ (chỉ chứa chữ cái, dấu cách, và dài 2-50 ký tự).");
        }

        // 2. Kiểm tra Số điện thoại
        String newSdt = dto.getSoDienThoai();
        if (newSdt != null && !newSdt.trim().isEmpty()) {
            if (!Pattern.matches(PHONE_NUMBER_REGEX, newSdt)) {
                throw new ValidationException("Số điện thoại không hợp lệ (phải là 10 số, bắt đầu bằng 03, 05, 07, 08, 09).");
            }

            boolean isCreating = (existingEntity == null);
            boolean isUpdatingAndChanged = (!isCreating && !newSdt.equals(existingEntity.getSoDienThoai()));

            // Giả sử DAO có hàm này
            if ((isCreating || isUpdatingAndChanged) && nhanVienDAO.isSoDienThoaiExisted(newSdt)) {
                throw new ValidationException("Số điện thoại '" + newSdt + "' đã tồn tại.");
            }
        }
    }

    //=======================================================Dat==============================================
    /**
     * CẬP NHẬT: Dùng hàm toDTO() chính
     */
    public NhanVienDTO getNhanVienByTaiKhoanId(int taiKhoanId) {
        NhanVien entity = nhanVienDAO.findByTaiKhoanId(taiKhoanId);
        return toDTO(entity); // <-- SỬA: Dùng hàm 'toDTO' chính
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
        return nhanVienDAO.findBacSiByKhoaId(khoaId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}

