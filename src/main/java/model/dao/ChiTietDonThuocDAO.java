/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;

/**
 *
 * @author SunnyU
 */
import java.util.Collections;
import java.util.List;
import model.Entity.ChiTietDonThuoc;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;

public class ChiTietDonThuocDAO {

    public ChiTietDonThuoc create(ChiTietDonThuoc chiTiet) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(chiTiet);
            transaction.commit();
            return chiTiet;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
            return null;
        }
    }

    public ChiTietDonThuoc getById(int id) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(ChiTietDonThuoc.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void update(ChiTietDonThuoc chiTiet) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(chiTiet);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }
    
    public void delete(int id) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            ChiTietDonThuoc chiTiet = session.get(ChiTietDonThuoc.class, id);
            if (chiTiet != null) {
                session.delete(chiTiet);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            e.printStackTrace();
        }
    }

    public List<ChiTietDonThuoc> findByDonThuocId(int donThuocId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<ChiTietDonThuoc> query = session.createQuery(
                "FROM ChiTietDonThuoc c WHERE c.donThuoc.id = :donThuocId", ChiTietDonThuoc.class);
            query.setParameter("donThuocId", donThuocId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
