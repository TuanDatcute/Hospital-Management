package service;

import model.Entity.BenhNhan;
import model.Entity.DonThuoc;
import model.Entity.NhanVien;
import model.Entity.PhieuKhamBenh;
import model.dao.BenhNhanDAO;
import model.dao.DonThuocDAO; // Giả sử bạn có DonThuocDAO
import model.dao.NhanVienDAO;
import model.dao.PhieuKhamBenhDAO;
import model.dto.DonThuocDTO;   // Import DTO tương ứng
import model.dto.PhieuKhamBenhDTO;

/**
 * Lớp Service cho Phiếu Khám Bệnh. Chứa tất cả logic nghiệp vụ liên quan đến
 * việc tạo, cập nhật, và xử lý phiếu khám.
 */
public class PhieuKhamBenhService {

    // Khai báo các DAO một lần ở cấp lớp để tái sử dụng
    private final PhieuKhamBenhDAO phieuKhamDAO = new PhieuKhamBenhDAO();
    private final BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    /**
     * Xử lý nghiệp vụ tạo một phiếu khám bệnh mới.
     *
     * @param dto Dữ liệu phiếu khám từ Controller.
     * @return DTO của phiếu khám đã được tạo, hoặc null nếu thất bại.
     * @throws Exception nếu có lỗi nghiệp vụ.
     */
    public PhieuKhamBenhDTO createEncounter(PhieuKhamBenhDTO dto) throws Exception {

        // --- BƯỚC 1: LOGIC NGHIỆP VỤ (VALIDATION) ---
        if (phieuKhamDAO.isMaPhieuKhamExisted(dto.getMaPhieuKham())) {
            throw new Exception("Mã phiếu khám '" + dto.getMaPhieuKham() + "' đã tồn tại.");
        }

        // Lấy các đối tượng Entity liên quan từ CSDL
        BenhNhan benhNhanEntity = benhNhanDAO.getById(dto.getBenhNhanId());
        if (benhNhanEntity == null) {
            throw new Exception("Không tìm thấy bệnh nhân với ID: " + dto.getBenhNhanId());
        }

        NhanVien nhanVienEntity = nhanVienDAO.getById(dto.getBacSiId());
        if (nhanVienEntity == null) {
            throw new Exception("Không tìm thấy nhân viên (bác sĩ) với ID: " + dto.getBacSiId());
        }

        // --- BƯỚC 2: CHUYỂN ĐỔI DTO -> ENTITY ---
        // Truyền các Entity đã lấy được vào hàm toEntity
        PhieuKhamBenh entity = toEntity(dto, benhNhanEntity, nhanVienEntity);

        // --- BƯỚC 3: GỌI DAO ĐỂ LƯU ---
        PhieuKhamBenh savedEntity = phieuKhamDAO.create(entity);

        // --- BƯỚC 4: CHUYỂN ĐỔI ENTITY -> DTO ĐỂ TRẢ VỀ ---
        if (savedEntity != null) {
            return toDTO(savedEntity);
        }

        return null;
    }

    /**
     * Chuyển đổi từ DTO sang Entity. Hàm này chỉ làm nhiệm vụ gán giá trị,
     * không truy vấn CSDL.
     */
    private PhieuKhamBenh toEntity(PhieuKhamBenhDTO dto, BenhNhan benhNhan, NhanVien nhanVien) {
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

        // Gán các đối tượng Entity đã được lấy từ trước
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
        // SỬA LỖI: Gán dữ liệu từ Entity sang DTO
        dto.setChanDoan(entity.getChanDoan());
        dto.setKetLuan(entity.getKetLuan());

        if (entity.getBenhNhan() != null) {
            dto.setBenhNhanId(entity.getBenhNhan().getId());
            dto.setTenBenhNhan(entity.getBenhNhan().getHoTen());
        }
        if (entity.getBacSi() != null) {
            dto.setBacSiId(entity.getBacSi().getId());
            dto.setTenBacSi(entity.getBacSi().getHoTen());
        }
         if (entity.getDonThuoc() != null) {
             DonThuocService donThuoc = new DonThuocService();
            dto.setDonThuoc(donThuoc.toDTO(entity.getDonThuoc()));

        }
        return dto;
    }
}
