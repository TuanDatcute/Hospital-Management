/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;

import model.Entity.NhanVien;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author ADMIN
 */
public class NhanVienDAO {

    /**
     * Thêm một nhân viên mới vào CSDL.
     */
    public NhanVien create(NhanVien nhanVien) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(nhanVien);
            transaction.commit();
            return nhanVien;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Cập nhật thông tin một nhân viên.
     */
    public boolean update(NhanVien nhanVien) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(nhanVien);
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
     * Lấy thông tin nhân viên bằng ID.
     */
    public NhanVien getById(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(NhanVien.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy thông tin nhân viên bằng ID (Tải đầy đủ quan hệ).
     */
    public NhanVien getByIdWithRelations(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM NhanVien n "
                    + "LEFT JOIN FETCH n.taiKhoan "
                    + "LEFT JOIN FETCH n.khoa "
                    + "WHERE n.id = :id";
            Query<NhanVien> query = session.createQuery(hql, NhanVien.class);
            query.setParameter("id", id);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy tất cả nhân viên trong CSDL.
     */
    public List<NhanVien> getAll() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM NhanVien";
            Query<NhanVien> query = session.createQuery(hql, NhanVien.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Lấy tất cả nhân viên (Tải đầy đủ quan hệ).
     */
    public List<NhanVien> getAllWithRelations() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM NhanVien n "
                    + "LEFT JOIN FETCH n.taiKhoan "
                    + "LEFT JOIN FETCH n.khoa";
            Query<NhanVien> query = session.createQuery(hql, NhanVien.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * Kiểm tra xem Số điện thoại đã tồn tại chưa.
     */
    public boolean isSoDienThoaiExisted(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            return false;
        }
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(n.id) FROM NhanVien n WHERE n.soDienThoai = :sdt";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("sdt", soDienThoai);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Failsafe
        }
    }

    /**
     * Kiểm tra xem TaiKhoan ID đã được liên kết với Nhân viên nào chưa.
     */
    public boolean isTaiKhoanIdLinked(int taiKhoanId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(n.id) FROM NhanVien n WHERE n.taiKhoan.id = :tkId";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("tkId", taiKhoanId);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Failsafe
        }
    }

    public List<NhanVien> findDoctorsBySpecialty() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<NhanVien> query = session.createQuery(
                    "SELECT nv FROM NhanVien nv "
                    + "JOIN FETCH nv.taiKhoan "
                    + "WHERE nv.taiKhoan.vaiTro = :role",
                    NhanVien.class
            );
            query.setParameter("role", "BAC_SI");
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    //===================================Quang San ==============================================
    public List<NhanVien> findBacSiByKhoaId(int khoaId) {
        // Thêm Log để kiểm tra ID đang được truy vấn
        System.out.println("DAO: Đang truy vấn bác sĩ cho Khoa ID: " + khoaId);

        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {

            // HQL ĐÃ SỬA:
            // 1. SELECT nv FROM NhanVien nv
            // 2. INNER JOIN FETCH nv.taiKhoan tk (Giải quyết Lazy Loading cho TaiKhoan)
            // 3. INNER JOIN FETCH nv.khoa k (Giải quyết Lazy Loading cho Khoa)
            // 4. Bổ sung lại các điều kiện lọc Vai trò và Trạng thái
            String hql = "SELECT nv FROM NhanVien nv "
                    + "INNER JOIN FETCH nv.taiKhoan tk "
                    + "INNER JOIN FETCH nv.khoa k " // <--- Đã FETCH Khoa (Khắc phục lỗi Lazy Loading/500)
                    + "WHERE nv.khoa.id = :khoaId "
                    + "AND tk.vaiTro = 'BAC_SI' " // <--- Thêm lại điều kiện lọc vai trò
                    + "AND tk.trangThai = 'HOAT_DONG'"; // <--- Thêm lại điều kiện lọc trạng thái

            Query<NhanVien> query = session.createQuery(hql, NhanVien.class);
            query.setParameter("khoaId", khoaId);

            List<NhanVien> result = query.list();
            System.out.println("DAO: Số lượng bác sĩ tìm thấy: " + result.size());
            return result;

        } catch (Exception e) {
            System.err.println("LỖI DAO khi tìm bác sĩ theo khoa ID: " + khoaId);

            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    //=============================Dat================
    /**
     * Tìm nhân viên bằng taiKhoanId.
     */
    public NhanVien findByTaiKhoanId(int taiKhoanId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<NhanVien> query = session.createQuery(
                    "SELECT nv FROM NhanVien nv "
                    + "JOIN FETCH nv.taiKhoan "
                    + "LEFT JOIN FETCH nv.khoa "
                    + "WHERE nv.taiKhoan.id = :taiKhoanId",
                    NhanVien.class
            );
            query.setParameter("taiKhoanId", taiKhoanId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // === BẮT ĐẦU SỬA LỖI CONFLICT (Giữ lại code từ CẢ HAI nhánh) ===
    // --- Các hàm từ nhánh HEAD (Phân trang & Tìm kiếm Admin) ---
    /**
     * (Đã nâng cấp) Lấy danh sách đã phân trang (chưa lọc Xóa Mềm)
     */
    public List<NhanVien> getAllWithRelations(int page, int pageSize) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT n FROM NhanVien n "
                    + "LEFT JOIN FETCH n.taiKhoan "
                    + "LEFT JOIN FETCH n.khoa "
                    + "ORDER BY n.id ASC";

            Query<NhanVien> query = session.createQuery(hql, NhanVien.class);
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
     * (Đã nâng cấp) Đếm tổng số nhân viên (chưa lọc Xóa Mềm)
     */
    public long getTotalNhanVienCount() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(n.id) FROM NhanVien n";
            Query<Long> query = session.createQuery(hql, Long.class);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * (Đã nâng cấp) Tìm kiếm nhân viên (có phân trang, ĐÃ LỌC XÓA MỀM)
     */
    public List<NhanVien> searchNhanVienPaginated(String keyword, int page, int pageSize) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT n FROM NhanVien n "
                    + "LEFT JOIN FETCH n.taiKhoan tk "
                    + "LEFT JOIN FETCH n.khoa "
                    + "WHERE (tk IS NULL OR tk.trangThai = 'HOAT_DONG') " // Lọc Xóa Mềm
                    + "AND (n.hoTen LIKE :keyword OR n.soDienThoai LIKE :keyword OR n.chuyenMon LIKE :keyword) "
                    + "ORDER BY n.id ASC";

            Query<NhanVien> query = session.createQuery(hql, NhanVien.class);
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
     * (Đã nâng cấp) Đếm kết quả tìm kiếm nhân viên (ĐÃ LỌC XÓA MỀM)
     */
    public long getNhanVienSearchCount(String keyword) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(DISTINCT n.id) FROM NhanVien n "
                    + "LEFT JOIN n.taiKhoan tk "
                    + "WHERE (tk IS NULL OR tk.trangThai = 'HOAT_DONG') " // Lọc Xóa Mềm
                    + "AND (n.hoTen LIKE :keyword OR n.soDienThoai LIKE :keyword OR n.chuyenMon LIKE :keyword)";

            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("keyword", "%" + keyword + "%");
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * (Đã nâng cấp) Đếm nhân viên đang hoạt động theo Khoa
     */
    public long countActiveNhanVienByKhoaId(int khoaId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(n.id) FROM NhanVien n "
                    + "JOIN n.taiKhoan tk "
                    + "WHERE n.khoa.id = :khoaId AND tk.trangThai = 'HOAT_DONG'"; // Lọc Xóa Mềm
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("khoaId", khoaId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 999; // Failsafe
        }
    }

    // --- Hàm từ nhánh c6c746e... (Hỗ trợ Đặt lịch hẹn) ---
    /**
     * HÀM SỬA: Tìm các bác sĩ (NhanVien) theo Khoa ID.
     */
    // Trong file dao/NhanVienDAO.java
    public List<NhanVien> findDoctorsByKhoaId(int khoaId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {

            String hql = "SELECT nv FROM NhanVien nv "
                    + "INNER JOIN FETCH nv.taiKhoan tk "
                    + "INNER JOIN FETCH nv.khoa k "
                    + "WHERE nv.khoa.id = :khoaId "
                    + "AND tk.vaiTro = 'BAC_SI' "
                    + "AND tk.trangThai = 'HOAT_DONG'"; // Lọc Xóa Mềm

            Query<NhanVien> query = session.createQuery(hql, NhanVien.class);

            query.setParameter("khoaId", khoaId);
            return query.list();
        } catch (Exception e) {
            return Collections.EMPTY_LIST;
        }
    }

}
