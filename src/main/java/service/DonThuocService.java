package service;

import model.dao.DonThuocDAO;
import model.dao.PhieuKhamBenhDAO;
import model.dao.ThuocDAO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import model.dto.ChiTietDonThuocDTO;
import model.dto.DonThuocDTO;
import model.Entity.ChiTietDonThuoc;
import model.Entity.DonThuoc;
import model.Entity.PhieuKhamBenh;
import model.Entity.Thuoc;

public class DonThuocService {

    private final DonThuocDAO donThuocDAO = new DonThuocDAO();
    private final PhieuKhamBenhDAO phieuKhamDAO = new PhieuKhamBenhDAO();
    private final ThuocDAO thuocDAO = new ThuocDAO();

    /**
     * NGHIỆP VỤ: Tạo một đơn thuốc hoàn chỉnh, bao gồm cả các thuốc chi tiết.
     * Đây là một nghiệp vụ phức tạp, bao gồm kiểm tra, tạo và cập nhật nhiều
     * bảng.
     *
     * @param dto Dữ liệu đơn thuốc từ Controller, bao gồm cả danh sách thuốc.
     * @return DTO của đơn thuốc sau khi tạo thành công.
     * @throws Exception nếu có lỗi nghiệp vụ (phiếu khám không tồn tại, thuốc
     * hết hàng...).
     */
    public DonThuocDTO createPrescription(DonThuocDTO dto) throws Exception {

        // 1. Lấy và kiểm tra các Entity liên quan
        PhieuKhamBenh phieuKham = phieuKhamDAO.getEncounterById(dto.getPhieuKhamId());
        if (phieuKham == null) {
            throw new Exception("Không tìm thấy phiếu khám với ID: " + dto.getPhieuKhamId());
        }

        // 2. Tạo đối tượng DonThuoc (Entity) cha
        DonThuoc donThuocEntity = new DonThuoc();
        donThuocEntity.setPhieuKham(phieuKham);
        donThuocEntity.setLoiDan(dto.getLoiDan());
        donThuocEntity.setNgayKeDon(LocalDateTime.now());
        donThuocEntity.setChiTietDonThuoc(new ArrayList<>()); // Khởi tạo danh sách chi tiết

        // 3. Xử lý các ChiTietDonThuoc (Entity) con
        for (ChiTietDonThuocDTO chiTietDTO : dto.getChiTietDonThuoc()) {
            Thuoc thuoc = thuocDAO.getById(chiTietDTO.getThuocId());
            if (thuoc == null) {
                throw new Exception("Không tìm thấy thuốc với ID: " + chiTietDTO.getThuocId());
            }

            // Logic nghiệp vụ: Kiểm tra tồn kho
            if (thuoc.getSoLuongTonKho() < chiTietDTO.getSoLuong()) {
                throw new Exception("Không đủ số lượng tồn kho cho thuốc: " + thuoc.getTenThuoc());
            }

            // Cập nhật lại số lượng tồn kho
            thuoc.setSoLuongTonKho(thuoc.getSoLuongTonKho() - chiTietDTO.getSoLuong());

            // Tạo đối tượng ChiTietDonThuoc (Entity) con
            ChiTietDonThuoc chiTietEntity = new ChiTietDonThuoc();
            chiTietEntity.setThuoc(thuoc);
            chiTietEntity.setSoLuong(chiTietDTO.getSoLuong());
            chiTietEntity.setLieuDung(chiTietDTO.getLieuDung());
            chiTietEntity.setDonThuoc(donThuocEntity); // Liên kết ngược lại với cha

            donThuocEntity.getChiTietDonThuoc().add(chiTietEntity);
        }

        // 4. Gọi DAO để lưu (Nhờ CascadeType.ALL, Hibernate sẽ tự động lưu cả cha và con)
        DonThuoc savedDonThuoc = donThuocDAO.create(donThuocEntity);

        return toDTO(savedDonThuoc);
    }

    public DonThuocDTO getPrescriptionDetails(int donThuocId) {
        DonThuoc entity = donThuocDAO.getById(donThuocId);
        return toDTO(entity);
    }

    // --- Phương thức chuyển đổi ---
    protected DonThuocDTO toDTO(DonThuoc entity) {
        if (entity == null) {
            return null;
        }

        DonThuocDTO dto = new DonThuocDTO();
        dto.setId(entity.getId());
        dto.setNgayKeDon(entity.getNgayKeDon());
        dto.setLoiDan(entity.getLoiDan());

        if (entity.getPhieuKham() != null) {
            dto.setPhieuKhamId(entity.getPhieuKham().getId());
        }

        // Chuyển đổi danh sách con
        if (entity.getChiTietDonThuoc() != null) {
            List<ChiTietDonThuocDTO> chiTietDTOs = entity.getChiTietDonThuoc().stream()
                    .map(this::toChiTietDTO)
                    .collect(Collectors.toList());
            dto.setChiTietDonThuoc(chiTietDTOs);
        }
        return dto;
    }

    private ChiTietDonThuocDTO toChiTietDTO(ChiTietDonThuoc entity) {
        ChiTietDonThuocDTO dto = new ChiTietDonThuocDTO();
        dto.setId(entity.getId());
        dto.setSoLuong(entity.getSoLuong());
        dto.setLieuDung(entity.getLieuDung());
        if (entity.getThuoc() != null) {
            dto.setThuocId(entity.getThuoc().getId());
        }
        // Thêm các thông tin khác của thuốc nếu cần hiển thị
        return dto;
    }
}
