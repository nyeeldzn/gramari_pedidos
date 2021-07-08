package helpers.Database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class db_connect {


    private static String HOST = "";
    private static int PORT = 3306;
    private static String DB_NAME = "pedidos";
    private static String url = "jdbc:mysql://" + HOST + "/" + DB_NAME;
    //private static String USERNAME = "root";
    //private static String PASSWORD = "";
    private static String USERNAME = "client";
    private static String PASSWORD = "GExYDyvXw2NTzP3p";
    private static Connection connection;

    public static void setHOST(String host) {
        HOST = host;
        System.out.println("HOST DEFINIDO PARA: " + host);
    }

    public static String getHost (){
       String host = HOST;
       return host;
    }

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
