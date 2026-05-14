package yot.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import yot.services.CollectionService;
import yot.services.DatabaseConnectionService;
import yot.services.SellerService;
import yot.services.SellerService.SellerListing;

/**
 * Seller-only page: search owned cards, add to listings, edit quantity/price, delete.
 */
public class MyListingsPage extends JPanel {
	private final String username;
	private final BiConsumer<String, Integer> onOpenListingDetail;
	private final CollectionService collectionService;
	private final SellerService sellerService;
	private final JPanel listingsBody;
	private final Map<Integer, ImageIcon> imageCache = new HashMap<Integer, ImageIcon>();
	
	//Update parameter
	public MyListingsPage(DatabaseConnectionService dbService, String username, BiConsumer<String, Integer> onOpenListingDetail) {
		this.username = username;
		this.onOpenListingDetail = onOpenListingDetail;
		this.collectionService = new CollectionService(dbService);
		this.sellerService = new SellerService(dbService);

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setOpaque(false);

		JPanel page = UiFactory.pageContainer();
		page.add(UiFactory.pageHeader("My Listings",
				"Search cards you own, then click a result to add a listing. Adjust quantity, price, or remove a listing."));
		page.add(Box.createVerticalStrut(14));

		CardSearchPanel search = new CardSearchPanel(this::queryOwnedCards, this::onSearchCardPicked, dbService);
		search.setAlignmentX(LEFT_ALIGNMENT);
		page.add(search);
		page.add(Box.createVerticalStrut(16));

		JPanel listCard = UiFactory.panelCard();
		listCard.setLayout(new BoxLayout(listCard, BoxLayout.Y_AXIS));
		listCard.setBorder(new EmptyBorder(16, 16, 16, 16));
		listCard.add(UiFactory.sectionTitle("Active Listings"));
		listCard.add(Box.createVerticalStrut(10));

		listingsBody = new JPanel();
		listingsBody.setOpaque(false);
		listingsBody.setLayout(new BoxLayout(listingsBody, BoxLayout.Y_AXIS));
		listingsBody.setAlignmentX(LEFT_ALIGNMENT);

		listCard.add(UiFactory.scrollWrap(listingsBody));
		page.add(listCard);

		add(UiFactory.scrollWrap(page));
		reloadListings();
	}

	private List<Integer> queryOwnedCards(Map<String, String> map) {
		return collectionService.getUserCardIDsWithFilter(username, map);
	}

	private void onSearchCardPicked(int cardId) {
		if (sellerService.addCardToListing(cardId, username)) {
			reloadListings();
		}
	}

	public void reloadListings() {
		listingsBody.removeAll();
		List<SellerListing> listings = sellerService.getSellerListings(username);
		if (listings.isEmpty()) {
			JLabel empty = new JLabel("No listings yet. Search above and click a card to create one.");
			empty.setForeground(Theme.MUTED);
			empty.setAlignmentX(LEFT_ALIGNMENT);
			listingsBody.add(empty);
		} else {
			for (int i = 0; i < listings.size(); i++) {
				listingsBody.add(buildListingRow(listings.get(i)));
				if (i < listings.size() - 1) {
					listingsBody.add(Box.createVerticalStrut(10));
				}
			}
		}
		listingsBody.revalidate();
		listingsBody.repaint();
	}

