package yot.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import yot.services.CardSearchService;
import yot.services.DatabaseConnectionService;

public class CardSearchPanel extends JPanel {
	
    private final Function<Map<String, String>, List<Integer>> searchFn;
    private final Consumer<Integer> onCardClick;
    
    private static List<Integer> currentResults = null;
    private static Map<String, String> currentSearchParams = null;
    private static int currentPage = 0;
    private static final int RESULTS_PER_PAGE = 50;
    
    private JPanel cardsPanel;
    private JLabel pageLabel;
    private JButton prevButton;
    private JButton nextButton;
    
    private JPanel results = null;
    
    private CardSearchService cardSearchService = null;

    public CardSearchPanel(Function<Map<String, String>, List<Integer>> searchFn, Consumer<Integer> onCardClick, DatabaseConnectionService dbService) {
        this.searchFn = searchFn;
        this.onCardClick = onCardClick;
        this.cardSearchService = new CardSearchService(dbService);

        JPanel container = UiFactory.panelCard();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        container.setBorder(new EmptyBorder(16, 16, 16, 16));
        container.add(UiFactory.sectionTitle("Search Cards"));
        container.add(Box.createVerticalStrut(10));
        
        JPanel searcher = UiFactory.panelCard();
        searcher.setBorder(null);
        searcher.setLayout(new BoxLayout(searcher, BoxLayout.Y_AXIS));
        
        
        JPanel simpleFilters = UiFactory.rowPanel();
        simpleFilters.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        simpleFilters.setAlignmentX(LEFT_ALIGNMENT);
        JTextField nameInput = UiFactory.input("");
        JTextField codeInput = UiFactory.input("");
        simpleFilters.add(createValueFilter("Card Name", nameInput));
        simpleFilters.add(Box.createHorizontalStrut(8));
        simpleFilters.add(createValueFilter("Card Code", codeInput));
        searcher.add(simpleFilters);
        searcher.add(Box.createVerticalStrut(8));
        
        JPanel buttons = UiFactory.rowPanel();
        buttons.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        buttons.setAlignmentX(LEFT_ALIGNMENT);
        JButton searchButton = UiFactory.primaryButton("Search");
        buttons.add(searchButton);
        buttons.add(Box.createHorizontalStrut(8));
        JButton advancedButton = UiFactory.softButton("Advanced Search");
        buttons.add(advancedButton);
        searcher.add(buttons);
        searcher.add(Box.createVerticalStrut(8));
        
        JPanel advancedFilters = UiFactory.panelCard();
        advancedFilters.setLayout(new BoxLayout(advancedFilters, BoxLayout.Y_AXIS));
        advancedFilters.setVisible(false);
        advancedFilters.add(Box.createVerticalStrut(8));
        
        JPanel row1 = UiFactory.rowPanel();
        row1.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        row1.setAlignmentX(LEFT_ALIGNMENT);
        row1.add(Box.createHorizontalStrut(8));
        JComboBox<String> typeDropdown = new JComboBox<>(new DefaultComboBoxModel<>());
        row1.add(createOptionFilter("Type", typeDropdown, cardSearchService.getFilterOptions("Type")));
        row1.add(Box.createHorizontalStrut(8));
        JComboBox<String> attributeDropdown = new JComboBox<>(new DefaultComboBoxModel<>());
        row1.add(createOptionFilter("Attribute", attributeDropdown, cardSearchService.getFilterOptions("Attribute")));
        row1.add(Box.createHorizontalStrut(8));
        JComboBox<String> raceDropdown = new JComboBox<>(new DefaultComboBoxModel<>());
        row1.add(createOptionFilter("Race", raceDropdown, cardSearchService.getFilterOptions("Race")));
        row1.add(Box.createHorizontalStrut(8));
        JComboBox<String> rarityDropdown = new JComboBox<>(new DefaultComboBoxModel<>());
        row1.add(createOptionFilter("Rarity", rarityDropdown, cardSearchService.getFilterOptions("Rarity")));
        row1.add(Box.createHorizontalStrut(8));
        advancedFilters.add(row1);
        
        JPanel row2 = UiFactory.rowPanel();
        row2.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        row2.setAlignmentX(LEFT_ALIGNMENT);
        row2.add(Box.createHorizontalStrut(8));
        JTextField atkInput = UiFactory.input("");
        installSignedIntegerFilter(atkInput);
        row2.add(createValueFilter("ATK", atkInput));
        row2.add(Box.createHorizontalStrut(8));
        JTextField defInput = UiFactory.input("");
        installSignedIntegerFilter(defInput);
        row2.add(createValueFilter("DEF", defInput));
        row2.add(Box.createHorizontalStrut(8));
        JTextField levelInput = UiFactory.input("");
        installSignedIntegerFilter(levelInput);
        row2.add(createValueFilter("Level", levelInput));
        row2.add(Box.createHorizontalStrut(8));
        advancedFilters.add(row2);
        
        
        advancedFilters.add(Box.createVerticalStrut(8));
        searcher.add(advancedFilters);
        
        advancedButton.addActionListener(e -> advancedFilters.setVisible(!advancedFilters.isVisible()));
        
        container.add(searcher);
        container.add(Box.createVerticalStrut(12));
        
        results = UiFactory.panelCard();
		results.setLayout(new BoxLayout(results, BoxLayout.Y_AXIS));
		results.setVisible(false);
		
		this.cardsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		this.cardsPanel.setMaximumSize(new Dimension(900, Integer.MAX_VALUE));
		this.cardsPanel.setPreferredSize(new Dimension(0, 608));
		this.cardsPanel.setOpaque(false);
		this.cardsPanel.setAlignmentX(LEFT_ALIGNMENT);
		
		JScrollPane scrollWrapper = UiFactory.scrollWrap(this.cardsPanel);
//		scrollWrapper.setMinimumSize(new Dimension(cardsPanel.getWidth(), 600));
		
		JPanel paginationPanel = UiFactory.rowPanel();
		paginationPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		paginationPanel.setAlignmentX(LEFT_ALIGNMENT);
		
		this.prevButton = UiFactory.softButton("< Previous");
		this.prevButton.setEnabled(false);
		paginationPanel.add(this.prevButton);
		paginationPanel.add(Box.createHorizontalGlue());
		
		this.pageLabel = new JLabel("Page 1");
		this.pageLabel.setForeground(Theme.TEXT);
		paginationPanel.add(this.pageLabel);
		paginationPanel.add(Box.createHorizontalGlue());
		
		this.nextButton = UiFactory.softButton("Next >");
		this.nextButton.setEnabled(false);
		paginationPanel.add(this.nextButton);
		
		results.add(scrollWrapper);
		results.add(Box.createVerticalStrut(8));
		results.add(paginationPanel);
		
		searchButton.addActionListener(e -> {
        	currentSearchParams = new HashMap<>();
        	if (!nameInput.getText().isEmpty()) {
        		currentSearchParams.put("Name", nameInput.getText());
        	}
        	if (!codeInput.getText().isEmpty()) {
        		currentSearchParams.put("Code", codeInput.getText());
        	}
        	if (typeDropdown.getSelectedItem() != null && !typeDropdown.getSelectedItem().toString().isEmpty()) {
        		currentSearchParams.put("Type", typeDropdown.getSelectedItem().toString());
        	}
        	if (attributeDropdown.getSelectedItem() != null && !attributeDropdown.getSelectedItem().toString().isEmpty()) {
				currentSearchParams.put("Attribute", attributeDropdown.getSelectedItem().toString());
			}
        	if (raceDropdown.getSelectedItem() != null && !raceDropdown.getSelectedItem().toString().isEmpty()) {
        		currentSearchParams.put("Race", raceDropdown.getSelectedItem().toString());
        	}
        	if (rarityDropdown.getSelectedItem() != null && !rarityDropdown.getSelectedItem().toString().isEmpty()) {
				currentSearchParams.put("Rarity", rarityDropdown.getSelectedItem().toString());
			}
        	if (!atkInput.getText().isEmpty()) {
        		currentSearchParams.put("ATK", atkInput.getText());
        	}
        	if (!defInput.getText().isEmpty()) {
        		currentSearchParams.put("DEF", defInput.getText());
        	}
        	if (!levelInput.getText().isEmpty()) {
				currentSearchParams.put("Level", levelInput.getText());
			}
        	
//        	System.out.println("Performing search with parameters:" + currentSearchParams);
        	
        	currentResults = searchFn.apply(currentSearchParams);
        	currentPage = 0;
//        	System.out.println("Search results: " + currentResults);
        	
        	if (currentResults != null && !currentResults.isEmpty()) {
        		results.setVisible(true);
        		displayResultsPage();
        	} else {
        		results.setVisible(false);
        	}
        	
        	results.revalidate();
        	results.repaint();
        	
        });
		
		prevButton.addActionListener(e -> {
			if (currentPage > 0) {
				currentPage--;
				displayResultsPage();
				results.revalidate();
				results.repaint();
			}
		});
		
		nextButton.addActionListener(e -> {
			int totalPages = (currentResults.size() + RESULTS_PER_PAGE - 1) / RESULTS_PER_PAGE;
			if (currentPage < totalPages - 1) {
				currentPage++;
				displayResultsPage();
				results.revalidate();
				results.repaint();
			}
		});
		
//		results.setPreferredSize(new Dimension(results.getWidth(), container.getHeight()));
		container.add(results);

        add(container);
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
    }
    
