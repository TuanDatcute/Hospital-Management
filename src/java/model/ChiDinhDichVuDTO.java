/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author SunnyU
 */
public class ChiDinhDichVuDTO {

    private int id;
    private String ketQua;
    private String trangThai;
    private int phieuKhamId;
    private int dichVuId;

    // Constructors
    public ChiDinhDichVuDTO() {
    }

    public ChiDinhDichVuDTO(int id, String ketQua, String trangThai, int phieuKhamId, int dichVuId) {
        this.id = id;
        this.ketQua = ketQua;
        this.trangThai = trangThai;
        this.phieuKhamId = phieuKhamId;
        this.dichVuId = dichVuId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getPhieuKhamId() {
        return phieuKhamId;
    }

    public void setPhieuKhamId(int phieuKhamId) {
        this.phieuKhamId = phieuKhamId;
    }

    public int getDichVuId() {
        return dichVuId;
    }

    public void setDichVuId(int dichVuId) {
        this.dichVuId = dichVuId;
    }
}
