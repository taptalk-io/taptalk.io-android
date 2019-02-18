package io.taptalk.TapTalk.Data;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import io.taptalk.TapTalk.Const.TAPDefaultConstant;
import io.taptalk.TapTalk.Data.Contact.TAPMyContactDao;
import io.taptalk.TapTalk.Data.Message.TAPMessageDao;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Data.RecentSearch.TAPRecentSearchDao;
import io.taptalk.TapTalk.Data.RecentSearch.TAPRecentSearchEntity;
import io.taptalk.TapTalk.Model.TAPUserModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomDatabase.kDatabaseVersion;

@Database(entities = {TAPMessageEntity.class, TAPRecentSearchEntity.class, TAPUserModel.class}, version = kDatabaseVersion, exportSchema = false)
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
