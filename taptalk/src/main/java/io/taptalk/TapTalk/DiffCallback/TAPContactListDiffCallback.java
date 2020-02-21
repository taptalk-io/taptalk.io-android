package io.taptalk.TapTalk.DiffCallback;

import android.support.v7.util.DiffUtil;

import java.util.List;

import io.taptalk.TapTalk.Helper.TAPUtils;
import io.taptalk.TapTalk.Model.TAPUserModel;

public class TAPContactListDiffCallback extends DiffUtil.Callback {

    private List<List<TAPUserModel>> oldContactList;
    private List<List<TAPUserModel>> newContactList;

    public TAPContactListDiffCallback(List<List<TAPUserModel>> oldContactList, List<List<TAPUserModel>> newContactList) {
        this.oldContactList = oldContactList;
        this.newContactList = newContactList;
    }

    @Override
    public int getOldListSize() {
        return oldContactList.size();
    }

    @Override
    public int getNewListSize() {
        return newContactList.size();
    }

    @Override
    public boolean areItemsTheSame(int i, int i1) {
        return oldContactList.get(i).equals(newContactList.get(i1));
    }

    @Override
    public boolean areContentsTheSame(int i, int i1) {
        return TAPUtils.listEqualsIgnoreOrder(oldContactList.get(i), newContactList.get(i1));
    }
}
