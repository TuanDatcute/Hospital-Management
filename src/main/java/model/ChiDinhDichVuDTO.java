/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 *
 * @author SunnyU
 */
@Entity
public class ChiDinhDichVuDTO {

    @Id
    private int chiDinhDichVuId;

    @Column(name = "ket_qua")
    private String ketQua;

    @Column(name = "trang_thai")
    private String trangThai;

    @ManyToOne
    @JoinColumn(name = "phieu_kham_id")
    private PhieuKhamBenhDTO phieuKhamId;

    @ManyToOne
    @JoinColumn(name = "dich_vu_id")
    private DichVuDTO dichVuId;

    // Constructors
    public ChiDinhDichVuDTO() {
    }

    public ChiDinhDichVuDTO(int id, String ketQua, String trangThai, PhieuKhamBenhDTO phieuKhamId, DichVuDTO dichVuId) {
        this.chiDinhDichVuId = id;
        this.ketQua = ketQua;
        this.trangThai = trangThai;
        this.phieuKhamId = phieuKhamId;
        this.dichVuId = dichVuId;
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

    public PhieuKhamBenhDTO getPhieuKhamId() {
        return phieuKhamId;
    }

    public void setPhieuKhamId(PhieuKhamBenhDTO phieuKhamId) {
        this.phieuKhamId = phieuKhamId;
    }

    public DichVuDTO getDichVuId() {
        return dichVuId;
    }

    public void setDichVuId(DichVuDTO dichVuId) {
        this.dichVuId = dichVuId;
    }
}
