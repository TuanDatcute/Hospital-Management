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
public class ChiDinhDichVuDAO {
    
    //Tạo một yêu cầu thực hiện dịch vụ.
    public ChiDinhDichVuDTO createServiceRequest(int phieuKhamId,int  dichVuId){
        return new ChiDinhDichVuDTO();
    }
    
    //Cập nhật trạng thái thực hiện.
    public boolean updateServiceRequestStatus(int requestId,String trangThaiMoi){
        return false;
    }
    
    //Nhập kết quả cho dịch vụ đã hoàn thành.
    public boolean enterResult(int requestId,String ketQua){
        return true;
    }
    
    // Lấy danh sách các dịch vụ được chỉ định trong một phiếu khám.
    public ArrayList<DichVuDTO> listRequestsByEncounter(int phieuKhamId){
        return new ArrayList<DichVuDTO>();
    }
    
    // tìm 1 cd dịch vụ bằng id
     public ChiDinhDichVuDTO findService(int chiDinhDichVuId){
        return new ChiDinhDichVuDTO();
    }
}
