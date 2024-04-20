package com.codecool.seasonalproductdiscounter.service.users;

import com.codecool.seasonalproductdiscounter.model.products.Product;
import com.codecool.seasonalproductdiscounter.model.users.User;
import com.codecool.seasonalproductdiscounter.service.persistence.SqliteConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepositoryImpl implements UserRepository {
    private final SqliteConnector sqliteConnector;

    public UserRepositoryImpl(SqliteConnector sqliteConnector) {
        this.sqliteConnector = sqliteConnector;
    }

    @Override
    public List<User> getUsers() {
        String sql = "SELECT id, name, password FROM users";
        List<User> usersList = new ArrayList<>();

        try (Connection conn = sqliteConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            while (rs.next()) {
//                System.out.println(rs.getInt("id") + "\t" +
//                        rs.getString("name") + "\t" +
//                        rs.getDouble("capacity"));
                usersList.add(new User(rs.getInt("id"), rs.getString("name"), rs.getString("password")));
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return usersList;
    }

    @Override
    public boolean addUsers(List<User> users) {
        String sql = "INSERT INTO users(name,password) VALUES(?,?)";

        try (Connection conn = sqliteConnector.getConnection()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (User user :
                        users) {
                    pstmt.setString(1, user.userName());
                    pstmt.setString(2, user.password());
                    pstmt.executeUpdate();
                }
                System.out.println("Users were added to the DataBase!");
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

}
