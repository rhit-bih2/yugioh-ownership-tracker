package yot.services;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class SellerService {

	public static class SellerListing {
		private final int cardId;
		private final int quantity;
		private final BigDecimal price;

		public SellerListing(int cardId, int quantity, BigDecimal price) {
			this.cardId = cardId;
			this.quantity = quantity;
			this.price = price;
		}

		public int getCardId() {
			return cardId;
		}

		public int getQuantity() {
			return quantity;
		}

		public BigDecimal getPrice() {
			return price;
		}
	}

	private DatabaseConnectionService dbService = null;
	
	public SellerService(DatabaseConnectionService dbService) {
		this.dbService = dbService;
	}

	/**
	 * Expected columns: CardID (int), Quantity (int), Price (money/decimal).
	 */
	public List<SellerListing> getSellerListings(String username) {
		ArrayList<SellerListing> out = new ArrayList<SellerListing>();
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call GetSellerListings(?)}");
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				int cardId = rs.getInt("CardID");
				int qty = rs.getInt("Quantity");
				BigDecimal price = rs.getBigDecimal("Price");
				if (rs.wasNull()) {
					price = BigDecimal.ZERO;
				}
				out.add(new SellerListing(cardId, qty, price));
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
		}
		return out;
	}
	
	public boolean addCardToListing(int cardID, String username) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call AddCardToListing(?, ?)}");
			stmt.setInt(1, cardID);
			stmt.setString(2, username);
			stmt.execute();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean removeCardFromListing(int cardID, String username) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call RemoveCardFromListing(?, ?)}");
			stmt.setInt(1, cardID);
			stmt.setString(2, username);
			stmt.execute();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean deleteListing(int cardID, String username) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call DeleteListing(?, ?)}");
			stmt.setInt(1, cardID);
			stmt.setString(2, username);
			stmt.execute();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
		return true;
	}

	public boolean updateListingPrice(int cardID, String username, BigDecimal price) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call UpdateListingPrice(?, ?, ?)}");
			stmt.setInt(1, cardID);
			stmt.setString(2, username);
			stmt.setBigDecimal(3, price);
			stmt.execute();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
		return true;
	}
}
