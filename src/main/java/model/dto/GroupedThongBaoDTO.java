// path: model/dto/GroupedThongBaoDTO.java
package model.dto;

import java.time.LocalDateTime; // Sử dụng LocalDateTime

/**
 * DTO này tự động phân tích (parse) nội dung để tách tiền tố metadata.
 */
public class GroupedThongBaoDTO {

    // 1. Các trường dữ liệu
    private String tieuDe;
    private String noiDung; // Sẽ chỉ chứa nội dung GỐC (sau khi parse)
    private LocalDateTime thoiGianGui;
    private long soLuongNguoiNhan;
    private String nguoiNhanDisplay; // THÊM MỚI: (Vd: "Vai trò: BÁC SĨ", "Tất cả")

    // 2. Constructor rỗng (Bắt buộc)
    public GroupedThongBaoDTO() {
    }

    // 3. Constructor đầy đủ (HQL sẽ gọi)
    public GroupedThongBaoDTO(String tieuDe, String noiDung, LocalDateTime thoiGianGui, long soLuongNguoiNhan) {
        this.tieuDe = tieuDe;
        this.noiDung = noiDung; // Tạm thời chứa cả tiền tố
        this.thoiGianGui = thoiGianGui;
        this.soLuongNguoiNhan = soLuongNguoiNhan;

        // Gọi hàm helper để xử lý nội dung
        parseNoiDung();
    }

    /**
     * CẬP NHẬT: Logic parseNoiDung - Chỉ trích xuất thông tin, không tra cứu
     * tên
     */
    private void parseNoiDung() {
        if (this.noiDung != null && this.noiDung.startsWith("[GROUP:")) {
            int endPrefix = this.noiDung.indexOf("]");
            if (endPrefix != -1) {
                String prefixContent = this.noiDung.substring(7, endPrefix);
                String[] parts = prefixContent.split("=");

                if (parts.length == 2) {
                    String type = parts[0];
                    String value = parts[1];

                    if ("ROLE".equals(type)) {
                        if ("ALL".equals(value)) {
                            this.nguoiNhanDisplay = "Tất cả";
                        } else {
                            this.nguoiNhanDisplay = "Vai trò: " + value;
                        }
                    } else if ("USER".equals(type)) {
                        // SỬA: Chỉ lưu ID, Service sẽ xử lý sau
                        this.nguoiNhanDisplay = "[USER_ID:" + value + "]";
                    } else {
                        this.nguoiNhanDisplay = this.soLuongNguoiNhan + " người";
                    }
                } else {
                    this.nguoiNhanDisplay = this.soLuongNguoiNhan + " người";
                }

                // Cập nhật nội dung gốc
                this.noiDung = this.noiDung.substring(endPrefix + 1);

            } else {
                this.nguoiNhanDisplay = this.soLuongNguoiNhan + " người";
            }
        } else {
            this.nguoiNhanDisplay = this.soLuongNguoiNhan + " người";
        }
    }

    // 4. Getters và Setters (Bắt buộc phải có)
    public String getTieuDe() {
        return tieuDe;
    }

    public void setTieuDe(String tieuDe) {
        this.tieuDe = tieuDe;
    }

    public String getNoiDung() {
        return noiDung; // Trả về nội dung đã được làm sạch
    }

    public void setNoiDung(String noiDung) {
        this.noiDung = noiDung;
        parseNoiDung(); // Gọi lại parse nếu nội dung bị set thủ công
    }

    public LocalDateTime getThoiGianGui() {
        return thoiGianGui;
    }

    public void setThoiGianGui(LocalDateTime thoiGianGui) {
        this.thoiGianGui = thoiGianGui;
    }

    public long getSoLuongNguoiNhan() {
        return soLuongNguoiNhan;
    }

    public void setSoLuongNguoiNhan(long soLuongNguoiNhan) {
        this.soLuongNguoiNhan = soLuongNguoiNhan;
    }

    public String getNguoiNhanDisplay() {
        return nguoiNhanDisplay;
    }

    public void setNguoiNhanDisplay(String nguoiNhanDisplay) {
        this.nguoiNhanDisplay = nguoiNhanDisplay;
    }
}
