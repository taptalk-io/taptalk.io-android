package io.taptalk.TapTalk.Model;

import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;

import io.taptalk.TapTalk.Helper.TAPUtils;

public class TAPCountryRecycleItem {
    public enum RecyclerItemType {
        COUNTRY_INITIAL, COUNTRY_ITEM
    }

    private RecyclerItemType recyclerItemType;
    private TAPCountryListItem countryListItem;
    private char countryInitial;
    private boolean isSelected;

    public static TAPCountryRecycleItem fromHashMap(HashMap<String, Object> hashMap) {
        try {
            return TAPUtils.convertObject(hashMap, new TypeReference<TAPCountryRecycleItem>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, Object> toHashMap() {
        return TAPUtils.toHashMap(this);
    }

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

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
