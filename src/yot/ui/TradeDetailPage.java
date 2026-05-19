package yot.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import yot.services.DatabaseConnectionService;

public class TradeDetailPage extends JPanel {

    private final DatabaseConnectionService dbService;
    private String currentUsername;
    private Integer currentTradeId;

    private final JLabel senderUsernameLabel;
    private final JLabel receiverUsernameLabel;
    private final JLabel partnerConfirmStatusLabel;
    private final JButton confirmButton;
    private final JButton abortButton;
    private final JLabel currentUserConfirmStatusLabel;

    private Runnable backAction;
    private final JButton backBtn;

    public TradeDetailPage(Runnable onBack, DatabaseConnectionService dbService, String username) {
        this.dbService = dbService;
        this.backAction = onBack;
        this.currentUsername = username;

        JPanel page = UiFactory.pageContainer();

        // ── Top bar ───────────────────────────────────────────────────────────
        JPanel top = UiFactory.rowPanel();
        top.setAlignmentX(LEFT_ALIGNMENT);
        top.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Trade Detail");
        titleLabel.setFont(Theme.FONT_PAGE);
        titleLabel.setForeground(Theme.TEXT);

        JLabel sub = new JLabel("Review and manage your trade request.");
        sub.setForeground(Theme.MUTED);

        header.add(titleLabel);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);

        backBtn = UiFactory.outlineButton("← Back to Trade");
        backBtn.addActionListener(e -> {
            if (backAction != null) backAction.run();
        });

        top.add(header);
        top.add(Box.createHorizontalGlue());
        top.add(backBtn);
        page.add(top);
        page.add(Box.createVerticalStrut(14));

        // ── Trade Metadata Panel ──────────────────────────────────────────────
        JPanel metadataCard = UiFactory.panelCard();
        metadataCard.setLayout(new BoxLayout(metadataCard, BoxLayout.Y_AXIS));
        metadataCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        metadataCard.setAlignmentX(LEFT_ALIGNMENT);
        metadataCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        metadataCard.add(UiFactory.sectionTitle("Trade Metadata"));
        metadataCard.add(Box.createVerticalStrut(14));

        // Sender username row
        senderUsernameLabel = styledValue();
        metadataCard.add(infoRow("Sender", senderUsernameLabel));
        metadataCard.add(Box.createVerticalStrut(12));

        // Receiver username row
        receiverUsernameLabel = styledValue();
        metadataCard.add(infoRow("Receiver", receiverUsernameLabel));
        metadataCard.add(Box.createVerticalStrut(20));

        // Partner confirmation status
        metadataCard.add(styledFormLabel("Trade Partner Status"));
        metadataCard.add(Box.createVerticalStrut(6));
        partnerConfirmStatusLabel = new JLabel("—");
        partnerConfirmStatusLabel.setForeground(Theme.MUTED);
        partnerConfirmStatusLabel.setFont(Theme.FONT.deriveFont(15f));
        partnerConfirmStatusLabel.setAlignmentX(LEFT_ALIGNMENT);
        metadataCard.add(partnerConfirmStatusLabel);
        metadataCard.add(Box.createVerticalStrut(20));

        // Current user confirmation status
        metadataCard.add(styledFormLabel("Your Confirmation Status"));
        metadataCard.add(Box.createVerticalStrut(6));
        currentUserConfirmStatusLabel = new JLabel("—");
        currentUserConfirmStatusLabel.setForeground(Theme.ACCENT);
        currentUserConfirmStatusLabel.setFont(Theme.FONT_BOLD.deriveFont(15f));
        currentUserConfirmStatusLabel.setAlignmentX(LEFT_ALIGNMENT);
        metadataCard.add(currentUserConfirmStatusLabel);
        metadataCard.add(Box.createVerticalStrut(20));

        // Action buttons panel
        JPanel buttonRow = UiFactory.rowPanel();
        buttonRow.setAlignmentX(LEFT_ALIGNMENT);
        buttonRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        confirmButton = UiFactory.primaryButton("Confirm Trade");
        confirmButton.setMaximumSize(new Dimension(200, 36));
        confirmButton.setMinimumSize(new Dimension(200, 36));
        confirmButton.setPreferredSize(new Dimension(200, 36));
        confirmButton.addActionListener(e -> handleConfirmTrade());

        abortButton = UiFactory.dangerButton("Abort Trade");
        abortButton.setMaximumSize(new Dimension(200, 36));
        abortButton.setMinimumSize(new Dimension(200, 36));
        abortButton.setPreferredSize(new Dimension(200, 36));
        abortButton.addActionListener(e -> handleAbortTrade());

        buttonRow.add(confirmButton);
        buttonRow.add(Box.createHorizontalStrut(12));
        buttonRow.add(abortButton);
        buttonRow.add(Box.createHorizontalGlue());
        metadataCard.add(buttonRow);

        metadataCard.add(Box.createVerticalGlue());
        page.add(metadataCard);
        page.add(Box.createVerticalGlue());

