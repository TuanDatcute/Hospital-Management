/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author SunnyU
 */
@Entity
@Table(name = "DonThuoc")
public class DonThuocDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer donThuocId;

    @Column(name = "ngay_ke_don", nullable = false)
    private LocalDateTime ngayKeDon;

    @Lob
    @Column(name = "loi_dan")
    private String loiDan;

    @OneToOne
    @JoinColumn(name = "phieu_kham_id", nullable = false, unique = true)
    private PhieuKhamBenhDTO phieuKham;

    @OneToMany(mappedBy = "donThuoc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietDonThuocDTO> chiTietDonThuoc;

    public DonThuocDTO() {
    }

    public DonThuocDTO(Integer donThuocId, LocalDateTime ngayKeDon, String loiDan, PhieuKhamBenhDTO phieuKham) {
        this.donThuocId = donThuocId;
        this.ngayKeDon = ngayKeDon;
        this.loiDan = loiDan;
        this.phieuKham = phieuKham;
    }
 
    public Integer getDonThuocId() {
        return donThuocId;
    }

    public void setDonThuocId(Integer donThuocId) {
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

    public PhieuKhamBenhDTO getPhieuKham() {
        return phieuKham;
    }

    public void setPhieuKham(PhieuKhamBenhDTO phieuKham) {
        this.phieuKham = phieuKham;
    }

    public List<ChiTietDonThuocDTO> getChiTietDonThuoc() {
        return chiTietDonThuoc;
    }

    public void setChiTietDonThuoc(List<ChiTietDonThuocDTO> chiTietDonThuoc) {
        this.chiTietDonThuoc = chiTietDonThuoc;
    }

    
}
