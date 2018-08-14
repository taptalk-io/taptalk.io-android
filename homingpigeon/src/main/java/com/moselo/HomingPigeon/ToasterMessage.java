package com.moselo.HomingPigeon;

import android.content.Context;
import android.widget.Toast;

public class ToasterMessage {
    public static void showToast(Context context, String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}