        setLayout(new BorderLayout());
        setOpaque(false);
        add(UiFactory.scrollWrap(page), BorderLayout.CENTER);
    }

    /**
     * Load the trade detail page with the given trade ID and current user.
     * This method should fetch trade data from the database and populate the UI.
     */
    public void loadTradeDetail(Integer tradeId) {
        this.currentTradeId = tradeId;

        // Reset UI to loading state
        senderUsernameLabel.setText("Loading…");
        receiverUsernameLabel.setText("Loading…");
        partnerConfirmStatusLabel.setText("Loading…");
        currentUserConfirmStatusLabel.setText("Loading…");
        confirmButton.setEnabled(true);
        confirmButton.setText("Confirm Trade");
        confirmButton.setBackground(Theme.ACCENT);
        confirmButton.setForeground(java.awt.Color.WHITE);
        abortButton.setEnabled(true);
        abortButton.setText("Abort Trade");

        // TODO: Fetch trade data from database using TradeDetailService
        // For now, placeholder implementation
        new Thread(() -> {
            // Simulated data fetch - replace with actual service call
            // String[] tradeData = tradeDetailService.getTradeDetail(tradeId);
            
            SwingUtilities.invokeLater(() -> {
                // TODO: Replace with actual data
                senderUsernameLabel.setText("sender_username");
                receiverUsernameLabel.setText("receiver_username");
                
                // Check if current user is confirmed
                boolean currentUserConfirmed = false; // TODO: Get from database
                if (currentUserConfirmed) {
                    confirmButton.setEnabled(false);
                    confirmButton.setText("Confirmed");
                    confirmButton.setBackground(new Color(60, 70, 100));
                    confirmButton.setForeground(Theme.MUTED);
                    currentUserConfirmStatusLabel.setText("You have confirmed this trade");
                    currentUserConfirmStatusLabel.setForeground(Theme.ACCENT_ALT);
                } else {
                    currentUserConfirmStatusLabel.setText("You have not confirmed");
                    currentUserConfirmStatusLabel.setForeground(Theme.MUTED);
                }
                
                // Check if trade partner is confirmed
                boolean partnerConfirmed = false; // TODO: Get from database
                if (partnerConfirmed) {
                    partnerConfirmStatusLabel.setText("Trade partner has confirmed");
                    partnerConfirmStatusLabel.setForeground(Theme.ACCENT_ALT);
                } else {
                    partnerConfirmStatusLabel.setText("Trade partner has not confirmed");
                    partnerConfirmStatusLabel.setForeground(Theme.MUTED);
                }
                
                revalidate();
                repaint();
            });
        }).start();
    }

    private void handleConfirmTrade() {
        if (currentTradeId == null || currentUsername == null) return;

        confirmButton.setEnabled(false);
        confirmButton.setText("Confirming…");

        new Thread(() -> {
            // TODO: Call service to update trade confirmation status in database
            // tradeDetailService.confirmTrade(currentTradeId, currentUsername);

            SwingUtilities.invokeLater(() -> {
                confirmButton.setEnabled(false);
                confirmButton.setText("Confirmed");
                confirmButton.setBackground(new Color(60, 70, 100));
                confirmButton.setForeground(Theme.MUTED);
                currentUserConfirmStatusLabel.setText("You have confirmed this trade");
                currentUserConfirmStatusLabel.setForeground(Theme.ACCENT_ALT);
                revalidate();
                repaint();
            });
        }).start();
    }

    private void handleAbortTrade() {
        if (currentTradeId == null || currentUsername == null) return;

        abortButton.setEnabled(false);
        abortButton.setText("Aborting…");

        new Thread(() -> {
            // TODO: Call service to abort the trade in database
            // tradeDetailService.abortTrade(currentTradeId, currentUsername);

            SwingUtilities.invokeLater(() -> {
                abortButton.setEnabled(false);
                abortButton.setText("Trade Aborted");
                abortButton.setBackground(new Color(60, 70, 100));
                abortButton.setForeground(Theme.MUTED);
                confirmButton.setEnabled(false);
                revalidate();
                repaint();
            });
        }).start();
    }

    /**
     * Sets where the back button navigates.
     */
    public void setBackAction(Runnable action) {
        this.backAction = action;
    }

    /**
     * Sets the back button label text.
     */
    public void setBackLabel(String label) {
        backBtn.setText(label);
    }

    private JLabel styledValue() {
        JLabel lbl = new JLabel("—");
        lbl.setForeground(Theme.TEXT);
        lbl.setFont(Theme.FONT_BOLD.deriveFont(17f));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private JLabel styledFormLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setForeground(Theme.MUTED);
        lbl.setFont(Theme.FONT.deriveFont(15f));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        return lbl;
    }

    private JPanel infoRow(String labelText, JLabel valueLabel) {
        JPanel row = UiFactory.rowPanel();
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 36));
        JLabel lbl = styledFormLabel(labelText);
        lbl.setPreferredSize(new Dimension(120, 24));
        lbl.setMinimumSize(new Dimension(120, 24));
        row.add(lbl);
        row.add(Box.createHorizontalStrut(12));
        row.add(valueLabel);
        row.add(Box.createHorizontalGlue());
        return row;
    }

    @Override
    public void addNotify() {
        super.addNotify();
        java.awt.Window window = javax.swing.SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override
                public void componentResized(java.awt.event.ComponentEvent e) {
                    applyResponsiveFonts(window.getWidth());
                }
            });
        }
    }

    private void applyResponsiveFonts(int width) {
        float scale = Math.max(1.0f, width / 1240f);
        float base  = 15f * scale;
        float value = 17f * scale;

        senderUsernameLabel.setFont(Theme.FONT_BOLD.deriveFont(value));
        receiverUsernameLabel.setFont(Theme.FONT_BOLD.deriveFont(value));
        partnerConfirmStatusLabel.setFont(Theme.FONT.deriveFont(base));
        currentUserConfirmStatusLabel.setFont(Theme.FONT_BOLD.deriveFont(base));

        revalidate();
        repaint();
    }
}
