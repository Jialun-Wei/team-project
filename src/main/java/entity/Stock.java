package entity;

public class Stock {
    private final String symbol;
    private final String name;
    private final String exchange;
    private final double price;
    private final double changePercent;
    private final double changeDollar;

    public Stock(String symbol, String name, String exchange,
                 double price, double changeDollar, double changePercent) {
        this.symbol = symbol;
        this.name = name;
        this.exchange = exchange;
        this.price = price;
        this.changeDollar = changeDollar;
        this.changePercent = changePercent;
    }

    public String getSymbol() { return symbol; }

    public String getName() { return name; }

    public String getExchange() { return exchange; }
    public double getPrice() { return price; }
    public double getChangePercent() { return changePercent; }
    public double getChangeDollar() { return changeDollar; }
}