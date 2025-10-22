///*
// * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
// * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
// */
//package model.dao;
//
//import model.dto.ThongBaoDTO;
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//import utils.DBUtils;
//
///**
// *
// * @author quang
// */
//public class ThongBaoDAO {
//
//    public void createNotification(int taiKhoanId, String tieuDe, String noiDung) {
//        String sql = "INSERT INTO ThongBao (tieu_de, noi_dung, tai_khoan_id) VALUES (?, ?, ?)";
//        try ( Connection conn = DBUtils.getConnection(); // Sử dụng lớp kết nối của bạn
//                  PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setNString(1, tieuDe);
//            ps.setNString(2, noiDung);
//            ps.setInt(3, taiKhoanId);
//            ps.executeUpdate();
//        } catch (SQLException e) {
//            e.printStackTrace();
//            // Xử lý hoặc ném ngoại lệ thích hợp
//        }
//    }
//
//    public boolean markAsRead(int notificationId) {
//        String sql = "UPDATE ThongBao SET da_doc = 1 WHERE id = ?";
//        try ( Connection conn = DBUtils.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, notificationId);
//            int rowsUpdated = ps.executeUpdate();
//            return rowsUpdated > 0;
//        } catch (SQLException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
//
//    public List<ThongBaoDTO> listNotificationsByUser(int taiKhoanId) {
//        List<ThongBaoDTO> notifications = new ArrayList<>();
//        // Sắp xếp: Chưa đọc trước (da_doc ASC), sau đó theo thời gian gửi mới nhất (thoi_gian_gui DESC)
//        String sql = "SELECT * FROM ThongBao WHERE tai_khoan_id = ? ORDER BY da_doc ASC, thoi_gian_gui DESC";
//
//        try ( Connection conn = DBUtils.getConnection();
//            PreparedStatement ps = conn.prepareStatement(sql)) {
//
//            ps.setInt(1, taiKhoanId);
//            try ( ResultSet rs = ps.executeQuery()) {
//                while (rs.next()) {
//                    ThongBaoDTO tb = new ThongBaoDTO();
//                    tb.setThongBaoId(rs.getInt("id"));
//                    tb.setTieuDe(rs.getNString("tieu_de"));
//                    tb.setNoiDung(rs.getNString("noi_dung"));
//                    tb.setDaDoc(rs.getBoolean("da_doc"));
//                    tb.setThoiGianGui(rs.getTimestamp("thoi_gian_gui"));
//                    tb.setTaiKhoanId(rs.getInt("tai_khoan_id"));
//                    notifications.add(tb);
//                }
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//        return notifications;
//    }
//}
