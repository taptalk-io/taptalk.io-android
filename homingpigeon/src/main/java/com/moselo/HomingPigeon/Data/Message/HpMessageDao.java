package com.moselo.HomingPigeon.Data.Message;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import static com.moselo.HomingPigeon.Helper.HpDefaultConstant.NUM_OF_ITEM;

@Dao
public interface HpMessageDao {

    int numOfItem = NUM_OF_ITEM;

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HpMessageEntity messageEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<HpMessageEntity> messageEntities);

    @Query("delete from Message_Table where localID = :localId")
    void delete(String localId);

    @Query("select * from Message_Table order by Created desc")
    LiveData<List<HpMessageEntity>> getAllMessage();

    @Query("select MessageID, localID, RoomID, RoomName, RoomColor, RoomType," +
            " RoomImage, messageType, message, created, user, recipientID, " +
            "hasRead, isRead, isDelivered, isHidden, isDeleted, isSending, isFailedSend" +
            " from Message_Table where RoomID like :roomID order by Created desc limit " + numOfItem)
    List<HpMessageEntity> getAllMessageList(String roomID);

    @Query("select MessageID, localID, RoomID, RoomName, RoomColor, RoomType, " +
            "RoomImage, messageType, message, created, user, recipientID, " +
            "hasRead, isRead, isDelivered, isHidden, isDeleted, isSending, isFailedSend " +
            "from Message_Table where " +
            "Created in (select distinct Created from Message_Table where Created < :lastTimestamp and RoomID like :roomID order by Created desc limit " + numOfItem + " ) " +
            "and RoomID like :roomID order by Created desc")
    List<HpMessageEntity> getAllMessageTimeStamp(Long lastTimestamp, String roomID);

    @Query("select localID, user, RoomName, secondQuery.RoomID, RoomType, message, created, hasRead, isDelivered, isFailedSend, " +
            "isSending, recipientID, messageType from (select RoomID, max(created) as max_created from Message_Table group by RoomID) secondQuery join Message_Table firstQuery on firstQuery.RoomID = secondQuery.RoomID and firstQuery.created = secondQuery.max_created order by firstQuery.created desc")
    List<HpMessageEntity> getAllRoomList();

    @Query("select count(hasRead) from message_table where hasRead = 0 and RoomID like :roomID")
    Integer getUnreadCount(String roomID);

    @Query("update Message_Table set isFailedSend = 1, isSending = 0 where isSending = 1")
    void updatePendingStatus();

    @Query("update Message_Table set isFailedSend = 1, isSending = 0 where localID = :localID")
    void updatePendingStatus(String localID);
}
