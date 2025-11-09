package service;

import exception.ValidationException;
import java.time.LocalDate;
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
import java.util.ArrayList; // THÊM MỚI
import java.util.Arrays; // Import Arrays để dùng List
import java.util.List;
import java.util.stream.Collectors;
import model.Entity.TaiKhoan;
import util.EmailUtils;

/**
 * Lớp Service chứa logic nghiệp vụ cho LichHen. (ĐÃ GỘP: Giữ lại Phân trang,
 * Sửa Admin, khối Dat và khối Quang)
 */
public class LichHenService {

    // Khởi tạo các DAO cần thiết
    private final LichHenDAO lichHenDAO = new LichHenDAO();
    private final BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    // Khởi tạo Service cần thiết
    private final NhanVienService nhanVienService = new NhanVienService();

    // Danh sách các trạng thái lịch hẹn hợp lệ
    private static final List<String> VALID_TRANG_THAI = Arrays.asList(
            "CHO_XAC_NHAN", "DA_XAC_NHAN", "HOAN_THANH", "DA_HUY", "DA_DEN_KHAM"
    );

    // === GIỮ LẠI TỪ FILE 1 ===
    private static final int MAX_APPOINTMENTS_PER_DAY = 5;

    /**
     * Dịch vụ tạo một Lịch hẹn mới (cho Admin). (Giữ logic từ File 2 - đã lọc
     * xóa mềm)
     */
    public LichHenDTO createLichHen(LichHenDTO dto) throws Exception {

        if (dto.getBenhNhanId() <= 0) {
            throw new Exception("ID Bệnh nhân không hợp lệ.");
        }
        if (dto.getBacSiId() <= 0) {
            throw new Exception("ID Bác sĩ không hợp lệ.");
        }
        if (dto.getThoiGianHen() == null || dto.getThoiGianHen().isBefore(OffsetDateTime.now())) {
            throw new Exception("Thời gian hẹn phải là một thời điểm trong tương lai.");
        }

        BenhNhan benhNhanEntity = benhNhanDAO.getById(dto.getBenhNhanId()); // DAO đã lọc
        if (benhNhanEntity == null) {
            throw new Exception("Không tìm thấy Bệnh nhân (đang hoạt động) với ID: " + dto.getBenhNhanId());
        }

        NhanVienDTO bacSiDTO;
        try {
            bacSiDTO = nhanVienService.getNhanVienById(dto.getBacSiId()); // Service đã lọc
        } catch (Exception e) {
            throw new Exception("Không tìm thấy Bác sĩ đang hoạt động với ID: " + dto.getBacSiId());
        }

        NhanVien bacSiEntity = nhanVienDAO.getById(dto.getBacSiId());
        if (bacSiEntity == null) {
            throw new Exception("Lỗi không mong muốn: Không thể lấy entity Bác sĩ ID: " + dto.getBacSiId());
        }

        LichHen entity = toEntity(dto, benhNhanEntity, bacSiEntity);
        entity.setTrangThai("CHO_XAC_NHAN"); // Trạng thái mặc định

        LichHen savedEntity = lichHenDAO.create(entity);

        if (savedEntity != null) {
            // Tải lại bản đầy đủ (Trigger CSDL đã gán STT)
            LichHen fullSavedEntity = lichHenDAO.getByIdWithRelations(savedEntity.getId());
            return toDTO(fullSavedEntity);
        }
        throw new Exception("Không thể tạo lịch hẹn.");
    }

