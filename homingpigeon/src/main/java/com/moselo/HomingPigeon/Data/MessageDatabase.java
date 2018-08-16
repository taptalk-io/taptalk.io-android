package com.moselo.HomingPigeon.Data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;
import android.text.Editable;

import com.commonsware.cwac.saferoom.SafeHelperFactory;
import com.moselo.HomingPigeon.Helper.DefaultConstant;

@Database(entities = {MessageEntity.class}, version = DefaultConstant.RoomDatabase.kDatabaseVersion, exportSchema = false)
public abstract class MessageDatabase extends RoomDatabase{

    private static MessageDatabase database;

    public static MessageDatabase getDatabase(Context context){
        if (null == database){
            SafeHelperFactory factory = SafeHelperFactory.fromUser(
                    Editable.Factory.getInstance().newEditable("MoseloOlesom"));
            database = Room.databaseBuilder(context,
                    MessageDatabase.class, "message_database")
                    .openHelperFactory(factory)
                    .build();
        }

        return database;
    }

    public abstract MessageDao messageDao();
}
