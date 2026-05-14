package yot.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;

import java.util.function.Consumer;
import javax.imageio.ImageIO;
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
import yot.services.MarketplaceService;
import yot.services.SalesDetailService;
import yot.services.DatabaseConnectionService;

public class SalesDetailPage extends JPanel {

    private final SalesDetailService salesDetailService;
    private final CardSearchService cardSearchService;

    private final JLabel imageLabel;
    private final JLabel sellerUsernameLabel;
    private final JLabel storeNameLabel;
    private final JLabel phoneLabel;
    private final JLabel addressLabel;
    private final JLabel storeDescLabel;
    private final JLabel cardNameLabel;
    private final JLabel cardCodeLabel;
    private final JLabel rarityLabel;
    private final JLabel listingPriceLabel;
    private final JLabel marketPriceLabel;

    private int currentCardId = -1;
	private Runnable backAction;
    private final JButton backBtn;


    public SalesDetailPage(Runnable onBack, DatabaseConnectionService dbService,
                           Consumer<Integer> onOpenCardDetail) {
        this.salesDetailService = new SalesDetailService(dbService);
        this.cardSearchService  = new CardSearchService(dbService);
        this.backAction = onBack;

        JPanel page = UiFactory.pageContainer();

        // ── Top bar ───────────────────────────────────────────────────────────
        JPanel top = UiFactory.rowPanel();
        top.setAlignmentX(LEFT_ALIGNMENT);
        top.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel titleLabel = new JLabel("Card Sales Detail");
        titleLabel.setFont(Theme.FONT_PAGE);
        titleLabel.setForeground(Theme.TEXT);

        JLabel sub = new JLabel("Full seller and listing information for this card.");
        sub.setForeground(Theme.MUTED);

        header.add(titleLabel);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);

        backBtn = UiFactory.outlineButton("← Back to Marketplace");
        backBtn.addActionListener(e -> {
            if (backAction != null) backAction.run();
        });

        top.add(header);
        top.add(Box.createHorizontalGlue());
        top.add(backBtn);
        page.add(top);
        page.add(Box.createVerticalStrut(14));

        // ── Content row ───────────────────────────────────────────────────────
        JPanel contentRow = UiFactory.rowPanel();
        contentRow.setAlignmentX(LEFT_ALIGNMENT);
        contentRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // ── Left: image + button ──────────────────────────────────────────────
        JPanel imageCard = UiFactory.panelCard();
        imageCard.setLayout(new BoxLayout(imageCard, BoxLayout.Y_AXIS));
        imageCard.setBorder(new EmptyBorder(16, 16, 16, 16));
        imageCard.setPreferredSize(new Dimension(394, 550));
        imageCard.setMaximumSize(new Dimension(394, 550));
        imageCard.setMinimumSize(new Dimension(394, 550));

