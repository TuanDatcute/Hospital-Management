package model.dao;

import java.math.BigDecimal;
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
     * Lấy một chỉ định dịch vụ bằng ID của nó. 
     * của bạn.
     */
    public ChiDinhDichVu getById(int id) {
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        Query<ChiDinhDichVu> query = session.createQuery(
            "SELECT cdd FROM ChiDinhDichVu cdd " +
            "JOIN FETCH cdd.dichVu " + 
            "WHERE cdd.id = :id", 
            ChiDinhDichVu.class
        );
        query.setParameter("id", id);
        return query.uniqueResult();
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
    public List<ChiDinhDichVu> findByPhieuKhamId(int pkbId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ChiDinhDichVu> query = session.createQuery(
                    "FROM ChiDinhDichVu cdd "
                    + "LEFT JOIN FETCH cdd.dichVu "
                    + "WHERE cdd.phieuKham.id = :pkbId",
                    ChiDinhDichVu.class
            );
            query.setParameter("pkbId", pkbId);
            return query.list();
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

    public BigDecimal calculateTotalService(int phieuKhamId, Session session) {
        String hql = "SELECT SUM(cdv.dichVu.donGia) FROM ChiDinhDichVu cdv "
                + "WHERE cdv.phieuKham.id = :phieuKhamId AND cdv.trangThai = 'HOAN_THANH'"; // Chỉ tính DV đã hoàn thành
        BigDecimal total = session.createQuery(hql, BigDecimal.class)
                .setParameter("phieuKhamId", phieuKhamId)
                .uniqueResult();
        return total == null ? BigDecimal.ZERO : total;
    }

    public List<ChiDinhDichVu> findByPhieuKhamId(int phieuKhamId, Session session) {
        String hql = "FROM ChiDinhDichVu cddv WHERE cddv.phieuKham.id = :phieuKhamId";

        Query<ChiDinhDichVu> query = session.createQuery(hql, ChiDinhDichVu.class);
        query.setParameter("phieuKhamId", phieuKhamId);

        return query.list();
    }
}
