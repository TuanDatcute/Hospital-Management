package model.dao;

import model.Entity.PasswordChangeRequest;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil; // Giả sử lớp tiện ích Hibernate của bạn tên là HibernateUtil
import java.util.Optional;

/**
 * DAO cho Entity PasswordChangeRequest.
 */
public class PasswordChangeRequestDAO {

    /**
     * Lưu một yêu cầu đổi mật khẩu mới.
     */
    public boolean create(PasswordChangeRequest request) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(request);
            transaction.commit();
            return true;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Tìm kiếm một yêu cầu dựa trên Token.
     */
    public Optional<PasswordChangeRequest> findByToken(String token) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // HQL: Tìm bản ghi có token khớp
            String hql = "FROM PasswordChangeRequest WHERE token = :token";
            Query<PasswordChangeRequest> query = session.createQuery(hql, PasswordChangeRequest.class);
            query.setParameter("token", token);

            // Lấy kết quả đầu tiên (token là unique)
            return query.uniqueResultOptional();
        } catch (HibernateException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    /**
     * Xóa yêu cầu sau khi đã được sử dụng hoặc hết hạn.
     */
    public boolean delete(PasswordChangeRequest request) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.delete(request);
            transaction.commit();
            return true;
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }
}
