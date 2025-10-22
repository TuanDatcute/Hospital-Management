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
            // --- THAY ĐỔI Ở ĐÂY ---
            // Khi tạo mới, mặc định là CHƯA THANH TOÁN
            dto.setTrangThai("CHUA_THANH_TOAN");
            
            HoaDon hoaDon = convertToEntity(dto);
            if (hoaDon == null) {
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
     */
    // --- THAY ĐỔI Ở ĐÂY ---
    public boolean updateTrangThaiHoaDon(long hoaDonId, String trangThaiMoi) {
        // (Bạn nên có 1 bước kiểm tra xem trangThaiMoi có phải là
        // "DA_THANH_TOAN" hoặc "DA_HUY" hay không)
        
        try {
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
     */
    public List<HoaDonDTO> getHoaDonByBenhNhan(long benhNhanId) {
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
     */
    public HoaDonDTO getHoaDonById(long id) {
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
        dto.setId(entity.getId());
        dto.setMaHoaDon(entity.getMaHoaDon());
        dto.setNgayTao(entity.getNgayTao());
        dto.setTongTien(entity.getTongTien());
        
        // --- THAY ĐỔI Ở ĐÂY ---
        dto.setTrangThai(entity.getTrangThai()); // Lấy String
        
        if (entity.getBenhNhan() != null) {
            dto.setBenhNhanId(entity.getBenhNhan().getId());
        }
        if (entity.getPhieuKhamBenh() != null) {
            dto.setPhieuKhamId(entity.getPhieuKhamBenh().getId());
        }
        
        return dto;
    }

    // Chuyển DTO sang Entity
    private HoaDon convertToEntity(HoaDonDTO dto) {
        if (dto == null) return null;

        HoaDon entity = new HoaDon();
        entity.setId(dto.getId()); 
        entity.setMaHoaDon(dto.getMaHoaDon());
        entity.setTongTien(dto.getTongTien());

        // --- THAY ĐỔI Ở ĐÂY ---
        entity.setTrangThai(dto.getTrangThai()); // Gán String
        
        BenhNhan benhNhan = benhNhanDAO.getBenhNhanById(dto.getBenhNhanId());
        PhieuKhamBenh phieuKhamBenh = phieuKhamBenhDAO.getPhieuKhamBenhById(dto.getPhieuKhamBenhId());

        if (benhNhan == null || phieuKhamBenh == null) {
            System.err.println("Không tìm thấy Bệnh Nhân hoặc Phiếu Khám khi convert DTO");
            return null; 
        }

        entity.setBenhNhan(benhNhan);
        entity.setPhieuKhamBenh(phieuKhamBenh);

        return entity;
    }
}