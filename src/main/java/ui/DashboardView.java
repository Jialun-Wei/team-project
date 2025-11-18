package ui;

import controllers.DashboardController;
import data.ExpenseRepository;

import javax.swing.*;
import java.awt.*;

/**
 * The main dashboard screen shown after successful login.
 * Acts as a navigation hub to different use cases in the FinWise app.
 */
public class DashboardView extends JFrame {

    public DashboardView(
            Runnable onLogout,
            Runnable onTrackExpenses,
            Runnable onFinancialTrends,
            Runnable onStockPrices,
            Runnable onSimulatedInvestment,
            Runnable onPortfolioAnalysis,
            Runnable onMarketNews
    ) {
        setTitle("FinWise Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Layout Setup
        JPanel mainPanel = new JPanel(new BorderLayout());

    // Indices for tabs
    private static final int HOME_TAB = 0;
    private static final int NEWS_TAB = 1;
    private static final int TRACKER_TAB = 2;
    private static final int STOCK_TAB = 3;

        // Buttons for Use Cases
        JPanel buttonPanel = new JPanel(new GridLayout(3, 2, 10, 10));

        setTitle("Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 350);
        setLocationRelativeTo(null);

        // Top bar with greeting + logout
        JPanel topBar = new JPanel(new BorderLayout());
        JLabel greeting = new JLabel("Welcome, " + (username == null ? "User" : username) + "!");
        JButton logoutBtn = new JButton("Log out");
        logoutBtn.addActionListener(e -> {
            dispose();
            onLogout.run();
        });
        topBar.add(greeting, BorderLayout.WEST);
        topBar.add(logoutBtn, BorderLayout.EAST);

        // Tabs: Home (placeholder), News, Tracker, Stock
        tabs.addTab("Home", buildHomePanel());
        tabs.addTab("News", buildTabPlaceholder("Open the News window…"));
        tabs.addTab("Tracker", buildTabPlaceholder("Open the Tracker window…"));
        tabs.addTab("Stock", buildTabPlaceholder("Open the Stock window…"));

        // Logout Button
        JButton logoutBtn = new JButton("Logout");
        mainPanel.add(logoutBtn, BorderLayout.SOUTH);

        // Add action listeners (connect each button to its callback)
        logoutBtn.addActionListener(e -> {
            onLogout.run();
            dispose();
        });
        expensesBtn.addActionListener(e -> onTrackExpenses.run());
        trendsBtn.addActionListener(e -> onFinancialTrends.run());
        stockBtn.addActionListener(e -> onStockPrices.run());
        investBtn.addActionListener(e -> onSimulatedInvestment.run());
        portfolioBtn.addActionListener(e -> onPortfolioAnalysis.run());
        newsBtn.addActionListener(e -> onMarketNews.run());

        // Layout
        setLayout(new BorderLayout(8, 8));
        add(topBar, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    private JPanel buildHomePanel() {
        JPanel p = new JPanel(new BorderLayout());
        JTextArea info = new JTextArea("""
                This is your Dashboard Home.

                Use the tabs above:
                • News    → opens the News window
                • Tracker → opens the Tracker window
                • Stock   → opens the Stock window
                """);
        info.setEditable(false);
        info.setMargin(new Insets(8, 8, 8, 8));
        p.add(info, BorderLayout.CENTER);
        return p;
    }

    private JPanel buildTabPlaceholder(String text) {
        JPanel p = new JPanel(new GridBagLayout());
        p.add(new JLabel(text));
        return p;
    }
}

