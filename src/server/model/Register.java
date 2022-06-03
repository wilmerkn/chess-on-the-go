package server.model;

import javax.swing.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Register {

    public static PreparedStatement st;
    public static ResultSet rs;
    //ska lägga till lite kontrollering så att det inte är tomt på username och password..
    //lägger till constraints i DB också
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

