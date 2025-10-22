/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;

import model.dto.ThuocDTO;

/**
 *
 * @author SunnyU
 */
public class ThuocDAO {

    // Thêm một loại thuốc mới vào kho.
    public ThuocDTO createMedication(ThuocDTO thongTin) {
        return new ThuocDTO();
    }
    
    //Cập nhật thông tin, đơn giá.
    public boolean updateMedicationInfo(int thuocId, ThuocDTO thongTinMoi){
        return false;
    }
    
    //Cập nhật số lượng tồn kho 
    public boolean updateStockQuantity(int thuocId, int soLuongThayDoi){
       return false;
    }
    
    // Tìm kiếm thuốc trong kho (bằng tên)
    public ThuocDTO findMedicationByName(String tenThuoc){
        return new ThuocDTO();
    }
    
    // Tìm kiếm thuốc trong kho (bằng id)
    public ThuocDTO findMedicationById(int thuocId){
        return new ThuocDTO();
    }
}
