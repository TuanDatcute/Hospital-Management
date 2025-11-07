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
     */
    public LichHen create(LichHen lichHen) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(lichHen);
            transaction.commit();
            return lichHen;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            // Ném lỗi ra để Service biết
            throw new RuntimeException("Lỗi khi tạo lịch hẹn: " + e.getMessage(), e);
        }
    }

    /**
     * Cập nhật thông tin một lịch hẹn.
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
            // Ném lỗi ra để Service biết
            throw new RuntimeException("Lỗi khi cập nhật lịch hẹn: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy thông tin lịch hẹn bằng ID (không tải BenhNhan, BacSi).
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
     * Lấy thông tin lịch hẹn bằng ID (tải kèm BenhNhan và BacSi).
     */
    public LichHen getByIdWithRelations(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
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
     */
    public List<LichHen> findByBacSiId(int bacSiId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM LichHen lh "
                    + "JOIN FETCH lh.benhNhan "
                    + "JOIN FETCH lh.bacSi "
                    + "WHERE lh.bacSi.id = :bsId "
                    + "ORDER BY lh.thoiGianHen DESC";
            Query<LichHen> query = session.createQuery(hql, LichHen.class);
            query.setParameter("bsId", bacSiId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * CẬP NHẬT: Tìm tất cả lịch hẹn của một Bệnh Nhân (có tìm kiếm).
     *
     * @param benhNhanId ID của bệnh nhân
     * @param keyword Từ khóa để tìm (tên BS, lý do, trạng thái) hoặc null
     * @return Danh sách LichHen (đã tải relations).
     */
    public List<LichHen> findByBenhNhanId(int benhNhanId, String keyword) {
        String hql = "SELECT lh FROM LichHen lh "
                + "JOIN FETCH lh.benhNhan bn "
                + "JOIN FETCH lh.bacSi bs "
                + // bs là NhanVien
                "WHERE bn.id = :benhNhanId ";

        if (keyword != null && !keyword.trim().isEmpty()) {
            // Tìm theo tên bác sĩ (bs.hoTen), lý do khám, trạng thái
            hql += " AND (LOWER(lh.lyDoKham) LIKE LOWER(:keyword) "
                    + " OR LOWER(bs.hoTen) LIKE LOWER(:keyword) "
                    + " OR LOWER(lh.trangThai) LIKE LOWER(:keyword)) ";
        }
        hql += " ORDER BY lh.thoiGianHen DESC";

        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<LichHen> query = session.createQuery(hql, LichHen.class);
            query.setParameter("benhNhanId", benhNhanId);
            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
            }
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * HÀM MỚI: Lấy lịch hẹn (an toàn) Chỉ trả về nếu đúng ID lịch hẹn VÀ đúng
     * ID bệnh nhân. Dùng cho chức năng Hủy lịch.
     */
    public LichHen getByIdAndBenhNhanId(int lichHenId, int benhNhanId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Không cần JOIN FETCH ở đây vì Service chỉ cần kiểm tra
            String hql = "FROM LichHen lh "
                    + "WHERE lh.id = :lichHenId AND lh.benhNhan.id = :benhNhanId";
            Query<LichHen> query = session.createQuery(hql, LichHen.class);
            query.setParameter("lichHenId", lichHenId);
            query.setParameter("benhNhanId", benhNhanId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Tìm lịch hẹn của bác sĩ trong một ngày cụ thể (để lấy STT).
     */
    public List<LichHen> findByBacSiIdAndDate(int bacSiId, LocalDate date) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
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
     */
    public long countAppointmentsByDateAndDoctor(LocalDate date, int bacSiId) {
        ZoneOffset zoneOffset = ZoneOffset.of("+07:00");
        OffsetDateTime startOfDay = date.atStartOfDay().atOffset(zoneOffset);
        OffsetDateTime endOfDay = date.plusDays(1).atStartOfDay().atOffset(zoneOffset);

        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Thêm điều kiện KHÔNG TÍNH lịch đã hủy
            Query<Long> query = session.createQuery(
                    "SELECT count(lh.id) FROM LichHen lh "
                    + "WHERE lh.thoiGianHen >= :start AND lh.thoiGianHen < :end "
                    + "AND lh.bacSi.id = :bacSiId "
                    + "AND lh.trangThai != 'DA_HUY'", // <-- Thêm kiểm tra
                    Long.class
            );
            query.setParameter("start", startOfDay);
            query.setParameter("end", endOfDay);
            query.setParameter("bacSiId", bacSiId);

            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * Lấy tất cả các lịch hẹn CHƯA HOÀN THÀNH.
     */
    public List<LichHen> getAllPendingAppointments() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<LichHen> query = session.createQuery(
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
     * Lấy tất cả các lịch hẹn CHƯA HOÀN THÀNH của MỘT BÁC SĨ CỤ THỂ.
     */
    public List<LichHen> getPendingAppointmentsForDoctor(int bacSiId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<LichHen> query = session.createQuery(
                    "SELECT DISTINCT lh FROM LichHen lh "
                    + "JOIN FETCH lh.benhNhan "
                    + "JOIN FETCH lh.bacSi "
                    + "WHERE lh.bacSi.id = :bacSiId "
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
