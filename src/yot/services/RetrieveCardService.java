package yot.services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RetrieveCardService {

    private final DatabaseConnectionService dbService;

    public RetrieveCardService(DatabaseConnectionService dbService) {
        this.dbService = dbService;
    }

    /**
     * Calls the [dbo].[RetrieveCard] stored procedure with only @Name set.
     * All other parameters are left NULL so the SP returns all matching cards.
     *
     * @param name  The card name to search for (must match exactly — adjust
     *              the SP or use LIKE in a wrapper if you want partial matches).
     * @return List of String[] where each array is:
     *         [0] ID
     *         [1] Name
     *         [2] Code
     *         [3] Rarity
     *         [4] MarketPrice
     *         [5] Type
     *         [6] ATK
     *         [7] DEF
     *         [8] Level
     *         [9] Race
     *         [10] Attribute
     *         [11] SetID
     *         [12] Description  (if your Card table has it — remove if not)
     */
    public List<String[]> retrieveCardByName(String name) {
        List<String[]> results = new ArrayList<>();
        
        Connection conn = dbService.getConnection();  // get the already-open connection
        
        if (conn == null) {
            System.out.println("No active database connection.");
            return results;
        }
        
        CallableStatement cs = null;
        String query = "{CALL [dbo].[RetrieveCard](?, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL)}";

        try {
        	cs = conn.prepareCall(query);
            if (name == null || name.isEmpty()) {
                cs.setNull(1, java.sql.Types.NVARCHAR);
            } else {
                cs.setString(1, name);
            }

            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                String[] card = new String[13];
                card[0]  = nullDataHandle(rs, "ID");
                card[1]  = nullDataHandle(rs, "Name");
                card[2]  = nullDataHandle(rs, "Code");
                card[3]  = nullDataHandle(rs, "Rarity");
                card[4]  = nullDataHandle(rs, "MarketPrice");
                card[5]  = nullDataHandle(rs, "Type");
                card[6]  = nullDataHandle(rs, "ATK");
                card[7]  = nullDataHandle(rs, "DEF");
                card[8]  = nullDataHandle(rs, "Level");
                card[9]  = nullDataHandle(rs, "Race");
                card[10] = nullDataHandle(rs, "Attribute");
                card[11] = nullDataHandle(rs, "SetID");
                card[12] = nullDataHandle(rs, "Description");
                results.add(card);
            }
            rs.close();

        } catch (SQLException e) {
            System.out.println("Error calling RetrieveCard stored procedure.");
            e.printStackTrace();
        }

        return results;
    }

    /**
     * Safely gets a column value as String, returning "—" if null or column missing.
     */
    private String nullDataHandle(ResultSet rs, String column) {
        try {
            String val = rs.getString(column);
            return val != null ? val : "—";
        } catch (SQLException e) {
            return "—";
        }
    }
}