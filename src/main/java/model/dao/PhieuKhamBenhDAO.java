/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model.dao;

import model.Entity.PhieuKhamBenh;
import java.util.Collections;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.hibernate.Transaction;
import util.HibernateUtil;

/**
 *
 * @author SunnyU
 */
public class PhieuKhamBenhDAO {

    private final SessionFactory sessionFactory;

    public PhieuKhamBenhDAO() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

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

    /**
     * PHƯƠNG THỨC ĐẦY ĐỦ: Lấy thông tim để chỉnh sửa trạng thái
     *
     * @return Một đối tượng PhieuKhamBenh duy nhất với tất cả dữ liệu liên
     * quan.
     */
    public PhieuKhamBenh getDetailsByIdToUpdateStatus(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Câu query này đã lấy sẵn danh sách chỉ định, hoàn hảo cho nghiệp vụ của chúng ta
            Query<PhieuKhamBenh> query = session.createQuery(
                    "SELECT pkb FROM PhieuKhamBenh pkb "
                    + "LEFT JOIN FETCH pkb.danhSachChiDinh cdd "
                    + "LEFT JOIN FETCH pkb.benhNhan "
                    + "WHERE pkb.id = :id",
                    PhieuKhamBenh.class
            );
            query.setParameter("id", id);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Trong file dao/PhieuKhamBenhDAO.java
    public PhieuKhamBenh getEncounterById(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {

            // BƯỚC 1: Lấy Phiếu Khám và các mối quan hệ ĐƠN LẺ (@ManyToOne, @OneToOne)
            PhieuKhamBenh phieuKham = session.createQuery(
                    "SELECT pkb FROM PhieuKhamBenh pkb "
                    + "LEFT JOIN FETCH pkb.benhNhan "
                    + "LEFT JOIN FETCH pkb.bacSi "
                    + "LEFT JOIN FETCH pkb.lichHen "
                    + "WHERE pkb.id = :id",
                    PhieuKhamBenh.class
            ).setParameter("id", id).uniqueResult();

            if (phieuKham == null) {
                return null;
            }

            // BƯỚC 2: Tải DANH SÁCH Chỉ Định Dịch Vụ
            // Hibernate sẽ tự động "đính kèm" danh sách này vào đối tượng phieuKham đã có
            session.createQuery(
                    "SELECT DISTINCT pkb FROM PhieuKhamBenh pkb "
                    + "LEFT JOIN FETCH pkb.danhSachChiDinh cdd "
                    + "LEFT JOIN FETCH cdd.dichVu "
                    + "WHERE pkb.id = :id",
                    PhieuKhamBenh.class
            ).setParameter("id", id).uniqueResult();

            // BƯỚC 3: Tải DANH SÁCH Chi Tiết Đơn Thuốc
            session.createQuery(
                    "SELECT DISTINCT pkb FROM PhieuKhamBenh pkb "
                    + "LEFT JOIN FETCH pkb.donThuoc dt "
                    + "LEFT JOIN FETCH dt.chiTietDonThuoc cdt "
                    + "LEFT JOIN FETCH cdt.thuoc "
                    + "WHERE pkb.id = :id",
                    PhieuKhamBenh.class
            ).setParameter("id", id).uniqueResult();

            return phieuKham;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * PHƯƠNG THỨC GỌN NHẸ: Chỉ lấy thông tin cần thiết để hiển thị danh sách.
     *
     * @return Danh sách các PhieuKhamBenh với thông tin cơ bản.
     */
    public List<PhieuKhamBenh> getAllForListing() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // ✨ CHỈ FETCH NHỮNG GÌ CẦN CHO DANH SÁCH ✨
            Query<PhieuKhamBenh> query = session.createQuery(
                    "SELECT DISTINCT pkb FROM PhieuKhamBenh pkb "
                    + "LEFT JOIN FETCH pkb.benhNhan "
                    + "LEFT JOIN FETCH pkb.bacSi "
                    + "ORDER BY pkb.thoiGianKham DESC",
                    PhieuKhamBenh.class
            );
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * PHƯƠNG THỨC GỌN NHẸ: Tìm kiếm và chỉ lấy thông tin cần cho danh sách.
     */
    public List<PhieuKhamBenh> searchForListing(String keyword) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<PhieuKhamBenh> query = session.createQuery(
                    "SELECT DISTINCT pkb FROM PhieuKhamBenh pkb "
                    + "JOIN FETCH pkb.benhNhan bn "
                    + "JOIN FETCH pkb.bacSi "
                    + "WHERE pkb.maPhieuKham LIKE :keyword OR bn.hoTen LIKE :keyword "
                    + "ORDER BY pkb.trangThai ASC, pkb.thoiGianKham DESC",
                    PhieuKhamBenh.class
            );
            query.setParameter("keyword", "%" + keyword + "%");
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
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
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {

            Query<PhieuKhamBenh> query = session.createQuery(
                    // Dùng DISTINCT để tránh các bản ghi PhieuKhamBenh bị lặp lại
                    "SELECT DISTINCT pkb FROM PhieuKhamBenh pkb "
                    + "LEFT JOIN FETCH pkb.benhNhan "
                    + "LEFT JOIN FETCH pkb.bacSi "
                    + "LEFT JOIN FETCH pkb.lichHen "
                    + "LEFT JOIN FETCH pkb.danhSachChiDinh cdd "
                    + "LEFT JOIN FETCH cdd.dichVu "
                    + "LEFT JOIN FETCH pkb.donThuoc dt "
                    + "LEFT JOIN FETCH dt.chiTietDonThuoc cdt "
                    + "LEFT JOIN FETCH cdt.thuoc "
                    + "ORDER BY pkb.trangThai ASC, pkb.thoiGianKham DESC",
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

    // HQL để tìm PKB chưa có hóa đơn (HoaDon là null)
    private static final String UNINVOICED_HQL
            = "SELECT DISTINCT p FROM PhieuKhamBenh p" // Thêm DISTINCT
            + " JOIN FETCH p.benhNhan bn"
            + " JOIN FETCH p.bacSi nv"
            // Thêm dòng này để tải luôn danh sách chỉ định
            + " LEFT JOIN FETCH p.danhSachChiDinh"
            + " WHERE p.trangThai = 'CHUA_HOAN_THANH' AND NOT EXISTS ("
            + "SELECT 1 FROM HoaDon h WHERE h.phieuKhamBenh = p)";

    public List<PhieuKhamBenh> findUninvoiced() {
        try ( Session session = sessionFactory.openSession()) {
            return session.createQuery(UNINVOICED_HQL, PhieuKhamBenh.class).list();
        }
    }

    public List<PhieuKhamBenh> findUninvoicedByKeyword(String keyword) {
        String hql = UNINVOICED_HQL
                + " AND (p.maPhieuKham LIKE :keyword "
                + "OR bn.hoTen LIKE :keyword "
                + "OR nv.hoTen LIKE :keyword)";

        try ( Session session = sessionFactory.openSession()) {
            Query<PhieuKhamBenh> query = session.createQuery(hql, PhieuKhamBenh.class);
            query.setParameter("keyword", "%" + keyword + "%");
            return query.list();
        }
    }

    // Hàm này cần cho Service tạo hóa đơn
    public PhieuKhamBenh getById(int id, Session session) {
        return session.get(PhieuKhamBenh.class, id);
    }
}
