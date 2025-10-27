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
            Query<PhieuKhamBenh> query = session.createQuery(
                    "SELECT pkb FROM PhieuKhamBenh pkb "
                    + "LEFT JOIN FETCH pkb.benhNhan "
                    + "LEFT JOIN FETCH pkb.bacSi "
                    + "LEFT JOIN FETCH pkb.donThuoc dt "
                    + // Lấy đơn thuốc liên quan
                    "LEFT JOIN FETCH dt.chiTietDonThuoc cdt "
                    + // Lấy danh sách chi tiết của đơn thuốc
                    "LEFT JOIN FETCH cdt.thuoc "
                    + // Lấy luôn thông tin thuốc trong chi tiết
                    "WHERE pkb.id = :id",
                    PhieuKhamBenh.class
            );
            query.setParameter("id", phieuKhamId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public PhieuKhamBenh getEncounterById(int id, Session session) {
        Query<PhieuKhamBenh> query = session.createQuery("FROM PhieuKhamBenh pk LEFT JOIN FETCH pk.benhNhan WHERE pk.id = :id", PhieuKhamBenh.class);
        query.setParameter("id", id);
        return query.uniqueResult();
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
    try (Session session = HibernateUtil.getSessionFactory().openSession()) {
        
        // ✨ SỬ DỤNG MỘT CÂU TRUY VẤN DUY NHẤT VÀ ĐẦY ĐỦ NHẤT ✨
        Query<PhieuKhamBenh> query = session.createQuery(
            // Dùng DISTINCT để tránh các bản ghi PhieuKhamBenh bị lặp lại
            "SELECT DISTINCT pkb FROM PhieuKhamBenh pkb " +
            
            // Lấy các mối quan hệ đơn lẻ
            "LEFT JOIN FETCH pkb.benhNhan " +
            "LEFT JOIN FETCH pkb.bacSi " +
            "LEFT JOIN FETCH pkb.lichHen " +
            
            // Lấy danh sách Chỉ Định Dịch Vụ và các con của nó
            "LEFT JOIN FETCH pkb.danhSachChiDinh cdd " +
            "LEFT JOIN FETCH cdd.dichVu " +
            
            // Lấy Đơn Thuốc và các con của nó
            "LEFT JOIN FETCH pkb.donThuoc dt " +
            "LEFT JOIN FETCH dt.chiTietDonThuoc cdt " +
            "LEFT JOIN FETCH cdt.thuoc " +
            
            // Sắp xếp kết quả
            "ORDER BY pkb.thoiGianKham DESC", 
            PhieuKhamBenh.class
        );
        
        return query.list();
        
    } catch (Exception e) {
        // Ghi log lỗi một cách chi tiết
        System.err.println("Lỗi nghiêm trọng khi lấy tất cả Phiếu Khám Bệnh:");
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
