package model.dao;

import java.util.List;
import model.Entity.DichVu;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;

/**
 * Lớp DAO cho Dịch Vụ (Service).
 * Chịu trách nhiệm cho mọi thao tác CRUD với bảng DichVu trong CSDL.
 */
public class DichVuDAO {

    /**
     * Thêm một dịch vụ mới vào CSDL.
     * @param dichVu Đối tượng DichVu (Entity) cần lưu.
     * @return Đối tượng DichVu sau khi đã được lưu (đã có ID).
     */
    public DichVu create(DichVu dichVu) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(dichVu);
            transaction.commit();
            return dichVu;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy thông tin một dịch vụ bằng ID.
     * @param id ID của dịch vụ cần tìm.
     * @return Đối tượng DichVu hoặc null nếu không tìm thấy.
     */
    public DichVu getById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(DichVu.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy tất cả các dịch vụ trong CSDL.
     * @return Danh sách các đối tượng DichVu.
     */
    public List<DichVu> getAll() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM DichVu", DichVu.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return null; // Hoặc trả về một danh sách rỗng
        }
    }

    /**
     * Cập nhật thông tin một dịch vụ đã có.
     * @param dichVu Đối tượng DichVu chứa thông tin mới.
     */
    public void update(DichVu dichVu) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(dichVu);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    /**
     * Xóa một dịch vụ khỏi CSDL.
     * @param id ID của dịch vụ cần xóa.
     */
    public void delete(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            DichVu dichVu = session.get(DichVu.class, id);
            if (dichVu != null) {
                session.delete(dichVu);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    /**
     * Kiểm tra xem một tên dịch vụ đã tồn tại trong CSDL hay chưa.
     * @param tenDichVu Tên dịch vụ cần kiểm tra.
     * @return true nếu đã tồn tại, false nếu chưa.
     */
    public boolean isTenDichVuExisted(String tenDichVu) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                "SELECT count(d.id) FROM DichVu d WHERE d.tenDichVu = :ten", 
                Long.class
            );
            query.setParameter("ten", tenDichVu);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            // Trong trường hợp lỗi, trả về true để ngăn chặn việc tạo mới, đảm bảo an toàn dữ liệu
            return true;
        }
    }
    
}