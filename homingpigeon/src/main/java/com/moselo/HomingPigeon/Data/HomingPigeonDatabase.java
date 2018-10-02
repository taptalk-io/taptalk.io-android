package com.moselo.HomingPigeon.Data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.moselo.HomingPigeon.Data.Message.MessageDao;
import com.moselo.HomingPigeon.Data.Message.MessageEntity;
import com.moselo.HomingPigeon.Data.RecentSearch.RecentSearchDao;
import com.moselo.HomingPigeon.Data.RecentSearch.RecentSearchEntity;
import com.moselo.HomingPigeon.Helper.HpDefaultConstant;

@Database(entities = {MessageEntity.class, RecentSearchEntity.class}, version = HpDefaultConstant.RoomDatabase.kDatabaseVersion, exportSchema = false)
public abstract class HomingPigeonDatabase extends RoomDatabase{

    private static HomingPigeonDatabase database;

    public static HomingPigeonDatabase getDatabase(Context context){
        if (null == database){
//            SafeHelperFactory factory = SafeHelperFactory.fromUser(
//                    Editable.Factory.getInstance().newEditable(DB_ENCRYPT_PASS));
            database = Room.databaseBuilder(context,
                    HomingPigeonDatabase.class, "message_database")
//                    .openHelperFactory(factory)
                    .build();
        }

        return database;
    }

    public abstract MessageDao messageDao();
    public abstract RecentSearchDao recentSearchDao();
}
