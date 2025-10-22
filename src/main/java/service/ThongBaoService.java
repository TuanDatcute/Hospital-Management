// path: com/service/ThongBaoService.java
package service;

import java.util.ArrayList;
import model.dao.TaiKhoanDAO;
import model.dao.ThongBaoDAO;
import model.dto.ThongBaoDTO;
import model.Entity.TaiKhoan;
import model.Entity.ThongBao;

import java.util.List;
import java.util.stream.Collectors;

public class ThongBaoService {

    private ThongBaoDAO thongBaoDAO;
    private TaiKhoanDAO taiKhoanDAO;

    public ThongBaoService() {
        this.thongBaoDAO = new ThongBaoDAO();
        this.taiKhoanDAO = new TaiKhoanDAO();
    }

    // Tạo một thông báo mới
    public boolean createThongBao(ThongBaoDTO dto) {
        try {
            // Bước 1: Chuyển DTO sang Entity (Sử dụng hàm helper mới)
            ThongBao thongBao = convertToEntity(dto);
            if (thongBao == null) {
                // Lỗi xảy ra nếu không tìm thấy TaiKhoan
                return false;
            }

            // Bước 2: Lưu vào CSDL
            thongBaoDAO.addThongBao(thongBao);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Đánh dấu một thông báo là đã đọc
    public boolean markAsRead(long thongBaoId) {
        try {
            ThongBao thongBao = thongBaoDAO.getThongBaoById(thongBaoId);
            if (thongBao != null && !thongBao.isDaDoc()) {
                thongBao.setDaDoc(true);
                thongBaoDAO.updateThongBao(thongBao);
                return true;
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // Lấy tất cả thông báo cho một người dùng (trả về DTO)

    public List<ThongBaoDTO> getTatCaThongBao(long taiKhoanId) {
        // 1. Lấy danh sách Entity từ DAO
        List<ThongBao> entities = thongBaoDAO.getThongBaoByTaiKhoanId(taiKhoanId);

        // 2. Khởi tạo một danh sách DTO mới (rỗng)
        List<ThongBaoDTO> dtos = new ArrayList<>();

        // 3. Lặp qua từng 'entity' trong danh sách 'entities'
        for (ThongBao entity : entities) {

            // 4. (Thực hành tốt) Kiểm tra xem entity có null không
            if (entity != null) {

                // 5. Chuyển đổi entity đó sang DTO và thêm vào danh sách 'dtos'
                dtos.add(this.convertToDTO(entity));
            }
        }

        // 6. Trả về danh sách DTO đã được chuyển đổi
        return dtos;
    }

    // Lấy các thông báo CHƯA ĐỌC cho người dùng
    public List<ThongBaoDTO> getThongBaoChuaDoc(long taiKhoanId) {
        // 1. Lấy danh sách Entity từ DAO
        List<ThongBao> entities = thongBaoDAO.getThongBaoChuaDocByTaiKhoanId(taiKhoanId);

        // 2. Khởi tạo một danh sách DTO rỗng
        List<ThongBaoDTO> dtos = new ArrayList<>();

        // 3. Dùng vòng lặp for-each để duyệt qua danh sách entities
        for (ThongBao entity : entities) {

            // 4. (Nên có) Kiểm tra xem entity có bị null hay không
            if (entity != null) {

                // 5. Chuyển đổi Entity thành DTO và thêm vào danh sách dtos
                dtos.add(this.convertToDTO(entity));
            }
        }

        // 6. Trả về danh sách DTO đã được chuyển đổi
        return dtos;
    }

    // --- Phương thức chuyển đổi (Helper Methods) ---
    // Chuyển Entity sang DTO
    private ThongBaoDTO convertToDTO(ThongBao entity) {
        if (entity == null) {
            return null;
        }

        return new ThongBaoDTO(
                entity.getId(),
                entity.getTieuDe(),
                entity.getNoiDung(),
                entity.isDaDoc(),
                entity.getThoiGianGui(),
                entity.getTaiKhoan() != null ? entity.getTaiKhoan().getId() : null // Kiểm tra null
        );
    }

    // *** HÀM MỚI: Chuyển DTO sang Entity ***
    private ThongBao convertToEntity(ThongBaoDTO dto) {
        if (dto == null) {
            return null;
        }

        ThongBao entity = new ThongBao();

        // Gán các giá trị đơn giản
        entity.setId(dto.getId()); // Quan trọng nếu dùng cho việc cập nhật
        entity.setTieuDe(dto.getTieuDe());
        entity.setNoiDung(dto.getNoiDung());
        entity.setDaDoc(dto.isDaDoc());
        // Không set thoiGianGui vì nó được @CreationTimestamp quản lý khi tạo mới

        // Xử lý khóa ngoại (quan trọng)
        if (dto.getTaiKhoanId() != null) {
            TaiKhoan taiKhoan = taiKhoanDAO.getTaiKhoanById(dto.getTaiKhoanId());
            if (taiKhoan == null) {
                // Nếu không tìm thấy tài khoản, không thể tạo entity hợp lệ
                System.err.println("Không tìm thấy tài khoản với ID: " + dto.getTaiKhoanId() + " khi chuyển đổi DTO sang Entity.");
                return null;
            }
            entity.setTaiKhoan(taiKhoan);
        } else {
            // Nếu logic của bạn yêu cầu phải có taiKhoanId
            System.err.println("taiKhoanId là null trong DTO.");
            return null;
        }

        return entity;
    }
}
