/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;

import model.Entity.TaiKhoan;
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
public class TaiKhoanDAO {

    /**
     * Thêm một tài khoản mới vào CSDL.
     * @param taiKhoan Đối tượng TaiKhoan (Entity)
     * @return Đối tượng TaiKhoan đã được lưu (có ID) hoặc null nếu lỗi.
     */
    public TaiKhoan create(TaiKhoan taiKhoan) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Bắt đầu một transaction
            transaction = session.beginTransaction();
            // Lưu đối tượng
            session.save(taiKhoan);
            // Commit
            transaction.commit();
            // Trả về đối tượng đã lưu
            return taiKhoan;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Cập nhật thông tin một tài khoản.
     * @param taiKhoan Đối tượng TaiKhoan (Entity) đã được thay đổi.
     * @return true nếu cập nhật thành công, false nếu lỗi.
     */
    public boolean update(TaiKhoan taiKhoan) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(taiKhoan); // Dùng update cho đối tượng đã tồn tại
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
     * Lấy thông tin tài khoản bằng ID.
     * @param id ID của tài khoản
     * @return Đối tượng TaiKhoan hoặc null nếu không tìm thấy.
     */
    public TaiKhoan getById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Dùng session.get() để lấy bằng khóa chính
            return session.get(TaiKhoan.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Tìm một tài khoản bằng Tên đăng nhập (dùng cho Service login).
     * @param tenDangNhap Tên đăng nhập
     * @return Đối tượng TaiKhoan hoặc null nếu không tìm thấy.
     */
    public TaiKhoan findByTenDangNhap(String tenDangNhap) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Dùng HQL (Hibernate Query Language)
            String hql = "FROM TaiKhoan t WHERE t.tenDangNhap = :ten";
            Query<TaiKhoan> query = session.createQuery(hql, TaiKhoan.class);
            query.setParameter("ten", tenDangNhap);
            // uniqueResult() sẽ trả về null nếu không tìm thấy
            return query.uniqueResult(); 
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy tất cả tài khoản trong CSDL.
     * @return Một danh sách (List) các đối tượng TaiKhoan.
     */
    public List<TaiKhoan> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM TaiKhoan";
            Query<TaiKhoan> query = session.createQuery(hql, TaiKhoan.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            // Trả về danh sách rỗng nếu có lỗi
            return new ArrayList<>(); 
        }
    }

    /**
     * Kiểm tra xem Tên đăng nhập đã tồn tại chưa (dùng cho Service đăng ký).
     * @param tenDangNhap Tên đăng nhập cần kiểm tra
     * @return true nếu đã tồn tại, false nếu chưa.
     */
    public boolean isTenDangNhapExisted(String tenDangNhap) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(t.id) FROM TaiKhoan t WHERE t.tenDangNhap = :ten";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("ten", tenDangNhap);
            // uniqueResult() trả về số lượng (count)
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            // Nếu có lỗi CSDL, an toàn nhất là giả sử nó đã tồn tại
            return true; 
        }
    }

    /**
     * Kiểm tra xem Email đã tồn tại chưa (dùng cho Service đăng ký).
     * @param email Email cần kiểm tra
     * @return true nếu đã tồn tại, false nếu chưa.
     */
    public boolean isEmailExisted(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(t.id) FROM TaiKhoan t WHERE t.email = :email";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("email", email);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Failsafe
        }
    }
}