package yot.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Image;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

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
import yot.services.CollectionService.CollectionCard;
import yot.services.DatabaseConnectionService;

public class CollectionDetailPage extends JPanel {
	private static final int CARD_TILE_WIDTH = 360;
	private static final int CARD_TILE_GAP = 8;
	private static final int CARD_NAME_MAX_WIDTH = 190;
	private static final float CARD_NAME_MIN_FONT_SIZE = 10f;

    private final JLabel titleLabel;
    private final CollectionService collectionService;
    private final DatabaseConnectionService dbService;
    private final JPanel cardsPanel;
    private final JPanel cardsGridPanel;
    private final JPanel searchPanelContainer;
    private int currentCollectionID = -1;
    private String currentCollectionName = "All Cards";
	private final String username;
	private final Consumer<Integer> onOpenCardDetail;
	private final Map<Integer, ImageIcon> cardImageCache = new HashMap<Integer, ImageIcon>();
	private ArrayList<CardRow> currentRows = new ArrayList<CardRow>();
	
	private static class CardRow {
		private final CollectionCard card;
		private final int quantity;
		private final boolean allowMutations;
		
		private CardRow(CollectionCard card, int quantity, boolean allowMutations) {
			this.card = card;
			this.quantity = quantity;
			this.allowMutations = allowMutations;
		}
	}
	
	private static class OwnershipEnforcementResult {
		private final boolean success;
		private final int affectedCollections;
		private final int totalDecrements;
		
		private OwnershipEnforcementResult(boolean success, int affectedCollections, int totalDecrements) {
			this.success = success;
			this.affectedCollections = affectedCollections;
			this.totalDecrements = totalDecrements;
		}
	}

    public CollectionDetailPage(Runnable onBack, Consumer<Integer> onOpenCardDetail, DatabaseConnectionService dbService, String username) {
    	this.dbService = dbService;
    	this.collectionService = new CollectionService(dbService);
    	this.username = username;
    	this.onOpenCardDetail = onOpenCardDetail;
        JPanel page = UiFactory.pageContainer();

        JPanel top = UiFactory.rowPanel();
        top.setAlignmentX(LEFT_ALIGNMENT);
        top.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        titleLabel = new JLabel("Collection: All Cards");
        titleLabel.setFont(Theme.FONT_PAGE);
        titleLabel.setForeground(Theme.TEXT);
        JLabel sub = new JLabel("View every card in this collection by image and name.");
        sub.setForeground(Theme.MUTED);
        header.add(titleLabel);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);
        JButton back = UiFactory.outlineButton("Back to Collections");
        back.addActionListener(e -> onBack.run());
        top.add(header);
        top.add(Box.createHorizontalGlue());
        top.add(back);
        page.add(top);

        searchPanelContainer = new JPanel();
        searchPanelContainer.setOpaque(false);
        searchPanelContainer.setLayout(new BoxLayout(searchPanelContainer, BoxLayout.Y_AXIS));
        rebuildSearchPanel();
        page.add(searchPanelContainer);

        cardsPanel = UiFactory.panelCard();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
        cardsGridPanel = new JPanel();
        cardsGridPanel.setLayout(new BoxLayout(cardsGridPanel, BoxLayout.Y_AXIS));
        cardsGridPanel.setOpaque(false);
        cardsGridPanel.setAlignmentX(LEFT_ALIGNMENT);
        cardsPanel.add(UiFactory.sectionTitle("Cards in Collection"));
        cardsPanel.add(Box.createVerticalStrut(10));
        cardsPanel.add(cardsGridPanel);
        cardsPanel.addComponentListener(new ComponentAdapter() {
        	@Override
        	public void componentResized(ComponentEvent e) {
        		relayoutCardGrid();
        	}
        });
        addComponentListener(new ComponentAdapter() {
        	@Override
        	public void componentResized(ComponentEvent e) {
        		relayoutCardGrid();
        	}
        });
        rebuildCards(new ArrayList<CardRow>());
        page.add(Box.createVerticalStrut(14));
        page.add(cardsPanel);

