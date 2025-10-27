package model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;

/**
 * DTO (Data Transfer Object) cho Phiếu Khám Bệnh. Dùng để vận chuyển dữ liệu
 * giữa các tầng và ra ngoài API. Lớp này là một POJO đơn giản, KHÔNG chứa các
 * annotation của Hibernate.
 */
public class PhieuKhamBenhDTO {

    // --- Thông tin cơ bản của Phiếu Khám ---
    private int id;
    private String maPhieuKham;
    private LocalDateTime thoiGianKham;
    private String trieuChung;
    private String chanDoan;
    private String ketLuan;
    private LocalDateTime ngayTaiKham;

    // --- Chỉ số sinh tồn ---
    private BigDecimal nhietDo;
    private String huyetAp;
    private Integer nhipTim;
    private Integer nhipTho;

    // --- Thông tin liên kết (đã được "làm phẳng") ---
    private int benhNhanId;
    private String tenBenhNhan; // Tốt! Giữ lại để tiện hiển thị
    private int bacSiId;
    private String tenBacSi;    // Tốt! Giữ lại để tiện hiển thị
    private Integer lichHenId;

    private DonThuocDTO donThuoc;
    private List<ChiDinhDichVuDTO> danhSachChiDinh;

    //cho jsp format thời gian 
    private String formattedThoiGianKham = getThoiGianKhamFormatted();
    private String formattedNgayTaiKham = getNgayTaiKhamFormatted();

    // --- Constructors ---
    public PhieuKhamBenhDTO() {
    }

    public LocalDateTime getThoiGianKham() {
        return thoiGianKham;
    }

    public void setThoiGianKham(LocalDateTime thoiGianKham) {
        this.thoiGianKham = thoiGianKham;
    }

    public String getTrieuChung() {
        return trieuChung;
    }

    public void setTrieuChung(String trieuChung) {
        this.trieuChung = trieuChung;
    }

    public String getChanDoan() {
        return chanDoan;
    }

    public void setChanDoan(String chanDoan) {
        this.chanDoan = chanDoan;
    }

    public String getKetLuan() {
        return ketLuan;
    }

    public void setKetLuan(String ketLuan) {
        this.ketLuan = ketLuan;
    }

    public LocalDateTime getNgayTaiKham() {
        return ngayTaiKham;
    }

    public void setNgayTaiKham(LocalDateTime ngayTaiKham) {
        this.ngayTaiKham = ngayTaiKham;
    }

    public BigDecimal getNhietDo() {
        return nhietDo;
    }

    public void setNhietDo(BigDecimal nhietDo) {
        this.nhietDo = nhietDo;
    }

    public String getHuyetAp() {
        return huyetAp;
    }

    public void setHuyetAp(String huyetAp) {
        this.huyetAp = huyetAp;
    }

    public Integer getNhipTim() {
        return nhipTim;
    }

    public void setNhipTim(Integer nhipTim) {
        this.nhipTim = nhipTim;
    }

    public Integer getNhipTho() {
        return nhipTho;
    }

    public void setNhipTho(Integer nhipTho) {
        this.nhipTho = nhipTho;
    }

    public int getBenhNhanId() {
        return benhNhanId;
    }

    public void setBenhNhanId(int benhNhanId) {
        this.benhNhanId = benhNhanId;
    }

    public String getTenBenhNhan() {
        return tenBenhNhan;
    }

    public void setTenBenhNhan(String tenBenhNhan) {
        this.tenBenhNhan = tenBenhNhan;
    }

    public int getBacSiId() {
        return bacSiId;
    }

    public void setBacSiId(int bacSiId) {
        this.bacSiId = bacSiId;
    }

    public String getTenBacSi() {
        return tenBacSi;
    }

    public void setTenBacSi(String tenBacSi) {
        this.tenBacSi = tenBacSi;
    }

    public Integer getLichHenId() {
        return lichHenId;
    }

    public void setLichHenId(Integer lichHenId) {
        this.lichHenId = lichHenId;
    }

    public PhieuKhamBenhDTO(String maPhieuKham, LocalDateTime thoiGianKham, String trieuChung, String chanDoan, String ketLuan, LocalDateTime ngayTaiKham, BigDecimal nhietDo, String huyetAp, Integer nhipTim, Integer nhipTho, int benhNhanId, String tenBenhNhan, int bacSiId, String tenBacSi, Integer lichHenId, DonThuocDTO donThuoc, List<ChiDinhDichVuDTO> danhSachChiDinh) {
        this.maPhieuKham = maPhieuKham;
        this.thoiGianKham = thoiGianKham;
        this.trieuChung = trieuChung;
        this.chanDoan = chanDoan;
        this.ketLuan = ketLuan;
        this.ngayTaiKham = ngayTaiKham;
        this.nhietDo = nhietDo;
        this.huyetAp = huyetAp;
        this.nhipTim = nhipTim;
        this.nhipTho = nhipTho;
        this.benhNhanId = benhNhanId;
        this.tenBenhNhan = tenBenhNhan;
        this.bacSiId = bacSiId;
        this.tenBacSi = tenBacSi;
        this.lichHenId = lichHenId;
        this.donThuoc = donThuoc;
        this.danhSachChiDinh = danhSachChiDinh;
    }

    // --- Getters and Setters (Giữ nguyên như code của bạn) ---
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMaPhieuKham() {
        return maPhieuKham;
    }

    public void setMaPhieuKham(String maPhieuKham) {
        this.maPhieuKham = maPhieuKham;
    }

    // ... (tất cả các getters và setters khác) ...
    public DonThuocDTO getDonThuoc() {
        return donThuoc;
    }

    public void setDonThuoc(DonThuocDTO donThuoc) {
        this.donThuoc = donThuoc;
    }

    public List<ChiDinhDichVuDTO> getDanhSachChiDinh() {
        return danhSachChiDinh;
    }

    public void setDanhSachChiDinh(List<ChiDinhDichVuDTO> danhSachChiDinh) {
        this.danhSachChiDinh = danhSachChiDinh;
    }

    public String getFormattedThoiGianKham() {
        return formattedThoiGianKham;
    }

    public String getFormattedNgayTaiKham() {
        return formattedNgayTaiKham;
    }

    public String getThoiGianKhamFormatted() {
        if (this.thoiGianKham == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        return this.thoiGianKham.format(formatter);
    }

    public String getNgayTaiKhamFormatted() {
        if (this.ngayTaiKham == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        return this.ngayTaiKham.format(formatter);
    }
}
