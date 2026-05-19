package yot.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class CardDetailService{
	private DatabaseConnectionService dbService;
	public CardDetailService (DatabaseConnectionService dbService) {
		this.dbService = dbService;
	}
	
    public List<Integer> retrieveCard(Map<String, String> map) {
        List<Integer> list = new ArrayList<>();
        Connection conn = this.dbService.getConnection();
        String query = "{CALL RetrieveCard(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        CallableStatement cs = null;
        
        try {
        	cs = conn.prepareCall(query);
            setNullable(cs, 1,  map.get("Name"),        java.sql.Types.NVARCHAR);
            setNullable(cs, 2,  map.get("Code"),        java.sql.Types.VARCHAR);
            setNullable(cs, 3,  map.get("Rarity"),      java.sql.Types.VARCHAR);
            setNullable(cs, 4,  map.get("MarketPrice"), java.sql.Types.FLOAT);
            setNullable(cs, 5,  map.get("Type"),        java.sql.Types.VARCHAR);
            setNullable(cs, 6,  map.get("ATK"),         java.sql.Types.INTEGER);
            setNullable(cs, 7,  map.get("DEF"),         java.sql.Types.INTEGER);
            setNullable(cs, 8,  map.get("Level"),       java.sql.Types.INTEGER);
            setNullable(cs, 9,  map.get("Race"),        java.sql.Types.VARCHAR);
            setNullable(cs, 10, map.get("Attribute"),   java.sql.Types.VARCHAR);
            setNullable(cs, 11, map.get("SetID"),       java.sql.Types.INTEGER);

            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt("ID"));
            }
            rs.close();

        } catch (SQLException e) {
            System.out.println("Error calling RetrieveCard stored procedure.");
            e.printStackTrace();
        }
        return list;
    }

    
    /**     
     * Array indices:
     *   [0] ID  [1] Name  [2] Code  [3] Rarity  [4] Description
     *   [5] MarketPrice  [6] Type  [7] ATK  [8] DEF  [9] Level
     *   [10] Race  [11] Attribute  [12] ImageURL  [13] SetID
     */
    public String[] getCardById(Integer id) {
        Connection conn = dbService.getConnection();
        String query = "{CALL GetCardInfo(?)}";
        CallableStatement cs = null;
        try {
        	cs = conn.prepareCall(query);
            cs.setInt(1, id);
            ResultSet rs = cs.executeQuery();

            if (rs.next()) {
                String[] card = new String[14];
                card[0]  = nullDataHandle(rs, "ID");
                card[1]  = nullDataHandle(rs, "Name");
                card[2]  = nullDataHandle(rs, "Code");
                card[3]  = nullDataHandle(rs, "Rarity");
                card[4]  = nullDataHandle(rs, "Description");
                card[5]  = nullDataHandle(rs, "MarketPrice");
                card[6]  = nullDataHandle(rs, "Type");
                card[7]  = nullDataHandle(rs, "ATK");
                card[8]  = nullDataHandle(rs, "DEF");
                card[9]  = nullDataHandle(rs, "Level");
                card[10] = nullDataHandle(rs, "Race");
                card[11] = nullDataHandle(rs, "Attribute");
                card[12] = nullDataHandle(rs, "ImageURL");
                card[13] = nullDataHandle(rs, "SetID");
                rs.close();
                return card;
            }
            rs.close();

        } catch (SQLException e) {
            System.out.println("Error fetching card by ID: " + id);
            e.printStackTrace();
        }
        return null;
    }

    private void setNullable(CallableStatement cs, int index, String value, int sqlType) throws SQLException{
        if (value == null || value.isEmpty()) {
            cs.setNull(index, sqlType);
        } else {
            cs.setString(index, value);
        }
    }

    private String nullDataHandle(ResultSet rs, String column) {
        try {
            String val = rs.getString(column);
            if (val == null) return "-";
            if (column == "ATK" || column == "DEF") {
            	if(val != null && val.equals("-1")) {
            		return "?";
            	}
            }
            return val;
        } catch (SQLException e) {
            return "—";
        }
    }
    
    public boolean isCardOwned(String cardId, String username) {
        Connection conn = dbService.getConnection();
        String query = "{Call CardOwnershipCheck(?,?)}";
        CallableStatement cs = null;
        try {
        	cs = conn.prepareCall(query);
            cs.setInt(1, Integer.parseInt(cardId));
            cs.setString(2, username);
            ResultSet rs = cs.executeQuery();
            if (rs.next()) {
                return rs.getInt("Result") == 1;
            }
        } catch (SQLException e) {
            System.out.println("Error checking card ownership.");
            e.printStackTrace();
        }
        return false;
    }
    
    public void AddCardToOwned(int CardID, String username) {
    	Connection conn = dbService.getConnection();
    	String query = "{Call [dbo].[AddCardToOwned] (?, ?)}";
    	CallableStatement cs = null;
    	try {
    	    cs = conn.prepareCall(query);
    	    cs.setInt(1, CardID);
    	    cs.setString(2, username);
    	    cs.execute();
    	} catch (SQLException e) {
    		e.printStackTrace();
    	}
    	
    }
}
