package com.moselo.HomingPigeon.Data.Message;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.NUM_OF_ITEM;

@Dao
public interface MessageDao {

    int numOfItem = NUM_OF_ITEM;

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MessageEntity messageEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<MessageEntity> messageEntities);

    @Query("delete from message_table where localID = :localId")
    void delete(String localId);

    @Query("select * from message_table order by Created desc")
    LiveData<List<MessageEntity>> getAllMessage();

    @Query("select * from message_table order by Created desc limit " + numOfItem)
    List<MessageEntity> getAllMessageList();

    @Query("select * from message_table where Created in (select distinct Created from message_table where Created < :lastTimestamp order by Created desc limit "+ numOfItem+" ) order by Created desc")
    List<MessageEntity> getAllMessageTimeStamp(Long lastTimestamp);

    @Query("update message_table set isFailedSend = 1, isSending = 0 where isSending = 1")
    void updatePendingStatus();

    @Query("update message_table set isFailedSend = 1, isSending = 0 where localID = :localID")
    void updatePendingStatus(String localID);
}
