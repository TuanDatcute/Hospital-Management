/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;

import model.Entity.BenhNhan;
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
public class BenhNhanDAO {

    /**
     * Thêm một bệnh nhân mới vào CSDL.
     * @param benhNhan Đối tượng BenhNhan (Entity)
     * @return Đối tượng BenhNhan đã được lưu (có ID) hoặc null nếu lỗi.
     */
    public BenhNhan create(BenhNhan benhNhan) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(benhNhan);
            transaction.commit();
            return benhNhan;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Cập nhật thông tin một bệnh nhân.
     * @param benhNhan Đối tượng BenhNhan (Entity) đã được thay đổi.
     * @return true nếu cập nhật thành công, false nếu lỗi.
     */
    public boolean update(BenhNhan benhNhan) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(benhNhan);
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
     * Lấy thông tin bệnh nhân bằng ID (không tải thông tin Tài Khoản).
     * @param id ID của bệnh nhân
     * @return Đối tượng BenhNhan hoặc null nếu không tìm thấy.
     */
    public BenhNhan getById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // session.get() là cách lấy nhanh bằng khóa chính
            return session.get(BenhNhan.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Lấy thông tin bệnh nhân bằng ID (tải kèm thông tin Tài Khoản).
     * Dùng khi cần chuyển đổi sang DTO.
     * @param id ID của bệnh nhân
     * @return Đối tượng BenhNhan (đã tải lazy relation) hoặc null.
     */
    public BenhNhan getByIdWithRelations(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Dùng HQL với LEFT JOIN FETCH để tải cả TaiKhoan (vì nó là nullable)
            String hql = "FROM BenhNhan bn LEFT JOIN FETCH bn.taiKhoan WHERE bn.id = :id";
            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
            query.setParameter("id", id);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy tất cả bệnh nhân trong CSDL (không tải Tài Khoản).
     * @return Một danh sách (List) các đối tượng BenhNhan.
     */
    public List<BenhNhan> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM BenhNhan";
            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    /**
     * Lấy tất cả bệnh nhân trong CSDL (tải kèm thông tin Tài Khoản).
     * @return Danh sách BenhNhan (đã tải lazy relations).
     */
    public List<BenhNhan> getAllWithRelations() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM BenhNhan bn LEFT JOIN FETCH bn.taiKhoan";
            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Kiểm tra xem Mã Bệnh Nhân đã tồn tại chưa.
     * @param maBenhNhan Mã bệnh nhân cần kiểm tra
     * @return true nếu đã tồn tại, false nếu chưa.
     */
    public boolean isMaBenhNhanExisted(String maBenhNhan) {
        if (maBenhNhan == null || maBenhNhan.trim().isEmpty()) {
            return false;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(bn.id) FROM BenhNhan bn WHERE bn.maBenhNhan = :ma";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("ma", maBenhNhan);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Failsafe
        }
    }

    /**
     * Kiểm tra xem TaiKhoan ID đã được liên kết với Bệnh nhân nào chưa.
     * @param taiKhoanId ID tài khoản cần kiểm tra
     * @return true nếu đã được liên kết, false nếu chưa.
     */
    public boolean isTaiKhoanIdLinked(int taiKhoanId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(bn.id) FROM BenhNhan bn WHERE bn.taiKhoan.id = :tkId";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("tkId", taiKhoanId);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Failsafe
        }
    }
    
    /**
     * Tìm bệnh nhân bằng Mã Bệnh Nhân.
     * @param maBenhNhan Mã bệnh nhân
     * @return Đối tượng BenhNhan (tải kèm TaiKhoan) hoặc null.
     */
    public BenhNhan findByMaBenhNhan(String maBenhNhan) {
        if (maBenhNhan == null || maBenhNhan.trim().isEmpty()) {
            return null;
        }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM BenhNhan bn LEFT JOIN FETCH bn.taiKhoan WHERE bn.maBenhNhan = :ma";
            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
            query.setParameter("ma", maBenhNhan);
            return query.uniqueResult(); 
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Tìm bệnh nhân bằng ID Tài khoản.
     * @param taiKhoanId ID tài khoản
     * @return Đối tượng BenhNhan (tải kèm TaiKhoan) hoặc null.
     */
    public BenhNhan findByTaiKhoanId(int taiKhoanId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM BenhNhan bn LEFT JOIN FETCH bn.taiKhoan WHERE bn.taiKhoan.id = :tkId";
            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
            query.setParameter("tkId", taiKhoanId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}