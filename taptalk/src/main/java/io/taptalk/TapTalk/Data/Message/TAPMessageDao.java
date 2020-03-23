package io.taptalk.TapTalk.Data.Message;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MAX_ITEMS_PER_PAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_FILE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_IMAGE;
import static io.taptalk.TapTalk.Const.TAPDefaultConstant.MessageType.TYPE_VIDEO;

@Dao
public interface TAPMessageDao {

    int numOfItem = MAX_ITEMS_PER_PAGE;

    @Delete
    void delete(List<TAPMessageEntity> messageEntities);

    @Query("delete from message_table where roomID = :roomID and created <= :maxCreatedTimestamp")
    void deleteRoomMessageBeforeTimestamp(String roomID, long maxCreatedTimestamp);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(TAPMessageEntity messageEntity);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(List<TAPMessageEntity> messageEntities);

    @Query("delete from Message_Table where localID = :localId")
    void delete(String localId);

    @Query("delete from Message_Table")
    void deleteAllMessage();

    @Query("select * from Message_Table order by created desc")
    LiveData<List<TAPMessageEntity>> getAllMessageLiveData();

    @Query("select * from Message_Table where RoomID like :roomID order by created desc")
    List<TAPMessageEntity> getAllMessagesInRoom(String roomID);

    @Query("select * from Message_Table where RoomID like :roomID order by created desc limit " + numOfItem)
    List<TAPMessageEntity> getAllMessageListDesc(String roomID);

    @Query("select * from Message_Table where RoomID like :roomID order by created asc limit " + numOfItem)
    List<TAPMessageEntity> getAllMessageListAsc(String roomID);

    @Query("select * from Message_Table where " +
            "created in (select distinct created from Message_Table where created < :lastTimestamp and RoomID like :roomID order by created desc limit " + numOfItem + " ) " +
            "and RoomID like :roomID order by created desc")
    List<TAPMessageEntity> getAllMessageTimeStamp(Long lastTimestamp, String roomID);

    @Query("select * from Message_Table where body like :keyword escape '\\' and type != 9001 and isHidden = 0 order by created desc")
    List<TAPMessageEntity> searchAllMessages(String keyword);

    @Query("select * from (select roomID, max(created) as max_created from Message_Table where isHidden = 0 group by roomID) secondQuery join Message_Table firstQuery on firstQuery.roomID = secondQuery.roomID and firstQuery.created = secondQuery.max_created " +
            "group by firstQuery.roomID order by firstQuery.created desc")
    List<TAPMessageEntity> getAllRoomList();

    @Query("select * from Message_Table where isRead = 0 and isHidden = 0 and isDeleted = 0 and RoomID like :roomID and userID not like :userID order by created asc")
    List<TAPMessageEntity> getAllUnreadMessagesFromRoom(String userID, String roomID);

    @Query("select * from (select roomID, max(created) as max_created from Message_Table group by roomID) " +
            "secondQuery join Message_Table firstQuery on firstQuery.roomID = secondQuery.roomID and firstQuery.created = secondQuery.max_created where roomName like :keyword escape '\\' group by firstQuery.roomID order by firstQuery.created desc")
    List<TAPMessageEntity> searchAllChatRooms(String keyword);

    @Query("select * /*localID, messageID, body, type, created, data, roomID, userID, xcUserID, username, userFullName, userImage*/ " +
            "from Message_Table where type in (" + TYPE_IMAGE + ", " + TYPE_VIDEO +
            ") and roomID = :roomID and isSending = 0 and isHidden = 0 and isDeleted = 0 and (isFailedSend = 0 or isFailedSend IS NULL) " +
            "order by created desc limit " + numOfItem)
    List<TAPMessageEntity> getRoomMedias(String roomID);

    @Query("select * /*localID, messageID, body, type, created, data, roomID, userID, xcUserID, username, userFullName, userImage*/ " +
            "from Message_Table where type in (" + TYPE_IMAGE + ", " + TYPE_VIDEO +
            ") and created < :lastTimestamp and roomID = :roomID and isSending = 0 and isHidden = 0 and isDeleted = 0 and (isFailedSend = 0 or isFailedSend IS NULL) " +
            "order by created desc limit " + numOfItem)
    List<TAPMessageEntity> getRoomMedias(Long lastTimestamp, String roomID);

    @Query("select * from message_table where" +
            " type in (" + TYPE_IMAGE + ", " + TYPE_FILE + ", " + TYPE_VIDEO + ") " +
            "and roomID = :roomID and created <= :maxCreatedTimestamp")
    List<TAPMessageEntity> getRoomMediaMessageBeforeTimestamp(String roomID, long maxCreatedTimestamp);

    @Query("select * from message_table where" +
            " type in (" + TYPE_IMAGE + ", " + TYPE_FILE + ", " + TYPE_VIDEO + ") " +
            "and roomID = :roomID")
    List<TAPMessageEntity> getRoomMediaMessage(String roomID);

    @Query("select localID, roomName, roomImage, roomType, roomColor from Message_Table where roomID = :roomID")
    TAPMessageEntity getRoom(String roomID);

    @Query("select count(isRead) from Message_Table where isRead = 0 and isHidden = 0 and isDeleted = 0 and RoomID like :roomID and userID not like :userID")
    Integer getUnreadCount(String userID, String roomID);

    @Query("select count(isRead) from Message_Table where isRead = 0 and isHidden = 0 and isDeleted = 0 and userID not like :userID")
    Integer getUnreadCount(String userID);

    //@Query("select min(created) as created from message_table where isRead = 0 and isHidden = 0 and isDeleted = 0 and RoomID like :roomID and userID not like :userID")
    @Query("select localID, created from message_table where isRead = 0 and isHidden = 0 and isDeleted = 0 and RoomID like :roomID and userID not like :userID order by created asc limit 1")
    TAPMessageEntity getMinCreatedOfUnreadMessage(String userID, String roomID);

    @Query("update Message_Table set isFailedSend = 1, isSending = 0 where isSending = 1")
    void updatePendingStatus();

    @Query("update Message_Table set isFailedSend = 1, isSending = 0 where localID = :localID")
    void updatePendingStatus(String localID);

    @Query("update Message_Table set isFailedSend = 0, isSending = 1 where localID = :localID")
    void updateFailedStatusToSending(String localID);

    @Query("update Message_Table set isDelivered = 1, isRead = 1 where messageID = :messageID")
    void updateMessageAsRead(String messageID);

    @Query("update Message_Table set isDelivered = 1, isRead = 1 where messageID in (:messageIDs)")
    void updateMessagesAsRead(List<String> messageIDs);

    @Query("delete from Message_Table where roomID = :roomId")
    void deleteMessageByRoomId(String roomId);
}
