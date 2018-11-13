package com.moselo.HomingPigeon.Data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.moselo.HomingPigeon.Data.Contact.TAPMyContactDao;
import com.moselo.HomingPigeon.Data.Message.TAPMessageDao;
import com.moselo.HomingPigeon.Data.Message.TAPMessageEntity;
import com.moselo.HomingPigeon.Data.RecentSearch.TAPRecentSearchDao;
import com.moselo.HomingPigeon.Data.RecentSearch.TAPRecentSearchEntity;
import com.moselo.HomingPigeon.Const.TAPDefaultConstant;
import com.moselo.HomingPigeon.Model.TAPUserModel;

@Database(entities = {TAPMessageEntity.class, TAPRecentSearchEntity.class, TAPUserModel.class}, version = TAPDefaultConstant.RoomDatabase.kDatabaseVersion, exportSchema = false)
public abstract class TapTalkDatabase extends RoomDatabase{

    private static TapTalkDatabase database;

    // TODO: 16/10/18 kalau udah di deploy jangan lupa di encrypt
    public static TapTalkDatabase getDatabase(Context context){
        if (null == database){
//            SafeHelperFactory factory = SafeHelperFactory.fromUser(
//                    Editable.Factory.getInstance().newEditable(DB_ENCRYPT_PASS));
            database = Room.databaseBuilder(context,
                    TapTalkDatabase.class, "message_database")
//                    .openHelperFactory(factory)
                    .build();
        }

        return database;
    }

    public abstract TAPMessageDao messageDao();
    public abstract TAPRecentSearchDao recentSearchDao();
    public abstract TAPMyContactDao myContactDao();
}
