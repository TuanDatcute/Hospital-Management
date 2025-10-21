/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

/**
 *
 * @author SunnyU
 */
@Entity
public class DichVuDTO {

    @Id
    private int dichVuId;

    @Column(name = "ten_dich_vu")
    private String tenDichVu;

    @Column(name = "mo_ta")
    private String moTa;

    @Column(name = "don_gia")
    private BigDecimal donGia;

    // Constructors
    public DichVuDTO() {
    }

    public DichVuDTO(int id, String tenDichVu, String moTa, BigDecimal donGia) {
        this.dichVuId = id;
        this.tenDichVu = tenDichVu;
        this.moTa = moTa;
        this.donGia = donGia;
    }

    // Getters and Setters
    public int getDichVuId() {
        return dichVuId;
    }

    public void setDichVuId(int dichVuId) {
        this.dichVuId = dichVuId;
    }

    public String getTenDichVu() {
        return tenDichVu;
    }

    public void setTenDichVu(String tenDichVu) {
        this.tenDichVu = tenDichVu;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }
}
