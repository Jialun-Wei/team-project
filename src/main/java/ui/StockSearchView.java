package ui;

import data.AlphaVantageAPI;
import interface_adapters.controllers.StockSearchController;
import use_case.stocksearch.StockSearchOutputData;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

/**
 * Swing UI for Use Case 3: Stock Search + Quote + Time Series Chart.
 */
public class StockSearchView extends JFrame {

    private final StockSearchController controller;
    private final AlphaVantageAPI api;
    private final Runnable onBack;

    // top user + back
    private final JLabel userLabel = new JLabel();
    private final JButton backButton = new JButton("Back to Dashboard");

    // search + suggestions
    private final JTextField searchField = new JTextField();
    private final JList<AlphaVantageAPI.StockSearchResult> suggestionsList = new JList<>();
    private final JScrollPane suggestionsScroll = new JScrollPane(suggestionsList);

    // quote info
    private final JLabel companyNameLabel = new JLabel("Search for a stock");
    private final JLabel symbolLabel = new JLabel("");
    private final JLabel priceLabel = new JLabel("");
    private final JLabel changeLabel = new JLabel("");
    private final JButton refreshButton = new JButton("⟳");
    private final JButton watchButton = new JButton("♡");

    // time range buttons
    private final ButtonGroup rangeGroup = new ButtonGroup();
    private final JPanel rangePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

    // chart
    private final ChartPanel chartPanel = new ChartPanel();

    // status bar
    private final JLabel statusLabel = new JLabel("Enter keywords to search.");

    // state
    private String currentSymbol = "";
    private AlphaVantageAPI.StockSearchResult currentSelectedResult = null;
    private SwingWorker<?, ?> currentSearchWorker;
    private SwingWorker<?, ?> currentQuoteWorker;
    private SwingWorker<?, ?> currentSeriesWorker;

