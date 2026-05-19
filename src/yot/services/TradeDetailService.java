package yot.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
	
	public Map<String, String> getTradeConfirmInfo(int tradeID){
		Map<String, String> map = new HashMap<String,String>();
		Connection con = dbService.getConnection();
		CallableStatement cs = null;
		String query = "{Call GetTradeInfo(?)}";
		
		try {
			cs = con.prepareCall(query);
			cs.setInt(1, tradeID);
			ResultSet rs = cs.executeQuery();
			if (rs.next()) {
				map.put("SenderConfirmed", rs.getString("SenderConfirmed"));
				map.put("ReceiverConfirmed", rs.getString("ReceiverConfirmed"));
				map.put("IsComplete", rs.getString("IsComplete"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public Map<String, String> getAllTradeInfo(int tradeID){ //Not yet done
		Map<String, String> map = new HashMap<String,String>();
		Connection con = dbService.getConnection();
		CallableStatement cs = null;
		String query = "{Call GetTradeInfo(?)}";
		
		try {
			cs = con.prepareCall(query);
			cs.setInt(1, tradeID);
			ResultSet rs = cs.executeQuery();
			if (rs.next()) {
				map.put("SenderConfirmed", rs.getString("SenderConfirmed"));
				map.put("ReceiverConfirmed", rs.getString("ReceiverConfirmed"));
				map.put("IsComplete", rs.getString("IsComplete"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return map;
	}
	
	public ArrayList<Card> getCardsOffered(int tradeID, String username){
		
		return null;
	}
	
	public boolean offerCard(int tradeID, int cardID, String username) {
		
		return false;
	}
	
	public boolean incrementCardOffered(int tradeID, int cardID, String username) {

		return false;
	}
	
	public boolean decrementCardOffered(int tradeID, int cardID, String username) {

		return false;
	}
	
	public boolean removeCardOffered(int tradeID, int cardID, String username) {
		
		return false;
	}
	
	public String getCardImage(int cardID) {
		
		return null;
	}
	
	public boolean confirmTrade(int tradeID, String username) {
		
		return false;
	}
	
	public boolean abortTrade(int tradeID) {
		
		return false;
	}
	
	
}
