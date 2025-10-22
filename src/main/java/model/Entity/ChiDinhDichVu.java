/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.Entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author SunnyU
 */
@Entity
@Table(name = "ChiDinhDichVu")
public class ChiDinhDichVu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Lob
    @Column(name = "ket_qua")
    private String ketQua;

    @Column(name = "trang_thai", nullable = false)
    private String trangThai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phieu_kham_id", nullable = false)
    private PhieuKhamBenh phieuKham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dich_vu_id", nullable = false)
    private DichVu dichVu;

    // Constructors
    public ChiDinhDichVu() {
    }

    public ChiDinhDichVu(String ketQua, String trangThai, PhieuKhamBenh phieuKhamId, DichVu dichVuId) {
        this.ketQua = ketQua;
        this.trangThai = trangThai;
        this.phieuKham = phieuKhamId;
        this.dichVu = dichVuId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int chiDinhDichVuId) {
        this.id = chiDinhDichVuId;
    }

    public String getKetQua() {
        return ketQua;
    }

    public void setKetQua(String ketQua) {
        this.ketQua = ketQua;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public PhieuKhamBenh getPhieuKham() {
        return phieuKham;
    }

    public void setPhieuKham(PhieuKhamBenh phieuKham) {
        this.phieuKham = phieuKham;
    }

    public DichVu getDichVu() {
        return dichVu;
    }

    public void setDichVu(DichVu dichVu) {
        this.dichVu = dichVu;
    }

}
