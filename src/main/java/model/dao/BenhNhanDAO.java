package model.dao;

import model.Entity.BenhNhan;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 *
 * @author ADMIN (Đã NÂNG CẤP: Thêm Xóa Mềm & Tìm kiếm & Sửa Mã BN)
 */
public class BenhNhanDAO {

    // (Hàm create và update giữ nguyên)
    public BenhNhan create(BenhNhan benhNhan) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(benhNhan); // Service sẽ đảm bảo trạng thái là HOAT_DONG
            transaction.commit();
            return benhNhan;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Lỗi DAO khi tạo BenhNhan: " + e.getMessage(), e);
        }
    }

    public void update(BenhNhan benhNhan) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(benhNhan);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            throw new RuntimeException("Lỗi DAO khi cập nhật BenhNhan: " + e.getMessage(), e);
        }
    }

    // === BẮT ĐẦU THÊM MỚI (XÓA MỀM) ===
    /**
     * HÀM MỚI (XÓA MỀM): Vô hiệu hóa một bệnh nhân
     */
    public boolean softDelete(int id) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            BenhNhan benhNhan = session.get(BenhNhan.class, id); // Lấy entity (kể cả đã xóa)
            if (benhNhan != null && benhNhan.getTrangThai().equals("HOAT_DONG")) {
                benhNhan.setTrangThai("DA_XOA"); // Đổi trạng thái
                session.update(benhNhan); // Cập nhật lại
            }
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
     * SỬA (XÓA MỀM): Lấy bằng ID (chỉ bệnh nhân HOAT_DONG)
     */
    public BenhNhan getById(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM BenhNhan bn WHERE bn.id = :id AND bn.trangThai = 'HOAT_DONG'";
            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
            query.setParameter("id", id);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * SỬA (XÓA MỀM): Lấy bằng ID (chỉ HOAT_DONG BenhNhan và HOAT_DONG TaiKhoan)
     */
    public BenhNhan getByIdWithRelations(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM BenhNhan bn "
                    + "LEFT JOIN FETCH bn.taiKhoan tk "
                    + "LEFT JOIN FETCH bn.khoa "
                    + "WHERE bn.id = :id AND bn.trangThai = 'HOAT_DONG' "
                    + "AND (tk IS NULL OR tk.trangThai = 'HOAT_DONG')";
            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
            query.setParameter("id", id);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * HÀM MỚI (CHO Service): Lấy bệnh nhân (kể cả bị vô hiệu hóa) Dùng cho
     * logic 'softDelete' và 'confirmAndLink'
     */
    public BenhNhan getByIdEvenIfInactive(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
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
     * (Hàm getAll của bạn - SỬA: Lọc theo trangThai)
     */
    public List<BenhNhan> getAll() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM BenhNhan bn WHERE bn.trangThai = 'HOAT_DONG'";
            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * (Hàm getAllWithRelations_IncludingInactive của bạn - Giữ nguyên)
     */
    public List<BenhNhan> getAllWithRelations_IncludingInactive() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT bn FROM BenhNhan bn LEFT JOIN FETCH bn.taiKhoan LEFT JOIN FETCH bn.khoa";
            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * SỬA (XÓA MỀM): Thêm lọc trangThai (Đây là hàm Phân trang của bạn)
     */
    public List<BenhNhan> getAllActiveWithRelations(int page, int pageSize) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT bn FROM BenhNhan bn "
                    + "LEFT JOIN FETCH bn.taiKhoan tk "
                    + "LEFT JOIN FETCH bn.khoa k "
                    + "WHERE bn.trangThai = 'HOAT_DONG' AND (tk IS NULL OR tk.trangThai = 'HOAT_DONG') "
                    + "ORDER BY bn.id ASC";
            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
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
     * SỬA (XÓA MỀM): Thêm lọc trangThai (Đây là hàm Đếm của bạn)
     */
    public long getTotalActiveBenhNhanCount() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(DISTINCT bn.id) FROM BenhNhan bn "
                    + "LEFT JOIN bn.taiKhoan tk "
                    + "WHERE bn.trangThai = 'HOAT_DONG' AND (tk IS NULL OR tk.trangThai = 'HOAT_DONG')";
            Query<Long> query = session.createQuery(hql, Long.class);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * SỬA (XÓA MỀM): Thêm lọc trangThai (Hàm này cho Dropdown)
     */
    public List<BenhNhan> getAllActiveWithRelations() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT bn FROM BenhNhan bn "
                    + "LEFT JOIN FETCH bn.taiKhoan tk "
                    + "LEFT JOIN FETCH bn.khoa k "
                    + "WHERE bn.trangThai = 'HOAT_DONG' AND (tk IS NULL OR tk.trangThai = 'HOAT_DONG') "
                    + "ORDER BY bn.hoTen ASC";
            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // === BẮT ĐẦU THÊM MỚI (TÌM KIẾM) ===
    /**
     * HÀM MỚI (TÌM KIẾM): Tìm kiếm Bệnh nhân (có phân trang) Tìm theo Mã BN,
     * Tên, SĐT, CCCD
     */
    public List<BenhNhan> searchBenhNhanPaginated(String keyword, int page, int pageSize) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT bn FROM BenhNhan bn "
                    + "LEFT JOIN FETCH bn.taiKhoan tk "
                    + "LEFT JOIN FETCH bn.khoa k "
                    + "WHERE bn.trangThai = 'HOAT_DONG' "
                    + "AND (tk IS NULL OR tk.trangThai = 'HOAT_DONG') "
                    + "AND (bn.hoTen LIKE :keyword OR bn.soDienThoai LIKE :keyword OR bn.cccd LIKE :keyword OR bn.maBenhNhan LIKE :keyword) "
                    + "ORDER BY bn.id ASC";

            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
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
     * HÀM MỚI (TÌM KIẾM): Đếm kết quả tìm kiếm Bệnh nhân
     */
    public long getBenhNhanSearchCount(String keyword) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(DISTINCT bn.id) FROM BenhNhan bn "
                    + "LEFT JOIN bn.taiKhoan tk "
                    + "WHERE bn.trangThai = 'HOAT_DONG' "
                    + "AND (tk IS NULL OR tk.trangThai = 'HOAT_DONG') "
                    + "AND (bn.hoTen LIKE :keyword OR bn.soDienThoai LIKE :keyword OR bn.cccd LIKE :keyword OR bn.maBenhNhan LIKE :keyword)";

            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("keyword", "%" + keyword + "%");
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
    // === KẾT THÚC THÊM MỚI (TÌM KIẾM) ===

    /**
     * SỬA (XÓA MỀM): Lọc theo trangThai
     */
    public boolean isMaBenhNhanExisted(String maBenhNhan) {
        if (maBenhNhan == null || maBenhNhan.trim().isEmpty()) {
            return false;
        }
        String trimmedMa = maBenhNhan.trim();
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(bn.id) FROM BenhNhan bn WHERE bn.maBenhNhan = :ma AND bn.trangThai = 'HOAT_DONG'";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("ma", trimmedMa);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Failsafe
        }
    }

    /**
     * SỬA (XÓA MỀM): Lọc theo trangThai
     */
    public boolean isTaiKhoanIdLinked(int taiKhoanId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(bn.id) FROM BenhNhan bn WHERE bn.taiKhoan.id = :tkId AND bn.trangThai = 'HOAT_DONG'";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("tkId", taiKhoanId);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Failsafe
        }
    }

    /**
     * SỬA (XÓA MỀM): Lọc theo trangThai
     */
    public BenhNhan findByMaBenhNhan(String maBenhNhan) {
        if (maBenhNhan == null || maBenhNhan.trim().isEmpty()) {
            return null;
        }
        String trimmedMa = maBenhNhan.trim();
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM BenhNhan bn LEFT JOIN FETCH bn.taiKhoan "
                    + "WHERE bn.maBenhNhan = :ma AND bn.trangThai = 'HOAT_DONG'";
            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
            query.setParameter("ma", trimmedMa);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * SỬA (XÓA MỀM): Lọc theo trangThai
     */
    public BenhNhan findByTaiKhoanId(int taiKhoanId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM BenhNhan bn LEFT JOIN FETCH bn.taiKhoan "
                    + "WHERE bn.taiKhoan.id = :tkId AND bn.trangThai = 'HOAT_DONG'";
            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
            query.setParameter("tkId", taiKhoanId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * (Hàm generateNewMaBenhNhan của bạn - ĐÃ NÂNG CẤP LÊN 5 SỐ 0)
     */
    public String generateNewMaBenhNhan() {
        String defaultMa = "BN-00001";
        int nextNum = 1;

        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT bn.maBenhNhan FROM BenhNhan bn WHERE bn.maBenhNhan LIKE 'BN-%'";
            Query<String> query = session.createQuery(hql, String.class);
            List<String> allMaBN = query.list();

            if (allMaBN.isEmpty()) {
                return defaultMa;
            }

            int maxNum = allMaBN.stream()
                    .map(ma -> ma.substring(3))
                    .mapToInt(numStr -> {
                        try {
                            return Integer.parseInt(numStr);
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    })
                    .max()
                    .orElse(0);

            nextNum = maxNum + 1;

        } catch (Exception e) {
            e.printStackTrace();
            return "BN-ERR" + (System.currentTimeMillis() % 10000);
        }
        return "BN-" + String.format("%05d", nextNum);
    }

    /**
     * SỬA (XÓA MỀM): Lọc theo trangThai
     */
    public BenhNhan findByCccd(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) {
            return null;
        }
        String trimmedCccd = cccd.trim();
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM BenhNhan bn LEFT JOIN FETCH bn.taiKhoan "
                    + "WHERE bn.cccd = :cccd AND bn.trangThai = 'HOAT_DONG'";
            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
            query.setParameter("cccd", trimmedCccd);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * SỬA (XÓA MỀM): Lọc theo trangThai
     */
    public boolean isCccdExisted(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) {
            return false;
        }
        String trimmedCccd = cccd.trim();
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(bn.id) FROM BenhNhan bn WHERE bn.cccd = :cccd AND bn.trangThai = 'HOAT_DONG'";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("cccd", trimmedCccd);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Failsafe
        }
    }

    /**
     * SỬA (XÓA MỀM): Lọc theo trangThai
     */
    public boolean isSoDienThoaiExisted(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            return false;
        }
        String trimmedSdt = soDienThoai.trim();
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(bn.id) FROM BenhNhan bn WHERE bn.soDienThoai = :sdt AND bn.trangThai = 'HOAT_DONG'";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("sdt", trimmedSdt);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Failsafe
        }
    }

    /**
     * SỬA (XÓA MỀM): Lọc theo trangThai
     */
    public BenhNhan findBySoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            return null;
        }
        String trimmedSdt = soDienThoai.trim();
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM BenhNhan bn LEFT JOIN FETCH bn.taiKhoan "
                    + "WHERE bn.soDienThoai = :sdt AND bn.trangThai = 'HOAT_DONG'";
            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
            query.setParameter("sdt", trimmedSdt);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<BenhNhan> getAllWithRelations() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM BenhNhan bn LEFT JOIN FETCH bn.taiKhoan LEFT JOIN FETCH bn.khoa";
            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * SỬA (XÓA MỀM): Lọc theo trangThai
     */
    public List<BenhNhan> getBenhNhanChuaCoGiuongWithRelations() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<BenhNhan> query = session.createQuery(
                    "FROM BenhNhan bn "
                    + "LEFT JOIN FETCH bn.taiKhoan tk "
                    + "WHERE bn.trangThai = 'HOAT_DONG' AND (tk IS NULL OR tk.trangThai = 'HOAT_DONG') "
                    + "AND bn.id NOT IN ( "
                    + "  SELECT gb.benhNhan.id "
                    + "  FROM GiuongBenh gb "
                    + "  WHERE gb.benhNhan IS NOT NULL "
                    + ") "
                    + "ORDER BY bn.hoTen",
                    BenhNhan.class
            );
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

   
    public List<BenhNhan> searchByNameOrMaBN(String keyword) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<BenhNhan> query = session.createQuery(
                    "FROM BenhNhan bn WHERE bn.hoTen LIKE :keyword OR bn.maBenhNhan LIKE :keyword",
                    BenhNhan.class
            );
            query.setParameter("keyword", "%" + keyword + "%");
            query.setMaxResults(10); 
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
