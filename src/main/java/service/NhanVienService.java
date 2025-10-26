package service;

import model.Entity.Khoa;
import model.Entity.NhanVien;
import model.Entity.TaiKhoan;
import model.dao.KhoaDAO;
import model.dao.NhanVienDAO;
import model.dao.TaiKhoanDAO;
import model.dto.NhanVienDTO;
import java.time.LocalDateTime; // Cần import LocalDateTime
import java.util.List;
import java.util.stream.Collectors;
import java.util.Collections;

/**
 * Lớp Service chứa logic nghiệp vụ cho NhanVien.
 * @author ADMIN
 */
public class NhanVienService {

    // Khởi tạo các DAO cần thiết
    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();
    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();
    private final KhoaDAO khoaDAO = new KhoaDAO(); 

    /**
     * Dịch vụ tạo một Nhân Viên mới.
     * @param dto DTO chứa thông tin nhân viên mới.
     * @return DTO của nhân viên đã được tạo (có ID).
     * @throws Exception ném ra nếu logic validation thất bại.
     */
    public NhanVienDTO createNhanVien(NhanVienDTO dto) throws Exception {
        
        // --- BƯỚC 1: LOGIC NGHIỆP VỤ (VALIDATION) ---
        
        if (dto.getHoTen() == null || dto.getHoTen().trim().isEmpty()) {
            throw new Exception("Họ tên nhân viên không được để trống.");
        }
        if (dto.getTaiKhoanId() <= 0) {
            throw new Exception("ID Tài khoản không hợp lệ. Phải gán một tài khoản.");
        }
        
        if (dto.getSoDienThoai() != null && !dto.getSoDienThoai().trim().isEmpty()) {
            if (nhanVienDAO.isSoDienThoaiExisted(dto.getSoDienThoai())) {
                throw new Exception("Số điện thoại '" + dto.getSoDienThoai() + "' đã tồn tại.");
            }
        }
        
        // --- BƯỚC 2: KIỂM TRA & LẤY CÁC ENTITY LIÊN QUAN ---
        
        TaiKhoan taiKhoanEntity = taiKhoanDAO.getById(dto.getTaiKhoanId());
        if (taiKhoanEntity == null) {
            throw new Exception("Không tìm thấy Tài khoản với ID: " + dto.getTaiKhoanId());
        }
        if (!"HOAT_DONG".equals(taiKhoanEntity.getTrangThai())) {
             throw new Exception("Không thể gán tài khoản đã bị khóa.");
        }
        
        if (nhanVienDAO.isTaiKhoanIdLinked(dto.getTaiKhoanId())) {
             throw new Exception("Tài khoản này đã được gán cho một nhân viên khác.");
        }

        Khoa khoaEntity = null; 
        if (dto.getKhoaId() != null && dto.getKhoaId() > 0) {
            khoaEntity = khoaDAO.getById(dto.getKhoaId());
            if (khoaEntity == null) {
                throw new Exception("Không tìm thấy Khoa với ID: " + dto.getKhoaId());
            }
        }

        // --- BƯỚC 3: CHUYỂN ĐỔI (MAP) ---
        NhanVien entity = toEntity(dto, taiKhoanEntity, khoaEntity);

        // --- BƯỚC 4: GỌI DAO ĐỂ LƯU ---
        NhanVien savedEntity = nhanVienDAO.create(entity);

        // --- BƯỚC 5: TRẢ VỀ DTO ---
        if (savedEntity != null) {
             // Tải lại để đảm bảo có đủ thông tin relations cho toDTO
            NhanVien fullSavedEntity = nhanVienDAO.getByIdWithRelations(savedEntity.getId());
            return toDTO(fullSavedEntity); 
        }
        return null;
    }