    private JPanel createValueFilter(String label, JTextField input) {
		JPanel panel = UiFactory.panelCard();
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
		panel.setBorder(null);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.add(UiFactory.formLabel(label));
		panel.add(Box.createVerticalStrut(4));
		panel.add(input);
		return panel;
	}
    
    private JPanel createOptionFilter(String label, JComboBox<String> dropdown, List<String> options) {
    	JPanel panel = UiFactory.panelCard();
    	panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
    	panel.setBorder(null);
    	panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
    	panel.setAlignmentX(LEFT_ALIGNMENT);
    	panel.add(UiFactory.formLabel(label));
    	panel.add(Box.createVerticalStrut(4));
    	DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
    	model.addElement("");
    	for (String option : options) {
    		model.addElement(option);
    	}
    	dropdown.setModel(model);
    	dropdown.setFocusable(false);
    	dropdown.setBackground(Theme.PANEL);
    	dropdown.setForeground(Theme.TEXT);
    	dropdown.setOpaque(true);
    	dropdown.setAlignmentX(LEFT_ALIGNMENT);
    	panel.add(dropdown);
    	return panel;
    }
    
    private ImageIcon getCardImageFromUrl(String imageUrl, int width, int height) {
		try {
			ImageIcon cardImage = new ImageIcon(new URL(imageUrl));
			Image scaledImage = cardImage.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
			return new ImageIcon(scaledImage);
		} catch (Exception e) {
			System.err.println("Failed to load image from URL: " + imageUrl);
			e.printStackTrace();
			// Return a placeholder if image fails to load
			return new ImageIcon();
		}
	}
    
