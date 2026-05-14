package yot.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.function.Consumer;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import yot.services.CardSearchService;
import yot.services.DatabaseConnectionService;
import yot.services.MarketplaceService;

public class MarketplacePage extends JPanel {

    private final MarketplaceService marketplaceService;
    private final CardSearchService cardSearchService;
    private final Consumer<Integer> onOpenSalesDetail;
    private final JPanel listingDetailPanel;

    public MarketplacePage(DatabaseConnectionService dbService,
                           Consumer<Integer> onOpenCardDetail,
                           Consumer<Integer> onOpenSalesDetail) {
        this.marketplaceService = new MarketplaceService(dbService);
        this.cardSearchService  = new CardSearchService(dbService);
        this.onOpenSalesDetail  = onOpenSalesDetail;

        JPanel page = UiFactory.pageContainer();

        page.add(UiFactory.pageHeader("Marketplace",
                "Browse and search cards available for sale."));
        page.add(Box.createVerticalStrut(14));

        JPanel searchBox = new CardSearchPanel(
                marketplaceService::searchListings,
                this::onCardClicked,
                dbService
        );
        searchBox.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        searchBox.setAlignmentX(LEFT_ALIGNMENT);
        page.add(searchBox);
        page.add(Box.createVerticalStrut(14));

        listingDetailPanel = UiFactory.panelCard();
        listingDetailPanel.setLayout(new BoxLayout(listingDetailPanel, BoxLayout.Y_AXIS));
        listingDetailPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
        listingDetailPanel.setVisible(false);
        listingDetailPanel.setAlignmentX(LEFT_ALIGNMENT);
        listingDetailPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        page.add(listingDetailPanel);
        page.add(Box.createVerticalGlue());

        setLayout(new BorderLayout());
        setOpaque(false);
        add(page, BorderLayout.CENTER);
    }

    private void onCardClicked(Integer cardId) {
        listingDetailPanel.setVisible(false);
        listingDetailPanel.removeAll();
        listingDetailPanel.add(UiFactory.sectionTitle("Listing Detail"));
        listingDetailPanel.add(Box.createVerticalStrut(12));

        List<String[]> sellers = marketplaceService.getSellerDetail(cardId);
        String imageUrl = cardSearchService.getCardImageUrl(cardId);

        if (sellers == null || sellers.isEmpty()) {
            JLabel none = new JLabel("No listings found for this card.");
            none.setForeground(Theme.MUTED);
            none.setFont(Theme.FONT_BOLD.deriveFont(16f));
            none.setAlignmentX(LEFT_ALIGNMENT);
            listingDetailPanel.add(none);
            listingDetailPanel.setVisible(true);
            listingDetailPanel.revalidate();
            listingDetailPanel.repaint();
            return;
        }

        for (int i = 0; i < sellers.size(); i++) {
            listingDetailPanel.add(buildListingRow(sellers.get(i), imageUrl, cardId));
            if (i < sellers.size() - 1) {
                listingDetailPanel.add(Box.createVerticalStrut(10));
            }
        }

        listingDetailPanel.setVisible(true);
        listingDetailPanel.revalidate();
        listingDetailPanel.repaint();
    }

