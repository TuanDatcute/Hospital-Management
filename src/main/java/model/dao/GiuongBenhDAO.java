// path: com/dao/GiuongBenhDAO.java
package model.dao;

import java.util.ArrayList;
import model.Entity.GiuongBenh;
import util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;
import org.hibernate.SessionFactory;

public class GiuongBenhDAO {

    // Thêm giường mới
    public void addGiuong(GiuongBenh giuong) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
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
    public void updateGiuong(GiuongBenh giuong) { // Sửa lại: nên throw Exception
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();

            session.merge(giuong); // <-- 1. Dùng merge()

            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            // 2. Ném lỗi ra ngoài để Service biết mà xử lý (thay vì nuốt lỗi)
            throw new RuntimeException("Lỗi khi cập nhật giường: " + e.getMessage(), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    // Lấy giường bằng ID
    public GiuongBenh getGiuongById(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Dùng LEFT JOIN FETCH cho benhNhan vì nó có thể là NULL
            Query<GiuongBenh> query = session.createQuery(
                    "FROM GiuongBenh gb "
                    + "JOIN FETCH gb.phongBenh "
                    + "LEFT JOIN FETCH gb.benhNhan "
                    + // LEFT JOIN vì benhNhan có thể null
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
    public List<GiuongBenh> getGiuongByPhongBenhId(int phongBenhId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<GiuongBenh> query = session.createQuery(
                    "FROM GiuongBenh gb "
                    + "JOIN FETCH gb.phongBenh "
                    + "LEFT JOIN FETCH gb.benhNhan "
                    + "WHERE gb.phongBenh.id = :phongBenhId ORDER BY gb.tenGiuong",
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
    public List<GiuongBenh> getGiuongTrongByPhong(int phongBenhId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<GiuongBenh> query = session.createQuery(
                    "FROM GiuongBenh gb "
                    + "JOIN FETCH gb.phongBenh "
                    + "WHERE gb.phongBenh.id = :phongBenhId AND gb.trangThai = 'TRONG' "
                    + "ORDER BY gb.tenGiuong",
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

    /**
     * Lấy tất cả giường bệnh trong hệ thống
     */
    public List<GiuongBenh> getAllGiuong() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Luôn JOIN FETCH phòng bệnh (bắt buộc)
            // và LEFT JOIN FETCH bệnh nhân (có thể NULL)
            // để tránh lỗi N+1 khi chuyển đổi sang DTO
            Query<GiuongBenh> query = session.createQuery(
                    "FROM GiuongBenh gb "
                    + "JOIN FETCH gb.phongBenh "
                    + "LEFT JOIN FETCH gb.benhNhan "
                    + "ORDER BY gb.phongBenh.id, gb.tenGiuong", // Sắp xếp theo phòng, rồi theo tên giường
                    GiuongBenh.class
            );
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            // Trả về danh sách rỗng nếu có lỗi
            return Collections.emptyList();
        }
    }

    /**
     * Tìm kiếm Giường Bệnh theo từ khóa. Từ khóa sẽ so khớp với Tên giường, Tên
     * phòng, hoặc Tên bệnh nhân.
     *
     * @param keyword Từ khóa tìm kiếm
     * @return Danh sách GiuongBenh
     */
    public List<GiuongBenh> findGiuongByKeyword(String keyword) {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        List<GiuongBenh> list = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            try ( Session session = sessionFactory.openSession()) {
                list = session.createQuery("FROM GiuongBenh", GiuongBenh.class).list();
            }
            return list;
        }

        keyword = keyword.trim();

        String hql = "SELECT DISTINCT g FROM GiuongBenh g "
                + "JOIN FETCH g.phongBenh p "
                + "LEFT JOIN FETCH g.benhNhan bn "
                + "WHERE LOWER(g.tenGiuong) LIKE LOWER(:keyword) "
                + "OR LOWER(p.tenPhong) LIKE LOWER(:keyword) "
                + "OR LOWER(bn.hoTen) LIKE LOWER(:keyword)";

        try ( Session session = sessionFactory.openSession()) {
            Query<GiuongBenh> query = session.createQuery(hql, GiuongBenh.class);
            query.setParameter("keyword", "%" + keyword + "%");
            list = query.getResultList();
            System.out.println(">>> Keyword: " + keyword + " | Found: " + list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

}
