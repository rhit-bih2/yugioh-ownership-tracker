package yot.ui;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.function.Consumer;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import yot.services.*;


public class LandingPage extends JPanel {
	private static String username;
	private static UserService userService;
	private static Consumer<String> loginFn;
	private static Consumer<String> switchPageFn;
	
    public LandingPage(Consumer<String> onLogin, Consumer<String> onSwitchPage, DatabaseConnectionService dbService) {
		userService = new UserService(dbService);
		loginFn = onLogin;
		switchPageFn = onSwitchPage;
    	
        setLayout(new GridBagLayout());
        setBackground(Theme.BG);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel shell = new JPanel();
        shell.setOpaque(false);
        shell.setLayout(new BoxLayout(shell, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Yugioh Card Ownership Tracker");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT);
        title.setAlignmentX(CENTER_ALIGNMENT);
        shell.add(title);
        shell.add(Box.createVerticalStrut(24));

        JPanel authWrap = new JPanel();
        authWrap.setOpaque(false);
        authWrap.setLayout(new BoxLayout(authWrap, BoxLayout.X_AXIS));
        JPanel authCard = UiFactory.panelCard();
        authCard.setLayout(new BoxLayout(authCard, BoxLayout.Y_AXIS));
        authCard.setPreferredSize(new Dimension(420, 430));
        authCard.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel authTitle = new JLabel("Sign in or register");
        authTitle.setFont(Theme.FONT_BOLD.deriveFont(24f));
        authTitle.setForeground(Theme.TEXT);
        authTitle.setAlignmentX(LEFT_ALIGNMENT);
        JLabel authSub = new JLabel("Username and password authentication");
        authSub.setForeground(Theme.MUTED);
        authSub.setAlignmentX(LEFT_ALIGNMENT);

        authCard.add(authTitle);
        authCard.add(Box.createVerticalStrut(4));
        authCard.add(authSub);
        authCard.add(Box.createVerticalStrut(18));
        authCard.add(UiFactory.formLabel("Username*"));
        authCard.add(Box.createVerticalStrut(6));
        JTextField usernameInput = UiFactory.input("");
        authCard.add(usernameInput);
        authCard.add(Box.createVerticalStrut(12));
        authCard.add(UiFactory.formLabel("Password"));
        authCard.add(Box.createVerticalStrut(6));
        JTextField passwordInput = UiFactory.passwordInput();
        authCard.add(passwordInput);
        authCard.add(Box.createVerticalStrut(18));

        JButton loginButton = UiFactory.primaryButton("Log In");
        loginButton.addActionListener(e -> login(usernameInput.getText(), passwordInput.getText()));
        JButton registerButton = UiFactory.outlineButton("Create New Account");
        registerButton.addActionListener(e -> register(usernameInput.getText(), passwordInput.getText()));
        authCard.add(UiFactory.fillButton(loginButton));
        authCard.add(Box.createVerticalStrut(8));
        authCard.add(UiFactory.fillButton(registerButton));

        authWrap.add(Box.createHorizontalGlue());
        authWrap.add(authCard);
        authWrap.add(Box.createHorizontalGlue());
        shell.add(authWrap);

        // Add bottom right button to switch to seller registration
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.add(Box.createHorizontalGlue());
        JButton sellerRegButton = UiFactory.softButton("Register as Seller");
        sellerRegButton.addActionListener(e -> switchPageFn.accept("sellerReg"));
        bottomPanel.add(sellerRegButton);
        bottomPanel.setBorder(new EmptyBorder(16, 0, 0, 0));
        shell.add(Box.createVerticalGlue());
        shell.add(bottomPanel);

        add(shell);
    }
    
    private void login(String username, String password) {
    	if (userService.login(username, password)) {
    		LandingPage.username = username;
    		loginFn.accept(username);
		} else {
			JOptionPane.showMessageDialog(null, "Invalid username or password.");
		}
    }
    
    private void register(String username, String password) {
    	if (userService.register(username, password)) {
    		LandingPage.username = username;
    		loginFn.accept(username);
		} else {
			JOptionPane.showMessageDialog(null, "Username already exists.");
		}
    }
    
    public String getUsername() {
    	return LandingPage.username;
    }
}
