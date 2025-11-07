package model.dto;

import java.time.LocalDateTime;

public class NhanVienDTO {

    private int id;
    private String hoTen;
    private LocalDateTime ngaySinh;
    private String gioiTinh;
    private String diaChi;
    private String soDienThoai;
    private String chuyenMon;
    private String bangCap;

    private int taiKhoanId;
    private Integer khoaId;
    private String vaiTro; // new from dat
    private String tenKhoa;
    // Constructors

    public NhanVienDTO() {
    }

    public NhanVienDTO(int id, String hoTen, LocalDateTime ngaySinh, String gioiTinh, String diaChi, String soDienThoai, String chuyenMon, String bangCap, int taiKhoanId, Integer khoaId, String vaiTro, String tenKhoa) {
        this.id = id;
        this.hoTen = hoTen;
        this.ngaySinh = ngaySinh;
        this.gioiTinh = gioiTinh;
        this.diaChi = diaChi;
        this.soDienThoai = soDienThoai;
        this.chuyenMon = chuyenMon;
        this.bangCap = bangCap;
        this.taiKhoanId = taiKhoanId;
        this.khoaId = khoaId;
        this.vaiTro = vaiTro;
        this.tenKhoa = tenKhoa;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHoTen() {
        return hoTen;
    }

    public void setHoTen(String hoTen) {
        this.hoTen = hoTen;
    }

    public LocalDateTime getNgaySinh() {
        return ngaySinh;
    }

    public void setNgaySinh(LocalDateTime ngaySinh) {
        this.ngaySinh = ngaySinh;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getDiaChi() {
        return diaChi;
    }

    public void setDiaChi(String diaChi) {
        this.diaChi = diaChi;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }

    public String getChuyenMon() {
        return chuyenMon;
    }

    public void setChuyenMon(String chuyenMon) {
        this.chuyenMon = chuyenMon;
    }

    public String getBangCap() {
        return bangCap;
    }

    public void setBangCap(String bangCap) {
        this.bangCap = bangCap;
    }

    public int getTaiKhoanId() {
        return taiKhoanId;
    }

    public void setTaiKhoanId(int taiKhoanId) {
        this.taiKhoanId = taiKhoanId;
    }

    public Integer getKhoaId() {
        return khoaId;
    }

    public void setKhoaId(Integer khoaId) {
        this.khoaId = khoaId;
    }

    public String getVaiTro() {
        return vaiTro;
    }

    public void setVaiTro(String vaiTro) {
        this.vaiTro = vaiTro;
    }

    public String getTenKhoa() {
        return tenKhoa;
    }

    public void setTenKhoa(String tenKhoa) {
        this.tenKhoa = tenKhoa;
    }
  
}