    /**
     * HÀM MỚI (TỪ FILE 2): Dịch vụ cập nhật thông tin Lịch hẹn (cho Admin).
     */
    public LichHenDTO updateLichHen(int lichHenId, LichHenDTO dto) throws ValidationException, Exception {
        if (dto.getBenhNhanId() <= 0) {
            throw new ValidationException("ID Bệnh nhân không hợp lệ.");
        }
        if (dto.getBacSiId() <= 0) {
            throw new ValidationException("ID Bác sĩ không hợp lệ.");
        }
        if (dto.getThoiGianHen() == null) {
            throw new ValidationException("Thời gian hẹn không được để trống.");
        }

        LichHen existingEntity = lichHenDAO.getById(lichHenId); // DAO đã lọc
        if (existingEntity == null) {
            throw new ValidationException("Không tìm thấy lịch hẹn với ID: " + lichHenId);
        }

        if (existingEntity.getBenhNhan().getId() != dto.getBenhNhanId()) {
            BenhNhan newBenhNhan = benhNhanDAO.getById(dto.getBenhNhanId()); // DAO đã lọc
            if (newBenhNhan == null) {
                throw new ValidationException("Không tìm thấy Bệnh nhân mới (ID: " + dto.getBenhNhanId() + ").");
            }
            existingEntity.setBenhNhan(newBenhNhan);
        }

        if (existingEntity.getBacSi().getId() != dto.getBacSiId()) {
            NhanVien newBacSi = nhanVienDAO.getById(dto.getBacSiId()); // DAO đã lọc
            if (newBacSi == null) {
                throw new ValidationException("Không tìm thấy Bác sĩ mới (ID: " + dto.getBacSiId() + ").");
            }
            existingEntity.setBacSi(newBacSi);
        }

        existingEntity.setThoiGianHen(dto.getThoiGianHen());
        existingEntity.setLyDoKham(dto.getLyDoKham());
        existingEntity.setGhiChu(dto.getGhiChu());

        if (!lichHenDAO.update(existingEntity)) {
            throw new Exception("Lỗi CSDL: Cập nhật lịch hẹn thất bại.");
        }

        return toDTO(lichHenDAO.getByIdWithRelations(lichHenId));
    }

    /**
     * Dịch vụ cập nhật trạng thái của một Lịch hẹn. (Giữ logic từ File 2)
     */
    public LichHenDTO updateTrangThaiLichHen(int lichHenId, String newTrangThai, String ghiChu) throws Exception {

        if (newTrangThai == null || newTrangThai.trim().isEmpty()) {
            throw new Exception("Trạng thái mới không được để trống.");
        }
        if (!VALID_TRANG_THAI.contains(newTrangThai)) {
            throw new Exception("Trạng thái '" + newTrangThai + "' không hợp lệ.");
        }

        LichHen existingEntity = lichHenDAO.getById(lichHenId); // DAO đã lọc
        if (existingEntity == null) {
            throw new Exception("Không tìm thấy lịch hẹn với ID: " + lichHenId);
        }

        existingEntity.setTrangThai(newTrangThai);
        if (ghiChu != null) {
            existingEntity.setGhiChu(ghiChu);
        }

        boolean success = lichHenDAO.update(existingEntity);
        if (!success) {
            throw new Exception("Cập nhật trạng thái lịch hẹn thất bại.");
        }

        LichHen updatedEntity = lichHenDAO.getByIdWithRelations(lichHenId);
        return toDTO(updatedEntity);
    }

    /**
     * Lấy lịch hẹn bằng ID (tải đủ quan hệ). (Giữ logic từ File 2)
     */
    public LichHenDTO getLichHenById(int id) throws Exception {
        LichHen entity = lichHenDAO.getByIdWithRelations(id);
        if (entity == null) {
            throw new Exception("Không tìm thấy lịch hẹn với ID: " + id);
        }
        return toDTO(entity);
    }