    public StockSearchView(StockSearchController controller,
                           String username,
                           Runnable onBack) {
        this.controller = controller;
        this.onBack = onBack;
        this.api = new AlphaVantageAPI();

        setTitle("FinWise — Live Stock Prices");
        setSize(1000, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        initUI(username);
        initListeners();
        showEmptyState();
    }


    private void initUI(String username) {
        JPanel topBar = new JPanel(new BorderLayout());
        JPanel leftTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JPanel rightTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        userLabel.setText("Logged in as: " + username);
        leftTop.add(userLabel);

        rightTop.add(backButton);

        topBar.add(leftTop, BorderLayout.WEST);
        topBar.add(rightTop, BorderLayout.EAST);

        JPanel searchCard = new JPanel();
        searchCard.setLayout(new BoxLayout(searchCard, BoxLayout.Y_AXIS));
        searchCard.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        searchCard.setBackground(Color.WHITE);

        searchField.setPreferredSize(new Dimension(300, 32));
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        searchField.putClientProperty("JComponent.sizeVariant", "regular");
        searchField.setToolTipText("Search stocks (e.g., AAPL, Tesla)...");

        suggestionsList.setVisibleRowCount(6);
        suggestionsList.setCellRenderer(new SuggestionCellRenderer());
        suggestionsScroll.setVisible(false);
        suggestionsScroll.setBorder(BorderFactory.createEmptyBorder());

        searchCard.add(searchField);
        searchCard.add(Box.createVerticalStrut(4));
        searchCard.add(suggestionsScroll);

        JPanel detailsCard = new JPanel();
        detailsCard.setLayout(new BoxLayout(detailsCard, BoxLayout.Y_AXIS));
        detailsCard.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        detailsCard.setBackground(Color.WHITE);

        companyNameLabel.setFont(companyNameLabel.getFont().deriveFont(Font.BOLD, 20f));
        symbolLabel.setFont(symbolLabel.getFont().deriveFont(Font.PLAIN, 14f));
        symbolLabel.setForeground(new Color(0x66, 0x70, 0x85));
        priceLabel.setFont(priceLabel.getFont().deriveFont(Font.BOLD, 24f));
        changeLabel.setFont(changeLabel.getFont().deriveFont(Font.PLAIN, 16f));

        JPanel headerLeft = new JPanel();
        headerLeft.setLayout(new BoxLayout(headerLeft, BoxLayout.Y_AXIS));
        headerLeft.setOpaque(false);
        headerLeft.add(companyNameLabel);
        headerLeft.add(symbolLabel);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        headerRight.setOpaque(false);
        headerRight.add(refreshButton);
        headerRight.add(watchButton);

        JPanel headerRow = new JPanel(new BorderLayout());
        headerRow.setOpaque(false);

        JPanel pricePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        pricePanel.setOpaque(false);
        pricePanel.add(priceLabel);
        pricePanel.add(changeLabel);

        headerRow.add(headerLeft, BorderLayout.WEST);
        headerRow.add(pricePanel, BorderLayout.CENTER);
        headerRow.add(headerRight, BorderLayout.EAST);

        detailsCard.add(headerRow);

        rangePanel.setOpaque(false);
        addRangeButton("1D", true);
        addRangeButton("1W", false);
        addRangeButton("1M", false);
        addRangeButton("3M", false);
        addRangeButton("1Y", false);
        addRangeButton("5Y", false);

        JPanel chartCard = new JPanel();
        chartCard.setLayout(new BorderLayout());
        chartCard.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        chartCard.setBackground(Color.WHITE);

        JLabel chartTitle = new JLabel("Price Chart");
        chartTitle.setFont(chartTitle.getFont().deriveFont(Font.BOLD, 16f));

        JPanel chartHeader = new JPanel(new BorderLayout());
        chartHeader.setOpaque(false);
        chartHeader.add(chartTitle, BorderLayout.WEST);
        chartHeader.add(rangePanel, BorderLayout.EAST);

        chartPanel.setPreferredSize(new Dimension(800, 360));
        chartPanel.setBackground(new Color(0xf7, 0xf8, 0xfb));

        chartCard.add(chartHeader, BorderLayout.NORTH);
        chartCard.add(chartPanel, BorderLayout.CENTER);

        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setBackground(new Color(0xf7, 0xf8, 0xfb));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        centerPanel.add(searchCard);
        centerPanel.add(Box.createVerticalStrut(12));
        centerPanel.add(detailsCard);
        centerPanel.add(Box.createVerticalStrut(12));
        centerPanel.add(chartCard);

        JPanel statusBar = new JPanel(new BorderLayout());
        statusBar.setBorder(BorderFactory.createEmptyBorder(4, 12, 4, 12));
        statusBar.add(statusLabel, BorderLayout.WEST);

        setLayout(new BorderLayout());
        add(topBar, BorderLayout.NORTH);
        add(new JScrollPane(centerPanel), BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    private void addRangeButton(String label, boolean selected) {
        JToggleButton btn = new JToggleButton(label);
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(4, 10, 4, 10));
        if (selected) {
            btn.setSelected(true);
            btn.setBackground(new Color(0x3b, 0x82, 0xf6));
            btn.setForeground(Color.WHITE);
        }
        btn.addActionListener(e -> onRangeSelected(label, btn));
        rangeGroup.add(btn);
        rangePanel.add(btn);
    }

    private void initListeners() {
        backButton.addActionListener(e -> {
            dispose();
            if (onBack != null) onBack.run();
        });

        searchField.getDocument().addDocumentListener(new DocumentListener() {
            @Override public void insertUpdate(DocumentEvent e) { onSearchTextChanged(); }
            @Override public void removeUpdate(DocumentEvent e) { onSearchTextChanged(); }
            @Override public void changedUpdate(DocumentEvent e) { onSearchTextChanged(); }
        });

        searchField.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && suggestionsList.getModel().getSize() > 0) {
                    AlphaVantageAPI.StockSearchResult sel = suggestionsList.getModel().getElementAt(0);
                    selectSuggestion(sel);
                } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    suggestionsScroll.setVisible(false);
                }
            }
        });

        suggestionsList.addMouseListener(new MouseAdapter() {
            @Override public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 1) {
                    AlphaVantageAPI.StockSearchResult sel = suggestionsList.getSelectedValue();
                    selectSuggestion(sel);
                }
            }
        });

        refreshButton.addActionListener(e -> {
            if (currentSelectedResult != null) {
                loadQuote(currentSelectedResult);
            }
        });

        watchButton.addActionListener(e -> {
            if ("♡".equals(watchButton.getText())) {
                watchButton.setText("♥");
            } else {
                watchButton.setText("♡");
            }
        });
    }

    private void onSearchTextChanged() {
        String text = searchField.getText().trim();
        if (text.isEmpty()) {
            suggestionsScroll.setVisible(false);
            suggestionsList.setListData(new AlphaVantageAPI.StockSearchResult[0]);
            statusLabel.setText("Enter keywords to search.");
            return;
        }
        performAsyncSearch(text);
    }

    private void performAsyncSearch(String query) {
        if (currentSearchWorker != null && !currentSearchWorker.isDone()) {
            currentSearchWorker.cancel(true);
        }

        statusLabel.setText("Searching for \"" + query + "\" ...");

        currentSearchWorker = new SwingWorker<StockSearchOutputData, Void>() {
            @Override
            protected StockSearchOutputData doInBackground() {
                return controller.search(query);
            }

            @Override
            protected void done() {
                if (isCancelled()) return;

                try {
                    StockSearchOutputData output = get();
                    if (!searchField.getText().trim().equals(query)) {
                        return;
                    }

                    statusLabel.setText(output.getMessage());

                    if (!output.isSuccess()) {
                        suggestionsList.setListData(new AlphaVantageAPI.StockSearchResult[0]);
                        suggestionsScroll.setVisible(true);
                        return;
                    }

                    List<AlphaVantageAPI.StockSearchResult> results = output.getResults();
                    suggestionsList.setListData(results.toArray(new AlphaVantageAPI.StockSearchResult[0]));
                    suggestionsScroll.setVisible(true);

                } catch (CancellationException ignored) {
                } catch (InterruptedException | ExecutionException e) {
                    statusLabel.setText("Search failed: " + e.getMessage());
                    JOptionPane.showMessageDialog(
                            StockSearchView.this,
                            "Search failed: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
                revalidate();
                repaint();
            }
        };

        currentSearchWorker.execute();
    }

    private void selectSuggestion(AlphaVantageAPI.StockSearchResult s) {
        if (s == null) return;
        searchField.setText(s.getSymbol());
        suggestionsList.setListData(new AlphaVantageAPI.StockSearchResult[0]);
        suggestionsScroll.setVisible(false);
        currentSelectedResult = s;
        loadQuote(s);
    }

    private void showEmptyState() {
        currentSymbol = "";
        currentSelectedResult = null;
        companyNameLabel.setText("Search for a stock");
        symbolLabel.setText("");
        priceLabel.setText("");
        changeLabel.setText("");
        chartPanel.setSeries(null);
        chartPanel.repaint();
    }

    private void loadQuote(AlphaVantageAPI.StockSearchResult result) {
        if (currentQuoteWorker != null && !currentQuoteWorker.isDone()) {
            currentQuoteWorker.cancel(true);
        }

        statusLabel.setText("Loading quote for " + result.getSymbol() + " ...");

        currentQuoteWorker = new SwingWorker<AlphaVantageAPI.StockQuote, Void>() {
            @Override
            protected AlphaVantageAPI.StockQuote doInBackground() {
                try {
                    return api.getQuote(result.getSymbol());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            protected void done() {
                if (isCancelled()) return;
                try {
                    AlphaVantageAPI.StockQuote quote = get();
                    companyNameLabel.setText(result.getName());
                    symbolLabel.setText(result.getSymbol() + " • " + result.getExchange());
                    priceLabel.setText(String.format("$%.2f", quote.getPrice()));

                    double change = quote.getChange();
                    double changePct = quote.getChangePercent();
                    changeLabel.setText(String.format("%+.2f (%+.2f%%)", change, changePct));
                    changeLabel.setForeground(change >= 0 ? new Color(0x22, 0xc5, 0x5e)
                            : new Color(0xef, 0x44, 0x44));

                    currentSymbol = result.getSymbol();

                    String range = getSelectedRangeOrDefault();
                    fetchAndRenderSeries(currentSymbol, range);

                    statusLabel.setText("Quote loaded for " + currentSymbol);

                } catch (CancellationException ignored) {
                } catch (InterruptedException | ExecutionException e) {
                    statusLabel.setText("Failed to load quote: " + e.getMessage());
                    JOptionPane.showMessageDialog(
                            StockSearchView.this,
                            "Failed to load quote: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        currentQuoteWorker.execute();
    }

    private String getSelectedRangeOrDefault() {
        for (AbstractButton b : java.util.Collections.list(rangeGroup.getElements())) {
            if (b.isSelected()) return b.getText();
        }
        return "1D";
    }

    private void onRangeSelected(String range, JToggleButton btn) {
        for (AbstractButton b : java.util.Collections.list(rangeGroup.getElements())) {
            if (b == btn) {
                b.setBackground(new Color(0x3b, 0x82, 0xf6));
                b.setForeground(Color.WHITE);
            } else {
                b.setBackground(null);
                b.setForeground(Color.BLACK);
            }
        }
        if (!currentSymbol.isEmpty()) {
            fetchAndRenderSeries(currentSymbol, range);
        }
    }

    private void fetchAndRenderSeries(String symbol, String range) {
        if (currentSeriesWorker != null && !currentSeriesWorker.isDone()) {
            currentSeriesWorker.cancel(true);
        }

        statusLabel.setText("Loading chart (" + range + ") for " + symbol + " ...");
        chartPanel.setSeries(null);
        chartPanel.repaint();

        currentSeriesWorker = new SwingWorker<List<AlphaVantageAPI.StockPriceData>, Void>() {
            @Override
            protected List<AlphaVantageAPI.StockPriceData> doInBackground() {
                try {
                    return api.getTimeSeries(symbol, range);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }

            @Override
            protected void done() {
                if (isCancelled()) return;
                try {
                    List<AlphaVantageAPI.StockPriceData> seriesData = get();
                    chartPanel.setSeries(seriesData);
                    chartPanel.repaint();
                    statusLabel.setText("Chart loaded for " + symbol + " (" + range + ")");
                } catch (CancellationException ignored) {
                } catch (InterruptedException | ExecutionException e) {
                    statusLabel.setText("Failed to load chart: " + e.getMessage());
                    JOptionPane.showMessageDialog(
                            StockSearchView.this,
                            "Failed to load chart: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        };

        currentSeriesWorker.execute();
    }

    private static class SuggestionCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {

            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            if (value instanceof AlphaVantageAPI.StockSearchResult) {
                AlphaVantageAPI.StockSearchResult item = (AlphaVantageAPI.StockSearchResult) value;

                String html =
                        "<html><b>" + escapeHtml(item.getName()) + "</b><br/>" +
                                "<span style='color:#667085;font-size:11px;'>" +
                                escapeHtml(item.getSymbol()) + " • " + escapeHtml(item.getExchange()) +
                                "</span></html>";
                setText(html);
            }

            return this;
        }

        private String escapeHtml(String s) {
            return s == null ? "" :
                    s.replace("&", "&amp;")
                            .replace("<", "&lt;")
                            .replace(">", "&gt;");
        }
    }

    private static class ChartPanel extends JPanel {
        private List<AlphaVantageAPI.StockPriceData> series;

        public void setSeries(List<AlphaVantageAPI.StockPriceData> series) {
            this.series = series;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (series == null || series.isEmpty()) {
                g.setColor(new Color(0x99, 0x99, 0x99));
                g.drawString("No chart data. Select a stock to view its price history.",
                        20, getHeight() / 2);
                return;
            }

            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = getWidth();
            int h = getHeight();
            int padding = 40;
            int bottomPadding = 30;

            double min = Double.MAX_VALUE;
            double max = Double.MIN_VALUE;
            for (AlphaVantageAPI.StockPriceData d : series) {
                double p = d.getPrice();
                min = Math.min(min, p);
                max = Math.max(max, p);
            }
            if (min == max) {
                min -= 1;
                max += 1;
            }

            g2.setColor(new Color(0xdd, 0xdd, 0xdd));
            int x0 = padding;
            int y0 = h - bottomPadding;
            int x1 = w - padding;
            int y1 = padding;

            g2.drawLine(x0, y0, x1, y0);
            g2.drawLine(x0, y0, x0, y1);

            int n = series.size();
            if (n > 1) {
                double xStep = (x1 - x0) * 1.0 / (n - 1);
                g2.setColor(new Color(0x3b, 0x82, 0xf6));
                int prevX = -1;
                int prevY = -1;
                for (int i = 0; i < n; i++) {
                    double price = series.get(i).getPrice();
                    double normalized = (price - min) / (max - min);
                    int x = (int) (x0 + i * xStep);
                    int y = (int) (y0 - normalized * (y0 - y1));
                    if (prevX >= 0) {
                        g2.drawLine(prevX, prevY, x, y);
                    }
                    prevX = x;
                    prevY = y;
                }
            }

            g2.setColor(new Color(0x66, 0x70, 0x85));
            String minStr = String.format("%.2f", min);
            String maxStr = String.format("%.2f", max);
            g2.drawString(minStr, 5, y0);
            g2.drawString(maxStr, 5, y1 + 5);

            g2.dispose();
        }
    }
}