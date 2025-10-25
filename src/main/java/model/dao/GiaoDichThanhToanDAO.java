// path: com/dao/GiaoDichThanhToanDAO.java
package model.dao;

import model.Entity.GiaoDichThanhToan;
import util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;

public class GiaoDichThanhToanDAO {

    // Thêm một giao dịch mới
    public void addGiaoDich(GiaoDichThanhToan giaoDich) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(giaoDich);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Lấy tất cả giao dịch của một hóa đơn
    public List<GiaoDichThanhToan> getGiaoDichByHoaDonId(int hoaDonId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Dùng JOIN FETCH để tải HoaDon, tránh LazyInitializationException
            Query<GiaoDichThanhToan> query = session.createQuery(
                "FROM GiaoDichThanhToan gdt " +
                "JOIN FETCH gdt.hoaDon " + 
                "WHERE gdt.hoaDon.id = :hoaDonId ORDER BY gdt.thoiGianGiaoDich DESC", 
                GiaoDichThanhToan.class
            );
            query.setParameter("hoaDonId", hoaDonId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList(); // Trả về danh sách rỗng nếu lỗi
        }
    }

    // (Tùy chọn) Lấy một giao dịch bằng ID
    public GiaoDichThanhToan getGiaoDichById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(GiaoDichThanhToan.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}