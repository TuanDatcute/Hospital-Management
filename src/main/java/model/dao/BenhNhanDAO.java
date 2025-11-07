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
 * @author ADMIN (Đã NÂNG CẤP: Thêm Xóa Mềm & Tìm kiếm)
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

    // === BẮT ĐẦU SỬA (XÓA MỀM) ===
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
     * SỬA: Lấy bằng ID (chỉ bệnh nhân HOAT_DONG)
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
     * SỬA: Lấy bằng ID (kể cả bị khóa, nhưng chỉ HOAT_DONG taiKhoan)
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
     * logic 'softDelete' để kiểm tra xem tài khoản có bị khóa không
     */
    public BenhNhan getByIdEvenIfInactive(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Không lọc theo trangThai
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
     * (Hàm getAll của bạn - không còn dùng cho Phân trang, chỉ cho dropdown)
     * SỬA: Đổi tên thành getAllActiveWithRelations (cho rõ nghĩa)
     */
    public List<BenhNhan> getAllActiveWithRelations() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT bn FROM BenhNhan bn "
                    + "LEFT JOIN FETCH bn.taiKhoan tk "
                    + "LEFT JOIN FETCH bn.khoa k "
                    + "WHERE bn.trangThai = 'HOAT_DONG' AND (tk IS NULL OR tk.trangThai = 'HOAT_DONG') "
                    + "ORDER BY bn.hoTen ASC"; // Sắp xếp theo tên cho dropdown
            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * (Hàm phân trang của bạn - SỬA: Thêm lọc Xóa Mềm)
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
     * (Hàm đếm của bạn - SỬA: Thêm lọc Xóa Mềm)
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

    // (Các hàm 'is...Existed', 'findBy...', 'generateNewMa...'... của bạn giữ nguyên,
    // nhưng cũng cần SỬA để lọc theo 'trangThai')
    public boolean isMaBenhNhanExisted(String maBenhNhan) {
        if (maBenhNhan == null || maBenhNhan.trim().isEmpty()) {
            return false;
        }
        String trimmedMa = maBenhNhan.trim();
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // SỬA: Chỉ kiểm tra các BN đang hoạt động
            String hql = "SELECT count(bn.id) FROM BenhNhan bn WHERE bn.maBenhNhan = :ma AND bn.trangThai = 'HOAT_DONG'";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("ma", trimmedMa);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Failsafe
        }
    }

    public boolean isTaiKhoanIdLinked(int taiKhoanId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // SỬA: Chỉ kiểm tra các BN đang hoạt động
            String hql = "SELECT count(bn.id) FROM BenhNhan bn WHERE bn.taiKhoan.id = :tkId AND bn.trangThai = 'HOAT_DONG'";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("tkId", taiKhoanId);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Failsafe
        }
    }

    public BenhNhan findByMaBenhNhan(String maBenhNhan) {
        // (Hàm này giữ nguyên, không cần lọc, vì nó dùng để tìm kiếm)
        if (maBenhNhan == null || maBenhNhan.trim().isEmpty()) {
            return null;
        }
        String trimmedMa = maBenhNhan.trim();
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM BenhNhan bn LEFT JOIN FETCH bn.taiKhoan WHERE bn.maBenhNhan = :ma";
            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
            query.setParameter("ma", trimmedMa);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public BenhNhan findByTaiKhoanId(int taiKhoanId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // SỬA: Chỉ tìm BN đang hoạt động
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
     * Tự động tạo Mã Bệnh Nhân mới (ĐÃ NÂNG CẤP: Thêm zero-padding 5 số) Sẽ tạo
     * ra các mã như: BN-00001, BN-00002 ... BN-00010 ... BN-01000
     */
    public String generateNewMaBenhNhan() {
        // === SỬA 1: Đổi mã mặc định ===
        String defaultMa = "BN-00001";
        int nextNum = 1; // Mặc định là 1

        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT bn.maBenhNhan FROM BenhNhan bn WHERE bn.maBenhNhan LIKE 'BN-%'";
            Query<String> query = session.createQuery(hql, String.class);
            List<String> allMaBN = query.list();

            if (allMaBN.isEmpty()) {
                return defaultMa; // Trả về mã đầu tiên
            }

            // === SỬA 2: Sửa logic tìm MAX ===
            // Logic cũ của bạn (cắt chuỗi, chuyển sang int) đã rất tốt
            int maxNum = allMaBN.stream()
                    .map(ma -> ma.substring(3)) // Lấy phần số (vd: "00001", "10001")
                    .mapToInt(numStr -> {
                        try {
                            // Integer.parseInt("00001") sẽ ra số 1 (chính xác)
                            return Integer.parseInt(numStr);
                        } catch (NumberFormatException e) {
                            return 0;
                        }
                    })
                    .max()
                    .orElse(0); // Sửa: nếu có lỗi, bắt đầu từ 0

            nextNum = maxNum + 1; // Số tiếp theo

        } catch (Exception e) {
            e.printStackTrace();
            // Failsafe (dự phòng)
            return "BN-ERR" + (System.currentTimeMillis() % 10000);
        }

        // === SỬA 3: Định dạng đầu ra với 5 số 0 ===
        // "%05d" nghĩa là:
        // d: định dạng số nguyên
        // 5: tổng độ dài là 5
        // 0: nếu thiếu, hãy đệm bằng số 0
        return "BN-" + String.format("%05d", nextNum);
    }

    public BenhNhan findByCccd(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) {
            return null;
        }
        String trimmedCccd = cccd.trim();
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // SỬA: Thêm lọc trạng thái
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

    public boolean isCccdExisted(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) {
            return false;
        }
        String trimmedCccd = cccd.trim();
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // SỬA: Chỉ kiểm tra các BN đang hoạt động
            String hql = "SELECT count(bn.id) FROM BenhNhan bn WHERE bn.cccd = :cccd AND bn.trangThai = 'HOAT_DONG'";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("cccd", trimmedCccd);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Failsafe
        }
    }

    public boolean isSoDienThoaiExisted(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            return false;
        }
        String trimmedSdt = soDienThoai.trim();
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // SỬA: Chỉ kiểm tra các BN đang hoạt động
            String hql = "SELECT count(bn.id) FROM BenhNhan bn WHERE bn.soDienThoai = :sdt AND bn.trangThai = 'HOAT_DONG'";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("sdt", trimmedSdt);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Failsafe
        }
    }

    public BenhNhan findBySoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            return null;
        }
        String trimmedSdt = soDienThoai.trim();
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // SỬA: Chỉ tìm BN đang hoạt động
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

    public List<BenhNhan> getBenhNhanChuaCoGiuongWithRelations() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<BenhNhan> query = session.createQuery(
                    "FROM BenhNhan bn "
                    + "LEFT JOIN FETCH bn.taiKhoan tk " // Thêm JOIN FETCH
                    // SỬA: Thêm lọc Xóa Mềm
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
    
    public List<BenhNhan> getAllBenhNhan() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Sử dụng HQL để truy vấn
            Query<BenhNhan> query = session.createQuery(
                "FROM BenhNhan bn ORDER BY bn.hoTen ASC", 
                BenhNhan.class
            );
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList(); // Trả về danh sách rỗng nếu có lỗi
        }
    }
}
