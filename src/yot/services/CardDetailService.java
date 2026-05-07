package yot.services;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CardDetailService {

    private final DatabaseConnectionService dbService;

    public CardDetailService(DatabaseConnectionService dbService) {
        this.dbService = dbService;
    }

    /**
     * Runs: SELECT * FROM Card WHERE ID = ?
     *
     * Returned array indices:
     *   [0]  ID
     *   [1]  Name
     *   [2]  Code
     *   [3]  Rarity
     *   [4]  Description
     *   [5]  MarketPrice
     *   [6]  Type
     *   [7]  ATK
     *   [8]  DEF
     *   [9]  Level
     *   [10] Race
     *   [11] Attribute
     *   [12] ImageURL
     *   [13] SetID
     */
    public String[] getCardById(String id) {
        Connection conn = dbService.getConnection();
        if (conn == null) {
            System.out.println("No active database connection.");
            return null;
        }
        
        String query = "SELECT * FROM Card WHERE ID = ?";

        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, Integer.parseInt(id));
            ResultSet rs = stmt.executeQuery();

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
        } catch (NumberFormatException e) {
            System.out.println("Invalid card ID format: " + id);
            e.printStackTrace();
        }

        return null;
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