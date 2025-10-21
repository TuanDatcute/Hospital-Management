/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

/**
 *
 * @author SunnyU
 */
@Entity
public class DonThuocDTO {
    
    @Id
    private int donThuocId;
    
    @Column(name = "ngay_ke_don")
    private LocalDateTime ngayKeDon;
    
    @Column(name = "loi_dan")
    private String loiDan;
    
    @OneToOne
    private PhieuKhamBenhDTO phieuKhamId;
    
    // mở rộng: Có thể chứa danh sách các chi tiết đơn thuốc
    private List<ChiTietDonThuocDTO> chiTietDonThuocList;

    // Constructors
    public DonThuocDTO() {
    }

    public DonThuocDTO(int id, LocalDateTime ngayKeDon, String loiDan, PhieuKhamBenhDTO phieuKhamId) {
        this.donThuocId = id;
        this.ngayKeDon = ngayKeDon;
        this.loiDan = loiDan;
        this.phieuKhamId = phieuKhamId;
    }

    // Getters and Setters
    public int getDonThuocId() {
        return donThuocId;
    }

    public void setDonThuocId(int donThuocId) {
        this.donThuocId = donThuocId;
    }

    public LocalDateTime getNgayKeDon() {
        return ngayKeDon;
    }

    public void setNgayKeDon(LocalDateTime ngayKeDon) {
        this.ngayKeDon = ngayKeDon;
    }

    public String getLoiDan() {
        return loiDan;
    }

    public void setLoiDan(String loiDan) {
        this.loiDan = loiDan;
    }

    public PhieuKhamBenhDTO getPhieuKhamId() {
        return phieuKhamId;
    }

    public void setPhieuKhamId(PhieuKhamBenhDTO phieuKhamId) {
        this.phieuKhamId = phieuKhamId;
    }
    
    public List<ChiTietDonThuocDTO> getChiTietDonThuocList() {
        return chiTietDonThuocList;
    }

    public void setChiTietDonThuocList(List<ChiTietDonThuocDTO> chiTietDonThuocList) {
        this.chiTietDonThuocList = chiTietDonThuocList;
    }
}
