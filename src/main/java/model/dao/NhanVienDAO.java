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
     *
     * @param nhanVien Đối tượng NhanVien (Entity)
     * @return Đối tượng NhanVien đã được lưu (có ID) hoặc null nếu lỗi.
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
     *
     * @param nhanVien Đối tượng NhanVien (Entity) đã được thay đổi.
     * @return true nếu cập nhật thành công, false nếu lỗi.
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
     *
     * @param id ID của nhân viên
     * @return Đối tượng NhanVien hoặc null nếu không tìm thấy.
     */
    public NhanVien getById(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Dùng session.get() để lấy bằng khóa chính
            // CẢNH BÁO: Các trường LAZY (TaiKhoan, Khoa) sẽ không được tải.
            // Service sẽ phải xử lý việc này nếu cần.
            return session.get(NhanVien.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy thông tin nhân viên bằng ID (Tải đầy đủ quan hệ). Dùng hàm này nếu
     * bạn cần lấy cả thông tin Khoa và TaiKhoan.
     *
     * @param id ID của nhân viên
     * @return Đối tượng NhanVien (đã tải lazy relations) hoặc null.
     */
    public NhanVien getByIdWithRelations(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM NhanVien n "
                    + "LEFT JOIN FETCH n.taiKhoan "
                    + "LEFT JOIN FETCH n.khoa "
                    + // Dùng LEFT JOIN vì khoa có thể null
                    "WHERE n.id = :id";
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
     *
     * @return Một danh sách (List) các đối tượng NhanVien.
     */
    public List<NhanVien> getAll() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Cảnh báo: Hàm này sẽ gây lỗi N+1 select nếu service
            // duyệt qua danh sách và gọi getKhoa() hoặc getTaiKhoan().
            // Cân nhắc dùng 'getAllWithRelations()' nếu cần.
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
     *
     * @return Danh sách NhanVien (đã tải lazy relations).
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
     *
     * @param soDienThoai SĐT cần kiểm tra
     * @return true nếu đã tồn tại, false nếu chưa.
     */
    public boolean isSoDienThoaiExisted(String soDienThoai) {
        // Bỏ qua kiểm tra nếu sđt là null hoặc rỗng
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
     *
     * @param taiKhoanId ID tài khoản cần kiểm tra
     * @return true nếu đã được liên kết, false nếu chưa.
     */
    public boolean isTaiKhoanIdLinked(int taiKhoanId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Truy vấn dựa trên ID của đối tượng liên quan
            String hql = "SELECT count(n.id) FROM NhanVien n WHERE n.taiKhoan.id = :tkId";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("tkId", taiKhoanId);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true; // Failsafe
        }
    }

    /**
     * Tìm nhân viên bằng taiKhoanId.
     *
     * @param taiKhoanId ID của tài khoản
     * @return Đối tượng NhanVien hoặc null nếu không tìm thấy.
     */
    public NhanVien findByTaiKhoanId(int taiKhoanId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM NhanVien n WHERE n.taiKhoan.id = :tkId";
            Query<NhanVien> query = session.createQuery(hql, NhanVien.class);
            query.setParameter("tkId", taiKhoanId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

   public List<NhanVien> findDoctorsBySpecialty() {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        // ✨ SỬA LẠI HQL ĐỂ LỌC THEO VAI TRÒ BÁC SĨ
        Query<NhanVien> query = session.createQuery(
            "FROM NhanVien nv WHERE nv.taiKhoan.vaiTro = :role",
            NhanVien.class
        );

        query.setParameter("role", "BAC_SI"); // Thêm tham số cho vai trò
        return query.list();
    } catch (Exception e) {
        e.printStackTrace();
        return Collections.emptyList();
    }
}
}
