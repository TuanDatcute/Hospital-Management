// path: com/dto/ThongBaoDTO.java
package model.dto;

import java.time.LocalDateTime;

public class ThongBaoDTO {
    private Long id;
    private String tieuDe;
    private String noiDung;
    private boolean daDoc;
    private LocalDateTime thoiGianGui;
    private Long taiKhoanId; // Chỉ lưu ID của người nhận

    // --- Constructors ---
    public ThongBaoDTO() {
    }

    public ThongBaoDTO(Long id, String tieuDe, String noiDung, boolean daDoc, LocalDateTime thoiGianGui, Long taiKhoanId) {
        this.id = id;
        this.tieuDe = tieuDe;
        this.noiDung = noiDung;
        this.daDoc = daDoc;
        this.thoiGianGui = thoiGianGui;
        this.taiKhoanId = taiKhoanId;
    }

    // --- Getters and Setters ---
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public LocalDateTime getThoiGianGui() {
        return thoiGianGui;
    }

    public void setThoiGianGui(LocalDateTime thoiGianGui) {
        this.thoiGianGui = thoiGianGui;
    }

    public Long getTaiKhoanId() {
        return taiKhoanId;
    }

    public void setTaiKhoanId(Long taiKhoanId) {
        this.taiKhoanId = taiKhoanId;
    }
}