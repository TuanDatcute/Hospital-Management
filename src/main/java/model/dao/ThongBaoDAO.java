// path: com/dao/ThongBaoDAO.java
package model.dao;

import model.Entity.ThongBao;
import util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory; // Import SessionFactory

import java.util.ArrayList; // Import ArrayList
import java.util.Collections; // Import Collections
import java.util.List;

public class ThongBaoDAO {

    // === THÊM MỚI: Hằng số trạng thái ===
    private static final String TRANG_THAI_ACTIVE = "HOAT_DONG"; // Hoặc HOAT_DONG
    private static final String TRANG_THAI_XOA = "NGUNG_HOAT_DONG";   // Hoặc NGUNG_HOAT_DON
    // ===================================

    private final SessionFactory sessionFactory; // Nên dùng instance thay vì gọi static

    public ThongBaoDAO() {
        this.sessionFactory = HibernateUtil.getSessionFactory(); // Khởi tạo 1 lần
    }

    // Thêm một thông báo mới (Giữ nguyên - Tự quản lý transaction)
    public void addThongBao(ThongBao thongBao) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            // Đảm bảo trạng thái mặc định
            if (thongBao.getTrangThai() == null) {
                thongBao.setTrangThai(TRANG_THAI_ACTIVE);
            }
            session.save(thongBao);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace(); // Nên dùng logger
             // Ném lỗi ra
            throw new RuntimeException("Lỗi khi thêm thông báo: " + e.getMessage(), e);
        }
    }

    /**
     * HÀM MỚI: Lưu entity trong session hiện tại (Service quản lý transaction).
     * Dùng cho việc tạo hàng loạt.
     */
    public void saveInSession(ThongBao thongBao, Session session) {
         // Đảm bảo trạng thái mặc định
        if (thongBao.getTrangThai() == null) {
            thongBao.setTrangThai(TRANG_THAI_ACTIVE);
        }
        session.save(thongBao); // Chỉ save, không commit
    }


    // Cập nhật thông báo (Hàm cũ - ít dùng)
    public void updateThongBao(ThongBao thongBao) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(thongBao); // Hoặc merge(thongBao) nếu cần
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace(); // Nên dùng logger
            throw new RuntimeException("Lỗi khi cập nhật thông báo: " + e.getMessage(), e);
        }
    }

    /**
     * HÀM MỚI: Cập nhật entity trong session hiện tại (Service quản lý transaction).
     * Dùng cho hàm update của Service.
     */
    public void updateInSession(ThongBao thongBao, Session session) {
        session.update(thongBao); // Chỉ update, không commit
    }

    /**
     * Lấy thông báo bằng ID (Cập nhật: Thêm JOIN FETCH)
     * Vẫn lấy thông báo đã bị xóa mềm.
     */
    public ThongBao getThongBaoById(int id) {
        try (Session session = sessionFactory.openSession()) {
             // Thêm LEFT JOIN FETCH để lấy luôn thông tin TaiKhoan nếu có
             Query<ThongBao> query = session.createQuery(
                 "FROM ThongBao tb LEFT JOIN FETCH tb.taiKhoan WHERE tb.id = :id",
                 ThongBao.class
             );
             query.setParameter("id", id);
             return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            return null;
        }
    }

    /**
     * HÀM MỚI: Lấy thông báo bằng ID trong session hiện tại.
     * Dùng cho hàm update của Service.
     */
     public ThongBao getThongBaoById(int id, Session session) {
         // Không cần JOIN FETCH vì entity sẽ được quản lý bởi session
         return session.get(ThongBao.class, id);
     }


    // Lấy tất cả thông báo của một tài khoản cụ thể (Hàm cũ - ít dùng)
    public List<ThongBao> getThongBaoByTaiKhoanId(int taiKhoanId) {
        try (Session session = sessionFactory.openSession()) {
            Query<ThongBao> query = session.createQuery(
                "FROM ThongBao WHERE taiKhoan.id = :taiKhoanId ORDER BY thoiGianGui DESC",
                ThongBao.class
            );
            query.setParameter("taiKhoanId", taiKhoanId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            return Collections.emptyList(); // Trả về list rỗng
        }
    }

    // Lấy các thông báo chưa đọc của một tài khoản (Hàm cũ - ít dùng)
    public List<ThongBao> getThongBaoChuaDocByTaiKhoanId(int taiKhoanId) {
        try (Session session = sessionFactory.openSession()) {
            Query<ThongBao> query = session.createQuery(
                "FROM ThongBao WHERE taiKhoan.id = :taiKhoanId AND daDoc = false ORDER BY thoiGianGui DESC",
                ThongBao.class
            );
            query.setParameter("taiKhoanId", taiKhoanId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            return Collections.emptyList(); // Trả về list rỗng
        }
    }

    /**
     * HÀM MỚI: Lấy tất cả thông báo ACTIVE.
     */
    public List<ThongBao> findAllActive() {
        try (Session session = sessionFactory.openSession()) {
            Query<ThongBao> query = session.createQuery(
                "FROM ThongBao tb JOIN FETCH tb.taiKhoan " + // Fetch tài khoản
                "WHERE tb.trangThai = :trangThai ORDER BY tb.thoiGianGui DESC",
                ThongBao.class
            );
            query.setParameter("trangThai", TRANG_THAI_ACTIVE);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

     /**
     * HÀM MỚI: Tìm kiếm thông báo ACTIVE theo keyword.
     */
    public List<ThongBao> findActiveByKeyword(String keyword) {
        String searchPattern = "%" + keyword.trim().toLowerCase() + "%";
        try (Session session = sessionFactory.openSession()) {
            Query<ThongBao> query = session.createQuery(
                "SELECT DISTINCT tb FROM ThongBao tb JOIN FETCH tb.taiKhoan tk " + // Fetch tài khoản
                "WHERE tb.trangThai = :trangThai " +
                "AND (LOWER(tb.tieuDe) LIKE :keyword OR LOWER(tb.noiDung) LIKE :keyword) " +
                "ORDER BY tb.thoiGianGui DESC",
                ThongBao.class
            );
            query.setParameter("trangThai", TRANG_THAI_ACTIVE);
            query.setParameter("keyword", searchPattern);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * HÀM MỚI: Lấy thông báo ACTIVE cho một tài khoản.
     */
    public List<ThongBao> getActiveByTaiKhoanId(int taiKhoanId) {
        try (Session session = sessionFactory.openSession()) {
            Query<ThongBao> query = session.createQuery(
                "FROM ThongBao WHERE taiKhoan.id = :taiKhoanId " +
                "AND trangThai = :trangThai ORDER BY thoiGianGui DESC",
                ThongBao.class
            );
            query.setParameter("taiKhoanId", taiKhoanId);
            query.setParameter("trangThai", TRANG_THAI_ACTIVE);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * HÀM MỚI: Lấy thông báo ACTIVE và CHƯA ĐỌC cho một tài khoản.
     */
    public List<ThongBao> getUnreadActiveByTaiKhoanId(int taiKhoanId) {
        try (Session session = sessionFactory.openSession()) {
            Query<ThongBao> query = session.createQuery(
                "FROM ThongBao WHERE taiKhoan.id = :taiKhoanId " +
                "AND daDoc = false AND trangThai = :trangThai ORDER BY thoiGianGui DESC",
                ThongBao.class
            );
            query.setParameter("taiKhoanId", taiKhoanId);
            query.setParameter("trangThai", TRANG_THAI_ACTIVE);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

     /**
     * HÀM MỚI: Cập nhật trạng thái (Xóa mềm). Tự quản lý transaction.
     */
    public void updateTrangThai(int id, String newTrangThai) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            ThongBao notification = session.get(ThongBao.class, id);
            if (notification != null) {
                notification.setTrangThai(newTrangThai);
                session.update(notification);
                transaction.commit();
            } else {
                 System.err.println("Không tìm thấy thông báo ID " + id + " để cập nhật trạng thái.");
                 if(transaction != null) transaction.rollback(); // Rollback nếu không tìm thấy
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi cập nhật trạng thái thông báo: " + e.getMessage(), e);
        }
    }

     /**
     * HÀM MỚI: Đánh dấu đã đọc. Tự quản lý transaction.
     * @return true nếu cập nhật thành công, false nếu không tìm thấy hoặc đã đọc rồi.
     */
    public boolean markAsRead(int id) {
        Transaction transaction = null;
        boolean success = false;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            ThongBao notification = session.get(ThongBao.class, id);
            if (notification != null && !notification.isDaDoc()) {
                notification.setDaDoc(true);
                session.update(notification);
                transaction.commit();
                success = true;
            } else {
                 if(transaction != null) transaction.rollback(); // Rollback nếu không cần update
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            // Không ném lỗi ra ngoài, chỉ trả về false
        }
        return success;
    }
}