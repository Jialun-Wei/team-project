package use_case.trends;

import entity.Expense;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TrendsInteractor implements TrendsInputBoundary {

    private final TrendsDataAccess dataAccess;
    private final TrendsOutputBoundary presenter;

    public TrendsInteractor(TrendsDataAccess dataAccess, TrendsOutputBoundary presenter) {
        this.dataAccess = dataAccess;
        this.presenter = presenter;
    }

    @Override
    public void execute(TrendsInputData inputData) {
        List<Expense> expenses = dataAccess.getExpenses();

        Map<String, Double> totalExpenses = new HashMap<>();
        for (Expense expense : expenses) {
            if (inputData.getUsername().equals(expense.getUsername())) {
                String type = expense.getType();
                totalExpenses.put(type, expense.getAmount());
            }
        }

        TrendsOutputData outputData = new TrendsOutputData(totalExpenses);
        presenter.present(outputData);
    }
}
