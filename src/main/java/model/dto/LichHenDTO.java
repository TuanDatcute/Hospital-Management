package model.dto;

import java.time.OffsetDateTime;

/**
 *
 * @author ADMIN
 */
// Xóa @Entity và @Id
public class LichHenDTO {
    
    private int id;
    private Integer stt;
    private OffsetDateTime thoiGianHen; // Dùng kiểu hiện đại
    private String lyDoKham;
    private String trangThai; // Dùng String để khớp CSDL
    private String ghiChu; // Thêm trường bị thiếu
    
    // =====DAT=======
    private int benhNhanId;
    private int bacSiId; // Đổi tên từ nhanVienId thành bacSiId cho khớp
    private String tenBenhNhan;
    private String tenBacSi;
    //================datend===
    public LichHenDTO() {
    }

    public LichHenDTO(int id, Integer stt, OffsetDateTime thoiGianHen, String lyDoKham, String trangThai, String ghiChu, int benhNhanId, int bacSiId) {
        this.id = id;
        this.stt = stt;
        this.thoiGianHen = thoiGianHen;
        this.lyDoKham = lyDoKham;
        this.trangThai = trangThai;
        this.ghiChu = ghiChu;
        this.benhNhanId = benhNhanId;
        this.bacSiId = bacSiId;
    }

    public String getTenBenhNhan() {
        return tenBenhNhan;
    }

    public void setTenBenhNhan(String tenBenhNhan) {
        this.tenBenhNhan = tenBenhNhan;
    }

    public String getTenBacSi() {
        return tenBacSi;
    }

    public void setTenBacSi(String tenBacSi) {
        this.tenBacSi = tenBacSi;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getStt() {
        return stt;
    }

    public void setStt(Integer stt) {
        this.stt = stt;
    }

    public OffsetDateTime getThoiGianHen() {
        return thoiGianHen;
    }

    public void setThoiGianHen(OffsetDateTime thoiGianHen) {
        this.thoiGianHen = thoiGianHen;
    }

    public String getLyDoKham() {
        return lyDoKham;
    }

    public void setLyDoKham(String lyDoKham) {
        this.lyDoKham = lyDoKham;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }

    public int getBenhNhanId() {
        return benhNhanId;
    }

    public void setBenhNhanId(int benhNhanId) {
        this.benhNhanId = benhNhanId;
    }

    public int getBacSiId() {
        return bacSiId;
    }

    public void setBacSiId(int bacSiId) {
        this.bacSiId = bacSiId;
    }
}