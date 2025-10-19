/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.ArrayList;

/**
 *
 * @author SunnyU
 */
public class DichVuDAO {

    public DichVuDTO createService(DichVuDTO thongTin) {
        return new DichVuDTO();
    }

    public boolean updateServiceInfo(int serviceId, DichVuDTO thongTinMoi) {
        return true;
    }

    public ArrayList<DichVuDTO> listAllServices() {
        ArrayList<DichVuDTO> list = new ArrayList<>();
        return list;
    }

    public DichVuDTO findServiceById(int serviceId) {
        return new DichVuDTO();
    }
}
