package io.taptalk.TapTalk.Data.Message;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.NUM_OF_ITEM;

@Dao
public interface TAPMessageDao {

    int numOfItem = NUM_OF_ITEM;

    @Delete
    void delete(List<TAPMessageEntity> messageEntities);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TAPMessageEntity messageEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<TAPMessageEntity> messageEntities);

    @Query("delete from Message_Table where localID = :localId")
    void delete(String localId);

    @Query("delete from Message_Table")
    void deleteAllMessage();

    @Query("select * from Message_Table order by created desc")
    LiveData<List<TAPMessageEntity>> getAllMessage();

    @Query("select * from Message_Table where RoomID like :roomID order by created desc limit " + numOfItem)
    List<TAPMessageEntity> getAllMessageListDesc(String roomID);

    @Query("select * from Message_Table where RoomID like :roomID order by created desc limit " + numOfItem)
    List<TAPMessageEntity> getAllMessageListAsc(String roomID);

    @Query("select * from Message_Table where " +
            "created in (select distinct created from Message_Table where created < :lastTimestamp and RoomID like :roomID order by created desc limit " + numOfItem + " ) " +
            "and RoomID like :roomID order by created desc")
    List<TAPMessageEntity> getAllMessageTimeStamp(Long lastTimestamp, String roomID);

    @Query("select * from Message_Table where body like :keyword escape '\\' order by created desc")
    List<TAPMessageEntity> searchAllMessages(String keyword);

    @Query("select * from (select roomID, max(created) as max_created from Message_Table group by roomID) secondQuery join Message_Table firstQuery on firstQuery.roomID = secondQuery.roomID and firstQuery.created = secondQuery.max_created " +
            "group by firstQuery.roomID order by firstQuery.created desc")
    List<TAPMessageEntity> getAllRoomList();

    @Query("select * from (select roomID, max(created) as max_created from Message_Table group by roomID) " +
            "secondQuery join Message_Table firstQuery on firstQuery.roomID = secondQuery.roomID and firstQuery.created = secondQuery.max_created where roomName like :keyword escape '\\' group by firstQuery.roomID order by firstQuery.created desc")
    List<TAPMessageEntity> searchAllChatRooms(String keyword);

    @Query("select localID, messageID, body, type, created, data, userID, xcUserID, username, userFullName, userImage " +
            "from Message_Table where type in (" + TYPE_IMAGE + ", " + TYPE_VIDEO +
            ") and created < :lastTimestamp and roomID = :roomID order by created desc limit " + numOfItem)
    List<TAPMessageEntity> getRoomMedias(Long lastTimestamp, String roomID);

    @Query("select localID, roomName, roomImage, roomType, roomColor from Message_Table where roomID = :roomID")
    TAPMessageEntity getRoom(String roomID);

    @Query("select count(isRead) from Message_Table where isRead = 0 and isHidden = 0 and isDeleted = 0 and RoomID like :roomID and userID not like :userID")
    Integer getUnreadCount(String userID, String roomID);

    @Query("select count(isRead) from Message_Table where isRead = 0 and isHidden = 0 and isDeleted = 0 and userID not like :userID")
    Integer getUnreadCount(String userID);

    @Query("update Message_Table set isFailedSend = 1, isSending = 0 where isSending = 1")
    void updatePendingStatus();

    @Query("update Message_Table set isFailedSend = 1, isSending = 0 where localID = :localID")
    void updatePendingStatus(String localID);

    @Query("update Message_Table set isFailedSend = 0, isSending = 1 where localID = :localID")
    void updateFailedStatusToSending(String localID);
}
