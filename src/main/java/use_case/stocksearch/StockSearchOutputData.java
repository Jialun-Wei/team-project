package use_case.stocksearch;

import data.AlphaVantageAPI.StockSearchResult;
import java.util.List;

public class StockSearchOutputData {
    private final boolean success;
    private final String message;
    private final List<StockSearchResult> results;


    public StockSearchOutputData(boolean success, String message, List<StockSearchResult> results) {
        this.success = success;
        this.message = message;
        this.results = results;
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<StockSearchResult> getResults() { return results; }
}
