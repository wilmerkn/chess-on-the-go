package server.model;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Login {

    public static PreparedStatement st;
    public static ResultSet rs;

    public static boolean loginCheck(Connection c, String username, String password) {
        try {
            st = c.prepareStatement("SELECT * FROM login WHERE username = ?");
            st.setString(1, username);
            rs = st.executeQuery();

            if (rs.next()) {
                if (password.equals(rs.getString(2))) {
                    c.close();
                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "Wrong password or username, try again");
                    return false;
                }
            } else {
                JOptionPane.showMessageDialog(null, "Wrong password or username, try again");
                return false;
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return false;
    }
}

