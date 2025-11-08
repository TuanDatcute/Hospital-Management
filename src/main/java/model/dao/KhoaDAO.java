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
 * @author ADMIN  Chuyển sang Xóa Mềm, giữ nguyên tên hàm
 */
public class KhoaDAO {

    public Khoa create(Khoa khoa) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(khoa);
            transaction.commit();
            return khoa;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new RuntimeException("Lỗi DAO khi tạo Khoa: " + e.getMessage(), e);
        }
    }

    public boolean update(Khoa khoa) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
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
     * ===  (XÓA MỀM) === Hàm delete(int id) của bạn giờ sẽ thực hiện Xóa
     * Mềm.
     */
    public boolean delete(int id) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            Khoa khoa = session.get(Khoa.class, id); // Lấy entity (kể cả đã xóa)
            if (khoa != null && khoa.getTrangThai().equals("HOAT_DONG")) {
                khoa.setTrangThai("DA_XOA"); // Đổi trạng thái
                session.update(khoa); // Cập nhật lại
            }
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            // Ném lỗi (ví dụ: lỗi khóa ngoại) để Service bắt
            throw new RuntimeException("Lỗi DAO khi xóa Khoa: " + e.getMessage(), e);
        }
    }

    /**
     * === SỬA (XÓA MỀM) === Lấy thông tin khoa bằng ID (Chỉ lấy khoa HOAT_DONG)
     */
    public Khoa getById(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Khoa k WHERE k.id = :id AND k.trangThai = 'HOAT_DONG'";
            Query<Khoa> query = session.createQuery(hql, Khoa.class);
            query.setParameter("id", id);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * === SỬA (XÓA MỀM) === Lấy tất cả các khoa (Chỉ khoa HOAT_DONG) - Dùng cho
     * dropdown
     */
    public List<Khoa> getAll() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Khoa k WHERE k.trangThai = 'HOAT_DONG' ORDER BY k.tenKhoa ASC";
            Query<Khoa> query = session.createQuery(hql, Khoa.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * === SỬA (XÓA MỀM) === Kiểm tra xem Tên khoa đã tồn tại chưa (Chỉ khoa
     * HOAT_DONG)
     */
    public boolean isTenKhoaExisted(String tenKhoa) {
        if (tenKhoa == null || tenKhoa.trim().isEmpty()) {
            return false;
        }
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(k.id) FROM Khoa k WHERE k.tenKhoa = :ten AND k.trangThai = 'HOAT_DONG'";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("ten", tenKhoa.trim()); // Trim ở đây
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Failsafe
        }
    }

    /**
     * HÀM BỔ SUNG (CHO VALIDATION): Tìm khoa bằng Tên
     */
    public Khoa findByTenKhoa(String tenKhoa) {
        if (tenKhoa == null || tenKhoa.trim().isEmpty()) {
            return null;
        }
        String trimmedTen = tenKhoa.trim();
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Chỉ tìm các khoa đang hoạt động
            String hql = "FROM Khoa k WHERE k.tenKhoa = :ten AND k.trangThai = 'HOAT_DONG'";
            Query<Khoa> query = session.createQuery(hql, Khoa.class);
            query.setParameter("ten", trimmedTen);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // === CÁC HÀM NÂNG CẤP (PHÂN TRANG & TÌM KIẾM) ===
    /**
     * HÀM MỚI (PHÂN TRANG): Lấy danh sách Khoa (có phân trang, chỉ HOAT_DONG)
     */
    public List<Khoa> getAllKhoa(int page, int pageSize) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Khoa k WHERE k.trangThai = 'HOAT_DONG' ORDER BY k.id ASC";
            Query<Khoa> query = session.createQuery(hql, Khoa.class);

            int offset = (page - 1) * pageSize;
            query.setFirstResult(offset);
            query.setMaxResults(pageSize);

            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * HÀM MỚI (PHÂN TRANG): Đếm tổng số Khoa (chỉ HOAT_DONG)
     */
    public long getTotalKhoaCount() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(k.id) FROM Khoa k WHERE k.trangThai = 'HOAT_DONG'";
            Query<Long> query = session.createQuery(hql, Long.class);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * HÀM MỚI (TÌM KIẾM): Tìm kiếm Khoa (có phân trang, chỉ HOAT_DONG)
     */
    public List<Khoa> searchKhoaPaginated(String keyword, int page, int pageSize) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM Khoa k "
                    + "WHERE (k.tenKhoa LIKE :keyword OR k.moTa LIKE :keyword) "
                    + "AND k.trangThai = 'HOAT_DONG' "
                    + "ORDER BY k.id ASC";
            Query<Khoa> query = session.createQuery(hql, Khoa.class);
            query.setParameter("keyword", "%" + keyword + "%");
            int offset = (page - 1) * pageSize;
            query.setFirstResult(offset);
            query.setMaxResults(pageSize);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * HÀM MỚI (TÌM KIẾM): Đếm kết quả tìm kiếm Khoa (chỉ HOAT_DONG)
     */
    public long getKhoaSearchCount(String keyword) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(k.id) FROM Khoa k "
                    + "WHERE (k.tenKhoa LIKE :keyword OR k.moTa LIKE :keyword) "
                    + "AND k.trangThai = 'HOAT_DONG'";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("keyword", "%" + keyword + "%");
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
