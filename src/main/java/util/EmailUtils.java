package util;

import java.io.InputStream; // THÊM MỚI: Để đọc file
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
 * **ĐÃ CẬP NHẬT:** Tự động tải cấu hình từ file 'mail.properties'.
 * Cung cấp các hàm trừu tượng (ví dụ: sendVerificationEmail).
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
     * Tải cấu hình từ file .properties và tạo Session.
     */
    static {
        try (InputStream input = EmailUtils.class.getClassLoader().getResourceAsStream(PROPS_FILE)) {
            
            if (input == null) {
                System.err.println("!!! LỖI NGHIÊM TRỌNG: Không tìm thấy file " + PROPS_FILE + " trong classpath.");
                // Đảm bảo file mail.properties nằm trong thư mục resources/src/main/resources
                throw new RuntimeException("Không tìm thấy file " + PROPS_FILE);
            }
            
            // Tải file properties
            props.load(input);

            // Lấy các giá trị cấu hình từ file properties
            FROM_EMAIL = props.getProperty("mail.smtp.user");
            APP_PASSWORD = props.getProperty("mail.smtp.password");
            APP_BASE_URL = props.getProperty("app.base.url");

            // Kiểm tra xem đã lấy đủ cấu hình chưa
            if (FROM_EMAIL == null || APP_PASSWORD == null || APP_BASE_URL == null) {
                throw new RuntimeException("Một trong các thuộc tính (user, password, app.base.url) bị thiếu trong " + PROPS_FILE);
            }

            // Tạo Session 1 lần và tái sử dụng
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
     * **HÀM CÔNG KHAI (Public):**
     * Gửi email xác thực tài khoản.
     *
     * @param toEmail Email người nhận
     * @param tenDangNhap Tên người nhận (để chào)
     * @param token Token xác thực
     * @throws MessagingException
     */
    public static void sendVerificationEmail(String toEmail, String tenDangNhap, String token) throws MessagingException {
        
        // 1. Xây dựng Link (Lấy từ file config)
        // Link này sẽ là: http://localhost:8080/Hospital_Managerment/verify?token=...
        String verificationLink = APP_BASE_URL + "/verify?token=" + token;
        
        // 2. Xây dựng Tiêu đề
        String subject = "Kích hoạt tài khoản của bạn tại Bệnh viện";
        
        // 3. Xây dựng Nội dung HTML
        String htmlContent = buildVerificationEmailHtml(tenDangNhap, verificationLink);
        
        // 4. Gọi hàm gửi
        private_sendEmail(toEmail, subject, htmlContent);
    }
    
    /**
     * **HÀM NỘI BỘ (Private):**
     * Phương thức tĩnh cốt lõi để gửi email.
     * Sử dụng Session đã được tạo sẵn trong khối static.
     */
    private static void private_sendEmail(String toEmail, String subject, String htmlContent) throws MessagingException {
        
        System.out.println("Đang chuẩn bị gửi email tới: " + toEmail);

        // Tạo đối tượng MimeMessage (nội dung email)
        // Dùng Session đã được tạo sẵn (tái sử dụng)
        // **FIX:** Khai báo là MimeMessage để dùng được hàm setSubject(subject, "UTF-8")
        MimeMessage message = new MimeMessage(session);
        
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject, "UTF-8"); // Đặt tiêu đề (hỗ trợ UTF-8)
        
        // Thiết lập nội dung là HTML
        message.setContent(htmlContent, "text/html; charset=utf-8");

        // Gửi email
        Transport.send(message);
        
        System.out.println("Gửi email thành công!");
    }

    /**
     * Hàm tiện ích để tạo nội dung HTML cho email xác thực.
     * (Đây là code HTML đẹp của bạn, giữ nguyên)
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

    /**
     * **HÀM TEST (THỬ NGHIỆM)**
     * Dùng hàm main này để tự kiểm tra nhanh xem có gửi được email không.
     * Cách chạy: Nhấn chuột phải vào file này -> Run File.
     */
    public static void main(String[] args) {
        // **THAY EMAIL NÀY THÀNH EMAIL CỦA BẠN ĐỂ TEST**
        String testRecipientEmail = "gabokan02564@gmail.com"; 
        
        try {
            System.out.println("Đang bắt đầu gửi email test...");
            
            // Test bằng hàm public mới
            sendVerificationEmail(testRecipientEmail, "Bệnh Nhân Test", "abc123xyz_test_token");
            
            System.out.println("--- TEST THÀNH CÔNG ---");
            System.out.println("Kiểm tra hòm thư của: " + testRecipientEmail);

        } catch (Exception e) {
            System.err.println("--- !!! TEST THẤT BẠI !!! ---");
            e.printStackTrace();
            
            // Phân tích lỗi (giữ nguyên, rất hữu ích)
            if (e instanceof MessagingException) {
                Exception nextException = ((MessagingException) e).getNextException();
                if (nextException instanceof java.net.SocketException) {
                    System.err.println("\n*** LỖI GỢI Ý: KẾT NỐI THẤT BẠI. (SocketException) ***");
                    System.err.println("-> Nguyên nhân: Kiểm tra Tường lửa (Firewall) hoặc Antivirus có đang chặn cổng 587 không.");
                }
            }
            if (e.getMessage() != null && e.getMessage().contains("AuthenticationFailedException")) {
                 System.err.println("\n*** LỖI GỢI Ý: XÁC THỰC THẤT BẠI. (AuthenticationFailedException) ***");
                 System.err.println("-> Nguyên nhân: 'mail.smtp.user' hoặc 'mail.smtp.password' trong file mail.properties bị sai.");
            }
             if (e.getMessage() != null && e.getMessage().contains("jakarta.mail.internet.AddressException")) {
                 System.err.println("\n*** LỖI GỢI Ý: ĐỊA CHỈ EMAIL KHÔNG HỢP LỆ. (AddressException) ***");
                 System.err.println("-> Nguyên nhân: 'mail.smtp.user' hoặc 'testRecipientEmail' có ký tự lạ hoặc sai định dạng.");
             }
        }
    }
}