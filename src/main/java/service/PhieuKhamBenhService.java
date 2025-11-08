package service;

import exception.ValidationException;
import static java.rmi.server.LogStream.log;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import model.Entity.BenhNhan;
import model.Entity.ChiDinhDichVu;
import model.Entity.LichHen;
import model.Entity.NhanVien;
import model.Entity.PhieuKhamBenh;
import model.dao.BenhNhanDAO;
import model.dao.LichHenDAO;
import model.dao.NhanVienDAO;
import model.dao.PhieuKhamBenhDAO;
import model.dto.ChiDinhDichVuDTO;
import model.dto.PhieuKhamBenhDTO;
import model.dto.TaiKhoanDTO;
import util.EmailUtils;

/**
 * Lớp Service cho Phiếu Khám Bệnh. Chứa tất cả logic nghiệp vụ liên quan đến
 * việc tạo, cập nhật, và xử lý phiếu khám.
 */
public class PhieuKhamBenhService {

    // Khai báo các DAO và Service cần thiết một lần ở cấp lớp để tái sử dụng
    private final PhieuKhamBenhDAO phieuKhamDAO = new PhieuKhamBenhDAO();
    private final BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final DonThuocService donThuocService = new DonThuocService();
    private final ChiDinhDichVuService chiDinhService = new ChiDinhDichVuService();
    private final LichHenDAO lichHenDAO = new LichHenDAO();
    private final TaiKhoanService taiKhoanService = new TaiKhoanService();

    /**
     * Xử lý nghiệp vụ tạo một phiếu khám bệnh mới.
     *
     * @param dto Dữ liệu phiếu khám từ Controller.
     * @return DTO của phiếu khám đã được tạo.
     * @throws ValidationException nếu có lỗi nghiệp vụ (mã trùng, không tìm
     * thấy bệnh nhân...).
     */
    public PhieuKhamBenhDTO createEncounter(PhieuKhamBenhDTO dto) throws ValidationException {
        // --- BƯỚC 1: TẠO MÃ PHIẾU KHÁM TỰ ĐỘNG ---
        LocalDate today = LocalDate.now();
        // Định dạng ngày, ví dụ: 20251027
        String dateStr = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        // Đếm số phiếu đã tạo trong ngày hôm nay
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();
        long count = phieuKhamDAO.countByDateRange(startOfDay, endOfDay);

        // Tạo số thứ tự mới (ví dụ: 001, 002, 010)
        String sequenceStr = String.format("%03d", count + 1);

        // Ghép lại thành mã hoàn chỉnh
        String newMaPhieuKham = "PK" + dateStr + sequenceStr;
        dto.setMaPhieuKham(newMaPhieuKham); // Gán mã mới vào DTO

        BenhNhan benhNhanEntity = benhNhanDAO.getById(dto.getBenhNhanId());
        if (benhNhanEntity == null) {
            throw new ValidationException("Không tìm thấy bệnh nhân với ID: " + dto.getBenhNhanId());
        }

        NhanVien nhanVienEntity = nhanVienDAO.getById(dto.getBacSiId());
        if (nhanVienEntity == null) {
            throw new ValidationException("Không tìm thấy nhân viên (bác sĩ) với ID: " + dto.getBacSiId());
        }

        LichHen lh = null;
        if (dto.getLichHenId() != null) {
            lh = new LichHenDAO().getById(dto.getLichHenId());
        }

        // --- BƯỚC 2: CHUYỂN ĐỔI DTO -> ENTITY ---
        PhieuKhamBenh entity = toEntity(dto, benhNhanEntity, nhanVienEntity, lh);

        // --- BƯỚC 3: GỌI DAO ĐỂ LƯU ---
        PhieuKhamBenh savedEntity = phieuKhamDAO.create(entity);

        // --- BƯỚC 4: CẬP NHẬT TRẠNG THÁI LỊCH HẸN (NẾU CÓ) ✨ ---
        if (dto.getLichHenId() != null) {
            try {
                // Giả sử LichHenService được tiêm vào (hoặc new)
                LichHenService lichHenService = new LichHenService();
                lichHenService.updateAppointmentStatus(dto.getLichHenId(), "DA_DEN_KHAM");
            } catch (ValidationException e) {
                // Ghi log lỗi cập nhật lịch hẹn, nhưng không làm sập giao dịch chính
                throw new ValidationException("Lỗi khi cập nhật trạng thái Lịch hẹn: " + e.getMessage());
            }
        }

        // --- BƯỚC 5: CHUYỂN ĐỔI ENTITY -> DTO ĐỂ TRẢ VỀ ---
        return toDTO(savedEntity);
    }

