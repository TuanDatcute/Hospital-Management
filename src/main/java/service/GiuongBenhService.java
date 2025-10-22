// path: com/service/GiuongBenhService.java
package service;

import model.dao.BenhNhanDAO;
import model.dao.GiuongBenhDAO;
import model.dao.PhongBenhDAO;
import model.dto.GiuongBenhDTO;
import model.Entity.BenhNhan;
import model.Entity.GiuongBenh;
import model.Entity.PhongBenh;

import java.util.ArrayList;
import java.util.List;

public class GiuongBenhService {

    private GiuongBenhDAO giuongBenhDAO;
    private PhongBenhDAO phongBenhDAO;
    private BenhNhanDAO benhNhanDAO;

    public GiuongBenhService() {
        this.giuongBenhDAO = new GiuongBenhDAO();
        this.phongBenhDAO = new PhongBenhDAO();
        this.benhNhanDAO = new BenhNhanDAO();
    }

    /**
     * Tạo một giường bệnh mới (mặc định là 'TRONG')
     */
    public boolean createGiuong(GiuongBenhDTO dto) {
        try {
            // Khi tạo mới, giường luôn trống và không có bệnh nhân
            dto.setTrangThai("TRONG");
            dto.setBenhNhanId(null); 
            
            GiuongBenh entity = convertToEntity(dto);
            if (entity == null) {
                return false; // Lỗi (không tìm thấy Phòng Bệnh)
            }
            
            giuongBenhDAO.addGiuong(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gán một bệnh nhân vào một giường trống
     */
    public boolean assignBenhNhanToGiuong(long giuongId, long benhNhanId) {
        try {
            GiuongBenh giuong = giuongBenhDAO.getGiuongById(giuongId);
            BenhNhan benhNhan = benhNhanDAO.getBenhNhanById(benhNhanId);

            if (giuong == null || benhNhan == null) {
                System.err.println("Không tìm thấy giường hoặc bệnh nhân.");
                return false;
            }

            if (!giuong.getTrangThai().equals("TRONG")) {
                System.err.println("Giường không ở trạng thái 'TRONG'.");
                return false;
            }

            // Gán bệnh nhân và cập nhật trạng thái
            giuong.setBenhNhan(benhNhan);
            giuong.setTrangThai("DANG_SU_DUNG");
            
            giuongBenhDAO.updateGiuong(giuong);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Bệnh nhân trả giường (xuất viện hoặc chuyển giường)
     */
    public boolean releaseGiuong(long giuongId) {
        try {
            GiuongBenh giuong = giuongBenhDAO.getGiuongById(giuongId);
            if (giuong == null) {
                return false;
            }

            // Bỏ gán bệnh nhân và cập nhật trạng thái
            giuong.setBenhNhan(null); // Gán về NULL
            giuong.setTrangThai("DANG_DON_DEP"); // Chuyển sang dọn dẹp
            
            giuongBenhDAO.updateGiuong(giuong);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Cập nhật trạng thái giường (ví dụ: Dọn dẹp -> Trống, hoặc Trống -> Bảo trì)
     */
    public boolean updateTrangThaiGiuong(long giuongId, String trangThaiMoi) {
         try {
            GiuongBenh giuong = giuongBenhDAO.getGiuongById(giuongId);
            if (giuong == null) {
                return false;
            }
            // Nếu giường đang sử dụng thì không cho đổi
            if (giuong.getTrangThai().equals("DANG_SU_DUNG") && !trangThaiMoi.equals("DANG_SU_DUNG")) {
                 System.err.println("Không thể đổi trạng thái giường đang có bệnh nhân.");
                 return false;
            }
            
            giuong.setTrangThai(trangThaiMoi);
            giuongBenhDAO.updateGiuong(giuong);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy danh sách giường trong một phòng
     */
    public List<GiuongBenhDTO> getGiuongByPhong(long phongBenhId) {
        List<GiuongBenh> entities = giuongBenhDAO.getGiuongByPhongBenhId(phongBenhId);
        
        // **SỬ DỤNG VÒNG LẶP FOR**
        List<GiuongBenhDTO> dtos = new ArrayList<>();
        for (GiuongBenh entity : entities) {
            if (entity != null) {
                dtos.add(convertToDTO(entity));
            }
        }
        return dtos;
    }
    
    /**
     * Lấy danh sách giường TRỐNG trong một phòng
     */
    public List<GiuongBenhDTO> getGiuongTrongByPhong(long phongBenhId) {
        List<GiuongBenh> entities = giuongBenhDAO.getGiuongTrongByPhong(phongBenhId);
        
        // **SỬ DỤNG VÒNG LẶP FOR**
        List<GiuongBenhDTO> dtos = new ArrayList<>();
        for (GiuongBenh entity : entities) {
            if (entity != null) {
                dtos.add(convertToDTO(entity));
            }
        }
        return dtos;
    }

    // --- Phương thức chuyển đổi (Helper Methods) ---

    // Chuyển Entity sang DTO
    private GiuongBenhDTO convertToDTO(GiuongBenh entity) {
        if (entity == null) return null;

        GiuongBenhDTO dto = new GiuongBenhDTO();
        dto.setId(entity.getId());
        dto.setTenGiuong(entity.getTenGiuong());
        dto.setTrangThai(entity.getTrangThai());
        
        // Lấy thông tin Phòng Bệnh (đã JOIN FETCH)
        if (entity.getPhongBenh() != null) {
            dto.setPhongBenhId(entity.getPhongBenh().getId());
            dto.setTenPhong(entity.getPhongBenh().getTenPhong());
        }
        
        // Lấy thông tin Bệnh Nhân (đã LEFT JOIN FETCH)
        if (entity.getBenhNhan() != null) {
            dto.setBenhNhanId(entity.getBenhNhan().getId());
            dto.setTenBenhNhan(entity.getBenhNhan().getHoTen()); // Giả định BenhNhan có hàm getHoTen()
        }
        
        return dto;
    }

    // Chuyển DTO sang Entity
    private GiuongBenh convertToEntity(GiuongBenhDTO dto) {
        if (dto == null) return null;

        GiuongBenh entity = new GiuongBenh();
        
        // Nếu là update (có ID), gán ID
        if (dto.getId() != null) {
             entity.setId(dto.getId());
        }
       
        entity.setTenGiuong(dto.getTenGiuong());
        entity.setTrangThai(dto.getTrangThai());

        // Lấy Phòng Bệnh (Bắt buộc)
        if (dto.getPhongBenhId() == null) {
             System.err.println("DTO thiếu phongBenhId.");
            return null;
        }
        PhongBenh phongBenh = phongBenhDAO.getPhongBenhById(dto.getPhongBenhId());
        if (phongBenh == null) {
            System.err.println("Không tìm thấy Phòng Bệnh ID: " + dto.getPhongBenhId());
            return null;
        }
        entity.setPhongBenh(phongBenh);

        // Lấy Bệnh Nhân (Không bắt buộc, có thể NULL)
        if (dto.getBenhNhanId() != null) {
            BenhNhan benhNhan = benhNhanDAO.getBenhNhanById(dto.getBenhNhanId());
            if (benhNhan != null) {
                 entity.setBenhNhan(benhNhan);
            }
            // Nếu ID có mà không tìm thấy bệnh nhân, vẫn tiếp tục (nhưng gán null)
        } else {
            entity.setBenhNhan(null);
        }

        return entity;
    }
}