/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;

import java.util.Collections;
import java.util.List;
import model.Entity.DonThuoc;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;

public class DonThuocDAO {

    public DonThuoc create(DonThuoc donThuoc, Session session) {
        session.save(donThuoc);
        return donThuoc;
    }

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

    // Trong dao/DonThuocDAO.java
    public DonThuoc getById(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<DonThuoc> query = session.createQuery(
                    "SELECT DISTINCT dt FROM DonThuoc dt "
                    + "LEFT JOIN FETCH dt.phieuKham pk "
                    + "LEFT JOIN FETCH pk.benhNhan "
                    + "LEFT JOIN FETCH dt.chiTietDonThuoc cdt "
                    + "LEFT JOIN FETCH cdt.thuoc "
                    + "WHERE dt.id = :id",
                    DonThuoc.class
            );
            query.setParameter("id", id);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Trong dao/DonThuocDAO.java
    public List<DonThuoc> getAll() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<DonThuoc> query = session.createQuery(
                    // Dùng DISTINCT để tránh các bản ghi DonThuoc bị lặp lại
                    "SELECT DISTINCT dt FROM DonThuoc dt "
                    + "LEFT JOIN FETCH dt.phieuKham pk "
                    + "LEFT JOIN FETCH pk.benhNhan "
                    + "LEFT JOIN FETCH dt.chiTietDonThuoc cdt "
                    + // Lấy danh sách chi tiết
                    "LEFT JOIN FETCH cdt.thuoc "
                    + // Lấy luôn thông tin thuốc trong chi tiết
                    "ORDER BY dt.ngayKeDon DESC",
                    DonThuoc.class
            );
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Tìm kiếm các đơn thuốc theo tên bệnh nhân (tìm kiếm tương đối).
     *
     */
    public List<DonThuoc> findByPatientName(String patientName) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {

            Query<DonThuoc> query = session.createQuery(
                    "SELECT DISTINCT dt FROM DonThuoc dt "
                    + "LEFT JOIN FETCH dt.phieuKham pk "
                    + "LEFT JOIN FETCH pk.benhNhan bn "
                    + "LEFT JOIN FETCH dt.chiTietDonThuoc cdt "
                    + // Lấy danh sách chi tiết
                    "LEFT JOIN FETCH cdt.thuoc "
                    + // Lấy luôn thông tin thuốc trong chi tiết
                    "WHERE bn.hoTen LIKE :keyword "
                    + "ORDER BY dt.ngayKeDon DESC",
                    DonThuoc.class
            );
            query.setParameter("keyword", "%" + patientName + "%");
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
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
