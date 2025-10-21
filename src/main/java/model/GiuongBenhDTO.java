/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author quang
 */
public class GiuongBenhDTO {
    private int giuongBenhId;
    private String tenGiuong;
    private String trangThai;
    private int phongBenhId;
    private int benhNhanId;

    public GiuongBenhDTO() {
    }

    public GiuongBenhDTO(int giuongBenhID, String tenGiuong, String trangThai, int phongBenhID, int benhNhanID) {
        this.giuongBenhId = giuongBenhID;
        this.tenGiuong = tenGiuong;
        this.trangThai = trangThai;
        this.phongBenhId = phongBenhID;
        this.benhNhanId = benhNhanID;
    }

    public int getGiuongBenhID() {
        return giuongBenhId;
    }

    public void setGiuongBenhID(int giuongBenhID) {
        this.giuongBenhId = giuongBenhID;
    }

    public String getTenGiuong() {
        return tenGiuong;
    }

    public void setTenGiuong(String tenGiuong) {
        this.tenGiuong = tenGiuong;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public int getPhongBenhID() {
        return phongBenhId;
    }

    public void setPhongBenhID(int phongBenhID) {
        this.phongBenhId = phongBenhID;
    }

    public int getBenhNhanID() {
        return benhNhanId;
    }

    public void setBenhNhanID(int benhNhanID) {
        this.benhNhanId = benhNhanID;
    }
    
    
}
