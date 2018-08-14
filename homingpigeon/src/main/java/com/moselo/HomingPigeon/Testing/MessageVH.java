package com.moselo.HomingPigeon.Testing;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.moselo.HomingPigeon.R;

public class MessageVH extends RecyclerView.ViewHolder {

    private TextView tvMessage;
    private TextView tvUsername;

    public MessageVH(View itemView) {
        super(itemView);
        tvMessage = itemView.findViewById(R.id.tv_message);
        tvUsername = itemView.findViewById(R.id.tv_username);
    }

    public void setMessage(String message){
        if (null == message) return;
        tvMessage.setText(message);
        Log.e(LibraryActivity.class.getSimpleName(), "setMessage: " );
    }

    public void setUsername(String username){
        if (null == username) return;
        tvUsername.setText(username);
    }
}
