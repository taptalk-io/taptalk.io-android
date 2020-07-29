package io.taptalk.TapTalk.Data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.HashMap;

import io.taptalk.TapTalk.Data.Contact.TAPMyContactDao;
import io.taptalk.TapTalk.Data.Message.TAPMessageDao;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Data.RecentSearch.TAPRecentSearchDao;
import io.taptalk.TapTalk.Data.RecentSearch.TAPRecentSearchEntity;
import io.taptalk.TapTalk.Model.TAPUserModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomDatabase.DATABASE_NAME;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomDatabase.kDatabaseVersion;

@Database(entities = {TAPMessageEntity.class, TAPRecentSearchEntity.class, TAPUserModel.class}, version = kDatabaseVersion, exportSchema = false)
public abstract class TapTalkDatabase extends RoomDatabase {

    private static HashMap<String, TapTalkDatabase> databases;

    private static HashMap<String, TapTalkDatabase> getDatabases() {
        return null == databases ? databases = new HashMap<>() : databases;
    }

    // TODO: 16/10/18 kalau udah di deploy jangan lupa di encrypt
    public static TapTalkDatabase getDatabase(String instanceKey, Context context) {
        if (null == getDatabases().get(instanceKey)) {
//            SafeHelperFactory factory = SafeHelperFactory.fromUser(
//                    Editable.Factory.getInstance().newEditable(DB_ENCRYPT_PASS));
            synchronized (TapTalkDatabase.class) {
                String prefix = "";
                if (null != instanceKey && !instanceKey.isEmpty()) {
                    prefix = instanceKey + "_";
                }
                TapTalkDatabase database = Room.databaseBuilder(
                        context,
                        TapTalkDatabase.class,
                        prefix + DATABASE_NAME)
                        .addMigrations(MIGRATION_1_2)
                        .addMigrations(MIGRATION_2_3)
                        .addMigrations(MIGRATION_3_4)
                        .addMigrations(MIGRATION_4_5)
                        .addMigrations(MIGRATION_5_6)
                        .addMigrations(MIGRATION_6_7)
                        .addMigrations(MIGRATION_7_8)
//                    .openHelperFactory(factory)
                        .build();
                getDatabases().put(instanceKey, database);
            }
        }
        return getDatabases().get(instanceKey);
    }

    private static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("CREATE INDEX index_MyContact_isContact ON MyContact(isContact)");
        }
    };

    private static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'MyContact' ADD COLUMN 'phoneWithCode' TEXT");
            database.execSQL("ALTER TABLE 'MyContact' ADD COLUMN 'isEmailVerified' INTEGER");
            database.execSQL("ALTER TABLE 'MyContact' ADD COLUMN 'isPhoneVerified' INTEGER");
            database.execSQL("ALTER TABLE 'MyContact' ADD COLUMN 'countryID' INTEGER");
            database.execSQL("ALTER TABLE 'MyContact' ADD COLUMN 'countryCallingCode' TEXT");
        }
    };

    private static final Migration MIGRATION_3_4 = new Migration(3, 4) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'MyContact' ADD COLUMN 'deleted' INTEGER");
        }
    };

    private static final Migration MIGRATION_4_5 = new Migration(4, 5) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'Message_Table' ADD COLUMN 'deleted' INTEGER");
        }
    };

    private static final Migration MIGRATION_5_6 = new Migration(5, 6) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'Message_Table' ADD COLUMN 'action' TEXT");
            database.execSQL("ALTER TABLE 'Message_Table' ADD COLUMN 'targetType' TEXT");
            database.execSQL("ALTER TABLE 'Message_Table' ADD COLUMN 'targetID' TEXT");
            database.execSQL("ALTER TABLE 'Message_Table' ADD COLUMN 'targetXCID' TEXT");
            database.execSQL("ALTER TABLE 'Message_Table' ADD COLUMN 'targetName' TEXT");
        }
    };

    private static final Migration MIGRATION_6_7 = new Migration(6, 7) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("ALTER TABLE 'Message_Table' ADD COLUMN 'xcRoomID' TEXT");
            database.execSQL("ALTER TABLE 'Message_Table' ADD COLUMN 'isRoomLocked' INTEGER");
            database.execSQL("ALTER TABLE 'Message_Table' ADD COLUMN 'isRoomDeleted' INTEGER");
            database.execSQL("ALTER TABLE 'Message_Table' ADD COLUMN 'roomDeletedTimestamp' INTEGER");
            database.execSQL("ALTER TABLE 'Message_Table' ADD COLUMN 'roomLockedTimestamp' INTEGER");
        }
    };


    private static final Migration MIGRATION_7_8 = new Migration(7, 8) {
        @Override
        public void migrate(@NonNull SupportSQLiteDatabase database) {
            database.execSQL("DROP INDEX index_MyContact_isContact");
        }
    };

    public abstract TAPMessageDao messageDao();

    public abstract TAPRecentSearchDao recentSearchDao();

    public abstract TAPMyContactDao myContactDao();
}
