package yot.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Image;
import java.io.IOException;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

import yot.services.DatabaseConnectionService;

public class CardDetailPage extends JPanel {

    

    private final JLabel titleLabel;
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
    private final JLabel descriptionLabel;
    private final JLabel imageLabel;
    private JButton addToCollectionBtn;
    private DatabaseConnectionService dbService;
    private String username;
    private Integer cardID;

    public CardDetailPage(Runnable onBack, DatabaseConnectionService dbService, String username) {
    	this.dbService = dbService;
    	this.username = username;
    	

        JPanel page = UiFactory.pageContainer();

        // ── Top bar ───────────────────────────────────────────────────────────
        JPanel top = UiFactory.rowPanel();
        top.setAlignmentX(LEFT_ALIGNMENT);
        top.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        titleLabel = new JLabel("Card Detail");
        titleLabel.setFont(Theme.FONT_PAGE);
        titleLabel.setForeground(Theme.TEXT);

        JLabel sub = new JLabel("Full card information from the database.");
        sub.setForeground(Theme.MUTED);

        header.add(titleLabel);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);

        JButton back = UiFactory.outlineButton("← Back to Card Library");
        back.addActionListener(e -> onBack.run());

        top.add(header);
        top.add(Box.createHorizontalGlue());
        top.add(back);
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

        contentRow.add(imageCard);

        // Separate button panel below image panel
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
    	 	// TODO: wire to CollectionService.addCardToCollection()
     		AddCardToOwned(cardID);
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

        // Name
        infoCard.add(UiFactory.sectionTitle("Card Name"));
        infoCard.add(Box.createVerticalStrut(4));
        titleLabel.setAlignmentX(LEFT_ALIGNMENT);
        infoCard.add(titleLabel);
        infoCard.add(Box.createVerticalStrut(12));

        // ID
        infoCard.add(UiFactory.formLabel("Card ID"));
        idLabel = new JLabel("—");
        idLabel.setForeground(Theme.MUTED);
        idLabel.setFont(Theme.FONT);
        idLabel.setAlignmentX(LEFT_ALIGNMENT);
        infoCard.add(idLabel);
        infoCard.add(Box.createVerticalStrut(10));

        // Rarity + Code
        JPanel metaRow = UiFactory.rowPanel();
        metaRow.setAlignmentX(LEFT_ALIGNMENT);
        JPanel rarityBlock = infoBlock("Rarity");
        rarityLabel = new JLabel("—");
        rarityLabel.setForeground(Theme.ACCENT);
        rarityLabel.setFont(Theme.FONT_BOLD);
        rarityBlock.add(rarityLabel);
        JPanel codeBlock = infoBlock("Code");
        setCodeLabel = new JLabel("—");
        setCodeLabel.setForeground(Theme.ACCENT_ALT);
        setCodeLabel.setFont(Theme.FONT_BOLD);
        codeBlock.add(setCodeLabel);
        metaRow.add(rarityBlock);
        metaRow.add(Box.createHorizontalStrut(24));
        metaRow.add(codeBlock);
        infoCard.add(metaRow);
        infoCard.add(Box.createVerticalStrut(10));

        // Type + Attribute + Race
        JPanel typeRow = UiFactory.rowPanel();
        typeRow.setAlignmentX(LEFT_ALIGNMENT);
        JPanel typeBlock = infoBlock("Type");
        typeLabel = new JLabel("—");
        typeLabel.setForeground(Theme.TEXT);
        typeLabel.setFont(Theme.FONT);
        typeBlock.add(typeLabel);
        JPanel attrBlock = infoBlock("Attribute");
        attributeLabel = new JLabel("—");
        attributeLabel.setForeground(Theme.TEXT);
        attributeLabel.setFont(Theme.FONT);
        attrBlock.add(attributeLabel);
        JPanel raceBlock = infoBlock("Race");
        raceLabel = new JLabel("—");
        raceLabel.setForeground(Theme.TEXT);
        raceLabel.setFont(Theme.FONT);
        raceBlock.add(raceLabel);
        typeRow.add(typeBlock);
        typeRow.add(Box.createHorizontalStrut(24));
        typeRow.add(attrBlock);
        typeRow.add(Box.createHorizontalStrut(24));
        typeRow.add(raceBlock);
        infoCard.add(typeRow);
        infoCard.add(Box.createVerticalStrut(10));

