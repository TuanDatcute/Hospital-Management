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

    /**
     * Kiểm tra xem một loại thuốc có đang được sử dụng trong bất kỳ đơn thuốc
     * nào không. Phương thức này an toàn để gọi trước khi thực hiện thao tác
     * xóa.
     *
     * @param thuocId ID của thuốc cần kiểm tra.
     * @return true nếu thuốc đang được sử dụng (có trong ít nhất 1 đơn thuốc),
     * false nếu thuốc không được sử dụng.
     */
    public static boolean isMedicationInUse(int thuocId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {

            // Tạo câu truy vấn HQL để đếm số lượng bản ghi ChiTietDonThuoc
            // có liên kết đến thuocId này.
            Query<Long> query = session.createQuery(
                    // ct.thuoc.id là cách truy vấn HQL, nó tự động join đến bảng Thuoc
                    "SELECT count(ct.id) FROM ChiTietDonThuoc ct WHERE ct.thuoc.id = :thuocId",
                    Long.class
            );

            query.setParameter("thuocId", thuocId);
            Long count = query.uniqueResult();

            // Nếu số lượng lớn hơn 0, nghĩa là thuốc đang được sử dụng
            return (count != null && count > 0);

        } catch (Exception e) {
            e.printStackTrace();

            return true;
        }
    }

    public ChiTietDonThuoc create(ChiTietDonThuoc chiTiet) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(chiTiet);
            transaction.commit();
            return chiTiet;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    public ChiTietDonThuoc getById(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {

            Query<ChiTietDonThuoc> query = session.createQuery(
                    "SELECT cdt FROM ChiTietDonThuoc cdt "
                    + "JOIN FETCH cdt.thuoc "
                    + // Lấy luôn cả đối tượng Thuoc liên quan
                    "WHERE cdt.id = :id",
                    ChiTietDonThuoc.class
            );
            query.setParameter("id", id);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void update(ChiTietDonThuoc chiTiet) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(chiTiet);
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
            ChiTietDonThuoc chiTiet = session.get(ChiTietDonThuoc.class, id);
            if (chiTiet != null) {
                session.delete(chiTiet);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }

    public List<ChiTietDonThuoc> findByDonThuocId(int donThuocId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
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
