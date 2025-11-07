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
import model.dto.NhanVienDTO;
import service.NhanVienService;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Lớp Service chứa logic nghiệp vụ cho LichHen.
 *
 * @author ADMIN (Đã CẬP NHẬT: Thêm CRUD Sửa và Phân trang)
 */
public class LichHenService {

    private final LichHenDAO lichHenDAO = new LichHenDAO();
    private final BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final NhanVienService nhanVienService = new NhanVienService();

    private static final List<String> VALID_TRANG_THAI = Arrays.asList(
            "CHO_XAC_NHAN", "DA_XAC_NHAN", "HOAN_THANH", "DA_HUY", "DA_DEN_KHAM"
    );

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

        BenhNhan benhNhanEntity = benhNhanDAO.getById(dto.getBenhNhanId());
        if (benhNhanEntity == null) {
            throw new Exception("Không tìm thấy Bệnh nhân với ID: " + dto.getBenhNhanId());
        }

        NhanVienDTO bacSiDTO;
        try {
            bacSiDTO = nhanVienService.getNhanVienById(dto.getBacSiId());
        } catch (Exception e) {
            throw new Exception("Không tìm thấy Bác sĩ đang hoạt động với ID: " + dto.getBacSiId());
        }

        NhanVien bacSiEntity = nhanVienDAO.getById(dto.getBacSiId());
        if (bacSiEntity == null) {
            throw new Exception("Lỗi không mong muốn: Không thể lấy entity Bác sĩ ID: " + dto.getBacSiId());
        }

        LichHen entity = toEntity(dto, benhNhanEntity, bacSiEntity);
        entity.setTrangThai("CHO_XAC_NHAN");
        LichHen savedEntity = lichHenDAO.create(entity);

