package utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DBUtils {
////tạo file db.properties bằng netbeans New -> Other >File Types > Properties File 
    //thêm các câu dưới đây vào file vừa tạo
//db.driver=com.microsoft.sqlserver.jdbc.SQLServerDriver
//db.url=jdbc:sqlserver://localhost:1433;databaseName=[NHAP DATA BASE NAME]
//db.username=[NHAP NAME]
//db.password[NHAP PASS]
    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Tạo đối tượng Properties để đọc file .properties
            Properties props = new Properties();
            
            // Lấy luồng đọc file từ classpath
            InputStream is = DBUtils.class.getClassLoader().getResourceAsStream("db.properties");
            
            // Tải các thuộc tính từ file
            props.load(is);
            
            // Lấy thông tin từ properties
            String driver = props.getProperty("db.driver");
            String url = props.getProperty("db.url");
            String user = props.getProperty("db.username");
            String password = props.getProperty("db.password");
            
            // Kết nối database
            Class.forName(driver);
            conn = DriverManager.getConnection(url, user, password);
            
        } catch (Exception ex) {
            Logger.getLogger(DBUtils.class.getName()).log(Level.SEVERE, "Database connection failed", ex);
        }
        return conn;
    }

    public static void main(String[] args) {
        Connection conn = DBUtils.getConnection();
        if (conn != null) {
            System.out.println("Connection successful!");
            System.out.println(conn);
        } else {
            System.out.println("Connection failed!");
        }
    }
}