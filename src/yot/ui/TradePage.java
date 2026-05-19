package yot.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import yot.services.DatabaseConnectionService;
import yot.services.TradeService;
import yot.services.TradeService.TradeRequest;

public class TradePage extends JPanel {
	private final String username;
	private final TradeService tradeService;
	private final JPanel requestsBody;
	private final JTextField receiverInput;
	private final BiConsumer<String, Integer> onOpen;

	public TradePage(DatabaseConnectionService dbService, String username, BiConsumer<String, Integer> onOpen) {
		this.username = username;
		this.tradeService = new TradeService(dbService);
		this.onOpen = onOpen;

		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setOpaque(false);

		JPanel page = UiFactory.pageContainer();
		page.add(UiFactory.pageHeader("Trade", "Send trade requests and manage trades you are involved in."));
		page.add(Box.createVerticalStrut(14));

		JPanel sendCard = UiFactory.panelCard();
		sendCard.setLayout(new BoxLayout(sendCard, BoxLayout.Y_AXIS));
		sendCard.setBorder(new EmptyBorder(16, 16, 16, 16));
		sendCard.add(UiFactory.sectionTitle("New Trade Request"));
		sendCard.add(Box.createVerticalStrut(10));

		JPanel sendRow = UiFactory.rowPanel();
		sendRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
		sendRow.setAlignmentX(LEFT_ALIGNMENT);
		receiverInput = UiFactory.input("Receiver username");
		sendRow.add(receiverInput);
		sendRow.add(Box.createHorizontalStrut(8));
		JButton sendBtn = UiFactory.primaryButton("Send");
		sendBtn.addActionListener(e -> sendTradeRequest());
		sendRow.add(sendBtn);
		sendCard.add(sendRow);
		page.add(sendCard);
		page.add(Box.createVerticalStrut(14));

		JPanel listCard = UiFactory.panelCard();
		listCard.setLayout(new BoxLayout(listCard, BoxLayout.Y_AXIS));
		listCard.setBorder(new EmptyBorder(16, 16, 16, 16));
		listCard.add(UiFactory.sectionTitle("Trade Requests"));
		listCard.add(Box.createVerticalStrut(10));

		requestsBody = new JPanel();
		requestsBody.setOpaque(false);
		requestsBody.setLayout(new BoxLayout(requestsBody, BoxLayout.Y_AXIS));
		requestsBody.setAlignmentX(LEFT_ALIGNMENT);
		listCard.add(UiFactory.scrollWrap(requestsBody));
		page.add(listCard);

		add(UiFactory.scrollWrap(page));
		refresh();
	}

	public void refresh() {
		requestsBody.removeAll();
		List<TradeRequest> requests = tradeService.getTradeRequests(username);
		if (requests.isEmpty()) {
			JLabel empty = new JLabel("No trade requests yet.");
			empty.setForeground(Theme.MUTED);
			empty.setAlignmentX(LEFT_ALIGNMENT);
			requestsBody.add(empty);
		} else {
			for (int i = 0; i < requests.size(); i++) {
				requestsBody.add(buildTradeRow(requests.get(i)));
				if (i < requests.size() - 1) {
					requestsBody.add(Box.createVerticalStrut(8));
				}
			}
		}
		requestsBody.revalidate();
		requestsBody.repaint();
	}

	private void sendTradeRequest() {
		String receiver = receiverInput.getText().trim();
		if (receiver.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Enter a receiver username.", "Trade Request", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (receiver.equalsIgnoreCase(username)) {
			JOptionPane.showMessageDialog(this, "You cannot send a trade request to yourself.", "Trade Request", JOptionPane.WARNING_MESSAGE);
			return;
		}
		if (tradeService.createTR(username, receiver)) {
			receiverInput.setText("");
			refresh();
		}
	}

	private JPanel buildTradeRow(TradeRequest request) {
		JPanel row = new JPanel(new BorderLayout(12, 0));
		row.setBackground(new Color(35, 45, 80));
		row.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Theme.BORDER),
				new EmptyBorder(12, 12, 12, 12)));
		row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
		row.setAlignmentX(LEFT_ALIGNMENT);

		JButton openBtn = UiFactory.outlineButton("Open");
		// Reserved for future navigation to trade detail page.
		openBtn.addActionListener(e -> {
			this.onOpen.accept(this.username, request.getTradeRequestID());
		});
		row.add(openBtn, BorderLayout.WEST);

		JPanel center = UiFactory.rowPanel();
		center.setOpaque(false);
		center.setAlignmentX(LEFT_ALIGNMENT);

		JPanel namesColumn = new JPanel();
		namesColumn.setOpaque(false);
		namesColumn.setLayout(new BoxLayout(namesColumn, BoxLayout.Y_AXIS));
		namesColumn.setAlignmentX(LEFT_ALIGNMENT);

		JLabel senderLabel = new JLabel("Sender: " + request.getSenderUsername());
		senderLabel.setForeground(Theme.TEXT);
		senderLabel.setFont(Theme.FONT_BOLD);
		senderLabel.setAlignmentX(LEFT_ALIGNMENT);

		JLabel receiverLabel = new JLabel("Receiver: " + request.getReceiverUsername());
		receiverLabel.setForeground(Theme.MUTED);
		receiverLabel.setAlignmentX(LEFT_ALIGNMENT);

		namesColumn.add(senderLabel);
		namesColumn.add(Box.createVerticalStrut(4));
		namesColumn.add(receiverLabel);

		JLabel dateLabel = new JLabel("Sent: " + formatDateCreated(request.getDateCreated()));
		dateLabel.setForeground(Theme.MUTED);

		center.add(namesColumn);
		center.add(Box.createHorizontalStrut(24));
		center.add(dateLabel);
		center.add(Box.createHorizontalGlue());
		row.add(center, BorderLayout.CENTER);

		JButton declineBtn = UiFactory.dangerButton("Delete");
		declineBtn.addActionListener(e -> {
			int choice = JOptionPane.showConfirmDialog(this,
					"Delete this trade request?",
					"Delete Trade Request",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.WARNING_MESSAGE);
			if (choice == JOptionPane.YES_OPTION && tradeService.deleteTR(request.getTradeRequestID())) {
				refresh();
			}
		});
		row.add(declineBtn, BorderLayout.EAST);

		return row;
	}

	private String formatDateCreated(Date dateCreated) {
		if (dateCreated == null) {
			return "—";
		}
		return new SimpleDateFormat("MMM d, yyyy").format(dateCreated);
	}
}
