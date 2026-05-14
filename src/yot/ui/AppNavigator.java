package yot.ui;

import java.awt.CardLayout;
import java.awt.Component;
import java.util.function.Consumer;
import javax.swing.JPanel;

import yot.services.DatabaseConnectionService;

public class AppNavigator {
    public static final String PAGE_COLLECTIONS = "collections";
    public static final String PAGE_DETAIL = "detail";
    public static final String PAGE_TRADE = "trade";
    public static final String PAGE_LIBRARY = "library";
    public static final String PAGE_MY_LISTINGS = "myListings";
    public static final String PAGE_CARD_DETAIL = "cardDetail";
    public static final String PAGE_SALES_DETAIL = "salesDetail";  
    public static final String PAGE_MARKETPLACE = "marketplace";


    private final CardLayout layout;
    private final JPanel container;
    private final CollectionDetailPage detailPage;
    private final CardDetailPage cardDetailPage;
    private final Consumer<String> onPageChanged;
    private final SalesDetailPage salesDetailPage;


    public AppNavigator(CardLayout layout, JPanel container, Consumer<String> onPageChanged, DatabaseConnectionService dbService, String username, boolean isSeller) {
        this.layout = layout;
        this.container = container;
        this.onPageChanged = onPageChanged;

        detailPage = new CollectionDetailPage(() -> show(PAGE_COLLECTIONS), this::openCardDetailFromCollection, dbService, username);
        container.add(new CollectionsPage(this::openCollectionDetail, dbService, username), PAGE_COLLECTIONS);
        container.add(detailPage, PAGE_DETAIL);
        container.add(new PlaceholderPage("Trade", "Trade workflow content will be added later."), PAGE_TRADE);
        
        cardDetailPage = new CardDetailPage(() -> show(PAGE_LIBRARY), dbService, username);
        salesDetailPage = new SalesDetailPage(() -> show(PAGE_MARKETPLACE), dbService, this::openCardDetailFromMarketplace);
        container.add(new CardLibraryPage(this::openCardDetail, dbService), PAGE_LIBRARY);
        container.add(cardDetailPage, PAGE_CARD_DETAIL);
        container.add(new MarketplacePage(dbService, this::openCardDetail, this::openSalesDetail), PAGE_MARKETPLACE);
        container.add(salesDetailPage, PAGE_SALES_DETAIL);


        if (isSeller) {
            container.add(new MyListingsPage(dbService, username, this::openSalesDetailFromMyListings), PAGE_MY_LISTINGS);
        }
    }

    public void show(String pageId) {
        layout.show(container, pageId);
        onPageChanged.accept(pageId);
    }
    
    public void openCollectionDetail(Integer collectionID, String collectionName) {
        detailPage.showCollection(collectionID, collectionName);
        show(PAGE_DETAIL);
    }

    public void openCardDetail(Integer cardID) {
        cardDetailPage.resetBackToLibrary(() -> show(PAGE_LIBRARY));
        cardDetailPage.setCardData(cardID);
        show(PAGE_CARD_DETAIL);
    }

    public void openCardDetailFromMarketplace(Integer cardID) {
        cardDetailPage.setBackAction(() -> show(PAGE_SALES_DETAIL));
        cardDetailPage.setBackLabel("← Back to Sales Detail");
        cardDetailPage.setCardData(cardID);
        show(PAGE_CARD_DETAIL);
    }

    public void openCardDetailFromCollection(Integer cardID) {
        cardDetailPage.setBackAction(() -> show(PAGE_DETAIL));
        cardDetailPage.setBackLabel("← Back to Collection");
        cardDetailPage.setCardData(cardID);
        show(PAGE_CARD_DETAIL);
    }
    
    public void openSalesDetailFromMyListings(String username, Integer CardID) {
    	salesDetailPage.setBackAction(() -> show(PAGE_MY_LISTINGS));
    	salesDetailPage.setBackLabel("← Back to My Listing");
    	salesDetailPage.loadDetail(CardID, username);
        show(PAGE_SALES_DETAIL);
    }
    
    public void openSalesDetail(String username, Integer CardID) {
    	salesDetailPage.setBackAction(() -> show(PAGE_MARKETPLACE));
    	salesDetailPage.setBackLabel("← Back to Marketplace");
        salesDetailPage.loadDetail(CardID, username);
        show(PAGE_SALES_DETAIL);
    }


    public Component getView() {
        return container;
    }
}
