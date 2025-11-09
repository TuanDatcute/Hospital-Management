package util;

import java.io.InputStream;
import java.util.Properties;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import model.dto.LichHenDTO;
import model.dto.PhieuKhamBenhDTO;

/**
 * Lớp Tiện ích (Utils) để gửi email. **ĐÃ CẬP NHẬT:** Thêm logic Gửi email Quên
 * Mật khẩu.
 */
public class EmailUtils {

    // Tên file cấu hình
    private static final String PROPS_FILE = "mail.properties";

    // Khai báo các biến static
    private static final Properties props = new Properties();
    private static final String FROM_EMAIL;
    private static final String APP_PASSWORD;
    private static final String APP_BASE_URL;
    private static final Session session; // Session được tạo 1 lần và tái sử dụng

    /**
     * Khối static: Tự động chạy 1 LẦN khi lớp được tải. (Giữ nguyên logic tải
     * .properties)
     */
    static {
        try ( InputStream input = EmailUtils.class.getClassLoader().getResourceAsStream(PROPS_FILE)) {

            if (input == null) {
                System.err.println("!!! LỖI NGHIÊM TRỌNG: Không tìm thấy file " + PROPS_FILE + " trong classpath.");
                throw new RuntimeException("Không tìm thấy file " + PROPS_FILE);
            }

            props.load(input);
            FROM_EMAIL = props.getProperty("mail.smtp.user");
            APP_PASSWORD = props.getProperty("mail.smtp.password");
            APP_BASE_URL = props.getProperty("app.base.url");

            if (FROM_EMAIL == null || APP_PASSWORD == null || APP_BASE_URL == null) {
                throw new RuntimeException("Một trong các thuộc tính (user, password, app.base.url) bị thiếu trong " + PROPS_FILE);
            }

            Authenticator auth = new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
                }
            };
            session = Session.getInstance(props, auth);

