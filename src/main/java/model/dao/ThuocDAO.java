package model.dao;

import java.util.Collections;
import java.util.List;
import model.Entity.Thuoc;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;

public class ThuocDAO {

    public Thuoc getById(int id, Session session) {
        return session.get(Thuoc.class, id);
    }

    public Thuoc create(Thuoc thuoc) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(thuoc);
            transaction.commit();
            return thuoc;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    public Thuoc getById(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(Thuoc.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Thuoc> getAll() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.createQuery("FROM Thuoc", Thuoc.class).list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public void update(Thuoc thuoc) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(thuoc);
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
            Thuoc thuoc = session.get(Thuoc.class, id);
            if (thuoc != null) {
                session.delete(thuoc);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public boolean isTenThuocExisted(String tenThuoc) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Long> query = session.createQuery(
                    "SELECT count(t.id) FROM Thuoc t WHERE t.tenThuoc = :ten", Long.class);
            query.setParameter("ten", tenThuoc);
            return query.uniqueResult() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }

    public List<Thuoc> findByName(String tenThuoc) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Sử dụng HQL với mệnh đề LIKE để tìm kiếm tương đối
            Query<Thuoc> query = session.createQuery(
                    "FROM Thuoc t WHERE t.tenThuoc LIKE :ten", Thuoc.class
            );
            // Thêm dấu '%' để tìm kiếm bất kỳ chuỗi nào chứa từ khóa
            query.setParameter("ten", "%" + tenThuoc + "%");

            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Thuoc findToUpdateMed(String tenThuoc) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<Thuoc> query = session.createQuery(
                    "FROM Thuoc t WHERE t.tenThuoc = :ten", Thuoc.class);
            query.setParameter("ten", tenThuoc);
            // uniqueResult sẽ trả về một đối tượng duy nhất hoặc null
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
