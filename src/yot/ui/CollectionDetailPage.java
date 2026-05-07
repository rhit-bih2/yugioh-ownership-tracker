package yot.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import yot.services.CollectionService;
import yot.services.CollectionService.CollectionCard;
import yot.services.DatabaseConnectionService;

public class CollectionDetailPage extends JPanel {
    private final JLabel titleLabel;
    private final CollectionService collectionService;
    private final JPanel cardsPanel;
    private int currentCollectionID = -1;
    private String currentCollectionName = "All Cards";

    public CollectionDetailPage(Runnable onBack, DatabaseConnectionService dbService, String username) {
    	this.collectionService = new CollectionService(dbService);
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

        JPanel addCard = UiFactory.panelCard();
        addCard.setLayout(new BoxLayout(addCard, BoxLayout.Y_AXIS));
        addCard.setBorder(new EmptyBorder(16, 16, 16, 16));
        addCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        addCard.add(UiFactory.sectionTitle("Add Card to Collection"));
        addCard.add(Box.createVerticalStrut(10));
        JPanel addRow = UiFactory.rowPanel();
        addRow.add(UiFactory.input("Card name"));
        addRow.add(Box.createHorizontalStrut(8));
        addRow.add(UiFactory.input("Image URL"));
        addRow.add(Box.createHorizontalStrut(8));
        addRow.add(UiFactory.primaryButton("Add Card"));
        page.add(Box.createVerticalStrut(14));
        page.add(addCard);

        cardsPanel = UiFactory.panelCard();
        cardsPanel.setLayout(new BoxLayout(cardsPanel, BoxLayout.Y_AXIS));
        cardsPanel.setBorder(new EmptyBorder(16, 16, 16, 16));
        cardsPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1600));
        rebuildCards(new ArrayList<CollectionCard>());
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
        ArrayList<CollectionCard> cards = collectionID < 0
                ? new ArrayList<CollectionCard>()
                : collectionService.getCollectionCardEntries(collectionID);
        rebuildCards(cards);
    }

    private void rebuildCards(ArrayList<CollectionCard> cards) {
    	cardsPanel.removeAll();
    	cardsPanel.add(UiFactory.sectionTitle("Cards in Collection"));
    	cardsPanel.add(Box.createVerticalStrut(10));
    	if (cards.isEmpty()) {
    		JLabel empty = new JLabel("No cards in this collection yet.");
    		empty.setForeground(Theme.MUTED);
    		cardsPanel.add(empty);
    	} else {
    		for (CollectionCard card : cards) {
    			cardsPanel.add(cardTile(card));
    			cardsPanel.add(Box.createVerticalStrut(8));
    		}
    	}
    	cardsPanel.revalidate();
    	cardsPanel.repaint();
    }

    private JPanel cardTile(CollectionCard card) {
        JPanel tile = new JPanel();
        tile.setBackground(new Color(35, 45, 80));
        tile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                new EmptyBorder(12, 12, 12, 12)
        ));
        tile.setLayout(new BoxLayout(tile, BoxLayout.X_AXIS));

        JLabel image = new JLabel("Card Image", SwingConstants.CENTER);
        image.setOpaque(true);
        image.setBackground(new Color(32, 42, 79));
        image.setForeground(Theme.MUTED);
        image.setPreferredSize(new Dimension(120, 150));
        image.setMaximumSize(new Dimension(120, 150));

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        JLabel cardName = new JLabel(card.getName());
        cardName.setForeground(Theme.TEXT);
        cardName.setFont(Theme.FONT_BOLD);
        right.add(cardName);
        right.add(Box.createVerticalStrut(8));

        JButton removeBtn = UiFactory.dangerButton("Remove");
        removeBtn.addActionListener(e -> {
        	if (currentCollectionID < 0) {
        		return;
        	}
        	if (collectionService.deleteCardFromCollection(currentCollectionID, card.getId())) {
        		showCollection(currentCollectionID, currentCollectionName);
        	}
        });
        right.add(removeBtn);

        tile.add(image);
        tile.add(Box.createHorizontalStrut(12));
        tile.add(right);
        return tile;
    }
}
