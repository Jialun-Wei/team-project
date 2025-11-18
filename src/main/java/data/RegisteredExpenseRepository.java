package data;

import entity.Expense;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegisteredExpenseRepository implements ExpenseRepository {

    private final DataSource dataSource;

    public RegisteredExpenseRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Expense> findByUsername(String username) {
        String query = "SELECT * FROM expenses WHERE username = ?";
        List<Expense> expenses = new ArrayList<>();

        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    long id = resultSet.getLong("id");
                    String u = resultSet.getString("username");
                    String dt = resultSet.getString("datetime");
                    String t = resultSet.getString("type");
                    double a = resultSet.getDouble("amount");
                    expenses.add(new Expense(id, u, dt, t, a));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByUsername expense failed!", e);
        } return expenses;
    }

    @Override
    public void add(String username, String datetime, String type, double amount) {
        String query = "INSERT INTO expenses (username, datetime, type, amount) VALUES (?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, datetime);
            preparedStatement.setString(3, type);
            preparedStatement.setDouble(4, amount);
            preparedStatement.executeUpdate();
        }  catch (SQLException e) {
            throw new RuntimeException("add expense failed!", e);
        }
    }

    @Override
    public double getTotalForUser(String username) {
        String query = "SELECT SUM(amount) FROM expenses WHERE username = ?";
        try (Connection connection = dataSource.getConnection(); PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, username);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getDouble(1);
                } return 0.0;
            }
        } catch (SQLException e) {
            throw new RuntimeException("get totalForUser expense failed!", e);
        }
    }
}
