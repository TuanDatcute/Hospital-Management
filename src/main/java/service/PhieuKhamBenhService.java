/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import model.Entity.PhieuKhamBenh;
import model.dao.PhieuKhamBenhDAO;
import model.dto.PhieuKhamBenhDTO;

/**
 *
 * @author SunnyU
 */
public class PhieuKhamBenhService {

    public PhieuKhamBenhDTO createEncounter(PhieuKhamBenhDTO dto) throws Exception {
        // --- BƯỚC 1: LOGIC NGHIỆP VỤ (VALIDATION) ---
        PhieuKhamBenhDAO dao = new PhieuKhamBenhDAO();
        // Kiểm tra xem mã phiếu khám có bị trùng không
        if (dao.isMaPhieuKhamExisted(dto.getMaPhieuKham())) {
            // Ném ra một ngoại lệ cụ thể để SePPrvlet có thể bắt
            throw new Exception("Mã phiếu khám '" + dto.getMaPhieuKham() + "' đã tồn tại. Vui lòng sử dụng mã khác.");
        }
        // Các logic kiểm tra khác...
        if (dto.getBenhNhanId() <= 0 || dto.getBacSiId() <= 0) {
            throw new Exception("ID Bệnh nhân hoặc Bác sĩ không hợp lệ.");
        }

        // --- CÁC BƯỚC TIẾP THEO GIỮ NGUYÊN ---
        PhieuKhamBenh entity = toEntity(dto);
        PhieuKhamBenh savedEntity = dao.create(entity);
        if (savedEntity != null) {
            return toDTO(savedEntity);
        }
        return null;
    }

    private PhieuKhamBenh toEntity(PhieuKhamBenhDTO dto) {
        PhieuKhamBenh entity = new PhieuKhamBenh();
        entity.setMaPhieuKham(dto.getMaPhieuKham());
        entity.setThoiGianKham(dto.getThoiGianKham());
        entity.setTrieuChung(dto.getTrieuChung());
        entity.setNhietDo(dto.getNhietDo());
        entity.setHuyetAp(dto.getHuyetAp());
        entity.setNhipTim(dto.getNhipTim());
        entity.setNhipTho(dto.getNhipTho());
        entity.setChanDoan(dto.getChanDoan());
        entity.setKetLuan(dto.getKetLuan());
//      BenhNhan benhNhanEntity = benhNhanDAO.getById(dto.getBenhNhanId());
//      entity.setBenhNhan(benhNhanEntity);
//      NhanVien nhanVienEntity = nhanVienDAO.getById(dto.getNhanVienId());
//      entity.setBacSi(nhanVienEntity);
        return entity;
    }

    private PhieuKhamBenhDTO toDTO(PhieuKhamBenh entity) {
        PhieuKhamBenhDTO dto = new PhieuKhamBenhDTO();
        dto.setId(entity.getId());
        dto.setMaPhieuKham(entity.getMaPhieuKham());
        dto.setThoiGianKham(entity.getThoiGianKham());
        dto.setTrieuChung(entity.getTrieuChung());
        dto.setNhietDo(entity.getNhietDo());
        dto.setHuyetAp(entity.getHuyetAp());
        dto.setNhipTim(entity.getNhipTim());
        dto.setNhipTho(entity.getNhipTho());
        entity.setChanDoan(dto.getChanDoan());
        entity.setKetLuan(dto.getKetLuan());
        if (entity.getBenhNhan() != null) {
            dto.setBenhNhanId(entity.getBenhNhan().getId());
            dto.setTenBenhNhan(entity.getBenhNhan().getHoTen());
        }
        if (entity.getBacSi() != null) {
            dto.setBacSiId(entity.getBacSi().getId());
            dto.setTenBacSi(entity.getBacSi().getHoTen());
        }
        if (entity.getDonThuoc() != null) {
            dto.setDonThuoc(entity.getDonThuoc());
        }
        return dto;
    }
}
