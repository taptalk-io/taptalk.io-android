package com.moselo.HomingPigeon.Data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.moselo.HomingPigeon.Data.Message.HpMessageDao;
import com.moselo.HomingPigeon.Data.Message.HpMessageEntity;
import com.moselo.HomingPigeon.Data.RecentSearch.HpRecentSearchDao;
import com.moselo.HomingPigeon.Data.RecentSearch.HpRecentSearchEntity;
import com.moselo.HomingPigeon.Helper.HpDefaultConstant;

@Database(entities = {HpMessageEntity.class, HpRecentSearchEntity.class}, version = HpDefaultConstant.RoomDatabase.kDatabaseVersion, exportSchema = false)
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

    public abstract HpMessageDao messageDao();
    public abstract HpRecentSearchDao recentSearchDao();
}
