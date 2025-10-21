/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

/**
 *
 * @author SunnyU
 */
public class ChiTietDonThuocDTO {

    private int id;
    private int soLuong;
    private String lieuDung;
    private int donThuocId;
    private int thuocId;

    // Constructors
    public ChiTietDonThuocDTO() {
    }

    public ChiTietDonThuocDTO(int id, int soLuong, String lieuDung, int donThuocId, int thuocId) {
        this.id = id;
        this.soLuong = soLuong;
        this.lieuDung = lieuDung;
        this.donThuocId = donThuocId;
        this.thuocId = thuocId;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public String getLieuDung() {
        return lieuDung;
    }

    public void setLieuDung(String lieuDung) {
        this.lieuDung = lieuDung;
    }

    public int getDonThuocId() {
        return donThuocId;
    }

    public void setDonThuocId(int donThuocId) {
        this.donThuocId = donThuocId;
    }

    public int getThuocId() {
        return thuocId;
    }

    public void setThuocId(int thuocId) {
        this.thuocId = thuocId;
    }
}