	private JPanel buildListingRow(SellerListing listing) {
		int cardId = listing.getCardId();
		String name = collectionService.getCardNameFromID(cardId);
		if (name == null || name.isEmpty()) {
			name = "Card #" + cardId;
		}

		JPanel row = new JPanel(new BorderLayout(12, 0));
		row.setBackground(new Color(35, 45, 80));
		row.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Theme.BORDER),
				new EmptyBorder(12, 12, 12, 12)));
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));
		row.setAlignmentX(LEFT_ALIGNMENT);

		JLabel imageLabel = buildListingImage(cardId);
		row.add(imageLabel, BorderLayout.WEST);

		JPanel center = new JPanel();
		center.setOpaque(false);
		center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
		center.setAlignmentX(LEFT_ALIGNMENT);

		JLabel nameLabel = new JLabel(name);
		nameLabel.setForeground(Theme.TEXT);
		nameLabel.setFont(Theme.FONT_BOLD);
		nameLabel.setAlignmentX(LEFT_ALIGNMENT);
		center.add(nameLabel);
		center.add(Box.createVerticalStrut(10));

		JPanel controls = UiFactory.rowPanel();
		controls.setOpaque(false);
		controls.setAlignmentX(LEFT_ALIGNMENT);

		JLabel priceLbl = new JLabel("Price $");
		priceLbl.setForeground(Theme.MUTED);
		controls.add(priceLbl);

		JTextField priceField = new JTextField(10);
		priceField.setText(listing.getPrice() != null ? listing.getPrice().toPlainString() : "0.00");
		priceField.setMaximumSize(new Dimension(120, 32));
		priceField.setBackground(new Color(30, 39, 71));
		priceField.setForeground(Theme.TEXT);
		priceField.setCaretColor(Theme.TEXT);
		priceField.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Theme.BORDER),
				new EmptyBorder(6, 8, 6, 8)));
		priceField.addFocusListener(new FocusAdapter() {
			@Override
			public void focusLost(FocusEvent e) {
				commitPrice(cardId, priceField);
			}
		});
		controls.add(priceField);
		controls.add(Box.createHorizontalStrut(16));

		JLabel qtyLabel = new JLabel("Qty: " + listing.getQuantity());
		qtyLabel.setForeground(Theme.TEXT);
		controls.add(qtyLabel);
		controls.add(Box.createHorizontalStrut(8));

		JButton minus = UiFactory.outlineButton("-");
		minus.addActionListener(e -> {
			if (sellerService.removeCardFromListing(cardId, username)) {
				reloadListings();
			}
		});
		controls.add(minus);
		controls.add(Box.createHorizontalStrut(6));

		JButton plus = UiFactory.primaryButton("+");
		plus.addActionListener(e -> {
			if (sellerService.addCardToListing(cardId, username)) {
				reloadListings();
			}
		});
		controls.add(plus);

		center.add(controls);
		row.add(center, BorderLayout.CENTER);

		JButton deleteBtn = UiFactory.dangerButton("Delete");
		deleteBtn.addActionListener(e -> {
			int choice = JOptionPane.showConfirmDialog(this,
					"Remove this listing entirely?",
					"Delete Listing",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (choice == JOptionPane.YES_OPTION && sellerService.deleteListing(cardId, username)) {
				reloadListings();
			}
		});
		row.add(deleteBtn, BorderLayout.EAST);

		return row;
	}

	private void commitPrice(int cardId, JTextField priceField) {
		try {
			String raw = priceField.getText().trim();
			if (raw.isEmpty()) {
				return;
			}
			BigDecimal price = new BigDecimal(raw);
			if (price.compareTo(BigDecimal.ZERO) < 0) {
				JOptionPane.showMessageDialog(this, "Price cannot be negative.", "Invalid price", JOptionPane.WARNING_MESSAGE);
				return;
			}
			if (sellerService.updateListingPrice(cardId, username, price)) {
				reloadListings();
			}
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Enter a valid decimal price.", "Invalid price", JOptionPane.WARNING_MESSAGE);
		}
	}

	private JLabel buildListingImage(int cardId) {
		JLabel label = new JLabel("", JLabel.CENTER);
		label.setOpaque(true);
		label.setBackground(new Color(32, 42, 79));
		label.setForeground(Theme.MUTED);
		label.setPreferredSize(new Dimension(100, 140));
		label.setMaximumSize(new Dimension(100, 140));
		label.setMinimumSize(new Dimension(100, 140));
		label.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

		ImageIcon cached = imageCache.get(cardId);
		if (cached != null) {
			label.setIcon(cached);
		} else {
			String url = collectionService.getCardImage(cardId);
			if (url != null && !url.trim().isEmpty()) {
				try {
					Image raw = ImageIO.read(new URL(url.trim()));
					if (raw != null) {
						Image scaled = raw.getScaledInstance(100, 140, Image.SCALE_SMOOTH);
						cached = new ImageIcon(scaled);
						imageCache.put(cardId, cached);
						label.setIcon(cached);
					} else {
						label.setText("No image");
					}
				} catch (IOException ex) {
					label.setText("No image");
				}
			} else {
				label.setText("No image");
			}
		}

		label.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onOpenListingDetail.accept(username, cardId);
			}
		});
		return label;
	}
}
