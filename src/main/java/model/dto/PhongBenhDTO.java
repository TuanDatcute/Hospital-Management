/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dto;

/**
 *
 * @author quang
 */
public class PhongBenhDTO {
    private int phongBenhId;
    private String tenPhong;
    private String loaiPhong;
    private int sucChua;
    private int khoaId;

    public PhongBenhDTO() {
    }

    public PhongBenhDTO(int phongBenhID, String tenPhong, String loaiPhong, int sucChua, int khoaID) {
        this.phongBenhId = phongBenhID;
        this.tenPhong = tenPhong;
        this.loaiPhong = loaiPhong;
        this.sucChua = sucChua;
        this.khoaId = khoaID;
    }

    public int getPhongBenhID() {
        return phongBenhId;
    }

    public void setPhongBenhID(int phongBenhID) {
        this.phongBenhId = phongBenhID;
    }

    public String getTenPhong() {
        return tenPhong;
    }

    public void setTenPhong(String tenPhong) {
        this.tenPhong = tenPhong;
    }

    public String getLoaiPhong() {
        return loaiPhong;
    }

    public void setLoaiPhong(String loaiPhong) {
        this.loaiPhong = loaiPhong;
    }

    public int getSucChua() {
        return sucChua;
    }

    public void setSucChua(int sucChua) {
        this.sucChua = sucChua;
    }

    public int getKhoaID() {
        return khoaId;
    }

    public void setKhoaID(int khoaID) {
        this.khoaId = khoaID;
    }
    
    
}
