package yot.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import yot.services.DatabaseConnectionService;

public class MainFrame extends JFrame {
    private static final String PAGE_LANDING = "landing";
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
        
        LandingPage landingPage = new LandingPage(this::onLoginSuccess);
        MainFrame.dbService = landingPage.getdbService();
        rootPanel.add(landingPage, PAGE_LANDING);
        setContentPane(rootPanel);
        showLanding();
    }

    private void onLoginSuccess(String username) {
        CardLayout appLayout = new CardLayout();
        JPanel appContentPanel = new JPanel(appLayout);
        navigator = new AppNavigator(appLayout, appContentPanel, this::updateNavActive, dbService, username);

        rootPanel.add(buildAppShell(navigator.getView()), PAGE_APP);
        showAppPage(AppNavigator.PAGE_COLLECTIONS);
    }

    private JPanel buildAppShell(java.awt.Component content) {
        JPanel shell = new JPanel(new BorderLayout());
        shell.setBackground(Theme.BG);

        shell.add(buildSidebar(), BorderLayout.WEST);
        shell.add(content, BorderLayout.CENTER);
        return shell;
    }

    private JPanel buildSidebar() {
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

        int numPage = 3;
        JPanel navGroup = new JPanel(new GridLayout(numPage, 1, 0, 0));
        navGroup.setOpaque(false);
        navButtons.clear();
        navGroup.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30*numPage));
        navGroup.add(navButton("Collections", AppNavigator.PAGE_COLLECTIONS));
        navGroup.add(navButton("Trade", AppNavigator.PAGE_TRADE));
        navGroup.add(navButton("Card Library", AppNavigator.PAGE_LIBRARY));
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
            }
        }
    }

    private void setNavActive(JButton btn) {
        btn.setForeground(Color.WHITE);
        btn.setBackground(new Color(77, 98, 180));
    }
    
    private JButton buildLogoutButton() {
        JButton logout = new JButton("Log Out");
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
