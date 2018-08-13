package com.moselo.HomingPigeon.Testing;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.moselo.HomingPigeon.R;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageVH> {

    private List<MessageEntity> items;

    public MessageAdapter(List<MessageEntity> items) {
        this.items = items;
    }

    public void addItems(MessageEntity entity){
        items.add(0,entity);
        notifyItemInserted(0);
//        notifyDataSetChanged();
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
        Log.e(LibraryActivity.class.getSimpleName(), "onBindViewHolder: " );
        Log.e(LibraryActivity.class.getSimpleName(), "onBindViewHolder: "+items.get(position).getMessage() );
        Log.e(LibraryActivity.class.getSimpleName(), "onBindViewHolder: "+items.get(position).getUsername());
        MessageEntity mMessage = items.get(position);
        holder.setMessage(items.get(position).getMessage());
        holder.setUsername(items.get(position).getUsername());
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
