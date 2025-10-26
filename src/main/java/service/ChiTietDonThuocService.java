/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import exception.ValidationException;
import model.dao.ChiTietDonThuocDAO;
import model.dao.DonThuocDAO;
import model.dao.ThuocDAO;
import model.dto.ChiTietDonThuocDTO;
import model.Entity.ChiTietDonThuoc;
import model.Entity.DonThuoc;
import model.Entity.Thuoc;

public class ChiTietDonThuocService {
    
    private final ChiTietDonThuocDAO chiTietDAO = new ChiTietDonThuocDAO();
    private final DonThuocDAO donThuocDAO = new DonThuocDAO();
    private final ThuocDAO thuocDAO = new ThuocDAO();

    /**
     * NGHIỆP VỤ: Thêm một loại thuốc mới vào một đơn thuốc đã tồn tại.
     * @param dto Dữ liệu chi tiết cần thêm.
     * @return DTO của chi tiết vừa được tạo.
     * @throws Exception nếu có lỗi (đơn thuốc không tồn tại, hết thuốc...).
     */
    public ChiTietDonThuocDTO addMedicationToPrescription(ChiTietDonThuocDTO dto) throws ValidationException {
        // --- Logic nghiệp vụ: Kiểm tra ---
        DonThuoc donThuoc = donThuocDAO.getById(dto.getDonThuocId());
        if (donThuoc == null) {
            throw new ValidationException("Không tìm thấy đơn thuốc với ID: " + dto.getDonThuocId());
        }

        Thuoc thuoc = thuocDAO.getById(dto.getThuocId());
        if (thuoc == null) {
            throw new ValidationException("Không tìm thấy thuốc với ID: " + dto.getThuocId());
        }

        if (thuoc.getSoLuongTonKho() < dto.getSoLuong()) {
            throw new ValidationException("Không đủ số lượng tồn kho cho thuốc: " + thuoc.getTenThuoc());
        }

        // --- Tạo Entity và lưu ---
        ChiTietDonThuoc chiTietEntity = toEntity(dto, donThuoc, thuoc);
        ChiTietDonThuoc savedEntity = chiTietDAO.create(chiTietEntity);
        
        // --- Cập nhật tồn kho ---
        thuoc.setSoLuongTonKho(thuoc.getSoLuongTonKho() - dto.getSoLuong());
        thuocDAO.update(thuoc);

        return toDTO(savedEntity);
    }
    
    /**
     * NGHIỆP VỤ: Xóa một loại thuốc khỏi đơn thuốc và hoàn lại số lượng vào kho.
     * @param chiTietId ID của dòng chi tiết cần xóa.
     * @throws Exception nếu không tìm thấy chi tiết.
     */
    public void removeMedicationFromPrescription(int chiTietId) throws ValidationException {
        ChiTietDonThuoc chiTiet = chiTietDAO.getById(chiTietId);
        if (chiTiet == null) {
            throw new ValidationException("Không tìm thấy chi tiết đơn thuốc với ID: " + chiTietId);
        }

        Thuoc thuoc = chiTiet.getThuoc();
        int soLuongHoanTra = chiTiet.getSoLuong();
        
        // Xóa chi tiết khỏi CSDL
        chiTietDAO.delete(chiTietId);
        
        // Cập nhật lại tồn kho (cộng trả lại)
        if (thuoc != null) {
            thuoc.setSoLuongTonKho(thuoc.getSoLuongTonKho() + soLuongHoanTra);
            thuocDAO.update(thuoc);
        }
    }
    
    public ChiTietDonThuocDTO updatePrescriptionDetail(ChiTietDonThuocDTO dto) throws ValidationException {
        // 1. Lấy bản ghi Entity gốc từ CSDL
        ChiTietDonThuoc existingDetail = chiTietDAO.getById(dto.getId());
        if (existingDetail == null) {
            throw new ValidationException("Không tìm thấy chi tiết đơn thuốc để cập nhật.");
        }

        // 2. Lấy thông tin cần thiết cho việc tính toán tồn kho
        Thuoc thuoc = existingDetail.getThuoc();
        int soLuongCu = existingDetail.getSoLuong();
        int soLuongMoi = dto.getSoLuong();

        // 3. Tính toán sự thay đổi và kiểm tra tồn kho
        int soLuongThayDoi = soLuongMoi - soLuongCu; // > 0 nếu tăng, < 0 nếu giảm

        // Nếu số lượng mới lớn hơn số lượng cũ (lấy thêm thuốc)
        if (soLuongThayDoi > 0) {
            if (thuoc.getSoLuongTonKho() < soLuongThayDoi) {
                throw new ValidationException("Không đủ tồn kho. Chỉ còn " + thuoc.getSoLuongTonKho() + " sản phẩm.");
            }
        }
        
     
        // (Nếu giảm số lượng thì soLuongThayDoi là số âm, trừ đi số âm sẽ thành cộng)
        thuoc.setSoLuongTonKho(thuoc.getSoLuongTonKho() - soLuongThayDoi);
        thuocDAO.update(thuoc);

        //  Cập nhật thông tin cho chi tiết đơn thuốc 
        existingDetail.setSoLuong(soLuongMoi);
        existingDetail.setLieuDung(dto.getLieuDung());
        chiTietDAO.update(existingDetail);

        //  Trả về DTO đã được cập nhật 
        return toDTO(existingDetail);
    }
    
    // --- Các phương thức chuyển đổi ---

    private ChiTietDonThuocDTO toDTO(ChiTietDonThuoc entity) {
        if (entity == null) return null;
        
        ChiTietDonThuocDTO dto = new ChiTietDonThuocDTO();
        dto.setId(entity.getId());
        dto.setSoLuong(entity.getSoLuong());
        dto.setLieuDung(entity.getLieuDung());

        if (entity.getDonThuoc() != null) {
            dto.setDonThuocId(entity.getDonThuoc().getId());
        }
        if (entity.getThuoc() != null) {
            dto.setThuocId(entity.getThuoc().getId());
            dto.setTenThuoc(entity.getThuoc().getTenThuoc());
            dto.setDonViTinh(entity.getThuoc().getDonViTinh());
        }
        return dto;
    }
    
    private ChiTietDonThuoc toEntity(ChiTietDonThuocDTO dto, DonThuoc donThuoc, Thuoc thuoc) {
        ChiTietDonThuoc entity = new ChiTietDonThuoc();
        entity.setSoLuong(dto.getSoLuong());
        entity.setLieuDung(dto.getLieuDung());
        entity.setDonThuoc(donThuoc);
        entity.setThuoc(thuoc);
        return entity;
    }
}
