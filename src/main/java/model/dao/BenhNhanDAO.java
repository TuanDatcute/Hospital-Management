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

/**
 *
 * @author ADMIN (Đã sửa lỗi generateNewMaBenhNhan)
 */
public class BenhNhanDAO {

    /**
     * Thêm một bệnh nhân mới vào CSDL. CẬP NHẬT: Ném RuntimeException để báo
     * lỗi gốc.
     */
    public BenhNhan create(BenhNhan benhNhan) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(benhNhan);
            transaction.commit();
            return benhNhan;
        } catch (Exception e) {

            System.out.println("!!!!!!!!!!!!!! LỖI GỐC KHI CREATE BENHNHAN !!!!!!!!!!!!!!");
            e.printStackTrace();
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    System.out.println("Lỗi khi rollback (đã dự đoán): " + rollbackEx.getMessage());
                }
            }
            throw new RuntimeException("Lỗi DAO khi tạo BenhNhan (Xem LỖI GỐC ở trên): " + e.getMessage(), e);
        }
    }

    /**
     * Cập nhật thông tin một bệnh nhân. CẬP NHẬT: Ném RuntimeException để báo
     * lỗi gốc.
     */
    public boolean update(BenhNhan benhNhan) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(benhNhan);
            transaction.commit();
            return true;
        } catch (Exception e) {

            System.out.println("!!!!!!!!!!!!!! LỖI GỐC KHI UPDATE BENHNHAN !!!!!!!!!!!!!!");
            e.printStackTrace();
            System.out.println("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");

            if (transaction != null) {
                try {
                    transaction.rollback();
                } catch (Exception rollbackEx) {
                    System.out.println("Lỗi khi rollback (đã dự đoán): " + rollbackEx.getMessage());
                }
            }
            throw new RuntimeException("Lỗi DAO khi cập nhật BenhNhan (Xem LỖI GỐC ở trên): " + e.getMessage(), e);
        }
    }

    // (Giữ nguyên các hàm: getById, getByIdWithRelations, getAll, getAllWithRelations, isMaBenhNhanExisted, isTaiKhoanIdLinked, findByMaBenhNhan, findByTaiKhoanId)
    // ...
    public BenhNhan getById(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(BenhNhan.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public BenhNhan getByIdWithRelations(int id) {
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

    public List<BenhNhan> getAllWithRelations() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM BenhNhan bn LEFT JOIN FETCH bn.taiKhoan";
            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean isMaBenhNhanExisted(String maBenhNhan) {
        if (maBenhNhan == null || maBenhNhan.trim().isEmpty()) {
            return false;
        }
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(bn.id) FROM BenhNhan bn WHERE bn.maBenhNhan = :ma";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("ma", maBenhNhan);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Failsafe
        }
    }

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

    public BenhNhan findByMaBenhNhan(String maBenhNhan) {
        if (maBenhNhan == null || maBenhNhan.trim().isEmpty()) {
            return null;
        }
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM BenhNhan bn LEFT JOIN FETCH bn.taiKhoan WHERE bn.maBenhNhan = :ma";
            Query<BenhNhan> query = session.createQuery(hql, BenhNhan.class);
            query.setParameter("ma", maBenhNhan);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

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

    // --- **BẮT ĐẦU SỬA (Logic tạo mã an toàn hơn)** ---
    /**
     * Tự động tạo Mã Bệnh Nhân mới (ví dụ: BN-10001) **CẬP NHẬT:** Dùng logic
     * Java để tìm số lớn nhất, an toàn hơn HQL CAST.
     *
     * @return Một Mã Bệnh Nhân mới (String).
     */
    public String generateNewMaBenhNhan() {
        String defaultMa = "BN-10001";

        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {

            // 1. Lấy tất cả các mã BN hiện có (dạng String)
            String hql = "SELECT bn.maBenhNhan FROM BenhNhan bn WHERE bn.maBenhNhan LIKE 'BN-%'";
            Query<String> query = session.createQuery(hql, String.class);
            List<String> allMaBN = query.list();

            if (allMaBN.isEmpty()) {
                return defaultMa; // Nếu bảng rỗng, trả về mã đầu tiên
            }

            // 2. Dùng Java Stream để tìm số lớn nhất
            int maxNum = allMaBN.stream()
                    .map(ma -> ma.substring(3)) // Cắt bỏ "BN-"
                    .mapToInt(numStr -> {
                        try {
                            return Integer.parseInt(numStr); // Chuyển "10001" thành 10001
                        } catch (NumberFormatException e) {
                            return 0; // Bỏ qua nếu mã bị hỏng (ví dụ: "BN-abc")
                        }
                    })
                    .max() // Tìm số lớn nhất
                    .orElse(10000); // Nếu không có số nào, bắt đầu từ 10000

            // 3. Tạo mã mới
            int nextNum = maxNum + 1;
            return "BN-" + nextNum;

        } catch (Exception e) {
            // Nếu toàn bộ try-catch bị lỗi (ví dụ: CSDL mất kết nối)
            e.printStackTrace();
            // Trả về mã dự phòng (failsafe) dựa trên thời gian (rất khó trùng)
            return "BN-" + (System.currentTimeMillis() % 100000);
        }
    }
    // --- **KẾT THÚC SỬA** ---

    // (Giữ nguyên các hàm: isCccdExisted, isSoDienThoaiExisted, getBenhNhanChuaCoGiuongWithRelations)
    // ...
    public boolean isCccdExisted(String cccd) {
        if (cccd == null || cccd.trim().isEmpty()) {
            return false;
        }
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(bn.id) FROM BenhNhan bn WHERE bn.cccd = :cccd";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("cccd", cccd);
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
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(bn.id) FROM BenhNhan bn WHERE bn.soDienThoai = :sdt";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("sdt", soDienThoai);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Failsafe
        }
    }

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