    /**
     * Dịch vụ cập nhật thông tin Nhân Viên.
     */
    public NhanVienDTO updateNhanVien(int nhanVienId, NhanVienDTO dto) throws Exception {
        
        // --- BƯỚC 1: LẤY ENTITY GỐC (Kèm relations để kiểm tra) ---
        NhanVien existingEntity = nhanVienDAO.getByIdWithRelations(nhanVienId); 
        if (existingEntity == null) {
            throw new Exception("Không tìm thấy nhân viên với ID: " + nhanVienId);
        }
        // Kiểm tra xem tài khoản có bị khóa không (không cho sửa nhân viên bị khóa)
        if (existingEntity.getTaiKhoan() == null || !"HOAT_DONG".equals(existingEntity.getTaiKhoan().getTrangThai())) {
             throw new Exception("Không thể cập nhật thông tin cho nhân viên có tài khoản bị khóa hoặc không tồn tại.");
        }


        // --- BƯỚC 2: VALIDATION ---
        if (dto.getHoTen() == null || dto.getHoTen().trim().isEmpty()) {
            throw new Exception("Họ tên không được để trống.");
        }
        
        String newSdt = dto.getSoDienThoai();
        if (newSdt != null && !newSdt.trim().isEmpty() && !newSdt.equals(existingEntity.getSoDienThoai())) {
            if (nhanVienDAO.isSoDienThoaiExisted(newSdt)) {
                throw new Exception("Số điện thoại '" + newSdt + "' đã tồn tại.");
            }
        }
        
        // --- BƯỚC 3: CẬP NHẬT CÁC TRƯỜNG ---
        existingEntity.setHoTen(dto.getHoTen());
        existingEntity.setNgaySinh(dto.getNgaySinh());
        existingEntity.setGioiTinh(dto.getGioiTinh());
        existingEntity.setDiaChi(dto.getDiaChi());
        existingEntity.setSoDienThoai(dto.getSoDienThoai());
        existingEntity.setChuyenMon(dto.getChuyenMon());
        existingEntity.setBangCap(dto.getBangCap());

        Integer newKhoaId = dto.getKhoaId();
        if (newKhoaId != null && newKhoaId > 0) {
            if (existingEntity.getKhoa() == null || existingEntity.getKhoa().getId() != newKhoaId) {
                Khoa khoaEntity = khoaDAO.getById(newKhoaId);
                if (khoaEntity == null) {
                    throw new Exception("Không tìm thấy Khoa mới với ID: " + newKhoaId);
                }
                existingEntity.setKhoa(khoaEntity);
            }
        } else {
            existingEntity.setKhoa(null); 
        }
        
        // --- BƯỚC 4: GỌI DAO ĐỂ CẬP NHẬT ---
        boolean success = nhanVienDAO.update(existingEntity);
        if (!success) {
            throw new Exception("Cập nhật nhân viên thất bại.");
        }

        // --- BƯỚC 5: TRẢ VỀ DTO (ĐÃ CẬP NHẬT) ---
        // Tải lại để đảm bảo dữ liệu mới nhất
        NhanVien updatedEntity = nhanVienDAO.getByIdWithRelations(nhanVienId);
        return toDTO(updatedEntity);
    }

     /**
     * Dịch vụ thực hiện Soft Delete cho Nhân Viên.
     * Bằng cách cập nhật trạng thái của TaiKhoan liên kết thành 'BI_KHOA'.
     * @param nhanVienId ID của nhân viên cần "xóa".
     * @throws Exception nếu không tìm thấy nhân viên hoặc lỗi cập nhật tài khoản.
     */
    public void softDeleteNhanVien(int nhanVienId) throws Exception {
        NhanVien nhanVien = nhanVienDAO.getByIdWithRelations(nhanVienId);
        if (nhanVien == null) {
            throw new Exception("Không tìm thấy nhân viên với ID: " + nhanVienId + " để xóa.");
        }

        TaiKhoan taiKhoan = nhanVien.getTaiKhoan();
        if (taiKhoan == null) {
             throw new Exception("Nhân viên (ID: " + nhanVienId + ") không có tài khoản liên kết.");
        }

        if (!"BI_KHOA".equals(taiKhoan.getTrangThai())) {
            taiKhoan.setTrangThai("BI_KHOA");
            taiKhoan.setUpdatedAt(LocalDateTime.now()); 

            boolean success = taiKhoanDAO.update(taiKhoan);
            if (!success) {
                throw new Exception("Không thể cập nhật trạng thái tài khoản cho nhân viên ID: " + nhanVienId);
            }
        }
    }


