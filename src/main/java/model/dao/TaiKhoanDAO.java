/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;

import model.Entity.TaiKhoan;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil; // Lớp util của bạn
import java.util.ArrayList;
import java.util.Collections; // Import Collections
import java.util.List;

/**
 *
 * @author ADMIN (Đã cập nhật logic xác thực email)
 */
public class TaiKhoanDAO {

    // === THÊM MỚI: Hằng số trạng thái ===
    private static final String TRANG_THAI_HOAT_DONG = "HOAT_DONG";
    // ===================================

    /**
     * Thêm một tài khoản mới vào CSDL.
     * (Giữ nguyên - code của bạn đã tốt)
     */
    public TaiKhoan create(TaiKhoan taiKhoan) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
             // Đảm bảo trạng thái mặc định khi tạo mới
            if (taiKhoan.getTrangThai() == null) {
                taiKhoan.setTrangThai(TRANG_THAI_HOAT_DONG);
            }
            session.save(taiKhoan);
            transaction.commit();
            return taiKhoan;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace(); // Nên dùng logger
            // Ném lỗi ra để Service biết
            throw new RuntimeException("Lỗi khi tạo tài khoản: " + e.getMessage(), e);
        }
    }

    /**
     * Cập nhật thông tin một tài khoản.
     * **CẬP NHẬT:** Sửa lại để ném lỗi thay vì trả về 'false' cho nhất quán.
     */
    public boolean update(TaiKhoan taiKhoan) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(taiKhoan); // Hoặc merge(taiKhoan) nếu cần
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace(); // Nên dùng logger
            
            // --- **SỬA LẠI** ---
            // Ném lỗi ra ngoài để Service bắt được lỗi gốc (ví dụ: lỗi UNIQUE)
            throw new RuntimeException("Lỗi khi cập nhật tài khoản: " + e.getMessage(), e);
            // return false; // <-- XÓA DÒNG NÀY
            // --- **KẾT THÚC SỬA** ---
        }
    }

    /**
     * Lấy thông tin tài khoản bằng ID.
     * (Giữ nguyên)
     */
    public TaiKhoan getById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(TaiKhoan.class, id);
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            return null;
        }
    }

    /**
     * Tìm một tài khoản bằng Tên đăng nhập (dùng cho Service login).
     * (Giữ nguyên)
     */
    public TaiKhoan findByTenDangNhap(String tenDangNhap) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM TaiKhoan t WHERE t.tenDangNhap = :ten";
            Query<TaiKhoan> query = session.createQuery(hql, TaiKhoan.class);
            query.setParameter("ten", tenDangNhap);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            return null;
        }
    }

    /**
     * Lấy tất cả tài khoản trong CSDL.
     * (Giữ nguyên)
     */
    public List<TaiKhoan> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM TaiKhoan";
            Query<TaiKhoan> query = session.createQuery(hql, TaiKhoan.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            return Collections.emptyList(); // Trả về danh sách rỗng nếu có lỗi
        }
    }

    /**
     * Kiểm tra xem Tên đăng nhập đã tồn tại chưa (dùng cho Service đăng ký).
     * (Giữ nguyên)
     */
    public boolean isTenDangNhapExisted(String tenDangNhap) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(t.id) FROM TaiKhoan t WHERE t.tenDangNhap = :ten";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("ten", tenDangNhap);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            return false;
        }
    }

    /**
     * Kiểm tra xem Email đã tồn tại chưa (dùng cho Service đăng ký).
     * (Giữ nguyên)
     */
    public boolean isEmailExisted(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(t.id) FROM TaiKhoan t WHERE t.email = :email";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("email", email);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            return false;
        }
    }

    /**
     * Tìm các tài khoản đang hoạt động và chưa được gán.
     * (Giữ nguyên)
     */
    public List<TaiKhoan> findActiveAndUnassignedAccounts(String role) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT tk FROM TaiKhoan tk " +
                         "WHERE tk.trangThai = :trangThai " +
                         "AND NOT EXISTS (SELECT 1 FROM NhanVien nv WHERE nv.taiKhoan = tk) " + 
                         "AND NOT EXISTS (SELECT 1 FROM BenhNhan bn WHERE bn.taiKhoan = tk) "; 

            if (role != null && !role.trim().isEmpty()) {
                hql += " AND tk.vaiTro = :vaiTro";
            }

            Query<TaiKhoan> query = session.createQuery(hql, TaiKhoan.class);
            query.setParameter("trangThai", TRANG_THAI_HOAT_DONG); 

            if (role != null && !role.trim().isEmpty()) {
                query.setParameter("vaiTro", role);
            }

            return query.list();
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            return Collections.emptyList(); // Trả về list rỗng nếu lỗi
        }
    }


    // --- **BẮT ĐẦU THÊM MỚI (BƯỚC 2/6)** ---
    /**
     * HÀM MỚI: Tìm một tài khoản bằng verification token.
     * Dùng cho Service khi xác thực email.
     * @param token Mã token
     * @return Đối tượng TaiKhoan hoặc null nếu không tìm thấy.
     */
    public TaiKhoan findByVerificationToken(String token) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM TaiKhoan t WHERE t.verificationToken = :token";
            Query<TaiKhoan> query = session.createQuery(hql, TaiKhoan.class);
            query.setParameter("token", token);
            // Dùng uniqueResult vì token là duy nhất
            return query.uniqueResult(); 
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            return null; // Trả về null, Service sẽ xử lý lỗi "token không tìm thấy"
        }
    }
    // --- **KẾT THÚC THÊM MỚI** ---


    // ============================================
    // === CÁC HÀM MỚI CHO THONGBAOSERVICE ===
    // ============================================
    // (Giữ nguyên)

    public List<Integer> getAllActiveTaiKhoanIds() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT t.id FROM TaiKhoan t WHERE t.trangThai = :trangThai";
            Query<Integer> query = session.createQuery(hql, Integer.class);
            query.setParameter("trangThai", TRANG_THAI_HOAT_DONG);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace(); 
            return Collections.emptyList();
        }
    }

    public List<Integer> getActiveTaiKhoanIdsByRole(String role) {
         if (role == null || role.trim().isEmpty()) {
              return Collections.emptyList(); 
         }
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT t.id FROM TaiKhoan t WHERE t.trangThai = :trangThai AND t.vaiTro = :vaiTro";
            Query<Integer> query = session.createQuery(hql, Integer.class);
            query.setParameter("trangThai", TRANG_THAI_HOAT_DONG);
            query.setParameter("vaiTro", role.trim());
            return query.list();
        } catch (Exception e) {
            e.printStackTrace(); 
            return Collections.emptyList();
        }
    }
}