        setLayout(new BorderLayout());
        setOpaque(false);
        add(UiFactory.scrollWrap(page), BorderLayout.CENTER);
    }

    public void showCollection(int collectionID, String collectionName) {
    	currentCollectionID = collectionID;
    	currentCollectionName = collectionName;
        titleLabel.setText("Collection: " + collectionName);
        rebuildSearchPanel();
        
        ArrayList<CardRow> rows = new ArrayList<CardRow>();
        if (collectionID < 0) {
        	// Default "All Cards" view: show all cards the user owns.
        	for (int cardID : collectionService.getUserCardIDs(username)) {
        		String cardName = collectionService.getCardNameFromID(cardID);
        		int quantity = collectionService.getUserCardQuantity(cardID, username);
        		rows.add(new CardRow(new CollectionCard(cardID, cardName), quantity, true));
        	}
        } else {
        	for (CollectionCard card : collectionService.getCollectionCardEntries(collectionID)) {
        		int quantity = collectionService.getCollectionCardQuantity(collectionID, card.getId());
        		rows.add(new CardRow(card, quantity, true));
        	}
        }
        
        rebuildCards(rows);
    }
    
    private void rebuildSearchPanel() {
    	searchPanelContainer.removeAll();
    	CardSearchPanel addCard;
    	if (currentCollectionID < 0) {
    		addCard = new CardSearchPanel(this::queryFnForOwned, this::cardFnForOwned, dbService);
    	} else {
    		addCard = new CardSearchPanel(this::queryFnForCollection, this::cardFnForCollection, dbService);
    	}
    	searchPanelContainer.add(addCard);
    	searchPanelContainer.revalidate();
    	searchPanelContainer.repaint();
    }

    private void rebuildCards(ArrayList<CardRow> rows) {
    	currentRows = new ArrayList<CardRow>(rows);
    	relayoutCardGrid();
    }

    private void relayoutCardGrid() {
    	cardsGridPanel.removeAll();

    	if (currentRows.isEmpty()) {
    		JLabel empty = new JLabel("No cards in this collection yet.");
    		empty.setForeground(Theme.MUTED);
    		empty.setAlignmentX(LEFT_ALIGNMENT);
    		cardsGridPanel.add(empty);
    		cardsGridPanel.revalidate();
    		cardsGridPanel.repaint();
    		return;
    	}

    	int availableWidth = getCardsViewportWidth();
    	int columns = Math.max(1, (availableWidth + CARD_TILE_GAP) / (CARD_TILE_WIDTH + CARD_TILE_GAP));
    	int index = 0;
    	while (index < currentRows.size()) {
    		JPanel rowPanel = new JPanel();
    		rowPanel.setOpaque(false);
    		rowPanel.setAlignmentX(LEFT_ALIGNMENT);
    		rowPanel.setLayout(new BoxLayout(rowPanel, BoxLayout.X_AXIS));
    		
    		for (int col = 0; col < columns && index < currentRows.size(); col++) {
    			rowPanel.add(cardTile(currentRows.get(index)));
    			index++;
    			if (col < columns - 1 && index < currentRows.size()) {
    				rowPanel.add(Box.createHorizontalStrut(CARD_TILE_GAP));
    			}
    		}
    		rowPanel.add(Box.createHorizontalGlue());
    		cardsGridPanel.add(rowPanel);
    		if (index < currentRows.size()) {
    			cardsGridPanel.add(Box.createVerticalStrut(CARD_TILE_GAP));
    		}
    	}

    	cardsPanel.revalidate();
    	cardsPanel.repaint();
    }
    
    private int getCardsViewportWidth() {
    	JViewport viewport = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, cardsPanel);
    	if (viewport != null && viewport.getWidth() > 0) {
    		return Math.max(CARD_TILE_WIDTH, viewport.getWidth() - 32);
    	}
    	if (cardsPanel.getWidth() > 0) {
    		return Math.max(CARD_TILE_WIDTH, cardsPanel.getWidth() - 32);
    	}
    	return CARD_TILE_WIDTH;
    }

    private JPanel cardTile(CardRow row) {
    	CollectionCard card = row.card;
        JPanel tile = new JPanel();
        tile.setBackground(new Color(35, 45, 80));
        tile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                new EmptyBorder(12, 12, 12, 12)
        ));
        tile.setLayout(new BoxLayout(tile, BoxLayout.X_AXIS));
        tile.setPreferredSize(new Dimension(CARD_TILE_WIDTH, 174));
        tile.setMaximumSize(new Dimension(CARD_TILE_WIDTH, 174));
        tile.setMinimumSize(new Dimension(CARD_TILE_WIDTH, 174));

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
        JLabel quantityLabel = new JLabel("Quantity: " + row.quantity);
        quantityLabel.setForeground(Theme.MUTED);
        right.add(quantityLabel);
        right.add(Box.createVerticalStrut(8));

        if (row.allowMutations) {
	        JButton addBtn = UiFactory.primaryButton("Add 1");
	        addBtn.addActionListener(e -> {
	        	if (currentCollectionID < 0) {
	        		if (collectionService.incrementOwnedCard(card.getId(), username)) {
	        			showCollection(currentCollectionID, currentCollectionName);
	        		}
	        		return;
	        	}
	        	int collectionQty = collectionService.getCollectionCardQuantity(currentCollectionID, card.getId());
	        	int ownedQty = collectionService.getUserCardQuantity(card.getId(), username);
	        	if (collectionQty >= ownedQty) {
	        		JOptionPane.showMessageDialog(
	        				this,
	        				"Cannot add more than the total owned quantity.",
	        				"Quantity Limit",
	        				JOptionPane.WARNING_MESSAGE
	        		);
	        		return;
	        	}
	        	if (collectionService.addCardIntoCollection(currentCollectionID, card.getId(), username)) {
	        		showCollection(currentCollectionID, currentCollectionName);
	        	}
	        });
	        right.add(addBtn);
	        right.add(Box.createVerticalStrut(8));
	
	        JButton removeBtn = UiFactory.dangerButton("Remove 1");
	        removeBtn.addActionListener(e -> {
	        	if (currentCollectionID < 0) {
	        		int ownedQuantity = collectionService.getUserCardQuantity(card.getId(), username);
	        		if (ownedQuantity <= 0) {
	        			return;
	        		}
	        		if (ownedQuantity == 1) {
	        			int choice = JOptionPane.showOptionDialog(
	        					this,
	        					"Remove \"" + card.getName() + "\" from All Cards? This will remove the card from any collections it is in.",
	        					"Remove Card",
	        					JOptionPane.YES_NO_OPTION,
	        					JOptionPane.WARNING_MESSAGE,
	        					null,
	        					new String[] { "Yes", "No" },
	        					"No"
	        			);
	        			if (choice != JOptionPane.YES_OPTION) {
	        				return;
	        			}
	        		}
	        		if (!collectionService.decrementOwnedCard(card.getId(), username)) {
	        			return;
	        		}
	        		int newOwnedQuantity = collectionService.getUserCardQuantity(card.getId(), username);
	        		OwnershipEnforcementResult enforcementResult =
	        				enforceOwnedQuantityLimitAcrossCollections(card.getId(), newOwnedQuantity);
	        		if (!enforcementResult.success) {
	        			return;
	        		}
	        		if (enforcementResult.totalDecrements > 0) {
	        			JOptionPane.showMessageDialog(
	        					this,
	        					"Updated " + enforcementResult.affectedCollections + " collection(s) and removed "
	        							+ enforcementResult.totalDecrements + " extra card entr"
	        							+ (enforcementResult.totalDecrements == 1 ? "y" : "ies")
	        							+ " to match your owned quantity.",
	        					"Collections Updated",
	        					JOptionPane.INFORMATION_MESSAGE
	        			);
	        		}
	        		showCollection(currentCollectionID, currentCollectionName);
	        		return;
	        	}
	        	
	        	int quantity = collectionService.getCollectionCardQuantity(currentCollectionID, card.getId());
	        	boolean shouldDelete = true;
	        	if (quantity == 1) {
	        		int choice = JOptionPane.showOptionDialog(
	        				this,
	        				"Remove \"" + card.getName() + "\" from the collection?",
	        				"Remove Card",
	        				JOptionPane.YES_NO_OPTION,
	        				JOptionPane.WARNING_MESSAGE,
	        				null,
	        				new String[] { "Yes", "No" },
	        				"No"
	        		);
	        		shouldDelete = choice == JOptionPane.YES_OPTION;
	        	}
	
	        	if (!shouldDelete) {
	        		return;
	        	}
	
	        	if (collectionService.deleteCardFromCollection(currentCollectionID, card.getId())) {
	        		showCollection(currentCollectionID, currentCollectionName);
	        	}
	        });
	        right.add(removeBtn);
        }

        tile.add(image);
        tile.add(Box.createHorizontalStrut(12));
        tile.add(right);
        return tile;
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
        	wireCardImageClick(image, cardID);
        	return image;
        }

        String imageUrl = collectionService.getCardImage(cardID);
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
        	image.setText("No Image");
        	wireCardImageClick(image, cardID);
        	return image;
        }

        try {
        	Image raw = ImageIO.read(new URL(imageUrl));
        	if (raw == null) {
        		image.setText("No Image");
        		wireCardImageClick(image, cardID);
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

        wireCardImageClick(image, cardID);
        return image;
    }
    
    private void wireCardImageClick(JLabel imageLabel, int cardID) {
    	imageLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    	imageLabel.addMouseListener(new MouseAdapter() {
    		@Override
    		public void mouseClicked(MouseEvent e) {
    			onOpenCardDetail.accept(cardID);
    		}
    	});
    }
    
    private void fitCardNameFont(JLabel label, String text) {
    	Font currentFont = Theme.FONT_BOLD;
    	FontMetrics metrics = label.getFontMetrics(currentFont);
    	while (metrics.stringWidth(text) > CARD_NAME_MAX_WIDTH && currentFont.getSize2D() > CARD_NAME_MIN_FONT_SIZE) {
    		currentFont = currentFont.deriveFont(currentFont.getSize2D() - 1f);
    		metrics = label.getFontMetrics(currentFont);
    	}
    	label.setFont(currentFont);
    }
    
    private OwnershipEnforcementResult enforceOwnedQuantityLimitAcrossCollections(int cardID, int ownedQuantity) {
    	int affectedCollections = 0;
    	int totalDecrements = 0;
    	for (int collectionID : collectionService.getCollectionIDs(username)) {
    		int collectionQuantity = collectionService.getCollectionCardQuantity(collectionID, cardID);
    		int decrementsForCollection = 0;
    		while (collectionQuantity > ownedQuantity) {
    			if (!collectionService.deleteCardFromCollection(collectionID, cardID)) {
    				return new OwnershipEnforcementResult(false, affectedCollections, totalDecrements);
    			}
    			collectionQuantity--;
    			decrementsForCollection++;
    		}
    		if (decrementsForCollection > 0) {
    			affectedCollections++;
    			totalDecrements += decrementsForCollection;
    		}
    	}
    	return new OwnershipEnforcementResult(true, affectedCollections, totalDecrements);
    }
    
    public List<Integer> queryFnForCollection(Map<String, String> map) {
    	//what cards are queried. Should be only cards in the user's collection
    	ArrayList<Integer> cards = collectionService.getUserCardIDsWithFilter(username, map);
//    	System.out.println(cards);
    	return cards;
    }
    
    void cardFnForCollection(int id) {
    	//What happens when a user clicks on a card image
    	//add card to collection
    	if (collectionService.addCardIntoCollection(currentCollectionID, id, username)) {
    		showCollection(currentCollectionID, currentCollectionName);
    	}
    }
    
    public List<Integer> queryFnForOwned(Map<String, String> map) {
    	//what cards are queried. Should be only cards in the user's collection
    	ArrayList<Integer> cards = collectionService.getCardIDsWithFilter(username, map);
//    	System.out.println(cards);
    	return cards;
    }
    
    void cardFnForOwned(int id) {
    	//What happens when a user clicks on a card image
    	//add card to collection
    	if (collectionService.addCardToOwned(id, username)) {
    		showCollection(currentCollectionID, currentCollectionName);
    	}
    }
}
