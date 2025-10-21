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
public class ChiTietDonThuocDTO {

    @Id
    private int id;

    @Column(name = "so_luong")
    private int soLuong;

    @Column(name = "lieu_dung")
    private String lieuDung;

    @ManyToOne
    @JoinColumn(name = "don_thuoc_id")
    private DonThuocDTO donThuocId;

    @ManyToOne
    @JoinColumn(name = "thuoc_id")
    private ThuocDTO thuocId;

    // Constructors
    public ChiTietDonThuocDTO() {
    }

    public ChiTietDonThuocDTO(int id, int soLuong, String lieuDung, DonThuocDTO donThuocId, ThuocDTO thuocId) {
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

    public DonThuocDTO getDonThuocId() {
        return donThuocId;
    }

    public void setDonThuocId(DonThuocDTO donThuocId) {
        this.donThuocId = donThuocId;
    }

    public ThuocDTO getThuocId() {
        return thuocId;
    }

    public void setThuocId(ThuocDTO thuocId) {
        this.thuocId = thuocId;
    }
}
