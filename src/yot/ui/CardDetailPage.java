package yot.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
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

import yot.services.CardDetailService;
import yot.services.DatabaseConnectionService;

public class CardDetailPage extends JPanel {

    // pageTitleLabel — fixed "Card Detail" text in the top bar (never changes)
    // cardNameLabel  — shows the actual card name inside the info panel (changes per card)
    private final JLabel cardNameLabel;
    private final JLabel idLabel;
    private final JLabel rarityLabel;
    private final JLabel setCodeLabel;
    private final JLabel typeLabel;
    private final JLabel attributeLabel;
    private final JLabel raceLabel;
    private final JLabel levelLabel;
    private final JLabel atkLabel;
    private final JLabel defLabel;
    private final JLabel marketPriceLabel;
    private final javax.swing.JTextArea descriptionLabel;
    private final JLabel imageLabel;
    private JButton addToCollectionBtn;
    private String username;
    private Integer cardIDCheck;
    private CardDetailService cardDetailService;

    private Runnable backAction;
    private final JButton backBtn;

    public CardDetailPage(Runnable onBack, DatabaseConnectionService dbService, String username) {
        this.username = username;
        this.cardDetailService = new CardDetailService(dbService);
        this.backAction = onBack;

        JPanel page = UiFactory.pageContainer();

        // ── Top bar ───────────────────────────────────────────────────────────
        JPanel top = UiFactory.rowPanel();
        top.setAlignmentX(LEFT_ALIGNMENT);
        top.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        // Separate label for the page header — never re-added anywhere else
        JLabel pageTitleLabel = new JLabel("Card Detail");
        pageTitleLabel.setFont(Theme.FONT_PAGE);
        pageTitleLabel.setForeground(Theme.TEXT);

        JLabel sub = new JLabel("Full card information from the database.");
        sub.setForeground(Theme.MUTED);

        header.add(pageTitleLabel);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);

        backBtn = UiFactory.outlineButton("← Back to Card Library");
        backBtn.addActionListener(e -> {
            if (backAction != null) backAction.run();
        });

        top.add(header);
        top.add(Box.createHorizontalGlue());
        top.add(backBtn);
        page.add(top);
        page.add(Box.createVerticalStrut(14));

        // ── Content row: image + info ─────────────────────────────────────────
        JPanel contentRow = UiFactory.rowPanel();
        contentRow.setAlignmentX(LEFT_ALIGNMENT);
        contentRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // ── Image panel ───────────────────────────────────────────────────────
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
        imageCard.add(imageLabel);

        imageCard.add(Box.createVerticalStrut(10));