    private JPanel createCardResultItem(Integer cardId) {
		JPanel cardPanel = new JPanel(new BorderLayout());
		cardPanel.setPreferredSize(new Dimension(80, 116));
		cardPanel.setMaximumSize(new Dimension(80, 116));
		cardPanel.setMinimumSize(new Dimension(80, 116));
		cardPanel.setBorder(javax.swing.BorderFactory.createLineBorder(Theme.BORDER));
		cardPanel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		cardPanel.setBackground(Theme.PANEL);
		
		String imageUrl = this.cardSearchService.getCardImageUrl(cardId);
		ImageIcon scaledIcon = getCardImageFromUrl(imageUrl, 80, 116);
		
		JLabel imageLabel = new JLabel(scaledIcon);
		imageLabel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
		
		// ...existing code...
		imageLabel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onCardClick.accept(cardId);
			}
		});
		
		cardPanel.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				onCardClick.accept(cardId);
			}
		});
		
		cardPanel.add(imageLabel, BorderLayout.CENTER);
		return cardPanel;
	}
    
    private void displayResultsPage() {
    	if (currentResults == null || cardsPanel == null) return;
    	
    	// Clear and repopulate cards panel
    	cardsPanel.removeAll();
    	
    	int startIdx = currentPage * RESULTS_PER_PAGE;
    	int endIdx = Math.min(startIdx + RESULTS_PER_PAGE, currentResults.size());
    	
    	for (int i = startIdx; i < endIdx; i++) {
    		cardsPanel.add(createCardResultItem(currentResults.get(i)));
    	}
    	
    	// Update page label
    	int totalPages = (currentResults.size() + RESULTS_PER_PAGE - 1) / RESULTS_PER_PAGE;
    	pageLabel.setText("Page " + (currentPage + 1) + " of " + totalPages);
    	
    	// Update button states
    	prevButton.setEnabled(currentPage > 0);
    	nextButton.setEnabled(currentPage < totalPages - 1);
    }

    private static void installSignedIntegerFilter(JTextField field) {
    	((AbstractDocument) field.getDocument()).setDocumentFilter(new DocumentFilter() {
    		@Override
    		public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
    			replace(fb, offset, 0, string, attr);
    		}

    		@Override
    		public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
    			if (text == null) {
    				text = "";
    			}
    			int docLen = fb.getDocument().getLength();
    			String current = docLen == 0 ? "" : fb.getDocument().getText(0, docLen);
    			StringBuilder merged = new StringBuilder();
    			for (int i = 0; i < current.length(); i++) {
    				if (i < offset) {
    					merged.append(current.charAt(i));
    				}
    			}
    			merged.append(text);
    			for (int i = offset + length; i < current.length(); i++) {
    				merged.append(current.charAt(i));
    			}
    			String sanitized = sanitizeSignedInteger(merged.toString());
    			fb.replace(0, docLen, sanitized, attrs);
    		}
    	});
    }

    private static String sanitizeSignedInteger(String raw) {
    	boolean negative = false;
    	StringBuilder digits = new StringBuilder();
    	for (int i = 0; i < raw.length(); i++) {
    		char c = raw.charAt(i);
    		if (c == '-' && digits.length() == 0 && !negative) {
    			negative = true;
    		} else if (Character.isDigit(c)) {
    			digits.append(c);
    		}
    	}
    	if (negative) {
    		return "-" + digits;
    	}
    	return digits.toString();
    }
    
}
