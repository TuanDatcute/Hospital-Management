package service;

import exception.ValidationException;
import model.dto.DichVuDTO;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import model.Entity.DichVu;
import model.dao.ChiDinhDichVuDAO;
import model.dao.DichVuDAO;

/**
 * Lớp Service cho Dịch Vụ. Chứa tất cả logic nghiệp vụ liên quan đến việc quản
 * lý dịch vụ.
 */
public class DichVuService {

    private DichVuDAO dichVuDAO = new DichVuDAO();
 private ChiDinhDichVuDAO chiDinhDAO = new ChiDinhDichVuDAO();
    /**
     * Lấy danh sách tất cả dịch vụ.
     *
     * @return List<DichVuDTO> danh sách các dịch vụ.
     */
    public List<DichVuDTO> getAllServices() {
        List<DichVu> entityList = dichVuDAO.getAll();
        List<DichVuDTO> dtoList = new ArrayList<>();
        for (DichVu entity : entityList) {
            dtoList.add(toDTO(entity));
        }
        return dtoList;
    }

    public List<DichVuDTO> getAllServicesActive() {
        List<DichVu> entities = dichVuDAO.getAllActiveServices();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * Lấy thông tin một dịch vụ theo ID.
     *
     * @param id ID của dịch vụ.
     * @return DichVuDTO hoặc null nếu không tìm thấy.
     */
    public DichVuDTO getServiceById(int id) {
        DichVu entity = dichVuDAO.getById(id);
        if (entity != null) {
            return toDTO(entity);
        }
        return null;
    }

    /**
     * Xử lý nghiệp vụ tạo một dịch vụ mới.
     *
     * @param dto Dữ liệu dịch vụ từ Controller.
     * @return DTO của dịch vụ đã tạo, hoặc null nếu thất bại.
     * @throws Exception nếu có lỗi nghiệp vụ (ví dụ: tên trùng).
     */
    public DichVuDTO createService(DichVuDTO dto) throws ValidationException {
        // --- Logic nghiệp vụ: Kiểm tra dữ liệu đầu vào ---
        if (dto.getTenDichVu() == null || dto.getTenDichVu().trim().isEmpty()) {
            throw new ValidationException("Tên dịch vụ không được để trống.");
        }
        if (dto.getDonGia() == null || dto.getDonGia().doubleValue() < 0) {
            throw new ValidationException("Đơn giá không hợp lệ.");
        }
        if (dichVuDAO.isTenDichVuExisted(dto.getTenDichVu())) {
            throw new ValidationException("Tên dịch vụ '" + dto.getTenDichVu() + "' đã tồn tại.");
        }

        // --- Chuyển đổi DTO -> Entity ---
        DichVu entity = toEntity(dto);

        // --- Gọi DAO để lưu ---
        DichVu savedEntity = dichVuDAO.create(entity);

        // --- Chuyển đổi Entity -> DTO để trả về ---
        if (savedEntity != null) {
            return toDTO(savedEntity);
        }
        return null;
    }

    /**
     * Xử lý nghiệp vụ cập nhật thông tin dịch vụ.
     *
     * @param id ID của dịch vụ cần cập nhật.
     * @param dto Dữ liệu mới.
     * @return DTO của dịch vụ sau khi cập nhật.
     * @throws Exception nếu có lỗi (dịch vụ không tồn tại, tên trùng...).
     */
    public DichVuDTO updateService(int id, DichVuDTO dto) throws ValidationException {
        // --- Logic nghiệp vụ: Kiểm tra ---
        DichVu existingEntity = dichVuDAO.getById(id);
        if (existingEntity == null) {
            throw new ValidationException("Không tìm thấy dịch vụ với ID: " + id);
        }

        // Kiểm tra nếu tên mới trùng với một dịch vụ khác
        if (!existingEntity.getTenDichVu().equals(dto.getTenDichVu()) && dichVuDAO.isTenDichVuExisted(dto.getTenDichVu())) {
            throw new ValidationException("Tên dịch vụ '" + dto.getTenDichVu() + "' đã được sử dụng.");
        }

        // --- Cập nhật thông tin từ DTO vào Entity đã có ---
        existingEntity.setTenDichVu(dto.getTenDichVu());
        existingEntity.setMoTa(dto.getMoTa());
        existingEntity.setDonGia(dto.getDonGia());

        // --- Gọi DAO để cập nhật ---
        dichVuDAO.update(existingEntity);

        return toDTO(existingEntity);
    }

    /**
     * Xử lý nghiệp vụ xóa một dịch vụ.
     *
     * @param id ID của dịch vụ cần xóa.
     * @throws Exception nếu có lỗi (dịch vụ không tồn tại, đang được sử
     * dụng...).
     */
    public void deleteService(int id) throws ValidationException {
//         --- Logic nghiệp vụ: Kiểm tra xem dịch vụ có đang được sử dụng không ---
        ChiDinhDichVuDAO check = new ChiDinhDichVuDAO();
        if (check.isServiceInUse(id)) {
            throw new ValidationException("Không thể xóa dịch vụ này vì nó đang được sử dụng trong các phiếu khám.");
        }
        // --- Gọi DAO để xóa ---
        dichVuDAO.delete(id);
    }

    public List<DichVuDTO> searchServicesByName(String keyword) {
        return dichVuDAO.searchByIdOrName(keyword).stream().map(this::toDTO).collect(Collectors.toList());
    }

    // --- CÁC PHƯƠNG THỨC CHUYỂN ĐỔI (HELPER METHODS) ---
    /**
     * Chuyển đổi từ DTO sang Entity.
     */
    private DichVu toEntity(DichVuDTO dto) {
        DichVu entity = new DichVu();
        // Không set id cho Entity mới, để CSDL tự tạo
        entity.setTenDichVu(dto.getTenDichVu());
        entity.setMoTa(dto.getMoTa());
        entity.setDonGia(dto.getDonGia());
        return entity;
    }

    /**
     * Chuyển đổi từ Entity sang DTO.
     */
    private DichVuDTO toDTO(DichVu entity) {
        DichVuDTO dto = new DichVuDTO();
        dto.setId(entity.getId());
        dto.setTenDichVu(entity.getTenDichVu());
        dto.setMoTa(entity.getMoTa());
        dto.setDonGia(entity.getDonGia());
        dto.setTrangThai(entity.getTrangThai()); // ✨ Đảm bảo có dòng này
        return dto;
    }

    /**
     * ✨ HÀM MỚI ✨ NGHIỆP VỤ: Cập nhật trạng thái của một dịch vụ.
     *
     * @param id ID của dịch vụ cần cập nhật.
     * @param newStatus Trạng thái mới (ví dụ: "NGUNG_SU_DUNG").
     * @throws ValidationException nếu có lỗi nghiệp vụ.
     */
    public void updateServiceStatus(int id, String newStatus) throws ValidationException {
        // 1. Lấy Entity gốc
        DichVu existingService = dichVuDAO.getById(id);
        if (existingService == null) {
            throw new ValidationException("Không tìm thấy dịch vụ với ID: " + id);
        }

        // 2. Kiểm tra logic nghiệp vụ
        if ("NGUNG_SU_DUNG".equals(newStatus)) {
            // Kiểm tra xem dịch vụ có đang được sử dụng không
            if (chiDinhDAO.isServiceInUse(id)) {
                throw new ValidationException("Không thể ngừng: Dịch vụ đang được sử dụng trong một phiếu khám.");
            }
        }

        // 3. Cập nhật trạng thái
        existingService.setTrangThai(newStatus);

        // 4. Gọi DAO để lưu thay đổi
        dichVuDAO.update(existingService);
    }
}
