/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;

import java.util.List;
import model.Entity.DonThuoc;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;

public class DonThuocDAO {

    public DonThuoc create(DonThuoc donThuoc) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(donThuoc);
            transaction.commit();
            return donThuoc;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    public DonThuoc getById(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(DonThuoc.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Tìm đơn thuốc dựa trên ID của phiếu khám. Vì mối quan hệ là 1-1, nên chỉ
     * trả về một kết quả.
     */
    public DonThuoc findByPhieuKhamId(int phieuKhamId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<DonThuoc> query = session.createQuery(
                    "FROM DonThuoc dt WHERE dt.phieuKham.id = :phieuKhamId", DonThuoc.class);
            query.setParameter("phieuKhamId", phieuKhamId);
            return query.uniqueResult(); // uniqueResult vì chỉ có 1 hoặc 0 kết quả
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void update(DonThuoc donThuoc) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(donThuoc);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
