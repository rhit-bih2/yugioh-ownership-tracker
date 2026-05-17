package yot.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SalesDetailService {
	
	private final DatabaseConnectionService dbService;
	
	public SalesDetailService(DatabaseConnectionService dbService) {
		 this.dbService = dbService;
	}
	
	
	public String[] getCardSalesDetail(int cardId, String username) {
        Connection conn = dbService.getConnection();
        String query = "{CALL GetCardSalesDetail(?, ?)}";
        CallableStatement cs = null;
 
        try {
        	cs = conn.prepareCall(query);
            cs.setInt(1, cardId);
            cs.setString(2, username);
            ResultSet rs = cs.executeQuery();
 
            if (rs.next()) {
                String[] row = new String[15];
                row[0]  = nullDataHandle(rs, "SellerUsername");
                row[1]  = nullDataHandle(rs, "SellerID");
                row[2]  = nullDataHandle(rs, "StoreName");
                row[3]  = nullDataHandle(rs, "Address");
                row[4]  = nullDataHandle(rs, "City");
                row[5]  = nullDataHandle(rs, "State");
                row[6]  = nullDataHandle(rs, "ZipCode");
                row[7]  = nullDataHandle(rs, "SellerDescription");
                row[8]  = nullDataHandle(rs, "Phone");
                row[9]  = nullDataHandle(rs, "CardID");
                row[10] = nullDataHandle(rs, "CardName");
                row[11] = nullDataHandle(rs, "CardCode");
                row[12] = nullDataHandle(rs, "Rarity");
                row[13] = nullDataHandle(rs, "ListingPrice");
                row[14] = nullDataHandle(rs, "MarketPrice");
                rs.close();
                return row;
            }
            rs.close();
 
        } catch (SQLException e) {
            System.out.println("Error calling GetCardSalesDetail.");
            e.printStackTrace();
        }
 
        return null;
    }
	 
	private String nullDataHandle(ResultSet rs, String column) {
        try {
            String val = rs.getString(column);
            if (val == null) return "-";
            return val;
        } catch (SQLException e) {
            return "—";
        }
    }
}
