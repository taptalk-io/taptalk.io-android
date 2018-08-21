package com.moselo.HomingPigeon.Data;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MessageEntity messageEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<MessageEntity> messageEntities);

    @Query("select * from message_table order by Created desc")
    LiveData<List<MessageEntity>> getAllMessage();

    @Query("select * from message_table order by Created desc")
    List<MessageEntity> getAllMessageList();
}
