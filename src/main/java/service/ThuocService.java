package service;

import model.dao.ThuocDAO;
import model.dto.ThuocDTO;
import model.Entity.Thuoc;
import java.util.List;
import java.util.stream.Collectors;

public class ThuocService {

    private final ThuocDAO thuocDAO = new ThuocDAO();

    public ThuocDTO createMedication(ThuocDTO dto) throws Exception {
        if (dto.getTenThuoc() == null || dto.getTenThuoc().trim().isEmpty()) {
            throw new Exception("Tên thuốc không được để trống.");
        }
        if (thuocDAO.isTenThuocExisted(dto.getTenThuoc())) {
            throw new Exception("Tên thuốc '" + dto.getTenThuoc() + "' đã tồn tại.");
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

    public ThuocDTO updateMedicationInfo(int id, ThuocDTO dto) throws Exception {
        Thuoc existingEntity = thuocDAO.getById(id);
        if (existingEntity == null) {
            throw new Exception("Không tìm thấy thuốc với ID: " + id);
        }

        existingEntity.setTenThuoc(dto.getTenThuoc());
        existingEntity.setHoatChat(dto.getHoatChat());
        existingEntity.setDonViTinh(dto.getDonViTinh());
        existingEntity.setDonGia(dto.getDonGia());
       

        thuocDAO.update(existingEntity);
        return toDTO(existingEntity);
    }

    /**
     * NGHIỆP VỤ: Cập nhật số lượng tồn kho (nhập/xuất kho).
     *
     * @param id ID của thuốc.
     * @param quantityChange Số lượng thay đổi (số dương để nhập, số âm để
     * xuất).
     * @return DTO của thuốc sau khi cập nhật kho.
     */
    public ThuocDTO updateStockQuantity(int id, int quantityChange) throws Exception {
        Thuoc thuoc = thuocDAO.getById(id);
        if (thuoc == null) {
            throw new Exception("Không tìm thấy thuốc với ID: " + id);
        }

        int newQuantity = thuoc.getSoLuongTonKho() + quantityChange;
        if (newQuantity < 0) {
            throw new Exception("Số lượng tồn kho không đủ để xuất.");
        }

        thuoc.setSoLuongTonKho(newQuantity);
        thuocDAO.update(thuoc);
        return toDTO(thuoc);
    }

    public void deleteMedication(int id) {
        // Cần thêm logic kiểm tra xem thuốc có đang được dùng trong đơn thuốc nào không
        thuocDAO.delete(id);
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

    // --- Phương thức chuyển đổi ---
    private Thuoc toEntity(ThuocDTO dto) {
        Thuoc entity = new Thuoc();
        entity.setTenThuoc(dto.getTenThuoc());
        entity.setHoatChat(dto.getHoatChat());
        entity.setDonViTinh(dto.getDonViTinh());
        entity.setDonGia(dto.getDonGia());
        entity.setSoLuongTonKho(dto.getSoLuongTonKho());
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
                entity.getSoLuongTonKho()
        );
    }

}
