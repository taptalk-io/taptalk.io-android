package com.moselo.HomingPigeon.Data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MessageDao {

    @Query("select * from message_table order by MessageID desc")
    LiveData<List<MessageEntity>> getAllMessage();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert (List<MessageEntity> messageEntities);
}
