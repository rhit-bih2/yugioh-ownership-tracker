package yot.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import yot.services.CardSearchService;
import yot.services.DatabaseConnectionService;
import yot.services.MarketplaceService;

public class MarketplacePage extends JPanel {

    private final MarketplaceService marketplaceService;
    private final CardSearchService cardSearchService;

    private final BiConsumer<String, Integer> onOpenSalesDetail;

    private final JPanel listingDetailWrapper;
    private final JPanel listingRowsPanel;

    public MarketplacePage(DatabaseConnectionService dbService,
                           Consumer<Integer> onOpenCardDetail,
                           BiConsumer<String, Integer>  onOpenSalesDetail) {
        this.marketplaceService = new MarketplaceService(dbService);
        this.cardSearchService  = new CardSearchService(dbService);
        this.onOpenSalesDetail  = onOpenSalesDetail;

        JPanel page = UiFactory.pageContainer();

        page.add(UiFactory.pageHeader("Marketplace",
                "Browse and search cards available for sale."));
        page.add(Box.createVerticalStrut(14));

        JPanel searchBox = new CardSearchPanel(
                marketplaceService::retrieveCard,
                this::onCardClicked,
                dbService
        );
        searchBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        searchBox.setAlignmentX(LEFT_ALIGNMENT);
        page.add(searchBox);
        page.add(Box.createVerticalStrut(14));

        // ── Listing detail wrapper ────────────────────────────────────────────
        listingDetailWrapper = UiFactory.panelCard();
        listingDetailWrapper.setLayout(new BoxLayout(listingDetailWrapper, BoxLayout.Y_AXIS));
        listingDetailWrapper.setBorder(new EmptyBorder(16, 16, 16, 16));
        listingDetailWrapper.setVisible(false);
        listingDetailWrapper.setAlignmentX(LEFT_ALIGNMENT);
        listingDetailWrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        listingDetailWrapper.add(UiFactory.sectionTitle("Listings for This Card"));
        listingDetailWrapper.add(Box.createVerticalStrut(12));

        // Scrollable rows panel
        listingRowsPanel = new JPanel();
        listingRowsPanel.setOpaque(false);
        listingRowsPanel.setLayout(new BoxLayout(listingRowsPanel, BoxLayout.Y_AXIS));
        listingRowsPanel.setAlignmentX(LEFT_ALIGNMENT);

        listingDetailWrapper.add(listingRowsPanel);
        page.add(listingDetailWrapper);
        page.add(Box.createVerticalGlue());

        setLayout(new BorderLayout());
        setOpaque(false);
        add(UiFactory.scrollWrap(page), BorderLayout.CENTER);
    }

    private void onCardClicked(Integer cardId) {
        listingDetailWrapper.setVisible(false);
        listingRowsPanel.removeAll();

        // Use GetListingDetail — one row per seller
        List<String[]> listings = marketplaceService.getListingDetail(cardId);
        String imageUrl = cardSearchService.getCardImageUrl(cardId);

        if (listings == null || listings.isEmpty()) {
            JLabel none = new JLabel("No listings found for this card.");
            none.setForeground(Theme.MUTED);
            none.setFont(Theme.FONT_BOLD.deriveFont(16f));
            none.setAlignmentX(LEFT_ALIGNMENT);
            listingRowsPanel.add(none);
            listingDetailWrapper.setVisible(true);
            listingDetailWrapper.revalidate();
            listingDetailWrapper.repaint();
            return;
        }

        for (int i = 0; i < listings.size(); i++) {
            listingRowsPanel.add(buildListingRow(listings.get(i), imageUrl, cardId));
            if (i < listings.size() - 1) {
                listingRowsPanel.add(Box.createVerticalStrut(10));
            }
        }

        listingDetailWrapper.setVisible(true);
        listingDetailWrapper.revalidate();
        listingDetailWrapper.repaint();
    }

