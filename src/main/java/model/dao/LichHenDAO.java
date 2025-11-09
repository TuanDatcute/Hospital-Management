package model.dao;

import model.Entity.LichHen;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import util.HibernateUtil;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author ADMIN (Đã GỘP: Giữ lại Phân trang & Xóa Mềm, và giữ nguyên khối code
 * DẠT)
 */
public class LichHenDAO {

    /**
     * Thêm một lịch hẹn mới vào CSDL. (Giữ logic throw RuntimeException từ File
     * 1)
     */
    public LichHen create(LichHen lichHen) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.save(lichHen);
            transaction.commit();
            // Sau khi commit, 'lichHen' sẽ được cập nhật ID và STT (từ trigger)
            return lichHen;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Cập nhật thông tin một lịch hẹn. (Giữ logic throw RuntimeException từ
     * File 1)
     */
    public boolean update(LichHen lichHen) {
        Transaction transaction = null;
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.update(lichHen);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Lấy thông tin lịch hẹn bằng ID (không tải BenhNhan, BacSi).
     *
     * @param id ID của lịch hẹn
     * @return Đối tượng LichHen hoặc null nếu không tìm thấy.
     */
    public LichHen getById(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            return session.get(LichHen.class, id);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Lấy thông tin lịch hẹn bằng ID (đã NÂNG CẤP Xóa Mềm từ File 2).
     */
    public LichHen getByIdWithRelations(int id) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Dùng HQL với JOIN FETCH để tải cả BenhNhan và BacSi
            String hql = "FROM LichHen lh "
                    + "LEFT JOIN FETCH lh.benhNhan bn " // Sửa: Dùng LEFT JOIN
                    + "LEFT JOIN FETCH lh.bacSi bs " // Sửa: Dùng LEFT JOIN
                    + "LEFT JOIN FETCH bn.taiKhoan bntk "
                    + "LEFT JOIN FETCH bs.taiKhoan bstk "
                    + "WHERE lh.id = :id "
                    + "AND (bn.trangThai = 'HOAT_DONG') " // Lọc BN Xóa Mềm
                    + "AND (bstk.trangThai = 'HOAT_DONG')"; // Lọc BS Xóa Mềm
            Query<LichHen> query = session.createQuery(hql, LichHen.class);
            query.setParameter("id", id);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * NÂNG CẤP (PHÂN TRANG): Lấy tất cả lịch hẹn (từ File 2)
     */
    public List<LichHen> getAllWithRelations(int page, int pageSize) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT DISTINCT lh FROM LichHen lh "
                    + "LEFT JOIN FETCH lh.benhNhan bn "
                    + "LEFT JOIN FETCH lh.bacSi bs "
                    + "LEFT JOIN FETCH bn.taiKhoan bntk "
                    + "LEFT JOIN FETCH bs.taiKhoan bstk "
                    + "WHERE (bn.trangThai = 'HOAT_DONG') " // Lọc BN Xóa Mềm
                    + "AND (bstk.trangThai = 'HOAT_DONG') " // Lọc BS Xóa Mềm
                    + "ORDER BY lh.thoiGianHen DESC";
            Query<LichHen> query = session.createQuery(hql, LichHen.class);

            int offset = (page - 1) * pageSize;
            query.setFirstResult(offset);
            query.setMaxResults(pageSize);

            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<LichHen> getAllWithRelations() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM LichHen lh "
                    + "JOIN FETCH lh.benhNhan "
                    + "JOIN FETCH lh.bacSi";
            Query<LichHen> query = session.createQuery(hql, LichHen.class);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * HÀM MỚI (PHÂN TRANG): Đếm tổng số Lịch hẹn (đã lọc) (từ File 2)
     */
    public long getTotalLichHenCount() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "SELECT count(DISTINCT lh.id) FROM LichHen lh "
                    + "JOIN lh.benhNhan bn " // Dùng JOIN
                    + "JOIN lh.bacSi bs "
                    + "JOIN bn.taiKhoan bntk "
                    + "JOIN bs.taiKhoan bstk "
                    + "WHERE (bn.trangThai = 'HOAT_DONG') " // Lọc BN Xóa Mềm
                    + "AND (bstk.trangThai = 'HOAT_DONG')"; // Lọc BS Xóa Mềm
            Query<Long> query = session.createQuery(hql, Long.class);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * NÂNG CẤP: Tìm tất cả lịch hẹn của một Bác Sĩ (từ File 2, đã lọc)
     */
    public List<LichHen> findByBacSiId(int bacSiId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM LichHen lh "
                    + "LEFT JOIN FETCH lh.benhNhan bn "
                    + "LEFT JOIN FETCH lh.bacSi bs "
                    + "LEFT JOIN FETCH bn.taiKhoan bntk "
                    + "LEFT JOIN FETCH bs.taiKhoan bstk "
                    + "WHERE bs.id = :bsId "
                    + "AND (bn.trangThai = 'HOAT_DONG') "
                    + "AND (bstk.trangThai = 'HOAT_DONG') "
                    + "ORDER BY lh.thoiGianHen DESC";
            Query<LichHen> query = session.createQuery(hql, LichHen.class);
            query.setParameter("bsId", bacSiId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    /**
     * GỘP: Giữ lại hàm tìm kiếm (từ File 1) và THÊM logic Xóa Mềm (từ File 2).
     */
    public List<LichHen> findByBenhNhanId(int benhNhanId, String keyword) {
        String hql = "SELECT lh FROM LichHen lh "
                + "LEFT JOIN FETCH lh.benhNhan bn " // Sửa: LEFT JOIN
                + "LEFT JOIN FETCH lh.bacSi bs " // Sửa: LEFT JOIN
                + "LEFT JOIN FETCH bn.taiKhoan bntk " // Thêm
                + "LEFT JOIN FETCH bs.taiKhoan bstk " // Thêm
                + "WHERE bn.id = :benhNhanId "
                + "AND (bn.trangThai = 'HOAT_DONG') " // Thêm
                + "AND (bstk.trangThai = 'HOAT_DONG') "; // Thêm

        if (keyword != null && !keyword.trim().isEmpty()) {
            // Tìm theo tên bác sĩ (bs.hoTen), lý do khám, trạng thái
            hql += " AND (LOWER(lh.lyDoKham) LIKE LOWER(:keyword) "
                    + " OR LOWER(bs.hoTen) LIKE LOWER(:keyword) "
                    + " OR LOWER(lh.trangThai) LIKE LOWER(:keyword)) ";
        }
        hql += " ORDER BY lh.thoiGianHen DESC";

        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<LichHen> query = session.createQuery(hql, LichHen.class);
            query.setParameter("benhNhanId", benhNhanId);
            if (keyword != null && !keyword.trim().isEmpty()) {
                query.setParameter("keyword", "%" + keyword.trim().toLowerCase() + "%");
            }
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * HÀM GIỮ LẠI (từ File 1): Lấy lịch hẹn (an toàn)
     */
    public LichHen getByIdAndBenhNhanId(int lichHenId, int benhNhanId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // Không cần JOIN FETCH ở đây vì Service chỉ cần kiểm tra
            String hql = "FROM LichHen lh "
                    + "WHERE lh.id = :lichHenId AND lh.benhNhan.id = :benhNhanId";
            Query<LichHen> query = session.createQuery(hql, LichHen.class);
            query.setParameter("lichHenId", lichHenId);
            query.setParameter("benhNhanId", benhNhanId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * HÀM GIỮ LẠI (từ File 1) & NÂNG CẤP (thêm lọc Xóa Mềm): Tìm lịch hẹn của
     * bác sĩ trong một ngày cụ thể (để lấy STT).
     */
    public List<LichHen> findByBacSiIdAndDate(int bacSiId, LocalDate date) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // HQL dùng hàm date() để chỉ so sánh phần ngày của OffsetDateTime
            String hql = "FROM LichHen lh "
                    + "LEFT JOIN FETCH lh.benhNhan bn "
                    + "LEFT JOIN FETCH lh.bacSi bs "
                    + "LEFT JOIN FETCH bn.taiKhoan bntk " // Thêm
                    + "LEFT JOIN FETCH bs.taiKhoan bstk " // Thêm
                    + "WHERE bs.id = :bsId "
                    + "AND date(lh.thoiGianHen) = :date "
                    + "AND (bn.trangThai = 'HOAT_DONG') " // Thêm
                    + "AND (bstk.trangThai = 'HOAT_DONG') " // Thêm
                    + "ORDER BY lh.stt ASC";
            Query<LichHen> query = session.createQuery(hql, LichHen.class);
            query.setParameter("bsId", bacSiId);
            query.setParameter("date", date);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    public List<LichHen> searchLichHenPaginated(String keyword, int page, int pageSize) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            
            // Dùng SELECT DISTINCT để tránh trùng lặp khi JOIN
            // Dùng LEFT JOIN FETCH để tải luôn dữ liệu, tránh lỗi N+1
            String hql = "SELECT DISTINCT lh FROM LichHen lh "
                    + "LEFT JOIN FETCH lh.benhNhan bn "
                    + "LEFT JOIN FETCH lh.bacSi bs " // 'bacSi' là tên trường NhanVien trong LichHen
                    + "WHERE (bn.hoTen LIKE :keyword "
                    + "OR bs.hoTen LIKE :keyword "
                    + "OR lh.lyDoKham LIKE :keyword "
                    + "OR lh.trangThai LIKE :keyword) "
                    + "ORDER BY lh.thoiGianHen DESC"; // Sắp xếp theo thời gian hẹn mới nhất

            Query<LichHen> query = session.createQuery(hql, LichHen.class);
            query.setParameter("keyword", "%" + keyword + "%");

            // Logic phân trang
            int offset = (page - 1) * pageSize;
            query.setFirstResult(offset);
            query.setMaxResults(pageSize);

            return query.list();
            
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList(); // Trả về danh sách rỗng nếu có lỗi
        }
    }

    /**
     * Đếm tổng số kết quả Lịch hẹn khớp với từ khóa tìm kiếm.
     *
     * @param keyword Từ khóa tìm kiếm
     * @return Tổng số lịch hẹn
     */
    public long getLichHenSearchCount(String keyword) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            
            // Câu HQL này phải JOIN và WHERE giống hệt như hàm search
            String hql = "SELECT count(DISTINCT lh.id) FROM LichHen lh "
                    + "LEFT JOIN lh.benhNhan bn "
                    + "LEFT JOIN lh.bacSi bs "
                    + "WHERE (bn.hoTen LIKE :keyword "
                    + "OR bs.hoTen LIKE :keyword "
                    + "OR lh.lyDoKham LIKE :keyword "
                    + "OR lh.trangThai LIKE :keyword)";

            Query<Long> query = session.createQuery(hql, Long.class);
            query.setParameter("keyword", "%" + keyword + "%");
            
            return query.uniqueResult();
            
        } catch (Exception e) {
            e.printStackTrace();
            return 0; // Trả về 0 nếu có lỗi
        }
    }


    //============================================DẠT===================================================
    /**
     * Đếm số lượng lịch hẹn đã có trong một ngày cụ thể CHO MỘT BÁC SĨ CỤ THỂ.
     *
     * @param date Ngày cần đếm.
     * @param bacSiId ID của bác sĩ cần lọc.
     * @return Tổng số lịch hẹn của bác sĩ đó trong ngày.
     */
    public long countAppointmentsByDateAndDoctor(LocalDate date, int bacSiId) {
        // Xác định thời điểm bắt đầu và kết thúc của ngày (giữ nguyên)
        ZoneOffset zoneOffset = ZoneOffset.of("+07:00");
        OffsetDateTime startOfDay = date.atStartOfDay().atOffset(zoneOffset);
        OffsetDateTime endOfDay = date.plusDays(1).atStartOfDay().atOffset(zoneOffset);

        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            // ✨ THÊM ĐIỀU KIỆN 'lh.bacSi.id = :bacSiId' VÀO TRUY VẤN ✨
            Query<Long> query = session.createQuery(
                    "SELECT count(lh.id) FROM LichHen lh "
                    + "WHERE lh.thoiGianHen >= :start AND lh.thoiGianHen < :end "
                    + "AND lh.bacSi.id = :bacSiId",
                    Long.class
            );
            query.setParameter("start", startOfDay);
            query.setParameter("end", endOfDay);
            query.setParameter("bacSiId", bacSiId); // Gán tham số bacSiId

            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return 0L;
        }
    }

    /**
     * Lấy tất cả các lịch hẹn CHƯA HOÀN THÀNH.
     *
     * @return Danh sách các LichHen.
     */
    public List<LichHen> getAllPendingAppointments() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {

            // ✨ SỬ DỤNG JOIN FETCH ĐỂ TẢI SẴN DỮ LIỆU LIÊN QUAN ✨
            Query<LichHen> query = session.createQuery(
                    // Sử dụng 'SELECT DISTINCT lh' để đảm bảo kết quả là duy nhất
                    "SELECT DISTINCT lh FROM LichHen lh "
                    + "JOIN FETCH lh.benhNhan "
                    + "JOIN FETCH lh.bacSi "
                    + "WHERE lh.trangThai NOT IN ('HOAN_THANH', 'DA_DEN_KHAM', 'DA_HUY')",
                    LichHen.class
            );

            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Lấy tất cả các lịch hẹn CHƯA HOÀN THÀNH của MỘT BÁC SĨ CỤ THỂ. Tải sẵn
     * thông tin BenhNhan và BacSi.
     *
     * @param bacSiId ID của bác sĩ cần tìm lịch hẹn.
     * @return Danh sách các LichHen của bác sĩ đó.
     */
    public List<LichHen> getPendingAppointmentsForDoctor(int bacSiId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {

            Query<LichHen> query = session.createQuery(
                    "SELECT DISTINCT lh FROM LichHen lh "
                    + "JOIN FETCH lh.benhNhan "
                    + "JOIN FETCH lh.bacSi "
                    + // ✨ THÊM ĐIỀU KIỆN LỌC THEO BÁC SĨ ✨
                    "WHERE lh.bacSi.id = :bacSiId "
                    + "AND lh.trangThai NOT IN ('HOAN_THANH', 'DA_DEN_KHAM', 'DA_HUY')",
                    LichHen.class
            );

            query.setParameter("bacSiId", bacSiId);

            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Lấy tất cả lịch hẹn, tải sẵn thông tin bệnh nhân và bác sĩ. Sắp xếp theo
     * STT và thời gian hẹn.
     */
    public List<LichHen> getAll() {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<LichHen> query = session.createQuery(
                    "SELECT lh FROM LichHen lh "
                    + "LEFT JOIN FETCH lh.benhNhan "
                    + "LEFT JOIN FETCH lh.bacSi "
                    + "ORDER BY "
                    + "  CASE lh.trangThai "
                    + "    WHEN 'CHO_XAC_NHAN' THEN 1 "
                    + "    WHEN 'DA_XAC_NHAN' THEN 2 "
                    + "    ELSE 3 "
                    + "  END ASC, "
                    + "  lh.thoiGianHen DESC, "
                    + "  lh.stt ASC",
                    LichHen.class
            );
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     * Tìm kiếm lịch hẹn theo từ khóa (tên bệnh nhân, tên bác sĩ, lý do, trạng
     * thái).
     */
    public List<LichHen> search(String keyword) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query<LichHen> query = session.createQuery(
                    "SELECT DISTINCT lh FROM LichHen lh "
                    + "JOIN FETCH lh.benhNhan bn "
                    + "JOIN FETCH lh.bacSi bs "
                    + "WHERE bn.hoTen LIKE :keyword "
                    + "OR bs.hoTen LIKE :keyword "
                    + "OR lh.lyDoKham LIKE :keyword "
                    + "OR lh.trangThai LIKE :keyword "
                    + "ORDER BY lh.thoiGianHen DESC",
                    LichHen.class
            );
            query.setParameter("keyword", "%" + keyword + "%");
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    /**
     *  Lấy các lịch hẹn CHƯA HOÀN THÀNH, lọc theo NGÀY HẸN và
     * BÁC SĨ.
     *
     * @param date Ngày hẹn (ví dụ: 2025-11-09).
     * @param bacSiId ID của bác sĩ cần lọc.
     * @return Danh sách LichHen khớp với cả hai điều kiện.
     */
    public List<LichHen> getPendingAppointmentsByDateAndDoctor(LocalDate date, int bacSiId) {

        // 1. Logic tính toán ngày (từ hàm cũ)
        ZoneOffset zoneOffset = ZoneOffset.of("+07:00");
        OffsetDateTime startOfDay = date.atStartOfDay().atOffset(zoneOffset);
        OffsetDateTime endOfDay = date.plusDays(1).atStartOfDay().atOffset(zoneOffset);

        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {

            // 2. Logic HQL (đã gộp)
            Query<LichHen> query = session.createQuery(
                    "SELECT DISTINCT lh FROM LichHen lh "
                    + "JOIN FETCH lh.benhNhan "
                    + "JOIN FETCH lh.bacSi "
                    // ✨ Gộp 2 điều kiện WHERE lại ✨
                    + "WHERE lh.bacSi.id = :bacSiId " // Lọc theo bác sĩ
                    + "AND lh.thoiGianHen >= :start AND lh.thoiGianHen < :end " // Lọc theo ngày
                    + "AND lh.trangThai NOT IN ('HOAN_THANH', 'DA_DEN_KHAM', 'DA_HUY')", // Lọc trạng thái
                    LichHen.class
            );

            // 3. Set cả 3 tham số
            query.setParameter("bacSiId", bacSiId);
            query.setParameter("start", startOfDay);
            query.setParameter("end", endOfDay);

            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public LichHen getByIdAndBenhNhanIdWithRelations(int lichHenId, int benhNhanId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM LichHen lh "
                    + "JOIN FETCH lh.benhNhan "
                    + "JOIN FETCH lh.bacSi "
                    + "WHERE lh.id = :lichHenId AND lh.benhNhan.id = :benhNhanId";
            Query<LichHen> query = session.createQuery(hql, LichHen.class);
            query.setParameter("lichHenId", lichHenId);
            query.setParameter("benhNhanId", benhNhanId);
            return query.uniqueResult();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Tìm tất cả lịch hẹn của một Bệnh Nhân cụ thể.
     *
     * @param benhNhanId ID của bệnh nhân
     * @return Danh sách LichHen (đã tải relations).
     */
    public List<LichHen> findByBenhNhanId(int benhNhanId) {
        try ( Session session = HibernateUtil.getSessionFactory().openSession()) {
            String hql = "FROM LichHen lh "
                    + "JOIN FETCH lh.benhNhan "
                    + "JOIN FETCH lh.bacSi "
                    + "WHERE lh.benhNhan.id = :bnId "
                    + "ORDER BY lh.thoiGianHen DESC";
            Query<LichHen> query = session.createQuery(hql, LichHen.class);
            query.setParameter("bnId", benhNhanId);
            return query.list();
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

}
