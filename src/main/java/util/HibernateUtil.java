package util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

/**
 * Lớp tiện ích để quản lý SessionFactory của Hibernate.
 * Đây là cách làm chuẩn, đảm bảo chỉ có MỘT SessionFactory duy nhất
 * được tạo ra trong toàn bộ ứng dụng.
 */
public class HibernateUtil {

    // 1. Khai báo một đối tượng SessionFactory duy nhất, được tạo một lần duy nhất.
    private static final SessionFactory sessionFactory = buildSessionFactory();

    /**
     * Phương thức private để xây dựng SessionFactory.
     * Nó được gọi một lần khi lớp này được tải vào bộ nhớ.
     */
    private static SessionFactory buildSessionFactory() {
        try {
            // 2. Tạo SessionFactory từ file hibernate.cfg.xml
            // Lệnh .configure() sẽ tự động tìm và đọc file hibernate.cfg.xml
            // trong thư mục src/main/resources.
            return new Configuration().configure().buildSessionFactory();
            
        } catch (Throwable ex) {
            // 3. Nếu có lỗi nghiêm trọng xảy ra trong quá trình khởi tạo,
            // in lỗi ra và dừng ứng dụng.
            System.err.println("Không thể khởi tạo SessionFactory: " + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Phương thức public để các lớp khác có thể lấy được đối tượng SessionFactory duy nhất.
     * @return Đối tượng SessionFactory của ứng dụng.
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * (Tùy chọn) Phương thức để đóng SessionFactory khi ứng dụng tắt.
     */
    public static void shutdown() {
        if (getSessionFactory() != null) {
            getSessionFactory().close();
        }
    }
}