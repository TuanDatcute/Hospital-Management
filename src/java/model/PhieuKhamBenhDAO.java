/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;

/**
 *
 * @author SunnyU
 */
public class PhieuKhamBenhDAO {
    
    //Tạo một phiếu khám bệnh mới khi bệnh nhân đến khám.
    public PhieuKhamBenhDTO createEncounter(PhieuKhamBenhDTO thongTinKham){
        return thongTinKham;
    }
   
    //Cập nhật thông tin trong quá trình khám (thêm chẩn đoán, kết luận...).
    public boolean updateEncounterDetails(int phieuKhamId,PhieuKhamBenhDTO thongTinCapNhat){
        return false;
    }
    
    //Xem chi tiết một lần khám.
    public PhieuKhamBenhDTO getEncounterDetails(int phieuKhamId){
        return new PhieuKhamBenhDTO();
    }
    
    // Thêm hoặc cập nhật các chỉ số sinh tồn.
    public boolean addVitals(int phieuKhamId,BigDecimal nhietDo, String huyetAp, Integer nhipTim, Integer nhipTho){
        return true;
    }
}
