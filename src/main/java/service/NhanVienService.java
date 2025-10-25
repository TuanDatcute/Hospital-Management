/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import model.Entity.NhanVien;
import model.dao.NhanVienDAO;
import model.dto.NhanVienDTO;

/**
 *
 * @author ADMIN
 */
public class NhanVienService {

    private final NhanVienDAO nhanVienDAO = new NhanVienDAO();

    // ... các phương thức khác ...
    /**
     * NGHIỆP VỤ: Lấy danh sách tất cả các bác sĩ.
     *
     * @return Danh sách các NhanVienDTO là bác sĩ.
     */
    public List<NhanVienDTO> findDoctorsBySpecialty() {
        // 1. Gọi DAO để lấy danh sách Entity
        List<NhanVien> entities = nhanVienDAO.findDoctorsBySpecialty("Bác sĩ");

        if (entities == null) {
            return Collections.emptyList();
        }

        // 2. Chuyển đổi danh sách Entity sang danh sách DTO để trả về
        return entities.stream()
                .map(this::toDTO) // Áp dụng hàm toDTO cho mỗi phần tử
                .collect(Collectors.toList());
    }

    /**
     * Phương thức helper để chuyển đổi NhanVien (Entity) sang NhanVienDTO.
     */
    private NhanVienDTO toDTO(NhanVien entity) {
        if (entity == null) {
            return null;
        }

        NhanVienDTO dto = new NhanVienDTO();
        dto.setId(entity.getId());
        dto.setHoTen(entity.getHoTen());
        dto.setChuyenMon(entity.getChuyenMon());
        // Gán các trường cần thiết khác cho DTO ở đây...

        return dto;
    }
}