    /**
     * One listing row per seller using GetListingDetail indices:
     *   [0] SellerUsername  [1] SellerID  [2] StoreName  [3] Phone
     *   [4] CardID          [5] CardName  [6] CardCode   [7] Rarity
     *   [8] ListingPrice    [9] MarketPrice
     */
    private JPanel buildListingRow(String[] listing, String imageUrl, int cardId) {
        String sellerusername = listing[0];

        JPanel row = new JPanel();
        row.setBackground(new Color(35, 45, 80));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 230));

        // ── Card image — click → SalesDetail ─────────────────────────────────
        JLabel imgLabel = new JLabel("Loading…", SwingConstants.CENTER);
        imgLabel.setOpaque(true);
        imgLabel.setBackground(new Color(32, 42, 79));
        imgLabel.setForeground(Theme.MUTED);
        imgLabel.setPreferredSize(new Dimension(110, 158));
        imgLabel.setMaximumSize(new Dimension(110, 158));
        imgLabel.setMinimumSize(new Dimension(110, 158));
        imgLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imgLabel.setVerticalAlignment(SwingConstants.CENTER);
        imgLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        imgLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                onOpenSalesDetail.accept(sellerusername, cardId);
            }
        });

        new Thread(() -> {
            if (imageUrl == null || imageUrl.equals("—")) {
                SwingUtilities.invokeLater(() -> imgLabel.setText("No Image"));
                return;
            }
            try {
                Image raw = ImageIO.read(new URL(imageUrl));
                if (raw == null) {
                    SwingUtilities.invokeLater(() -> imgLabel.setText("No Image"));
                    return;
                }
                Image scaled = raw.getScaledInstance(110, 158, Image.SCALE_SMOOTH);
                SwingUtilities.invokeLater(() -> {
                    imgLabel.setIcon(new ImageIcon(scaled));
                    imgLabel.setText(null);
                });
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> imgLabel.setText("N/A"));
            }
        }).start();

        // ── Col 1: Card Name, Seller, Phone ──────────────────────────────────
        JPanel col1 = new JPanel();
        col1.setOpaque(false);
        col1.setLayout(new BoxLayout(col1, BoxLayout.Y_AXIS));
        col1.add(infoCell("Card Name", listing[5]));
        col1.add(Box.createVerticalStrut(12));
        col1.add(infoCell("Seller",    listing[0]));
        col1.add(Box.createVerticalStrut(12));
        col1.add(infoCell("Phone",     listing[3]));

        // ── Col 2: Card Code, Store, Rarity ──────────────────────────────────
        JPanel col2 = new JPanel();
        col2.setOpaque(false);
        col2.setLayout(new BoxLayout(col2, BoxLayout.Y_AXIS));
        col2.add(infoCell("Card Code", listing[6]));
        col2.add(Box.createVerticalStrut(12));
        col2.add(infoCell("Store",     listing[2]));
        col2.add(Box.createVerticalStrut(12));
        col2.add(infoCell("Rarity",    listing[7]));

        // ── Col 3: Listing Price, Market Price ───────────────────────────────
        JPanel col3 = new JPanel();
        col3.setOpaque(false);
        col3.setLayout(new BoxLayout(col3, BoxLayout.Y_AXIS));

        String listingPriceText;
        try {
            listingPriceText = String.format("$%.2f", Double.parseDouble(listing[8]));
        } catch (NumberFormatException e) {
            listingPriceText = "$" + listing[8];
        }
        String marketPriceText;
        try {
            marketPriceText = String.format("$%.2f", Double.parseDouble(listing[9]));
        } catch (NumberFormatException e) {
            marketPriceText = "$" + listing[9];
        }

        col3.add(pricedCell("Listing Price", listingPriceText, Theme.ACCENT_ALT));
        col3.add(Box.createVerticalStrut(12));
        col3.add(pricedCell("Market Price",  marketPriceText,  Theme.ACCENT));

        // ── Sales Detail button ───────────────────────────────────────────────
        JPanel btnPanel = new JPanel();
        btnPanel.setOpaque(false);
        btnPanel.setLayout(new BoxLayout(btnPanel, BoxLayout.Y_AXIS));
        btnPanel.add(Box.createVerticalGlue());
        JButton salesDetailBtn = UiFactory.primaryButton("Sales Detail");
        salesDetailBtn.setAlignmentX(CENTER_ALIGNMENT);
        salesDetailBtn.addActionListener(e ->
                onOpenSalesDetail.accept(sellerusername, cardId));
        btnPanel.add(salesDetailBtn);

        row.add(imgLabel);
        row.add(Box.createHorizontalStrut(16));
        row.add(col1);
        row.add(Box.createHorizontalStrut(24));
        row.add(col2);
        row.add(Box.createHorizontalStrut(24));
        row.add(col3);
        row.add(Box.createHorizontalGlue());
        row.add(btnPanel);
        return row;
    }

    private JPanel infoCell(String label, String value) {
        JPanel cell = new JPanel();
        cell.setOpaque(false);
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
        JLabel lbl = UiFactory.formLabel(label);
        lbl.setFont(Theme.FONT.deriveFont(13f));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        JLabel val = new JLabel(value);
        val.setForeground(Theme.TEXT);
        val.setFont(Theme.FONT_BOLD.deriveFont(15f));
        val.setAlignmentX(LEFT_ALIGNMENT);
        cell.add(lbl);
        cell.add(Box.createVerticalStrut(2));
        cell.add(val);
        return cell;
    }

    private JPanel pricedCell(String label, String value, java.awt.Color valueColor) {
        JPanel cell = new JPanel();
        cell.setOpaque(false);
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
        JLabel lbl = UiFactory.formLabel(label);
        lbl.setFont(Theme.FONT.deriveFont(13f));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        JLabel val = new JLabel(value);
        val.setForeground(valueColor);
        val.setFont(Theme.FONT_BOLD.deriveFont(17f));
        val.setAlignmentX(LEFT_ALIGNMENT);
        cell.add(lbl);
        cell.add(Box.createVerticalStrut(2));
        cell.add(val);
        return cell;
    }
}