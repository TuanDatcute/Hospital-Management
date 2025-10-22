// path: com/dao/GiuongBenhDAO.java
package model.dao;

import model.Entity.GiuongBenh;
import util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;

public class GiuongBenhDAO {

    // Thêm giường mới
    public void addGiuong(GiuongBenh giuong) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(giuong);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Cập nhật giường
    public void updateGiuong(GiuongBenh giuong) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(giuong);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Lấy giường bằng ID
    public GiuongBenh getGiuongById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Dùng LEFT JOIN FETCH cho benhNhan vì nó có thể là NULL
            Query<GiuongBenh> query = session.createQuery(
                "FROM GiuongBenh gb " +
                "JOIN FETCH gb.phongBenh " +
                "LEFT JOIN FETCH gb.benhNhan " + // LEFT JOIN vì benhNhan có thể null
                "WHERE gb.id = :id", 
                GiuongBenh.class
            );
            query.setParameter("id", id);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Lấy tất cả giường trong một phòng
    public List<GiuongBenh> getGiuongByPhongBenhId(long phongBenhId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<GiuongBenh> query = session.createQuery(
                "FROM GiuongBenh gb " +
                "JOIN FETCH gb.phongBenh " +
                "LEFT JOIN FETCH gb.benhNhan " +
                "WHERE gb.phongBenh.id = :phongBenhId ORDER BY gb.tenGiuong", 
                GiuongBenh.class
            );
            query.setParameter("phongBenhId", phongBenhId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
    
    // Lấy tất cả các giường trống (còn dùng được) trong một phòng
    public List<GiuongBenh> getGiuongTrongByPhong(long phongBenhId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<GiuongBenh> query = session.createQuery(
                "FROM GiuongBenh gb " +
                "JOIN FETCH gb.phongBenh " +
                "WHERE gb.phongBenh.id = :phongBenhId AND gb.trangThai = 'TRONG' " +
                "ORDER BY gb.tenGiuong", 
                GiuongBenh.class
            );
            // Không cần JOIN FETCH benhNhan vì trạng thái TRONG thì benhNhan chắc chắn là NULL
            query.setParameter("phongBenhId", phongBenhId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}