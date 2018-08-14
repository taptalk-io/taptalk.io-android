package com.moselo.HomingPigeon.Data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {MessageEntity.class}, version = 1)
public abstract class MessageDatabase extends RoomDatabase{

    private static MessageDatabase database;

    public static MessageDatabase getDatabase(Context context){
        if (null == database){
            database = Room.databaseBuilder(context,
                    MessageDatabase.class, "message_database")
                    .build();
        }

        return database;
    }

    public abstract MessageDao messageDao();
}
