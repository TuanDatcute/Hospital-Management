/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;

import model.dto.ChiTietDonThuocDTO;
import model.dto.DonThuocDTO;

/**
 *
 * @author SunnyU
 */
public class DonThuocDAO {
    //Tạo một đơn thuốc hoàn chỉnh (bao gồm cả các thuốc chi tiết)
    public DonThuocDTO createPrescription(int phieuKhamId, String loiDan, ChiTietDonThuocDTO chiTietDonThuoc){
        return new DonThuocDTO();
    }
    
    // Xem chi tiết một đơn thuốc
    public DonThuocDTO getPrescriptionDetails(int donThuocId){
        return new DonThuocDTO();
    }
}
