/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dto;

import java.math.BigDecimal;
import java.sql.Date;

/**
 *
 * @author quang
 */
public class HoaDonDTO {
    private int hoaDonId;
    private String maHoaDon;
    private Date ngayTao;
    private BigDecimal tongTien;
    private String trangThai;
    private int benhNhanId;
    private int phieuKhamBenhId;

    public HoaDonDTO() {
    }

    public HoaDonDTO(int hoaDonID, String maHoaDon, Date ngayTao, BigDecimal tongTien, String trangThai, int benhNhanID, int phieuKhamBenhID) {
        this.hoaDonId = hoaDonID;
        this.maHoaDon = maHoaDon;
        this.ngayTao = ngayTao;
        this.tongTien = tongTien;
        this.trangThai = trangThai;
        this.benhNhanId = benhNhanID;
        this.phieuKhamBenhId = phieuKhamBenhID;
    }

    public int getHoaDonID() {
        return hoaDonId;
    }

    public void setHoaDonID(int hoaDonID) {
        this.hoaDonId = hoaDonID;
    }

    public String getMaHoaDon() {
        return maHoaDon;
    }

    public void setMaHoaDon(String maHoaDon) {
        this.maHoaDon = maHoaDon;
    }

    public Date getNgayTao() {
        return ngayTao;
    }

    public void setNgayTao(Date ngayTao) {
        this.ngayTao = ngayTao;
    }

    public BigDecimal getTongTien() {
        return tongTien;
    }

    public void setTongTien(BigDecimal tongTien) {
        this.tongTien = tongTien;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public int getBenhNhanID() {
        return benhNhanId;
    }

    public void setBenhNhanID(int benhNhanID) {
        this.benhNhanId = benhNhanID;
    }

    public int getPhieuKhamBenhID() {
        return phieuKhamBenhId;
    }

    public void setPhieuKhamBenhID(int phieuKhamBenhID) {
        this.phieuKhamBenhId = phieuKhamBenhID;
    }
    
    
}
