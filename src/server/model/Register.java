package server.model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Register: Creates a query to the database when the user tries to register a new account
 * @version 1.0
 * @author wilmerknutas
 */

public class Register {

    public static PreparedStatement st;
    public static boolean register(Connection c, String username, String password, String country) {
        try {
            st = c.prepareStatement("INSERT INTO login (username, password, country) VALUES (?,?,?)");
            st.setString(1, username);
            st.setString(2, password);
            st.setString(3,country);
            st.executeUpdate();

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
}

