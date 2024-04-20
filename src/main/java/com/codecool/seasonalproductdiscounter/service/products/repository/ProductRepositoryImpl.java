package com.codecool.seasonalproductdiscounter.service.products.repository;

import com.codecool.seasonalproductdiscounter.model.enums.Color;
import com.codecool.seasonalproductdiscounter.model.enums.Season;
import com.codecool.seasonalproductdiscounter.model.products.Product;
import com.codecool.seasonalproductdiscounter.service.persistence.SqliteConnector;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductRepositoryImpl implements ProductRepository {
    private final SqliteConnector sqliteConnector;

    public ProductRepositoryImpl(SqliteConnector sqliteConnector) {
        this.sqliteConnector = sqliteConnector;
    }

    @Override
    public List<Product> getAvailableProducts() {
        String sql = "SELECT id, name, color, season,price, sold FROM products";
        List<Product> productsList = new ArrayList<>();

        try (Connection conn = sqliteConnector.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            // loop through the result set
            while (rs.next()) {
//                System.out.println(rs.getInt("id") + "\t" +
//                        rs.getString("name") + "\t" +
//                        rs.getDouble("capacity"));
                if (!rs.getBoolean("sold")) {
                    productsList.add(new Product(rs.getInt("id"), rs.getString("name"), Color.valueOf(rs.getString("color")), Season.valueOf(rs.getString("season")), rs.getDouble("price"), rs.getBoolean("sold")));
                }
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return productsList.stream().filter(e -> !e.sold()).toList();
    }

    @Override
    public boolean addProducts(List<Product> products) {
        String sql = "INSERT INTO products(name,color,season,price,sold) VALUES(?,?,?,?,?)";

        try (Connection conn = sqliteConnector.getConnection()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                for (Product product :
                        products) {
                    pstmt.setString(1, product.name());
                    pstmt.setString(2, String.valueOf(product.color()));
                    pstmt.setString(3, String.valueOf(product.season()));
                    pstmt.setDouble(4, product.price());
                    pstmt.setInt(5, 0);
                    pstmt.executeUpdate();
                }
                System.out.println("Products were added to the DataBase!");
                return true;
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

    @Override
    public boolean setProductAsSold(Product product) {
        String sql = "UPDATE products SET name = ? , "
                + "color = ? ,"
                + "season = ? ,"
                + "price = ? ,"
                + "sold = ? "
                + "WHERE id = ?";

        try (Connection conn = sqliteConnector.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // set the corresponding param
            pstmt.setString(1, product.name());
            pstmt.setString(2, String.valueOf(product.color()));
            pstmt.setString(3, String.valueOf(product.season()));
            pstmt.setDouble(4, product.price());
            pstmt.setInt(5, 1);
            pstmt.setInt(6, product.id());
            // update
            pstmt.executeUpdate();
            System.out.println("Product was set as Sold!");
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return false;
        }
    }
}
