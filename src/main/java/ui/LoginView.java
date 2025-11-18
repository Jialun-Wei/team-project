package ui;

import interface_adapters.controllers.LoginController;
import use_case.login.LoginOutputData;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

/**
 * The login window that allows the user to log into the system or navigate to the sign-up screen.
 * This class belongs to the View layer (UI) in Clean Architecture.
 * It communicates with the LoginController and triggers a callback to the Dashboard view upon successful login.
 */
public class LoginView extends JFrame {

    private final JTextField usernameField = new JTextField();
    private final JPasswordField passwordField = new JPasswordField();
    private final JButton loginButton = new JButton("Login");
    private final JButton signUpButton = new JButton("Sign Up");

    /**
     * @param loginController the controller that handles login logic
     * @param showSignUpView  callback that opens the SignUpView
     * @param onLoginSuccess  callback that opens the Dashboard after successful login
     */
    public LoginView(LoginController loginController, Runnable showSignUpView, Consumer<String> onLoginSuccess) {
        setTitle("Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 250);
        setLocationRelativeTo(null);

        // Layout Setup
        JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
        panel.add(new JLabel("Username:"));
        panel.add(usernameField);
        panel.add(new JLabel("Password:"));
        panel.add(passwordField);
        panel.add(loginButton);
        panel.add(signUpButton);
        add(panel);

        // Login Button Action
        loginButton.addActionListener(e -> {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());  // Convert password to plain text

            LoginOutputData output = loginController.login(username, password);
            JOptionPane.showMessageDialog(this, output.getMessage());

            if (output.isSuccess()) {
                dispose();                        // close the current Login window
                onLoginSuccess.accept(username);  // trigger the callback defined
            }
        });

        // Sign-Up Button Action
        signUpButton.addActionListener(e -> {
            showSignUpView.run();    // trigger the callback to open SignUpView
            dispose();               // close the current window
        });

    }
}
