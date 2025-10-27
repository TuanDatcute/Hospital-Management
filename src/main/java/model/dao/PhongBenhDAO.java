// path: com/dao/PhongBenhDAO.java
package model.dao;

import java.util.ArrayList;
import model.Entity.PhongBenh;
import util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;
import org.hibernate.SessionFactory;

public class PhongBenhDAO {

    // Thêm phòng bệnh mới
    public void addPhongBenh(PhongBenh phongBenh) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(phongBenh);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Cập nhật phòng bệnh
    public void updatePhongBenh(PhongBenh phongBenh) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(phongBenh);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    // Lấy phòng bệnh bằng ID
    public PhongBenh getPhongBenhById(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Dùng JOIN FETCH để tải thông tin Khoa ngay lập tức
            Query<PhongBenh> query = session.createQuery(
                    "FROM PhongBenh pb JOIN FETCH pb.khoa WHERE pb.id = :id",
                    PhongBenh.class
            );
            query.setParameter("id", id);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Lấy phòng bệnh bằng Tên Phòng (duy nhất)
    public PhongBenh getPhongBenhByTenPhong(String tenPhong) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<PhongBenh> query = session.createQuery(
                    "FROM PhongBenh pb JOIN FETCH pb.khoa WHERE pb.tenPhong = :tenPhong",
                    PhongBenh.class
            );
            query.setParameter("tenPhong", tenPhong);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Lấy tất cả phòng bệnh
    public List<PhongBenh> getAllPhongBenh() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<PhongBenh> query = session.createQuery(
                    "FROM PhongBenh pb JOIN FETCH pb.khoa ORDER BY pb.tenPhong",
                    PhongBenh.class
            );
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList(); // Trả về danh sách rỗng
        }
    }

    // Lấy tất cả phòng bệnh theo Khoa
    public List<PhongBenh> getPhongBenhByKhoa(int khoaId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<PhongBenh> query = session.createQuery(
                    "FROM PhongBenh pb JOIN FETCH pb.khoa WHERE pb.khoa.id = :khoaId ORDER BY pb.tenPhong",
                    PhongBenh.class
            );
            query.setParameter("khoaId", khoaId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList(); // Trả về danh sách rỗng
        }
    }

    // (Tùy chọn) Xóa phòng bệnh
    public void deletePhongBenh(int id) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            PhongBenh phongBenh = session.get(PhongBenh.class, id);
            if (phongBenh != null) {
                session.delete(phongBenh);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<PhongBenh> findPhongBenhByKeyword(String keyword) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        List<PhongBenh> list = new ArrayList<>();

        // Nếu keyword null hoặc rỗng thì trả toàn bộ danh sách
        if (keyword == null || keyword.trim().isEmpty()) {
            try ( Session session = sessionFactory.openSession()) {
                Query<PhongBenh> query = session.createQuery(
                        "SELECT DISTINCT p FROM PhongBenh p "
                        + "LEFT JOIN FETCH p.khoa k "
                        + "ORDER BY p.tenPhong",
                        PhongBenh.class
                );
                list = query.getResultList();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
        }

        keyword = "%" + keyword.trim().toLowerCase() + "%";

        // Sử dụng LOWER() để tìm kiếm không phân biệt hoa thường
        String hql = "SELECT DISTINCT p FROM PhongBenh p "
                + "LEFT JOIN FETCH p.khoa k "
                + "WHERE LOWER(p.tenPhong) LIKE :keyword "
                + "OR LOWER(p.loaiPhong) LIKE :keyword "
                + "OR LOWER(k.tenKhoa) LIKE :keyword "
                + "ORDER BY p.tenPhong";

        try ( Session session = sessionFactory.openSession()) {
            Query<PhongBenh> query = session.createQuery(hql, PhongBenh.class);
            query.setParameter("keyword", keyword);
            list = query.getResultList();
            System.out.println(">>> Tìm thấy " + list.size() + " phòng bệnh với từ khóa: " + keyword);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

}
