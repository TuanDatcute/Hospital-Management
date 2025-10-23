// path: com/dao/HoaDonDAO.java
package model.dao;

import model.Entity.HoaDon;
import util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;

public class HoaDonDAO {

    // Thêm hóa đơn mới
    public void addHoaDon(HoaDon hoaDon) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(hoaDon);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Cập nhật hóa đơn
    public void updateHoaDon(HoaDon hoaDon) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(hoaDon);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Lấy hóa đơn bằng ID
    public HoaDon getHoaDonById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Dùng JOIN FETCH để tải thông tin liên quan, tránh LazyInitializationException
            Query<HoaDon> query = session.createQuery(
                "FROM HoaDon hd " +
                "JOIN FETCH hd.benhNhan " +
                "JOIN FETCH hd.phieuKhamBenh " +
                "WHERE hd.id = :id", HoaDon.class);
            query.setParameter("id", id);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Lấy hóa đơn bằng Mã Hóa Đơn
    public HoaDon getHoaDonByMaHoaDon(String maHoaDon) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<HoaDon> query = session.createQuery(
                "FROM HoaDon hd " +
                "JOIN FETCH hd.benhNhan " +
                "JOIN FETCH hd.phieuKhamBenh " +
                "WHERE hd.maHoaDon = :maHoaDon", HoaDon.class);
            query.setParameter("maHoaDon", maHoaDon);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Lấy tất cả hóa đơn của một bệnh nhân
    public List<HoaDon> getHoaDonByBenhNhanId(int benhNhanId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<HoaDon> query = session.createQuery(
                "FROM HoaDon hd " +
                "JOIN FETCH hd.benhNhan " +
                "JOIN FETCH hd.phieuKhamBenh " +
                "WHERE hd.benhNhan.id = :benhNhanId ORDER BY hd.ngayTao DESC", HoaDon.class);
            query.setParameter("benhNhanId", benhNhanId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList(); // Trả về danh sách rỗng
        }
    }
}