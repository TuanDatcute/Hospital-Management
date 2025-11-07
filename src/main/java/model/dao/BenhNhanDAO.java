/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;

import model.Entity.BenhNhan;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional; // <-- Đảm bảo bạn đã import

/**
 *
 * @author ADMIN (Đã cập nhật Giai đoạn 2 & 3) (Đã Clean Code - Thêm .trim() vào
 * các truy vấn)
 */
public class BenhNhanDAO {

    /**
     * Thêm một bệnh nhân mới vào CSDL. (Giữ nguyên)
     */
    public BenhNhan create(BenhNhan benhNhan) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(benhNhan);
            transaction.commit();
            return benhNhan;
        } catch (Exception e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("Lỗi khi rollback (kết nối có thể đã đóng): " + rollbackEx.getMessage());
                }
            }
            throw new RuntimeException("Lỗi DAO khi tạo BenhNhan: " + e.getMessage(), e);
        }
    }

    /**
     * Cập nhật thông tin một bệnh nhân. (Đã Clean Code: void + throw)
     */
    public void update(BenhNhan benhNhan) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(benhNhan);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    System.err.println("Lỗi khi rollback (kết nối có thể đã đóng): " + rollbackEx.getMessage());
                }
            }
            throw new RuntimeException("Lỗi DAO khi cập nhật BenhNhan: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy thông tin bệnh nhân bằng ID. (Giữ nguyên)
     */
    public BenhNhan getById(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(BenhNhan.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy thông tin bệnh nhân bằng ID (tải kèm thông tin Relations). (Giữ
     * nguyên)
     */
    public BenhNhan getByIdWithRelations(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM BenhNhan bn LEFT JOIN FETCH bn.taiKhoan LEFT JOIN FETCH bn.khoa WHERE bn.id = :id";
            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
            query.setParameter("id", id);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy tất cả bệnh nhân trong CSDL (không tải Relations). (Giữ nguyên)
     */
    public List<BenhNhan> getAll() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM BenhNhan";
            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy tất cả bệnh nhân trong CSDL (tải kèm thông tin Relations). (Giữ
     * nguyên)
     */
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
     * Kiểm tra xem Mã Bệnh Nhân đã tồn tại chưa. (Đã cập nhật .trim())
     */
    public boolean isMaBenhNhanExisted(String maBenhNhan) {
        if (maBenhNhan == null || maBenhNhan.trim().isEmpty()) {
            return false;
        }
        String trimmedMa = maBenhNhan.trim();
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(bn.id) FROM BenhNhan bn WHERE bn.maBenhNhan = :ma";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("ma", trimmedMa);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Failsafe
        }
    }

    /**
     * Kiểm tra xem TaiKhoan ID đã được liên kết với Bệnh nhân nào chưa. (Giữ
     * nguyên)
     */
    public boolean isTaiKhoanIdLinked(int taiKhoanId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(bn.id) FROM BenhNhan bn WHERE bn.taiKhoan.id = :tkId";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("tkId", taiKhoanId);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Failsafe
        }
    }

    /**
     * Tìm bệnh nhân bằng Mã Bệnh Nhân. (Đã cập nhật .trim())
     */
    public BenhNhan findByMaBenhNhan(String maBenhNhan) {
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

    /**
     * Tìm bệnh nhân bằng ID Tài khoản. (Giữ nguyên)
     */
    public BenhNhan findByTaiKhoanId(int taiKhoanId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM BenhNhan bn LEFT JOIN FETCH bn.taiKhoan WHERE bn.taiKhoan.id = :tkId";
            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
            query.setParameter("tkId", taiKhoanId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Tự động tạo Mã Bệnh Nhân mới. (Giữ nguyên)
     */
    public String generateNewMaBenhNhan() {
        String defaultMa = "BN-10001";

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
                    .orElse(10000);

            int nextNum = maxNum + 1;
            return "BN-" + nextNum;

        } catch (Exception e) {
            e.printStackTrace();
            return "BN-" + (System.currentTimeMillis() % 100000);
        }
    }

    /**
     * HÀM MỚI: Tìm bệnh nhân bằng CCCD (duy nhất). (Đã cập nhật .trim())
     */
    public BenhNhan findByCccd(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) {
            return null;
        }
        String trimmedCccd = cccd.trim();
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM BenhNhan bn LEFT JOIN FETCH bn.taiKhoan WHERE bn.cccd = :cccd";
            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
            query.setParameter("cccd", trimmedCccd);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * HÀM MỚI: Kiểm tra xem CCCD đã tồn tại chưa (cho Kịch bản B). (Đã cập nhật
     * .trim())
     */
    public boolean isCccdExisted(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) {
            return false;
        }
        String trimmedCccd = cccd.trim();
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(bn.id) FROM BenhNhan bn WHERE bn.cccd = :cccd";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("cccd", trimmedCccd);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Failsafe
        }
    }

    /**
     * HÀM MỚI: Kiểm tra xem Số Điện Thoại đã tồn tại chưa (cho Kịch bản B). (Đã
     * cập nhật .trim())
     */
    public boolean isSoDienThoaiExisted(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            return false;
        }
        String trimmedSdt = soDienThoai.trim();
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(bn.id) FROM BenhNhan bn WHERE bn.soDienThoai = :sdt";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("sdt", trimmedSdt);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Failsafe
        }
    }

    /**
     * HÀM MỚI (BẮT BUỘC): Tìm bệnh nhân bằng SĐT (duy nhất). (Đã cập nhật
     * .trim())
     */
    public BenhNhan findBySoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            return null;
        }
        String trimmedSdt = soDienThoai.trim();
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM BenhNhan bn LEFT JOIN FETCH bn.taiKhoan WHERE bn.soDienThoai = :sdt";
            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
            query.setParameter("sdt", trimmedSdt);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy tất cả bệnh nhân CHƯA được gán vào giường bệnh. (Giữ nguyên)
     */
    public List<BenhNhan> getBenhNhanChuaCoGiuongWithRelations() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<BenhNhan> query = session.createQuery(
                    "FROM BenhNhan bn "
                    + "LEFT JOIN FETCH bn.taiKhoan "
                    + "WHERE bn.id NOT IN ( "
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
}
