package yot.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import yot.services.DatabaseConnectionService;
import yot.services.RetrieveCardService;

public class CardLibraryPage extends JPanel {

    private final JPanel resultList;

    public CardLibraryPage(Consumer<String[]> onOpenDetail, DatabaseConnectionService dbService) {
    	RetrieveCardService cardService = new RetrieveCardService(dbService);
    	JPanel page = UiFactory.pageContainer();

        // ── Page header ───────────────────────────────────────────────────────
        page.add(UiFactory.pageHeader("Card Library",
                "Search for any Yu-Gi-Oh card by name."));
        page.add(Box.createVerticalStrut(14));

        // ── Search bar panel ──────────────────────────────────────────────────
        JPanel searchCard = UiFactory.panelCard();
        searchCard.setLayout(new BoxLayout(searchCard, BoxLayout.Y_AXIS));
        searchCard.setBorder(new EmptyBorder(16, 16, 16, 16));
        searchCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        searchCard.add(UiFactory.sectionTitle("Search Cards"));
        searchCard.add(Box.createVerticalStrut(10));

        JPanel searchRow = UiFactory.rowPanel();
        searchRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        searchRow.setAlignmentX(LEFT_ALIGNMENT);

        JTextField nameInput = UiFactory.input("Enter card name…");
        searchRow.add(nameInput);
        searchRow.add(Box.createHorizontalStrut(8));

        JButton searchBtn = UiFactory.primaryButton("Search");
        searchRow.add(searchBtn);
        searchCard.add(searchRow);
        page.add(searchCard);
        page.add(Box.createVerticalStrut(14));

        // ── Results panel ─────────────────────────────────────────────────────
        JPanel resultsCard = UiFactory.panelCard();
        resultsCard.setLayout(new BoxLayout(resultsCard, BoxLayout.Y_AXIS));
        resultsCard.setBorder(new EmptyBorder(16, 16, 16, 16));
        resultsCard.add(UiFactory.sectionTitle("Results"));
        resultsCard.add(Box.createVerticalStrut(10));

        resultList = new JPanel();
        resultList.setOpaque(false);
        resultList.setLayout(new BoxLayout(resultList, BoxLayout.Y_AXIS));
        resultList.setAlignmentX(LEFT_ALIGNMENT);

        JLabel placeholder = new JLabel("Search for a card above to see results.");
        placeholder.setForeground(Theme.MUTED);
        placeholder.setAlignmentX(LEFT_ALIGNMENT);
        placeholder.setFont(Theme.FONT_BOLD.deriveFont(16f));
        resultList.add(placeholder);

        resultsCard.add(resultList);
        page.add(resultsCard);
        page.add(Box.createVerticalGlue());

        // ── Search action — calls RetrieveCard SP via CardService ─────────────
        searchBtn.addActionListener(e -> {
            String query = nameInput.getText().trim();
            if (query.isEmpty()) return;

            List<String[]> cards = cardService.retrieveCardByName(query);

            resultList.removeAll();

            if (cards.isEmpty()) {
                JLabel none = new JLabel("No cards found matching \"" + query + "\".");
                none.setForeground(Theme.MUTED);
                none.setAlignmentX(LEFT_ALIGNMENT);
                none.setFont(Theme.FONT_BOLD.deriveFont(16f));
                resultList.add(none);
            } else {
                for (int i = 0; i < cards.size(); i++) {
                    resultList.add(cardResultTile(cards.get(i), onOpenDetail));
                    if (i < cards.size() - 1) {
                        resultList.add(Box.createVerticalStrut(8));
                    }
                }
            }

            resultList.revalidate();
            resultList.repaint();
            SwingUtilities.invokeLater(() -> {
                resultList.revalidate();
                resultList.repaint();
            });
        });

        // ── Outer layout ──────────────────────────────────────────────────────
        setLayout(new BorderLayout());
        setOpaque(false);
        add(UiFactory.scrollWrap(page), BorderLayout.CENTER);
    }

    /**
     * Builds a single card result row.
     *
     * cardData indices (from CardService.retrieveCardByName):
     *   [0] ID  [1] Name  [2] Code  [3] Rarity  [4] MarketPrice
     *   [5] Type  [6] ATK  [7] DEF  [8] Level  [9] Race
     *   [10] Attribute  [11] SetID  [12] Description
     */
    private JPanel cardResultTile(String[] cardData, Consumer<String[]> onOpenDetail) {
        String name        = cardData[1];
        String rarity      = cardData[3];
        String code        = cardData[2];
        String description = cardData[12];

        String shortDesc = description.length() > 80
                ? description.substring(0, 77) + "…"
                : description;

        JPanel tile = new JPanel();
        tile.setBackground(new Color(35, 45, 80));
        tile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                new EmptyBorder(12, 12, 12, 12)
        ));
        tile.setLayout(new BoxLayout(tile, BoxLayout.X_AXIS));
        tile.setAlignmentX(LEFT_ALIGNMENT);
        tile.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        // Card image placeholder
        JLabel image = new JLabel("Card Image", javax.swing.SwingConstants.CENTER);
        image.setOpaque(true);
        image.setBackground(new Color(32, 42, 79));
        image.setForeground(Theme.MUTED);
        image.setPreferredSize(new Dimension(70, 90));
        image.setMaximumSize(new Dimension(70, 90));
        image.setMinimumSize(new Dimension(70, 90));

        // Info block
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel nameLabel = new JLabel(name);
        nameLabel.setForeground(Theme.TEXT);
        nameLabel.setFont(Theme.FONT_BOLD);

        JLabel metaLabel = new JLabel(rarity + "  ·  " + code);
        metaLabel.setForeground(Theme.ACCENT);
        metaLabel.setFont(Theme.FONT.deriveFont(12f));

        JLabel descLabel = new JLabel(
                "<html><body style='width:380px'>" + shortDesc + "</body></html>");
        descLabel.setForeground(Theme.MUTED);
        descLabel.setFont(Theme.FONT);

        info.add(nameLabel);
        info.add(Box.createVerticalStrut(4));
        info.add(metaLabel);
        info.add(Box.createVerticalStrut(4));
        info.add(descLabel);

        // Card Detail button
        JButton detailBtn = UiFactory.outlineButton("Card Detail");
        detailBtn.addActionListener(ev -> onOpenDetail.accept(cardData));

        tile.add(image);
        tile.add(Box.createHorizontalStrut(12));
        tile.add(info);
        tile.add(Box.createHorizontalGlue());
        tile.add(detailBtn);
        return tile;
    }
}