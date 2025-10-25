/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.Entity;

import model.Entity.PhieuKhamBenh;
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
public class DonThuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "ngay_ke_don", nullable = false)
    private LocalDateTime ngayKeDon;

    @Column(name = "loi_dan",columnDefinition = "NVARCHAR(MAX)")
    private String loiDan;

    @OneToOne
    @JoinColumn(name = "phieu_kham_id", nullable = false, unique = true)
    private PhieuKhamBenh phieuKham;

    @OneToMany(mappedBy = "donThuoc", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChiTietDonThuoc> chiTietDonThuoc;

    public DonThuoc() {
    }

    public DonThuoc(LocalDateTime ngayKeDon, String loiDan, PhieuKhamBenh phieuKham) {
        this.ngayKeDon = ngayKeDon;
        this.loiDan = loiDan;
        this.phieuKham = phieuKham;
    }
 
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public PhieuKhamBenh getPhieuKham() {
        return phieuKham;
    }

    public void setPhieuKham(PhieuKhamBenh phieuKham) {
        this.phieuKham = phieuKham;
    }

    public List<ChiTietDonThuoc> getChiTietDonThuoc() {
        return chiTietDonThuoc;
    }

    public void setChiTietDonThuoc(List<ChiTietDonThuoc> chiTietDonThuoc) {
        this.chiTietDonThuoc = chiTietDonThuoc;
    }

    
}