        imageLabel = new JLabel("Loading…", SwingConstants.CENTER);
        imageLabel.setOpaque(true);
        imageLabel.setBackground(new Color(32, 42, 79));
        imageLabel.setForeground(Theme.MUTED);
        imageLabel.setPreferredSize(new Dimension(362, 518));
        imageLabel.setMaximumSize(new Dimension(362, 518));
        imageLabel.setMinimumSize(new Dimension(362, 518));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imageLabel.setVerticalAlignment(SwingConstants.CENTER);
        imageLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        imageLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (currentCardId != -1) onOpenCardDetail.accept(currentCardId);
            }
        });
        imageCard.add(imageLabel);

        JPanel cardDetailBtnCard = UiFactory.panelCard();
        cardDetailBtnCard.setLayout(new BoxLayout(cardDetailBtnCard, BoxLayout.Y_AXIS));
        cardDetailBtnCard.setBorder(new EmptyBorder(8, 16, 8, 16));
        cardDetailBtnCard.setPreferredSize(new Dimension(394, 44));
        cardDetailBtnCard.setMaximumSize(new Dimension(394, 44));
        cardDetailBtnCard.setMinimumSize(new Dimension(394, 44));
        JButton cardDetailBtn = UiFactory.primaryButton("View Card Detail");
        cardDetailBtn.setAlignmentX(CENTER_ALIGNMENT);
        cardDetailBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        cardDetailBtn.setMinimumSize(new Dimension(362, 28));
        cardDetailBtn.setPreferredSize(new Dimension(362, 28));
        cardDetailBtn.addActionListener(e -> {
            if (currentCardId != -1) onOpenCardDetail.accept(currentCardId);
        });
        cardDetailBtnCard.add(UiFactory.fillButton(cardDetailBtn));

        JPanel leftStack = new JPanel();
        leftStack.setOpaque(false);
        leftStack.setLayout(new BoxLayout(leftStack, BoxLayout.Y_AXIS));
        leftStack.add(imageCard);
        leftStack.add(Box.createVerticalStrut(8));
        leftStack.add(cardDetailBtnCard);

        contentRow.add(leftStack);
        contentRow.add(Box.createHorizontalStrut(14));

        // ── Right stack ───────────────────────────────────────────────────────
        JPanel rightStack = new JPanel();
        rightStack.setOpaque(false);
        rightStack.setLayout(new BoxLayout(rightStack, BoxLayout.Y_AXIS));

        // ── Seller Information panel ──────────────────────────────────────────
        JPanel sellerCard = UiFactory.panelCard();
        sellerCard.setLayout(new BoxLayout(sellerCard, BoxLayout.Y_AXIS));
        sellerCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        sellerCard.setAlignmentX(LEFT_ALIGNMENT);
        sellerCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        sellerCard.add(UiFactory.sectionTitle("Seller Information"));
        sellerCard.add(Box.createVerticalStrut(14));

        sellerUsernameLabel = styledValue();
        storeNameLabel      = styledValue();
        phoneLabel          = styledValue();
        addressLabel        = styledValue();

        storeDescLabel = new JLabel("<html><body style='width:360px'>—</body></html>");
        storeDescLabel.setForeground(Theme.MUTED);
        storeDescLabel.setFont(Theme.FONT.deriveFont(15f));
        storeDescLabel.setAlignmentX(LEFT_ALIGNMENT);

        sellerCard.add(infoRow("Username",   sellerUsernameLabel));
        sellerCard.add(Box.createVerticalStrut(12));
        sellerCard.add(infoRow("Store Name", storeNameLabel));
        sellerCard.add(Box.createVerticalStrut(12));
        sellerCard.add(infoRow("Phone",      phoneLabel));
        sellerCard.add(Box.createVerticalStrut(12));
        sellerCard.add(infoRow("Address",    addressLabel));
        sellerCard.add(Box.createVerticalStrut(12));
        sellerCard.add(styledFormLabel("Store Description"));
        sellerCard.add(Box.createVerticalStrut(6));
        sellerCard.add(storeDescLabel);

        // ── Card & Pricing panel ──────────────────────────────────────────────
        JPanel cardPriceCard = UiFactory.panelCard();
        cardPriceCard.setLayout(new BoxLayout(cardPriceCard, BoxLayout.Y_AXIS));
        cardPriceCard.setBorder(new EmptyBorder(20, 20, 20, 20));
        cardPriceCard.setAlignmentX(LEFT_ALIGNMENT);
        cardPriceCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        cardPriceCard.add(UiFactory.sectionTitle("Card & Pricing"));
        cardPriceCard.add(Box.createVerticalStrut(14));

        cardNameLabel = styledValue();
        cardCodeLabel = styledValue();
        rarityLabel   = styledValue();

        listingPriceLabel = new JLabel("—");
        listingPriceLabel.setForeground(Theme.ACCENT_ALT);
        listingPriceLabel.setFont(Theme.FONT_BOLD.deriveFont(26f));
        listingPriceLabel.setAlignmentX(LEFT_ALIGNMENT);

        marketPriceLabel = new JLabel("—");
        marketPriceLabel.setForeground(Theme.ACCENT);
        marketPriceLabel.setFont(Theme.FONT_BOLD.deriveFont(20f));
        marketPriceLabel.setAlignmentX(LEFT_ALIGNMENT);

        cardPriceCard.add(infoRow("Card Name", cardNameLabel));
        cardPriceCard.add(Box.createVerticalStrut(12));
        cardPriceCard.add(infoRow("Card Code", cardCodeLabel));
        cardPriceCard.add(Box.createVerticalStrut(12));
        cardPriceCard.add(infoRow("Rarity",    rarityLabel));
        cardPriceCard.add(Box.createVerticalStrut(20));
        cardPriceCard.add(styledFormLabel("Listing Price"));
        cardPriceCard.add(Box.createVerticalStrut(6));
        cardPriceCard.add(listingPriceLabel);
        cardPriceCard.add(Box.createVerticalStrut(14));
        cardPriceCard.add(styledFormLabel("Market Price"));
        cardPriceCard.add(Box.createVerticalStrut(6));
        cardPriceCard.add(marketPriceLabel);
        cardPriceCard.add(Box.createVerticalGlue());

        rightStack.add(sellerCard);
        rightStack.add(Box.createVerticalStrut(12));
        rightStack.add(cardPriceCard);
        rightStack.add(Box.createVerticalGlue());

        contentRow.add(rightStack);
        page.add(contentRow);
        page.add(Box.createVerticalGlue());

        setLayout(new BorderLayout());
        setOpaque(false);
        add(UiFactory.scrollWrap(page), BorderLayout.CENTER);
    }

    public void loadDetail(int cardId, String username) {
        currentCardId = cardId;

        imageLabel.setIcon(null);
        imageLabel.setText("Loading…");
        sellerUsernameLabel.setText("—");
        storeNameLabel.setText("—");
        phoneLabel.setText("—");
        addressLabel.setText("—");
        storeDescLabel.setText("<html><body style='width:360px'>—</body></html>");
        cardNameLabel.setText("—");
        cardCodeLabel.setText("—");
        rarityLabel.setText("—");
        listingPriceLabel.setText("—");
        marketPriceLabel.setText("—");

        // Use GetCardSalesDetail with both IDs for exact seller+card match
        String[] d = salesDetailService.getCardSalesDetail(cardId, username);

        if (d == null) {
            cardNameLabel.setText("Not found");
            revalidate();
            repaint();
            return;
        }

        // [0] SellerUsername  [1] SellerID   [2] StoreName      [3] Address
        // [4] City            [5] State      [6] ZipCode        [7] SellerDescription
        // [8] Phone           [9] CardID     [10] CardName      [11] CardCode
        // [12] Rarity         [13] ListingPrice                 [14] MarketPrice
        sellerUsernameLabel.setText(d[0]);
        storeNameLabel.setText(d[2]);
        phoneLabel.setText(d[8]);
        addressLabel.setText(d[3] + ", " + d[4] + ", " + d[5] + " " + d[6]);
        storeDescLabel.setText("<html><body style='width:360px'>" + d[7] + "</body></html>");
        cardNameLabel.setText(d[10]);
        cardCodeLabel.setText(d[11]);
        rarityLabel.setText(d[12]);

        try {
            listingPriceLabel.setText(String.format("$%.2f", Double.parseDouble(d[13])));
        } catch (NumberFormatException e) {
            listingPriceLabel.setText("$" + d[13]);
        }
        try {
            marketPriceLabel.setText(String.format("$%.2f", Double.parseDouble(d[14])));
        } catch (NumberFormatException e) {
            marketPriceLabel.setText("$" + d[14]);
        }

        revalidate();
        repaint();

        String imageUrl = cardSearchService.getCardImageUrl(cardId);
        new Thread(() -> {
            if (imageUrl == null || imageUrl.equals("—")) {
                SwingUtilities.invokeLater(() -> {
                    imageLabel.setIcon(null);
                    imageLabel.setText("No Image");
                });
                return;
            }
            try {
                Image raw = ImageIO.read(new URL(imageUrl));
                if (raw == null) {
                    SwingUtilities.invokeLater(() -> imageLabel.setText("No Image"));
                    return;
                }
                Image scaled = raw.getScaledInstance(362, 518, Image.SCALE_SMOOTH);
                SwingUtilities.invokeLater(() -> {
                    imageLabel.setIcon(new ImageIcon(scaled));
                    imageLabel.setText(null);
                });
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> imageLabel.setText("Image unavailable"));
            }
        }).start();
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
    
    /**
     * Sets where the back button navigates.
     * Call before navigating to this page from a non-library source.
     */
    public void setBackAction(Runnable action) {
        this.backAction = action;
    }

    /**
     * Sets the back button label text.
     * Call before navigating to this page from a non-library source.
     */
    public void setBackLabel(String label) {
        backBtn.setText(label);
    }
}