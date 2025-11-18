package data.usecase5;

// 股票拿历史价格
import entity.usecase5.PricePoint;
import java.util.List;

/**
 * Data access interface for historical price data of a stock.
 */
public interface PriceHistoryRepository {

    /**
     * Returns the historical price series for the given stock symbol.
     *
     * @param symbol stock ticker symbol, e.g. "AAPL"
     * @return list of price points ordered by date (oldest → newest).
     */
    List<PricePoint> getPriceHistory(String symbol);
}

