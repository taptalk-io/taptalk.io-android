package io.taptalk.TapTalk.Data;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import io.taptalk.TapTalk.Data.Contact.TAPMyContactDao;
import io.taptalk.TapTalk.Data.Message.TAPMessageDao;
import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Data.RecentSearch.TAPRecentSearchDao;
import io.taptalk.TapTalk.Data.RecentSearch.TAPRecentSearchEntity;
import io.taptalk.TapTalk.Model.TAPUserModel;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomDatabase.kDatabaseVersion;

@Database(entities = {TAPMessageEntity.class, TAPRecentSearchEntity.class, TAPUserModel.class}, version = kDatabaseVersion, exportSchema = false)
public abstract class TapTalkDatabase extends RoomDatabase {

    private static TapTalkDatabase database;

    // TODO: 16/10/18 kalau udah di deploy jangan lupa di encrypt
    public static TapTalkDatabase getDatabase(Context context) {
        if (null == database) {
//            SafeHelperFactory factory = SafeHelperFactory.fromUser(
//                    Editable.Factory.getInstance().newEditable(DB_ENCRYPT_PASS));
            database = Room.databaseBuilder(context,
                    TapTalkDatabase.class, "message_database")
                    .addMigrations(MIGRATION_1_2)
                    .addMigrations(MIGRATION_2_3)
                    .addMigrations(MIGRATION_3_4)
                    .addMigrations(MIGRATION_4_5)
                    .addMigrations(MIGRATION_5_6)
                    .addMigrations(MIGRATION_6_7)
//                    .openHelperFactory(factory)
                    .build();
        }

        return database;
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

    public abstract TAPMessageDao messageDao();

    public abstract TAPRecentSearchDao recentSearchDao();

    public abstract TAPMyContactDao myContactDao();
}
