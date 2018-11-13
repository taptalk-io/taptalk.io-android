package com.moselo.HomingPigeon.Data.Contact;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.moselo.HomingPigeon.Model.TAPUserModel;

import java.util.List;

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

    @Query("delete from TAPUserModel")
    void deleteAllContact();

    @Update
    void update(TAPUserModel userModel);

    @Query("select userID, xcUserID, name, thumbnail, fullsize, username, email, phoneNumber, " +
            "userRoleID, roleName, roleIconURL, lastLogin, lastActivity, requireChangePassword, " +
            "created, updated from TAPUserModel order by name asc")
    List<TAPUserModel> getAllMyContact();

    @Query("select userID, xcUserID, name, thumbnail, fullsize, username, email, phoneNumber, " +
            "userRoleID, roleName, roleIconURL, lastLogin, lastActivity, requireChangePassword, " +
            "created, updated from TAPUserModel order by name asc")
    LiveData<List<TAPUserModel>> getAllMyContactLive();

    @Query("select userID, xcUserID, name, thumbnail, fullsize, username, email, phoneNumber, " +
            "userRoleID, roleName, roleIconURL, lastLogin, lastActivity, requireChangePassword, " +
            "created, updated from TAPUserModel where name like :keyword order by name asc")
    List<TAPUserModel> searchAllMyContacts(String keyword);

    @Query("select count(userID) from TAPUserModel where userID like :userID")
    Integer checkUserInMyContacts(String userID);
}
