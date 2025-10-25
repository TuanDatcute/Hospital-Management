// path: com/service/HoaDonService.java
package service;

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

public class HoaDonService {

    private HoaDonDAO hoaDonDAO;
    private BenhNhanDAO benhNhanDAO;
    private PhieuKhamBenhDAO phieuKhamBenhDAO;

    public HoaDonService() {
        this.hoaDonDAO = new HoaDonDAO();
        this.benhNhanDAO = new BenhNhanDAO();
        this.phieuKhamBenhDAO = new PhieuKhamBenhDAO();
    }

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
            
            hoaDonDAO.addHoaDon(hoaDon);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật trạng thái hóa đơn
     * THAY ĐỔI: Sử dụng int
     */
    public boolean updateTrangThaiHoaDon(int hoaDonId, String trangThaiMoi) {
        // (Bạn nên có 1 bước kiểm tra xem trangThaiMoi có phải là
        // "DA_THANH_TOAN" hoặc "DA_HUY" hay không)
        
        try {
            // Giả sử DAO đã cập nhật
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
     * Lấy danh sách hóa đơn của một bệnh nhân
     * THAY ĐỔI: Sử dụng int
     */
    public List<HoaDonDTO> getHoaDonByBenhNhan(int benhNhanId) {
        // Giả sử DAO đã cập nhật
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
     * Lấy 1 hóa đơn bằng ID
     * THAY ĐỔI: Sử dụng int
     */
    public HoaDonDTO getHoaDonById(int id) {
        // Giả sử DAO đã cập nhật
        HoaDon entity = hoaDonDAO.getHoaDonById(id); 
        return convertToDTO(entity);
    }
    
    /**
     * Lấy 1 hóa đơn bằng Mã Hóa Đơn
     */
    public HoaDonDTO getHoaDonByMaHoaDon(String maHoaDon) {
        HoaDon entity = hoaDonDAO.getHoaDonByMaHoaDon(maHoaDon);
        return convertToDTO(entity);
    }

    // --- Phương thức chuyển đổi (Helper Methods) ---

    // Chuyển Entity sang DTO
    private HoaDonDTO convertToDTO(HoaDon entity) {
        if (entity == null) return null;

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
        
        return dto;
    }

    // Chuyển DTO sang Entity
    private HoaDon convertToEntity(HoaDonDTO dto) {
        if (dto == null) return null;

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
        BenhNhan benhNhan = benhNhanDAO.getById(dto.getBenhNhanId()); 
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