package yot.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import yot.services.CardDetailService;
import yot.services.DatabaseConnectionService;

public class CardLibraryPage extends JPanel {

    private final Consumer<Integer> onOpenDetail;
    private CardDetailService cardDetailService;

    public CardLibraryPage(Consumer<Integer> onOpenDetail, DatabaseConnectionService dbService) {
        this.onOpenDetail = onOpenDetail;
        this.cardDetailService = new CardDetailService(dbService);

        JPanel page = UiFactory.pageContainer();

        // ── Page header ───────────────────────────────────────────────────────
        page.add(UiFactory.pageHeader("Card Library",
                "Search for any Yu-Gi-Oh card by name."));
        page.add(Box.createVerticalStrut(14));

        // ── CardSearchPanel handles all search + results + pagination ─────────
        JPanel searchBox = new CardSearchPanel(
                this::retrieveCard,        // search fn: Map -> List<Integer>
                this::navigateDetailPage,  // click fn:  Integer -> void (Move to Detail Page)
                dbService
        );
        page.add(searchBox);
        page.add(Box.createVerticalGlue());

        setLayout(new BorderLayout());
        setOpaque(false);
        page.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        page.setAlignmentX(LEFT_ALIGNMENT);
        add(page, BorderLayout.CENTER);
    }

    /**
     * Called by CardSearchPanel when the user clicks a card image.
     * Fetches full card data then fires onOpenDetail to navigate to CardDetailPage.
     */
    private void navigateDetailPage(Integer cardId) {
        if (cardId != null) {
            onOpenDetail.accept(cardId);
        }
    }

    /**
     * Passed to CardSearchPanel as the search function.
     * Calls dbo.RetrieveCard with the filter map and returns matching IDs.
     */
    private List<Integer> retrieveCard(Map<String, String> map) {
        List<Integer> list = cardDetailService.retrieveCard(map);
        return list;
    }
    
}