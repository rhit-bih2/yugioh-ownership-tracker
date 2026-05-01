package yot.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class CollectionDetailPage extends JPanel {
    private final JLabel titleLabel;

    public CollectionDetailPage(Runnable onBack) {
        JPanel page = UiFactory.pageContainer();

        JPanel top = UiFactory.rowPanel();
        top.setAlignmentX(LEFT_ALIGNMENT);
        top.setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        titleLabel = new JLabel("Collection: All Cards");
        titleLabel.setFont(Theme.FONT_PAGE);
        titleLabel.setForeground(Theme.TEXT);
        JLabel sub = new JLabel("View every card in this collection by image and name.");
        sub.setForeground(Theme.MUTED);
        header.add(titleLabel);
        header.add(Box.createVerticalStrut(4));
        header.add(sub);
        JButton back = UiFactory.outlineButton("Back to Collections");
        back.addActionListener(e -> onBack.run());
        top.add(header);
        top.add(Box.createHorizontalGlue());
        top.add(back);
        page.add(top);

        JPanel addCard = UiFactory.panelCard();
        addCard.setLayout(new BoxLayout(addCard, BoxLayout.Y_AXIS));
        addCard.setBorder(new EmptyBorder(16, 16, 16, 16));
        addCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        addCard.add(UiFactory.sectionTitle("Add Card to Collection"));
        addCard.add(Box.createVerticalStrut(10));
        JPanel addRow = UiFactory.rowPanel();
        addRow.add(UiFactory.input("Card name"));
        addRow.add(Box.createHorizontalStrut(8));
        addRow.add(UiFactory.input("Image URL"));
        addRow.add(Box.createHorizontalStrut(8));
        addRow.add(UiFactory.primaryButton("Add Card"));
        page.add(Box.createVerticalStrut(14));
        page.add(addCard);

        JPanel cards = UiFactory.panelCard();
        cards.setLayout(new BoxLayout(cards, BoxLayout.Y_AXIS));
        cards.setBorder(new EmptyBorder(16, 16, 16, 16));
        cards.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1600));
        cards.add(UiFactory.sectionTitle("Cards in Collection"));
        cards.add(Box.createVerticalStrut(10));
        cards.add(cardTile("Blue-Eyes White Dragon"));
        cards.add(Box.createVerticalStrut(8));
        cards.add(cardTile("Dark Magician"));
        cards.add(Box.createVerticalStrut(8));
        cards.add(cardTile("Charizard VMAX"));
        cards.add(Box.createVerticalStrut(8));
        cards.add(cardTile("Pikachu EX"));
        cards.add(Box.createVerticalStrut(8));
        cards.add(cardTile("Ancient Mew"));
        cards.add(Box.createVerticalStrut(8));
        cards.add(cardTile("Red-Eyes Black Dragon"));
        page.add(Box.createVerticalStrut(14));
        page.add(cards);

        setLayout(new BorderLayout());
        setOpaque(false);
        add(UiFactory.scrollWrap(page), BorderLayout.CENTER);
    }

    public void setCollectionName(String collectionName) {
        titleLabel.setText("Collection: " + collectionName);
    }

    private JPanel cardTile(String name) {
        JPanel tile = new JPanel();
        tile.setBackground(new Color(35, 45, 80));
        tile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                new EmptyBorder(12, 12, 12, 12)
        ));
        tile.setLayout(new BoxLayout(tile, BoxLayout.X_AXIS));

        JLabel image = new JLabel("Card Image", SwingConstants.CENTER);
        image.setOpaque(true);
        image.setBackground(new Color(32, 42, 79));
        image.setForeground(Theme.MUTED);
        image.setPreferredSize(new Dimension(120, 150));
        image.setMaximumSize(new Dimension(120, 150));

        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        JLabel cardName = new JLabel(name);
        cardName.setForeground(Theme.TEXT);
        cardName.setFont(Theme.FONT_BOLD);
        right.add(cardName);
        right.add(Box.createVerticalStrut(8));
        right.add(UiFactory.dangerButton("Remove"));

        tile.add(image);
        tile.add(Box.createHorizontalStrut(12));
        tile.add(right);
        return tile;
    }
}
