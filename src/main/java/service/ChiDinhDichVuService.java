package service;

import model.dao.ChiDinhDichVuDAO;
import model.dao.DichVuDAO;
import model.dao.PhieuKhamBenhDAO;
import model.dto.ChiDinhDichVuDTO;
import model.Entity.ChiDinhDichVu;
import model.Entity.DichVu;
import model.Entity.PhieuKhamBenh;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.hibernate.Session;
import util.HibernateUtil;

/**
 * Lớp Service cho Chỉ Định Dịch Vụ. Chứa toàn bộ logic nghiệp vụ (business
 * logic) liên quan đến việc chỉ định dịch vụ, cập nhật trạng thái, nhập kết quả
 * và truy vấn thông tin.
 */
public class ChiDinhDichVuService {

    private final ChiDinhDichVuDAO chiDinhDAO = new ChiDinhDichVuDAO();
    private final PhieuKhamBenhDAO phieuKhamDAO = new PhieuKhamBenhDAO();
    private final DichVuDAO dichVuDAO = new DichVuDAO();

    /**
     * NGHIỆP VỤ: Chỉ định một dịch vụ mới cho một lần khám.
     *
     * @param phieuKhamId ID của phiếu khám đang diễn ra.
     * @param dichVuId ID của dịch vụ được bác sĩ chọn.
     * @return DTO của chỉ định vừa được tạo.
     * @throws Exception nếu phiếu khám hoặc dịch vụ không tồn tại.
     */
    public ChiDinhDichVuDTO createServiceRequest(int phieuKhamId, int dichVuId) throws Exception {
        // 1. Kiểm tra sự tồn tại của các đối tượng liên quan
        PhieuKhamBenh phieuKham = phieuKhamDAO.getEncounterById(phieuKhamId); // Sửa lại tên hàm cho nhất quán
        if (phieuKham == null) {
            throw new Exception("Không tìm thấy phiếu khám với ID: " + phieuKhamId);
        }

        DichVu dichVu = dichVuDAO.getById(dichVuId);
        if (dichVu == null) {
            throw new Exception("Không tìm thấy dịch vụ với ID: " + dichVuId);
        }

        // 2. Tạo đối tượng Entity mới
        ChiDinhDichVu newChiDinh = new ChiDinhDichVu();
        newChiDinh.setPhieuKham(phieuKham);
        newChiDinh.setDichVu(dichVu);
        newChiDinh.setTrangThai("CHO_THUC_HIEN"); // Trạng thái mặc định khi mới tạo

        // 3. Gọi DAO để lưu vào CSDL
        ChiDinhDichVu savedChiDinh = chiDinhDAO.create(newChiDinh);

        // 4. Chuyển đổi sang DTO để trả về
        return toDTO(savedChiDinh);
    }

    /**
     * NGHIỆP VỤ: Cập nhật kết quả và/hoặc trạng thái cho một chỉ định dịch vụ.
     * Gộp chức năng của updateServiceRequestStatus và enterResult.
     *
     * @param chiDinhId ID của chỉ định cần cập nhật.
     * @param ketQua Kết quả mới của dịch vụ (có thể là chuỗi rỗng).
     * @param trangThai Trạng thái mới (ví dụ: 'HOAN_THANH').
     * @return DTO của chỉ định sau khi đã được cập nhật.
     * @throws Exception nếu không tìm thấy chỉ định.
     */
    public ChiDinhDichVuDTO updateResultAndStatus(int chiDinhId, String ketQua, String trangThai) throws Exception {
        // 1. Lấy bản ghi Entity gốc từ CSDL (Không trùng lặp code nữa)
        ChiDinhDichVu existingChiDinh = chiDinhDAO.getById(chiDinhId);
        if (existingChiDinh == null) {
            throw new Exception("Không tìm thấy chỉ định dịch vụ để cập nhật.");
        }

        // 2. Cập nhật các thuộc tính của Entity
        existingChiDinh.setKetQua(ketQua);
        existingChiDinh.setTrangThai(trangThai);

        // 3. Gọi DAO để lưu thay đổi vào CSDL
        chiDinhDAO.update(existingChiDinh);

        // 4. Trả về DTO đã được cập nhật
        return toDTO(existingChiDinh);
    }

