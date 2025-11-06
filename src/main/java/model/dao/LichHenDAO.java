/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;

import model.Entity.LichHen;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class LichHenDAO {

    /**
     * Thêm một lịch hẹn mới vào CSDL. STT sẽ được tự động gán bởi Trigger trong
     * CSDL.
     *
     * @param lichHen Đối tượng LichHen (Entity)
     * @return Đối tượng LichHen đã được lưu (có ID và STT) hoặc null nếu lỗi.
     */
    public LichHen create(LichHen lichHen) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(lichHen);
            transaction.commit();
            // Sau khi commit, 'lichHen' sẽ được cập nhật ID và STT (từ trigger)
            return lichHen;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Cập nhật thông tin một lịch hẹn (ví dụ: đổi trạng thái, thêm ghi chú).
     *
     * @param lichHen Đối tượng LichHen (Entity) đã được thay đổi.
     * @return true nếu cập nhật thành công, false nếu lỗi.
     */
    public boolean update(LichHen lichHen) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(lichHen);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy thông tin lịch hẹn bằng ID (không tải BenhNhan, BacSi).
     *
     * @param id ID của lịch hẹn
     * @return Đối tượng LichHen hoặc null nếu không tìm thấy.
     */
    public LichHen getById(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(LichHen.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy thông tin lịch hẹn bằng ID (tải kèm BenhNhan và BacSi). Dùng khi cần
     * chuyển đổi sang DTO.
     *
     * @param id ID của lịch hẹn
     * @return Đối tượng LichHen (đã tải lazy relations) hoặc null.
     */
    public LichHen getByIdWithRelations(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Dùng HQL với JOIN FETCH để tải cả BenhNhan và BacSi
            String hql = "FROM LichHen lh "
                    + "JOIN FETCH lh.benhNhan "
                    + "JOIN FETCH lh.bacSi "
                    + "WHERE lh.id = :id";
            Query<LichHen> query = session.createQuery(hql, LichHen.class);
            query.setParameter("id", id);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy tất cả lịch hẹn (tải kèm BenhNhan và BacSi).
     *
     * @return Danh sách LichHen (đã tải lazy relations).
     */
    public List<LichHen> getAllWithRelations() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM LichHen lh "
                    + "JOIN FETCH lh.benhNhan "
                    + "JOIN FETCH lh.bacSi";
            Query<LichHen> query = session.createQuery(hql, LichHen.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Tìm tất cả lịch hẹn của một Bác Sĩ cụ thể.
     *
     * @param bacSiId ID của bác sĩ (NhanVien)
     * @return Danh sách LichHen (đã tải relations).
     */
    public List<LichHen> findByBacSiId(int bacSiId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM LichHen lh "
                    + "JOIN FETCH lh.benhNhan "
                    + "JOIN FETCH lh.bacSi "
                    + "WHERE lh.bacSi.id = :bsId "
                    + "ORDER BY lh.thoiGianHen DESC"; // Sắp xếp mới nhất lên đầu
            Query<LichHen> query = session.createQuery(hql, LichHen.class);
            query.setParameter("bsId", bacSiId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Tìm tất cả lịch hẹn của một Bệnh Nhân cụ thể.
     *
     * @param benhNhanId ID của bệnh nhân
     * @return Danh sách LichHen (đã tải relations).
     */
    public List<LichHen> findByBenhNhanId(int benhNhanId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM LichHen lh "
                    + "JOIN FETCH lh.benhNhan "
                    + "JOIN FETCH lh.bacSi "
                    + "WHERE lh.benhNhan.id = :bnId "
                    + "ORDER BY lh.thoiGianHen DESC";
            Query<LichHen> query = session.createQuery(hql, LichHen.class);
            query.setParameter("bnId", benhNhanId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Tìm lịch hẹn của bác sĩ trong một ngày cụ thể (để lấy STT).
     *
     * @param bacSiId ID của bác sĩ
     * @param date Ngày cần kiểm tra
     * @return Danh sách LichHen (đã tải relations).
     */
    public List<LichHen> findByBacSiIdAndDate(int bacSiId, LocalDate date) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // HQL dùng hàm date() để chỉ so sánh phần ngày của OffsetDateTime
            String hql = "FROM LichHen lh "
                    + "JOIN FETCH lh.benhNhan "
                    + "JOIN FETCH lh.bacSi "
                    + "WHERE lh.bacSi.id = :bsId "
                    + "AND date(lh.thoiGianHen) = :date "
                    + "ORDER BY lh.stt ASC";
            Query<LichHen> query = session.createQuery(hql, LichHen.class);
            query.setParameter("bsId", bacSiId);
            query.setParameter("date", date);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    //============================================DẠT===================================================
    /**
     * Đếm số lượng lịch hẹn đã có trong một ngày cụ thể CHO MỘT BÁC SĨ CỤ THỂ.
     *
     * @param date Ngày cần đếm.
     * @param bacSiId ID của bác sĩ cần lọc.
     * @return Tổng số lịch hẹn của bác sĩ đó trong ngày.
     */
    public long countAppointmentsByDateAndDoctor(LocalDate date, int bacSiId) {
        // Xác định thời điểm bắt đầu và kết thúc của ngày (giữ nguyên)
        ZoneOffset zoneOffset = ZoneOffset.of("+07:00");
        OffsetDateTime startOfDay = date.atStartOfDay().atOffset(zoneOffset);
        OffsetDateTime endOfDay = date.plusDays(1).atStartOfDay().atOffset(zoneOffset);

        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // ✨ THÊM ĐIỀU KIỆN 'lh.bacSi.id = :bacSiId' VÀO TRUY VẤN ✨
            Query<Long> query = session.createQuery(
                    "SELECT count(lh.id) FROM LichHen lh "
                    + "WHERE lh.thoiGianHen >= :start AND lh.thoiGianHen < :end "
                    + "AND lh.bacSi.id = :bacSiId",
                    Long.class
            );
            query.setParameter("start", startOfDay);
            query.setParameter("end", endOfDay);
            query.setParameter("bacSiId", bacSiId); // Gán tham số bacSiId

            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * Lấy tất cả các lịch hẹn CHƯA HOÀN THÀNH.
     *
     * @return Danh sách các LichHen.
     */
    public List<LichHen> getAllPendingAppointments() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {

            // ✨ SỬ DỤNG JOIN FETCH ĐỂ TẢI SẴN DỮ LIỆU LIÊN QUAN ✨
            Query<LichHen> query = session.createQuery(
                    // Sử dụng 'SELECT DISTINCT lh' để đảm bảo kết quả là duy nhất
                    "SELECT DISTINCT lh FROM LichHen lh "
                    + "JOIN FETCH lh.benhNhan "
                    + "JOIN FETCH lh.bacSi "
                    + "WHERE lh.trangThai NOT IN ('HOAN_THANH', 'DA_DEN_KHAM', 'DA_HUY')",
                    LichHen.class
            );

            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Lấy tất cả các lịch hẹn CHƯA HOÀN THÀNH của MỘT BÁC SĨ CỤ THỂ. Tải sẵn
     * thông tin BenhNhan và BacSi.
     *
     * @param bacSiId ID của bác sĩ cần tìm lịch hẹn.
     * @return Danh sách các LichHen của bác sĩ đó.
     */
    public List<LichHen> getPendingAppointmentsForDoctor(int bacSiId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {

            Query<LichHen> query = session.createQuery(
                    "SELECT DISTINCT lh FROM LichHen lh "
                    + "JOIN FETCH lh.benhNhan "
                    + "JOIN FETCH lh.bacSi "
                    + // ✨ THÊM ĐIỀU KIỆN LỌC THEO BÁC SĨ ✨
                    "WHERE lh.bacSi.id = :bacSiId "
                    + "AND lh.trangThai NOT IN ('HOAN_THANH', 'DA_DEN_KHAM', 'DA_HUY')",
                    LichHen.class
            );

            query.setParameter("bacSiId", bacSiId);

            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
