package util;


import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;


public class HibernateUtil {

    // 1. Khai báo một đối tượng SessionFactory duy nhất, được tạo một lần.
    private static final SessionFactory sessionFactory = buildSessionFactory();
    
    private static SessionFactory buildSessionFactory() {
        try {
            // 2. Tạo SessionFactory từ file hibernate.cfg.xml
            // Hibernate sẽ tự động tìm file này trong classpath.
            return new Configuration().configure().buildSessionFactory();
        } catch (Throwable ex) {
            // 3. Nếu có lỗi xảy ra trong quá trình khởi tạo, log lỗi và ném ra ngoại lệ.
            // Việc này sẽ làm ứng dụng dừng lại, vì không có CSDL thì không thể chạy.
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Phương thức public để các lớp khác có thể lấy được đối tượng
     * SessionFactory.
     *
     * @return Đối tượng SessionFactory duy nhất của ứng dụng.
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * (Tùy chọn) Phương thức để đóng SessionFactory khi ứng dụng tắt.
     */
    public static void shutdown() {
        getSessionFactory().close();
    }
}
