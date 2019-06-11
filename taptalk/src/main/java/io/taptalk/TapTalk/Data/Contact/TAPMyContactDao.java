package io.taptalk.TapTalk.Data.Contact;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

import io.taptalk.TapTalk.Model.TAPUserModel;

@Dao
public interface TAPMyContactDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TAPUserModel... userModels);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<TAPUserModel> userModels);

    @Delete
    void delete(TAPUserModel... userModels);

    @Delete
    void delete(List<TAPUserModel> userModels);

    @Query("delete from MyContact")
    void deleteAllContact();

    @Update
    void update(TAPUserModel userModel);

    @Query("select * from MyContact where isContact = 1 and deleted is null or deleted = 0 order by name collate nocase asc")
    List<TAPUserModel> getAllMyContact();

    @Query("select * from MyContact where isContact = 1 AND deleted is null or deleted = 0 order by name collate nocase asc")
    LiveData<List<TAPUserModel>> getAllMyContactLive();

    @Query("select * from MyContact where name like :keyword escape '\\' and isContact = 1 order by name asc")
    List<TAPUserModel> searchAllMyContacts(String keyword);

    @Query("select count(userID) from MyContact where userID like :userID and isContact = 1 ")
    Integer checkUserInMyContacts(String userID);

    @Query("select * from MyContact where xcUserID = :xcUserID")
    TAPUserModel checkUserWithXcUserID(String xcUserID);

    @Query("select * from MyContact")
    List<TAPUserModel> getAllUserData();
}
