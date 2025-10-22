package model.Entity;

import java.time.OffsetDateTime; // Kiểu dữ liệu chính xác cho DATETIMEOFFSET
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "LichHen")
public class LichHen {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "stt")
    private Integer stt; // Có thể NULL

    @Column(name = "thoi_gian_hen", nullable = false)
    private OffsetDateTime thoiGianHen;

    @Column(name = "ly_do_kham")
    private String lyDoKham;

    @Column(name = "trang_thai", nullable = false)
    private String trangThai; // Phải là String để khớp với CSDL

    @Column(name = "ghi_chu")
    private String ghiChu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "benh_nhan_id", nullable = false)
    private BenhNhan benhNhan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bac_si_id", nullable = false) // Tên cột trong CSDL là bac_si_id
    private NhanVien bacSi; // Tham chiếu đến NhanVien

    // Constructors
    public LichHen() {
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

    public BenhNhan getBenhNhan() {
        return benhNhan;
    }

    public void setBenhNhan(BenhNhan benhNhan) {
        this.benhNhan = benhNhan;
    }

    public NhanVien getBacSi() {
        return bacSi;
    }

    public void setBacSi(NhanVien bacSi) {
        this.bacSi = bacSi;
    }
}