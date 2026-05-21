package yot.ui;

import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;

import yot.services.*;


public class SellerRegPage extends JPanel {
	private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{3}-\\d{3}-\\d{4}$");
	private static final Pattern ZIP_PATTERN = Pattern.compile("^\\d{5}$");
	private static final Set<String> VALID_STATE_ABBREVIATIONS = new HashSet<>(Arrays.asList(
			"AL", "AK", "AZ", "AR", "CA", "CO", "CT", "DE", "FL", "GA",
			"HI", "ID", "IL", "IN", "IA", "KS", "KY", "LA", "ME", "MD",
			"MA", "MI", "MN", "MS", "MO", "MT", "NE", "NV", "NH", "NJ",
			"NM", "NY", "NC", "ND", "OH", "OK", "OR", "PA", "RI", "SC",
			"SD", "TN", "TX", "UT", "VT", "VA", "WA", "WV", "WI", "WY", "DC"
	));

	private static String username;
	private static UserService userService;
	private static Consumer<String> loginFn;
	private static Consumer<String> switchPageFn;
	
    public SellerRegPage(Consumer<String> onLogin, Consumer<String> onSwitchPage, DatabaseConnectionService dbService) {
		userService = new UserService(dbService);
		loginFn = onLogin;
		switchPageFn = onSwitchPage;
    	
        setLayout(new GridBagLayout());
        setBackground(Theme.BG);
        setBorder(new EmptyBorder(24, 24, 24, 24));

        JPanel shell = new JPanel();
        shell.setOpaque(false);
        shell.setLayout(new BoxLayout(shell, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Yugioh Card Ownership Tracker");
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(Theme.TEXT);
        title.setAlignmentX(CENTER_ALIGNMENT);
        shell.add(title);
        shell.add(Box.createVerticalStrut(24));

        JPanel regWrap = new JPanel();
        regWrap.setOpaque(false);
        regWrap.setLayout(new BoxLayout(regWrap, BoxLayout.X_AXIS));
        JPanel regCard = UiFactory.panelCard();
        regCard.setLayout(new BoxLayout(regCard, BoxLayout.Y_AXIS));
//        regCard.setMinimumSize(new Dimension(1000, 850));
        regCard.setBorder(new EmptyBorder(24, 24, 24, 24));

        JLabel regTitle = new JLabel("Register as Seller");
        regTitle.setFont(Theme.FONT_BOLD.deriveFont(24f));
        regTitle.setForeground(Theme.TEXT);
        regTitle.setAlignmentX(LEFT_ALIGNMENT);
        JLabel regSub = new JLabel("Create a seller account with store information");
        regSub.setForeground(Theme.MUTED);
        regSub.setAlignmentX(LEFT_ALIGNMENT);

        regCard.add(regTitle);
        regCard.add(Box.createVerticalStrut(4));
        regCard.add(regSub);
        regCard.add(Box.createVerticalStrut(18));

        // Two-column layout
        JPanel twoColumnContainer = new JPanel();
        twoColumnContainer.setOpaque(false);
        twoColumnContainer.setLayout(new BoxLayout(twoColumnContainer, BoxLayout.X_AXIS));
        twoColumnContainer.setAlignmentX(LEFT_ALIGNMENT);

        // Left column
        JPanel leftColumn = new JPanel();
        leftColumn.setOpaque(false);
        leftColumn.setLayout(new BoxLayout(leftColumn, BoxLayout.Y_AXIS));
        leftColumn.setAlignmentX(LEFT_ALIGNMENT);

        // Right column
        JPanel rightColumn = new JPanel();
        rightColumn.setOpaque(false);
        rightColumn.setLayout(new BoxLayout(rightColumn, BoxLayout.Y_AXIS));
        rightColumn.setAlignmentX(LEFT_ALIGNMENT);

        // Left column fields
        leftColumn.add(UiFactory.formLabel("Username*"));
        leftColumn.add(Box.createVerticalStrut(6));
        JTextField usernameInput = UiFactory.input("");
        leftColumn.add(usernameInput);
        leftColumn.add(Box.createVerticalStrut(12));

        leftColumn.add(UiFactory.formLabel("Password"));
        leftColumn.add(Box.createVerticalStrut(6));
        JTextField passwordInput = UiFactory.passwordInput();
        leftColumn.add(passwordInput);
        leftColumn.add(Box.createVerticalStrut(12));

        leftColumn.add(UiFactory.formLabel("Phone Number*"));
        leftColumn.add(Box.createVerticalStrut(6));
        JTextField phoneInput = UiFactory.input("");
        installPhoneFilter(phoneInput);
        leftColumn.add(phoneInput);
        leftColumn.add(Box.createVerticalStrut(12));

        leftColumn.add(Box.createVerticalGlue());

        // Right column fields
        rightColumn.add(UiFactory.formLabel("Store Name*"));
        rightColumn.add(Box.createVerticalStrut(6));
        JTextField storeNameInput = UiFactory.input("");
        rightColumn.add(storeNameInput);
        rightColumn.add(Box.createVerticalStrut(12));

        rightColumn.add(UiFactory.formLabel("Street Address*"));
        rightColumn.add(Box.createVerticalStrut(6));
        JTextField streetInput = UiFactory.input("");
        rightColumn.add(streetInput);
        rightColumn.add(Box.createVerticalStrut(12));

        // State, City, Zip Code row
        JPanel stateCityZipRow = UiFactory.rowPanel();
        stateCityZipRow.setBorder(null);
        stateCityZipRow.setAlignmentX(LEFT_ALIGNMENT);
        
        JPanel stateCol = new JPanel();
        stateCol.setOpaque(false);
        stateCol.setLayout(new BoxLayout(stateCol, BoxLayout.Y_AXIS));
        stateCol.setAlignmentX(LEFT_ALIGNMENT);
        stateCol.add(UiFactory.formLabel("State*"));
        stateCol.add(Box.createVerticalStrut(6));
        JTextField stateInput = UiFactory.input("");
        installStateFilter(stateInput);
        stateCol.add(stateInput);
        stateCol.setMaximumSize(new Dimension(100, Integer.MAX_VALUE));
        stateCityZipRow.add(stateCol);
        stateCityZipRow.add(Box.createHorizontalStrut(12));

        JPanel cityCol = new JPanel();
        cityCol.setOpaque(false);
        cityCol.setLayout(new BoxLayout(cityCol, BoxLayout.Y_AXIS));
        cityCol.setAlignmentX(LEFT_ALIGNMENT);
        cityCol.add(UiFactory.formLabel("City*"));
        cityCol.add(Box.createVerticalStrut(6));
        JTextField cityInput = UiFactory.input("");
        cityCol.add(cityInput);
        cityCol.setMaximumSize(new Dimension(100, Integer.MAX_VALUE));
        stateCityZipRow.add(cityCol);
        stateCityZipRow.add(Box.createHorizontalStrut(12));

        JPanel zipCol = new JPanel();
        zipCol.setOpaque(false);
        zipCol.setLayout(new BoxLayout(zipCol, BoxLayout.Y_AXIS));
        zipCol.setAlignmentX(LEFT_ALIGNMENT);
        zipCol.add(UiFactory.formLabel("Zip Code*"));
        zipCol.add(Box.createVerticalStrut(6));
        JTextField zipInput = UiFactory.input("");
        installZipFilter(zipInput);
        zipCol.add(zipInput);
        zipCol.setMaximumSize(new Dimension(100, Integer.MAX_VALUE));
        stateCityZipRow.add(zipCol);
        stateCityZipRow.add(Box.createHorizontalGlue());

        rightColumn.add(stateCityZipRow);
        rightColumn.add(Box.createVerticalStrut(12));

        // Add columns to container
        twoColumnContainer.add(leftColumn);
        twoColumnContainer.add(Box.createHorizontalStrut(24));
        twoColumnContainer.add(rightColumn);

        regCard.add(twoColumnContainer);
//        regCard.add(Box.createVerticalStrut(12));

        // Store Description field - full width below columns
        JPanel descriptionWrap = new JPanel();
        descriptionWrap.setOpaque(false);
        descriptionWrap.setLayout(new BoxLayout(descriptionWrap, BoxLayout.Y_AXIS));
        descriptionWrap.setBorder(null);
        descriptionWrap.add(UiFactory.formLabel("Store Description"));
        descriptionWrap.add(Box.createVerticalStrut(6));
        JTextArea descriptionInput = new JTextArea();
        descriptionInput.setLineWrap(true);
        descriptionInput.setWrapStyleWord(true);
        descriptionInput.setBackground(new java.awt.Color(30, 39, 71));
        descriptionInput.setForeground(new java.awt.Color(141, 151, 196));
        descriptionInput.setCaretColor(Theme.TEXT);
        descriptionInput.setFont(Theme.FONT);
        descriptionInput.setBorder(javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(Theme.BORDER),
                new EmptyBorder(8, 10, 8, 10)
        ));
        descriptionInput.setAlignmentX(LEFT_ALIGNMENT);
        descriptionWrap.add(descriptionInput);
        regCard.add(descriptionWrap);
        regCard.add(Box.createVerticalStrut(18));

        JButton registerButton = UiFactory.primaryButton("Register as Seller");
        registerButton.addActionListener(e -> registerSeller(
            usernameInput.getText(),
            passwordInput.getText(),
            phoneInput.getText(),
            storeNameInput.getText(),
            streetInput.getText(),
            cityInput.getText(),
            stateInput.getText(),
            zipInput.getText(),
            descriptionInput.getText()
        ));
        regCard.add(UiFactory.fillButton(registerButton));

        regWrap.add(Box.createHorizontalGlue());
        regWrap.add(regCard);
        regWrap.add(Box.createHorizontalGlue());
        shell.add(regWrap);

        // Add bottom right button to switch back to landing page
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.X_AXIS));
        bottomPanel.add(Box.createHorizontalGlue());
        JButton backToLandingButton = UiFactory.softButton("Back to Login");
        backToLandingButton.addActionListener(e -> switchPageFn.accept("landing"));
        bottomPanel.add(backToLandingButton);
        bottomPanel.setBorder(new EmptyBorder(16, 0, 0, 0));
        shell.add(Box.createVerticalGlue());
        shell.add(bottomPanel);

