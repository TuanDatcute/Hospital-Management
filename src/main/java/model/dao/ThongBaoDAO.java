// path: com/dao/ThongBaoDAO.java
package model.dao;

import model.Entity.ThongBao;
import model.dto.GroupedThongBaoDTO; // THÊM IMPORT NÀY
import util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.hibernate.SessionFactory;

import java.time.LocalDateTime; // THÊM IMPORT NÀY
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThongBaoDAO {

    // === Hằng số trạng thái ===
    private static final String TRANG_THAI_ACTIVE = "HOAT_DONG";
    private static final String TRANG_THAI_XOA = "NGUNG_HOAT_DONG";
    // ===================================

    private final SessionFactory sessionFactory;

    public ThongBaoDAO() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    // (Các hàm addThongBao, saveInSession, updateThongBao, updateInSession, getThongBaoById... giữ nguyên)
    // ...
    public void addThongBao(ThongBao thongBao) {
        Transaction transaction = null;
        try ( Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            if (thongBao.getTrangThai() == null) {
                thongBao.setTrangThai(TRANG_THAI_ACTIVE);
            }
            session.save(thongBao);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi thêm thông báo: " + e.getMessage(), e);
        }
    }

    public void saveInSession(ThongBao thongBao, Session session) {
        if (thongBao.getTrangThai() == null) {
            thongBao.setTrangThai(TRANG_THAI_ACTIVE);
        }
        session.save(thongBao);
    }

    public void updateThongBao(ThongBao thongBao) {
        Transaction transaction = null;
        try ( Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            session.update(thongBao);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi cập nhật thông báo: " + e.getMessage(), e);
        }
    }

    public void updateInSession(ThongBao thongBao, Session session) {
        session.update(thongBao);
    }

    public ThongBao getThongBaoById(int id) {
        try ( Session session = sessionFactory.openSession()) {
            Query<ThongBao> query = session.createQuery(
                    "FROM ThongBao tb LEFT JOIN FETCH tb.taiKhoan WHERE tb.id = :id",
                    ThongBao.class
            );
            query.setParameter("id", id);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public ThongBao getThongBaoById(int id, Session session) {
        return session.get(ThongBao.class, id);
    }

    // (Các hàm getThongBaoByTaiKhoanId, getThongBaoChuaDocByTaiKhoanId, findAllActive, findActiveByKeyword... giữ nguyên)
    // ...
    public List<ThongBao> getThongBaoByTaiKhoanId(int taiKhoanId) {
        try ( Session session = sessionFactory.openSession()) {
            Query<ThongBao> query = session.createQuery(
                    "FROM ThongBao WHERE taiKhoan.id = :taiKhoanId ORDER BY thoiGianGui DESC",
                    ThongBao.class
            );
            query.setParameter("taiKhoanId", taiKhoanId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<ThongBao> getThongBaoChuaDocByTaiKhoanId(int taiKhoanId) {
        try ( Session session = sessionFactory.openSession()) {
            Query<ThongBao> query = session.createQuery(
                    "FROM ThongBao WHERE taiKhoan.id = :taiKhoanId AND daDoc = false ORDER BY thoiGianGui DESC",
                    ThongBao.class
            );
            query.setParameter("taiKhoanId", taiKhoanId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<ThongBao> findAllActive() {
        try ( Session session = sessionFactory.openSession()) {
            Query<ThongBao> query = session.createQuery(
                    "FROM ThongBao tb JOIN FETCH tb.taiKhoan "
                    + "WHERE tb.trangThai = :trangThai ORDER BY tb.thoiGianGui DESC",
                    ThongBao.class
            );
            query.setParameter("trangThai", TRANG_THAI_ACTIVE);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<ThongBao> findActiveByKeyword(String keyword) {
        String searchPattern = "%" + keyword.trim().toLowerCase() + "%";
        try ( Session session = sessionFactory.openSession()) {
            Query<ThongBao> query = session.createQuery(
                    "SELECT DISTINCT tb FROM ThongBao tb JOIN FETCH tb.taiKhoan tk "
                    + "WHERE tb.trangThai = :trangThai "
                    + "AND (LOWER(tb.tieuDe) LIKE :keyword OR LOWER(tb.noiDung) LIKE :keyword) "
                    + "ORDER BY tb.thoiGianGui DESC",
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

    // (getActiveByTaiKhoanId, getUnreadActiveByTaiKhoanId, updateTrangThai, markAsRead giữ nguyên)
    // ...
    public List<ThongBao> getActiveByTaiKhoanId(int taiKhoanId) {
        try ( Session session = sessionFactory.openSession()) {
            Query<ThongBao> query = session.createQuery(
                    "FROM ThongBao WHERE taiKhoan.id = :taiKhoanId "
                    + "AND trangThai = :trangThai ORDER BY thoiGianGui DESC",
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

    public List<ThongBao> getUnreadActiveByTaiKhoanId(int taiKhoanId) {
        try ( Session session = sessionFactory.openSession()) {
            Query<ThongBao> query = session.createQuery(
                    "FROM ThongBao WHERE taiKhoan.id = :taiKhoanId "
                    + "AND daDoc = false AND trangThai = :trangThai ORDER BY thoiGianGui DESC",
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

    public void updateTrangThai(int id, String newTrangThai) {
        Transaction transaction = null;
        try ( Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            ThongBao notification = session.get(ThongBao.class, id);
            if (notification != null) {
                notification.setTrangThai(newTrangThai);
                session.update(notification);
                transaction.commit();
            } else {
                System.err.println("Không tìm thấy thông báo ID " + id + " để cập nhật trạng thái.");
                if (transaction != null) {
                    transaction.rollback();
                }
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi cập nhật trạng thái thông báo: " + e.getMessage(), e);
        }
    }

    public boolean markAsRead(int id) {
        Transaction transaction = null;
        boolean success = false;
        try ( Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            ThongBao notification = session.get(ThongBao.class, id);
            if (notification != null && !notification.isDaDoc()) {
                notification.setDaDoc(true);
                session.update(notification);
                transaction.commit();
                success = true;
            } else {
                if (transaction != null) {
                    transaction.rollback();
                }
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return success;
    }

    // (Các hàm gộp nhóm findAllGrouped, findGroupedByKeyword giữ nguyên)
    // ...
    private static final String GROUPED_HQL_SELECT
            = "SELECT new model.dto.GroupedThongBaoDTO(tb.tieuDe, tb.noiDung, tb.thoiGianGui, COUNT(tb.id)) "
            + "FROM ThongBao tb "
            + "WHERE tb.trangThai = :trangThai ";
    private static final String GROUPED_HQL_GROUP_BY
            = " GROUP BY tb.tieuDe, tb.noiDung, tb.thoiGianGui "
            + " ORDER BY tb.thoiGianGui DESC";

    public List<GroupedThongBaoDTO> findAllGrouped() {
        String hql = GROUPED_HQL_SELECT + GROUPED_HQL_GROUP_BY;
        try ( Session session = sessionFactory.openSession()) {
            Query<GroupedThongBaoDTO> query = session.createQuery(hql, GroupedThongBaoDTO.class);
            query.setParameter("trangThai", TRANG_THAI_ACTIVE);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<GroupedThongBaoDTO> findGroupedByKeyword(String keyword) {
        String searchPattern = "%" + keyword.trim().toLowerCase() + "%";
        String hql = GROUPED_HQL_SELECT
                + "AND (LOWER(tb.tieuDe) LIKE :keyword OR LOWER(tb.noiDung) LIKE :keyword) "
                + GROUPED_HQL_GROUP_BY;
        try ( Session session = sessionFactory.openSession()) {
            Query<GroupedThongBaoDTO> query = session.createQuery(hql, GroupedThongBaoDTO.class);
            query.setParameter("trangThai", TRANG_THAI_ACTIVE);
            query.setParameter("keyword", searchPattern);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    // ==========================================================
    // === CÁC HÀM MỚI CHO NGƯỜI DÙNG (CLIENT-SIDE) ===
    // ==========================================================
    /**
     * HÀM MỚI: Tìm kiếm thông báo (ACTIVE) của 1 người dùng theo keyword
     */
    public List<ThongBao> findActiveByKeywordForUser(int taiKhoanId, String keyword) {
        String searchPattern = "%" + keyword.trim().toLowerCase() + "%";

        // Không cần JOIN FETCH taiKhoan, vì chúng ta đã biết ID
        String hql = "SELECT tb FROM ThongBao tb "
                + "WHERE tb.trangThai = :trangThai "
                + "AND tb.taiKhoan.id = :taiKhoanId "
                + // Chỉ lấy của user này
                "AND (LOWER(tb.tieuDe) LIKE :keyword OR LOWER(tb.noiDung) LIKE :keyword) "
                + "ORDER BY tb.thoiGianGui DESC";

        try ( Session session = sessionFactory.openSession()) {
            Query<ThongBao> query = session.createQuery(hql, ThongBao.class);
            query.setParameter("trangThai", TRANG_THAI_ACTIVE);
            query.setParameter("taiKhoanId", taiKhoanId);
            query.setParameter("keyword", searchPattern);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * HÀM MỚI: Đánh dấu đã đọc (an toàn) Chỉ đánh dấu đã đọc nếu ID thông báo
     * VÀ ID tài khoản khớp.
     *
     * @return true nếu cập nhật thành công (1 hàng)
     */
    public boolean markAsReadForUser(int thongBaoId, int taiKhoanId) {
        Transaction transaction = null;
        int updatedCount = 0;
        // Dùng HQL UPDATE để an toàn (không cho user A sửa của user B)
        String hql = "UPDATE ThongBao tb SET tb.daDoc = true "
                + "WHERE tb.id = :thongBaoId "
                + "AND tb.taiKhoan.id = :taiKhoanId "
                + "AND tb.daDoc = false"; // Chỉ update nếu chưa đọc

        try ( Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Query query = session.createQuery(hql);
            query.setParameter("thongBaoId", thongBaoId);
            query.setParameter("taiKhoanId", taiKhoanId);

            updatedCount = query.executeUpdate(); // Trả về số dòng bị ảnh hưởng
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi đánh dấu đã đọc: " + e.getMessage(), e);
        }
        return updatedCount > 0; // Trả về true nếu có 1 hàng được update
    }

    /**
     * HÀM MỚI: Xóa mềm (an toàn) Chỉ xóa mềm nếu ID thông báo VÀ ID tài khoản
     * khớp.
     *
     * @return true nếu cập nhật thành công (1 hàng)
     */
    public boolean softDeleteForUser(int thongBaoId, int taiKhoanId) {
        Transaction transaction = null;
        int updatedCount = 0;
        String hql = "UPDATE ThongBao tb SET tb.trangThai = :trangThaiXoa "
                + "WHERE tb.id = :thongBaoId "
                + "AND tb.taiKhoan.id = :taiKhoanId";

        try ( Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            Query query = session.createQuery(hql);
            query.setParameter("trangThaiXoa", TRANG_THAI_XOA);
            query.setParameter("thongBaoId", thongBaoId);
            query.setParameter("taiKhoanId", taiKhoanId);

            updatedCount = query.executeUpdate();
            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi xóa mềm thông báo: " + e.getMessage(), e);
        }
        return updatedCount > 0;
    }
}
