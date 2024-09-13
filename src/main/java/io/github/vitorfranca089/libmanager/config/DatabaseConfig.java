package io.github.vitorfranca089.libmanager.config;

import org.flywaydb.core.Flyway;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConfig {

    private final static String DB_URL = System.getenv("DB_URL");
    private final static String DB_USERNAME = System.getenv("DB_USERNAME");
    private final static String DB_PASSWORD = System.getenv("DB_PASSWORD");

    private static Connection conn;

    public static Connection getConnection(){
        try {
            if(conn == null || conn.isClosed()) conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
        }catch(SQLException e){
            e.printStackTrace();
        }
        return conn;
    }

    public static void migrateDatabase() {
        Flyway flyway = Flyway.configure()
                .dataSource(DB_URL, DB_USERNAME, DB_PASSWORD)
                .load();
        flyway.migrate();
    }

}
