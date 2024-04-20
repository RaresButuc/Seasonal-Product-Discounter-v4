package com.codecool.seasonalproductdiscounter.service.transactions.repository;

import com.codecool.seasonalproductdiscounter.model.enums.Color;
import com.codecool.seasonalproductdiscounter.model.enums.Season;
import com.codecool.seasonalproductdiscounter.model.products.Product;
import com.codecool.seasonalproductdiscounter.model.transactions.Transaction;
import com.codecool.seasonalproductdiscounter.model.users.User;
import com.codecool.seasonalproductdiscounter.service.persistence.SqliteConnector;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransactionRepositoryImpl implements TransactionRepository {
    private final SqliteConnector sqliteConnector;

    public TransactionRepositoryImpl(SqliteConnector sqliteConnector) {
        this.sqliteConnector = sqliteConnector;
    }

    @Override
    public boolean add(Transaction transaction) {
        String sql = "INSERT INTO transactions(date,user_id,product_id,price_paid) VALUES(?,?,?,?)";

        try (Connection conn = sqliteConnector.getConnection()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, transaction.date().toString());
                pstmt.setInt(2, transaction.user().id());
                pstmt.setInt(3, transaction.product().id());
                pstmt.setDouble(4, transaction.pricePaid());
                pstmt.executeUpdate();
                System.out.println("Transaction was added to the DataBase!");
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public List<Transaction> getAll() {
        String sql =
                "SELECT *, users.id AS user_id, users.name AS user_name, users.password AS user_password, products.id AS product_id, products.name AS product_name, products.color AS product_color, products.season AS product_season, products.price AS product_price, products.sold AS product_sold " +
                        "FROM transactions " +
                        "INNER JOIN users ON transactions.user_id = users.id " +
                        "INNER JOIN products ON transactions.product_id = products.id";
        List<Transaction> transactionList = new ArrayList<>();

        try (Connection conn = sqliteConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            while (rs.next()) {
//                System.out.println(rs.getInt("id") + "\t" +
//                        rs.getString("name") + "\t" +
//                        rs.getDouble("capacity"));
                int transactionId = rs.getInt("id");
                LocalDate transactionDate = LocalDate.parse(rs.getString("date"));
                double pricePaid = rs.getDouble("price_paid");

                int userId = rs.getInt("user_id");
                String userName = rs.getString("user_name");
                String userPassword = rs.getString("user_password");

                int productId = rs.getInt("product_id");
                String productName = rs.getString("product_name");
                Color productColor = Color.valueOf(rs.getString("product_color"));
                Season productSeason = Season.valueOf(rs.getString("product_season"));
                double productPrice = rs.getDouble("product_price");
                boolean productSold = rs.getBoolean("product_sold");

                User user = new User(userId, userName, userPassword);
                Product product = new Product(productId, productName, productColor, productSeason, productPrice, productSold);

                Transaction transaction = new Transaction(transactionId, transactionDate, user, product, pricePaid);

                transactionList.add(transaction);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return transactionList;
    }

}
