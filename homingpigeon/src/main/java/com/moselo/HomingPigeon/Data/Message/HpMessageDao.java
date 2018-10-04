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

    @Query("select * from Message_Table order by messageCreated desc")
    LiveData<List<HpMessageEntity>> getAllMessage();

    @Query("select messageID, localID, roomID, roomName, roomColor, roomType," +
            " roomImage, messageType, body, messageCreated, " +
            "userID, xcUserID, userFullName, userImage, username, userEmail, " +
            "userPhone, userRole, lastLogin, requireChangePassword, userCreated, userUpdated, " +
            "recipientID, hasRead, isRead, isDelivered, isHidden, isDeleted, isSending, isFailedSend" +
            " from Message_Table where RoomID like :roomID order by messageCreated desc limit " + numOfItem)
    List<HpMessageEntity> getAllMessageList(String roomID);

    @Query("select messageID, localID, roomID, roomName, roomColor, roomType, " +
            "roomImage, messageType, body, messageCreated, " +
            "userID, xcUserID, userFullName, userImage, username, userEmail, " +
            "userPhone, userRole, lastLogin, requireChangePassword, userCreated, userUpdated, " +
            "recipientID, hasRead, isRead, isDelivered, isHidden, isDeleted, isSending, isFailedSend " +
            "from Message_Table where " +
            "messageCreated in (select distinct messageCreated from Message_Table where messageCreated < :lastTimestamp and RoomID like :roomID order by messageCreated desc limit " + numOfItem + " ) " +
            "and RoomID like :roomID order by messageCreated desc")
    List<HpMessageEntity> getAllMessageTimeStamp(Long lastTimestamp, String roomID);

    @Query("select localID, " +
            "userID, xcUserID, userFullName, userImage, username, userEmail, " +
            "userPhone, userRole, lastLogin, requireChangePassword, userCreated, userUpdated, " +
            "roomName, secondQuery.roomID, roomType, body, messageCreated, hasRead, isDelivered, isFailedSend, " +
            "isSending, recipientID, messageType from (select roomID, max(messageCreated) as max_created from Message_Table group by roomID) secondQuery join Message_Table firstQuery on firstQuery.roomID = secondQuery.roomID and firstQuery.messageCreated = secondQuery.max_created order by firstQuery.messageCreated desc")
    List<HpMessageEntity> getAllRoomList();

    //@Query("select count(hasRead) from message_table where hasRead = 0 and RoomID like :roomID and userID not like :userID")
    @Query("select count(isSending) from message_table where isSending = 0 and RoomID like :roomID and userID not like :userID")
    Integer getUnreadCount(String userID, String roomID);

    @Query("update Message_Table set isFailedSend = 1, isSending = 0 where isSending = 1")
    void updatePendingStatus();

    @Query("update Message_Table set isFailedSend = 1, isSending = 0 where localID = :localID")
    void updatePendingStatus(String localID);
}
