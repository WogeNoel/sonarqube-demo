package de.demo.shop;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * LEGACY-CODE: Datenzugriff mit typischen Altlasten.
 * Wird absichtlich NICHT repariert, um "Clean as You Code" zu zeigen:
 * Das Quality Gate bewertet nur neuen Code.
 */
public class OrderRepository {

    private static final String DB_URL = "jdbc:postgresql://localhost:5432/shop";
    private static final String DB_USER = "shop";
    private static final String DB_PASSWORD = "geheim123";

    public List<String> findOrderIdsByCustomer(String customerName) throws SQLException {
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        Statement statement = connection.createStatement();
        ResultSet result = statement.executeQuery(
                "SELECT id FROM orders WHERE customer = '" + customerName + "'");
        List<String> ids = new ArrayList<>();
        while (result.next()) {
            ids.add(result.getString("id"));
        }
        return ids;
    }
}
