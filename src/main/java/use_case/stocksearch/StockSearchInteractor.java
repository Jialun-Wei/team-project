package use_case.stocksearch;

import data.AlphaVantageAPI;
import data.AlphaVantageAPI.StockSearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StockSearchInteractor {
    private final AlphaVantageAPI api;

    public StockSearchInteractor(AlphaVantageAPI api) {
        this.api = api;
    }


    public StockSearchOutputData execute(StockSearchInputData input) {
        String keywords = input.getKeywords();

        if (keywords == null || keywords.isBlank()) {
            return new StockSearchOutputData(false, "Search keywords cannot be empty", new ArrayList<>());
        }

        try {
            List<StockSearchResult> results = api.searchStocks(keywords);
            if (results.isEmpty()) {
                return new StockSearchOutputData(false, "No results for \"" + keywords + "\"", new ArrayList<>());
            }
            return new StockSearchOutputData(true, "Search completed", results);
        } catch (IOException e) {
            return new StockSearchOutputData(false, "Network error: " + e.getMessage(), new ArrayList<>());
        } catch (Exception e) {
            return new StockSearchOutputData(false, "Error: " + e.getMessage(), new ArrayList<>());
        }
    }
}
