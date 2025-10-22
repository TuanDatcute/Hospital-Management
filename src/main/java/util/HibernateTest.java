package util;

// ✨ SỬA LẠI CÁC IMPORT CHO ĐÚNG ✨
import org.hibernate.Session; // ĐÚNG: Session của Hibernate
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration; // ĐÚNG: Configuration của Hibernate

/**
 * Lớp dùng để kiểm tra kết nối đến cơ sở dữ liệu thông qua Hibernate.
 */
public class HibernateTest { // Đổi tên từ HibernateConnectionTest nếu bạn muốn

    public static void main(String[] args) {
        SessionFactory factory = null;
        Session session = null; // Bây giờ IDE sẽ hiểu lớp Session này

        System.out.println("Đang bắt đầu kiểm tra kết nối Hibernate...");

        try {
            // Dòng này bây giờ sẽ hoạt động bình thường
            System.out.println("Đang đọc file hibernate.cfg.xml và xây dựng SessionFactory...");
            factory = new Configuration().configure().buildSessionFactory(); 
            System.out.println("SessionFactory đã được tạo thành công!");

            System.out.println("Đang mở một Session để kết nối tới CSDL...");
            session = factory.openSession();
            System.out.println("Session đã được mở thành công!");

            System.out.println("\n=======================================================");
            System.out.println("✅ CHÚC MỪNG! KẾT NỐI HIBERNATE THÀNH CÔNG!");
            System.out.println("=======================================================");

        } catch (Throwable ex) {
            System.err.println("\n=======================================================");
            System.err.println("❌ THẤT BẠI! Không thể khởi tạo SessionFactory.");
            System.err.println("Lỗi chi tiết:");
            ex.printStackTrace();
            System.err.println("=======================================================");
        } finally {
            if (session != null && session.isOpen()) {
                System.out.println("Đang đóng Session...");
                session.close();
            }
            if (factory != null && !factory.isClosed()) {
                System.out.println("Đang đóng SessionFactory...");
                factory.close();
            }
        }
    }
}