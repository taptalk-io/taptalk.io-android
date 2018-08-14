package com.moselo.HomingPigeon.Testing;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moselo.HomingPigeon.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageVH> {

    private List<com.moselo.HomingPigeon.Data.MessageEntity> items;

    public MessageAdapter(List<com.moselo.HomingPigeon.Data.MessageEntity> items) {
        this.items = items;
    }

    public void addItems(com.moselo.HomingPigeon.Data.MessageEntity entity){
        items.add(0,entity);
        notifyItemInserted(0);
    }

    public void addItems(List<com.moselo.HomingPigeon.Data.MessageEntity> entities){
        items = entities;
        notifyItemInserted(0);
    }

    @Override
    public MessageVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout;
        switch (viewType){
            case 1:
                layout = R.layout.layout_chat_message;
                break;
            default:
                layout = R.layout.layout_chat_log;
                break;
        }
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(layout,parent,false);
        return new MessageVH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageVH holder, int position) {
        com.moselo.HomingPigeon.Data.MessageEntity mMessage = items.get(position);
        holder.setMessage(mMessage.getMessage());
        holder.setUsername(mMessage.getUserName());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType();
    }
}
