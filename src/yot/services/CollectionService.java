package yot.services;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
	
	
	
	public boolean createCollection(int userID, String name) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call CreateCollection(?, ?)}");
			stmt.setInt(1, userID);
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
	
	public int getUserID(String username) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call GetUserID(?)}");
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			rs.next();
			return rs.getInt("ID");
		}
		catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Failed to retrieve UserID");
			ex.printStackTrace();
			return -1;
		}
		
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
	
	public ArrayList<Integer> getCollectionIDs(int userID) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call GetUserCollectionIDs(?)}");
			stmt.setInt(1, userID);
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
					"An error ocurred while retrieving sodas by restaurants. See printed stack trace.");
			ex.printStackTrace();
			return new ArrayList<Integer>();
		}

	}
	
	private ArrayList<String> parseResultsString(ResultSet rs) {
		try {
			ArrayList<String> cardNames = new ArrayList<String>();
			while (rs.next()) {
				cardNames.add(rs.getString("Name"));
			}

			return cardNames;
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(null,
					"An error ocurred while retrieving sodas by restaurants. See printed stack trace.");
			ex.printStackTrace();
			return new ArrayList<String>();
		}

	}
	
	public ArrayList<String> getCollectionCards(int collectionID) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call GetCollectionCards(?)}");
			stmt.setInt(1, collectionID);
			ResultSet rs = stmt.executeQuery();
			return parseResultsString(rs);
		}
		catch (SQLException ex) {
			JOptionPane.showMessageDialog(null, "Failed to retrieve collection cards");
			ex.printStackTrace();
			return new ArrayList<String>();
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
	
	public boolean addCardIntoCollection(int collectionID, int cardID, int quantity) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call AddCardIntoCollection(?, ?, ?)}");
			stmt.setInt(1, collectionID);
			stmt.setInt(2, cardID);
			stmt.setInt(3, quantity);
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
}
