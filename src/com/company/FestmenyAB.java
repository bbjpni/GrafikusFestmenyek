package com.company;

import java.sql.*;
import java.util.ArrayList;

public class FestmenyAB {
        Connection conn;

        public FestmenyAB() throws SQLException {
            String url = "jdbc:mysql://localhost:3306/java";
            String user = "root";
            String pass = "";

            conn = DriverManager.getConnection(url, user, pass);
        }

        public ArrayList<Festmeny> getFestmenyek() throws SQLException {
            ArrayList<Festmeny> festmenyek = new ArrayList<>();
            Statement st = conn.createStatement();
            ResultSet result = st.executeQuery("SELECT * FROM festmenyek");
            while (result.next()){
                int id = result.getInt("id");
                String szerzo = result.getString("szerzo");
                int datum = result.getInt("datum");
                boolean display = result.getInt("megtekintheto") > 0;
                String cim = result.getString("cim");
                Festmeny f = new Festmeny(id,szerzo,display,datum,cim);
                festmenyek.add(f);
            }
            return festmenyek;
        }

        public int deleteFestmeny(int id) throws SQLException {
            PreparedStatement st = conn.prepareStatement("DELETE FROM festmenyek WHERE id = ?");
            st.setInt(1,id);
            return st.executeUpdate();
        }

        public int insertFestmeny(String szerzo, boolean display, int ev, String cim) throws SQLException {
            String sql = "INSERT INTO festmenyek(datum, cim, szerzo, megtekintheto) VALUES(?,?,?,?)";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setInt(1,ev);
            st.setString(2,cim);
            st.setString(3,szerzo);
            st.setInt(4,display ? 1 : 0);
            return st.executeUpdate();
        }

        public int updateFestmeny(int id, String szerzo, boolean display, int ev, String cim) throws SQLException {
            String sql = "UPDATE festmenyek SET szerzo = ?, datum = ?, cim = ?, megtekintheto = ? WHERE id = ?";
            PreparedStatement st = conn.prepareStatement(sql);
            st.setString(1,szerzo);
            st.setInt(2,ev);
            st.setString(3,cim);
            st.setInt(4,display ? 1 : 0);
            st.setInt(5,id);
            return st.executeUpdate();
        }
}
