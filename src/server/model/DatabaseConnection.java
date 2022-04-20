package server.model;

import server.controller.LoginController;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.sql.*;

public class DatabaseConnection {

        private final Properties properties;
        private final String url;
        private Connection conn;


        public DatabaseConnection() {
            this.properties = new Properties();
            properties.setProperty("user","postgres");
            properties.setProperty("password","vz4fv1xd");
            properties.setProperty("ssl","false");
            url = "jdbc:postgresql://localhost:5432/";
        }

        public Connection getConnection(){

            try {

                if (conn == null || conn.isClosed() ) {
                    conn = DriverManager.getConnection(url, properties);
                } else {
                    return conn;
                }
            } catch (SQLException e) {
                e.printStackTrace();

            }
            return conn;
        }


}
