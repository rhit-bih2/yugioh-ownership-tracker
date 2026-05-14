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

    public List<Integer> searchListings(Map<String, String> map) {
        List<Integer> list = new ArrayList<>();
        Connection conn = dbService.getConnection();
        CallableStatement cs = null;
        String query = "{CALL GetListingCards(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";

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
                list.add(rs.getInt("CardID"));
            }
            rs.close();

        } catch (SQLException e) {
            System.out.println("Error calling GetListingCards.");
            e.printStackTrace();
        }

        return list;
    }

    /**
     * Calls [dbo].[GetSellerDetail] with CardID
     * Returns a list of String[] — one row per seller listing this card.
     *
     * Indices:
     *   [0]  SellerUsername
     *   [1]  SellerID
     *   [2]  StoreName
     *   [3]  Address
     *   [4]  City
     *   [5]  State
     *   [6]  ZipCode
     *   [7]  SellerDescription
     *   [8]  Phone
     *   [9]  CardID
     *   [10] CardName
     *   [11] CardDescription
     *   [12] Price
     */
    public List<String[]> getSellerDetail(int cardId) {
        List<String[]> results = new ArrayList<>();
        Connection conn = dbService.getConnection();
        CallableStatement cs = null;
        String query = "{CALL GetSellerDetail(?)}";

        try {
        	cs = conn.prepareCall(query);
            cs.setInt(1, cardId);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                String[] row = new String[13];
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
                row[11] = nullDataHandle(rs, "CardDescription");
                row[12] = nullDataHandle(rs, "Price");
                results.add(row);
            }
            rs.close();

        } catch (SQLException e) {
            System.out.println("Error calling GetSellerDetail.");
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
            return val != null ? val : "—";
        } catch (SQLException e) {
            return "—";
        }
    }
}