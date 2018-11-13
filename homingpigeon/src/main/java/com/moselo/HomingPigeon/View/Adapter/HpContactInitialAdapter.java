package com.moselo.HomingPigeon.View.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moselo.HomingPigeon.Interface.TapTalkContactListInterface;
import com.moselo.HomingPigeon.Model.HpUserModel;
import com.moselo.HomingPigeon.R;

import java.util.List;

public class HpContactInitialAdapter extends RecyclerView.Adapter<HpContactInitialAdapter.ContactListHolder> {

    private List<List<HpUserModel>> contactList;
    private List<HpUserModel> selectedContacts;
    private TapTalkContactListInterface listener;
    private int viewType;

    public HpContactInitialAdapter(int viewType, List<List<HpUserModel>> contactList) {
        this.viewType = viewType;
        this.contactList = contactList;
    }

    // Constructor for selectable contacts
    public HpContactInitialAdapter(int viewType, List<List<HpUserModel>> contactList, List<HpUserModel> selectedContacts, TapTalkContactListInterface listener) {
        this.viewType = viewType;
        this.contactList = contactList;
        this.selectedContacts = selectedContacts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactListHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.hp_cell_contact_list_initial, parent, false));
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

    public List<List<HpUserModel>> getItems() {
        return contactList;
    }

    public List<HpUserModel> getItemAt(int position) {
        return contactList.get(position);
    }

    public void setItems(List<List<HpUserModel>> contactList) {
        this.contactList = contactList;
        notifyDataSetChanged();
    }

    class ContactListHolder extends RecyclerView.ViewHolder {

        private RecyclerView rvContactInitial;
        private TextView tvInitial;
        private List<HpUserModel> item;
        private HpContactListAdapter adapter;

        ContactListHolder(View itemView) {
            super(itemView);

            tvInitial = itemView.findViewById(R.id.tv_initial);
            rvContactInitial = itemView.findViewById(R.id.rv_contact_list_initial);
        }

        void onBind(int position) {
            item = getItemAt(position);

            char initial = item.get(0).getName().charAt(0);
            if (!Character.isAlphabetic(initial)) initial = '#';
            tvInitial.setText(String.valueOf(initial));

            if (viewType == HpContactListAdapter.SELECT && null != selectedContacts) {
                adapter = new HpContactListAdapter(getItemAt(position), selectedContacts, listener);
            } else {
                adapter = new HpContactListAdapter(viewType, getItemAt(position), listener);
            }
            rvContactInitial.setAdapter(adapter);
            rvContactInitial.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.VERTICAL, false));
            rvContactInitial.setHasFixedSize(false);
        }
    }
}