            System.out.println("EmailUtils: Đã tải cấu hình email thành công cho " + FROM_EMAIL);

        } catch (Exception e) {
            System.err.println("Lỗi khi tải cấu hình EmailUtils: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Lớp tiện ích không nên được khởi tạo.
     */
    private EmailUtils() {
    }

    /**
     * HÀM NỘI BỘ (Private): Phương thức tĩnh cốt lõi để gửi email. (Giữ nguyên
     * hàm này)
     */
    private static void private_sendEmail(String toEmail, String subject, String htmlContent) throws MessagingException {

        System.out.println("Đang chuẩn bị gửi email tới: " + toEmail);
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject, "UTF-8");
        message.setContent(htmlContent, "text/html; charset=utf-8");
        Transport.send(message);
        System.out.println("Gửi email thành công!");
    }

    /**
     * Gửi email thông báo KHI PHIẾU KHÁM HOÀN THÀNH.
     *
     * @param toEmail Email của bệnh nhân.
     * @param phieuKham DTO chứa thông tin phiếu khám.
     * @throws MessagingException
     */
    public static void sendEncounterCompletedEmail(String toEmail, PhieuKhamBenhDTO phieuKham) throws MessagingException {
        // 1. Xây dựng Tiêu đề
        String subject = "Thông báo hoàn thành Phiếu khám #" + phieuKham.getMaPhieuKham();

        // 2. Xây dựng Nội dung HTML
        String htmlContent = buildEncounterEmailHtml(phieuKham);

        // 3. Gọi hàm gửi
        private_sendEmail(toEmail, subject, htmlContent);
    }

    /**
     * HÀM NỘI BỘ (Private): Xây dựng nội dung HTML cho email thông báo phiếu
     * khám.
     */
    private static String buildEncounterEmailHtml(PhieuKhamBenhDTO phieuKham) {
        // (Đây là nội dung email tóm tắt, không phải file in bệnh án đầy đủ)

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>");
        sb.append("<div style='width: 90%; max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>");

        sb.append("<h2 style='color: #4a90e2; margin-top: 0;'>Kết quả Phiếu khám bệnh của bạn đã có</h2>");
        sb.append("<p>Xin chào <strong>").append(phieuKham.getTenBenhNhan()).append("</strong>,</p>");
        sb.append("<p>Phiếu khám của bạn (mã: <strong>").append(phieuKham.getMaPhieuKham()).append("</strong>) đã được bác sĩ xử lý và hoàn thành.</p>");
        sb.append("<hr style='border: 0; border-top: 1px solid #eee;'>");

        sb.append("<h3 style='color: #4a90e2;'>Tóm tắt Phiếu khám</h3>");
        sb.append("<ul style='list-style: none; padding-left: 0;'>");
        sb.append("<li><strong>Bác sĩ khám:</strong> ").append(phieuKham.getTenBacSi()).append("</li>");
        sb.append("<li><strong>Thời gian:</strong> ").append(phieuKham.getThoiGianKhamFormatted()).append("</li>");
        sb.append("</ul>");

        sb.append("<h4>Chẩn đoán của bác sĩ</h4>");
        sb.append("<p style='background: #f4f7f9; padding: 15px; border-radius: 5px;'><i>");
        sb.append(phieuKham.getChanDoan() != null ? phieuKham.getChanDoan() : "Không có chẩn đoán").append("</i></p>");

        sb.append("<h4>Kết luận & Dặn dò</h4>");
        sb.append("<p style='background: #f4f7f9; padding: 15px; border-radius: 5px;'>");
        sb.append(phieuKham.getKetLuan() != null ? phieuKham.getKetLuan() : "Không có dặn dò cụ thể.").append("</p>");

        sb.append("<hr style='border: 0; border-top: 1px solid #eee;'>");
        sb.append("<p style='text-align: center; margin-top: 20px;'>Vui lòng đăng nhập vào hệ thống của chúng tôi (sử dụng liên kết bên dưới) để xem chi tiết đơn thuốc và các kết quả dịch vụ.</p>");

        sb.append("<div style='text-align: center; margin: 30px 0;'>");
        sb.append("<a href='").append(APP_BASE_URL).append("' style='background-color: #4a90e2; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold;'>Đăng nhập hệ thống</a>");
        sb.append("</div>");

        sb.append("<p style='font-size: 0.9em; color: #777; text-align: center;'>Trân trọng,<br>Bệnh viện Quốc tế HQD</p>");
        sb.append("</div></body></html>");

        return sb.toString();
    }

    /**
     * HÀM CÔNG KHAI (Public): Gửi email XÁC THỰC tài khoản. (Giữ nguyên hàm
     * này)
     */
    public static void sendVerificationEmail(String toEmail, String tenDangNhap, String token) throws MessagingException {

        // 1. Xây dựng Link (Lấy từ file config)
        String verificationLink = APP_BASE_URL + "/verify?token=" + token;

        // 2. Xây dựng Tiêu đề
        String subject = "Kích hoạt tài khoản của bạn tại Bệnh viện";

        // 3. Xây dựng Nội dung HTML
        String htmlContent = buildVerificationEmailHtml(tenDangNhap, verificationLink);

        // 4. Gọi hàm gửi
        private_sendEmail(toEmail, subject, htmlContent);
    }

    // --- **BẮT ĐẦU THÊM MỚI (Quên Mật khẩu)** ---
    /**
     * **HÀM CÔNG KHAI (Public) MỚI:** Gửi email đặt lại mật khẩu.
     *
     * @param toEmail Email người nhận
     * @param tenNguoiDung Tên người dùng (để chào)
     * @param token Token reset
     * @throws MessagingException
     */
    public static void sendPasswordResetEmail(String toEmail, String tenNguoiDung, String token) throws MessagingException {

        // 1. Xây dựng Link (Lấy từ file config)
        // **Quan trọng:** Dùng đường dẫn "/reset" (cho PasswordResetController)
        String resetLink = APP_BASE_URL + "/reset?token=" + token;

        // 2. Xây dựng Tiêu đề
        String subject = "Yêu cầu đặt lại mật khẩu Bệnh viện";

        // 3. Xây dựng Nội dung HTML
        String htmlContent = buildPasswordResetEmailHtml(tenNguoiDung, resetLink);

        // 4. Gọi hàm gửi
        private_sendEmail(toEmail, subject, htmlContent);
    }

    // --- **KẾT THÚC THÊM MỚI** ---
    /**
     * Hàm tiện ích để tạo nội dung HTML cho email XÁC THỰC. (Giữ nguyên hàm
     * này)
     */
    private static String buildVerificationEmailHtml(String username, String verificationLink) {
        return "<html lang='vi'>"
                + "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
                + "  <div style='max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;'>"
                + "    <h2 style='color: #0056b3;'>Chào mừng bạn, " + username + "!</h2>"
                + "    <p>Cảm ơn bạn đã đăng ký tài khoản tại <strong>Bệnh viện Đa khoa</strong> của chúng tôi.</p>"
                + "    <p>Vui lòng nhấp vào nút bên dưới để hoàn tất quá trình đăng ký và kích hoạt tài khoản của bạn:</p>"
                + "    <a href='" + verificationLink + "' "
                + "      style='display: inline-block; background-color: #007bff; color: white; padding: 12px 20px; text-decoration: none; border-radius: 5px; font-weight: bold;'>"
                + "      Kích hoạt tài khoản"
                + "    </a>"
                + "    <p style='margin-top: 20px;'>Nếu nút trên không hoạt động, bạn cũng có thể sao chép và dán liên kết sau vào trình duyệt:</p>"
                + "    <p style='word-break: break-all; color: #555;'>" + verificationLink + "</p>"
                + "    <hr style='border: 0; border-top: 1px solid #eee; margin: 20px 0;'>"
                + "    <p style='font-size: 0.9em; color: #777;'>Nếu bạn không thực hiện đăng ký này, vui lòng bỏ qua email.</p>"
                + "    <p style='font-size: 0.9em; color: #777;'>Trân trọng,<br>Đội ngũ Bệnh viện</p>"
                + "  </div>"
                + "</body>"
                + "</html>";
    }

    // --- **BẮT ĐẦU THÊM MỚI (Mẫu HTML cho Quên Mật khẩu)** ---
    /**
     * **HÀM MỚI:** Tạo HTML cho email đặt lại mật khẩu.
     */
    private static String buildPasswordResetEmailHtml(String username, String resetLink) {
        return "<html lang='vi'>"
                + "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
                + "  <div style='max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;'>"
                + "    <h2 style='color: #0056b3;'>Xin chào, " + username + "!</h2>"
                + "    <p>Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản của bạn.</p>"
                + "    <p>Vui lòng nhấp vào nút bên dưới để đặt mật khẩu mới. Link này sẽ **hết hạn sau 1 giờ**:</p>"
                + "    <a href='" + resetLink + "' "
                // Nút màu đỏ (cảnh báo)
                + "      style='display: inline-block; background-color: #dc3545; color: white; padding: 12px 20px; text-decoration: none; border-radius: 5px; font-weight: bold;'>"
                + "      Đặt lại Mật khẩu"
                + "    </a>"
                + "    <p style='margin-top: 20px;'>Nếu nút trên không hoạt động, bạn cũng có thể sao chép và dán liên kết sau vào trình duyệt:</p>"
                + "    <p style='word-break: break-all; color: #555;'>" + resetLink + "</p>"
                + "    <hr style='border: 0; border-top: 1px solid #eee; margin: 20px 0;'>"
                + "    <p style='font-size: 0.9em; color: #777;'>Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email.</p>"
                + "  </div>"
                + "</body>"
                + "</html>";
    }
    // --- **KẾT THÚC THÊM MỚI** ---

    /**
     * HÀM MỚI: Gửi email XÁC NHẬN khi bệnh nhân đặt lịch hẹn
     */
    public static void sendAppointmentConfirmationEmail(String toEmail, LichHenDTO lichHen) throws MessagingException {
        // 1. Xây dựng Tiêu đề
        String subject = "Xác nhận Lịch hẹn #" + lichHen.getId() + " - STT: " + lichHen.getStt();

        // 2. Xây dựng Nội dung HTML
        String htmlContent = buildAppointmentConfirmationHtml(toEmail, lichHen);

        // 3. Gọi hàm gửi
        private_sendEmail(toEmail, subject, htmlContent);
    }

    /**
     * HÀM MỚI: Gửi email THÔNG BÁO khi bệnh nhân hủy lịch hẹn
     */
    public static void sendAppointmentCancellationEmail(String toEmail, LichHenDTO lichHen) throws MessagingException {
        // 1. Xây dựng Tiêu đề
        String subject = "Thông báo Hủy Lịch hẹn #" + lichHen.getId();

        // 2. Xây dựng Nội dung HTML
        String htmlContent = buildAppointmentCancellationHtml(toEmail, lichHen);

        // 3. Gọi hàm gửi
        private_sendEmail(toEmail, subject, htmlContent);
    }

    /**
     * HÀM MỚI (Helper): Tạo HTML cho email Xác nhận đặt lịch
     */
    private static String buildAppointmentConfirmationHtml(String toEmail, LichHenDTO lichHen) {
        // Định dạng lại thời gian
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm 'ngày' dd/MM/yyyy");
        // Giả sử múi giờ +07:00
        String thoiGianHenFormatted = lichHen.getThoiGianHen().withOffsetSameInstant(ZoneOffset.ofHours(7)).format(formatter);

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>");
        sb.append("<div style='max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>");

        sb.append("<h2 style='color: #007bff; margin-top: 0;'>Xác nhận Đặt lịch hẹn thành công</h2>");
        sb.append("<p>Xin chào <strong>").append(lichHen.getTenBenhNhan()).append("</strong>,</p>");
        sb.append("<p>Bạn đã đặt lịch hẹn thành công tại Bệnh viện Quốc tế HQD. Vui lòng kiểm tra thông tin dưới đây:</p>");
        sb.append("<hr style='border: 0; border-top: 1px solid #eee;'>");

        sb.append("<h3 style='color: #4a90e2;'>Chi tiết Lịch hẹn</h3>");
        sb.append("<ul style='list-style: none; padding-left: 0;'>");
        sb.append("<li><strong>Bác sĩ:</strong> ").append(lichHen.getTenBacSi()).append("</li>");
        sb.append("<li><strong>Thời gian:</strong> <strong style='color: #c00;'>").append(thoiGianHenFormatted).append("</strong></li>");
        sb.append("<li><strong>Số thứ tự (STT):</strong> <strong style='color: #c00;'>").append(lichHen.getStt()).append("</strong></li>");
        sb.append("<li><strong>Lý do khám:</strong> ").append(lichHen.getLyDoKham()).append("</li>");
        sb.append("<li><strong>Trạng thái:</strong> ").append(lichHen.getTrangThai()).append(" (Sẽ được nhân viên y tế xác nhận sớm)</li>");
        sb.append("</ul>");

        sb.append("<p>Vui lòng có mặt trước 15 phút. Nếu muốn hủy lịch, bạn có thể đăng nhập vào hệ thống.</p>");

        sb.append("<div style='text-align: center; margin: 30px 0;'>");
        sb.append("<a href='").append(APP_BASE_URL).append("' style='background-color: #007bff; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold;'>Đăng nhập hệ thống</a>");
        sb.append("</div>");

        sb.append("<p style='font-size: 0.9em; color: #777; text-align: center;'>Trân trọng,<br>Bệnh viện HospitalManagement</p>");
        sb.append("</div></body></html>");

        return sb.toString();
    }

    /**
     * HÀM MỚI (Helper): Tạo HTML cho email Hủy lịch
     */
    private static String buildAppointmentCancellationHtml(String toEmail, LichHenDTO lichHen) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm 'ngày' dd/MM/yyyy");
        String thoiGianHenFormatted = lichHen.getThoiGianHen().withOffsetSameInstant(ZoneOffset.ofHours(7)).format(formatter);

        StringBuilder sb = new StringBuilder();
        sb.append("<html><body style='font-family: Arial, sans-serif; line-height: 1.6; color: #333;'>");
        sb.append("<div style='max-width: 600px; margin: auto; padding: 20px; border: 1px solid #ddd; border-radius: 8px;'>");

        sb.append("<h2 style='color: #dc3545; margin-top: 0;'>Thông báo Hủy lịch hẹn</h2>");
        sb.append("<p>Xin chào <strong>").append(lichHen.getTenBenhNhan()).append("</strong>,</p>");
        sb.append("<p>Lịch hẹn của bạn đã được hủy thành công.</p>");
        sb.append("<hr style='border: 0; border-top: 1px solid #eee;'>");

        sb.append("<h3 style='color: #4a90e2;'>Chi tiết Lịch hẹn đã hủy</h3>");
        sb.append("<ul style='list-style: none; padding-left: 0;'>");
        sb.append("<li><strong>Bác sĩ:</strong> ").append(lichHen.getTenBacSi()).append("</li>");
        sb.append("<li><strong>Thời gian:</strong> ").append(thoiGianHenFormatted).append("</li>");
        sb.append("<li><strong>Trạng thái:</strong> <strong style='color: #dc3545;'>ĐÃ HỦY</strong></li>");
        sb.append("</ul>");

        sb.append("<p>Nếu bạn không phải là người thực hiện hủy lịch, vui lòng liên hệ với chúng tôi ngay lập tức.</p>");

        sb.append("<div style='text-align: center; margin: 30px 0;'>");
        sb.append("<a href='").append(APP_BASE_URL).append("' style='background-color: #007bff; color: white; padding: 12px 25px; text-decoration: none; border-radius: 5px; font-weight: bold;'>Đăng nhập hệ thống</a>");
        sb.append("</div>");

        sb.append("</div></body></html>");

        return sb.toString();
    }

    /**
     * **HÀM TEST (THỬ NGHIỆM)** **CẬP NHẬT:** Test cả 2 hàm.
     */
    public static void main(String[] args) {
        String testRecipientEmail = "gabokan02564@gmail.com";

        try {
            System.out.println("--- 1. Đang test email XÁC THỰC TÀI KHOẢN ---");
            sendVerificationEmail(testRecipientEmail, "Bệnh Nhân Test (Xác thực)", "abc_token_xac_thuc");
            System.out.println("-> Test 1 THÀNH CÔNG.");

            System.out.println("\n--- 2. Đang test email QUÊN MẬT KHẨU ---");
            sendPasswordResetEmail(testRecipientEmail, "Bệnh Nhân Test (Quên MK)", "xyz_token_reset_mk");
            System.out.println("-> Test 2 THÀNH CÔNG.");

            System.out.println("\n--- TEST HOÀN TẤT ---");
            System.out.println("Kiểm tra hòm thư của: " + testRecipientEmail);

        } catch (Exception e) {
            System.err.println("--- !!! TEST THẤT BẠI !!! ---");
            e.printStackTrace();

            // (Phân tích lỗi của bạn, giữ nguyên)
            if (e instanceof MessagingException) {
                Exception nextException = ((MessagingException) e).getNextException();
                if (nextException instanceof java.net.SocketException) {
                    System.err.println("\n*** LỖI GỢI Ý: KẾT NỐI THẤT BẠI. (SocketException) ***");
                }
            }
            if (e.getMessage() != null && e.getMessage().contains("AuthenticationFailedException")) {
                System.err.println("\n*** LỖI GỢI Ý: XÁC THỰC THẤT BẠI. (AuthenticationFailedException) ***");
                System.err.println("-> Nguyên nhân: 'mail.smtp.user' hoặc 'mail.smtp.password' trong file mail.properties bị sai.");
            }
            if (e.getMessage() != null && e.getMessage().contains("jakarta.mail.internet.AddressException")) {
                System.err.println("\n*** LỖI GỢI Ý: ĐỊA CHỈ EMAIL KHÔNG HỢP LỆ. (AddressException) ***");
            }
        }
    }
}
