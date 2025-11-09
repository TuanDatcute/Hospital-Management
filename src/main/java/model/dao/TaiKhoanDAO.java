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
     *
     * @param taiKhoan Đối tượng TaiKhoan (Entity)
     * @return Đối tượng TaiKhoan đã được lưu (có ID) hoặc null nếu lỗi.
     */
    public TaiKhoan create(TaiKhoan taiKhoan) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            // Đảm bảo trạng thái mặc định khi tạo mới

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
     *
     * @param taiKhoan Đối tượng TaiKhoan (Entity) đã được thay đổi.
     * @return true nếu cập nhật thành công, false nếu lỗi.
     */
    public void update(TaiKhoan taiKhoan) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(taiKhoan);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace(); // Nên dùng logger
            // Cân nhắc ném lỗi thay vì trả về false
            // throw new RuntimeException("Lỗi khi cập nhật tài khoản: " + e.getMessage(), e);
            //return false;
            throw new RuntimeException("Lỗi khi cập nhật tài khoản: " + e.getMessage(), e);
        }
    }

    /**
     * HÀM MỚI: Xóa một tài khoản khỏi CSDL.
     */
    public void delete(TaiKhoan taiKhoan) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
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
            return query.uniqueResult(); // Trả về null nếu không tìm thấy
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
            return Collections.emptyList();
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
     * HÀM MỚI (SỬA LỖI): Tìm tất cả tài khoản NHÂN VIÊN (không phải BENH_NHAN)
     * đang hoạt động và chưa được gán.
     *
     * @return Danh sách các TaiKhoan (Entity) phù hợp.
     */
    public List<TaiKhoan> findAllActiveAndUnassignedStaffAccounts() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT tk FROM TaiKhoan tk "
                    + "WHERE tk.trangThai = :trangThai "
                    // Quan trọng: Chỉ loại trừ bệnh nhân
                    + "AND tk.vaiTro != 'BENH_NHAN' "
                    // Kiểm tra chưa gán cho cả NhanVien và BenhNhan
                    + "AND NOT EXISTS (SELECT 1 FROM NhanVien nv WHERE nv.taiKhoan = tk) "
                    + "AND NOT EXISTS (SELECT 1 FROM BenhNhan bn WHERE bn.taiKhoan = tk) ";

            Query<TaiKhoan> query = session.createQuery(hql, TaiKhoan.class);
            query.setParameter("trangThai", TRANG_THAI_HOAT_DONG);

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
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
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
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
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
     * HÀM MỚI: Tìm một tài khoản bằng Email (dùng cho Quên mật khẩu). Trả về
     * Optional<TaiKhoan> để Service có thể lấy entity và cập nhật token.
     */
    public Optional<TaiKhoan> findByEmail(String email) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
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
    /**
     * HÀM MỚI: Lấy danh sách ID của tất cả tài khoản đang hoạt động. Dùng cho
     * ThongBaoService khi gửi cho "ALL".
     *
     * @return List<Integer> chứa ID tài khoản.
     */
    public List<Integer> getAllActiveTaiKhoanIds() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT t.id FROM TaiKhoan t WHERE t.trangThai = :trangThai";
            Query<Integer> query = session.createQuery(hql, Integer.class);
            query.setParameter("trangThai", TRANG_THAI_HOAT_DONG);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
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
            return Collections.emptyList();
        }
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
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

    /**
     * HÀM MỚI (PHÂN TRANG): Lấy danh sách Tài khoản (có phân trang) Lấy tất cả,
     * bao gồm cả tài khoản BỊ KHÓA (để Admin quản lý)
     */
    public List<TaiKhoan> getAllTaiKhoan(int page, int pageSize) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM TaiKhoan t ORDER BY t.id ASC";
            Query<TaiKhoan> query = session.createQuery(hql, TaiKhoan.class);

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
     * HÀM MỚI (PHÂN TRANG): Đếm tổng số Tài khoản (bao gồm cả bị khóa)
     */
    public long getTotalTaiKhoanCount() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(t.id) FROM TaiKhoan t";
            Query<Long> query = session.createQuery(hql, Long.class);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * HÀM MỚI (TÌM KIẾM): Tìm kiếm Tài khoản (có phân trang) Tìm theo Tên đăng
     * nhập hoặc Email
     */
    public List<TaiKhoan> searchTaiKhoanPaginated(String keyword, int page, int pageSize) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM TaiKhoan t "
                    + "WHERE t.tenDangNhap LIKE :keyword OR t.email LIKE :keyword "
                    + "ORDER BY t.id ASC";

            Query<TaiKhoan> query = session.createQuery(hql, TaiKhoan.class);
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
     * HÀM MỚI (TÌM KIẾM): Đếm kết quả tìm kiếm Tài khoản
     */
    public long getTaiKhoanSearchCount(String keyword) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(t.id) FROM TaiKhoan t "
                    + "WHERE t.tenDangNhap LIKE :keyword OR t.email LIKE :keyword";

            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("keyword", "%" + keyword + "%");
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
