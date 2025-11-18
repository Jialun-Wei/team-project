package interface_adapters.controllers;

import use_case.stocksearch.StockSearchInputData;
import use_case.stocksearch.StockSearchInteractor;
import use_case.stocksearch.StockSearchOutputData;

public class StockSearchController {
    private final StockSearchInteractor stockSearchInteractor;

    public StockSearchController(StockSearchInteractor stockSearchInteractor) {
        this.stockSearchInteractor = stockSearchInteractor;
    }


    public StockSearchOutputData search(String keywords) {
        StockSearchInputData input = new StockSearchInputData(keywords);
        return stockSearchInteractor.execute(input);
    }
}