package yot.ui;

import java.awt.CardLayout;
import java.awt.Component;
import java.util.function.Consumer;
import javax.swing.JPanel;

public class AppNavigator {
    public static final String PAGE_COLLECTIONS = "collections";
    public static final String PAGE_DETAIL = "detail";
    public static final String PAGE_TRADE = "trade";
    public static final String PAGE_LIBRARY = "library";

    private final CardLayout layout;
    private final JPanel container;
    private final CollectionDetailPage detailPage;
    private final Consumer<String> onPageChanged;

    public AppNavigator(CardLayout layout, JPanel container, Consumer<String> onPageChanged) {
        this.layout = layout;
        this.container = container;
        this.onPageChanged = onPageChanged;

        detailPage = new CollectionDetailPage(() -> show(PAGE_COLLECTIONS));
        container.add(new CollectionsPage(this::openCollectionDetail), PAGE_COLLECTIONS);
        container.add(detailPage, PAGE_DETAIL);
        container.add(new PlaceholderPage("Trade", "Trade workflow content will be added later."), PAGE_TRADE);
        container.add(new PlaceholderPage("Card Library", "Card library content will be implemented later."), PAGE_LIBRARY);
    }

    public void show(String pageId) {
        layout.show(container, pageId);
        onPageChanged.accept(pageId);
    }

    public void openCollectionDetail(String collectionName) {
        detailPage.setCollectionName(collectionName);
        show(PAGE_DETAIL);
    }

    public Component getView() {
        return container;
    }
}
