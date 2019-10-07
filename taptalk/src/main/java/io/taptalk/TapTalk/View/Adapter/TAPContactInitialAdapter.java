package io.taptalk.TapTalk.View.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.taptalk.TapTalk.DiffCallback.TAPContactListDiffCallback;
import io.taptalk.TapTalk.Listener.TapContactListListener;
import io.taptalk.TapTalk.Model.TAPUserModel;
import io.taptalk.Taptalk.R;

@Deprecated
public class TAPContactInitialAdapter extends RecyclerView.Adapter<TAPContactInitialAdapter.ContactListHolder> {

    private List<List<TAPUserModel>> contactList;
    private List<TAPUserModel> selectedContacts;
    private List<String> sectionTitles;
    private TapContactListListener listener;
    private int viewType;

    public TAPContactInitialAdapter(int viewType, List<List<TAPUserModel>> contactList) {
        this.viewType = viewType;
        this.contactList = contactList;
        sectionTitles = new ArrayList<>();
    }

    // Constructor for selectable contacts
    public TAPContactInitialAdapter(int viewType, List<List<TAPUserModel>> contactList, List<TAPUserModel> selectedContacts, TapContactListListener listener) {
        this.viewType = viewType;
        this.contactList = contactList;
        this.selectedContacts = selectedContacts;
        this.listener = listener;
        sectionTitles = new ArrayList<>();
    }

    public void updateAdapterData(List<List<TAPUserModel>> contactList) {
        DiffUtil.DiffResult diffResult = DiffUtil
                .calculateDiff(new TAPContactListDiffCallback(getItems(), contactList));
        setItems(contactList, false);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ContactListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactListHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.tap_cell_contact_list_initial, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactListHolder holder, int position) {
        holder.onBind(position);
    }

    @Override
    public int getItemCount() {
        if (null != contactList) return contactList.size();
        return 0;
    }

    public List<List<TAPUserModel>> getItems() {
        return contactList;
    }

    public List<TAPUserModel> getItemAt(int position) {
        return contactList.get(position);
    }

    public void setItems(List<List<TAPUserModel>> contactList) {
        this.contactList = contactList;
        notifyDataSetChanged();
    }

    public void setItems(List<List<TAPUserModel>> contactList, boolean isNotify) {
        this.contactList = contactList;
        if (isNotify)
            notifyDataSetChanged();
    }

    class ContactListHolder extends RecyclerView.ViewHolder {

        private RecyclerView rvContactInitial;
        private TextView tvInitial;
        private List<TAPUserModel> item;
        private TAPContactListAdapterOld adapter;

        ContactListHolder(View itemView) {
            super(itemView);

            tvInitial = itemView.findViewById(R.id.tv_initial);
            rvContactInitial = itemView.findViewById(R.id.rv_contact_list_initial);
        }

        void onBind(int position) {
            item = getItemAt(position);

            if (sectionTitles.isEmpty() || sectionTitles.size() <= position) {
                // Set initial as section title
                char initial = item.get(0).getName().charAt(0);
                if (!Character.isAlphabetic(initial)) {
                    initial = '#';
                }
                tvInitial.setText(String.valueOf(initial));
            } else {
                // Set custom section title
                tvInitial.setText(sectionTitles.get(position));
            }

            if (viewType == TAPContactListAdapterOld.SELECT && null != selectedContacts) {
                adapter = new TAPContactListAdapterOld(getItemAt(position), selectedContacts, listener);
            } else {
                adapter = new TAPContactListAdapterOld(viewType, getItemAt(position), listener);
            }
            rvContactInitial.setAdapter(adapter);
            rvContactInitial.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.VERTICAL, false));
            rvContactInitial.setHasFixedSize(false);
        }
    }

    public void setSectionTitles(String... sectionTitles) {
        this.sectionTitles = new ArrayList<>(Arrays.asList(sectionTitles));
    }

    public void clearSectionTitles() {
        sectionTitles.clear();
    }
}
