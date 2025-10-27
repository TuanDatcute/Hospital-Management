// path: com/dao/HoaDonDAO.java
package model.dao;

import java.util.ArrayList;
import model.Entity.HoaDon;
import util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;
import org.hibernate.Criteria;
import org.hibernate.SessionFactory;

public class HoaDonDAO {

    // Thêm hóa đơn mới
    public void addHoaDon(HoaDon hoaDon) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
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
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
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
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Dùng JOIN FETCH để tải thông tin liên quan, tránh LazyInitializationException
            Query<HoaDon> query = session.createQuery(
                    "FROM HoaDon hd "
                    + "JOIN FETCH hd.benhNhan "
                    + "JOIN FETCH hd.phieuKhamBenh "
                    + "WHERE hd.id = :id", HoaDon.class);
            query.setParameter("id", id);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Lấy hóa đơn bằng Mã Hóa Đơn
    public HoaDon getHoaDonByMaHoaDon(String maHoaDon) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<HoaDon> query = session.createQuery(
                    "FROM HoaDon hd "
                    + "JOIN FETCH hd.benhNhan "
                    + "JOIN FETCH hd.phieuKhamBenh "
                    + "WHERE hd.maHoaDon = :maHoaDon", HoaDon.class);
            query.setParameter("maHoaDon", maHoaDon);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Lấy tất cả hóa đơn của một bệnh nhân
    public List<HoaDon> getHoaDonByBenhNhanId(int benhNhanId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<HoaDon> query = session.createQuery(
                    "FROM HoaDon hd "
                    + "JOIN FETCH hd.benhNhan "
                    + "JOIN FETCH hd.phieuKhamBenh "
                    + "WHERE hd.benhNhan.id = :benhNhanId ORDER BY hd.ngayTao DESC", HoaDon.class);
            query.setParameter("benhNhanId", benhNhanId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList(); // Trả về danh sách rỗng
        }
    }

    /**
     * Lấy tất cả Hóa đơn và "fetch" (lấy kèm) luôn thông tin Bệnh nhân để tránh
     * lỗi N+1 và để Service có đủ dữ liệu.
     *
     * * @return List<HoaDon> (Danh sách các ENTITY)
     */
    public List<HoaDon> getAllInvoicesWithPatient() {
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        List<HoaDon> list = new ArrayList<>();

        // SỬA HQL:
        // Chúng ta cần JOIN FETCH *cả* 'phieuKhamBenh' (gây ra lỗi) 
        // và 'benhNhan' (mà bạn muốn tải)
        String hql = "SELECT h FROM HoaDon h "
                + "LEFT JOIN FETCH h.phieuKhamBenh pkb "
                + "LEFT JOIN FETCH h.benhNhan bn";

        // ***Lưu ý quan trọng:*** // 1. Đảm bảo 'phieuKhamBenh' và 'benhNhan' là tên thuộc tính chính xác 
        //    trong entity HoaDon.java của bạn.
        // 2. Sử dụng LEFT JOIN FETCH để đảm bảo bạn vẫn nhận được HoaDon
        //    ngay cả khi một trong các liên kết là null.
        try ( Session session = sessionFactory.openSession()) {
            // Để tránh bị trùng lặp (duplicates) khi dùng nhiều JOIN FETCH
            // chúng ta nên dùng DISTINCT_ROOT_ENTITY
            Query<HoaDon> query = session.createQuery(hql, HoaDon.class)
                    .setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

            list = query.getResultList();
        } catch (Exception e) {
            e.printStackTrace(); // Luôn log lỗi
        }

        // Danh sách này bây giờ đã có cả PhieuKhamBenh và BenhNhan được tải sẵn
        return list;
    }

    public List<HoaDon> findInvoicesByKeyword(String keyword) {
        List<HoaDon> list = new ArrayList<>();
        SessionFactory sessionFactory = HibernateUtil.getSessionFactory();

        // SỬA Ở ĐÂY: Thêm "LEFT JOIN FETCH h.phieuKhamBenh pkb"
        // Chúng ta dùng LEFT JOIN FETCH để đảm bảo vẫn lấy được Hóa đơn
        // ngay cả khi nó không có Bệnh nhân hoặc Phiếu khám bệnh liên kết.
        String hql = "SELECT DISTINCT h FROM HoaDon h "
                + "LEFT JOIN FETCH h.benhNhan bn "
                + "LEFT JOIN FETCH h.phieuKhamBenh pkb " // <-- Thêm dòng này
                + "WHERE h.maHoaDon LIKE :keyword "
                + "OR bn.hoTen LIKE :keyword "
                + "OR h.trangThai LIKE :keyword";

        try ( Session session = sessionFactory.openSession()) {
            Query<HoaDon> query = session.createQuery(hql, HoaDon.class);
            query.setParameter("keyword", "%" + keyword.trim() + "%");
            list = query.getResultList();
        } catch (Exception e) {
            System.err.println("Lỗi khi tìm kiếm hóa đơn theo từ khóa: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Lấy một Hóa đơn bằng ID trong một transaction có sẵn.
     *
     * @param id ID của hóa đơn.
     * @param session Session Hibernate đang được quản lý bởi lớp Service.
     * @return Entity HoaDon hoặc null nếu không tìm thấy.
     */
    public HoaDon getById(int id, Session session) {
        return session.get(HoaDon.class, id);
    }

    /**
     * Cập nhật một đối tượng Hóa đơn trong DB trong một transaction có sẵn.
     *
     * @param hoaDonEntity Đối tượng entity cần cập nhật.
     * @param session Session Hibernate đang được quản lý bởi lớp Service.
     */
    public void update(HoaDon hoaDonEntity, Session session) {
        session.update(hoaDonEntity);
    }
}
