package use_case.portfolio;

import data.usecase5.PortfolioRepository;
import data.usecase5.PriceHistoryRepository;
import entity.usecase5.Holding;
import entity.usecase5.PortfolioSnapshot;
import entity.usecase5.PricePoint;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Interactor for the Portfolio Analysis use case.
 *
 * Use Case layer
 */
public class PortfolioInteractor implements PortfolioInputBoundary {

    private final PortfolioRepository portfolioRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final PortfolioOutputBoundary outputBoundary;

    /**
     * @param portfolioRepository
     * @param priceHistoryRepository
     * @param outputBoundary
     */
    public PortfolioInteractor(PortfolioRepository portfolioRepository,
                               PriceHistoryRepository priceHistoryRepository,
                               PortfolioOutputBoundary outputBoundary) {
        this.portfolioRepository = portfolioRepository;
        this.priceHistoryRepository = priceHistoryRepository;
        this.outputBoundary = outputBoundary;
    }

    /**
     * Executes the portfolio analysis use case.
     * @param input the input data (e.g., username)
     */
    @Override
    public void execute(PortfolioInputData input) {
        String username = input.getUsername();

        // ===== Step 1: Load holdings for this user =====
        List<Holding> holdings = portfolioRepository.findHoldingsByUser(username);

        PortfolioOutputData output;

        if (holdings.isEmpty()) {
            // No investments: nothing to analyze
            output = new PortfolioOutputData(
                    Collections.emptyList(),   // snapshots
                    Collections.emptyList(),   // holdings
                    false,
                    "No data available: the user has no holdings."
            );
            outputBoundary.present(output);
            return;
        }

        // ===== Step 2: Choose a base date series from the first symbol =====
        Holding firstHolding = holdings.get(0);
        List<PricePoint> baseHistory =
                priceHistoryRepository.getPriceHistory(firstHolding.getSymbol());

        if (baseHistory.isEmpty()) {
            // No price data at all: cannot compute performance
            output = new PortfolioOutputData(
                    Collections.emptyList(),
                    holdings,
                    false,
                    "No historical price data available for analysis."
            );
            outputBoundary.present(output);
            return;
        }

        List<PortfolioSnapshot> snapshots = new ArrayList<>();

        // ===== Step 3: For each date, compute total cost & total value =====
        for (PricePoint basePoint : baseHistory) {
            LocalDate date = basePoint.getDate();
            double totalCost = 0.0;
            double totalValue = 0.0;

            for (Holding h : holdings) {
                // cost side: shares * avgCost (constant over time)
                totalCost += h.getTotalCost();

                // value side: shares * price on this date
                List<PricePoint> history = priceHistoryRepository.getPriceHistory(h.getSymbol());
                double priceOnDate = findPriceOnDate(history, date);
                totalValue += h.getShares() * priceOnDate;
            }

            // Create a snapshot (profit & profitRate derived inside)
            PortfolioSnapshot snapshot =
                    PortfolioSnapshot.fromCostAndValue(date, totalCost, totalValue);
            snapshots.add(snapshot);
        }

        // ===== Step 4: Build output data and pass to presenter =====
        output = new PortfolioOutputData(
                snapshots,
                holdings,
                true,
                "Portfolio analysis calculated successfully."
        );
        outputBoundary.present(output);
    }

    /**
     * Finds the price for the given date in the history list.
     * If no exact match is found, falls back to the latest available price.
     */
    private double findPriceOnDate(List<PricePoint> history, LocalDate date) {
        if (history.isEmpty()) {
            // No data at all: treat as zero price (or you can choose another strategy)
            return 0.0;
        }

        for (PricePoint p : history) {
            if (p.getDate().equals(date)) {
                return p.getPrice();
            }
        }

        // If we do not have this exact date, use the most recent price as a fallback.
        return history.get(history.size() - 1).getPrice();
    }
}


