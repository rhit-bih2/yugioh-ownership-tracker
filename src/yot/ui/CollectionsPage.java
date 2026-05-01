package yot.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.util.function.Consumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class CollectionsPage extends JPanel {
    public CollectionsPage(Consumer<String> onOpenDetail) {
        JPanel page = UiFactory.pageContainer();

        page.add(UiFactory.pageHeader("Your Collections", "Create, edit, or delete your own collections."));

        JPanel create = UiFactory.panelCard();
        create.setLayout(new BoxLayout(create, BoxLayout.Y_AXIS));
        create.setBorder(new EmptyBorder(16, 16, 16, 16));
        create.add(UiFactory.sectionTitle("Create Collection"));
        create.add(Box.createVerticalStrut(10));
        JPanel createRow = UiFactory.rowPanel();
        createRow.setMaximumSize(new Dimension(Integer.MAX_VALUE,40));
        createRow.setAlignmentX(LEFT_ALIGNMENT);
        JTextField name = UiFactory.input("Collection name");
        createRow.add(name);
        createRow.add(Box.createHorizontalStrut(8));
        createRow.add(UiFactory.primaryButton("Create"));
        create.add(createRow);
        page.add(Box.createVerticalStrut(14));
        page.add(create);

        JPanel list = UiFactory.panelCard();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBorder(new EmptyBorder(16, 16, 16, 16));
        list.add(UiFactory.sectionTitle("Existing Collections"));
        list.add(Box.createVerticalStrut(10));
        list.add(collectionItem("All Cards", "Default collection with all owned cards", true, onOpenDetail));
        list.add(Box.createVerticalStrut(8));
        list.add(collectionItem("Water Deck", "12 cards", false, onOpenDetail));
        list.add(Box.createVerticalStrut(8));
        list.add(collectionItem("Dragon Showcase", "7 cards", false, onOpenDetail));
        list.add(Box.createVerticalStrut(8));
        list.add(collectionItem("Dragon Showcase", "7 cards", false, onOpenDetail));
        list.add(Box.createVerticalStrut(8));
        list.add(collectionItem("Dragon Showcase", "7 cards", false, onOpenDetail));
        list.add(Box.createVerticalStrut(8));
        list.add(collectionItem("Dragon Showcase", "7 cards", false, onOpenDetail));
        list.add(Box.createVerticalStrut(8));
        list.add(collectionItem("Dragon Showcase", "7 cards", false, onOpenDetail));
        list.add(Box.createVerticalStrut(8));
        list.add(collectionItem("Dragon Showcase", "7 cards", false, onOpenDetail));
        list.add(Box.createVerticalStrut(8));
        list.add(collectionItem("Dragon Showcase", "7 cards", false, onOpenDetail));
        list.add(Box.createVerticalStrut(8));
        list.add(collectionItem("Dragon Showcase", "7 cards", false, onOpenDetail));
        list.add(Box.createVerticalStrut(8));
        list.add(collectionItem("Dragon Showcase", "7 cards", false, onOpenDetail));
        list.add(Box.createVerticalStrut(8));
        list.add(collectionItem("Dragon Showcase", "7 cards", false, onOpenDetail));
        list.add(Box.createVerticalStrut(8));
        list.add(collectionItem("Dragon Showcase", "7 cards", false, onOpenDetail));
        list.add(Box.createVerticalStrut(8));
        list.add(collectionItem("Dragon Showcase", "7 cards", false, onOpenDetail));
        list.add(Box.createVerticalStrut(8));
        list.add(collectionItem("Dragon Showcase", "7 cards", false, onOpenDetail));
        list.add(Box.createVerticalStrut(8));
        page.add(Box.createVerticalStrut(14));
        page.add(list);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        add(UiFactory.scrollWrap(page));
    }

    private JPanel collectionItem(String name, String meta, boolean isDefault, Consumer<String> onOpenDetail) {
        JPanel item = new JPanel();
        item.setBackground(new Color(35, 45, 80));
        item.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                new EmptyBorder(12, 12, 12, 12)
        ));
        item.setLayout(new BoxLayout(item, BoxLayout.X_AXIS));
        item.setAlignmentX(LEFT_ALIGNMENT);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setAlignmentX(LEFT_ALIGNMENT);
        JButton nameBtn = UiFactory.linkButton(name);
        nameBtn.addActionListener(e -> onOpenDetail.accept(name));
        JLabel metaLabel = new JLabel(meta);
        metaLabel.setForeground(Theme.MUTED);
        left.add(nameBtn);
        left.add(Box.createVerticalStrut(4));
        left.add(metaLabel);

        item.add(left);
        item.add(Box.createHorizontalGlue());
        item.add(UiFactory.outlineButton("Edit"));
        if (!isDefault) {
            item.add(Box.createHorizontalStrut(6));
            item.add(UiFactory.dangerButton("Delete"));
        }
        return item;
    }
}
