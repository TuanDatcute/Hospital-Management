package service;

import exception.ValidationException;
import java.math.BigDecimal;
import model.dao.DonThuocDAO;
import model.dao.PhieuKhamBenhDAO;
import model.dao.ThuocDAO;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import model.dto.ChiTietDonThuocDTO;
import model.dto.DonThuocDTO;
import model.Entity.ChiTietDonThuoc;
import model.Entity.DonThuoc;
import model.Entity.PhieuKhamBenh;
import model.Entity.Thuoc;
import model.dao.ChiTietDonThuocDAO;
import org.hibernate.Session;
import org.hibernate.Transaction;
import util.HibernateUtil;

public class DonThuocService {

    private final DonThuocDAO donThuocDAO = new DonThuocDAO();
    private final PhieuKhamBenhDAO phieuKhamDAO = new PhieuKhamBenhDAO();
    private final ThuocDAO thuocDAO = new ThuocDAO();
    private final ChiTietDonThuocDAO chiTietDonThuocDAO = new ChiTietDonThuocDAO();

    /**
     * NGHIỆP VỤ: Tạo một đơn thuốc hoàn chỉnh, bao gồm cả các thuốc chi
     * tiết.Đây là một nghiệp vụ phức tạp, bao gồm kiểm tra, tạo và cập nhật
     * nhiều bảng.
     *
     * @param dto Dữ liệu đơn thuốc từ Controller, bao gồm cả danh sách thuốc.
     * @return DTO của đơn thuốc sau khi tạo thành công.
     * @throws exception.ValidationException
     * @throws Exception nếu có lỗi nghiệp vụ (phiếu khám không tồn tại, thuốc
     * hết hàng...).
     */
    public DonThuocDTO createPrescription(DonThuocDTO dto) throws ValidationException {
        Session session = null;
        Transaction transaction = null;
        DonThuoc savedDonThuoc = null;

        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction(); // <-- BẮT ĐẦU TRANSACTION

            // 1. Lấy và kiểm tra các Entity, sử dụng các phương thức DAO mới
            PhieuKhamBenh phieuKham = phieuKhamDAO.getEncounterById(dto.getPhieuKhamId(), session);
            if (phieuKham == null) {
                throw new ValidationException("Không tìm thấy phiếu khám với ID: " + dto.getPhieuKhamId());
            }

            DonThuoc donThuocEntity = new DonThuoc();
            donThuocEntity.setPhieuKham(phieuKham);
            donThuocEntity.setLoiDan(dto.getLoiDan());
            donThuocEntity.setNgayKeDon(LocalDateTime.now());
            donThuocEntity.setChiTietDonThuoc(new ArrayList<>());

            // 3. Xử lý các ChiTietDonThuoc
            for (ChiTietDonThuocDTO chiTietDTO : dto.getChiTietDonThuoc()) {
                Thuoc thuoc = thuocDAO.getById(chiTietDTO.getThuocId(), session); // <-- Dùng DAO mới
                if (thuoc == null) {
                    throw new ValidationException("Không tìm thấy thuốc với ID: " + chiTietDTO.getThuocId());
                }
                if (thuoc.getSoLuongTonKho() < chiTietDTO.getSoLuong()) {
                    throw new ValidationException("Không đủ tồn kho cho thuốc: " + thuoc.getTenThuoc());
                }

                // Cập nhật tồn kho trong bộ nhớ, Hibernate sẽ tự phát hiện (dirty checking)
                thuoc.setSoLuongTonKho(thuoc.getSoLuongTonKho() - chiTietDTO.getSoLuong());

                ChiTietDonThuoc chiTietEntity = new ChiTietDonThuoc();
                chiTietEntity.setThuoc(thuoc);
                chiTietEntity.setSoLuong(chiTietDTO.getSoLuong());
                chiTietEntity.setLieuDung(chiTietDTO.getLieuDung());
                chiTietEntity.setDonThuoc(donThuocEntity);
                donThuocEntity.getChiTietDonThuoc().add(chiTietEntity);
            }

            // 4. Lưu đối tượng cha, sử dụng phương thức DAO mới
            donThuocDAO.create(donThuocEntity, session); // <-- Dùng DAO mới

            transaction.commit(); // <-- COMMIT: Chỉ lưu khi mọi thứ thành công
            savedDonThuoc = donThuocEntity;

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // <-- ROLLBACK: Hủy tất cả nếu có lỗi
            }
            throw new ValidationException("Lỗi khi tạo đơn thuốc: " + e.getMessage());
        } finally {
            if (session != null) {
                session.close(); // Luôn đóng session
            }
        }
        return toDTO(savedDonThuoc);
    }

    public DonThuocDTO getPrescriptionDetails(int donThuocId) {
        DonThuoc entity = donThuocDAO.getById(donThuocId);
        return toDTO(entity);
    }

    public List<ChiTietDonThuocDTO> listDetailsByPrescription(int donThuocId) {
        List<ChiTietDonThuoc> entities = chiTietDonThuocDAO.findByDonThuocId(donThuocId);
        return entities.stream().map(this::toChiTietDTO).collect(Collectors.toList());
    }

    /**
     * Xóa một chi tiết (một dòng thuốc) khỏi đơn thuốc.
     */
    public void deletePrescriptionDetail(int chiTietId) throws ValidationException {
        ChiTietDonThuoc chiTiet = chiTietDonThuocDAO.getById(chiTietId);
        if (chiTiet == null) {
            throw new ValidationException("Không tìm thấy chi tiết đơn thuốc để xóa.");
        }

        // Logic nghiệp vụ: Hoàn trả lại số lượng thuốc vào kho
        Thuoc thuoc = chiTiet.getThuoc();
        if (thuoc != null) {
            thuoc.setSoLuongTonKho(thuoc.getSoLuongTonKho() + chiTiet.getSoLuong());
            thuocDAO.update(thuoc); // Cập nhật lại số lượng trong kho
        }

        chiTietDonThuocDAO.delete(chiTiet.getId());
    }

    public List<DonThuocDTO> searchPrescriptionsByPatientName(String name) {
        // Gọi DAO để thực hiện truy vấn
        List<DonThuoc> entities = donThuocDAO.findByPatientName(name);

        // Chuyển đổi danh sách Entity sang DTO để trả về
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<DonThuocDTO> getAllPrescriptions() {
        List<DonThuoc> entities = donThuocDAO.getAll();
        // Chuyển đổi danh sách Entity sang DTO
        return entities.stream()
                .map(this::toDTO) // Áp dụng hàm toDTO cho mỗi DonThuoc
                .collect(Collectors.toList());
    }

    public List<ChiTietDonThuocDTO> getChiTietByPhieuKhamId(int phieuKhamId) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            List<ChiTietDonThuoc> entities = donThuocDAO.findChiTietByPhieuKhamId(phieuKhamId, session);
            transaction.commit();
            // Chuyển List<Entity> sang List<DTO>
            return entities.stream()
                    .map(this::convertThuocToDTO)
                    .collect(Collectors.toList());
        }catch (Exception e) {
            if (transaction != null) {
                transaction.rollback(); // Rollback nếu có lỗi
            }
            e.printStackTrace(); // Log lỗi
            // Có thể throw một custom exception ở đây
            return new LinkedList<>(); 
        }
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
            if (entity.getPhieuKham().getBenhNhan() != null) {
                dto.setTenBenhNhan(entity.getPhieuKham().getBenhNhan().getHoTen());
            }
        }

        //  Lấy danh sách chi tiết từ chính đối tượng entity 
        if (entity.getChiTietDonThuoc() != null) {
            // Duyệt qua danh sách ChiTietDonThuoc (Entity) của đơn thuốc hiện tại
            List<ChiTietDonThuocDTO> chiTietDTOs = entity.getChiTietDonThuoc().stream()
                    .map(this::toChiTietDTO) // Áp dụng hàm chuyển đổi cho mỗi chi tiết
                    .collect(Collectors.toList()); // Gom kết quả lại thành một List

            dto.setChiTietDonThuoc(chiTietDTOs);
        }

        return dto;
    }

    /**
     * Chuyển đổi một đối tượng ChiTietDonThuoc (Entity) sang
     * ChiTietDonThuocDTO.
     */
    private ChiTietDonThuocDTO toChiTietDTO(ChiTietDonThuoc chiTietEntity) {
        if (chiTietEntity == null) {
            return null;
        }

        ChiTietDonThuocDTO chiTietDTO = new ChiTietDonThuocDTO();
        chiTietDTO.setId(chiTietEntity.getId());
        chiTietDTO.setSoLuong(chiTietEntity.getSoLuong());
        chiTietDTO.setLieuDung(chiTietEntity.getLieuDung());

        if (chiTietEntity.getThuoc() != null) {
            chiTietDTO.setThuocId(chiTietEntity.getThuoc().getId());
            chiTietDTO.setTenThuoc(chiTietEntity.getThuoc().getTenThuoc()); // "Làm phẳng" để tiện hiển thị
        }

        return chiTietDTO;
    }

    private ChiTietDonThuocDTO convertThuocToDTO(ChiTietDonThuoc entity) {
        ChiTietDonThuocDTO dto = new ChiTietDonThuocDTO();
        dto.setTenThuoc(entity.getThuoc().getTenThuoc());
        dto.setSoLuong(entity.getSoLuong());
        dto.setDonGia(entity.getThuoc().getDonGia());

        BigDecimal thanhTien = entity.getThuoc().getDonGia()
                .multiply(new BigDecimal(entity.getSoLuong()));
        dto.setThanhTien(thanhTien);
        return dto;
    }
}
