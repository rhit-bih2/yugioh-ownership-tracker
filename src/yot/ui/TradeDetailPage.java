package yot.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JViewport;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import yot.services.CollectionService;
import yot.services.DatabaseConnectionService;
import yot.services.TradeDetailService;
import yot.services.TradeDetailService.Card;

public class TradeDetailPage extends JPanel {

    private static final int CARD_TILE_WIDTH = 360;
    private static final int CARD_TILE_GAP = 8;
    private static final int CARD_NAME_MAX_WIDTH = 190;
    private static final float CARD_NAME_MIN_FONT_SIZE = 10f;

    private final DatabaseConnectionService dbService;
    private final TradeDetailService tradeService;
    private final CollectionService collectionService;
    private String currentUsername;
    private Integer currentTradeId;
    private String partnerUsername;

    private final JLabel senderUsernameLabel;
    private final JLabel receiverUsernameLabel;
    private final JLabel partnerConfirmStatusLabel;
    private final JButton confirmButton;
    private final JButton abortButton;
    private final JLabel currentUserConfirmStatusLabel;

    private final JPanel partnerCardsGridPanel;
    private final JPanel myOffersGridPanel;
    private final JPanel partnerOffersCard;
    private final JPanel myOfferCard;

    private final Map<Integer, ImageIcon> cardImageCache = new HashMap<Integer, ImageIcon>();
    private ArrayList<Card> partnerCards = new ArrayList<Card>();
    private ArrayList<Card> myOfferedCards = new ArrayList<Card>();

    private Runnable backAction;
    private final JButton backBtn;

