// --- ĐÃ SỬA: Đặt trong package util ---
package util; 

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
 * Lớp Tiện ích (Utils) để gửi email qua Gmail SMTP.
 * Lớp này chứa các phương thức static, có thể gọi trực tiếp.
 */
public class EmailUtils {

    // --- CẤU HÌNH BẮT BUỘC ---
    // Thay thế bằng email của bạn (email này phải bật xác minh 2 bước)
    private static final String FROM_EMAIL = "gabokan02564@gmail.com"; 
    
    // Thay thế bằng MẬT KHẨU ỨNG DỤNG 16 KÝ TỰ (lấy từ Google App Passwords)
    private static final String APP_PASSWORD = "rghh aayb pzxf gjjn"; 
    // -------------------------

    /**
     * Lớp tiện ích không nên được khởi tạo.
     */
    private EmailUtils() {
    }

    /**
     * Phương thức tĩnh chính để gửi email.
     *
     * @param toEmail Email người nhận
     * @param subject Tiêu đề email
     * @param htmlContent Nội dung email (dạng HTML)
     * @throws MessagingException Ném ra nếu có lỗi khi gửi
     */
    public static void sendEmail(String toEmail, String subject, String htmlContent) throws MessagingException {
        
        System.out.println("Đang chuẩn bị gửi email tới: " + toEmail);

        // 1. Cấu hình thuộc tính cho máy chủ SMTP
        Properties props = new Properties();
        props.put("mail.smtp.host", "smtp.gmail.com"); // Máy chủ SMTP của Gmail
        props.put("mail.smtp.port", "587"); // Port cho TLS
        props.put("mail.smtp.auth", "true"); // Yêu cầu xác thực
        props.put("mail.smtp.starttls.enable", "true"); // Bật STARTTLS

        // 2. Tạo phiên (Session) với Authenticator
        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                // Cung cấp email và Mật khẩu ứng dụng
                return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
            }
        });

        // 3. Tạo đối tượng MimeMessage (nội dung email)
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmail));
        message.setSubject(subject);
        
        // 4. Thiết lập nội dung là HTML
        message.setContent(htmlContent, "text/html; charset=utf-8");

        // 5. Gửi email
        Transport.send(message);
        
        System.out.println("Gửi email thành công!");
    }

    /**
     * Hàm tiện ích để tạo nội dung HTML cho email xác thực.
     */
    public static String buildVerificationEmailHtml(String username, String verificationLink) {
        return "<html lang='vi'>"
                + "<body style='font-family: Arial, sans-serif; line-height: 1.6;'>"
                + "  <div style='max-width: 600px; margin: 20px auto; padding: 20px; border: 1px solid #ddd; border-radius: 5px;'>"
                + "    <h2 style='color: #0056b3;'>Chào mừng bạn, " + username + "!</h2>"
                + "    <p>Cảm ơn bạn đã đăng ký tài khoản tại <strong>Bệnh viện Đa khoa</strong> của chúng tôi.</p>"
                + "    <p>Vui lòng nhấp vào nút bên dưới để hoàn tất quá trình đăng ký và kích hoạt tài khoản của bạn:</p>"
                + "    <a href='" + verificationLink + "' "
                + "       style='display: inline-block; background-color: #007bff; color: white; padding: 12px 20px; text-decoration: none; border-radius: 5px; font-weight: bold;'>"
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
     * Cách chạy: Nhấn chuột phải vào file này -> Run File (hoặc Run as Java Application).
     */
    public static void main(String[] args) {
        // **THAY EMAIL NÀY THÀNH EMAIL CỦA BẠN ĐỂ TEST**
        String testRecipientEmail = "gabokan02564@gmail.com"; 
        
        try {
            System.out.println("Đang bắt đầu gửi email test...");
            
            String subject = "Test Email từ Ứng dụng Bệnh viện";
            String link = "http://localhost:8080/MyWebApp/MainController?action=verify&token=abc123xyz";
            String html = buildVerificationEmailHtml("Bệnh Nhân Test", link);
            
            sendEmail(testRecipientEmail, subject, html);
            
            System.out.println("--- TEST THÀNH CÔNG ---");
            System.out.println("Kiểm tra hòm thư của: " + testRecipientEmail);

        } catch (Exception e) {
            System.err.println("--- !!! TEST THẤT BẠI !!! ---");
            e.printStackTrace();
            
            // Phân tích lỗi thường gặp cho bạn
            if (e instanceof MessagingException) {
                Exception nextException = ((MessagingException) e).getNextException();
                if (nextException instanceof java.net.SocketException) {
                    System.err.println("\n*** LỖI GỢI Ý: KẾT NỐI THẤT BẠI. (SocketException) ***");
                    System.err.println("-> Nguyên nhân: Kiểm tra Tường lửa (Firewall) hoặc Antivirus có đang chặn cổng 587 không.");
                }
            }
            if (e.getMessage().contains("AuthenticationFailedException")) {
                 System.err.println("\n*** LỖI GỢI Ý: XÁC THỰC THẤT BẠI. (AuthenticationFailedException) ***");
                 System.err.println("-> Nguyên nhân 1: Sai 'FROM_EMAIL'.");
                 System.err.println("-> Nguyên nhân 2: 'APP_PASSWORD' (16 ký tự) bị sai hoặc chưa tạo.");
            }
             if (e.getMessage().contains("jakarta.mail.internet.AddressException")) {
                 System.err.println("\n*** LỖI GỢI Ý: ĐỊA CHỈ EMAIL KHÔNG HỢP LỆ. (AddressException) ***");
                 System.err.println("-> Nguyên nhân: 'FROM_EMAIL' hoặc 'testRecipientEmail' có ký tự lạ hoặc sai định dạng.");
            }
        }
    }
}