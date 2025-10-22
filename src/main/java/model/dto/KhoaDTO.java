package model.dto;

public class KhoaDTO {
    private int id; 
    private String tenKhoa;
    private String moTa;

    public KhoaDTO() {
    }

    public KhoaDTO(int id, String tenKhoa, String moTa) {
        this.id = id;
        this.tenKhoa = tenKhoa;
        this.moTa = moTa;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTenKhoa() {
        return tenKhoa;
    }

    public void setTenKhoa(String tenKhoa) {
        this.tenKhoa = tenKhoa;
    }

    public String getMoTa() {
        return moTa;
    }

    public void setMoTa(String moTa) {
        this.moTa = moTa;
    }
}