        if (savedEntity != null) {
            LichHen fullSavedEntity = lichHenDAO.getByIdWithRelations(savedEntity.getId());
            return toDTO(fullSavedEntity);
        }
        throw new Exception("Không thể tạo lịch hẹn.");
    }

    /**
     * Dịch vụ cập nhật thông tin Lịch hẹn (cho Admin).
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

        LichHen existingEntity = lichHenDAO.getById(lichHenId);
        if (existingEntity == null) {
            throw new ValidationException("Không tìm thấy lịch hẹn với ID: " + lichHenId);
        }

        if (existingEntity.getBenhNhan().getId() != dto.getBenhNhanId()) {
            BenhNhan newBenhNhan = benhNhanDAO.getById(dto.getBenhNhanId());
            if (newBenhNhan == null) {
                throw new ValidationException("Không tìm thấy Bệnh nhân mới (ID: " + dto.getBenhNhanId() + ").");
            }
            existingEntity.setBenhNhan(newBenhNhan);
        }

        if (existingEntity.getBacSi().getId() != dto.getBacSiId()) {
            NhanVien newBacSi = nhanVienDAO.getById(dto.getBacSiId());
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
     * Dịch vụ cập nhật trạng thái của một Lịch hẹn.
     */
    public LichHenDTO updateTrangThaiLichHen(int lichHenId, String newTrangThai, String ghiChu) throws Exception {
        if (newTrangThai == null || newTrangThai.trim().isEmpty()) {
            throw new Exception("Trạng thái mới không được để trống.");
        }
        if (!VALID_TRANG_THAI.contains(newTrangThai)) {
            throw new Exception("Trạng thái '" + newTrangThai + "' không hợp lệ.");
        }
        LichHen existingEntity = lichHenDAO.getById(lichHenId);
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

    public LichHenDTO getLichHenById(int id) throws Exception {
        LichHen entity = lichHenDAO.getByIdWithRelations(id);
        if (entity == null) {
            throw new Exception("Không tìm thấy lịch hẹn với ID: " + id);
        }
        return toDTO(entity);
    }

    public List<LichHenDTO> getAllLichHenPaginated(int page, int pageSize) {
        List<LichHen> entities = lichHenDAO.getAllWithRelations(page, pageSize);
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public long getLichHenCount() {
        return lichHenDAO.getTotalLichHenCount();
    }

    public List<LichHenDTO> getLichHenByBacSi(int bacSiId) throws Exception {
        try {
            nhanVienService.getNhanVienById(bacSiId);
        } catch (Exception e) {
            throw new Exception("Không tìm thấy Bác sĩ đang hoạt động với ID: " + bacSiId);
        }

        List<LichHen> entities = lichHenDAO.findByBacSiId(bacSiId);
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<LichHenDTO> getLichHenByBenhNhan(int benhNhanId) throws Exception {
        if (benhNhanDAO.getById(benhNhanId) == null) {
            throw new Exception("Không tìm thấy Bệnh nhân với ID: " + benhNhanId);
        }
        List<LichHen> entities = lichHenDAO.findByBenhNhanId(benhNhanId);
        List<LichHenDTO> dtos = new ArrayList<>();
        for (LichHen entity : entities) {
            dtos.add(toDTO(entity));
        }
        return dtos;
    }

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
            dto.setTenBenhNhan(entity.getBenhNhan().getHoTen());
        }
        if (entity.getBacSi() != null) {
            dto.setBacSiId(entity.getBacSi().getId());
            dto.setTenBacSi(entity.getBacSi().getHoTen());
        }
        return dto;
    }

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

    public LichHenDTO createAppointmentByNurse(LichHenDTO dto) throws ValidationException {
        if (dto.getThoiGianHen() == null) {
            throw new ValidationException("Thời gian hẹn không được để trống.");
        }
        if (dto.getBenhNhanId() <= 0) {
            throw new ValidationException("Vui lòng chọn bệnh nhân.");
        }
        if (dto.getBacSiId() <= 0) {
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

        LocalDate appointmentDate = dto.getThoiGianHen().toLocalDate();
        long count = lichHenDAO.countAppointmentsByDateAndDoctor(appointmentDate, bacSi.getId());
        int newStt = (int) count + 1;

        LichHen newLichHen = toEntity(dto, benhNhan, bacSi);
        newLichHen.setStt(newStt);
        newLichHen.setTrangThai("CHO_XAC_NHAN");

        LichHen savedEntity = lichHenDAO.create(newLichHen);
        return toDTO(savedEntity);
    }

    public void updateAppointmentStatus(Integer lichHenId, String newStatus) throws ValidationException {
        if (lichHenId == null) {
            return;
        }
        LichHen lichHen = lichHenDAO.getById(lichHenId);
        if (lichHen != null) {
            lichHen.setTrangThai(newStatus);
            lichHenDAO.update(lichHen);
        } else {
            throw new ValidationException("Không tìm thấy lịch hẹn với ID: " + lichHenId);
        }
    }

    public List<LichHenDTO> getAllPendingAppointments() {
        List<LichHen> entities = lichHenDAO.getAllPendingAppointments();
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<LichHenDTO> getPendingAppointmentsForDoctor(int bacSiId) {
        List<LichHen> entities = lichHenDAO.getPendingAppointmentsForDoctor(bacSiId);
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public LichHenDTO createAppointmentByPatient(LichHenDTO dto, int taiKhoanIdCuaBenhNhan) throws Exception {
        if (dto.getBacSiId() <= 0) {
            throw new ValidationException("Vui lòng chọn bác sĩ.");
        }
        if (dto.getThoiGianHen() == null || dto.getThoiGianHen().isBefore(OffsetDateTime.now())) {
            throw new ValidationException("Thời gian hẹn phải là một thời điểm trong tương lai.");
        }
        if (dto.getLyDoKham() == null || dto.getLyDoKham().trim().isEmpty()) {
            throw new ValidationException("Vui lòng nhập lý do khám.");
        }

        BenhNhan benhNhanEntity = benhNhanDAO.findByTaiKhoanId(taiKhoanIdCuaBenhNhan);
        if (benhNhanEntity == null) {
            throw new Exception("Không tìm thấy hồ sơ bệnh nhân tương ứng với tài khoản của bạn.");
        }

        NhanVien bacSiEntity = nhanVienDAO.getById(dto.getBacSiId());
        if (bacSiEntity == null) {
            throw new Exception("Không tìm thấy bác sĩ đã chọn.");
        }

        try {
            nhanVienService.getNhanVienById(dto.getBacSiId());
        } catch (Exception e) {
            throw new Exception("Bác sĩ bạn chọn hiện không hoạt động.");
        }

        LocalDate appointmentDate = dto.getThoiGianHen().toLocalDate();
        long count = lichHenDAO.countAppointmentsByDateAndDoctor(appointmentDate, bacSiEntity.getId());
        int newStt = (int) count + 1;

        LichHen entity = new LichHen();
        entity.setThoiGianHen(dto.getThoiGianHen());
        entity.setLyDoKham(dto.getLyDoKham().trim());
        entity.setGhiChu(dto.getGhiChu());
        entity.setBenhNhan(benhNhanEntity);
        entity.setBacSi(bacSiEntity);
        entity.setStt(newStt);
        entity.setTrangThai("CHO_XAC_NHAN");

        LichHen savedEntity = lichHenDAO.create(entity);
        LichHen fullSavedEntity = lichHenDAO.getByIdWithRelations(savedEntity.getId());
        return toDTO(fullSavedEntity);
    }
}
