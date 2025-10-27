// path: com/service/PhongBenhService.java
package service;

import model.dao.KhoaDAO;
import model.dao.PhongBenhDAO;
import model.dto.PhongBenhDTO;
import model.Entity.Khoa;
import model.Entity.PhongBenh;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import model.Entity.GiuongBenh;
import model.dao.GiuongBenhDAO;

public class PhongBenhService {

    private PhongBenhDAO phongBenhDAO;
    private KhoaDAO khoaDAO;
    private GiuongBenhDAO giuongBenhDAO; // MỚI

    // Hằng số xóa mềm
    private static final String TRANG_THAI_XOA = "NGUNG_HOAT_DONG";
    private static final String TRANG_THAI_DANG_SU_DUNG = "DANG_SU_DUNG";

    public PhongBenhService() {
        this.phongBenhDAO = new PhongBenhDAO();
        this.khoaDAO = new KhoaDAO();
        this.giuongBenhDAO = new GiuongBenhDAO();
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
            // Giả sử dto.getId() trả về int và DAO nhận int
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
     * THAY ĐỔI: Sử dụng int
     */
    public PhongBenhDTO getPhongBenhById(int id) {
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
     * THAY ĐỔI: Sử dụng int
     */
    public List<PhongBenhDTO> getPhongBenhByKhoa(int khoaId) {
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
    
    /**
     * CẬP NHẬT: Hàm searchPhongBenh
     * Sẽ gọi DAO (đã được sửa) để LỌC BỎ các phòng đã xóa.
     */
    public List<PhongBenhDTO> searchPhongBenh(String keyword) {
        List<PhongBenh> entities;
        if (keyword == null || keyword.trim().isEmpty()) {
            entities = phongBenhDAO.getAllPhongBenh(); // DAO này PHẢI được sửa
        } else {
            entities = phongBenhDAO.findPhongBenhByKeyword(keyword); // DAO này PHẢI được sửa
        }
        
        // Chuyển đổi sang DTO
        return entities.stream()
                       .map(this::convertToDTO)
                       .collect(Collectors.toList());
    }
    
    /**
     * HÀM MỚI: Xử lý logic nghiệp vụ Xóa mềm Phòng
     */
    public void softDeletePhongBenh(int roomId) throws Exception {
        // 1. Lấy TẤT CẢ giường trong phòng (kể cả giường đã xóa)
        // (Bạn cần sửa GiuongBenhDAO.getGiuongByPhongBenhId để không lọc)
        List<GiuongBenh> bedsInRoom = giuongBenhDAO.getAllGiuongByPhongBenhId_Check(roomId);

        // 2. Kiểm tra điều kiện:
        for (GiuongBenh bed : bedsInRoom) {
            if (TRANG_THAI_DANG_SU_DUNG.equals(bed.getTrangThai())) {
                throw new Exception("Không thể xóa. Phòng đang có bệnh nhân tại giường [" + bed.getTenGiuong() + "].");
            }
        }

        // 3. Nếu điều kiện OK, tiến hành xóa mềm các giường con
        for (GiuongBenh bed : bedsInRoom) {
            // Chỉ xóa mềm nếu nó chưa bị xóa
            if (!TRANG_THAI_XOA.equals(bed.getTrangThai())) {
                giuongBenhDAO.updateTrangThai(bed.getId(), TRANG_THAI_XOA);
            }
        }
        
        // 4. Xóa mềm phòng
        phongBenhDAO.updateTrangThai(roomId, TRANG_THAI_XOA);
    }

    // --- Phương thức chuyển đổi (Helper Methods) ---

    // Chuyển Entity sang DTO
    private PhongBenhDTO convertToDTO(PhongBenh entity) {
        if (entity == null) return null;

        PhongBenhDTO dto = new PhongBenhDTO();
        dto.setId(entity.getId()); // int sang int
        dto.setTenPhong(entity.getTenPhong());
        dto.setLoaiPhong(entity.getLoaiPhong());
        dto.setSucChua(entity.getSucChua());
        
        // Đã JOIN FETCH trong DAO nên không lo LazyException
        if (entity.getKhoa() != null) {
            dto.setKhoaId(entity.getKhoa().getId()); // int sang int
            dto.setTenKhoa(entity.getKhoa().getTenKhoa()); // Thêm tên khoa
        }
        
        return dto;
    }

    // Chuyển DTO sang Entity
    private PhongBenh convertToEntity(PhongBenhDTO dto) {
        if (dto == null) return null;

        PhongBenh entity = new PhongBenh();
        
        // THAY ĐỔI: Kiểm tra != 0 thay vì != null
        // Giả định 0 nghĩa là "mới" và không cần set
        if (dto.getId() != 0) {
             entity.setId(dto.getId());
        }
        
        entity.setTenPhong(dto.getTenPhong());
        entity.setLoaiPhong(dto.getLoaiPhong());
        entity.setSucChua(dto.getSucChua());

        // Lấy Khoa
        // THAY ĐỔI: Kiểm tra == 0 thay vì == null
        if (dto.getKhoaId() == 0) {
             System.err.println("DTO thiếu khoaId.");
            return null;
        }
        
        // Giả sử DAO đã cập nhật
        Khoa khoa = khoaDAO.getById(dto.getKhoaId()); 
        if (khoa == null) {
            System.err.println("Không tìm thấy Khoa ID: " + dto.getKhoaId());
            return null; // Khoa không tồn tại
        }

        entity.setKhoa(khoa);
        entity.setTrangThai("HOAT_DONG");
        return entity;
    }
}