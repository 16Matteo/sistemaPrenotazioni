package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String privatePath = "/Users/matteannu/Desktop/test2/";
    private static final String URL = "jdbc:sqlite:"+privatePath+"test2.db";

    public static Connection connect() {
        String testURL = System.getProperty("useTestDB");
        if("true".equals(testURL)) {
            Connection testConn = null;
            try {
                testConn = getTestConnection();
            }catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
            return testConn;
        }
        try{
            Connection conn =  DriverManager.getConnection(URL);

            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA journal_mode = WAL;"); // Attivare Write-Ahead Logging per evitare lock
            }

            return conn;
        } catch (SQLException e){
            e.printStackTrace();
            System.out.println("Errore di connessione al database: " + e.getMessage());
            return null;
        }
    }

    public static Connection getTestConnection() throws SQLException {
        String url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
        String user = "sa";
        String pass = "";
        return DriverManager.getConnection(url, user, pass);
    }

    public static void main(String[] args) {
        Connection test = DatabaseManager.connect();
        if(test == null){
            System.out.println("Errore di connessione al database");
        } else {
            System.out.println("Connessione al database riuscita");
        }
    }
}