    /**
     * KHÔI PHỤC (TỪ FILE 1): Lấy tất cả lịch hẹn (không phân trang).
     */
    public List<LichHenDTO> getAllLichHen() {
        List<LichHen> entities = lichHenDAO.getAllWithRelations();
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * HÀM MỚI (TỪ FILE 2): Lấy tất cả lịch hẹn (có phân trang).
     */
    public List<LichHenDTO> getAllLichHenPaginated(int page, int pageSize) {
        List<LichHen> entities = lichHenDAO.getAllWithRelations(page, pageSize);
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * HÀM MỚI (TỪ FILE 2): Đếm tổng số lịch hẹn.
     */
    public long getLichHenCount() {
        return lichHenDAO.getTotalLichHenCount();
    }

    /**
     * Lấy tất cả lịch hẹn của một bác sĩ đang hoạt động (tải đủ quan hệ). (Giữ
     * logic từ File 2)
     */
    public List<LichHenDTO> getLichHenByBacSi(int bacSiId) throws Exception {
        try {
            nhanVienService.getNhanVienById(bacSiId); // Kiểm tra ID và trạng thái
        } catch (Exception e) {
            throw new Exception("Không tìm thấy Bác sĩ đang hoạt động với ID: " + bacSiId);
        }

        List<LichHen> entities = lichHenDAO.findByBacSiId(bacSiId);
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * GỘP (TỪ FILE 1): Lấy tất cả lịch hẹn của một bệnh nhân (có tìm kiếm)
     */
    public List<LichHenDTO> getLichHenByBenhNhan(int benhNhanId, String keyword) throws Exception {
        if (benhNhanDAO.getById(benhNhanId) == null) {
            throw new Exception("Không tìm thấy Bệnh nhân với ID: " + benhNhanId);
        }
        // Gọi hàm DAO đã cập nhật (có keyword) - từ File 1
        List<LichHen> entities = lichHenDAO.findByBenhNhanId(benhNhanId, keyword);

        List<LichHenDTO> dtos = new ArrayList<>();
        for (LichHen entity : entities) {
            dtos.add(toDTO(entity));
        }
        return dtos;
    }

    // --- CÁC HÀM MAPPER (Chuyển đổi DTO <-> Entity) ---
    /**
     * Chuyển LichHen (Entity) sang LichHenDTO (đã "làm phẳng"). (Giữ logic từ
     * File 2 - giống hệt File 1)
     */
    private LichHenDTO toDTO(LichHen entity) {
        if (entity == null) {
            return null;
        }
        LichHenDTO dto = new LichHenDTO();
        dto.setId(entity.getId());
        dto.setStt(entity.getStt());
        dto.setThoiGianHen(entity.getThoiGianHen());
        dto.setLyDoKham(entity.getLyDoKham());
        dto.setTrangThai(entity.getTrangThai());
        dto.setGhiChu(entity.getGhiChu());

        if (entity.getBenhNhan() != null) {
            dto.setBenhNhanId(entity.getBenhNhan().getId());
            dto.setTenBenhNhan(entity.getBenhNhan().getHoTen()); // <-- LÀM PHẲNG
        }
        if (entity.getBacSi() != null) {
            dto.setBacSiId(entity.getBacSi().getId());
            dto.setTenBacSi(entity.getBacSi().getHoTen()); // <-- LÀM PHẲNG
        }

        return dto;
    }

    /**
     * Chuyển LichHenDTO sang LichHen (Entity). (Giữ logic từ File 2 - giống hệt
     * File 1)
     */
    private LichHen toEntity(LichHenDTO dto, BenhNhan benhNhan, NhanVien bacSi) {
        LichHen entity = new LichHen();

        entity.setThoiGianHen(dto.getThoiGianHen());
        entity.setLyDoKham(dto.getLyDoKham());
        entity.setTrangThai(dto.getTrangThai());
        entity.setGhiChu(dto.getGhiChu());

        entity.setBenhNhan(benhNhan);
        entity.setBacSi(bacSi);

        return entity;
    }

    //===================================================Dat=======================================
    // (KHỐI CODE NÀY ĐƯỢC SAO CHÉP NGUYÊN BẢN TỪ FILE 1 THEO YÊU CẦU)
    /**
     * NGHIỆP VỤ: Tạo lịch hẹn mới (do Y tá hoặc Lễ tân thực hiện).
     */
    public LichHenDTO createAppointmentByNurse(LichHenDTO dto) throws ValidationException {

        // --- BƯỚC 1: VALIDATE DỮ LIỆU ĐẦU VÀO ---
        if (dto.getThoiGianHen() == null) {
            throw new ValidationException("Thời gian hẹn không được để trống.");
        }
        if (dto.getBenhNhanId() <= 0) { // Thêm kiểm tra
            throw new ValidationException("Vui lòng chọn bệnh nhân.");
        }
        if (dto.getBacSiId() <= 0) { // Thêm kiểm tra
            throw new ValidationException("Vui lòng chọn bác sĩ.");
        }

        BenhNhan benhNhan = benhNhanDAO.getById(dto.getBenhNhanId());
        if (benhNhan == null) {
            throw new ValidationException("Không tìm thấy bệnh nhân.");
        }

        NhanVien bacSi = nhanVienDAO.getById(dto.getBacSiId());
        if (bacSi == null) {
            throw new ValidationException("Không tìm thấy bác sĩ.");
        }

        // --- BƯỚC 2: LOGIC NGHIỆP VỤ TẠO STT (CỐT LÕI) ---
        LocalDate appointmentDate = dto.getThoiGianHen().toLocalDate();
        long count = lichHenDAO.countAppointmentsByDateAndDoctor(appointmentDate, bacSi.getId());
        int newStt = (int) count + 1;

        // --- BƯỚC 3: CHUYỂN DTO -> ENTITY (toEntity) ---
        // (Dùng hàm toEntity helper)
        LichHen newLichHen = toEntity(dto, benhNhan, bacSi);

        // Gán các giá trị tự động
        newLichHen.setStt(newStt);
        newLichHen.setTrangThai("CHO_XAC_NHAN");

        // --- BƯỚC 4: LƯU VÀO CSDL ---
        LichHen savedEntity = lichHenDAO.create(newLichHen);
        LichHenDTO finalDTO = toDTO(savedEntity);
        try {
            // Lấy email của bệnh nhân (từ TaiKhoan liên kết)
            TaiKhoan taiKhoanBenhNhan = benhNhan.getTaiKhoan();
            if (taiKhoanBenhNhan != null && taiKhoanBenhNhan.getEmail() != null) {
                System.out.println("Đang gửi email xác nhận đặt lịch tới: " + taiKhoanBenhNhan.getEmail());
                // Gọi hàm EmailUtils (sẽ tạo ở Bước 2)
                EmailUtils.sendAppointmentConfirmationEmail(
                        taiKhoanBenhNhan.getEmail(),
                        finalDTO // Gửi DTO đã "làm phẳng"
                );
            } else {
                System.err.println("Không thể gửi email: Bệnh nhân " + benhNhan.getHoTen() + " không có email hoặc tài khoản.");
            }
        } catch (Exception e) {
            // QUAN TRỌNG: Không ném lỗi (throw e) ở đây
            // Việc gửi mail thất bại KHÔNG nên làm hỏng toàn bộ chức năng đặt lịch.
            e.printStackTrace();
            System.err.println("LỖI GỬI EMAIL: Đặt lịch vẫn thành công, nhưng gửi email thất bại. " + e.getMessage());
        }

        // --- BƯỚC 5: TRẢ VỀ DTO ---
        return toDTO(savedEntity);
    }

    /**
     * Đánh dấu một lịch hẹn là đã được xử lý (ví dụ: ĐÃ KHÁM).
     */
    public void updateAppointmentStatus(Integer lichHenId, String newStatus) throws ValidationException {
        if (lichHenId == null) {
            return;
        }

        LichHen lichHen = lichHenDAO.getById(lichHenId);
        if (lichHen != null) {
            lichHen.setTrangThai(newStatus);
            lichHenDAO.update(lichHen); // Giả sử DAO có hàm update
        } else {
            throw new ValidationException("Không tìm thấy lịch hẹn với ID: " + lichHenId);
        }
    }

    /**
     * Lấy tất cả các lịch hẹn đang ở trạng thái chờ khám (chưa hoàn thành).
     */
    public List<LichHenDTO> getAllPendingAppointments() {
        List<LichHen> entities = lichHenDAO.getAllPendingAppointments();
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Lấy tất cả các lịch hẹn đang chờ của một bác sĩ cụ thể.
     */
    public List<LichHenDTO> getPendingAppointmentsForDoctor(int bacSiId) {
        List<LichHen> entities = lichHenDAO.getPendingAppointmentsForDoctor(bacSiId);
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    // (KẾT THÚC KHỐI CODE CỦA DAT)

    /**
     * Lấy tất cả các lịch hẹn (dưới dạng DTO).
     */
    public List<LichHenDTO> getAllAppointments() {
        List<LichHen> entities = lichHenDAO.getAll();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Tìm kiếm lịch hẹn theo từ khóa (dưới dạng DTO).
     */
    public List<LichHenDTO> searchAppointments(String keyword) {
        List<LichHen> entities = lichHenDAO.search(keyword);
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * NGHIỆP VỤ: Cập nhật trạng thái của một lịch hẹn.
     *
     * @param lichHenId ID của lịch hẹn cần cập nhật.
     * @param newStatus Trạng thái mới.
     * @return DTO của lịch hẹn sau khi đã cập nhật.
     * @throws ValidationException nếu không tìm thấy lịch hẹn.
     */
    public LichHenDTO updateAppointmentStatus(int lichHenId, String newStatus) throws ValidationException {
        // 1. Lấy Entity gốc
        LichHen existingLichHen = lichHenDAO.getByIdWithRelations(lichHenId);
        if (existingLichHen == null) {
            throw new ValidationException("Không tìm thấy lịch hẹn với ID: " + lichHenId);
        }

        // (Bạn có thể thêm logic kiểm tra ở đây, ví dụ: không cho phép đổi từ DA_HUY sang DA_XAC_NHAN)
        // 2. Cập nhật trạng thái
        existingLichHen.setTrangThai(newStatus);

        // 3. Lưu lại
        lichHenDAO.update(existingLichHen);

        // 4. Trả về DTO mới
        return toDTO(existingLichHen);
    }

    /**
     * Lấy danh sách lịch hẹn đang chờ của một bác sĩ trong một ngày cụ thể.
     */
    public List<LichHenDTO> getPendingAppointments(LocalDate date, int bacSiId) {

        List<LichHen> entities = lichHenDAO.getPendingAppointmentsByDateAndDoctor(date, bacSiId);

        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    //===================================================Quang=======================================
    // (KHỐI CODE NÀY ĐƯỢC SAO CHÉP NGUYÊN BẢN TỪ FILE 1 THEO YÊU CẦU)
    // ===================================================
    // === HÀM MỚI CHO BỆNH NHÂN TỰ ĐẶT LỊCH ===
    // ===================================================
    /**
     * HÀM MỚI: Dành cho Bệnh nhân tự đặt lịch. Tự động lấy BenhNhanId từ
     * TaiKhoanId trong session.
     *
     * @param dto DTO chứa (bacSiId, thoiGianHen, lyDoKham)
     * @param taiKhoanIdCuaBenhNhan ID tài khoản của bệnh nhân (LẤY TỪ SESSION)
     * @return DTO của lịch hẹn đã tạo
     * @throws Exception
     */
    public LichHenDTO createAppointmentByPatient(LichHenDTO dto, int taiKhoanIdCuaBenhNhan) throws Exception {

        // --- BƯỚC 1: VALIDATE DỮ LIỆU ĐẦU VÀO ---
        if (dto.getBacSiId() <= 0) {
            throw new ValidationException("Vui lòng chọn bác sĩ.");
        }
        if (dto.getThoiGianHen() == null || dto.getThoiGianHen().isBefore(OffsetDateTime.now())) {
            throw new ValidationException("Thời gian hẹn phải là một thời điểm trong tương lai.");
        }
        if (dto.getLyDoKham() == null || dto.getLyDoKham().trim().isEmpty()) {
            throw new ValidationException("Vui lòng nhập lý do khám.");
        }

        // --- BƯỚC 2: LẤY ENTITY LIÊN QUAN (QUAN TRỌNG) ---
        // Lấy Bệnh nhân TỪ TÀI KHOẢN ĐĂNG NHẬP (An toàn)
        BenhNhan benhNhanEntity = benhNhanDAO.findByTaiKhoanId(taiKhoanIdCuaBenhNhan); // (Cần hàm này trong BenhNhanDAO)
        if (benhNhanEntity == null) {
            throw new Exception("Không tìm thấy hồ sơ bệnh nhân tương ứng với tài khoản của bạn.");
        }

        // Kiểm tra Bác sĩ
        NhanVien bacSiEntity = nhanVienDAO.getById(dto.getBacSiId());
        if (bacSiEntity == null) {
            throw new Exception("Không tìm thấy bác sĩ đã chọn.");
        }
        try {
            nhanVienService.getNhanVienById(dto.getBacSiId());
        } catch (Exception e) {
            throw new Exception("Bác sĩ bạn chọn hiện không hoạt động.");
        }

        // --- BƯỚC 3: LOGIC NGHIỆP VỤ (Lấy STT TỰ ĐỘNG) ---
        LocalDate appointmentDate = dto.getThoiGianHen().toLocalDate();
        long count = lichHenDAO.countAppointmentsByDateAndDoctor(appointmentDate, bacSiEntity.getId());
        int newStt = (int) count + 1;

        // --- BƯỚC 4: CHUYỂN ĐỔI (MAP) ---
        LichHen entity = new LichHen();
        entity.setThoiGianHen(dto.getThoiGianHen());
        entity.setLyDoKham(dto.getLyDoKham().trim());
        entity.setGhiChu(dto.getGhiChu());

        entity.setBenhNhan(benhNhanEntity); // Gán bệnh nhân từ session
        entity.setBacSi(bacSiEntity);       // Gán bác sĩ từ form

        entity.setStt(newStt); // Gán STT (Trigger sẽ ghi đè nếu có)
        entity.setTrangThai("CHO_XAC_NHAN"); // Mặc định

        // --- BƯỚC 5: GỌI DAO ĐỂ LƯU ---
        LichHen savedEntity = lichHenDAO.create(entity);

        // --- BƯỚC 6: TẢI LẠI VÀ CHUYỂN SANG DTO ---
        LichHen fullSavedEntity = lichHenDAO.getByIdWithRelations(savedEntity.getId());
        LichHenDTO finalDTO = toDTO(fullSavedEntity); // DTO đã "làm phẳng" (có tên BS, BN)

        // === THÊM MỚI: GỬI EMAIL THÔNG BÁO ===
        try {
            // Lấy email của bệnh nhân (từ TaiKhoan liên kết)
            TaiKhoan taiKhoanBenhNhan = benhNhanEntity.getTaiKhoan();
            if (taiKhoanBenhNhan != null && taiKhoanBenhNhan.getEmail() != null) {
                System.out.println("Đang gửi email xác nhận đặt lịch tới: " + taiKhoanBenhNhan.getEmail());
                // Gọi hàm EmailUtils (sẽ tạo ở Bước 2)
                EmailUtils.sendAppointmentConfirmationEmail(
                        taiKhoanBenhNhan.getEmail(),
                        finalDTO // Gửi DTO đã "làm phẳng"
                );
            } else {
                System.err.println("Không thể gửi email: Bệnh nhân " + benhNhanEntity.getHoTen() + " không có email hoặc tài khoản.");
            }
        } catch (Exception e) {
            // QUAN TRỌNG: Không ném lỗi (throw e) ở đây
            // Việc gửi mail thất bại KHÔNG nên làm hỏng toàn bộ chức năng đặt lịch.
            e.printStackTrace();
            System.err.println("LỖI GỬI EMAIL: Đặt lịch vẫn thành công, nhưng gửi email thất bại. " + e.getMessage());
        }
        // ===================================

        // --- BƯỚC 7: TRẢ VỀ DTO ---
        return finalDTO;
    }

    /**
     * HÀM MỚI: Bệnh nhân tự hủy lịch hẹn (an toàn)
     */
    public void cancelAppointmentByPatient(int lichHenId, int taiKhoanIdCuaBenhNhan) throws Exception {
        // 1. Tìm bệnh nhân từ tài khoản
        BenhNhan benhNhan = benhNhanDAO.findByTaiKhoanId(taiKhoanIdCuaBenhNhan);
        if (benhNhan == null) {
            throw new ValidationException("Không tìm thấy hồ sơ bệnh nhân của bạn.");
        }

        // 2. Lấy lịch hẹn (an toàn)
        // SỬA: Phải lấy "WithRelations" để có thông tin Bác sĩ
        LichHen lichHen = lichHenDAO.getByIdAndBenhNhanIdWithRelations(lichHenId, benhNhan.getId()); // <-- Cần hàm DAO mới này
        if (lichHen == null) {
            throw new ValidationException("Không tìm thấy lịch hẹn hoặc bạn không có quyền hủy lịch này.");
        }

        // 3. Kiểm tra logic trạng thái
        String currentTrangThai = lichHen.getTrangThai();
        if (!"CHO_XAC_NHAN".equals(currentTrangThai) && !"DA_XAC_NHAN".equals(currentTrangThai)) {
            throw new ValidationException("Chỉ có thể hủy lịch hẹn đang 'Chờ xác nhận' hoặc 'Đã xác nhận'.");
        }

        // 4. Cập nhật
        lichHen.setTrangThai("DA_HUY"); // Cập nhật trạng thái
        lichHenDAO.update(lichHen); // Gọi DAO để lưu

        // === THÊM MỚI: GỬI EMAIL THÔNG BÁO HỦY ===
        try {
            TaiKhoan taiKhoanBenhNhan = benhNhan.getTaiKhoan();
            if (taiKhoanBenhNhan != null && taiKhoanBenhNhan.getEmail() != null) {
                System.out.println("Đang gửi email HỦY lịch tới: " + taiKhoanBenhNhan.getEmail());
                // Gọi hàm EmailUtils (sẽ tạo ở Bước 2)
                EmailUtils.sendAppointmentCancellationEmail(
                        taiKhoanBenhNhan.getEmail(),
                        toDTO(lichHen) // Chuyển entity đã cập nhật sang DTO
                );
            } else {
                System.err.println("Không thể gửi email hủy: Bệnh nhân " + benhNhan.getHoTen() + " không có email hoặc tài khoản.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("LỖI GỬI EMAIL: Hủy lịch vẫn thành công, nhưng gửi email thất bại. " + e.getMessage());
        }
    }

    /**
     * Lấy tất cả lịch hẹn của một bệnh nhân (tải đủ quan hệ).
     */
    public List<LichHenDTO> getLichHenByBenhNhan(int benhNhanId) throws Exception {
        if (benhNhanDAO.getById(benhNhanId) == null) {
            throw new Exception("Không tìm thấy Bệnh nhân với ID: " + benhNhanId);
        }
        List<LichHen> entities = lichHenDAO.findByBenhNhanId(benhNhanId);

        // Sửa lại: Dùng vòng lặp for an toàn
        List<LichHenDTO> dtos = new ArrayList<>();
        for (LichHen entity : entities) {
            dtos.add(toDTO(entity));
        }
        return dtos;
    }

    // (KẾT THÚC KHỐI CODE CỦA QUANG)
} // Kết thúc class
