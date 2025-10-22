/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

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
public class ChiDinhDichVuDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int chiDinhDichVuId;

    @Lob
    @Column(name = "ket_qua")
    private String ketQua;

    @Column(name = "trang_thai", nullable = false)
    private String trangThai;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "phieu_kham_id", nullable = false)
    private PhieuKhamBenhDTO phieuKham;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dich_vu_id", nullable = false)
    private DichVuDTO dichVu;

    // Constructors
    public ChiDinhDichVuDTO() {
    }

    public ChiDinhDichVuDTO(int id, String ketQua, String trangThai, PhieuKhamBenhDTO phieuKhamId, DichVuDTO dichVuId) {
        this.chiDinhDichVuId = id;
        this.ketQua = ketQua;
        this.trangThai = trangThai;
        this.phieuKham = phieuKhamId;
        this.dichVu = dichVuId;
    }

    // Getters and Setters

    public int getChiDinhDichVuId() {
        return chiDinhDichVuId;
    }

    public void setChiDinhDichVuId(int chiDinhDichVuId) {
        this.chiDinhDichVuId = chiDinhDichVuId;
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

    public PhieuKhamBenhDTO getPhieuKham() {
        return phieuKham;
    }

    public void setPhieuKham(PhieuKhamBenhDTO phieuKham) {
        this.phieuKham = phieuKham;
    }

    public DichVuDTO getDichVu() {
        return dichVu;
    }

    public void setDichVu(DichVuDTO dichVu) {
        this.dichVu = dichVu;
    }
    
}
