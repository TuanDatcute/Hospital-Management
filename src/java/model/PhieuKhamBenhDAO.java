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
    
    public PhieuKhamBenhDTO createEncounter(PhieuKhamBenhDTO thongTinKham){
        return thongTinKham;
    }
    
    public boolean updateEncounterDetails(int phieuKhamId,PhieuKhamBenhDTO thongTinCapNhat){
        return false;
    }
    
    public PhieuKhamBenhDTO getEncounterDetails(int phieuKhamId){
        return new PhieuKhamBenhDTO();
    }
    
    public boolean addVitals(int phieuKhamId,BigDecimal nhietDo, String huyetAp, Integer nhipTim, Integer nhipTho){
        return true;
    }
}
