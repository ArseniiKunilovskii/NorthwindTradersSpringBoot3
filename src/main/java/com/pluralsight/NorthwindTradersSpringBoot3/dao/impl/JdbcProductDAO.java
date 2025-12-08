package com.pluralsight.NorthwindTradersSpringBoot3.dao.impl;

import com.pluralsight.NorthwindTradersSpringBoot3.dao.interfaces.IProductDao;
import com.pluralsight.NorthwindTradersSpringBoot3.models.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcProductDAO implements IProductDao {

    private DataSource dataSource;

    @Autowired
    public JdbcProductDAO(DataSource dataSource) {
        this.dataSource = dataSource;
        initialize(); // Initialize database tables and data on startup.
    }

    private void initialize() {
        // This method sets up the database table and populates it with initial data if necessary.
        try (Connection connection = dataSource.getConnection()) {
            // SQL statement to create a Products table if it does not exist.
            String createTableQuery = "CREATE TABLE IF NOT EXISTS Products (" +
                    "Product_id INT PRIMARY KEY AUTO_INCREMENT," +
                    "amount DECIMAL(10, 2) NOT NULL," +
                    "vendor VARCHAR(255) NOT NULL" +
                    ")";
            try (PreparedStatement createTableStatement = connection.prepareStatement(createTableQuery)) {
                createTableStatement.execute(); // Execute the table creation query.
            }

            // Check if the table has any data already.
            String countQuery = "SELECT COUNT(*) AS rowCount FROM Products";
            try (PreparedStatement countStatement = connection.prepareStatement(countQuery);
                 ResultSet resultSet = countStatement.executeQuery()) {
                if (resultSet.next() && resultSet.getInt("rowCount") == 0) {
                    // Insert initial data if the table is empty.
                    String insertDataQuery = "INSERT INTO Products (amount, vendor) VALUES (?, ?)";
                    try (PreparedStatement insertDataStatement = connection.prepareStatement(insertDataQuery)) {
                        // Insert first Product.
                        insertDataStatement.setDouble(1, 2000.00);
                        insertDataStatement.setString(2, "Raymond");
                        insertDataStatement.executeUpdate();

                        // Insert second Product.
                        insertDataStatement.setDouble(1, 2500.00);
                        insertDataStatement.setString(2, "John");
                        insertDataStatement.executeUpdate();

                        // Insert third Product.
                        insertDataStatement.setDouble(1, 4000.00);
                        insertDataStatement.setString(2, "Jane");
                        insertDataStatement.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log or handle the SQL exception.
        }
    }

    @Override
    public Product insert(Product product) {
        // This method adds a new Product to the database.
        String insertDataQuery = "INSERT INTO Products (ProductName, CategoryID, UnitPrice) VALUES(?, ?, ?)\n ";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement insertStatement = connection.prepareStatement(insertDataQuery, Statement.RETURN_GENERATED_KEYS)) {
            // Setting parameters for the insert query.
            insertStatement.setString(1, product.getProductName());
            insertStatement.setInt(2, product.getCategoryId());
            insertStatement.setDouble(3, product.getUnitPrice());
            int affectedRows = insertStatement.executeUpdate(); // Execute the insert query.

            if (affectedRows == 0) {
                throw new SQLException("Creating Product failed, no rows affected.");
            }

            try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int generatedId = generatedKeys.getInt(1);
                    product.setProductId(generatedId);
                } else {
                    throw new SQLException("Creating Product failed, no ID obtained.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log or handle the SQL exception.
        }

        return product;
    }

    @Override
    public List<Product> getAll() {
        // This method retrieves all Products from the database.
        List<Product> Products = new ArrayList<>();
        String getAllQuery = "SELECT * FROM Products";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(getAllQuery);
             ResultSet resultSet = selectStatement.executeQuery()) {
            while (resultSet.next()) {
                // Extract data from each row in the result set.
                int productId = resultSet.getInt(1);
                String name = resultSet.getString(2);
                int category = resultSet.getInt(3);
                double price = resultSet.getDouble(4);

                // Create a Product object and add it to the list.
                Products.add(new Product(productId, name,category,price));
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log or handle the SQL exception.
        }
        return Products; // Return the list of Products.
    }

    @Override
    public Product getById(int productId) {
        // This method retrieves a specific Product by its ID.
        Product product = null;
        String getByIdQuery = "SELECT * FROM Products WHERE Product_id = ?";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement selectStatement = connection.prepareStatement(getByIdQuery)) {
            selectStatement.setInt(1, productId ); // Set the ID parameter in the query.
            try (ResultSet resultSet = selectStatement.executeQuery()) {
                if (resultSet.next()) {
                    // Extract data from the result set.
                    String name = resultSet.getString(2);
                    int category = resultSet.getInt(3);
                    double price = resultSet.getDouble(4);
                    // Create a Product object.
                    product = new Product(productId, name,category,price);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Log or handle the SQL exception.
        }
        return product; // Return the found Product or null.
    }


    @Override
    public void update(int ProductId, Product product) {
        // This method updates an existing Product in the database.
        String updateDataQuery = "UPDATE Products SET ProductName = ?, categoryId = ?, UnitPrice = ? WHERE Product_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement updateStatement = connection.prepareStatement(updateDataQuery)) {
            // Setting parameters for the update query.
            updateStatement.setString(1, product.getProductName());
            updateStatement.setInt(2, product.getCategoryId());
            updateStatement.setDouble(3, product.getUnitPrice());
            updateStatement.executeUpdate(); // Execute the update query.
        } catch (SQLException e) {
            e.printStackTrace(); // Log or handle the SQL exception.
        }
    }

    @Override
    public void delete(int ProductId) {
        // This method deletes a Product from the database.
        String deleteDataQuery = "DELETE FROM Products WHERE Product_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement deleteStatement = connection.prepareStatement(deleteDataQuery)) {
            deleteStatement.setInt(1, ProductId); // Set the ID parameter in the delete query.
            deleteStatement.executeUpdate(); // Execute the delete query.
        } catch (SQLException e) {
            e.printStackTrace(); // Log or handle the SQL exception.
        }
    }
}
