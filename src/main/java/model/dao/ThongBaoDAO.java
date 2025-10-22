// path: com/dao/ThongBaoDAO.java
package model.dao;

import model.Entity.ThongBao;
import util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class ThongBaoDAO {

    // Thêm một thông báo mới
    public void addThongBao(ThongBao thongBao) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(thongBao);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Cập nhật thông báo (thường là để đánh dấu đã đọc)
    public void updateThongBao(ThongBao thongBao) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(thongBao);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Lấy thông báo bằng ID
    public ThongBao getThongBaoById(long id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(ThongBao.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Lấy tất cả thông báo của một tài khoản cụ thể
    public List<ThongBao> getThongBaoByTaiKhoanId(long taiKhoanId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Sử dụng HQL (Hibernate Query Language)
            Query<ThongBao> query = session.createQuery(
                "FROM ThongBao WHERE taiKhoan.id = :taiKhoanId ORDER BY thoiGianGui DESC", 
                ThongBao.class
            );
            query.setParameter("taiKhoanId", taiKhoanId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    // Lấy các thông báo chưa đọc của một tài khoản
    public List<ThongBao> getThongBaoChuaDocByTaiKhoanId(long taiKhoanId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ThongBao> query = session.createQuery(
                "FROM ThongBao WHERE taiKhoan.id = :taiKhoanId AND daDoc = false ORDER BY thoiGianGui DESC", 
                ThongBao.class
            );
            query.setParameter("taiKhoanId", taiKhoanId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}