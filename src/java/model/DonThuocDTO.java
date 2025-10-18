/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.util.Date;
import java.util.List;

/**
 *
 * @author SunnyU
 */
public class DonThuocDTO {

    private int id;
    private Date ngayKeDon;
    private String loiDan;
    private int phieuKhamId;
    
    // mở rộng: Có thể chứa danh sách các chi tiết đơn thuốc
    private List<ChiTietDonThuocDTO> chiTietDonThuocList;

    // Constructors
    public DonThuocDTO() {
    }

    public DonThuocDTO(int id, Date ngayKeDon, String loiDan, int phieuKhamId) {
        this.id = id;
        this.ngayKeDon = ngayKeDon;
        this.loiDan = loiDan;
        this.phieuKhamId = phieuKhamId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getNgayKeDon() {
        return ngayKeDon;
    }

    public void setNgayKeDon(Date ngayKeDon) {
        this.ngayKeDon = ngayKeDon;
    }

    public String getLoiDan() {
        return loiDan;
    }

    public void setLoiDan(String loiDan) {
        this.loiDan = loiDan;
    }

    public int getPhieuKhamId() {
        return phieuKhamId;
    }

    public void setPhieuKhamId(int phieuKhamId) {
        this.phieuKhamId = phieuKhamId;
    }
    
    public List<ChiTietDonThuocDTO> getChiTietDonThuocList() {
        return chiTietDonThuocList;
    }

    public void setChiTietDonThuocList(List<ChiTietDonThuocDTO> chiTietDonThuocList) {
        this.chiTietDonThuocList = chiTietDonThuocList;
    }
}
