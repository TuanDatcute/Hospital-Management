/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author SunnyU
 */
public class PhieuKhamBenhDAO {

    //Tạo một phiếu khám bệnh mới khi bệnh nhân đến khám.
    public PhieuKhamBenhDTO createEncounter(PhieuKhamBenhDTO thongTinKham) {

        // Câu lệnh SQL INSERT, tên bảng và cột phải khớp với CSDL của bạn
        String sql = "INSERT INTO PhieuKhamBenh (ma_phieu_kham, thoi_gian_kham, trieu_chung, nhiet_do, "
                + "huyet_ap, nhip_tim, nhip_tho, chan_doan, ket_luan, ngay_tai_kham, benh_nhan_id, "
                + "nhan_vien_id, lich_hen_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            // 1. Kết nối đến CSDL
            conn = DBUtils.getConnection();
            if (conn != null) {
                // 2. Chuẩn bị câu lệnh SQL, yêu cầu trả về khóa tự động tạo (ID)
                pst = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

                // 3. Gán các giá trị từ đối tượng DTO vào câu lệnh PreparedStatement
                pst.setString(1, thongTinKham.getMaPhieuKham());
                pst.setTimestamp(2, thongTinKham.getThoiGianKham());
                pst.setString(3, thongTinKham.getTrieuChung());
                pst.setBigDecimal(4, thongTinKham.getNhietDo());
                pst.setString(5, thongTinKham.getHuyetAp());
                pst.setInt(6, thongTinKham.getNhipTim());
                pst.setInt(7, thongTinKham.getNhipTho());
                pst.setString(8, thongTinKham.getChanDoan());
                pst.setString(9, thongTinKham.getKetLuan());
                pst.setTimestamp(10, thongTinKham.getNgayTaiKham());
                pst.setInt(11, thongTinKham.getBenhNhanId());
                pst.setInt(12, thongTinKham.getNhanVienId());

                // Xử lý LichHenID có thể là null
                if (thongTinKham.getLichHenId() != null) {
                    pst.setInt(13, thongTinKham.getLichHenId());
                } else {
                    pst.setNull(13, java.sql.Types.INTEGER);
                }

                // 4. Thực thi câu lệnh
                int affectedRows = pst.executeUpdate();

                // 5. Lấy ID tự động tăng nếu chèn thành công
                if (affectedRows > 0) {
                    rs = pst.getGeneratedKeys();
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        // Cập nhật ID cho đối tượng DTO và trả về
                        thongTinKham.setPhieuKhamBenhId(generatedId);
                        return thongTinKham;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // In lỗi ra console để debug
        } finally {
            // 6. Đóng tất cả tài nguyên để tránh rò rỉ
            try {
                if (rs != null) {
                    rs.close();
                }
                if (pst != null) {
                    pst.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        // Trả về null nếu quá trình có lỗi
        return null;
    }

    //Cập nhật thông tin trong quá trình khám (thêm chẩn đoán, kết luận...).
    public boolean updateEncounterDetails(int phieuKhamId, PhieuKhamBenhDTO thongTinCapNhat) {
        return false;
    }

    //Xem chi tiết một lần khám.
    public PhieuKhamBenhDTO getEncounterDetails(int phieuKhamId) {
        return new PhieuKhamBenhDTO();
    }

    // Thêm hoặc cập nhật các chỉ số sinh tồn.
    public boolean addVitals(int phieuKhamId, BigDecimal nhietDo, String huyetAp, Integer nhipTim, Integer nhipTho) {
        return true;
    }
}