    public PhieuKhamBenhDTO getEncounterById(int id) {
        PhieuKhamBenh entity = phieuKhamDAO.getEncounterById(id);
        return toDTO(entity); // Sử dụng lại hàm toDTO đã có
    }

    public List<PhieuKhamBenhDTO> getAllEncounters() {
        List<PhieuKhamBenh> entities = phieuKhamDAO.getAll();
        // Chuyển đổi danh sách Entity sang danh sách DTO
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<PhieuKhamBenhDTO> searchEncounters(String keyword) {
        // Gọi hàm tìm kiếm gọn nhẹ
        List<PhieuKhamBenh> entities = phieuKhamDAO.searchForListing(keyword);
        return entities.stream().map(this::toDTOForListing).collect(Collectors.toList());
    }

    /**
     * NGHIỆP VỤ: Cập nhật trạng thái của phiếu khám thành "HOAN_THANH". Chỉ
     * thực hiện được khi tất cả các dịch vụ chỉ định đã ở trạng thái
     * "HOAN_THANH".
     *
     * @param phieuKhamId ID của phiếu khám cần hoàn thành.
     * @return DTO của phiếu khám sau khi cập nhật.
     * @throws ValidationException nếu có lỗi nghiệp vụ.
     */
    public PhieuKhamBenhDTO completeEncounterStatus(int phieuKhamId) throws ValidationException {
        // 1. Lấy Entity đầy đủ từ CSDL (bao gồm cả danh sách chỉ định)
        PhieuKhamBenh existingEntity = phieuKhamDAO.getDetailsByIdToUpdateStatus(phieuKhamId);
        if (existingEntity == null) {
            throw new ValidationException("Không tìm thấy phiếu khám để hoàn thành.");
        }

        Set<ChiDinhDichVu> danhSachChiDinh = existingEntity.getDanhSachChiDinh();

        // Nếu có danh sách chỉ định, kiểm tra từng mục
        if (danhSachChiDinh != null && !danhSachChiDinh.isEmpty()) {
            // Dùng Stream API để kiểm tra xem "tất cả" có khớp điều kiện không
            boolean allServicesCompleted = danhSachChiDinh.stream()
                    .allMatch(chiDinh -> ("HOAN_THANH".equals(chiDinh.getTrangThai())) || "DA_HUY".equals(chiDinh.getTrangThai()));

            if (!allServicesCompleted) {
                throw new ValidationException("Không thể hoàn thành phiếu khám vì còn dịch vụ chưa hoàn tất.");
            }
        }
        // Nếu không có dịch vụ nào, có thể cho phép hoàn thành (tùy nghiệp vụ)

        // 3. Cập nhật trạng thái
        existingEntity.setTrangThai("HOAN_THANH");

        // 4. Gọi DAO để lưu thay đổi
        phieuKhamDAO.update(existingEntity);

        PhieuKhamBenhDTO dto = toDTO(existingEntity);
        try {
            // Lấy email của bệnh nhân từ tài khoản của họ
            TaiKhoanDTO benhNhanAccount = taiKhoanService.getTaiKhoanByBenhNhanId(dto.getBenhNhanId());
            if (benhNhanAccount != null && benhNhanAccount.getEmail() != null) {
                String toEmail = benhNhanAccount.getEmail();
                // Chạy gửi email trên một luồng riêng để không làm chậm request
                new Thread(() -> {
                    try {
                        EmailUtils.sendEncounterCompletedEmail(toEmail, dto);
                    } catch (Exception e) {
                        log("Lỗi ngầm khi gửi email: " + e.getMessage());
                    }
                }).start();
            }
        } catch (Exception e) {
            log("Lỗi khi tìm email bệnh nhân: " + e.getMessage());
        }
        // 5. Trả về DTO đã cập nhật
        return toDTOForUpdateStatus(existingEntity); // Giả sử có hàm toDTO đầy đủ
    }

    /**
     * NGHIỆP VỤ: Cập nhật thông tin chi tiết cho một phiếu khám đã tồn tại.
     *
     * @param dto DTO chứa thông tin mới cần cập nhật. ID trong DTO phải tồn
     * tại.
     * @return DTO của phiếu khám sau khi đã được cập nhật.
     * @throws ValidationException nếu có lỗi nghiệp vụ (không tìm thấy phiếu
     * khám, bác sĩ không hợp lệ...).
     */
    public PhieuKhamBenhDTO updateEncounter(PhieuKhamBenhDTO dto) throws ValidationException {

        // --- BƯỚC 1: LẤY BẢN GHI GỐC TỪ CSDL ---
        PhieuKhamBenh existingEntity = phieuKhamDAO.getEncounterById(dto.getId());
        if (existingEntity.getTrangThai().equals("HOAN_THANH")) {
            throw new ValidationException("Không thể sửa phiếu khám đã hoàn thành");

        }
        if (existingEntity == null) {
            throw new ValidationException("Không tìm thấy phiếu khám để cập nhật.");
        }

        // --- BƯỚC 2: CẬP NHẬT CÁC TRƯỜNG DỮ LIỆU ĐƠN GIẢN ---
        // Các thông tin này có thể được bác sĩ chỉnh sửa sau khi đã tạo phiếu.
        existingEntity.setThoiGianKham(dto.getThoiGianKham());
        existingEntity.setTrieuChung(dto.getTrieuChung());
        existingEntity.setNhietDo(dto.getNhietDo());
        existingEntity.setHuyetAp(dto.getHuyetAp());
        existingEntity.setNhipTim(dto.getNhipTim());
        existingEntity.setNhipTho(dto.getNhipTho());
        existingEntity.setChanDoan(dto.getChanDoan());
        existingEntity.setKetLuan(dto.getKetLuan());
        existingEntity.setNgayTaiKham(dto.getNgayTaiKham());

        // Lưu ý: Không cập nhật maPhieuKham và benhNhan vì đây là các thông tin cố định.
        // --- BƯỚC 3: CẬP NHẬT CÁC MỐI QUAN HỆ (NẾU CÓ THAY ĐỔI) ---
        // Cập nhật bác sĩ (nếu ID bác sĩ trong DTO khác với ID bác sĩ hiện tại)
        if (existingEntity.getBacSi().getId() != dto.getBacSiId()) {
            NhanVien newBacSi = nhanVienDAO.getById(dto.getBacSiId());
            if (newBacSi == null) {
                throw new ValidationException("Bác sĩ mới không hợp lệ.");
            }
            existingEntity.setBacSi(newBacSi);
        }

        // Cập nhật lịch hẹn (nếu có)
        // Xử lý cả 3 trường hợp: gán mới, thay đổi, hoặc xóa bỏ lịch hẹn
        Integer lichHenIdMoi = dto.getLichHenId();
        LichHen lichHenHienTai = existingEntity.getLichHen();

        if (lichHenIdMoi != null) {
            if (lichHenHienTai == null || lichHenHienTai.getId() != lichHenIdMoi) {
                LichHen newLichHen = lichHenDAO.getById(lichHenIdMoi);
                if (newLichHen == null) {
                    throw new ValidationException("Lịch hẹn liên kết không hợp lệ.");
                }
                existingEntity.setLichHen(newLichHen);
            }
        } else {
            // Nếu DTO gửi lên lichHenId là null, ta sẽ xóa bỏ liên kết
            existingEntity.setLichHen(null);
        }

        // --- BƯỚC 4: GỌI DAO ĐỂ LƯU THAY ĐỔI ---
        phieuKhamDAO.update(existingEntity);

        // --- BƯỚC 5: TRẢ VỀ DTO ĐÃ ĐƯỢC CẬP NHẬT ---
        return toDTO(existingEntity);
    }

    /**
     * HÀM MỚI: Lấy các phiếu khám chưa có hóa đơn (để tìm kiếm)
     */
    public List<PhieuKhamBenhDTO> getUninvoicedEncounters(String keyword) {
        List<PhieuKhamBenh> entities;
        if (keyword == null || keyword.trim().isEmpty()) {
            entities = phieuKhamDAO.findUninvoiced();
        } else {
            entities = phieuKhamDAO.findUninvoicedByKeyword(keyword);
        }

        // Tạo một danh sách mới để chứa các DTO
        List<PhieuKhamBenhDTO> dtos = new ArrayList<>();

        // Lặp qua từng 'entity' trong danh sách 'entities'
        for (PhieuKhamBenh entity : entities) {
            // Chuyển đổi 'entity' thành DTO và thêm vào danh sách 'dtos'
            dtos.add(this.toDTO(entity));
        }

        // Trả về danh sách DTO đã hoàn chỉnh
        return dtos;
    }

    /**
     * Lấy tất cả phiếu khám cho một bác sĩ cụ thể.
     */
    public List<PhieuKhamBenhDTO> getAllEncountersForDoctor(int bacSiId) {
        List<PhieuKhamBenh> entities = phieuKhamDAO.getAllForDoctor(bacSiId);
        return entities.stream()
                .map(this::toDTOForListing)
                .collect(Collectors.toList());
    }

    /**
     * Tìm kiếm phiếu khám cho một bác sĩ cụ thể.
     */
    public List<PhieuKhamBenhDTO> searchEncountersForDoctor(String keyword, int bacSiId) {
        List<PhieuKhamBenh> entities = phieuKhamDAO.searchForDoctor(keyword, bacSiId);
        return entities.stream()
                .map(this::toDTOForListing)
                .collect(Collectors.toList());
    }

    /**
     * Chuyển đổi từ DTO sang Entity.
     */
    private PhieuKhamBenh toEntity(PhieuKhamBenhDTO dto, BenhNhan benhNhan, NhanVien nhanVien, LichHen lichHen) {
        PhieuKhamBenh entity = new PhieuKhamBenh();
        entity.setMaPhieuKham(dto.getMaPhieuKham());
        entity.setThoiGianKham(dto.getThoiGianKham());
        entity.setTrieuChung(dto.getTrieuChung());
        entity.setNhietDo(dto.getNhietDo());
        entity.setHuyetAp(dto.getHuyetAp());
        entity.setNhipTim(dto.getNhipTim());
        entity.setNhipTho(dto.getNhipTho());
        entity.setChanDoan(dto.getChanDoan());
        entity.setKetLuan(dto.getKetLuan());
        entity.setTrangThai(dto.getTrangThai());
        if (lichHen != null) {
            entity.setLichHen(lichHen);
        }
        entity.setBenhNhan(benhNhan);
        entity.setBacSi(nhanVien);

        return entity;
    }

    private PhieuKhamBenhDTO toDTOForUpdateStatus(PhieuKhamBenh entity) {
        if (entity == null) {
            return null;
        }
        PhieuKhamBenhDTO dto = new PhieuKhamBenhDTO();
        dto.setId(entity.getId());
        dto.setMaPhieuKham(entity.getMaPhieuKham());
        if (entity.getDanhSachChiDinh() != null && !entity.getDanhSachChiDinh().isEmpty()) {

            List<ChiDinhDichVuDTO> chiDinhDTOs = entity.getDanhSachChiDinh().stream()
                    .map(chiDinhService::toDTO)
                    .collect(Collectors.toList());
            dto.setDanhSachChiDinh(chiDinhDTOs);
        }
        return dto;
    }

    /**
     * Chuyển đổi một Entity PhieuKhamBenh sang một DTO "gọn nhẹ", chỉ chứa các
     * thông tin cần thiết để hiển thị trên danh sách.
     *
     * @param entity Đối tượng PhieuKhamBenh (đã fetch sẵn BenhNhan và BacSi).
     * @return Một PhieuKhamBenhDTO với các thông tin cơ bản.
     */
    private PhieuKhamBenhDTO toDTOForListing(PhieuKhamBenh entity) {
        if (entity == null) {
            return null;
        }

        PhieuKhamBenhDTO dto = new PhieuKhamBenhDTO();
        dto.setId(entity.getId());
        dto.setMaPhieuKham(entity.getMaPhieuKham());
        dto.setThoiGianKham(entity.getThoiGianKham());
        dto.setChanDoan(entity.getChanDoan());
        dto.setTrangThai(entity.getTrangThai());
        // Lấy thông tin từ các đối tượng liên quan đã được JOIN FETCH
        if (entity.getBenhNhan() != null) {
            dto.setTenBenhNhan(entity.getBenhNhan().getHoTen());
        }
        if (entity.getBacSi() != null) {
            dto.setTenBacSi(entity.getBacSi().getHoTen());
        }

        return dto;
    }

    /**
     * Chuyển đổi từ Entity sang DTO.
     */
    private PhieuKhamBenhDTO toDTO(PhieuKhamBenh entity) {
        if (entity == null) {
            return null;
        }

        PhieuKhamBenhDTO dto = new PhieuKhamBenhDTO();
        dto.setId(entity.getId());
        dto.setMaPhieuKham(entity.getMaPhieuKham());
        dto.setThoiGianKham(entity.getThoiGianKham());
        dto.setTrieuChung(entity.getTrieuChung());
        dto.setNhietDo(entity.getNhietDo());
        dto.setHuyetAp(entity.getHuyetAp());
        dto.setNhipTim(entity.getNhipTim());
        dto.setNhipTho(entity.getNhipTho());
        dto.setChanDoan(entity.getChanDoan());
        dto.setKetLuan(entity.getKetLuan());
        dto.setTrangThai(entity.getTrangThai());

        if (entity.getLichHen() != null) {
            dto.setLichHenId(entity.getLichHen().getId());
        }

        if (entity.getNgayTaiKham() != null) {
            dto.setNgayTaiKham(entity.getNgayTaiKham());
        }
        if (entity.getBenhNhan() != null) {
            dto.setBenhNhanId(entity.getBenhNhan().getId());
            dto.setTenBenhNhan(entity.getBenhNhan().getHoTen());
        }
        if (entity.getBacSi() != null) {
            dto.setBacSiId(entity.getBacSi().getId());
            dto.setTenBacSi(entity.getBacSi().getHoTen());
        }
        if (entity.getDonThuoc() != null) {
            dto.setDonThuoc(donThuocService.toDTO(entity.getDonThuoc()));
        }
        if (entity.getDanhSachChiDinh() != null && !entity.getDanhSachChiDinh().isEmpty()) {

            List<ChiDinhDichVuDTO> chiDinhDTOs = entity.getDanhSachChiDinh().stream()
                    .map(chiDinhService::toDTO)
                    .collect(Collectors.toList());
            dto.setDanhSachChiDinh(chiDinhDTOs);
        }
        return dto;
    }

    //====================Dat=============
    // Trong file: service/PhieuKhamBenhService.java
    public List<PhieuKhamBenhDTO> getHistoryForPatient(int benhNhanId) {
        List<PhieuKhamBenh> entities = phieuKhamDAO.findByPatientId(benhNhanId);

        // Dùng hàm toDTOForListing (gọn nhẹ) để hiển thị danh sách
        return entities.stream()
                .map(this::toDTOForListing)
                .collect(Collectors.toList());
    }
}
