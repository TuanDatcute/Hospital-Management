package service;

import model.Entity.BenhNhan;
import model.Entity.LichHen;
import model.Entity.NhanVien;
import model.dao.BenhNhanDAO;
import model.dao.LichHenDAO;
import model.dao.NhanVienDAO;
import model.dto.LichHenDTO;
import model.dto.NhanVienDTO; // Cần import NhanVienDTO
import service.NhanVienService; // Cần import NhanVienService
import java.time.OffsetDateTime;
import java.util.Arrays; // Import Arrays để dùng List
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp Service chứa logic nghiệp vụ cho LichHen.
 * @author ADMIN
 */
public class LichHenService {

    // Khởi tạo các DAO cần thiết
    private final LichHenDAO lichHenDAO = new LichHenDAO();
    private final BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    // Khởi tạo Service cần thiết
    private final NhanVienService nhanVienService = new NhanVienService(); // <-- Thêm Service

    // Danh sách các trạng thái lịch hẹn hợp lệ
    private static final List<String> VALID_TRANG_THAI = Arrays.asList(
            "CHO_XAC_NHAN", "DA_XAC_NHAN", "HOAN_THANH", "DA_HUY", "DA_DEN_KHAM"
    );

    /**
     * Dịch vụ tạo một Lịch hẹn mới.
     * STT sẽ được tự động gán bởi Trigger CSDL.
     * @param dto DTO chứa thông tin lịch hẹn mới.
     * @return DTO của lịch hẹn đã được tạo (có ID và STT).
     * @throws Exception ném ra nếu logic validation thất bại.
     */
    public LichHenDTO createLichHen(LichHenDTO dto) throws Exception {

        // --- BƯỚC 1: LOGIC NGHIỆP VỤ (VALIDATION) ---
        if (dto.getBenhNhanId() <= 0) {
            throw new Exception("ID Bệnh nhân không hợp lệ.");
        }
        if (dto.getBacSiId() <= 0) {
            throw new Exception("ID Bác sĩ không hợp lệ.");
        }
        if (dto.getThoiGianHen() == null || dto.getThoiGianHen().isBefore(OffsetDateTime.now())) {
            throw new Exception("Thời gian hẹn phải là một thời điểm trong tương lai.");
        }

        // --- BƯỚC 2: KIỂM TRA & LẤY CÁC ENTITY LIÊN QUAN ---
        BenhNhan benhNhanEntity = benhNhanDAO.getById(dto.getBenhNhanId());
        if (benhNhanEntity == null) {
            throw new Exception("Không tìm thấy Bệnh nhân với ID: " + dto.getBenhNhanId());
        }

        // --- SỬA LẠI PHẦN KIỂM TRA BÁC SĨ ---
        NhanVienDTO bacSiDTO = null; // Dùng để validate
        try {
            // Dùng NhanVienService.getNhanVienById để kiểm tra ID và trạng thái hoạt động
            bacSiDTO = nhanVienService.getNhanVienById(dto.getBacSiId());
        } catch (Exception e) {
            throw new Exception("Không tìm thấy Bác sĩ đang hoạt động với ID: " + dto.getBacSiId());
        }

        // (Tùy chọn) Kiểm tra vai trò dựa trên TaiKhoan của NhanVien
        // Cần sửa DAO/Service NhanVien để dễ dàng lấy vai trò nếu muốn kiểm tra ở đây
        // NhanVien tempBacSi = nhanVienDAO.getByIdWithRelations(dto.getBacSiId());
        // if (tempBacSi == null || tempBacSi.getTaiKhoan() == null || !"BAC_SI".equals(tempBacSi.getTaiKhoan().getVaiTro())) {
        //     throw new Exception("Nhân viên (ID: " + dto.getBacSiId() + ") không phải là Bác sĩ hoặc không có tài khoản.");
        // }

        // Lấy Entity Bác sĩ (chỉ cần ID vì đã validate)
        NhanVien bacSiEntity = nhanVienDAO.getById(dto.getBacSiId());
        if (bacSiEntity == null) { // Kiểm tra lại đề phòng
             throw new Exception("Lỗi không mong muốn: Không thể lấy entity Bác sĩ ID: " + dto.getBacSiId());
        }
        // --- KẾT THÚC SỬA ---


        // --- BƯỚC 3: CHUYỂN ĐỔI (MAP) ---
        LichHen entity = toEntity(dto, benhNhanEntity, bacSiEntity);
        entity.setTrangThai("CHO_XAC_NHAN"); // Trạng thái mặc định

        // --- BƯỚC 4: GỌI DAO ĐỂ LƯU ---
        LichHen savedEntity = lichHenDAO.create(entity);

        // --- BƯỚC 5: TRẢ VỀ DTO ---
        if (savedEntity != null) {
            // Tải lại bản đầy đủ (có relations) để chuyển sang DTO
            LichHen fullSavedEntity = lichHenDAO.getByIdWithRelations(savedEntity.getId());
            return toDTO(fullSavedEntity);
        }
        return null;
    }