    public TradeDetailPage(Runnable onBack, DatabaseConnectionService dbService, String username) {
        this.dbService = dbService;
        this.tradeService = new TradeDetailService(dbService, username);
        this.collectionService = new CollectionService(dbService);
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

        senderUsernameLabel = styledValue();
        metadataCard.add(infoRow("Sender", senderUsernameLabel));
        metadataCard.add(Box.createVerticalStrut(12));

        receiverUsernameLabel = styledValue();
        metadataCard.add(infoRow("Receiver", receiverUsernameLabel));
        metadataCard.add(Box.createVerticalStrut(20));

        metadataCard.add(styledFormLabel("Trade Partner Status"));
        metadataCard.add(Box.createVerticalStrut(6));
        partnerConfirmStatusLabel = new JLabel("—");
        partnerConfirmStatusLabel.setForeground(Theme.MUTED);
        partnerConfirmStatusLabel.setFont(Theme.FONT.deriveFont(15f));
        partnerConfirmStatusLabel.setAlignmentX(LEFT_ALIGNMENT);
        metadataCard.add(partnerConfirmStatusLabel);
        metadataCard.add(Box.createVerticalStrut(20));

        metadataCard.add(styledFormLabel("Your Confirmation Status"));
        metadataCard.add(Box.createVerticalStrut(6));
        currentUserConfirmStatusLabel = new JLabel("—");
        currentUserConfirmStatusLabel.setForeground(Theme.ACCENT);
        currentUserConfirmStatusLabel.setFont(Theme.FONT_BOLD.deriveFont(15f));
        currentUserConfirmStatusLabel.setAlignmentX(LEFT_ALIGNMENT);
        metadataCard.add(currentUserConfirmStatusLabel);
        metadataCard.add(Box.createVerticalStrut(20));

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

        page.add(metadataCard);
        page.add(Box.createVerticalStrut(14));

        // ── Partner's Offer Panel ─────────────────────────────────────────────
        partnerOffersCard = UiFactory.panelCard();
        partnerOffersCard.setLayout(new BoxLayout(partnerOffersCard, BoxLayout.Y_AXIS));
        partnerOffersCard.setBorder(new EmptyBorder(16, 16, 16, 16));
        partnerOffersCard.setAlignmentX(LEFT_ALIGNMENT);
        partnerOffersCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        partnerOffersCard.add(UiFactory.sectionTitle("Partner's Offer"));
        partnerOffersCard.add(Box.createVerticalStrut(10));
        partnerCardsGridPanel = new JPanel();
        partnerCardsGridPanel.setLayout(new BoxLayout(partnerCardsGridPanel, BoxLayout.Y_AXIS));
        partnerCardsGridPanel.setOpaque(false);
        partnerCardsGridPanel.setAlignmentX(LEFT_ALIGNMENT);
        partnerOffersCard.add(partnerCardsGridPanel);
        partnerOffersCard.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                relayoutPartnerGrid();
            }
        });
        page.add(partnerOffersCard);
        page.add(Box.createVerticalStrut(14));

        // ── Your Offer Panel ──────────────────────────────────────────────────
        myOfferCard = UiFactory.panelCard();
        myOfferCard.setLayout(new BoxLayout(myOfferCard, BoxLayout.Y_AXIS));
        myOfferCard.setBorder(new EmptyBorder(16, 16, 16, 16));
        myOfferCard.setAlignmentX(LEFT_ALIGNMENT);
        myOfferCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        myOfferCard.add(UiFactory.sectionTitle("Your Offer"));
        myOfferCard.add(Box.createVerticalStrut(10));

        CardSearchPanel searchPanel = new CardSearchPanel(this::queryOwnedCards, this::onSearchCardClick, dbService);
        myOfferCard.add(searchPanel);
        myOfferCard.add(Box.createVerticalStrut(14));
        myOfferCard.add(UiFactory.sectionTitle("Cards You Are Offering"));
        myOfferCard.add(Box.createVerticalStrut(10));

        myOffersGridPanel = new JPanel();
        myOffersGridPanel.setLayout(new BoxLayout(myOffersGridPanel, BoxLayout.Y_AXIS));
        myOffersGridPanel.setOpaque(false);
        myOffersGridPanel.setAlignmentX(LEFT_ALIGNMENT);
        myOfferCard.add(myOffersGridPanel);
        myOfferCard.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                relayoutMyOffersGrid();
            }
        });

        page.add(myOfferCard);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                relayoutPartnerGrid();
                relayoutMyOffersGrid();
            }
        });

        rebuildPartnerCards(new ArrayList<Card>());
        rebuildMyOffers(new ArrayList<Card>());

        setLayout(new BorderLayout());
        setOpaque(false);
        add(UiFactory.scrollWrap(page), BorderLayout.CENTER);
    }

    public void loadTradeDetail(Integer tradeId) {
        this.currentTradeId = tradeId;
        this.partnerUsername = null;

        senderUsernameLabel.setText("Loading…");
        receiverUsernameLabel.setText("Loading…");
        partnerConfirmStatusLabel.setText("Loading…");
        currentUserConfirmStatusLabel.setText("Loading…");
        confirmButton.setEnabled(true);
        confirmButton.setText("Confirm Trade");
        confirmButton.setBackground(Theme.ACCENT);
        confirmButton.setForeground(Color.WHITE);
        abortButton.setEnabled(true);
        abortButton.setText("Abort Trade");

        rebuildPartnerCards(new ArrayList<Card>());
        rebuildMyOffers(new ArrayList<Card>());

        new Thread(() -> {
            Map<String, String> tradeData = tradeService.getAllTradeInfo(tradeId);

            if (tradeData == null || tradeData.isEmpty()) {
                SwingUtilities.invokeLater(() -> {
                    senderUsernameLabel.setText("Error loading trade");
                    receiverUsernameLabel.setText("Error loading trade");
                    confirmButton.setEnabled(false);
                    abortButton.setEnabled(false);
                    revalidate();
                    repaint();
                });
                return;
            }

            String senderUsername = tradeData.getOrDefault("SenderUsername", "Unknown");
            String receiverUsername = tradeData.getOrDefault("ReceiverUsername", "Unknown");
            String senderConfirmedStr = tradeData.getOrDefault("SenderConfirmed", "0");
            String receiverConfirmedStr = tradeData.getOrDefault("ReceiverConfirmed", "0");

            String partner = currentUsername.equals(senderUsername) ? receiverUsername : senderUsername;

            ArrayList<Card> partnerOffer = tradeService.getCardsOffered(tradeId, partner);
            ArrayList<Card> myOffer = tradeService.getCardsOffered(tradeId, currentUsername);

            SwingUtilities.invokeLater(() -> {
                partnerUsername = partner;
                senderUsernameLabel.setText(senderUsername);
                receiverUsernameLabel.setText(receiverUsername);

                boolean senderConfirmed = "1".equals(senderConfirmedStr);
                boolean receiverConfirmed = "1".equals(receiverConfirmedStr);

                boolean currentUserConfirmed = (currentUsername.equals(senderUsername) && senderConfirmed)
                        || (currentUsername.equals(receiverUsername) && receiverConfirmed);

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

                boolean partnerConfirmed = (currentUsername.equals(senderUsername) && receiverConfirmed)
                        || (currentUsername.equals(receiverUsername) && senderConfirmed);

                if (partnerConfirmed) {
                    partnerConfirmStatusLabel.setText("Trade partner has confirmed");
                    partnerConfirmStatusLabel.setForeground(Theme.ACCENT_ALT);
                } else {
                    partnerConfirmStatusLabel.setText("Trade partner has not confirmed");
                    partnerConfirmStatusLabel.setForeground(Theme.MUTED);
                }

                rebuildPartnerCards(partnerOffer);
                rebuildMyOffers(myOffer);

                revalidate();
                repaint();
            });
        }).start();
    }

    private void refreshOfferPanels() {
        if (currentTradeId == null || partnerUsername == null) {
            return;
        }
        new Thread(() -> {
            ArrayList<Card> partnerOffer = tradeService.getCardsOffered(currentTradeId, partnerUsername);
            ArrayList<Card> myOffer = tradeService.getCardsOffered(currentTradeId, currentUsername);
            SwingUtilities.invokeLater(() -> {
                rebuildPartnerCards(partnerOffer);
                rebuildMyOffers(myOffer);
            });
        }).start();
    }

    private List<Integer> queryOwnedCards(Map<String, String> map) {
        return collectionService.getUserCardIDsWithFilter(currentUsername, map);
    }

    private void onSearchCardClick(int cardId) {
        if (currentTradeId == null) {
            return;
        }
        new Thread(() -> {
            boolean success = tradeService.offerCard(currentTradeId, cardId, currentUsername);
            if (success) {
                SwingUtilities.invokeLater(this::refreshOfferPanels);
            }
        }).start();
    }

    private void rebuildPartnerCards(ArrayList<Card> cards) {
        partnerCards = new ArrayList<Card>(cards);
        relayoutPartnerGrid();
    }

    private void rebuildMyOffers(ArrayList<Card> cards) {
        myOfferedCards = new ArrayList<Card>(cards);
        relayoutMyOffersGrid();
    }

    private void relayoutPartnerGrid() {
        relayoutCardGrid(partnerCardsGridPanel, partnerCards, false);
        partnerOffersCard.revalidate();
        partnerOffersCard.repaint();
    }

    private void relayoutMyOffersGrid() {
        relayoutCardGrid(myOffersGridPanel, myOfferedCards, true);
        myOfferCard.revalidate();
        myOfferCard.repaint();
    }

    private void relayoutCardGrid(JPanel gridPanel, ArrayList<Card> cards, boolean allowMutations) {
        gridPanel.removeAll();

        if (cards.isEmpty()) {
            JLabel empty = new JLabel(allowMutations
                    ? "You have not offered any cards yet. Search above to add cards."
                    : "Your trade partner has not offered any cards yet.");
            empty.setForeground(Theme.MUTED);
            empty.setAlignmentX(LEFT_ALIGNMENT);
            gridPanel.add(empty);
            gridPanel.revalidate();
            gridPanel.repaint();
            return;
        }

        JPanel hostPanel = allowMutations ? myOfferCard : partnerOffersCard;
        int availableWidth = getCardsViewportWidth(hostPanel);
        int columns = Math.max(1, (availableWidth + CARD_TILE_GAP) / (CARD_TILE_WIDTH + CARD_TILE_GAP));
        int index = 0;
        while (index < cards.size()) {
            JPanel rowPanel = new JPanel();
            rowPanel.setOpaque(false);
            rowPanel.setAlignmentX(LEFT_ALIGNMENT);
            rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));

            for (int col = 0; col < columns && index < cards.size(); col++) {
                rowPanel.add(cardTile(cards.get(index), allowMutations));
                index++;
                if (col < columns - 1 && index < cards.size()) {
                    rowPanel.add(Box.createHorizontalStrut(CARD_TILE_GAP));
                }
            }
            rowPanel.add(Box.createHorizontalGlue());
            gridPanel.add(rowPanel);
            if (index < cards.size()) {
                gridPanel.add(Box.createVerticalStrut(CARD_TILE_GAP));
            }
        }

        gridPanel.revalidate();
        gridPanel.repaint();
    }

    private int getCardsViewportWidth(JPanel hostPanel) {
        JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, hostPanel);
        if (viewport != null && viewport.getWidth() > 0) {
            return Math.max(CARD_TILE_WIDTH, viewport.getWidth() - 32);
        }
        if (hostPanel.getWidth() > 0) {
            return Math.max(CARD_TILE_WIDTH, hostPanel.getWidth() - 32);
        }
        return CARD_TILE_WIDTH;
    }

    private JPanel cardTile(Card card, boolean allowMutations) {
        JPanel tile = new JPanel();
        tile.setBackground(new Color(35, 45, 80));
        tile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                new EmptyBorder(12, 12, 12, 12)));
        tile.setLayout(new BoxLayout(tile, BoxLayout.X_AXIS));
        tile.setPreferredSize(new Dimension(CARD_TILE_WIDTH, allowMutations ? 220 : 174));
        tile.setMaximumSize(new Dimension(CARD_TILE_WIDTH, allowMutations ? 220 : 174));
        tile.setMinimumSize(new Dimension(CARD_TILE_WIDTH, allowMutations ? 220 : 174));

        JLabel image = buildCardImageLabel(card.getId());

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        JLabel cardName = new JLabel(card.getName());
        cardName.setForeground(Theme.TEXT);
        cardName.setFont(Theme.FONT_BOLD);
        fitCardNameFont(cardName, card.getName());
        right.add(cardName);
        right.add(Box.createVerticalStrut(8));
        JLabel quantityLabel = new JLabel("Quantity: " + card.getQuantity());
        quantityLabel.setForeground(Theme.MUTED);
        right.add(quantityLabel);

        if (allowMutations) {
            right.add(Box.createVerticalStrut(8));
            JButton addBtn = UiFactory.primaryButton("Add 1");
            addBtn.addActionListener(e -> handleIncrementOffer(card));
            right.add(addBtn);
            right.add(Box.createVerticalStrut(8));
            JButton removeBtn = UiFactory.dangerButton("Remove 1");
            removeBtn.addActionListener(e -> handleDecrementOffer(card));
            right.add(removeBtn);
        }

        tile.add(image);
        tile.add(Box.createHorizontalStrut(12));
        tile.add(right);
        return tile;
    }

    private void handleIncrementOffer(Card card) {
        if (currentTradeId == null) {
            return;
        }
        new Thread(() -> {
            boolean success = tradeService.incrementCardOffered(currentTradeId, card.getId(), currentUsername);
            if (success) {
                SwingUtilities.invokeLater(this::refreshOfferPanels);
            }
        }).start();
    }

    private void handleDecrementOffer(Card card) {
        if (currentTradeId == null) {
            return;
        }
        if (card.getQuantity() <= 1) {
            int choice = JOptionPane.showOptionDialog(
                    this,
                    "Remove \"" + card.getName() + "\" from your trade offer?",
                    "Remove Card",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE,
                    null,
                    new String[] { "Yes", "No" },
                    "No");
            if (choice != JOptionPane.YES_OPTION) {
                return;
            }
        }
        new Thread(() -> {
            boolean success;
            if (card.getQuantity() <= 1) {
                success = tradeService.removeCardOffered(currentTradeId, card.getId(), currentUsername);
            } else {
                success = tradeService.decrementCardOffered(currentTradeId, card.getId(), currentUsername);
            }
            if (success) {
                SwingUtilities.invokeLater(this::refreshOfferPanels);
            }
        }).start();
    }

    private JLabel buildCardImageLabel(int cardID) {
        JLabel image = new JLabel("Card Image", SwingConstants.CENTER);
        image.setOpaque(true);
        image.setBackground(new Color(32, 42, 79));
        image.setForeground(Theme.MUTED);
        image.setPreferredSize(new Dimension(120, 150));
        image.setMaximumSize(new Dimension(120, 150));

        ImageIcon cachedIcon = cardImageCache.get(cardID);
        if (cachedIcon != null) {
            image.setIcon(cachedIcon);
            image.setText(null);
            return image;
        }

        String imageUrl = tradeService.getCardImage(cardID);
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            image.setText("No Image");
            return image;
        }

        try {
            Image raw = ImageIO.read(new URL(imageUrl));
            if (raw == null) {
                image.setText("No Image");
                return image;
            }
            Image scaled = raw.getScaledInstance(120, 150, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaled);
            cardImageCache.put(cardID, icon);
            image.setIcon(icon);
            image.setText(null);
        } catch (IOException ex) {
            image.setText("Image unavailable");
        }

        return image;
    }

    private void fitCardNameFont(JLabel label, String text) {
        Font currentFont = Theme.FONT_BOLD;
        FontMetrics metrics = label.getFontMetrics(currentFont);
        while (metrics.stringWidth(text) > CARD_NAME_MAX_WIDTH
                && currentFont.getSize2D() > CARD_NAME_MIN_FONT_SIZE) {
            currentFont = currentFont.deriveFont(currentFont.getSize2D() - 1f);
            metrics = label.getFontMetrics(currentFont);
        }
        label.setFont(currentFont);
    }

    private void handleConfirmTrade() {
        if (currentTradeId == null || currentUsername == null) return;

        confirmButton.setEnabled(false);
        confirmButton.setText("Confirming…");

        new Thread(() -> {
            boolean success = tradeService.confirmTrade(currentTradeId, currentUsername);

            SwingUtilities.invokeLater(() -> {
                if (success) {
                    confirmButton.setEnabled(false);
                    confirmButton.setText("Confirmed");
                    confirmButton.setBackground(new Color(60, 70, 100));
                    confirmButton.setForeground(Theme.MUTED);
                    currentUserConfirmStatusLabel.setText("You have confirmed this trade");
                    currentUserConfirmStatusLabel.setForeground(Theme.ACCENT_ALT);
                } else {
                    confirmButton.setEnabled(true);
                    confirmButton.setText("Confirm Trade");
                    confirmButton.setBackground(Theme.ACCENT);
                    confirmButton.setForeground(Color.WHITE);
                }
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
            boolean success = tradeService.abortTrade(currentTradeId);

            SwingUtilities.invokeLater(() -> {
                if (success) {
                    abortButton.setEnabled(false);
                    abortButton.setText("Trade Aborted");
                    abortButton.setBackground(new Color(60, 70, 100));
                    abortButton.setForeground(Theme.MUTED);
                    confirmButton.setEnabled(false);
                } else {
                    abortButton.setEnabled(true);
                    abortButton.setText("Abort Trade");
                }
                revalidate();
                repaint();
            });
        }).start();
    }

    public void setBackAction(Runnable action) {
        this.backAction = action;
    }

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
        java.awt.Window window = SwingUtilities.getWindowAncestor(this);
        if (window != null) {
            window.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    applyResponsiveFonts(window.getWidth());
                }
            });
        }
    }

    private void applyResponsiveFonts(int width) {
        float scale = Math.max(1.0f, width / 1240f);
        float base = 15f * scale;
        float value = 17f * scale;

        senderUsernameLabel.setFont(Theme.FONT_BOLD.deriveFont(value));
        receiverUsernameLabel.setFont(Theme.FONT_BOLD.deriveFont(value));
        partnerConfirmStatusLabel.setFont(Theme.FONT.deriveFont(base));
        currentUserConfirmStatusLabel.setFont(Theme.FONT_BOLD.deriveFont(base));

        revalidate();
        repaint();
    }
}
