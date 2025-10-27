// path: com/dao/PhongBenhDAO.java
package model.dao; // Gói của bạn

import java.util.ArrayList;
import model.Entity.PhongBenh; // Entity của bạn
import util.HibernateUtil; // Lớp util của bạn
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.Collections;
import java.util.List;
import org.hibernate.SessionFactory;

public class PhongBenhDAO {

    // === THÊM MỚI: Hằng số trạng thái ===
    private static final String TRANG_THAI_XOA = "NGUNG_HOAT_DON";
    private static final String TRANG_THAI_HOAT_DONG = "HOAT_DONG";
    // ===================================

    // Thêm phòng bệnh mới (Giữ nguyên)
    public void addPhongBenh(PhongBenh phongBenh) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            // Đảm bảo trạng thái mặc định là HOAT_DONG khi thêm mới
            if (phongBenh.getTrangThai() == null) {
                phongBenh.setTrangThai(TRANG_THAI_HOAT_DONG);
            }
            session.save(phongBenh);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace(); // Nên dùng logger
            // Ném lỗi ra để Service biết
            throw new RuntimeException("Lỗi khi thêm phòng bệnh: " + e.getMessage(), e);
        }
    }

    // Cập nhật phòng bệnh (Giữ nguyên - logic trạng thái xử lý ở Service)
    public void updatePhongBenh(PhongBenh phongBenh) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(phongBenh); // Dùng update thay vì merge nếu entity đã được load
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace(); // Nên dùng logger
            // Ném lỗi ra để Service biết
            throw new RuntimeException("Lỗi khi cập nhật phòng bệnh: " + e.getMessage(), e);
        }
    }

    // Lấy phòng bệnh bằng ID (Giữ nguyên - Vẫn lấy phòng đã bị xóa mềm)
    public PhongBenh getPhongBenhById(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<PhongBenh> query = session.createQuery(
                    "FROM PhongBenh pb JOIN FETCH pb.khoa WHERE pb.id = :id",
                    PhongBenh.class
            );
            query.setParameter("id", id);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            return null;
        }
    }

    // Lấy phòng bệnh bằng Tên Phòng (Giữ nguyên - Vẫn lấy phòng đã bị xóa mềm)
    public PhongBenh getPhongBenhByTenPhong(String tenPhong) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<PhongBenh> query = session.createQuery(
                    "FROM PhongBenh pb JOIN FETCH pb.khoa WHERE pb.tenPhong = :tenPhong",
                    PhongBenh.class
            );
            query.setParameter("tenPhong", tenPhong);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            return null;
        }
    }

    /**
     * Lấy tất cả phòng bệnh CẬP NHẬT: Chỉ lấy phòng HOAT_DONG
     */
    public List<PhongBenh> getAllPhongBenh() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<PhongBenh> query = session.createQuery(
                    "FROM PhongBenh pb JOIN FETCH pb.khoa "
                    + "WHERE pb.trangThai = :trangThai "
                    + // <-- CẬP NHẬT
                    "ORDER BY pb.tenPhong",
                    PhongBenh.class
            );
            query.setParameter("trangThai", TRANG_THAI_HOAT_DONG); // <-- CẬP NHẬT
            return query.list();
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            return Collections.emptyList();
        }
    }

    /**
     * Lấy tất cả phòng bệnh theo Khoa CẬP NHẬT: Chỉ lấy phòng HOAT_DONG
     */
    public List<PhongBenh> getPhongBenhByKhoa(int khoaId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<PhongBenh> query = session.createQuery(
                    "FROM PhongBenh pb JOIN FETCH pb.khoa "
                    + "WHERE pb.khoa.id = :khoaId "
                    + "AND pb.trangThai = :trangThai "
                    + // <-- CẬP NHẬT
                    "ORDER BY pb.tenPhong",
                    PhongBenh.class
            );
            query.setParameter("khoaId", khoaId);
            query.setParameter("trangThai", TRANG_THAI_HOAT_DONG); // <-- CẬP NHẬT
            return query.list();
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            return Collections.emptyList();
        }
    }

    // === LOẠI BỎ: Hàm deletePhongBenh (xóa cứng) ===
    /*
    public void deletePhongBenh(int id) { ... }
     */
    /**
     * Tìm kiếm Phòng Bệnh theo từ khóa. CẬP NHẬT: Chỉ tìm trong các phòng
     * HOAT_DONG
     */
    public List<PhongBenh> findPhongBenhByKeyword(String keyword) {
        // Nếu keyword null hoặc rỗng thì gọi hàm getAll đã được cập nhật
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllPhongBenh(); // <-- CẬP NHẬT: Gọi hàm đã lọc
        }

        String searchPattern = "%" + keyword.trim().toLowerCase() + "%";
        List<PhongBenh> list = new ArrayList<>();

        // CẬP NHẬT: Thêm điều kiện WHERE trangThai
        String hql = "SELECT DISTINCT p FROM PhongBenh p "
                + "JOIN FETCH p.khoa k " // Dùng JOIN FETCH thay vì LEFT JOIN FETCH nếu Khoa là bắt buộc
                + "WHERE p.trangThai = :trangThai " // <-- CẬP NHẬT
                + "AND (LOWER(p.tenPhong) LIKE :keyword "
                + "  OR LOWER(p.loaiPhong) LIKE :keyword "
                + "  OR LOWER(k.tenKhoa) LIKE :keyword) "
                + "ORDER BY p.tenPhong";

        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<PhongBenh> query = session.createQuery(hql, PhongBenh.class);
            query.setParameter("trangThai", TRANG_THAI_HOAT_DONG); // <-- CẬP NHẬT
            query.setParameter("keyword", searchPattern);
            list = query.getResultList();
            System.out.println(">>> Tìm thấy " + list.size() + " phòng bệnh hoạt động với từ khóa: " + keyword);
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
        }

        return list;
    }

    /**
     * HÀM MỚI: Cập nhật trạng thái của một phòng (dùng cho Xóa mềm) Service sẽ
     * gọi hàm này.
     *
     * @param roomId ID của phòng cần cập nhật
     * @param newTrangThai Trạng thái mới (vd: "NGUNG_HOAT_DON")
     */
    public void updateTrangThai(int roomId, String newTrangThai) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            PhongBenh room = session.get(PhongBenh.class, roomId); // Dùng get() cho nhẹ
            if (room != null) {
                room.setTrangThai(newTrangThai);
                session.update(room); // Cập nhật
                transaction.commit();
            }
            // Nếu không tìm thấy phòng, không làm gì cả
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace(); // Nên dùng logger
            // Ném lỗi ra để Service biết
            throw new RuntimeException("Lỗi khi cập nhật trạng thái phòng: " + e.getMessage(), e);
        }
    }
}
