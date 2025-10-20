/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Timestamp;

/**
 *
 * @author quang
 */
public class ThongBaoDTO {
    private int thongBaoId;
    private String tieuDe;
    private String noiDung;
    private boolean daDoc;
    private Timestamp thoiGianGui;
    private int taiKhoanId;

    public ThongBaoDTO() {
    }

    public ThongBaoDTO(int thongBaoID, String tieuDe, String noiDung, boolean daDoc, Timestamp thoiGianGui, int taiKhoanID) {
        this.thongBaoId = thongBaoID;
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
        this.daDoc = daDoc;
        this.thoiGianGui = thoiGianGui;
        this.taiKhoanId = taiKhoanID;
    }

    public int getThongBaoId() {
        return thongBaoId;
    }

    public void setThongBaoId(int thongBaoID) {
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

    public Timestamp getThoiGianGui() {
        return thoiGianGui;
    }

    public void setThoiGianGui(Timestamp thoiGianGui) {
        this.thoiGianGui = thoiGianGui;
    }

    public int getTaiKhoanId() {
        return taiKhoanId;
    }

    public void setTaiKhoanId(int taiKhoanID) {
        this.taiKhoanId = taiKhoanID;
    }
    
    
}
