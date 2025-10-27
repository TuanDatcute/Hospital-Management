// path: com/service/HoaDonService.java
package service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import model.dao.BenhNhanDAO;
import model.dao.HoaDonDAO;
import model.dao.PhieuKhamBenhDAO;
import model.dto.HoaDonDTO;
import model.Entity.BenhNhan;
import model.Entity.HoaDon;
import model.Entity.PhieuKhamBenh;
// import com.model.TrangThaiHoaDon; // Không cần Enum

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import model.Entity.GiaoDichThanhToan;
import model.dao.GiaoDichThanhToanDAO;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import util.HibernateUtil;

public class HoaDonService {

    /**
     * Tạo một hóa đơn mới
     */
    public boolean createHoaDon(HoaDonDTO dto) {
        try {
            // Khi tạo mới, mặc định là CHƯA THANH TOÁN
            dto.setTrangThai("CHUA_THANH_TOAN");

            HoaDon hoaDon = convertToEntity(dto);
            if (hoaDon == null) {
                System.err.println("Không thể tạo hóa đơn do thiếu BenhNhanId hoặc PhieuKhamId.");
                return false;
            }
            HoaDonDAO hoaDonDAO = new HoaDonDAO();
            hoaDonDAO.addHoaDon(hoaDon);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật trạng thái hóa đơn THAY ĐỔI: Sử dụng int
     */
    public boolean updateTrangThaiHoaDon(int hoaDonId, String trangThaiMoi) {
        // (Bạn nên có 1 bước kiểm tra xem trangThaiMoi có phải là
        // "DA_THANH_TOAN" hoặc "DA_HUY" hay không)

        try {
            // Giả sử DAO đã cập nhật
            HoaDonDAO hoaDonDAO = new HoaDonDAO();
            HoaDon hoaDon = hoaDonDAO.getHoaDonById(hoaDonId);
            if (hoaDon == null) {
                return false; // Không tìm thấy hóa đơn
            }

            hoaDon.setTrangThai(trangThaiMoi);
            hoaDonDAO.updateHoaDon(hoaDon);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy danh sách hóa đơn của một bệnh nhân THAY ĐỔI: Sử dụng int
     */
    public List<HoaDonDTO> getHoaDonByBenhNhan(int benhNhanId) {
        // Giả sử DAO đã cập nhật
        HoaDonDAO hoaDonDAO = new HoaDonDAO();
        List<HoaDon> entities = hoaDonDAO.getHoaDonByBenhNhanId(benhNhanId);

        // Dùng vòng lặp for
        List<HoaDonDTO> dtos = new ArrayList<>();
        for (HoaDon entity : entities) {
            if (entity != null) {
                dtos.add(convertToDTO(entity));
            }
        }
        return dtos;
    }

    /**
     * Lấy 1 hóa đơn bằng ID THAY ĐỔI: Sử dụng int
     */
    public HoaDonDTO getHoaDonById(int id) {
        // Giả sử DAO đã cập nhật
        HoaDonDAO hoaDonDAO = new HoaDonDAO();
        HoaDon entity = hoaDonDAO.getHoaDonById(id);
        return convertToDTO(entity);
    }

    /**
     * Lấy 1 hóa đơn bằng Mã Hóa Đơn
     */
    public HoaDonDTO getHoaDonByMaHoaDon(String maHoaDon) {
        HoaDonDAO hoaDonDAO = new HoaDonDAO();
        HoaDon entity = hoaDonDAO.getHoaDonByMaHoaDon(maHoaDon);
        return convertToDTO(entity);
    }
    
    public List<HoaDonDTO> searchInvoices(String keyword) {
        List<HoaDon> entityList;
        HoaDonDAO hoaDonDAO = new HoaDonDAO();
        
        if (keyword == null || keyword.trim().isEmpty()) {
            // 1. Nếu không tìm kiếm, gọi hàm cũ
            entityList = hoaDonDAO.getAllInvoicesWithPatient();
        } else {
            // 2. Nếu có tìm kiếm, gọi hàm DAO mới
            entityList = hoaDonDAO.findInvoicesByKeyword(keyword);
        }
        
        // 3. Chuyển đổi List<Entity> sang List<DTO> (dùng hàm convertToDTO bạn đã có)
        return entityList.stream()
                         .map(this::convertToDTO)
                         .collect(Collectors.toList());
    }

    public List<HoaDonDTO> getAllInvoicesWithPatient() {
        // 1. Lấy danh sách ENTITY từ DAO
        HoaDonDAO hoaDonDAO = new HoaDonDAO();
        List<HoaDon> entityList = hoaDonDAO.getAllInvoicesWithPatient();

        // 2. Chuyển đổi List<Entity> sang List<DTO>
        List<HoaDonDTO> dtoList = new ArrayList<>();
        for (HoaDon entity : entityList) {
            dtoList.add(convertToDTO(entity));
        }
        return dtoList;
    }

    public void processPayment(int invoiceId, BigDecimal soTienThanhToan, String phuongThuc) throws Exception {
        Session session = null;
        Transaction transaction = null;
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // Bước 1: Lấy hóa đơn từ DB
            HoaDonDAO hoaDonDAO = new HoaDonDAO();
            HoaDon hoaDonEntity = hoaDonDAO.getById(invoiceId, session);

            // Kiểm tra nghiệp vụ
            if (hoaDonEntity == null) {
                throw new Exception("Hóa đơn với ID " + invoiceId + " không tồn tại.");
            }
            if (!"CHUA_THANH_TOAN".equalsIgnoreCase(hoaDonEntity.getTrangThai())) {
                throw new Exception("Hóa đơn này đã được xử lý hoặc đã hủy.");
            }

            // Bước 2: Tạo đối tượng GiaoDichThanhToan mới
            GiaoDichThanhToan giaoDichEntity = new GiaoDichThanhToan();
            giaoDichEntity.setSoTien(soTienThanhToan);
            giaoDichEntity.setPhuongThuc(phuongThuc);
            giaoDichEntity.setThoiGianGiaoDich(LocalDateTime.now()); // Hoặc new Date()
            giaoDichEntity.setHoaDon(hoaDonEntity); // Thiết lập mối quan hệ

            // Bước 3: Lưu giao dịch mới vào DB (trong cùng transaction)
            GiaoDichThanhToanDAO giaoDichThanhToanDAO = new GiaoDichThanhToanDAO();
            giaoDichThanhToanDAO.save(giaoDichEntity, session);

            // Bước 4: Cập nhật trạng thái hóa đơn
            // (Bạn có thể thêm logic phức tạp hơn, ví dụ: thanh toán một phần)
            if (soTienThanhToan.compareTo(hoaDonEntity.getTongTien()) >= 0) {
                hoaDonEntity.setTrangThai("DA_THANH_TOAN");
            } else {
                // Tùy chọn: Xử lý trạng thái "THANH_TOAN_MOT_PHAN"
                hoaDonEntity.setTrangThai("THANH_TOAN_MOT_PHAN");
            }

            // Bước 5: Cập nhật hóa đơn trong DB (trong cùng transaction)
            hoaDonDAO.update(hoaDonEntity, session);

            // Bước 6: Nếu mọi thứ thành công, commit transaction
            transaction.commit();

        } catch (Exception e) {
            // Nếu có bất kỳ lỗi nào, rollback toàn bộ transaction
            if (transaction != null) {
                transaction.rollback();
            }
            // Ném lại lỗi để Controller có thể bắt và xử lý
            throw new Exception("Xử lý thanh toán thất bại: " + e.getMessage());
        } finally {
            // Luôn đóng session sau khi hoàn tất
            if (session != null) {
                session.close();
            }
        }
    }

    // --- Phương thức chuyển đổi (Helper Methods) ---
    // Chuyển Entity sang DTO
    private HoaDonDTO convertToDTO(HoaDon entity) {
        if (entity == null) {
            return null;
        }

        HoaDonDTO dto = new HoaDonDTO();
        dto.setId(entity.getId()); // int sang int
        dto.setMaHoaDon(entity.getMaHoaDon());
        dto.setNgayTao(entity.getNgayTao());
        dto.setTongTien(entity.getTongTien());

        dto.setTrangThai(entity.getTrangThai()); // Lấy String

        if (entity.getBenhNhan() != null) {
            dto.setBenhNhanId(entity.getBenhNhan().getId()); // int sang int
        }
        if (entity.getPhieuKhamBenh() != null) {
            dto.setPhieuKhamId(entity.getPhieuKhamBenh().getId()); // int sang int
        }
        
        if (entity.getPhieuKhamBenh() != null) {
            dto.setMaPhieuKhamBenh(entity.getPhieuKhamBenh().getMaPhieuKham()); // int sang int
        }

        // Gán tên bệnh nhân (nếu có quan hệ)
        if (entity.getBenhNhan() != null) {
            dto.setHoTenBenhNhan(entity.getBenhNhan().getHoTen());
        }

        return dto;
    }

    // Chuyển DTO sang Entity
    private HoaDon convertToEntity(HoaDonDTO dto) {
        if (dto == null) {
            return null;
        }

        HoaDon entity = new HoaDon();

        // Giả định 0 là ID không hợp lệ (cho trường hợp tạo mới)
        if (dto.getId() != 0) {
            entity.setId(dto.getId());
        }

        entity.setMaHoaDon(dto.getMaHoaDon());
        entity.setTongTien(dto.getTongTien());
        entity.setTrangThai(dto.getTrangThai()); // Gán String

        // THAY ĐỔI: Kiểm tra 0 thay vì null
        if (dto.getBenhNhanId() == 0 || dto.getPhieuKhamBenhId() == 0) {
            System.err.println("Không thể convert DTO: BenhNhanId hoặc PhieuKhamBenhId bằng 0.");
            return null;
        }

        // Giả sử DAO đã cập nhật
        BenhNhanDAO benhNhanDAO = new BenhNhanDAO();
        BenhNhan benhNhan = benhNhanDAO.getById(dto.getBenhNhanId());
        PhieuKhamBenhDAO phieuKhamBenhDAO = new PhieuKhamBenhDAO();
        PhieuKhamBenh phieuKhamBenh = phieuKhamBenhDAO.getEncounterById(dto.getPhieuKhamBenhId());

        if (benhNhan == null || phieuKhamBenh == null) {
            System.err.println("Không tìm thấy Bệnh Nhân hoặc Phiếu Khám khi convert DTO");
            return null;
        }

        entity.setBenhNhan(benhNhan);
        entity.setPhieuKhamBenh(phieuKhamBenh);

        return entity;
    }
}
