package use_case.trends;

import entity.Expense;
import java.util.List;

public interface TrendsDataAccess {
    List<Expense> getExpenses();
}