    /**
     * 3-column layout:
     *   Col 1: Card ID + Seller + Phone
     *   Col 2: Card Name + Store + Price
     *   Col 3: (spacer) + Sales Detail button bottom
     *
     * seller indices:
     *   [0] SellerUsername  [1] SellerID   [2] StoreName    [3] Address
     *   [4] City            [5] State      [6] ZipCode      [7] SellerDescription
     *   [8] Phone           [9] CardID     [10] CardName    [11] CardDescription
     *   [12] Price
     */
    private JPanel buildListingRow(String[] seller, String imageUrl, int cardId) {
        JPanel row = new JPanel();
        row.setBackground(new Color(35, 45, 80));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                new EmptyBorder(16, 16, 16, 16)
        ));
        row.setLayout(new BoxLayout(row, BoxLayout.X_AXIS));
        row.setAlignmentX(LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

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
                onOpenSalesDetail.accept(cardId);
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

        // ── Column 1: Card ID, Seller, Phone ─────────────────────────────────
        JPanel col1 = new JPanel();
        col1.setOpaque(false);
        col1.setLayout(new BoxLayout(col1, BoxLayout.Y_AXIS));
        col1.add(infoCell("Card ID", seller[9]));
        col1.add(Box.createVerticalStrut(14));
        col1.add(infoCell("Seller",  seller[0]));
        col1.add(Box.createVerticalStrut(14));
        col1.add(infoCell("Phone",   seller[8]));

        // ── Column 2: Card Name, Store, Price ────────────────────────────────
        JPanel col2 = new JPanel();
        col2.setOpaque(false);
        col2.setLayout(new BoxLayout(col2, BoxLayout.Y_AXIS));
        col2.add(infoCell("Card Name", seller[10]));
        col2.add(Box.createVerticalStrut(14));
        col2.add(infoCell("Store",     seller[2]));
        col2.add(Box.createVerticalStrut(14));

        // Price with accent color
        JPanel priceCell = new JPanel();
        priceCell.setOpaque(false);
        priceCell.setLayout(new BoxLayout(priceCell, BoxLayout.Y_AXIS));
        JLabel priceLbl = UiFactory.formLabel("Price");
        priceLbl.setFont(Theme.FONT.deriveFont(13f));
        priceLbl.setAlignmentX(LEFT_ALIGNMENT);
        String priceText;
        try {
            priceText = String.format("$%.2f", Double.parseDouble(seller[12]));
        } catch (NumberFormatException e) {
            priceText = "$" + seller[12];
        }
        JLabel priceVal = new JLabel(priceText);
        priceVal.setForeground(Theme.ACCENT_ALT);
        priceVal.setFont(Theme.FONT_BOLD.deriveFont(18f));
        priceVal.setAlignmentX(LEFT_ALIGNMENT);
        priceCell.add(priceLbl);
        priceCell.add(Box.createVerticalStrut(2));
        priceCell.add(priceVal);
        col2.add(priceCell);

        // ── Column 3: Sales Detail button pinned to bottom ────────────────────
        JPanel col3 = new JPanel();
        col3.setOpaque(false);
        col3.setLayout(new BoxLayout(col3, BoxLayout.Y_AXIS));
        col3.add(Box.createVerticalGlue());
        JButton salesDetailBtn = UiFactory.primaryButton("Sales Detail");
        salesDetailBtn.setAlignmentX(CENTER_ALIGNMENT);
        salesDetailBtn.addActionListener(e -> onOpenSalesDetail.accept(cardId));
        col3.add(salesDetailBtn);

        row.add(imgLabel);
        row.add(Box.createHorizontalStrut(20));
        row.add(col1);
        row.add(Box.createHorizontalStrut(32));
        row.add(col2);
        row.add(Box.createHorizontalGlue());
        row.add(col3);
        return row;
    }

    /**
     * Builds a label + value cell with larger fonts for readability.
     */
    private JPanel infoCell(String label, String value) {
        JPanel cell = new JPanel();
        cell.setOpaque(false);
        cell.setLayout(new BoxLayout(cell, BoxLayout.Y_AXIS));
        JLabel lbl = UiFactory.formLabel(label);
        lbl.setFont(Theme.FONT.deriveFont(13f));
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        JLabel val = new JLabel(value);
        val.setForeground(Theme.TEXT);
        val.setFont(Theme.FONT_BOLD.deriveFont(16f));
        val.setAlignmentX(LEFT_ALIGNMENT);
        cell.add(lbl);
        cell.add(Box.createVerticalStrut(2));
        cell.add(val);
        return cell;
    }
}