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
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author ADMIN
 */
public class TaiKhoanDAO {

    // === THÊM MỚI: Hằng số trạng thái ===
    private static final String TRANG_THAI_HOAT_DONG = "HOAT_DONG";
    // ===================================

    /**
     * Thêm một tài khoản mới vào CSDL.
     *
     * @param taiKhoan Đối tượng TaiKhoan (Entity)
     * @return Đối tượng TaiKhoan đã được lưu (có ID) hoặc null nếu lỗi.
     */
    public TaiKhoan create(TaiKhoan taiKhoan) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
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
     *
     * @param taiKhoan Đối tượng TaiKhoan (Entity) đã được thay đổi.
     * @return true nếu cập nhật thành công, false nếu lỗi.
     */
    public boolean update(TaiKhoan taiKhoan) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(taiKhoan); // Hoặc merge(taiKhoan) nếu cần
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace(); // Nên dùng logger
            // Cân nhắc ném lỗi thay vì trả về false
            // throw new RuntimeException("Lỗi khi cập nhật tài khoản: " + e.getMessage(), e);
            return false;
        }
    }

    /**
     * Lấy thông tin tài khoản bằng ID.
     *
     * @param id ID của tài khoản
     * @return Đối tượng TaiKhoan hoặc null nếu không tìm thấy.
     */
    public TaiKhoan getById(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(TaiKhoan.class, id);
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            return null;
        }
    }

    /**
     * Tìm một tài khoản bằng Tên đăng nhập (dùng cho Service login).
     *
     * @param tenDangNhap Tên đăng nhập
     * @return Đối tượng TaiKhoan hoặc null nếu không tìm thấy.
     */
    public TaiKhoan findByTenDangNhap(String tenDangNhap) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
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
     *
     * @return Một danh sách (List) các đối tượng TaiKhoan.
     */
    public List<TaiKhoan> getAll() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
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
     *
     * @param tenDangNhap Tên đăng nhập cần kiểm tra
     * @return true nếu đã tồn tại, false nếu chưa.
     */
    public boolean isTenDangNhapExisted(String tenDangNhap) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(t.id) FROM TaiKhoan t WHERE t.tenDangNhap = :ten";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("ten", tenDangNhap);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            // Nếu có lỗi, trả về false để tránh chặn đăng ký oan
            return false;
        }
    }

    /**
     * Kiểm tra xem Email đã tồn tại chưa (dùng cho Service đăng ký).
     *
     * @param email Email cần kiểm tra
     * @return true nếu đã tồn tại, false nếu chưa.
     */
    public boolean isEmailExisted(String email) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(t.id) FROM TaiKhoan t WHERE t.email = :email";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("email", email);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            // Nếu có lỗi, trả về false để tránh chặn đăng ký oan
            return false;
        }
    }

    /**
     * Tìm các tài khoản đang hoạt động và chưa được gán cho bất kỳ Nhân viên
     * hay Bệnh nhân nào.
     *
     * @param role (Tùy chọn) Lọc thêm theo vai trò (ví dụ: "BENH_NHAN"). Để
     * null hoặc rỗng nếu không muốn lọc theo vai trò.
     * @return Danh sách các TaiKhoan (Entity) phù hợp.
     */
    public List<TaiKhoan> findActiveAndUnassignedAccounts(String role) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Sửa lại HQL để join đúng cách trong Hibernate
            String hql = "SELECT tk FROM TaiKhoan tk "
                    + "WHERE tk.trangThai = :trangThai "
                    + "AND NOT EXISTS (SELECT 1 FROM NhanVien nv WHERE nv.taiKhoan = tk) "
                    + // Dùng NOT EXISTS
                    "AND NOT EXISTS (SELECT 1 FROM BenhNhan bn WHERE bn.taiKhoan = tk) "; // Dùng NOT EXISTS

            if (role != null && !role.trim().isEmpty()) {
                hql += " AND tk.vaiTro = :vaiTro";
            }

            Query<TaiKhoan> query = session.createQuery(hql, TaiKhoan.class);
            query.setParameter("trangThai", TRANG_THAI_HOAT_DONG); // Dùng hằng số

            if (role != null && !role.trim().isEmpty()) {
                query.setParameter("vaiTro", role);
            }

            return query.list();
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            return Collections.emptyList(); // Trả về list rỗng nếu lỗi
        }
    }

    // ============================================
    // === CÁC HÀM MỚI CHO THONGBAOSERVICE ===
    // ============================================
    /**
     * HÀM MỚI: Lấy danh sách ID của tất cả tài khoản đang hoạt động. Dùng cho
     * ThongBaoService khi gửi cho "ALL".
     *
     * @return List<Integer> chứa ID tài khoản.
     */
    public List<Integer> getAllActiveTaiKhoanIds() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Chỉ select cột 'id'
            String hql = "SELECT t.id FROM TaiKhoan t WHERE t.trangThai = :trangThai";
            Query<Integer> query = session.createQuery(hql, Integer.class);
            query.setParameter("trangThai", TRANG_THAI_HOAT_DONG);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            return Collections.emptyList();
        }
    }

    /**
     * HÀM MỚI: Lấy danh sách ID của các tài khoản đang hoạt động theo vai trò.
     * Dùng cho ThongBaoService khi gửi cho "ROLE".
     *
     * @param role Tên vai trò cần lọc.
     * @return List<Integer> chứa ID tài khoản.
     */
    public List<Integer> getActiveTaiKhoanIdsByRole(String role) {
        if (role == null || role.trim().isEmpty()) {
            return Collections.emptyList(); // Trả về rỗng nếu role không hợp lệ
        }
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Chỉ select cột 'id' và thêm điều kiện 'vaiTro'
            String hql = "SELECT t.id FROM TaiKhoan t WHERE t.trangThai = :trangThai AND t.vaiTro = :vaiTro";
            Query<Integer> query = session.createQuery(hql, Integer.class);
            query.setParameter("trangThai", TRANG_THAI_HOAT_DONG);
            query.setParameter("vaiTro", role.trim()); // Dùng trim để an toàn
            return query.list();
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            return Collections.emptyList();
        }
    }

    // === THÊM HÀM MỚI ===
    /**
     * Lấy danh sách các vai trò (vaiTro) duy nhất đang tồn tại trong bảng
     * TaiKhoan.
     *
     * @return List<String> chứa các tên vai trò.
     */
    public List<String> getDistinctVaiTro() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Dùng DISTINCT để chỉ lấy các giá trị duy nhất
            String hql = "SELECT DISTINCT t.vaiTro FROM TaiKhoan t WHERE t.vaiTro IS NOT NULL";
            Query<String> query = session.createQuery(hql, String.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace(); // Nên dùng logger
            return Collections.emptyList(); // Trả về rỗng nếu lỗi
        }
    }
    // ======================

    /**
     * HÀM MỚI: Lấy Map<ID, TenDangNhap> từ một danh sách các ID. Rất hiệu quả
     * để tra cứu tên hàng loạt.
     *
     * @param ids Danh sách các ID tài khoản cần tìm
     * @return Một Map<Integer, String>
     */
    public Map<Integer, String> getTenDangNhapByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyMap();
        }

        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Dùng HQL 'IN (:ids)'
            String hql = "SELECT t.id, t.tenDangNhap FROM TaiKhoan t WHERE t.id IN (:ids)";
            Query<Object[]> query = session.createQuery(hql, Object[].class);
            query.setParameterList("ids", ids); // Dùng setParameterList cho IN

            // Chuyển kết quả List<Object[]> thành Map<Integer, String>
            return query.list().stream()
                    .collect(Collectors.toMap(
                            row -> (Integer) row[0], // Key là ID
                            row -> (String) row[1] // Value là TenDangNhap
                    ));
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyMap();
        }
    }
}
