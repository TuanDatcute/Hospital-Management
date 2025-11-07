// path: com/service/ThongBaoService.java
package service;

import java.time.LocalDateTime; // Sử dụng LocalDateTime
import java.util.ArrayList;
import java.util.Collections;
import model.dao.TaiKhoanDAO;
import model.dao.ThongBaoDAO;
import model.dto.ThongBaoDTO;
import model.dto.GroupedThongBaoDTO; // DTO cho chức năng gộp nhóm
import model.Entity.TaiKhoan;
import model.Entity.ThongBao;
import util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ThongBaoService {

    private final ThongBaoDAO thongBaoDAO;
    private final TaiKhoanDAO taiKhoanDAO;
    private final SessionFactory sessionFactory;

    // Đảm bảo các hằng số này khớp với CSDL của bạn
    private static final String TRANG_THAI_ACTIVE = "HOAT_DONG";
    private static final String TRANG_THAI_XOA = "NGUNG_HOAT_DONG";

    public ThongBaoService() {
        this.thongBaoDAO = new ThongBaoDAO();
        this.taiKhoanDAO = new TaiKhoanDAO();
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    // ===========================================
    // === CÁC HÀM CHO ADMIN (TRANG QUẢN LÝ) ===
    // ===========================================
    /**
     * CẬP NHẬT: Sửa lại định dạng hiển thị tên tài khoản cho khớp với dropdown
     */
    public List<GroupedThongBaoDTO> searchGroupedNotifications(String keyword) {
        List<GroupedThongBaoDTO> groupedList;
        if (keyword == null || keyword.trim().isEmpty()) {
            groupedList = thongBaoDAO.findAllGrouped();
        } else {
            groupedList = thongBaoDAO.findGroupedByKeyword(keyword);
        }
        List<Integer> userIdsToLookup = new ArrayList<>();
        for (GroupedThongBaoDTO dto : groupedList) {
            String displayName = dto.getNguoiNhanDisplay();
            if (displayName != null && displayName.startsWith("[USER_ID:")) {
                try {
                    int userId = Integer.parseInt(displayName.substring(9, displayName.length() - 1));
                    userIdsToLookup.add(userId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (!userIdsToLookup.isEmpty()) {
            Map<Integer, String> userIdToNameMap = taiKhoanDAO.getTenDangNhapByIds(userIdsToLookup);
            for (GroupedThongBaoDTO dto : groupedList) {
                String displayName = dto.getNguoiNhanDisplay();
                if (displayName != null && displayName.startsWith("[USER_ID:")) {
                    try {
                        int userId = Integer.parseInt(displayName.substring(9, displayName.length() - 1));
                        String tenDangNhap = userIdToNameMap.get(userId);
                        if (tenDangNhap != null) {
                            dto.setNguoiNhanDisplay(tenDangNhap + " (ID: " + userId + ")");
                        } else {
                            dto.setNguoiNhanDisplay("Tài khoản đã xóa (ID: " + userId + ")");
                        }
                    } catch (Exception e) {
                        /* Bỏ qua */ }
                }
            }
        }
        return groupedList;
    }

    /**
     * CẬP NHẬT: Thêm logic chèn "metadata prefix" vào nội dung
     */
    public void createNotification(String tieuDe, String noiDung, String targetType, String targetValue) throws Exception {
        // ... (Code tạo thông báo với [GROUP:...] prefix giữ nguyên)
        List<Integer> targetAccountIds = new ArrayList<>();
        String metadataPrefix;
        switch (targetType) {
            case "ALL":
                metadataPrefix = "[GROUP:ROLE=ALL]";
                targetAccountIds = taiKhoanDAO.getAllActiveTaiKhoanIds();
                break;
            case "ROLE":
                metadataPrefix = "[GROUP:ROLE=" + targetValue.trim().toUpperCase() + "]";
                targetAccountIds = taiKhoanDAO.getActiveTaiKhoanIdsByRole(targetValue);
                break;
            case "USER":
                metadataPrefix = "[GROUP:USER=" + targetValue.trim() + "]";
                try {
                    int accountId = Integer.parseInt(targetValue);
                    TaiKhoan tk = taiKhoanDAO.getById(accountId);
                    if (tk != null && TRANG_THAI_ACTIVE.equals(tk.getTrangThai())) {
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
            System.out.println("Không tìm thấy tài khoản hợp lệ để gửi thông báo.");
            return;
        }

        String finalNoiDung = metadataPrefix + noiDung.trim();
        LocalDateTime sharedTimestamp = LocalDateTime.now();

        Session session = null;
        Transaction transaction = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            for (Integer accountId : targetAccountIds) {
                ThongBao entity = new ThongBao();
                entity.setTieuDe(tieuDe);
                entity.setNoiDung(finalNoiDung);
                entity.setDaDoc(false);
                entity.setTrangThai(TRANG_THAI_ACTIVE);
                entity.setThoiGianGui(sharedTimestamp);
                TaiKhoan taiKhoanRef = session.load(TaiKhoan.class, accountId);
                entity.setTaiKhoan(taiKhoanRef);
                thongBaoDAO.saveInSession(entity, session);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
            throw new Exception("Lỗi hệ thống khi tạo thông báo hàng loạt: " + e.getMessage(), e);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }

    // ==========================================================
    // === CÁC HÀM MỚI CHO NGƯỜI DÙNG (CLIENT-SIDE) ===
    // ==========================================================
    /**
     * HÀM MỚI: Tìm kiếm thông báo (ACTIVE) của 1 người dùng (An toàn - chỉ tìm
     * của taiKhoanId)
     */
    public List<ThongBaoDTO> searchActiveNotificationsForUser(int taiKhoanId, String keyword) {
        List<ThongBao> entities;
        if (keyword == null || keyword.trim().isEmpty()) {
            // Gọi hàm getActiveByTaiKhoanId (DAO đã có)
            entities = thongBaoDAO.getActiveByTaiKhoanId(taiKhoanId);
        } else {
            // Gọi hàm tìm kiếm mới (findActiveByKeywordForUser)
            entities = thongBaoDAO.findActiveByKeywordForUser(taiKhoanId, keyword);
        }
        // Dùng hàm convertToDTO (đã có) để chuyển đổi và tách tiền tố
        return entities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * HÀM MỚI: Đánh dấu đã đọc (an toàn) Yêu cầu cả thongBaoId và taiKhoanId
     */
    public boolean markAsReadForUser(int thongBaoId, int taiKhoanId) {
        try {
            // Gọi hàm DAO an toàn (kiểm tra cả 2 ID)
            return thongBaoDAO.markAsReadForUser(thongBaoId, taiKhoanId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HÀM MỚI: Xóa mềm (an toàn) Yêu cầu cả thongBaoId và taiKhoanId
     */
    public boolean softDeleteForUser(int thongBaoId, int taiKhoanId) {
        try {
            // Gọi hàm DAO an toàn (kiểm tra cả 2 ID)
            return thongBaoDAO.softDeleteForUser(thongBaoId, taiKhoanId);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy tất cả thông báo ACTIVE (không lọc keyword)
    public List<ThongBaoDTO> getActiveNotificationsForUser(int taiKhoanId) {
        List<ThongBao> entities = thongBaoDAO.getActiveByTaiKhoanId(taiKhoanId);
        return entities.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Lấy thông báo CHƯA ĐỌC và ACTIVE
    public List<ThongBaoDTO> getUnreadActiveNotificationsForUser(int taiKhoanId) {
        List<ThongBao> entities = thongBaoDAO.getUnreadActiveByTaiKhoanId(taiKhoanId);
        return entities.stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    // Hàm search của Admin (tìm của mọi người)
    public List<ThongBaoDTO> searchActiveNotifications(String keyword) {
        List<ThongBao> entities;
        if (keyword == null || keyword.trim().isEmpty()) {
            entities = thongBaoDAO.findAllActive();
        } else {
            entities = thongBaoDAO.findActiveByKeyword(keyword);
        }
        return entities.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    // --- Phương thức chuyển đổi ---
    /**
     * Chuyển Entity ThongBao sang ThongBaoDTO (phẳng) (Hàm này sẽ tự động loại
     * bỏ tiền tố metadata)
     */
    private ThongBaoDTO convertToDTO(ThongBao entity) {
        if (entity == null) {
            return null;
        }
        ThongBaoDTO dto = new ThongBaoDTO();
        dto.setId(entity.getId());
        dto.setTieuDe(entity.getTieuDe());

        // === TÁCH TIỀN TỐ KHỎI NỘI DUNG ===
        String rawNoiDung = entity.getNoiDung();
        if (rawNoiDung != null && rawNoiDung.startsWith("[GROUP:")) {
            int endPrefix = rawNoiDung.indexOf("]");
            if (endPrefix != -1) {
                dto.setNoiDung(rawNoiDung.substring(endPrefix + 1)); // Lấy nội dung gốc
            } else {
                dto.setNoiDung(rawNoiDung); // (Lỗi tiền tố)
            }
        } else {
            dto.setNoiDung(rawNoiDung); // (Dữ liệu cũ hoặc không có tiền tố)
        }
        // =================================

        dto.setDaDoc(entity.isDaDoc());
        dto.setThoiGianGui(entity.getThoiGianGui()); // Dùng LocalDateTime
        dto.setTrangThai(entity.getTrangThai());
        dto.setTaiKhoanId(entity.getTaiKhoan() != null ? entity.getTaiKhoan().getId() : 0);
        return dto;
    }
}
