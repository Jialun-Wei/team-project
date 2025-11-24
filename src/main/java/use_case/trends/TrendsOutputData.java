package use_case.trends;

import java.util.Map;

public class TrendsOutputData {

    private final Map<String, Double> typeTotals;

    public TrendsOutputData(Map<String, Double> typeTotals) {
        this.typeTotals = typeTotals;
    }

    public Map<String, Double> getTypeTotals() {
        return typeTotals;
    }
}
