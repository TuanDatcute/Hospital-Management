package service;

import exception.ValidationException;
import model.dao.ThuocDAO;
import model.dto.ThuocDTO;
import model.Entity.Thuoc;
import java.util.List;
import java.util.stream.Collectors;
import model.dao.ChiTietDonThuocDAO;

public class ThuocService {

    private final ThuocDAO thuocDAO = new ThuocDAO();
    private final ChiTietDonThuocDAO chiTietDonThuocDAO = new ChiTietDonThuocDAO(); // ✨ Khởi tạo DAO

    public ThuocDTO createMedication(ThuocDTO dto) throws ValidationException {
        if (dto.getTenThuoc() == null || dto.getTenThuoc().trim().isEmpty()) {
            throw new ValidationException("Tên thuốc không được để trống.");
        }
        if (thuocDAO.isTenThuocExisted(dto.getTenThuoc())) {
            throw new ValidationException("Tên thuốc '" + dto.getTenThuoc() + "' đã tồn tại.");
        }

        Thuoc entity = toEntity(dto);
        Thuoc savedEntity = thuocDAO.create(entity);
        return toDTO(savedEntity);
    }

    public ThuocDTO getMedicationById(int id) {
        Thuoc entity = thuocDAO.getById(id);
        return toDTO(entity);
    }

    public List<ThuocDTO> getAllMedications() {
        List<Thuoc> entities = thuocDAO.getAll();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }
    
    public List<ThuocDTO> getAllMedicationsActive() {
        List<Thuoc> entities = thuocDAO.getAllActive();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    public ThuocDTO updateMedicationInfo(int id, ThuocDTO dto) throws ValidationException {
        // --- Logic nghiệp vụ: Kiểm tra ---
        Thuoc existingEntity = thuocDAO.getById(id);
        if (existingEntity == null) {
            throw new ValidationException("Không tìm thấy thuốc với ID: " + id);
        }

        // Kiểm tra nếu tên mới đang được thay đổi và có trùng với thuốc khác không
        if (!existingEntity.getTenThuoc().equalsIgnoreCase(dto.getTenThuoc())) {
            if (thuocDAO.findToUpdateMed(dto.getTenThuoc()) != null) {
                throw new ValidationException("Tên thuốc '" + dto.getTenThuoc() + "' đã được sử dụng.");
            }
        }

        // --- Cập nhật thông tin từ DTO vào Entity đã có ---
        existingEntity.setTenThuoc(dto.getTenThuoc());
        existingEntity.setHoatChat(dto.getHoatChat());
        existingEntity.setDonViTinh(dto.getDonViTinh());
        existingEntity.setDonGia(dto.getDonGia());

        // --- Gọi DAO để cập nhật ---
        thuocDAO.update(existingEntity);

        return toDTO(existingEntity);
    }

    public void deleteMedication(int id) throws ValidationException {
        Thuoc existingThuoc = thuocDAO.getById(id);
        if (existingThuoc == null) {
            throw new ValidationException("Không tìm thấy thuốc để xóa.");
        }

        if (ChiTietDonThuocDAO.isMedicationInUse(id)) {
            throw new ValidationException("Không thể xóa thuốc này vì nó đang được sử dụng trong các đơn thuốc đã kê.");
        }
        thuocDAO.delete(id);
    }

    /**
     * NGHIỆP VỤ: Cập nhật số lượng tồn kho (nhập/xuất kho).
     *
     * @param id ID của thuốc.
     * @param quantityChange Số lượng thay đổi (số dương để nhập, số âm để
     * xuất).
     * @return DTO của thuốc sau khi cập nhật kho.
     */
    public ThuocDTO updateStockQuantity(int thuocId, int soLuongThayDoi) throws ValidationException {
        Thuoc thuoc = thuocDAO.getById(thuocId);
        if (thuoc == null) {
            throw new ValidationException("Không tìm thấy thuốc với ID: " + thuocId);
        }

        int soLuongMoi = thuoc.getSoLuongTonKho() + soLuongThayDoi;

        // Logic nghiệp vụ: Không cho phép tồn kho là số âm
        if (soLuongMoi < 0) {
            throw new ValidationException("Số lượng tồn kho không đủ để xuất. Tồn kho hiện tại: " + thuoc.getSoLuongTonKho());
        }

        thuoc.setSoLuongTonKho(soLuongMoi);
        thuocDAO.update(thuoc);

        return toDTO(thuoc);
    }

    public List<ThuocDTO> searchMedicationsByName(String name) throws Exception {
        // --- Logic nghiệp vụ: Kiểm tra đầu vào ---
        if (name == null || name.trim().isEmpty()) {
            throw new Exception("Tên thuốc tìm kiếm không được để trống.");
        }

        // --- Gọi DAO để thực hiện truy vấn ---
        List<Thuoc> entities = thuocDAO.findByName(name);

        // --- Chuyển đổi danh sách Entity sang danh sách DTO để trả về ---
        return entities.stream()
                .map(this::toDTO) // Sử dụng lại phương thức toDTO đã có
                .collect(Collectors.toList());
    }

    /**
     * ✨ HÀM MỚI ✨ NGHIỆP VỤ: Cập nhật trạng thái của một loại thuốc.
     *
     * @param id ID của thuốc cần cập nhật.
     * @param newStatus Trạng thái mới (ví dụ: "NGUNG_SU_DUNG").
     * @throws ValidationException nếu có lỗi nghiệp vụ.
     */
    public void updateThuocStatus(int id, String newStatus) throws ValidationException {
        // 1. Lấy Entity gốc
        Thuoc existingThuoc = thuocDAO.getById(id);
        if (existingThuoc == null) {
            throw new ValidationException("Không tìm thấy thuốc với ID: " + id);
        }

        // 2. Kiểm tra logic nghiệp vụ
        if ("NGUNG_SU_DUNG".equals(newStatus)) {
            // Kiểm tra xem thuốc có đang được sử dụng không
            if (chiTietDonThuocDAO.isMedicationInUse(id)) {
                throw new ValidationException("Không thể ngừng: Thuốc đang được sử dụng trong một đơn thuốc đã kê.");
            }
        }

        // 3. Cập nhật trạng thái
        existingThuoc.setTrangThai(newStatus);

        // 4. Gọi DAO để lưu thay đổi
        thuocDAO.update(existingThuoc);
    }

    // --- Phương thức chuyển đổi ---
    private Thuoc toEntity(ThuocDTO dto) {
        Thuoc entity = new Thuoc();
        entity.setTenThuoc(dto.getTenThuoc());
        entity.setHoatChat(dto.getHoatChat());
        entity.setDonViTinh(dto.getDonViTinh());
        entity.setDonGia(dto.getDonGia());
        entity.setSoLuongTonKho(dto.getSoLuongTonKho());
        dto.setTrangThai(entity.getTrangThai()); 
        return entity;
    }

    private ThuocDTO toDTO(Thuoc entity) {
        if (entity == null) {
            return null;
        }
        return new ThuocDTO(
                entity.getId(),
                entity.getTenThuoc(),
                entity.getHoatChat(),
                entity.getDonViTinh(),
                entity.getDonGia(),
                entity.getSoLuongTonKho(),
                entity.getTrangThai()
        );
    }

}
