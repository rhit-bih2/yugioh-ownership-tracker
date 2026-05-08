package yot.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import yot.services.CollectionService;
import yot.services.DatabaseConnectionService;



public class CollectionsPage extends JPanel {
	private final CollectionService collectionService;
	private final BiConsumer<Integer, String> onOpenDetail;
	private final String username;
	private final JPanel list;

    public CollectionsPage(BiConsumer<Integer, String> onOpenDetail, DatabaseConnectionService dbService, String username) {
    	this.onOpenDetail = onOpenDetail;
    	this.username = username;
        this.collectionService = new CollectionService(dbService);

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
        
        
        JButton createCollectionButton = UiFactory.primaryButton("Create");
        createRow.add(createCollectionButton);
        createCollectionButton.addActionListener(e -> {
        	String collectionName = name.getText().trim();
        	if (collectionName.isEmpty()) {
        		JOptionPane.showMessageDialog(this, "Collection name is required.");
        		return;
        	}
        	if (collectionService.createCollection(username, collectionName)) {
        		name.setText("");
        		reloadCollections();
        	}
        });

        create.add(createRow);
        page.add(Box.createVerticalStrut(14));
        page.add(create);

        list = UiFactory.panelCard();
        list.setLayout(new BoxLayout(list, BoxLayout.Y_AXIS));
        list.setBorder(new EmptyBorder(16, 16, 16, 16));
        page.add(Box.createVerticalStrut(14));
        page.add(list);
        reloadCollections();

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setOpaque(false);
        add(UiFactory.scrollWrap(page));
    }
    
    private void reloadCollections() {
    	list.removeAll();
    	list.add(UiFactory.sectionTitle("Existing Collections"));
    	list.add(Box.createVerticalStrut(10));
    	list.add(collectionItem("All Cards", "Default collection with all owned cards", true, -1));
    	list.add(Box.createVerticalStrut(8));

    	ArrayList<Integer> collectionIDs = collectionService.getCollectionIDs(username);
    	for (int id : collectionIDs) {
    		list.add(collectionItem(collectionService.getCollectionName(id), "To be implemented...", false, id));
    		list.add(Box.createVerticalStrut(8));
    	}

    	list.revalidate();
    	list.repaint();
    }

    private JPanel collectionItem(String name, String meta, boolean isDefault, int collectionID) {
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
        nameBtn.addActionListener(e -> onOpenDetail.accept(collectionID, name));
        JLabel metaLabel = new JLabel(meta);
        metaLabel.setForeground(Theme.MUTED);
        left.add(nameBtn);
        left.add(Box.createVerticalStrut(4));
        left.add(metaLabel);

        item.add(left);
        item.add(Box.createHorizontalGlue());
        if (!isDefault) {
	        JButton editBtn = UiFactory.outlineButton("Edit");
	        editBtn.addActionListener(e -> {
	        	String updatedName = JOptionPane.showInputDialog(this, "Enter new collection name:");
	        	if (updatedName == null || updatedName.trim().isEmpty()) {
	        		return;
	        	}
	        	if (collectionService.updateCollectionName(collectionID, updatedName.trim())) {
	        		reloadCollections();
	        	}
	        });
	        item.add(editBtn);
        }
        if (!isDefault) {
            item.add(Box.createHorizontalStrut(6));
            JButton deleteBtn = UiFactory.dangerButton("Delete");
            deleteBtn.addActionListener(e -> {
            	int choice = JOptionPane.showOptionDialog(
            		this,
            		"Are you sure you want to delete \"" + name + "\"?",
            		"Delete Collection",
            		JOptionPane.YES_NO_OPTION,
            		JOptionPane.WARNING_MESSAGE,
            		null,
            		new String[]{"Yes", "No"},
            		"No"
            	);
            	if (choice == JOptionPane.YES_OPTION && collectionService.deleteCollection(collectionID)) {
            		reloadCollections();
            	}
            });
            item.add(deleteBtn);
        }
        return item;
    }
    
}
