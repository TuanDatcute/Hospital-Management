// path: com/service/ThongBaoService.java
package service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime; // Hoặc java.util.Date
import java.util.ArrayList;
import java.util.Collections; // Import Collections
import model.dao.TaiKhoanDAO;
import model.dao.ThongBaoDAO;
import model.dto.ThongBaoDTO;
import model.Entity.TaiKhoan;
import model.Entity.ThongBao;
import util.HibernateUtil; // Cần cho transaction
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;


import java.util.List;
import java.util.stream.Collectors; // Sử dụng Stream API cho ngắn gọn

public class ThongBaoService {

    private final ThongBaoDAO thongBaoDAO;
    private final TaiKhoanDAO taiKhoanDAO;
    private final SessionFactory sessionFactory; // Cần cho quản lý transaction

    // === Hằng số trạng thái ===
    private static final String TRANG_THAI_ACTIVE = "HOAT_DONG"; 
    private static final String TRANG_THAI_XOA = "NGUNG_HOAT_DONG";   
    // ========================

    public ThongBaoService() {
        this.thongBaoDAO = new ThongBaoDAO();
        this.taiKhoanDAO = new TaiKhoanDAO();
        this.sessionFactory = HibernateUtil.getSessionFactory(); // Khởi tạo SessionFactory
    }

    /**
     * HÀM MỚI: Tìm kiếm các thông báo đang hoạt động theo keyword.
     * @param keyword Từ khóa tìm kiếm (tiêu đề hoặc nội dung)
     * @return Danh sách ThongBaoDTO
     */
    public List<ThongBaoDTO> searchActiveNotifications(String keyword) {
        List<ThongBao> entities;
        if (keyword == null || keyword.trim().isEmpty()) {
            // Giả sử DAO có hàm này (chỉ lấy ACTIVE)
            entities = thongBaoDAO.findAllActive(); 
        } else {
            // Giả sử DAO có hàm này (tìm theo keyword và chỉ lấy ACTIVE)
            entities = thongBaoDAO.findActiveByKeyword(keyword); 
        }

        // Chuyển đổi List<Entity> sang List<DTO>
        return entities.stream()
                       .map(this::convertToDTO)
                       .collect(Collectors.toList());
    }

    /**
     * HÀM MỚI: Lấy thông báo theo ID (trả về DTO).
     * Dùng cho form cập nhật.
     */
    public ThongBaoDTO getNotificationById(int notificationId) {
        // Giả sử DAO có hàm getThongBaoById(int id)
        ThongBao entity = thongBaoDAO.getThongBaoById(notificationId); 
        return convertToDTO(entity);
    }


