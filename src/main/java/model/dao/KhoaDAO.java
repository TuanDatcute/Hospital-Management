/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;

import model.Entity.Khoa; 
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class KhoaDAO {

    /**
     * Thêm một khoa mới vào CSDL.
     * @param khoa Đối tượng Khoa (Entity)
     * @return Đối tượng Khoa đã được lưu (có ID) hoặc null nếu lỗi.
     */
    public Khoa create(Khoa khoa) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(khoa);
            transaction.commit();
            return khoa;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Cập nhật thông tin một khoa.
     * @param khoa Đối tượng Khoa (Entity) đã được thay đổi.
     * @return true nếu cập nhật thành công, false nếu lỗi.
     */
    public boolean update(Khoa khoa) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(khoa);
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
     * Lấy thông tin khoa bằng ID.
     * @param id ID của khoa
     * @return Đối tượng Khoa hoặc null nếu không tìm thấy.
     */
    public Khoa getById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Khoa.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy tất cả các khoa trong CSDL.
     * @return Một danh sách (List) các đối tượng Khoa.
     */
    public List<Khoa> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Khoa";
            Query<Khoa> query = session.createQuery(hql, Khoa.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Kiểm tra xem Tên khoa đã tồn tại chưa.
     * @param tenKhoa Tên khoa cần kiểm tra
     * @return true nếu đã tồn tại, false nếu chưa.
     */
    public boolean isTenKhoaExisted(String tenKhoa) {
        if (tenKhoa == null || tenKhoa.trim().isEmpty()) {
            return false;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(k.id) FROM Khoa k WHERE k.tenKhoa = :ten";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("ten", tenKhoa);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Failsafe
        }
    }
    
    /**
     * Xóa một khoa bằng ID.
     * @param id ID của khoa cần xóa
     * @return true nếu xóa thành công, false nếu lỗi.
     */
    public boolean delete(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Khoa khoa = session.get(Khoa.class, id);
            if (khoa != null) {
                session.delete(khoa);
            }
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                // Lưu ý: Nếu khoa này đang được tham chiếu (ví dụ bởi Nhân Viên),
                // CSDL sẽ ném lỗi Foreign Key và transaction sẽ rollback.
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
}