    /**
     * Lấy nhân viên bằng ID (chỉ trả về nếu tài khoản đang hoạt động).
     */
    public NhanVienDTO getNhanVienById(int id) throws Exception {
        NhanVien entity = nhanVienDAO.getByIdWithRelations(id); 
        if (entity == null || entity.getTaiKhoan() == null || !"HOAT_DONG".equals(entity.getTaiKhoan().getTrangThai())) {
            throw new Exception("Không tìm thấy nhân viên đang hoạt động với ID: " + id);
        }
        return toDTO(entity);
    }
    
    /**
     * Lấy tất cả nhân viên đang hoạt động.
     */
    public List<NhanVienDTO> getAllNhanVien() {
        List<NhanVien> entities = nhanVienDAO.getAllWithRelations();
        
        // Lọc ra những nhân viên có tài khoản hoạt động
        return entities.stream()
                       .filter(nv -> nv.getTaiKhoan() != null && "HOAT_DONG".equals(nv.getTaiKhoan().getTrangThai()))
                       .map(this::toDTO)
                       .collect(Collectors.toList());
    }

    /**
     * Dịch vụ tìm tất cả bác sĩ đang hoạt động.
     */
    public List<NhanVienDTO> findDoctorsBySpecialty() {
        // DAO cần được sửa để tối ưu hơn, tạm thời lọc ở Service
        List<NhanVien> entities = nhanVienDAO.findDoctorsBySpecialty("Bác sĩ"); 

        if (entities == null) {
            return Collections.emptyList();
        }

        // Lọc bác sĩ đang hoạt động
        return entities.stream()
                .filter(nv -> nv.getTaiKhoan() != null && "HOAT_DONG".equals(nv.getTaiKhoan().getTrangThai()))
                .map(this::toDTO) 
                .collect(Collectors.toList());
    }
    

    /**
     * Chuyển NhanVien (Entity) sang NhanVienDTO (Làm phẳng liên kết).
     */
    private NhanVienDTO toDTO(NhanVien entity) {
        if (entity == null) return null; // Thêm kiểm tra null
        NhanVienDTO dto = new NhanVienDTO();
        
        dto.setId(entity.getId());
        dto.setHoTen(entity.getHoTen());
        dto.setNgaySinh(entity.getNgaySinh());
        dto.setGioiTinh(entity.getGioiTinh());
        dto.setDiaChi(entity.getDiaChi());
        dto.setSoDienThoai(entity.getSoDienThoai());
        dto.setChuyenMon(entity.getChuyenMon());
        dto.setBangCap(entity.getBangCap());

        if (entity.getTaiKhoan() != null) {
            dto.setTaiKhoanId(entity.getTaiKhoan().getId());
        }
        
        if (entity.getKhoa() != null) {
            dto.setKhoaId(entity.getKhoa().getId());
        }
        
        return dto;
    }

    /**
     * Chuyển NhanVienDTO sang NhanVien (Entity) để lưu CSDL.
     */
    private NhanVien toEntity(NhanVienDTO dto, TaiKhoan taiKhoan, Khoa khoa) {
        NhanVien entity = new NhanVien();
        
        entity.setHoTen(dto.getHoTen());
        entity.setNgaySinh(dto.getNgaySinh());
        entity.setGioiTinh(dto.getGioiTinh());
        entity.setDiaChi(dto.getDiaChi());
        entity.setSoDienThoai(dto.getSoDienThoai());
        entity.setChuyenMon(dto.getChuyenMon());
        entity.setBangCap(dto.getBangCap());
        
        entity.setTaiKhoan(taiKhoan);
        entity.setKhoa(khoa);
        
        return entity;
    }
}