        // Level + ATK + DEF
        JPanel statsRow = UiFactory.rowPanel();
        statsRow.setAlignmentX(LEFT_ALIGNMENT);
        JPanel lvlBlock = infoBlock("Level / Rank");
        levelLabel = new JLabel("—");
        levelLabel.setForeground(Theme.TEXT);
        levelLabel.setFont(Theme.FONT);
        lvlBlock.add(levelLabel);
        JPanel atkBlock = infoBlock("ATK");
        atkLabel = new JLabel("—");
        atkLabel.setForeground(new Color(255, 200, 120));
        atkLabel.setFont(Theme.FONT_BOLD);
        atkBlock.add(atkLabel);
        JPanel defBlock = infoBlock("DEF");
        defLabel = new JLabel("—");
        defLabel.setForeground(new Color(120, 200, 255));
        defLabel.setFont(Theme.FONT_BOLD);
        defBlock.add(defLabel);
        statsRow.add(lvlBlock);
        statsRow.add(Box.createHorizontalStrut(24));
        statsRow.add(atkBlock);
        statsRow.add(Box.createHorizontalStrut(24));
        statsRow.add(defBlock);
        infoCard.add(statsRow);
        infoCard.add(Box.createVerticalStrut(10));

        // Market Price
        JPanel priceBlock = infoBlock("Market Price");
        marketPriceLabel = new JLabel("—");
        marketPriceLabel.setForeground(Theme.ACCENT_ALT);
        marketPriceLabel.setFont(Theme.FONT_BOLD.deriveFont(16f));
        priceBlock.add(marketPriceLabel);
        infoCard.add(priceBlock);
        infoCard.add(Box.createVerticalStrut(10));

        // Description
        infoCard.add(UiFactory.sectionTitle("Card Description"));
        infoCard.add(Box.createVerticalStrut(6));
        descriptionLabel = new JLabel("<html><body style='width:420px'>—</body></html>");
        descriptionLabel.setForeground(Theme.MUTED);
        descriptionLabel.setFont(Theme.FONT);
        descriptionLabel.setAlignmentX(LEFT_ALIGNMENT);
        infoCard.add(descriptionLabel);
        infoCard.add(Box.createVerticalGlue());

        contentRow.add(infoCard);
        page.add(contentRow);
        page.add(Box.createVerticalGlue());

