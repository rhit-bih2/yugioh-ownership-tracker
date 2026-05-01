package yot.ui;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;

import javax.swing.JTextField;

import yot.services.*;

public class LandingPage extends JPanel {
	private static String serverUsername = null;
	private static String serverPassword = null;
	private static DatabaseConnectionService dbService = null;
	private static EncryptionService es = new EncryptionService();
	private static UserService userService;
	private static Runnable loginFn;
	
    public LandingPage(Runnable onLogin) {
    	Properties props = loadProperties();
    	serverUsername = props.getProperty("serverUsername");
		serverPassword = props.getProperty("serverPassword");
		dbService = new DatabaseConnectionService(props.getProperty("serverName"), props.getProperty("databaseName"));
		userService = new UserService(dbService);
		loginFn = onLogin;
    	
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
        authCard.add(UiFactory.formLabel("Username"));
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

        add(shell);
        
        if (!dbService.connect(serverUsername, serverPassword)) {
			JOptionPane.showMessageDialog(null, "Connection to database could not be made.");
		}
    }
    
    private void login(String username, String password) {
    	if (userService.login(username, password)) {
    		loginFn.run();
		} else {
			System.out.println("Invalid username or password");
		}
    }
    
    private void register(String username, String password) {
    	if (userService.register(username, password)) {
    		loginFn.run();
		} else {
			System.out.println("Invalid username");
		}
    }
    
    public static Properties loadProperties() {
		String binDir = System.getProperty("user.dir") + "/bin/";
		StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
		encryptor.setPassword(es.getEncryptionPassword());
		FileInputStream fis = null;
		EncryptableProperties props = new EncryptableProperties(encryptor);
		try {
			fis = new FileInputStream(binDir + "yot.properties");
			props.load(fis);
		} catch (FileNotFoundException e) {
			System.out.println("yot.properties file not found");
			e.printStackTrace();
			System.exit(1);
		} catch (IOException e) {
			System.out.println("yot.properties file could not be opened");
			e.printStackTrace();
			System.exit(1);
		}
		finally {
			if (fis!=null) {
				try {
					fis.close();
				} catch (IOException e) {
					System.out.println("Input Stream could not be closed.");
					e.printStackTrace();
				}
			}
		}
		return props;
	}
}
