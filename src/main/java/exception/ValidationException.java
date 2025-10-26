package exception;

/**
 * Ngoại lệ tùy chỉnh (Custom Exception) được sử dụng để báo hiệu các lỗi liên quan đến
 * <b>quy tắc nghiệp vụ</b> hoặc <b>dữ liệu đầu vào không hợp lệ</b>.
 * <p>
 * Ví dụ: Mã phiếu khám bị trùng, không tìm thấy bệnh nhân, số lượng tồn kho không đủ, v.v.
 * <p>
 */
public class ValidationException extends Exception {
    
    /**
     * Constructor nhận vào một thông báo lỗi.
     * @param message Thông báo lỗi cụ thể sẽ được hiển thị cho người dùng.
     */
    public ValidationException(String message) {
        // Gọi constructor của lớp cha (Exception) để lưu lại thông báo lỗi.
        super(message);
    }
}