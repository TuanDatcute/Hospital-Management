/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;

import model.Entity.PhieuKhamBenh;
import java.math.BigDecimal;
import org.hibernate.Session;
import org.hibernate.query.Query; 
import org.hibernate.Transaction;
import util.HibernateUtil;

/**
 *
 * @author SunnyU
 */
public class PhieuKhamBenhDAO {

    //Tạo một phiếu khám bệnh mới khi bệnh nhân đến khám.
    public PhieuKhamBenh create(PhieuKhamBenh phieuKham) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Bắt đầu một transaction
            transaction = session.beginTransaction();
            // Lưu đối tượng entity
            session.save(phieuKham);
            // Commit transaction
            transaction.commit();
            // Trả về đối tượng đã được lưu (lúc này đã có ID)
            return phieuKham;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    //Cập nhật thông tin trong quá trình khám (thêm chẩn đoán, kết luận...).
    public boolean updateEncounterDetails(int phieuKhamId, PhieuKhamBenh thongTinCapNhat) {
        return false;
    }

    //Xem chi tiết một lần khám.
    public PhieuKhamBenh getEncounterById(int phieuKhamId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(PhieuKhamBenh.class, phieuKhamId);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Thêm hoặc cập nhật các chỉ số sinh tồn.
    public boolean addVitals(int phieuKhamId, BigDecimal nhietDo, String huyetAp, Integer nhipTim, Integer nhipTho) {
        return true;
    }

    //
    public boolean isMaPhieuKhamExisted(String maPhieuKham) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(p.id) FROM PhieuKhamBenh p WHERE p.maPhieuKham = :ma";
            Query<Long> query = session.createQuery(hql,Long.class);
            query.setParameter("ma", maPhieuKham);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }
}
