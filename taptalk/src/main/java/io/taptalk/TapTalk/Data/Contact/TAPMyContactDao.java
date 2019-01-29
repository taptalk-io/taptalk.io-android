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

    @Query("select userID, xcUserID, name, thumbnail, fullsize, username, email, phoneNumber, " +
            "userRoleCode, roleName, roleIconURL, lastLogin, lastActivity, requireChangePassword, " +
            "created, updated from MyContact where isContact = 1 order by name asc")
    List<TAPUserModel> getAllMyContact();

    @Query("select userID, xcUserID, name, thumbnail, fullsize, username, email, phoneNumber, " +
            "userRoleCode, roleName, roleIconURL, lastLogin, lastActivity, requireChangePassword, " +
            "created, updated from MyContact where isContact = 1 order by name asc")
    LiveData<List<TAPUserModel>> getAllMyContactLive();

    @Query("select userID, xcUserID, name, thumbnail, fullsize, username, email, phoneNumber, " +
            "userRoleCode, roleName, roleIconURL, lastLogin, lastActivity, requireChangePassword, " +
            "created, updated from MyContact where name like :keyword and isContact = 1 order by name asc")
    List<TAPUserModel> searchAllMyContacts(String keyword);

    @Query("select count(userID) from MyContact where userID like :userID and isContact = 1 ")
    Integer checkUserInMyContacts(String userID);

    @Query("select userID, name, thumbnail, fullsize, username, " +
            "userRoleCode, roleName, roleIconURL, lastLogin, lastActivity " +
            "from MyContact where xcUserID = :xcUserID")
    TAPUserModel checkUserWithXcUserID(String xcUserID);

    @Query("select userID, xcUserID, name, thumbnail, fullsize, username, email, phoneNumber, " +
            "userRoleCode, roleName, roleIconURL, lastLogin, lastActivity, requireChangePassword, " +
            "created, updated, isContact from MyContact")
    List<TAPUserModel> getAllUserData();
}
