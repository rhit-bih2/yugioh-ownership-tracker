package yot.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.jasypt.properties.EncryptableProperties;

import yot.services.DatabaseConnectionService;
import yot.services.EncryptionService;
import yot.services.UserService;

public class MainFrame extends JFrame {
    private static final String PAGE_LANDING = "landing";
    private static final String PAGE_SELLER_REG = "sellerReg";
    private static final String PAGE_APP = "app";

    private final CardLayout rootLayout = new CardLayout();
    private final JPanel rootPanel = new JPanel(rootLayout);
    private AppNavigator navigator;
    private final List<JButton> navButtons = new ArrayList<>();
    
    private static DatabaseConnectionService dbService = null;

    public MainFrame() {
        super("Yugioh Card Ownership Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1240, 780);
        setLocationRelativeTo(null);

        rootPanel.setBackground(Theme.BG);
        
        // Initialize properties and database connection once
        Properties props = loadProperties();
        String serverUsername = props.getProperty("serverUsername");
        String serverPassword = props.getProperty("serverPassword");
        MainFrame.dbService = new DatabaseConnectionService(props.getProperty("serverName"), props.getProperty("databaseName"));
        
        if (!dbService.connect(serverUsername, serverPassword)) {
            JOptionPane.showMessageDialog(null, "Connection to database could not be made.");
        }
        
        LandingPage landingPage = new LandingPage(this::onLoginSuccess, this::switchPage, dbService);
        rootPanel.add(landingPage, PAGE_LANDING);
        
        SellerRegPage sellerRegPage = new SellerRegPage(this::onLoginSuccess, this::switchPage, dbService);
        rootPanel.add(sellerRegPage, PAGE_SELLER_REG);
        
        setContentPane(rootPanel);
        showLanding();
    }

    private void onLoginSuccess(String username) {
        boolean isSeller = new UserService(dbService).isSeller(username);
        CardLayout appLayout = new CardLayout();
        JPanel appContentPanel = new JPanel(appLayout);
        navigator = new AppNavigator(appLayout, appContentPanel, this::updateNavActive, dbService, username, isSeller);

        rootPanel.add(buildAppShell(navigator.getView(), isSeller), PAGE_APP);
        showAppPage(AppNavigator.PAGE_COLLECTIONS);
    }

    private JPanel buildAppShell(java.awt.Component content, boolean isSeller) {
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(Theme.BG);

        shell.add(buildSidebar(isSeller), BorderLayout.WEST);
        shell.add(content, BorderLayout.CENTER);
        return shell;
    }

    private JPanel buildSidebar(boolean isSeller) {
        JPanel side = new JPanel();
        side.setBackground(Theme.PANEL_ALT);
        side.setBorder(BorderFactory.createMatteBorder(0, 0, 0, 1, Theme.BORDER));
        side.setLayout(new BoxLayout(side, BoxLayout.Y_AXIS));
        side.setPreferredSize(new Dimension(210, 0));
        side.setBorder(new EmptyBorder(12, 0, 12, 0));

        JPanel brand = UiFactory.rowPanel();
        brand.setOpaque(false);
        brand.setBorder(new EmptyBorder(0, 12, 0, 12));
        JLabel title = new JLabel("Yugioh Tracker");
        title.setForeground(Theme.TEXT);
        title.setFont(Theme.FONT_BOLD.deriveFont(18f));
        brand.add(title);

        side.add(brand);
        side.add(Box.createVerticalStrut(16));

        int numPage = isSeller ? 5 : 4;
        JPanel navGroup = new JPanel(new GridLayout(numPage, 1, 0, 0));
        navGroup.setOpaque(false);
        navButtons.clear();
        navGroup.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30*numPage));
        navGroup.add(navButton("Collections", AppNavigator.PAGE_COLLECTIONS));
        navGroup.add(navButton("Trade", AppNavigator.PAGE_TRADE));
        navGroup.add(navButton("Card Library", AppNavigator.PAGE_LIBRARY));
        navGroup.add(navButton("Marketplace", AppNavigator.PAGE_MARKETPLACE));
        if (isSeller) {
            navGroup.add(navButton("My Listings", AppNavigator.PAGE_MY_LISTINGS));
        }
        side.add(navGroup);
        side.add(Box.createVerticalGlue());
        side.add(buildLogoutButton());
        side.add(Box.createVerticalStrut(8));

        return side;
    }

    private JButton navButton(String label, String pageId) {
        JButton button = new JButton(label);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setAlignmentX(JButton.LEFT_ALIGNMENT);
        button.setPreferredSize(new Dimension(0, 34));
        button.setFocusPainted(false);
        button.setForeground(Theme.MUTED);
        button.setBackground(Theme.PANEL_ALT);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder());
        button.setMargin(new java.awt.Insets(0, 0, 0, 0));
        button.setFont(Theme.FONT_BOLD);
        button.setRolloverEnabled(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(true);
        button.addActionListener(e -> showAppPage(pageId));
        navButtons.add(button);
        return button;
    }

    private void showLanding() {
        rootLayout.show(rootPanel, PAGE_LANDING);
    }

    private void switchPage(String pageId) {
        if ("landing".equals(pageId)) {
            rootLayout.show(rootPanel, PAGE_LANDING);
        } else if ("sellerReg".equals(pageId)) {
            rootLayout.show(rootPanel, PAGE_SELLER_REG);
        }
    }

    private static Properties loadProperties() {
        String binDir = System.getProperty("user.dir") + "/bin/";
        EncryptionService es = new EncryptionService();
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

    private void showAppPage(String pageId) {
        if (navigator == null) {
            showLanding();
            return;
        }
        rootLayout.show(rootPanel, PAGE_APP);
        navigator.show(pageId);
    }

    private void updateNavActive(String pageId) {
        for (JButton btn : navButtons) {
            btn.setForeground(Theme.MUTED);
            btn.setBackground(Theme.PANEL_ALT);
            if (btn.getText().contains("Collections") && AppNavigator.PAGE_COLLECTIONS.equals(pageId)) {
                setNavActive(btn);
            } else if (btn.getText().contains("Trade") && AppNavigator.PAGE_TRADE.equals(pageId)) {
                setNavActive(btn);
            } else if (btn.getText().contains("Library") && AppNavigator.PAGE_LIBRARY.equals(pageId)) {
                setNavActive(btn);
            } else if (btn.getText().contains("Listings") && AppNavigator.PAGE_MY_LISTINGS.equals(pageId)) {
                setNavActive(btn);
            } else if (btn.getText().contains("Marketplace") && AppNavigator.PAGE_MARKETPLACE.equals(pageId)) {
                setNavActive(btn);
            }
        }
    }

    private void setNavActive(JButton btn) {
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(77, 98, 180));
    }
    
    private JButton buildLogoutButton() {
        JButton logout = new JButton("Exit");
        logout.setHorizontalAlignment(SwingConstants.CENTER);
        logout.setAlignmentX(JButton.CENTER_ALIGNMENT);
        logout.setMaximumSize(new Dimension(186, 34));
        logout.setFocusPainted(false);
        logout.setForeground(Theme.DANGER);
        logout.setBackground(new Color(91, 45, 63));
        logout.setOpaque(true);
        logout.setBorderPainted(true);
        logout.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.DANGER),
                new EmptyBorder(6, 12, 6, 12)
        ));
        logout.setFont(Theme.FONT_BOLD);
        logout.setRolloverEnabled(false);
        logout.addActionListener(e -> {
        	MainFrame.dbService.disconnect();
            dispose();
        });
        return logout;
    }

}
