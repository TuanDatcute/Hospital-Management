/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;

import model.Entity.PhieuKhamBenh;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
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

    //Cập nhật thông tin trong quá trình khám .
    public void update(PhieuKhamBenh phieuKham) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(phieuKham);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            PhieuKhamBenh phieuKham = session.get(PhieuKhamBenh.class, id);
            if (phieuKham != null) {
                session.delete(phieuKham);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
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

    //
    public boolean isMaPhieuKhamExisted(String maPhieuKham) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(p.id) FROM PhieuKhamBenh p WHERE p.maPhieuKham = :ma";
            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("ma", maPhieuKham);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public List<PhieuKhamBenh> getAll() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM PhieuKhamBenh", PhieuKhamBenh.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<PhieuKhamBenh> findByBenhNhanId(int benhNhanId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT FROM PhieuKhamBenh p WHERE p.benhNhan.id = :ma";
            Query<PhieuKhamBenh> query = session.createQuery(hql, PhieuKhamBenh.class);
            query.setParameter("ma", benhNhanId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
