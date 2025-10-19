/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.sql.Date;

/**
 *
 * @author quang
 */
public class GiaoDichThanhToanDTO {
    private int giaoDichThanhToanId;
    private BigDecimal soTien;
    private String phuongThuc;
    private Date thoiGianGiaoDich;
    private int hoaDonId;

    public GiaoDichThanhToanDTO() {
    }

    public GiaoDichThanhToanDTO(int giaoDichThanhToanID, BigDecimal soTien, String phuongThuc, Date thoiGianGiaoDich, int hoaDonID) {
        this.giaoDichThanhToanId = giaoDichThanhToanID;
        this.soTien = soTien;
        this.phuongThuc = phuongThuc;
        this.thoiGianGiaoDich = thoiGianGiaoDich;
        this.hoaDonId = hoaDonID;
    }

    public int getGiaoDichThanhToanID() {
        return giaoDichThanhToanId;
    }

    public void setGiaoDichThanhToanID(int giaoDichThanhToanID) {
        this.giaoDichThanhToanId = giaoDichThanhToanID;
    }

    public BigDecimal getSoTien() {
        return soTien;
    }

    public void setSoTien(BigDecimal soTien) {
        this.soTien = soTien;
    }

    public String getPhuongThuc() {
        return phuongThuc;
    }

    public void setPhuongThuc(String phuongThuc) {
        this.phuongThuc = phuongThuc;
    }

    public Date getThoiGianGiaoDich() {
        return thoiGianGiaoDich;
    }

    public void setThoiGianGiaoDich(Date thoiGianGiaoDich) {
        this.thoiGianGiaoDich = thoiGianGiaoDich;
    }

    public int getHoaDonID() {
        return hoaDonId;
    }

    public void setHoaDonID(int hoaDonID) {
        this.hoaDonId = hoaDonID;
    }
    
    
}
