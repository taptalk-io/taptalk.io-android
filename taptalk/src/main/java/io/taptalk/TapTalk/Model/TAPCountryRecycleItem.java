package io.taptalk.TapTalk.Model;

public class TAPCountryRecycleItem {
    public enum RecyclerItemType {
        COUNTRY_INITIAL, COUNTRY_ITEM, COUNTRY_ITEM_BOTTOM
    }

    private RecyclerItemType recyclerItemType;
    private TAPCountryListItem countryListItem;
    private char countryInitial;

    public RecyclerItemType getRecyclerItemType() {
        return recyclerItemType;
    }

    public void setRecyclerItemType(RecyclerItemType recyclerItemType) {
        this.recyclerItemType = recyclerItemType;
    }

    public TAPCountryListItem getCountryListItem() {
        return countryListItem;
    }

    public void setCountryListItem(TAPCountryListItem countryListItem) {
        this.countryListItem = countryListItem;
    }

    public char getCountryInitial() {
        return countryInitial;
    }

    public void setCountryInitial(char countryInitial) {
        this.countryInitial = countryInitial;
    }
}
