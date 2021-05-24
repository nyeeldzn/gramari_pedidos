package helpers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class db_connect {


    private static String HOST = "localhost";
    private static int PORT = 3306;
    private static String DB_NAME = "pedidos";
    private static String url = "jdbc:mysql://" + HOST + "/" + DB_NAME;
    private static String USERNAME = "root";
    private static String PASSWORD = "";
    private static Connection connection;

    public static Connection getConnect (){
        try {
            //connection = DriverManager.getConnection(String.format("jdbc:mysql://" HOST, PORT, DB_NAME,  USERNAME, PASSWORD));
            connection = DriverManager.getConnection(url, USERNAME, PASSWORD);
/*
            connection = DriverManager.getConnection(String.format("jdbc:mysql://%s:%d:%s", HOST, PORT, DB_NAME, USERNAME, PASSWORD));

 */
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return connection;
    }
}