        add(shell);
    }
    
    private void registerSeller(String username, String password, String phone, String storeName,
    		String streetAddress, String city, String state, String zipCode, String description) {
    	phone = phone.trim();
    	state = state.trim();
    	zipCode = zipCode.trim();

    	if (username.isEmpty() || phone.isEmpty() || storeName.isEmpty() ||
				streetAddress.isEmpty() || city.isEmpty() || state.isEmpty() || zipCode.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Please fill in all required fields.");
			return;
		}
    	if (!PHONE_PATTERN.matcher(phone).matches()) {
    		JOptionPane.showMessageDialog(null, "Phone number must be in the format XXX-XXX-XXXX.");
    		return;
    	}
    	String stateUpper = state.toUpperCase();
    	if (!VALID_STATE_ABBREVIATIONS.contains(stateUpper)) {
    		JOptionPane.showMessageDialog(null, "State must be a valid 2-letter US abbreviation (e.g. NY, CA).");
    		return;
    	}
    	if (!ZIP_PATTERN.matcher(zipCode).matches()) {
    		JOptionPane.showMessageDialog(null, "Zip code must be exactly 5 digits.");
    		return;
    	}
    	if(userService.registerSeller(username, password, phone, storeName, streetAddress, city, stateUpper, zipCode, description)) {
			SellerRegPage.username = username;
			loginFn.accept(username);
		} else {
			JOptionPane.showMessageDialog(null, "Invalid username or password.");
		}
    }
    
    public String getUsername() {
    	return SellerRegPage.username;
    }

    private static void installZipFilter(JTextField field) {
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
    			StringBuilder digits = new StringBuilder();
    			String current = fb.getDocument().getText(0, fb.getDocument().getLength());
    			for (int i = 0; i < current.length(); i++) {
    				if (i < offset && Character.isDigit(current.charAt(i))) {
    					digits.append(current.charAt(i));
    				} else if (i >= offset + length && Character.isDigit(current.charAt(i))) {
    					digits.append(current.charAt(i));
    				}
    			}
    			for (int i = 0; i < text.length(); i++) {
    				char c = text.charAt(i);
    				if (Character.isDigit(c) && digits.length() < 5) {
    					digits.append(c);
    				}
    			}
    			fb.replace(0, fb.getDocument().getLength(), digits.toString(), attrs);
    		}
    	});
    }

    private static void installStateFilter(JTextField field) {
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
    			StringBuilder letters = new StringBuilder();
    			String current = fb.getDocument().getText(0, fb.getDocument().getLength());
    			for (int i = 0; i < current.length(); i++) {
    				if (i < offset && Character.isLetter(current.charAt(i))) {
    					letters.append(Character.toUpperCase(current.charAt(i)));
    				} else if (i >= offset + length && Character.isLetter(current.charAt(i))) {
    					letters.append(Character.toUpperCase(current.charAt(i)));
    				}
    			}
    			for (int i = 0; i < text.length() && letters.length() < 2; i++) {
    				char c = text.charAt(i);
    				if (Character.isLetter(c)) {
    					letters.append(Character.toUpperCase(c));
    				}
    			}
    			fb.replace(0, fb.getDocument().getLength(), letters.toString(), attrs);
    		}
    	});
    }

    private static void installPhoneFilter(JTextField field) {
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
    			StringBuilder digits = new StringBuilder();
    			String current = fb.getDocument().getText(0, fb.getDocument().getLength());
    			for (int i = 0; i < current.length(); i++) {
    				if (i < offset && Character.isDigit(current.charAt(i))) {
    					digits.append(current.charAt(i));
    				} else if (i >= offset + length && Character.isDigit(current.charAt(i))) {
    					digits.append(current.charAt(i));
    				}
    			}
    			for (int i = 0; i < text.length(); i++) {
    				char c = text.charAt(i);
    				if (Character.isDigit(c) && digits.length() < 10) {
    					digits.append(c);
    				}
    			}
    			fb.replace(0, fb.getDocument().getLength(), formatPhoneDigits(digits.toString()), attrs);
    		}
    	});
    }

    private static String formatPhoneDigits(String digits) {
    	if (digits.length() > 10) {
    		digits = digits.substring(0, 10);
    	}
    	StringBuilder formatted = new StringBuilder();
    	for (int i = 0; i < digits.length(); i++) {
    		if (i == 3 || i == 6) {
    			formatted.append('-');
    		}
    		formatted.append(digits.charAt(i));
    	}
    	return formatted.toString();
    }
}