package yot.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

public class CardSearchService {
	private DatabaseConnectionService dbService = null;
	
	public CardSearchService(DatabaseConnectionService dbService) {
		this.dbService = dbService;
	}
	
	public List<String> getFilterOptions(String filterType) {
		List<String> options = new ArrayList<>();
		try {
			Connection connection = this.dbService.getConnection();
			CallableStatement stmt = connection.prepareCall("{CALL ListCardValues(?)}");
			stmt.setString(1, filterType);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
				options.add(rs.getString(filterType));
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return options;
	}
	
	public String getCardImageUrl(int cardId) {
		try {
			Connection connection = this.dbService.getConnection();
			CallableStatement stmt = connection.prepareCall("{CALL GetCardImage(?)}");
			stmt.setInt(1, cardId);
			ResultSet rs = stmt.executeQuery();
			if(rs.next()) {
				return rs.getString("ImageURL");
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
		return null;
	}
}
