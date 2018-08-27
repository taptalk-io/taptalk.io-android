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

    @Query("select * from message_table order by Created desc limit 10")
    List<MessageEntity> getAllMessageList();

//    @Query("select * from message_table where Created in (select distinct Created from message_table where Created < :lastTimestamp limit 10) order by Created desc")
    @Query("select * from message_table where Created < :lastTimestamp order by Created desc limit 10")
//    @Query("select * from message_table order by Created desc limit 10")
    List<MessageEntity> getAllMessageTimeStamp(Long lastTimestamp);
//    List<MessageEntity> getAllMessageTimeStamp();
}
