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
import java.util.Optional; // --- THÊM IMPORT MỚI ---

/**
 *
 * @author ADMIN (Đã CẬP NHẬT logic xác thực email & quên mật khẩu)
 */
public class TaiKhoanDAO {

    // === THÊM MỚI: Hằng số trạng thái ===
    private static final String TRANG_THAI_HOAT_DONG = "HOAT_DONG";
    // ===================================

    /**
     * Thêm một tài khoản mới vào CSDL.
     */
    public TaiKhoan create(TaiKhoan taiKhoan) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            
            // LƯU Ý: Service sẽ set trạng thái là 'CHUA_XAC_THUC'
            // Dòng code dưới đây chỉ là dự phòng nếu Service quên.
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
            throw new RuntimeException("Lỗi khi tạo tài khoản: " + e.getMessage(), e);
        }
    }

    /**
     * Cập nhật thông tin một tài khoản.
     */
    public void update(TaiKhoan taiKhoan) { 
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(taiKhoan); 
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace(); // Nên dùng logger
            throw new RuntimeException("Lỗi khi cập nhật tài khoản: " + e.getMessage(), e);
        }
    }

    /**
     * HÀM MỚI: Xóa một tài khoản khỏi CSDL.
     */
    public void delete(TaiKhoan taiKhoan) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(taiKhoan);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace(); // Nên dùng logger
            throw new RuntimeException("Lỗi khi xóa tài khoản: " + e.getMessage(), e);
        }
    }

    /**
     * Lấy thông tin tài khoản bằng ID.
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
     */
    public TaiKhoan findByTenDangNhap(String tenDangNhap) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM TaiKhoan t WHERE t.tenDangNhap = :ten";
            Query<TaiKhoan> query = session.createQuery(hql, TaiKhoan.class);
            query.setParameter("ten", tenDangNhap);
            return query.uniqueResult(); // Trả về null nếu không tìm thấy
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            return null;
        }
    }

    /**
     * Lấy tất cả tài khoản trong CSDL.
     */
    public List<TaiKhoan> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM TaiKhoan";
            Query<TaiKhoan> query = session.createQuery(hql, TaiKhoan.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            return Collections.emptyList(); 
        }
    }

    /**
     * Kiểm tra xem Tên đăng nhập đã tồn tại chưa (dùng cho Service đăng ký).
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
            return Collections.emptyList(); 
        }
    }


    // --- **CÁC HÀM CHO XÁC THỰC/RESET (Đã cập nhật)** ---
    
    /**
     * HÀM MỚI (Kích hoạt): Tìm tài khoản bằng token xác thực/reset.
     */
    public Optional<TaiKhoan> findByVerificationToken(String token) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM TaiKhoan t WHERE t.verificationToken = :token";
            Query<TaiKhoan> query = session.createQuery(hql, TaiKhoan.class);
            query.setParameter("token", token);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            e.printStackTrace(); 
            return Optional.empty(); 
        }
    }
    
    /**
     * HÀM MỚI: Lấy token bằng email (dùng cho Xác thực Email).
     */
    public Optional<String> findVerificationTokenByEmail(String email) {
         try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT T.verificationToken FROM TaiKhoan T WHERE T.email = :email";
            Query<String> query = session.createQuery(hql, String.class);
            query.setParameter("email", email);
            return query.uniqueResultOptional();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }
    
    // --- **BẮT ĐẦU THÊM MỚI (Cho 'Quên Mật khẩu')** ---
    /**
     * HÀM MỚI: Tìm một tài khoản bằng Email (dùng cho Quên mật khẩu).
     * Trả về Optional<TaiKhoan> để Service có thể lấy entity và cập nhật token.
     */
    public Optional<TaiKhoan> findByEmail(String email) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM TaiKhoan t WHERE t.email = :email";
            Query<TaiKhoan> query = session.createQuery(hql, TaiKhoan.class);
            query.setParameter("email", email);
            return query.uniqueResultOptional(); // An toàn, trả về Optional.empty() nếu không tìm thấy
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            return Optional.empty(); // Trả về rỗng nếu có lỗi
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