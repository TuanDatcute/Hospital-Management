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
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author SunnyU
 */
@Entity
@Table(name = "ChiTietDonThuoc")
public class ChiTietDonThuoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "so_luong")
    private int soLuong;

    @Column(name = "lieu_dung",nullable = false,columnDefinition = "NVARCHAR(255)")
    private String lieuDung;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "don_thuoc_id",nullable = false)
    private DonThuoc donThuoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "thuoc_id", nullable = false)
    private Thuoc thuoc;

    public ChiTietDonThuoc() {
    }

    public ChiTietDonThuoc(int soLuong, String lieuDung, DonThuoc donThuoc, Thuoc thuoc) {
        this.soLuong = soLuong;
        this.lieuDung = lieuDung;
        this.donThuoc = donThuoc;
        this.thuoc = thuoc;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public DonThuoc getDonThuoc() {
        return donThuoc;
    }

    public void setDonThuoc(DonThuoc donThuoc) {
        this.donThuoc = donThuoc;
    }

    public Thuoc getThuoc() {
        return thuoc;
    }

    public void setThuoc(Thuoc thuoc) {
        this.thuoc = thuoc;
    }

     
}
