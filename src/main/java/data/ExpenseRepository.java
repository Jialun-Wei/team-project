package data;

import entity.Expense;

import java.util.List;

public interface ExpenseRepository {

    List<Expense> findByUsername(String username);
    void add(String username, String datetime, String type, double amount);
    double getTotalForUser(String username);
}