        setLayout(new BorderLayout());
        setOpaque(false);
        add(UiFactory.scrollWrap(page), BorderLayout.CENTER);
    }

    public void setCardData(String[] cardData) {
        if (cardData == null || cardData.length < 1) return;

        imageLabel.setIcon(null);
        imageLabel.setText("Loading…");

        String[] full = getCardById(cardData[0]);
        if (full == null) {
            titleLabel.setText("Card not found");
            idLabel.setText("—");
            rarityLabel.setText("—");
            setCodeLabel.setText("—");
            typeLabel.setText("—");
            attributeLabel.setText("—");
            raceLabel.setText("—");
            levelLabel.setText("—");
            atkLabel.setText("—");
            defLabel.setText("—");
            marketPriceLabel.setText("—");
            descriptionLabel.setText("<html><body style='width:420px'>No data found.</body></html>");
            imageLabel.setText("No Image");
            revalidate();
            repaint();
            return;
        }

        // [0] ID  [1] Name  [2] Code  [3] Rarity  [4] Description
        // [5] MarketPrice   [6] Type  [7] ATK  [8] DEF  [9] Level
        // [10] Race  [11] Attribute  [12] ImageURL  [13] SetID
        titleLabel.setText(full[1]);
        idLabel.setText("ID: " + full[0]);
        rarityLabel.setText(full[3]);
        setCodeLabel.setText(full[2]);
        typeLabel.setText(full[6]);
        attributeLabel.setText(full[11]);
        raceLabel.setText(full[10]);
        levelLabel.setText(full[9]);
        atkLabel.setText(full[7]);
        defLabel.setText(full[8]);
        marketPriceLabel.setText("$" + full[5]);
        descriptionLabel.setText(
                "<html><body style='width:420px'>" + full[4] + "</body></html>");

        revalidate();
        repaint();

        String imageUrl = full[12];
        String cardId = full[0];
        cardID = Integer.valueOf(cardId);
        new Thread(() -> {
            loadImage(imageUrl);
            boolean owned = isCardOwned(cardId);
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

            // Scaled to match imageLabel dimensions
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
    

    public String[] getCardById(String id) {
        Connection conn = dbService.getConnection();
        if (conn == null) {
            System.out.println("No active database connection.");
            return null;
        }
        
        String query = "{Call GetCardInfo (?)}";
        CallableStatement cs = null;
        
        try {
        	cs = conn.prepareCall(query);
            cs.setInt(1, Integer.parseInt(id));
            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                String[] card = new String[14];
                card[0]  = nullDataHandle(rs, "ID");
                card[1]  = nullDataHandle(rs, "Name");
                card[2]  = nullDataHandle(rs, "Code");
                card[3]  = nullDataHandle(rs, "Rarity");
                card[4]  = nullDataHandle(rs, "Description");
                card[5]  = nullDataHandle(rs, "MarketPrice");
                card[6]  = nullDataHandle(rs, "Type");
                card[7]  = nullDataHandle(rs, "ATK");
                card[8]  = nullDataHandle(rs, "DEF");
                card[9]  = nullDataHandle(rs, "Level");
                card[10] = nullDataHandle(rs, "Race");
                card[11] = nullDataHandle(rs, "Attribute");
                card[12] = nullDataHandle(rs, "ImageURL");
                card[13] = nullDataHandle(rs, "SetID");
                rs.close();
                return card;
            }
            rs.close();

        } catch (SQLException e) {
            System.out.println("Error fetching card by ID: " + id);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Invalid card ID format: " + id);
            e.printStackTrace();
        }

        return null;
    }

    private String nullDataHandle(ResultSet rs, String column) {
        try {
            String val = rs.getString(column);
            if (column == "ATK" || column == "DEF") {
            	if(val != null && val.equals("-1")) {
            		return "?";
            	}
            }
            return val != null ? val : "—";
        } catch (SQLException e) {
            return "—";
        }
    }
    
    /**
     * Checks if the current user already owns this card in any collection.
     * Query: Select * 
     * 		  From InCollection ic
     *        Join [Collection] c on ic.CollectionID = c.ID
     *        Join [User] u on c.UserID = u.ID
     *        Where u.Username = ? And ic.CardID = ?
     */
    private boolean isCardOwned(String cardId) {
        Connection conn = dbService.getConnection();
        if (conn == null) return false;

        String query = "{Call CardOwnershipCheck(?,?)}";
        try (CallableStatement cs = conn.prepareCall(query)) {
            cs.setInt(1, Integer.parseInt(cardId));
            cs.setString(2, username);
            ResultSet rs = cs.executeQuery();
            if (rs.next()) {
                return rs.getInt("Result") == 1;
            }
        } catch (SQLException e) {
            System.out.println("Error checking card ownership.");
            e.printStackTrace();
        }
        return false;
    }
    
    private void AddCardToOwned(int CardID) {
    	Connection conn = dbService.getConnection();
    	
    	String query = "{Call [dbo].[AddCardToOwned] (?, ?)}";
    	CallableStatement cs = null;
    	try {
    	    cs = conn.prepareCall(query);
    	    cs.setInt(1, CardID);
    	    cs.setString(2, username);
    	    cs.execute();
    	    // Disable button after successful add
    	    SwingUtilities.invokeLater(() -> {
    	        addToCollectionBtn.setText("Already Owned Card");
    	        addToCollectionBtn.setEnabled(false);
    	        addToCollectionBtn.setBackground(new Color(60, 70, 100));
    	        addToCollectionBtn.setForeground(Theme.MUTED);
    	    });
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	
    }
}