    /**
     * Dịch vụ cập nhật trạng thái của một Lịch hẹn.
     */
    public LichHenDTO updateTrangThaiLichHen(int lichHenId, String newTrangThai, String ghiChu) throws Exception {

        // --- BƯỚC 1: VALIDATION ---
        if (newTrangThai == null || newTrangThai.trim().isEmpty()) {
            throw new Exception("Trạng thái mới không được để trống.");
        }
        // Kiểm tra trạng thái có hợp lệ không
        if (!VALID_TRANG_THAI.contains(newTrangThai)) {
            throw new Exception("Trạng thái '" + newTrangThai + "' không hợp lệ.");
        }

        // --- BƯỚC 2: LẤY ENTITY GỐC ---
        LichHen existingEntity = lichHenDAO.getById(lichHenId);
        if (existingEntity == null) {
            throw new Exception("Không tìm thấy lịch hẹn với ID: " + lichHenId);
        }

        // --- BƯỚC 3: CẬP NHẬT ---
        existingEntity.setTrangThai(newTrangThai);
        if (ghiChu != null) { // Chỉ cập nhật ghi chú nếu được cung cấp
            existingEntity.setGhiChu(ghiChu);
        }

        // --- BƯỚC 4: GỌI DAO ĐỂ CẬP NHẬT ---
        boolean success = lichHenDAO.update(existingEntity);
        if (!success) {
            throw new Exception("Cập nhật trạng thái lịch hẹn thất bại.");
        }

        // --- BƯỚC 5: TRẢ VỀ DTO (ĐÃ CẬP NHẬT) ---
        LichHen updatedEntity = lichHenDAO.getByIdWithRelations(lichHenId);
        return toDTO(updatedEntity);
    }

    /**
     * Lấy lịch hẹn bằng ID (tải đủ quan hệ).
     */
    public LichHenDTO getLichHenById(int id) throws Exception {
        LichHen entity = lichHenDAO.getByIdWithRelations(id);
        if (entity == null) {
            throw new Exception("Không tìm thấy lịch hẹn với ID: " + id);
        }
        return toDTO(entity);
    }

    /**
     * Lấy tất cả lịch hẹn (tải đủ quan hệ).
     */
    public List<LichHenDTO> getAllLichHen() {
        List<LichHen> entities = lichHenDAO.getAllWithRelations();
        return entities.stream()
                       .map(this::toDTO)
                       .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả lịch hẹn của một bác sĩ đang hoạt động (tải đủ quan hệ).
     */
    public List<LichHenDTO> getLichHenByBacSi(int bacSiId) throws Exception {
         // --- SỬA LẠI PHẦN KIỂM TRA BÁC SĨ ---
         try {
             nhanVienService.getNhanVienById(bacSiId); // Kiểm tra ID và trạng thái hoạt động
         } catch (Exception e) {
              throw new Exception("Không tìm thấy Bác sĩ đang hoạt động với ID: " + bacSiId);
         }
         // --- KẾT THÚC SỬA ---

        List<LichHen> entities = lichHenDAO.findByBacSiId(bacSiId);
        return entities.stream()
                       .map(this::toDTO)
                       .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả lịch hẹn của một bệnh nhân (tải đủ quan hệ).
     */
    public List<LichHenDTO> getLichHenByBenhNhan(int benhNhanId) throws Exception {
        if (benhNhanDAO.getById(benhNhanId) == null) { // Chỉ cần kiểm tra tồn tại
             throw new Exception("Không tìm thấy Bệnh nhân với ID: " + benhNhanId);
        }
        List<LichHen> entities = lichHenDAO.findByBenhNhanId(benhNhanId);
        return entities.stream()
                       .map(this::toDTO)
                       .collect(Collectors.toList());
    }

    // --- CÁC HÀM MAPPER (Chuyển đổi DTO <-> Entity) ---

    /**
     * Chuyển LichHen (Entity) sang LichHenDTO.
     */
    private LichHenDTO toDTO(LichHen entity) {
        if (entity == null) return null; // Thêm kiểm tra null
        LichHenDTO dto = new LichHenDTO();
        dto.setId(entity.getId());
        dto.setStt(entity.getStt());
        dto.setThoiGianHen(entity.getThoiGianHen());
        dto.setLyDoKham(entity.getLyDoKham());
        dto.setTrangThai(entity.getTrangThai());
        dto.setGhiChu(entity.getGhiChu());

        if (entity.getBenhNhan() != null) {
            dto.setBenhNhanId(entity.getBenhNhan().getId());
        }
        if (entity.getBacSi() != null) {
            dto.setBacSiId(entity.getBacSi().getId());
        }

        return dto;
    }

    /**
     * Chuyển LichHenDTO sang LichHen (Entity).
     */
    private LichHen toEntity(LichHenDTO dto, BenhNhan benhNhan, NhanVien bacSi) {
        LichHen entity = new LichHen();

        entity.setThoiGianHen(dto.getThoiGianHen());
        entity.setLyDoKham(dto.getLyDoKham());
        entity.setTrangThai(dto.getTrangThai()); // Sẽ được ghi đè bởi logic create
        entity.setGhiChu(dto.getGhiChu());

        entity.setBenhNhan(benhNhan);
        entity.setBacSi(bacSi);

        return entity;
    }
}