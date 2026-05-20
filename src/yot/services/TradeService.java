package yot.services;

import java.sql.CallableStatement;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class TradeService {

	public static class TradeRequest {
		private final String senderUsername;
		private final String receiverUsername;
		private final int tradeRequestID;
		private final Date dateCreated;
		private boolean isComplete;

		public TradeRequest(String senderUsername, String receiverUsername, int tradeRequestID, Date dateCreated, boolean isComplete) {
			this.senderUsername = senderUsername;
			this.receiverUsername = receiverUsername;
			this.tradeRequestID = tradeRequestID;
			this.dateCreated = dateCreated;
			this.isComplete = isComplete;
		}

		public String getSenderUsername() {
			return this.senderUsername;
		}

		public String getReceiverUsername() {
			return this.receiverUsername;
		}
		
		public int getTradeRequestID() {
			return this.tradeRequestID;
		}
		
		public Date getDateCreated() {
			return this.dateCreated;
		}
		
		public boolean getIsComplete() {
			return this.isComplete;
		}
	}

	private DatabaseConnectionService dbService = null;
	
	public TradeService(DatabaseConnectionService dbService) {
		this.dbService = dbService;
	}

	
	public List<TradeRequest> getTradeRequests(String username) {
		ArrayList<TradeRequest> out = new ArrayList<TradeRequest>();
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call GetTradeRequests(?)}");
			stmt.setString(1, username);
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String senderUsername = rs.getString("SenderUsername");
				String receiverUsername = rs.getString("ReceiverUsername");
				int tradeRequestID = rs.getInt("ID");
				Date dateCreated = rs.getDate("DateCreated");
				boolean isComplete = rs.getBoolean("IsComplete");
				
				out.add(new TradeRequest(senderUsername, receiverUsername, tradeRequestID, dateCreated, isComplete));
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			e.printStackTrace();
		}
		return out;
	}
	
	public boolean createTR(String senderUsername, String receiverUsername) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call CreateTradeRequest(?, ?)}");
			stmt.setString(1, senderUsername);
			stmt.setString(2, receiverUsername);
			stmt.execute();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
		return true;
	}
	
	public boolean deleteTR(int tradeRequestID) {
		try {
			CallableStatement stmt = dbService.getConnection().prepareCall("{call DeleteTradeRequest(?)}");
			stmt.setInt(1, tradeRequestID);
			stmt.execute();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return false;
		}
		return true;
	}

}
