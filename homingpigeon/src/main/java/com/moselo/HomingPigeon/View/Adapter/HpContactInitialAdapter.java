package com.moselo.HomingPigeon.View.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moselo.HomingPigeon.Interface.ContactListInterface;
import com.moselo.HomingPigeon.Manager.HpDataManager;
import com.moselo.HomingPigeon.Model.HpUserModel;
import com.moselo.HomingPigeon.R;

import java.util.List;

public class HpContactInitialAdapter extends RecyclerView.Adapter<HpContactInitialAdapter.ContactListHolder> {

    private List<List<HpUserModel>> contactList;
    private ContactListInterface listener;
    private int viewType;

    public HpContactInitialAdapter(int viewType, List<List<HpUserModel>> contactList) {
        this.viewType = viewType;
        this.contactList = contactList;
    }

    public HpContactInitialAdapter(int viewType, List<List<HpUserModel>> contactList, ContactListInterface listener) {
        this.viewType = viewType;
        this.contactList = contactList;
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
        private String myID;

        ContactListHolder(View itemView) {
            super(itemView);

            tvInitial = itemView.findViewById(R.id.tv_initial);
            rvContactInitial = itemView.findViewById(R.id.rv_contact_list_initial);
            myID = HpDataManager.getInstance().getActiveUser(itemView.getContext()).getUserID();
        }

        void onBind(int position) {
            item = getItemAt(position);

            char initial = item.get(0).getName().charAt(0);
            if (!Character.isAlphabetic(initial)) initial = '#';
            tvInitial.setText(String.valueOf(initial));

            adapter = new HpContactListAdapter(viewType, getItemAt(position), listener, myID);
            rvContactInitial.setAdapter(adapter);
            rvContactInitial.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.VERTICAL, false));
            rvContactInitial.setHasFixedSize(false);
        }
    }
}