        // ── Button panel below image ──────────────────────────────────────────
        JPanel buttonCard = UiFactory.panelCard();
        buttonCard.setLayout(new BoxLayout(buttonCard, BoxLayout.Y_AXIS));
        buttonCard.setBorder(new EmptyBorder(8, 16, 8, 16));
        buttonCard.setPreferredSize(new Dimension(394, 44));
        buttonCard.setMaximumSize(new Dimension(394, 44));
        buttonCard.setMinimumSize(new Dimension(394, 44));
        addToCollectionBtn = UiFactory.primaryButton("Add Card to Collection");
        addToCollectionBtn.setAlignmentX(CENTER_ALIGNMENT);
        addToCollectionBtn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 28));
        addToCollectionBtn.setMinimumSize(new Dimension(362, 28));
        addToCollectionBtn.setPreferredSize(new Dimension(362, 28));
        addToCollectionBtn.addActionListener(e -> {
            AddCardToOwned(cardIDCheck);
            revalidate();
            repaint();
        });
        buttonCard.add(UiFactory.fillButton(addToCollectionBtn));

        JPanel leftStack = new JPanel();
        leftStack.setOpaque(false);
        leftStack.setLayout(new BoxLayout(leftStack, BoxLayout.Y_AXIS));
        leftStack.add(imageCard);
        leftStack.add(Box.createVerticalStrut(8));
        leftStack.add(buttonCard);
        contentRow.add(leftStack);
        contentRow.add(Box.createHorizontalStrut(14));

        // ── Info panel ────────────────────────────────────────────────────────
        JPanel infoCard = UiFactory.panelCard();
        infoCard.setLayout(new BoxLayout(infoCard, BoxLayout.Y_AXIS));
        infoCard.setBorder(new EmptyBorder(16, 16, 16, 16));

        // Card name — separate label, only added to infoCard
        infoCard.add(UiFactory.sectionTitle("Card Name"));
        infoCard.add(Box.createVerticalStrut(4));
        cardNameLabel = new JLabel("—");
        cardNameLabel.setFont(Theme.FONT_PAGE);
        cardNameLabel.setForeground(Theme.TEXT);
        cardNameLabel.setAlignmentX(LEFT_ALIGNMENT);
        infoCard.add(cardNameLabel);
        infoCard.add(Box.createVerticalStrut(12));

        infoCard.add(UiFactory.formLabel("Card ID"));
        idLabel = new JLabel("—");
        idLabel.setForeground(Theme.MUTED);
        idLabel.setFont(Theme.FONT.deriveFont(16f));
        idLabel.setAlignmentX(LEFT_ALIGNMENT);
        infoCard.add(idLabel);
        infoCard.add(Box.createVerticalStrut(10));

        JPanel metaRow = UiFactory.rowPanel();
        metaRow.setAlignmentX(LEFT_ALIGNMENT);
        JPanel rarityBlock = infoBlock("Rarity");
        rarityLabel = new JLabel("—");
        rarityLabel.setForeground(Theme.ACCENT);
        rarityLabel.setFont(Theme.FONT_BOLD.deriveFont(16f));
        rarityBlock.add(rarityLabel);
        JPanel codeBlock = infoBlock("Code");
        setCodeLabel = new JLabel("—");
        setCodeLabel.setForeground(Theme.ACCENT_ALT);
        setCodeLabel.setFont(Theme.FONT_BOLD.deriveFont(16f));
        codeBlock.add(setCodeLabel);
        metaRow.add(rarityBlock);
        metaRow.add(Box.createHorizontalStrut(24));
        metaRow.add(codeBlock);
        infoCard.add(metaRow);
        infoCard.add(Box.createVerticalStrut(10));

        JPanel typeRow = UiFactory.rowPanel();
        typeRow.setAlignmentX(LEFT_ALIGNMENT);
        JPanel typeBlock = infoBlock("Type");
        typeLabel = new JLabel("—");
        typeLabel.setForeground(Theme.TEXT);
        typeLabel.setFont(Theme.FONT.deriveFont(16f));
        typeBlock.add(typeLabel);
        JPanel attrBlock = infoBlock("Attribute");
        attributeLabel = new JLabel("—");
        attributeLabel.setForeground(Theme.TEXT);
        attributeLabel.setFont(Theme.FONT.deriveFont(16f));
        attrBlock.add(attributeLabel);
        JPanel raceBlock = infoBlock("Race");
        raceLabel = new JLabel("—");
        raceLabel.setForeground(Theme.TEXT);
        raceLabel.setFont(Theme.FONT.deriveFont(16f));
        raceBlock.add(raceLabel);
        typeRow.add(typeBlock);
        typeRow.add(Box.createHorizontalStrut(24));
        typeRow.add(attrBlock);
        typeRow.add(Box.createHorizontalStrut(24));
        typeRow.add(raceBlock);
        infoCard.add(typeRow);
        infoCard.add(Box.createVerticalStrut(10));

        JPanel statsRow = UiFactory.rowPanel();
        statsRow.setAlignmentX(LEFT_ALIGNMENT);
        JPanel lvlBlock = infoBlock("Level / Rank");
        levelLabel = new JLabel("—");
        levelLabel.setForeground(Theme.TEXT);
        levelLabel.setFont(Theme.FONT.deriveFont(16f));
        lvlBlock.add(levelLabel);
        JPanel atkBlock = infoBlock("ATK");
        atkLabel = new JLabel("—");
        atkLabel.setForeground(new Color(255, 200, 120));
        atkLabel.setFont(Theme.FONT_BOLD.deriveFont(16f));
        atkBlock.add(atkLabel);
        JPanel defBlock = infoBlock("DEF");
        defLabel = new JLabel("—");
        defLabel.setForeground(new Color(120, 200, 255));
        defLabel.setFont(Theme.FONT_BOLD.deriveFont(16f));
        defBlock.add(defLabel);
        statsRow.add(lvlBlock);
        statsRow.add(Box.createHorizontalStrut(24));
        statsRow.add(atkBlock);
        statsRow.add(Box.createHorizontalStrut(24));
        statsRow.add(defBlock);
        infoCard.add(statsRow);
        infoCard.add(Box.createVerticalStrut(10));

        JPanel priceBlock = infoBlock("Market Price");
        marketPriceLabel = new JLabel("—");
        marketPriceLabel.setForeground(Theme.ACCENT_ALT);
        marketPriceLabel.setFont(Theme.FONT_BOLD.deriveFont(16f));
        priceBlock.add(marketPriceLabel);
        infoCard.add(priceBlock);
        infoCard.add(Box.createVerticalStrut(10));

        infoCard.add(UiFactory.sectionTitle("Card Description"));
        infoCard.add(Box.createVerticalStrut(6));
        descriptionLabel = new javax.swing.JTextArea("—");
        descriptionLabel.setForeground(Theme.MUTED);
        descriptionLabel.setFont(Theme.FONT.deriveFont(16f));
        descriptionLabel.setBackground(Theme.PANEL);  
        descriptionLabel.setLineWrap(true);            
        descriptionLabel.setWrapStyleWord(true);       
        descriptionLabel.setEditable(false);           
        descriptionLabel.setAlignmentX(LEFT_ALIGNMENT);
        infoCard.add(descriptionLabel);

        contentRow.add(infoCard);
        page.add(contentRow);

        setLayout(new BorderLayout());
        setOpaque(false);
        add(UiFactory.scrollWrap(page), BorderLayout.CENTER);
    }

    public void setCardData(Integer cardID) {
        String[] cardData = cardDetailService.getCardById(cardID);
        if (cardData == null || cardData.length < 1) return;

        cardIDCheck = Integer.valueOf(cardData[0]);

        imageLabel.setIcon(null);
        imageLabel.setText("Loading…");
        addToCollectionBtn.setText("Checking…");
        addToCollectionBtn.setEnabled(false);
        addToCollectionBtn.setBackground(new Color(60, 70, 100));
        addToCollectionBtn.setForeground(Theme.MUTED);

        // [0] ID  [1] Name  [2] Code  [3] Rarity  [4] Description
        // [5] MarketPrice   [6] Type  [7] ATK  [8] DEF  [9] Level
        // [10] Race  [11] Attribute  [12] ImageURL  [13] SetID
        cardNameLabel.setText(cardData[1]);
        idLabel.setText("ID: " + cardData[0]);
        rarityLabel.setText(cardData[3]);
        setCodeLabel.setText(cardData[2]);
        typeLabel.setText(cardData[6]);
        attributeLabel.setText(cardData[11]);
        raceLabel.setText(cardData[10]);
        levelLabel.setText(cardData[9]);
        atkLabel.setText(cardData[7]);
        defLabel.setText(cardData[8]);
        marketPriceLabel.setText("$" + cardData[5]);
        descriptionLabel.setText(cardData[4]);
        revalidate();
        repaint();

        String imageUrl = cardData[12];
        String cardIdStr = cardData[0];

        new Thread(() -> {
            loadImage(imageUrl);
            boolean owned = cardDetailService.isCardOwned(cardIdStr, username);
            SwingUtilities.invokeLater(() -> {
                if (owned) {
                    addToCollectionBtn.setText("Already Owned Card");
                    addToCollectionBtn.setEnabled(false);
                    addToCollectionBtn.setBackground(new Color(60, 70, 100));
                    addToCollectionBtn.setForeground(Theme.MUTED);
                } else {
                    addToCollectionBtn.setText("Add Card to Collection");
                    addToCollectionBtn.setEnabled(true);
                    addToCollectionBtn.setBackground(Theme.ACCENT);
                    addToCollectionBtn.setForeground(java.awt.Color.WHITE);
                }
            });
        }).start();
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

    public void resetBackToLibrary(Runnable libraryAction) {
        this.backAction = libraryAction;
        backBtn.setText("← Back to Card Library");
    }

    private void loadImage(String imageUrl) {
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
                SwingUtilities.invokeLater(() -> {
                    imageLabel.setIcon(null);
                    imageLabel.setText("No Image");
                });
                return;
            }
            Image scaled = raw.getScaledInstance(362, 518, Image.SCALE_SMOOTH);
            ImageIcon icon = new ImageIcon(scaled);
            SwingUtilities.invokeLater(() -> {
                imageLabel.setIcon(icon);
                imageLabel.setText(null);
            });
        } catch (IOException e) {
            System.out.println("Failed to load card image: " + imageUrl);
            SwingUtilities.invokeLater(() -> {
                imageLabel.setIcon(null);
                imageLabel.setText("Image unavailable");
            });
        }
    }
    
    @Override
    public void addNotify() {
        super.addNotify();
        // Listen on the top-level window for resize events
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
        float base  = 16f * scale;
        float bold  = 16f * scale;
        float title = 28f * scale;

        cardNameLabel.setFont(Theme.FONT_PAGE.deriveFont(title));
        idLabel.setFont(Theme.FONT.deriveFont(base));
        rarityLabel.setFont(Theme.FONT_BOLD.deriveFont(bold));
        setCodeLabel.setFont(Theme.FONT_BOLD.deriveFont(bold));
        typeLabel.setFont(Theme.FONT.deriveFont(base));
        attributeLabel.setFont(Theme.FONT.deriveFont(base));
        raceLabel.setFont(Theme.FONT.deriveFont(base));
        levelLabel.setFont(Theme.FONT.deriveFont(base));
        atkLabel.setFont(Theme.FONT_BOLD.deriveFont(bold));
        defLabel.setFont(Theme.FONT_BOLD.deriveFont(bold));
        marketPriceLabel.setFont(Theme.FONT_BOLD.deriveFont(bold));
        descriptionLabel.setFont(Theme.FONT.deriveFont(base));

        revalidate();
        repaint();
    }

    private JPanel infoBlock(String labelText) {
        JPanel block = new JPanel();
        block.setOpaque(false);
        block.setLayout(new BoxLayout(block, BoxLayout.Y_AXIS));
        JLabel lbl = UiFactory.formLabel(labelText);
        lbl.setAlignmentX(LEFT_ALIGNMENT);
        block.add(lbl);
        block.add(Box.createVerticalStrut(2));
        return block;
    }

    private void AddCardToOwned(int CardID) {
        cardDetailService.AddCardToOwned(CardID, username);
        SwingUtilities.invokeLater(() -> {
            addToCollectionBtn.setText("Already Owned Card");
            addToCollectionBtn.setEnabled(false);
            addToCollectionBtn.setBackground(new Color(60, 70, 100));
            addToCollectionBtn.setForeground(Theme.MUTED);
        });
    }
}