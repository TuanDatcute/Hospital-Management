// path: com/service/PhongBenhService.java
package service;

import model.dao.KhoaDAO;
import model.dao.PhongBenhDAO;
import model.dto.PhongBenhDTO;
import model.Entity.Khoa;
import model.Entity.PhongBenh;

import java.util.ArrayList;
import java.util.List;

public class PhongBenhService {

    private PhongBenhDAO phongBenhDAO;
    private KhoaDAO khoaDAO;

    public PhongBenhService() {
        this.phongBenhDAO = new PhongBenhDAO();
        this.khoaDAO = new KhoaDAO();
    }

    /**
     * Tạo phòng bệnh mới
     */
    public boolean createPhongBenh(PhongBenhDTO dto) {
        try {
            // Kiểm tra xem tên phòng đã tồn tại chưa
            if (phongBenhDAO.getPhongBenhByTenPhong(dto.getTenPhong()) != null) {
                System.err.println("Tên phòng đã tồn tại: " + dto.getTenPhong());
                return false;
            }
            
            PhongBenh entity = convertToEntity(dto);
            if (entity == null) {
                return false; // Lỗi (không tìm thấy Khoa)
            }
            
            phongBenhDAO.addPhongBenh(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cập nhật thông tin phòng bệnh
     */
    public boolean updatePhongBenh(PhongBenhDTO dto) {
        try {
            // Kiểm tra xem phòng có tồn tại không
            PhongBenh existingPhong = phongBenhDAO.getPhongBenhById(dto.getId());
            if (existingPhong == null) {
                System.err.println("Không tìm thấy phòng để cập nhật.");
                return false;
            }
            
            // Kiểm tra nếu đổi tên, tên mới có bị trùng không
            if (!existingPhong.getTenPhong().equals(dto.getTenPhong())) {
                if (phongBenhDAO.getPhongBenhByTenPhong(dto.getTenPhong()) != null) {
                    System.err.println("Tên phòng mới đã tồn tại: " + dto.getTenPhong());
                    return false;
                }
            }
            
            PhongBenh entity = convertToEntity(dto);
            if (entity == null) {
                return false;
            }
            
            phongBenhDAO.updatePhongBenh(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy 1 phòng bệnh bằng ID
     */
    public PhongBenhDTO getPhongBenhById(long id) {
        PhongBenh entity = phongBenhDAO.getPhongBenhById(id);
        return convertToDTO(entity);
    }
    
    /**
     * Lấy tất cả phòng bệnh
     */
    public List<PhongBenhDTO> getAllPhongBenh() {
        List<PhongBenh> entities = phongBenhDAO.getAllPhongBenh();
        
        // **SỬ DỤNG VÒNG LẶP FOR**
        List<PhongBenhDTO> dtos = new ArrayList<>();
        for (PhongBenh entity : entities) {
            if (entity != null) {
                dtos.add(convertToDTO(entity));
            }
        }
        return dtos;
    }
    
    /**
     * Lấy tất cả phòng bệnh theo Khoa
     */
    public List<PhongBenhDTO> getPhongBenhByKhoa(long khoaId) {
        List<PhongBenh> entities = phongBenhDAO.getPhongBenhByKhoa(khoaId);
        
        // **SỬ DỤNG VÒNG LẶP FOR**
        List<PhongBenhDTO> dtos = new ArrayList<>();
        for (PhongBenh entity : entities) {
            if (entity != null) {
                dtos.add(convertToDTO(entity));
            }
        }
        return dtos;
    }

    // --- Phương thức chuyển đổi (Helper Methods) ---

    // Chuyển Entity sang DTO
    private PhongBenhDTO convertToDTO(PhongBenh entity) {
        if (entity == null) return null;

        PhongBenhDTO dto = new PhongBenhDTO();
        dto.setId(entity.getId());
        dto.setTenPhong(entity.getTenPhong());
        dto.setLoaiPhong(entity.getLoaiPhong());
        dto.setSucChua(entity.getSucChua());
        
        // Đã JOIN FETCH trong DAO nên không lo LazyException
        if (entity.getKhoa() != null) {
            dto.setKhoaId(entity.getKhoa().getId());
            dto.setTenKhoa(entity.getKhoa().getTenKhoa()); // Thêm tên khoa
        }
        
        return dto;
    }

    // Chuyển DTO sang Entity
    private PhongBenh convertToEntity(PhongBenhDTO dto) {
        if (dto == null) return null;

        PhongBenh entity = new PhongBenh();
        
        // Nếu là update (có ID), nên lấy entity cũ
        if (dto.getId() != null) {
             // Tải entity hiện tại để Hibernate quản lý việc cập nhật
             // Dùng getPhongBenhById sẽ bị JOIN FETCH, dùng session.get của KhoaDAO là đủ
             // Tạm thời dùng cách đơn giản:
             entity.setId(dto.getId());
        }
       
        entity.setTenPhong(dto.getTenPhong());
        entity.setLoaiPhong(dto.getLoaiPhong());
        entity.setSucChua(dto.getSucChua());

        // Lấy Khoa
        if (dto.getKhoaId() == null) {
             System.err.println("DTO thiếu khoaId.");
            return null;
        }
        
        Khoa khoa = khoaDAO.getKhoaById(dto.getKhoaId());
        if (khoa == null) {
            System.err.println("Không tìm thấy Khoa ID: " + dto.getKhoaId());
            return null; // Khoa không tồn tại
        }

        entity.setKhoa(khoa);
        return entity;
    }
}