package yot.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JOptionPane;

import yot.services.TradeService.TradeRequest;

public class TradeDetailService {
	private DatabaseConnectionService dbService = null;
	
	public TradeDetailService(DatabaseConnectionService dbService, String username) {
		this.dbService = dbService;
	}
	
	public static class Card {
		private final int id;
		private final String name;
		private final int quantity;

		public Card(int id, String name, int quantity) {
			this.id = id;
			this.name = name;
			this.quantity = quantity;
		}

		public int getId() {
			return id;
		}

		public String getName() {
			return name;
		}
		
		public int getQuantity() {
			return quantity;
		}
	}
	
	public Map<String, String> getAllTradeInfo(int tradeID){ 
		Map<String, String> map = new HashMap<String,String>();
		Connection con = dbService.getConnection();
		CallableStatement cs = null;
		String query = "{Call GetAllTradeInfo(?)}";
		
		try {
			cs = con.prepareCall(query);
			cs.setInt(1, tradeID);
			ResultSet rs = cs.executeQuery();
			if (rs.next()) {	
				map.put("ID", rs.getString("ID"));
				map.put("IsComplete", rs.getString("IsComplete"));
				map.put("SenderConfirmed", rs.getString("SenderConfirmed"));
				map.put("ReceiverConfirmed", rs.getString("ReceiverConfirmed"));
				map.put("DateCreated", rs.getString("DateCreated"));
				map.put("SenderUsername", rs.getString("SenderUsername"));
				map.put("ReceiverUsername", rs.getString("ReceiverUsername"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			if(e.getMessage().contains("Invalid TradeInfoID")) {
				JOptionPane.showMessageDialog(null, "Trade does not exist");
			}
		}
		return map;
	}
	
	public ArrayList<Card> getCardsOffered(int tradeID, String username){
		ArrayList<Card> list = new ArrayList<Card>();
		Connection con = dbService.getConnection();
		CallableStatement cs = null;
		String query = "{Call GetCardsOffered(?,?)}";
		
		try {
			cs = con.prepareCall(query);
			cs.setInt(1, tradeID);
			cs.setString(2, username);
			ResultSet rs = cs.executeQuery();
			while (rs.next()) {
				int cardId = rs.getInt("CardID");
				String cardName = rs.getString("CardName");
				int quantity = rs.getInt("CardQuantity");
				list.add(new Card(cardId, cardName, quantity));
			}
		} catch (SQLException e) {
			e.printStackTrace();
			if(e.getMessage().contains("Invalid TradeInfoID")) {
				JOptionPane.showMessageDialog(null, "Trade aborted by the other user.");
			}
		}
		return list;
	}
	
	public boolean offerCard(int tradeID, int cardID, String username) {
		Connection con = dbService.getConnection();
		CallableStatement cs = null;
		String query = "{Call OfferCard(?,?,?)}";
		
		try {
			cs = con.prepareCall(query);
			cs.setInt(1, tradeID);
			cs.setInt(2, cardID);
			cs.setString(3, username);
			cs.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			if(e.getMessage().contains("Invalid TradeInfoID")) {
				JOptionPane.showMessageDialog(null, "Trade aborted by the other user.");
				return false;
			}
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	public boolean incrementCardOffered(int tradeID, int cardID, String username) {
		Connection con = dbService.getConnection();
		CallableStatement cs = null;
		String query = "{Call IncrementCardOffered(?,?,?)}";
		
		try {
			cs = con.prepareCall(query);
			cs.setInt(1, tradeID);
			cs.setInt(2, cardID);
			cs.setString(3, username);
			cs.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			if(e.getMessage().contains("Invalid TradeInfoID")) {
				JOptionPane.showMessageDialog(null, "Trade aborted by the other user.");
				return false;
			}
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	public boolean decrementCardOffered(int tradeID, int cardID, String username) {
		Connection con = dbService.getConnection();
		CallableStatement cs = null;
		String query = "{Call DecrementCardOffered(?,?,?)}";
		
		try {
			cs = con.prepareCall(query);
			cs.setInt(1, tradeID);
			cs.setInt(2, cardID);
			cs.setString(3, username);
			cs.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			if(e.getMessage().contains("Invalid TradeInfoID")) {
				JOptionPane.showMessageDialog(null, "Trade aborted by the other user.");
				return false;
			}
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	public boolean removeCardOffered(int tradeID, int cardID, String username) {
		Connection con = dbService.getConnection();
		CallableStatement cs = null;
		String query = "{Call RemoveCardOffered(?,?,?)}";
		
		try {
			cs = con.prepareCall(query);
			cs.setInt(1, tradeID);
			cs.setInt(2, cardID);
			cs.setString(3, username);
			cs.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			if(e.getMessage().contains("Invalid TradeInfoID")) {
				JOptionPane.showMessageDialog(null, "Trade aborted by the other user.");
				return false;
			}
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	public String getCardImage(int cardID) {
		String imageURL = null;
		Connection con = dbService.getConnection();
		CallableStatement cs = null;
		String query = "{Call GetCardImage(?)}";
		
		try {
			cs = con.prepareCall(query);
			cs.setInt(1, cardID);
			ResultSet rs = cs.executeQuery();
			if (rs.next()) {
				imageURL = rs.getString("ImageURL");
			}
		} catch (SQLException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return imageURL;
	}
	
	public boolean confirmTrade(int tradeID, String username) {
		Connection con = dbService.getConnection();
		CallableStatement cs = null;
		String query = "{Call ConfirmTrade(?,?)}";
		
		try {
			cs = con.prepareCall(query);
			cs.setInt(1, tradeID);
			cs.setString(2, username);
			cs.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			if(e.getMessage().contains("Invalid TradeInfoID")) {
				JOptionPane.showMessageDialog(null, "Trade aborted by the other user.");
				return false;
			}
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
	public boolean abortTrade(int tradeID) {
		Connection con = dbService.getConnection();
		CallableStatement cs = null;
		String query = "{Call DeleteTradeRequest(?)}";
		
		try {
			cs = con.prepareCall(query);
			cs.setInt(1, tradeID);
			cs.execute();
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
			if(e.getMessage().contains("Invalid TradeInfoID")) {
				JOptionPane.showMessageDialog(null, "Trade aborted by the other user.");
				return false;
			}
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
	}
	
}
