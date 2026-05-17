package yot.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MarketplaceService {

    private final DatabaseConnectionService dbService;

    public MarketplaceService(DatabaseConnectionService dbService) {
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

    public List<String[]> getListingDetail(int cardId) {
        List<String[]> results = new ArrayList<>();
        Connection conn = dbService.getConnection();
        String query = "{CALL GetListingDetail(?)}";
        CallableStatement cs = null;
 
        try { 
        	cs = conn.prepareCall(query);
            cs.setInt(1, cardId);
            ResultSet rs = cs.executeQuery();
 
            while (rs.next()) {
                String[] row = new String[10];
                row[0] = nullDataHandle(rs, "SellerUsername");
                row[1] = nullDataHandle(rs, "SellerID");
                row[2] = nullDataHandle(rs, "StoreName");
                row[3] = nullDataHandle(rs, "Phone");
                row[4] = nullDataHandle(rs, "CardID");
                row[5] = nullDataHandle(rs, "CardName");
                row[6] = nullDataHandle(rs, "CardCode");
                row[7] = nullDataHandle(rs, "Rarity");
                row[8] = nullDataHandle(rs, "ListingPrice");
                row[9] = nullDataHandle(rs, "MarketPrice");
                results.add(row);
            }
            rs.close();
 
        } catch (SQLException e) {
            System.out.println("Error calling GetListingDetail.");
            e.printStackTrace();
        }
 
        return results;
    }

    private void setNullable(CallableStatement cs, int index, String value, int sqlType)
            throws SQLException {
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
            return val;
        } catch (SQLException e) {
            return "—";
        }
    }
}