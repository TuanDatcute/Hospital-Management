/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.sql.Date;

/**
 *
 * @author ADMIN
 */
public class LichHenDTO {
    private int lichHenId;
    private Date thoiGianHen;
    private String lyDoKham;
    private boolean trangThaiLichHen;
    private int benhNhanId;
    private int nhanVienId;

    public LichHenDTO() {
    }

    public LichHenDTO(int lichHenId, Date thoiGianHen, String lyDoKham, boolean trangThaiLichHen, int benhNhanId, int nhanVienId) {
        this.lichHenId = lichHenId;
        this.thoiGianHen = thoiGianHen;
        this.lyDoKham = lyDoKham;
        this.trangThaiLichHen = trangThaiLichHen;
        this.benhNhanId = benhNhanId;
        this.nhanVienId = nhanVienId;
    }

    public int getLichHenId() {
        return lichHenId;
    }

    public void setLichHenId(int lichHenId) {
        this.lichHenId = lichHenId;
    }

    public Date getThoiGianHen() {
        return thoiGianHen;
    }

    public void setThoiGianHen(Date thoiGianHen) {
        this.thoiGianHen = thoiGianHen;
    }

    public String getLyDoKham() {
        return lyDoKham;
    }

    public void setLyDoKham(String lyDoKham) {
        this.lyDoKham = lyDoKham;
    }

    public boolean isTrangThaiLichHen() {
        return trangThaiLichHen;
    }

    public void setTrangThaiLichHen(boolean trangThaiLichHen) {
        this.trangThaiLichHen = trangThaiLichHen;
    }

    public int getBenhNhanId() {
        return benhNhanId;
    }

    public void setBenhNhanId(int benhNhanId) {
        this.benhNhanId = benhNhanId;
    }

    public int getNhanVienId() {
        return nhanVienId;
    }

    public void setNhanVienId(int nhanVienId) {
        this.nhanVienId = nhanVienId;
    }
    
    
}
