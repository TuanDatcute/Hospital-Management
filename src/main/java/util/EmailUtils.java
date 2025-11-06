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

/**
 * Lớp Tiện ích (Utils) để gửi email.
 * **ĐÃ CẬP NHẬT:** Thêm logic Gửi email Quên Mật khẩu.
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
     * Khối static: 
     * Tự động chạy 1 LẦN khi lớp được tải.
     * (Giữ nguyên logic tải .properties)
     */
    static {
        try (InputStream input = EmailUtils.class.getClassLoader().getResourceAsStream(PROPS_FILE)) {
            
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
     * HÀM CÔNG KHAI (Public):
     * Gửi email XÁC THỰC tài khoản.
     * (Giữ nguyên hàm này)
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
     * **HÀM CÔNG KHAI (Public) MỚI:**
     * Gửi email đặt lại mật khẩu.
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
     * HÀM NỘI BỘ (Private):
     * Phương thức tĩnh cốt lõi để gửi email.
     * (Giữ nguyên hàm này)
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
     * Hàm tiện ích để tạo nội dung HTML cho email XÁC THỰC.
     * (Giữ nguyên hàm này)
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
     * **HÀM TEST (THỬ NGHIỆM)**
     * **CẬP NHẬT:** Test cả 2 hàm.
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