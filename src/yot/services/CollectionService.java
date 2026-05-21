package yot.services;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

public class CollectionService {

	public static class CollectionCard {
		private final int id;
		private final String name;

		public CollectionCard(int id, String name) {
			this.id = id;
			this.name = name;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}
	}

	private DatabaseConnectionService dbService = null;
	
	public CollectionService(DatabaseConnectionService dbService) {
		this.dbService = dbService;
	}
	
	
	
	public boolean createCollection(String username, String name) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call CreateCollection(?, ?)}");
			stmt.setString(1, username);
			stmt.setString(2, name);
			stmt.execute();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean deleteCollection(int collectionID) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call DeleteCollection(?)}");
			stmt.setInt(1, collectionID);
			stmt.execute();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean updateCollectionName(int collectionID, String name) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call UpdateCollectionName(?, ?)}");
			stmt.setString(1, name);
			stmt.setInt(2, collectionID);
			stmt.execute();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
		return true;
	}
	
	
	public String getCollectionName(int collectionID) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call GetCollectionName(?)}");
			stmt.setInt(1, collectionID);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			return rs.getString("Name");
		}
		catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Failed to retrieve collection name");
			ex.printStackTrace();
			return "";
		}
		
	}
	
	public ArrayList<Integer> getCollectionIDs(String username) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call GetUserCollectionIDs(?)}");
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			return parseResults(rs);
		}
		catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Failed to retrieve collections by user ID.");
			ex.printStackTrace();
			return new ArrayList<Integer>();
		}
	}
	
	private ArrayList<Integer> parseResults(ResultSet rs) {
		try {
			ArrayList<Integer> collectionIDs = new ArrayList<Integer>();
			while (rs.next()) {
				collectionIDs.add(rs.getInt("ID"));
			}

			return collectionIDs;
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null,
					"An error ocurred. See printed stack trace.");
			ex.printStackTrace();
			return new ArrayList<Integer>();
		}

	}

	public ArrayList<CollectionCard> getCollectionCardEntries(int collectionID) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call GetCollectionCards(?)}");
			stmt.setInt(1, collectionID);
			ResultSet rs = stmt.executeQuery();
			ArrayList<CollectionCard> cards = new ArrayList<CollectionCard>();
			while (rs.next()) {
				cards.add(new CollectionCard(rs.getInt("ID"), rs.getString("Name")));
			}
			return cards;
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Failed to retrieve collection cards");
			ex.printStackTrace();
			return new ArrayList<CollectionCard>();
		}
	}
	
	public boolean addCardIntoCollection(int collectionID, int cardID, String username) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call AddCardIntoCollection(?, ?, ?)}");
			stmt.setInt(1, collectionID);
			stmt.setInt(2, cardID);
			stmt.setString(3, username);
			stmt.execute();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean deleteCardFromCollection(int collectionID, int cardID) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call DeleteCardFromCollection(?, ?)}");
			stmt.setInt(1, collectionID);
			stmt.setInt(2, cardID);
			stmt.execute();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
		return true;
	}
	
	public int getCollectionCardQuantity(int collectionID, int cardID) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call getCollectionCardQuantity(?, ?)}");
			stmt.setInt(1, collectionID);
			stmt.setInt(2, cardID);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return 0;
		}
		return 0;
	}
	
	public int getUserCardQuantity(int cardID, String username) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call getUserCardQuantity(?, ?)}");
			stmt.setInt(1, cardID);
			stmt.setString(2, username);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return 0;
		}
		return 0;
	}
	
	public ArrayList<Integer> getUserCardIDs(String username) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call GetUserCardIDs(?)}");
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			ArrayList<Integer> collectionIDs = new ArrayList<Integer>();
			while (rs.next()) {
				collectionIDs.add(rs.getInt("CardID"));
			}
			return collectionIDs;
		}
		catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Failed to retrieve user card IDs");
			ex.printStackTrace();
			return new ArrayList<Integer>();
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(null, "One or more filter values have an invalid numeric format.");
			ex.printStackTrace();
			return new ArrayList<Integer>();
		}
	}
	
	public String getCardNameFromID(int cardID) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call GetCardNameFromID(?)}");
			stmt.setInt(1, cardID);
			ResultSet rs = stmt.executeQuery();
			if (rs.next()) {
				return rs.getString("Name");
			}
			return "";
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Failed to retrieve card name");
			ex.printStackTrace();
			return "";
		}
	}
	
	public boolean incrementOwnedCard(int cardID, String username) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call IncrementOwnedCard(?, ?)}");
			stmt.setInt(1, cardID);
			stmt.setString(2, username);
			stmt.execute();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean decrementOwnedCard(int cardID, String username) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call DecrementOwnedCard(?, ?)}");
			stmt.setInt(1, cardID);
			stmt.setString(2, username);
			stmt.execute();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
		return true;
	}
	
	public ArrayList<Integer> getUserCardIDsWithFilter(String username, Map<String, String> map) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call RetrieveCard(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
			setNullableString(stmt, 1, getFilterValue(map, "Name"));
			setNullableString(stmt, 2, getFilterValue(map, "Code"));
			setNullableString(stmt, 3, getFilterValue(map, "Rarity"));
			setNullableMoney(stmt, 4, getFilterValue(map, "MarketPrice"));
			setNullableString(stmt, 5, getFilterValue(map, "Type"));
			setNullableInt(stmt, 6, getFilterValue(map, "ATK"));
			setNullableInt(stmt, 7, getFilterValue(map, "DEF"));
			setNullableInt(stmt, 8, getFilterValue(map, "Level"));
			setNullableString(stmt, 9, getFilterValue(map, "Race"));
			setNullableString(stmt, 10, getFilterValue(map, "Attribute"));
			setNullableInt(stmt, 11, getFilterValue(map, "SetID"));
			
			ResultSet rs = stmt.executeQuery();
			ArrayList<Integer> filteredCardIDs = new ArrayList<Integer>();
			Set<Integer> ownedCardIDs = new HashSet<Integer>(getUserCardIDs(username));
			while (rs.next()) {
				int cardID = rs.getInt("ID");
				if (ownedCardIDs.contains(cardID)) {
					filteredCardIDs.add(cardID);
				}
			}
			return filteredCardIDs;
		}
		catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Failed to retrieve user card IDs");
			ex.printStackTrace();
			return new ArrayList<Integer>();
		}
	}
	
	private String getFilterValue(Map<String, String> map, String key) {
		if (map == null) {
			return null;
		}
		String value = map.get(key);
		if (value == null || value.trim().isEmpty()) {
			return null;
		}
		return value.trim();
	}
	
	private void setNullableString(CallableStatement stmt, int index, String value) throws SQLException {
		if (value == null) {
			stmt.setNull(index, Types.VARCHAR);
			return;
		}
		stmt.setString(index, value);
	}
	
	private void setNullableInt(CallableStatement stmt, int index, String value) throws SQLException {
		if (value == null) {
			stmt.setNull(index, Types.INTEGER);
			return;
		}
		stmt.setInt(index, Integer.parseInt(value));
	}
	
	private void setNullableMoney(CallableStatement stmt, int index, String value) throws SQLException {
		if (value == null) {
			stmt.setNull(index, Types.DECIMAL);
			return;
		}
		stmt.setBigDecimal(index, new BigDecimal(value));
	}
	
	public ArrayList<Integer> getCardIDsWithFilter(String username, Map<String, String> map) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call RetrieveCard(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}");
			setNullableString(stmt, 1, getFilterValue(map, "Name"));
			setNullableString(stmt, 2, getFilterValue(map, "Code"));
			setNullableString(stmt, 3, getFilterValue(map, "Rarity"));
			setNullableMoney(stmt, 4, getFilterValue(map, "MarketPrice"));
			setNullableString(stmt, 5, getFilterValue(map, "Type"));
			setNullableInt(stmt, 6, getFilterValue(map, "ATK"));
			setNullableInt(stmt, 7, getFilterValue(map, "DEF"));
			setNullableInt(stmt, 8, getFilterValue(map, "Level"));
			setNullableString(stmt, 9, getFilterValue(map, "Race"));
			setNullableString(stmt, 10, getFilterValue(map, "Attribute"));
			setNullableInt(stmt, 11, getFilterValue(map, "SetID"));
			
			ResultSet rs = stmt.executeQuery();
			ArrayList<Integer> filteredCardIDs = new ArrayList<Integer>();
			while (rs.next()) {
				int cardID = rs.getInt("ID");
				filteredCardIDs.add(cardID);
			}
			return filteredCardIDs;
		}
		catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Failed to retrieve card IDs");
			ex.printStackTrace();
			return new ArrayList<Integer>();
		}
	}
	
	public boolean addCardToOwned(int cardID, String username) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call AddCardToOwned(?, ?)}");
			stmt.setInt(1, cardID);
			stmt.setString(2, username);
			stmt.execute();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
		return true;
	}
	
	public String getCardImage(int cardID) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call GetCardImage(?)}");
			stmt.setInt(1, cardID);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			return rs.getString("ImageURL");
		}
		catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Failed to retrieve card image");
			ex.printStackTrace();
			return "";
		}
		
	}
}
