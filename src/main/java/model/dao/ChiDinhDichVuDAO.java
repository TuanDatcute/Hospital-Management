package model.dao;

import java.util.Collections;
import java.util.List;
import model.Entity.ChiDinhDichVu;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;

public class ChiDinhDichVuDAO {

    /**
     * Thêm một chỉ định dịch vụ mới vào CSDL.
     */
    public ChiDinhDichVu create(ChiDinhDichVu chiDinh) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(chiDinh);
            transaction.commit();
            return chiDinh;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy một chỉ định dịch vụ bằng ID của nó. Tương ứng với hàm findService
     * của bạn.
     */
    public ChiDinhDichVu getById(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(ChiDinhDichVu.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Cập nhật một chỉ định dịch vụ đã có.
     */
    public void update(ChiDinhDichVu chiDinh) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(chiDinh);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    /**
     * Lấy tất cả các chỉ định dịch vụ thuộc về một phiếu khám.
     */
    public List<ChiDinhDichVu> findByPhieuKhamId(int phieuKhamId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ChiDinhDichVu> query = session.createQuery(
                    "FROM ChiDinhDichVu cddv WHERE cddv.phieuKham.id = :phieuKhamId",
                    ChiDinhDichVu.class
            );
            query.setParameter("phieuKhamId", phieuKhamId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList(); // Trả về danh sách rỗng nếu có lỗi
        }
    }

    public boolean isServiceInUse(int dichVuId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT count(d.id) FROM ChiDinhDichVu d WHERE d.dichVu.id = :id",
                    Long.class
            );
            query.setParameter("id", dichVuId);

            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi kiểm tra dịch vụ đang sử dụng với ID: " + dichVuId, e);
        }
    }

    
}
