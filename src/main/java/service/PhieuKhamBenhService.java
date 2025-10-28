package service;

import exception.ValidationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import model.Entity.BenhNhan;
import model.Entity.LichHen;
import model.Entity.NhanVien;
import model.Entity.PhieuKhamBenh;
import model.dao.BenhNhanDAO;
import model.dao.LichHenDAO;
import model.dao.NhanVienDAO;
import model.dao.PhieuKhamBenhDAO;
import model.dto.ChiDinhDichVuDTO;
import model.dto.PhieuKhamBenhDTO;

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

    /**
     * Xử lý nghiệp vụ tạo một phiếu khám bệnh mới.
     *
     * @param dto Dữ liệu phiếu khám từ Controller.
     * @return DTO của phiếu khám đã được tạo.
     * @throws ValidationException nếu có lỗi nghiệp vụ (mã trùng, không tìm
     * thấy bệnh nhân...).
     */
    public PhieuKhamBenhDTO createEncounter(PhieuKhamBenhDTO dto) throws ValidationException {

        // --- BƯỚC 1: LOGIC NGHIỆP VỤ (VALIDATION) ---
        if (dto.getMaPhieuKham() == null || dto.getMaPhieuKham().trim().isEmpty()) {
            throw new ValidationException("Mã phiếu khám không được để trống.");
        }
        if (phieuKhamDAO.isMaPhieuKhamExisted(dto.getMaPhieuKham())) {
            throw new ValidationException("Mã phiếu khám '" + dto.getMaPhieuKham() + "' đã tồn tại.");
        }
        
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

        // --- BƯỚC 4: CHUYỂN ĐỔI ENTITY -> DTO ĐỂ TRẢ VỀ ---
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
    
    public PhieuKhamBenhDTO getEncounterDetailsById(int id) {
        // Gọi hàm đầy đủ để xem chi tiết
        PhieuKhamBenh entity = phieuKhamDAO.getEncounterById(id);
        return toDTO(entity); // Một hàm toDTO khác, chi tiết hơn
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
}
