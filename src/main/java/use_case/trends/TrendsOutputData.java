package use_case.trends;

import java.time.LocalDate;
import java.util.Map;

/**
 * Output data for the Trends use case.
 * The View layer will use this data to render a line graph.
 */
public class TrendsOutputData {

    private final Map<LocalDate, Map<String, Double>> typeTotals;

    /**
     * @param typeTotals the expenses, indexed by date and type.
     */
    public TrendsOutputData(Map<LocalDate, Map<String, Double>> typeTotals) {
        this.typeTotals = typeTotals;
    }

    /**
     * @return the expense totals, indexed by date and type.
     */
    public Map<LocalDate, Map<String, Double>> getTypeTotals() {
        return typeTotals;
    }
}
