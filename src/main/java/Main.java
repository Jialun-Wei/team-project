import interface_adapters.controllers.LoginController;
import interface_adapters.controllers.SignUpController;
import data.DataSourceFactory;
import data.JdbcUserRepository;
import data.SchemaInitializer;
import data.usecase5.InMemoryPortfolioRepository;
import data.usecase5.InMemoryPriceHistoryRepository;
import interface_adapters.use_case5.Presenter;
import interface_adapters.controllers.PortfolioController;
import interface_adapters.use_case5.PortfolioViewModel;
import data.AlphaVantageAPI;
import interface_adapters.controllers.StockSearchController;
import use_case.stocksearch.StockSearchInteractor;
import ui.*;
import use_case.login.LoginInteractor;
import use_case.portfolio.PortfolioInputBoundary;
import use_case.portfolio.PortfolioInteractor;
import use_case.signup.SignUpInteractor;

import javax.sql.DataSource;
import javax.swing.*;

public class Main {

    private static DataSource dataSource;
    private static RegisteredUserRepository userRepository;
    private static RegisteredExpenseRepository expenseRepository;

    private static SignUpController signUpController;
    private static LoginController loginController;
    private static DashboardController dashboardController;

    private static JFrame currentFrame;
    private static String currentUsername; // add a new global variable

    // New Add
    public static String getCurrentUsername() {
        return currentUsername;
    }

    // New Add
    private static void handleLoginSuccess(String username) {
        currentUsername = username;   // record the current username
        showDashboardView();          // open dashboard page
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Setup database
            dataSource = DataSourceFactory.sqlite("sqllite.db");
            TableInitializer.ensureSchema(dataSource);
            userRepository = new RegisteredUserRepository(dataSource);
            expenseRepository = new RegisteredExpenseRepository(dataSource);

            // Create interactors
            SignUpInteractor signUpInteractor = new SignUpInteractor(userRepository);
            LoginInteractor loginInteractor = new LoginInteractor(userRepository);

            // Create controllers
            signUpController = new SignUpController(signUpInteractor);
            loginController = new LoginController(loginInteractor);
            dashboardController = new DashboardController();

            // Start application on the login screen
            showLoginView();
        });
    }

    /** Displays the login window */
    private static void showLoginView() {
        if (currentFrame != null) currentFrame.dispose();

        LoginView loginView = new LoginView(
                loginController,
                Main::showSignUpView,       // callback switch to sign up
                Main::handleLoginSuccess    // callback open dashboard after login success
        );

        currentFrame = loginView;
        loginView.setVisible(true);
    }

    /** Displays the sign-up window */
    private static void showSignUpView() {
        if (currentFrame != null) currentFrame.dispose();

        SignUpView signUpView = new SignUpView(
                signUpController,
                Main::showLoginView    // callback to switch back
        );

        currentFrame = signUpView;
        signUpView.setVisible(true);
    }

    private static void showDashboardView(String username) {
        if (currentFrame != null) currentFrame.dispose();

        DashboardView dashboardView = new DashboardView(
                Main::showLoginView,         // Logout
                Main::showExpensesView,      // Track Expenses
                Main::showTrendsView,        // Financial Trends
                Main::showStockPricesView,   // Stock Prices
                Main::showInvestmentView,    // Simulated Investment
                Main::showPortfolioView,     // Portfolio Analysis
                Main::showNewsView           // Market News
        );

        currentFrame = dashboardView;
        dashboardView.setVisible(true);
    }

    private static void showNewsView() {
        // ToDo
    }

    private static void showPortfolioView() {
        // Use Case 5: Portfolio performance diagnostics
        if (currentFrame != null) currentFrame.dispose();

        // 1. create ViewModel
        PortfolioViewModel viewModel = new PortfolioViewModel();

        // 2. create Presenter（implement PortfolioOutputBoundary）
        Presenter presenter = new Presenter(viewModel);

        // 3. create Interactor（implement PortfolioInputBoundary）
        PortfolioInputBoundary interactor = new PortfolioInteractor(
                new InMemoryPortfolioRepository(),
                new InMemoryPriceHistoryRepository(),
                presenter
        );

        // 4. create Controller（dependent on InputBoundary + ViewModel）
        PortfolioController controller = new PortfolioController(interactor, viewModel);

        // 5. create View（dependent on Controller + username + dashboard）
        PortfolioView view = new PortfolioView(
                controller,
                currentUsername,
                Main::showDashboardView
        );

        currentFrame = view;
        view.setVisible(true);
    }

    private static void showStockPricesView() {
        if (currentFrame != null) currentFrame.dispose();

        AlphaVantageAPI api = new AlphaVantageAPI();
        StockSearchInteractor interactor = new StockSearchInteractor(api);
        StockSearchController controller = new StockSearchController(interactor);

        StockSearchView view = new StockSearchView(controller,
                currentUsername,
                Main::showDashboardView
        );

        currentFrame = view;
        view.setVisible(true);
    }

    private static void showInvestmentView() {
        // ToDo
    }

    private static void showTrendsView() {
        // ToDo
    }

    private static void showExpensesView() {
        // ToDo
    }
}