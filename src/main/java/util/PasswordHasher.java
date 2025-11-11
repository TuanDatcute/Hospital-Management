package util;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    private static final int WORK_FACTOR = 12;

    /**
     * Băm mật khẩu gốc thành chuỗi BCrypt.
     */
    public static String hashPassword(String plainPassword) {
        if (plainPassword == null || plainPassword.isEmpty()) {
            throw new IllegalArgumentException("Mật khẩu không được để trống");
        }
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(WORK_FACTOR));
    }

    /**
     * Kiểm tra xem mật khẩu người dùng nhập có khớp với mật khẩu đã băm không.
     */
    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        if (plainPassword == null || hashedPassword == null || hashedPassword.isEmpty()) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainPassword, hashedPassword);
        } catch (IllegalArgumentException e) {
            System.err.println("Lỗi kiểm tra mật khẩu: định dạng hash không hợp lệ.");
            return false;
        }
    }

    // Thêm đoạn này vào cuối file PasswordHasher.java
    public static void main(String[] args) {
        // Đặt mật khẩu admin bạn muốn dùng

        String adminPassword = "123456cup";


        // Tạo chuỗi hash
        String hashedPassword = hashPassword(adminPassword);

        System.out.println("----------------------------------------");
        System.out.println("Tài khoản Admin mới:");
        System.out.println("Mật khẩu Gốc: " + adminPassword);
        System.out.println("Mật khẩu ĐÃ HASH: " + hashedPassword);
        System.out.println("----------------------------------------");
    }
}
