package dao;

import static java.sql.DriverManager.*;



import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DAO {
    protected static Connection con = null;
//=c33x>d[tcw]}U)h
//    private static final String dbUrl = "jdbc:mysql://192.168.1.104//:3306/chatapp/";
//    private static final String userDB="root";
//    private static final String passwordDB="123456789";
//    private static final String dbClass="com.mysql.jdbc.Driver";
    public DAO() {
        initDAO();
    }

    private void initDAO() {
        if (con == null) {
            
            String dbUrl = "jdbc:mysql://35.194.160.94:3306/chatapp";
            String dbClass = "com.mysql.jdbc.Driver";

            try {
                Class.forName(dbClass);
                con = DriverManager.getConnection (dbUrl, "root", "123456789");
            }catch(Exception e) {
                e.printStackTrace();
            }

        }
    }
}
