package com.moselo.HomingPigeon.View.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.moselo.HomingPigeon.Listener.ContactListListener;
import com.moselo.HomingPigeon.Model.UserModel;
import com.moselo.HomingPigeon.R;

import java.util.List;

public class ContactInitialAdapter extends RecyclerView.Adapter<ContactInitialAdapter.ContactListHolder> {

    private List<List<UserModel>> contactList;
    private ContactListListener listener;
    private int viewType;

    public ContactInitialAdapter(int viewType, List<List<UserModel>> contactList) {
        this.viewType = viewType;
        this.contactList = contactList;
    }

    public ContactInitialAdapter(int viewType, List<List<UserModel>> contactList, ContactListListener listener) {
        this.viewType = viewType;
        this.contactList = contactList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ContactListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactListHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_contact_list_initial, parent, false));
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

    public List<List<UserModel>> getItems() {
        return contactList;
    }

    public List<UserModel> getItemAt(int position) {
        return contactList.get(position);
    }

    public void setItems(List<List<UserModel>> contactList) {
        this.contactList = contactList;
        notifyDataSetChanged();
    }

    class ContactListHolder extends RecyclerView.ViewHolder {

        private RecyclerView rvContactInitial;
        private TextView tvInitial;
        private List<UserModel> item;
        private ContactListAdapter adapter;

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

            adapter = new ContactListAdapter(viewType, getItemAt(position), listener);
            rvContactInitial.setAdapter(adapter);
            rvContactInitial.setLayoutManager(new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.VERTICAL, false));
            rvContactInitial.setHasFixedSize(false);
        }
    }
}