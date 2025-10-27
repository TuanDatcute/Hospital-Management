// path: com/dao/GiuongBenhDAO.java
package model.dao; // Gói của bạn

import java.util.ArrayList;
import model.Entity.GiuongBenh; // Entity của bạn
import util.HibernateUtil; // Lớp util của bạn
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;
import org.hibernate.SessionFactory;

public class GiuongBenhDAO {

    // THÊM: Hằng số để quản lý trạng thái xóa mềm
    private static final String TRANG_THAI_XOA = "NGUNG_HOAT_DONG";

    // Thêm giường mới (Giữ nguyên)
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

    // Cập nhật giường (Giữ nguyên)
    public void updateGiuong(GiuongBenh giuong) {
        Transaction transaction = null;
        Session session = null;
        try {
            session = HibernateUtil.getSessionFactory().openSession();
            transaction = session.beginTransaction();
            session.merge(giuong);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Lỗi khi cập nhật giường: " + e.getMessage(), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    // Lấy giường bằng ID (Giữ nguyên)
    // Hàm này VẪN lấy giường đã bị xóa mềm,
    // để Service có thể kiểm tra logic hoặc "khôi phục" nếu cần.
    public GiuongBenh getGiuongById(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<GiuongBenh> query = session.createQuery(
                    "FROM GiuongBenh gb "
                    + "JOIN FETCH gb.phongBenh "
                    + "LEFT JOIN FETCH gb.benhNhan "
                    + "WHERE gb.id = :id",
                    GiuongBenh.class
            );
            query.setParameter("id", id);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy tất cả giường trong một phòng CẬP NHẬT: Lọc bỏ giường đã xóa mềm
     */
    public List<GiuongBenh> getGiuongByPhongBenhId(int phongBenhId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<GiuongBenh> query = session.createQuery(
                    "FROM GiuongBenh gb "
                    + "JOIN FETCH gb.phongBenh "
                    + "LEFT JOIN FETCH gb.benhNhan "
                    + "WHERE gb.phongBenh.id = :phongBenhId "
                    + "AND gb.trangThai != :trangThaiXoa " // <-- CẬP NHẬT
                    + "ORDER BY gb.tenGiuong",
                    GiuongBenh.class
            );
            query.setParameter("phongBenhId", phongBenhId);
            query.setParameter("trangThaiXoa", TRANG_THAI_XOA); // <-- CẬP NHẬT
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // Lấy tất cả các giường trống (Giữ nguyên)
    // (Vì trạng thái 'TRONG' đã tự loại 'NGUNG_HOAT_DON')
    public List<GiuongBenh> getGiuongTrongByPhong(int phongBenhId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<GiuongBenh> query = session.createQuery(
                    "FROM GiuongBenh gb "
                    + "JOIN FETCH gb.phongBenh "
                    + "WHERE gb.phongBenh.id = :phongBenhId AND gb.trangThai = 'TRONG' "
                    + "ORDER BY gb.tenGiuong",
                    GiuongBenh.class
            );
            query.setParameter("phongBenhId", phongBenhId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Lấy tất cả giường bệnh trong hệ thống CẬP NHẬT: Lọc bỏ giường đã xóa mềm
     */
    public List<GiuongBenh> getAllGiuong() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<GiuongBenh> query = session.createQuery(
                    "FROM GiuongBenh gb "
                    + "JOIN FETCH gb.phongBenh "
                    + "LEFT JOIN FETCH gb.benhNhan "
                    + "WHERE gb.trangThai != :trangThaiXoa " // <-- CẬP NHẬT
                    + "ORDER BY gb.phongBenh.id, gb.tenGiuong",
                    GiuongBenh.class
            );
            query.setParameter("trangThaiXoa", TRANG_THAI_XOA); // <-- CẬP NHẬT
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Tìm kiếm Giường Bệnh theo từ khóa. CẬP NHẬT: 1. Sửa lỗi N+1 khi keyword
     * rỗng. 2. Lọc bỏ giường đã xóa mềm.
     */
    public List<GiuongBenh> findGiuongByKeyword(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            // SỬA LỖI N+1: Gọi hàm getAllGiuong() để tận dụng JOIN FETCH
            return getAllGiuong();
        }

        keyword = keyword.trim();
        List<GiuongBenh> list = new ArrayList<>();

        // CẬP NHẬT: Thêm ( ) và AND ...
        String hql = "SELECT DISTINCT g FROM GiuongBenh g "
                + "JOIN FETCH g.phongBenh p "
                + "LEFT JOIN FETCH g.benhNhan bn "
                + "WHERE (LOWER(g.tenGiuong) LIKE LOWER(:keyword) " // <-- Thêm (
                + "   OR LOWER(p.tenPhong) LIKE LOWER(:keyword) "
                + "   OR LOWER(bn.hoTen) LIKE LOWER(:keyword)) " // <-- Thêm )
                + "AND g.trangThai != :trangThaiXoa"; // <-- CẬP NHẬT

        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<GiuongBenh> query = session.createQuery(hql, GiuongBenh.class);
            query.setParameter("keyword", "%" + keyword + "%");
            query.setParameter("trangThaiXoa", TRANG_THAI_XOA); // <-- CẬP NHẬT
            list = query.getResultList();
            System.out.println(">>> Keyword: " + keyword + " | Found: " + list.size());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    /**
     * HÀM MỚI: Cập nhật trạng thái của một giường (dùng cho Xóa mềm) Service sẽ
     * gọi hàm này.
     *
     * @param giuongId ID của giường cần cập nhật
     * @param newTrangThai Trạng thái mới (vd: "NGUNG_HOAT_DON")
     */
    public void updateTrangThai(int giuongId, String newTrangThai) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            // Lấy entity (dùng 'get' cho nhẹ)
            GiuongBenh giuong = session.get(GiuongBenh.class, giuongId);

            if (giuong != null) {
                // Chỉ cập nhật trạng thái
                giuong.setTrangThai(newTrangThai);
                session.update(giuong); // Cập nhật lại
                transaction.commit();
            }
            // Nếu không tìm thấy giường, không làm gì cả

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            // Ném lỗi ra để Service biết
            throw new RuntimeException("Lỗi khi cập nhật trạng thái giường: " + e.getMessage(), e);
        }
    }

    /**
     * HÀM MỚI: Lấy TẤT CẢ giường (kể cả giường đã xóa) trong 1 phòng Dùng cho
     * Service để kiểm tra logic xóa
     */
    public List<GiuongBenh> getAllGiuongByPhongBenhId_Check(int phongBenhId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<GiuongBenh> query = session.createQuery(
                    "FROM GiuongBenh gb "
                    + "WHERE gb.phongBenh.id = :phongBenhId",
                    GiuongBenh.class
            );
            query.setParameter("phongBenhId", phongBenhId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