    /**
     * HÀM MỚI: Tạo thông báo cho nhiều người dùng (quản lý transaction).
     * @param tieuDe Tiêu đề thông báo
     * @param noiDung Nội dung thông báo
     * @param targetType Loại đối tượng ('ALL', 'ROLE', 'USER')
     * @param targetValue Giá trị đối tượng ('ALL', tên vai trò, ID tài khoản)
     * @throws Exception Nếu có lỗi xảy ra
     */
    public void createNotification(String tieuDe, String noiDung, String targetType, String targetValue) throws Exception {
        List<Integer> targetAccountIds = new ArrayList<>();

        // 1. Xác định danh sách ID tài khoản nhận thông báo
        switch (targetType) {
            case "ALL":
                // Giả sử TaiKhoanDAO có hàm này
                targetAccountIds = taiKhoanDAO.getAllActiveTaiKhoanIds(); 
                break;
            case "ROLE":
                // Giả sử TaiKhoanDAO có hàm này
                targetAccountIds = taiKhoanDAO.getActiveTaiKhoanIdsByRole(targetValue); 
                break;
            case "USER":
                try {
                    int accountId = Integer.parseInt(targetValue);
                    // Kiểm tra xem tài khoản có tồn tại và active không (tùy chọn)
                    TaiKhoan tk = taiKhoanDAO.getById(accountId); // Giả sử có hàm này
                    if (tk != null /* && tk is active */) {
                       targetAccountIds.add(accountId);
                    } else {
                        throw new IllegalArgumentException("Tài khoản với ID " + accountId + " không tồn tại hoặc không hoạt động.");
                    }
                } catch (NumberFormatException e) {
                     throw new IllegalArgumentException("ID tài khoản không hợp lệ: " + targetValue);
                }
                break;
            default:
                throw new IllegalArgumentException("Loại đối tượng không hợp lệ: " + targetType);
        }

        if (targetAccountIds.isEmpty()) {
            // Không ném lỗi mà chỉ thông báo (hoặc ghi log)
            System.out.println("Không tìm thấy tài khoản hợp lệ để gửi thông báo.");
            return; // Không có gì để làm
        }

        // 2. Tạo và lưu thông báo trong một transaction
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            for (Integer accountId : targetAccountIds) {
                ThongBao entity = new ThongBao();
                entity.setTieuDe(tieuDe);
                entity.setNoiDung(noiDung);
                entity.setDaDoc(false); // Mặc định là chưa đọc
                entity.setTrangThai(TRANG_THAI_ACTIVE); // Mặc định là active
                entity.setThoiGianGui(LocalDateTime.now()); // Hoặc new Date()

                // Tạo tham chiếu đến TaiKhoan (chỉ cần ID)
                TaiKhoan taiKhoanRef = session.load(TaiKhoan.class, accountId);
                entity.setTaiKhoan(taiKhoanRef);

                // Giả sử DAO có hàm saveInSession(entity, session)
                thongBaoDAO.saveInSession(entity, session); 
            }

            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace(); // Nên dùng logger
            // Ném lại lỗi để Controller biết
            throw new Exception("Lỗi hệ thống khi tạo thông báo hàng loạt: " + e.getMessage(), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

     /**
     * HÀM MỚI: Cập nhật Tiêu đề và Nội dung của thông báo.
     * @param dto DTO chứa ID, Tiêu đề mới, Nội dung mới
     * @throws Exception Nếu có lỗi
     */
    public void updateNotification(ThongBaoDTO dto) throws Exception {
        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();

            // Lấy entity hiện tại (nên dùng hàm getById trong transaction)
            ThongBao existingNotification = thongBaoDAO.getThongBaoById(dto.getId(), session); // Sửa DAO để nhận session

            if (existingNotification == null) {
                throw new IllegalArgumentException("Thông báo với ID " + dto.getId() + " không tồn tại.");
            }
            
            // Chỉ cho sửa thông báo đang active
            if (!TRANG_THAI_ACTIVE.equals(existingNotification.getTrangThai())) {
                 throw new IllegalStateException("Không thể sửa thông báo đã bị xóa.");
            }

            // Cập nhật các trường cho phép sửa
            existingNotification.setTieuDe(dto.getTieuDe());
            existingNotification.setNoiDung(dto.getNoiDung());
            // Không cho sửa người nhận, trạng thái đọc, thời gian gửi...

            // Gọi DAO để update (trong transaction)
            thongBaoDAO.updateInSession(existingNotification, session); // Sửa DAO để nhận session

            transaction.commit();

        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
             e.printStackTrace(); // Nên dùng logger
            // Ném lại lỗi
            throw e;
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }


    /**
     * HÀM MỚI: Xóa mềm thông báo.
     * @param notificationId ID của thông báo cần xóa
     * @throws Exception Nếu có lỗi
     */
    public void softDeleteNotification(int notificationId) throws Exception {
        // Gọi DAO để cập nhật trạng thái
        // Giả sử DAO `updateTrangThai` tự quản lý transaction của nó
        thongBaoDAO.updateTrangThai(notificationId, TRANG_THAI_XOA); 
    }


    // --- Các hàm cũ có thể cần sửa lại ---

    // Đánh dấu đã đọc (Giữ nguyên logic, nhưng DAO nên được sửa)
    public boolean markAsRead(int thongBaoId) {
        try {
            // Service không nên tự mở session/transaction cho các thao tác đơn lẻ
            // DAO nên cung cấp hàm markAsRead(id) tự quản lý transaction
            return thongBaoDAO.markAsRead(thongBaoId); // Giả sử DAO có hàm này
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy tất cả thông báo ACTIVE cho người dùng (Sửa lại)
    public List<ThongBaoDTO> getActiveNotificationsForUser(int taiKhoanId) {
        // Giả sử DAO có hàm getActiveByTaiKhoanId(id)
        List<ThongBao> entities = thongBaoDAO.getActiveByTaiKhoanId(taiKhoanId);
        return entities.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Lấy thông báo CHƯA ĐỌC và ACTIVE (Sửa lại)
    public List<ThongBaoDTO> getUnreadActiveNotificationsForUser(int taiKhoanId) {
        // Giả sử DAO có hàm getUnreadActiveByTaiKhoanId(id)
        List<ThongBao> entities = thongBaoDAO.getUnreadActiveByTaiKhoanId(taiKhoanId);
        return entities.stream().map(this::convertToDTO).collect(Collectors.toList());
    }


    // --- Phương thức chuyển đổi (Giữ nguyên) ---
    private ThongBaoDTO convertToDTO(ThongBao entity) {
        if (entity == null) return null;
        // Giả sử DTO của bạn có constructor phù hợp hoặc dùng setters
         ThongBaoDTO dto = new ThongBaoDTO();
         dto.setId(entity.getId());
         dto.setTieuDe(entity.getTieuDe());
         dto.setNoiDung(entity.getNoiDung());
         dto.setDaDoc(entity.isDaDoc());
         dto.setThoiGianGui(entity.getThoiGianGui()); // Giả sử DTO dùng Date/OffsetDateTime
         dto.setTrangThai(entity.getTrangThai());
         dto.setTaiKhoanId(entity.getTaiKhoan() != null ? entity.getTaiKhoan().getId() : 0);
         return dto;
    }

    // Hàm này không còn cần thiết nếu dùng hàm createNotification mới
    /*
    private ThongBao convertToEntity(ThongBaoDTO dto) { ... }
    */
}