    /**
     * NGHIỆP VỤ: Nhập kết quả cho một dịch vụ và tự động chuyển trạng thái sang
     * "HOAN_THANH".
     *
     * @param requestId ID của chỉ định cần nhập kết quả.
     * @param ketQua Nội dung kết quả.
     * @return DTO của chỉ định sau khi đã được cập nhật.
     * @throws Exception nếu không tìm thấy chỉ định.
     */
    public ChiDinhDichVuDTO enterResult(int requestId, String ketQua) throws Exception {
        ChiDinhDichVu chiDinh = chiDinhDAO.getById(requestId);
        if (chiDinh == null) {
            throw new Exception("Không tìm thấy chỉ định dịch vụ với ID: " + requestId);
        }

        chiDinh.setKetQua(ketQua);
        chiDinh.setTrangThai("HOAN_THANH"); // Logic nghiệp vụ: Nhập kết quả thì tự động hoàn thành

        chiDinhDAO.update(chiDinh);

        return toDTO(chiDinh);
    }

    /**
     * NGHIỆP VỤ: Lấy danh sách các dịch vụ đã được chỉ định trong một phiếu
     * khám.
     *
     * @param phieuKhamId ID của phiếu khám.
     * @return Một danh sách các ChiDinhDichVuDTO.
     */
    public List<ChiDinhDichVuDTO> listRequestsByEncounter(int phieuKhamId) {
        List<ChiDinhDichVu> entities = chiDinhDAO.findByPhieuKhamId(phieuKhamId);
        if (entities == null) {
            return Collections.emptyList(); // Trả về danh sách rỗng nếu có lỗi hoặc không có dữ liệu
        }
        // Chuyển đổi danh sách Entity sang danh sách DTO bằng Stream API
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }

    /**
     * NGHIỆP VỤ: Tìm một chỉ định dịch vụ cụ thể bằng ID của nó.
     *
     * @param chiDinhDichVuId ID của chỉ định dịch vụ.
     * @return DTO của chỉ định dịch vụ hoặc null nếu không tìm thấy.
     */
    public ChiDinhDichVuDTO findServiceRequestById(int chiDinhDichVuId) {
        ChiDinhDichVu entity = chiDinhDAO.getById(chiDinhDichVuId);
        return toDTO(entity); // toDTO đã xử lý trường hợp entity là null
    }

    // --- PHƯƠNG THỨC CHUYỂN ĐỔI (HELPER METHOD) ---
    /**
     * Chuyển đổi một đối tượng Entity (ChiDinhDichVu) sang DTO
     * (ChiDinhDichVuDTO). Dùng để trả dữ liệu ra bên ngoài một cách an toàn.
     *
     * @param entity Đối tượng Entity cần chuyển đổi.
     * @return Đối tượng DTO tương ứng.
     */
    protected ChiDinhDichVuDTO toDTO(ChiDinhDichVu entity) {
        if (entity == null) {
            return null;
        }

        ChiDinhDichVuDTO dto = new ChiDinhDichVuDTO();
        dto.setId(entity.getId());
        dto.setKetQua(entity.getKetQua());
        dto.setTrangThai(entity.getTrangThai());

        // Lấy thông tin từ các đối tượng liên quan để "làm phẳng" DTO
        if (entity.getPhieuKham() != null) {
            dto.setPhieuKhamId(entity.getPhieuKham().getId());
        }
        if (entity.getDichVu() != null) {
            dto.setDichVuId(entity.getDichVu().getId());
            dto.setTenDichVu(entity.getDichVu().getTenDichVu());
            dto.setDonGia(entity.getDichVu().getDonGia());
        }

        return dto;
    }


    public List<ChiDinhDichVuDTO> getByPhieuKhamId(int phieuKhamId) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            List<ChiDinhDichVu> entities = chiDinhDAO.findByPhieuKhamId(phieuKhamId, session);
            
            // Chuyển List<Entity> sang List<DTO>
            return entities.stream()
                           .map(this::toDTO)
                           .collect(Collectors.toList());
        }
    }
}
