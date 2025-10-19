/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Date;

/**
 *
 * @author quang
 */
public class ThongBaoDTO {
    private int thongBaoId;
    private String tieuDe;
    private String noiDung;
    private boolean daDoc;
    private Date thoiGianGui;
    private int taiKhoanId;

    public ThongBaoDTO() {
    }

    public ThongBaoDTO(int thongBaoID, String tieuDe, String noiDung, boolean daDoc, Date thoiGianGui, int taiKhoanID) {
        this.thongBaoId = thongBaoID;
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
        this.daDoc = daDoc;
        this.thoiGianGui = thoiGianGui;
        this.taiKhoanId = taiKhoanID;
    }

    public int getThongBaoID() {
        return thongBaoId;
    }

    public void setThongBaoID(int thongBaoID) {
        this.thongBaoId = thongBaoID;
    }

    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getNoiDung() {
        return noiDung;
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
    }

    public boolean isDaDoc() {
        return daDoc;
    }

    public void setDaDoc(boolean daDoc) {
        this.daDoc = daDoc;
    }

    public Date getThoiGianGui() {
        return thoiGianGui;
    }

    public void setThoiGianGui(Date thoiGianGui) {
        this.thoiGianGui = thoiGianGui;
    }

    public int getTaiKhoanID() {
        return taiKhoanId;
    }

    public void setTaiKhoanID(int taiKhoanID) {
        this.taiKhoanId = taiKhoanID;
    }
    
    
}
