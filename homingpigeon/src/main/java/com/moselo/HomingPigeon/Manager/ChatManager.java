package com.moselo.HomingPigeon.Manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.moselo.HomingPigeon.Data.MessageEntity;
import com.moselo.HomingPigeon.Helper.DefaultConstant;
import com.moselo.HomingPigeon.Helper.HomingPigeon;
import com.moselo.HomingPigeon.Helper.Utils;
import com.moselo.HomingPigeon.Listener.HomingPigeonChatListener;
import com.moselo.HomingPigeon.Listener.HomingPigeonSocketListener;
import com.moselo.HomingPigeon.Model.EmitModel;
import com.moselo.HomingPigeon.Model.MessageModel;
import com.moselo.HomingPigeon.Model.UserModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionEvent.kEventOpenRoom;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionEvent.kSocketAuthentication;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionEvent.kSocketCloseRoom;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionEvent.kSocketDeleteMessage;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionEvent.kSocketNewMessage;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionEvent.kSocketOpenMessage;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionEvent.kSocketStartTyping;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionEvent.kSocketStopTyping;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionEvent.kSocketUpdateMessage;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionEvent.kSocketUserOffline;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.ConnectionEvent.kSocketUserOnline;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.K_USER;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.MessageQueue.MESSAGE;
import static com.moselo.HomingPigeon.Helper.DefaultConstant.MessageQueue.NUM_OF_ATTEMPT;

public class ChatManager {

    private static ChatManager instance;
    private List<HomingPigeonChatListener> chatListeners;
    //    private Map<String, JSONObject> messageQueue;
    private Map<String, String> messageDrafts;
    private String activeRoom;
    private UserModel activeUser;
    private final Integer CHARACTER_LIMIT = 1000;

    private HomingPigeonSocketListener socketListener = new HomingPigeonSocketListener() {
        @Override
        public void onNewMessage(String eventName, String emitData) {
            switch (eventName) {
                case kEventOpenRoom:
                    break;
                case kSocketCloseRoom:
                    break;
                case kSocketNewMessage:
                    EmitModel<MessageModel> messageEmit = Utils.getInstance()
                            .fromJSON(new TypeReference<EmitModel<MessageModel>>() {
                            }, emitData);
                    MessageModel newMessage;
                    try {
                        // Decrypt received message
                        newMessage = MessageModel.BuilderDecrypt(messageEmit.getData());
                        newMessage.setIsSending(0);

                        // Insert decrypted message to database
                        DataManager.getInstance().insertToDatabase(ChatManager.getInstance().convertToEntity(newMessage));

                        // Receive message in active room
                        if (null != chatListeners && !chatListeners.isEmpty() && newMessage.getRoomID().equals(activeRoom)) {
                            for (HomingPigeonChatListener chatListener : chatListeners)
                                chatListener.onReceiveTextMessageInActiveRoom(newMessage);
                        }
                        // Receive message outside active room
                        else if (null != chatListeners && !chatListeners.isEmpty() && !newMessage.getRoomID().equals(activeRoom)) {
                            for (HomingPigeonChatListener chatListener : chatListeners)
                                chatListener.onReceiveTextMessageInOtherRoom(newMessage);
                        }

                        // Remove message from queue and re-check queue
                        //if (newMessage.getUser().getUserID().equals(activeUser.getUserID())) {
                        //removeFromQueue(newMessage.getLocalID());
                        //runMessageQueue();
                        //}
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                    break;
                case kSocketUpdateMessage:
                    break;
                case kSocketDeleteMessage:
                    break;
                case kSocketOpenMessage:
                    break;
                case kSocketStartTyping:
                    break;
                case kSocketStopTyping:
                    break;
                case kSocketAuthentication:
                    break;
                case kSocketUserOnline:
                    break;
                case kSocketUserOffline:
                    break;
            }
        }
    };

    public static ChatManager getInstance() {
        return instance == null ? (instance = new ChatManager()) : instance;
    }

    public ChatManager() {
        ConnectionManager.getInstance().addSocketListener(socketListener);
        setActiveUser(Utils.getInstance().fromJSON(new TypeReference<UserModel>() {
                                                   },
                PreferenceManager.getDefaultSharedPreferences(HomingPigeon.appContext)
                        .getString(K_USER, null)));
        chatListeners = new ArrayList<>();
        //messageQueue = new LinkedHashMap<>();
        messageDrafts = new HashMap<>();
    }

    public void addChatListener(HomingPigeonChatListener chatListener) {
        chatListeners.add(chatListener);
    }

    public void removeChatListener(HomingPigeonChatListener chatListener) {
        chatListeners.remove(chatListener);
    }

    public void removeChatListenerAt(int index) {
        chatListeners.remove(index);
    }

    public void clearChatListener() {
        chatListeners.clear();
    }

    public String getActiveRoom() {
        return activeRoom;
    }

    public void setActiveRoom(String roomId) {
        this.activeRoom = roomId;
    }

    public UserModel getActiveUser() {
        return activeUser;
    }

    public void setActiveUser(UserModel user) {
        this.activeUser = user;
    }

    public void saveActiveUser(Context context, UserModel user) {
        this.activeUser = user;
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putString(K_USER, Utils.getInstance().toJsonString(user)).apply();
    }

//    public Map<String, MessageModel> getMessageQueueInActiveRoom() {
//        Map<String, MessageModel> roomQueue = new LinkedHashMap<>();
//        for (Map.Entry<String, JSONObject> entry : messageQueue.entrySet()) {
//            try {
//                MessageModel tempMessage = (MessageModel) entry.getValue().get(MESSAGE);
//                if (tempMessage.getRoomID().equals(activeRoom)) {
//                    roomQueue.put(entry.getKey(), (MessageModel) entry.getValue().get(MESSAGE));
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//        }
//        return roomQueue;
//    }

    /**
     * generate room ID
     */
    public String arrangeRoomId(String userId, String friendId) {
        int myId = (null != userId && !"null".equals(userId)) ? Integer.parseInt(userId) : 0;
        int fId = (null != friendId && !"null".equals(friendId)) ? Integer.parseInt(friendId) : 0;
        return myId < fId ? myId + "-" + fId : fId + "-" + myId;
    }

    /**
     * convert MessageEntity to MessageModel
     */
    public MessageModel convertToModel(MessageEntity entity) {
        return new MessageModel(
                entity.getMessageID(),
                entity.getLocalID(),
                entity.getMessage(),
                entity.getRoomID(),
                entity.getType(),
                entity.getCreated(),
                Utils.getInstance().fromJSON(new TypeReference<UserModel>() {
                }, entity.getUser()),
                entity.getDeleted(),
                entity.getIsSending(),
                entity.getIsFailedSend());
    }

    /**
     * convert MessageModel to MessageEntity
     */
    public MessageEntity convertToEntity(MessageModel model) {
        return new MessageEntity(
                model.getMessageID(),
                model.getLocalID(),
                model.getRoomID(),
                model.getType(),
                model.getMessage(),
                model.getCreated(),
                Utils.getInstance().toJsonString(model.getUser()),
                Utils.getInstance().toJsonString(model.getDeliveredTo()),
                Utils.getInstance().toJsonString(model.getSeenBy()),
                model.getDeleted(),
                model.getIsSending(),
                model.getIsFailedSend());
    }

    /**
     * sending text messages
     */
    public void sendTextMessage(String textMessage) {
        Integer startIndex;
        // Check if message exceeds character limit
        if (textMessage.length() > CHARACTER_LIMIT) {
            List<MessageEntity> messageEntities = new ArrayList<>();
            Integer length = textMessage.length();
            for (startIndex = 0; startIndex < length; startIndex += CHARACTER_LIMIT) {
                String substr = Utils.getInstance().mySubString(textMessage, startIndex, CHARACTER_LIMIT);
                MessageModel messageModel = buildTextMessage(substr);

                messageEntities.add(ChatManager.getInstance().convertToEntity(messageModel));

                sendMessage(messageModel);
            }
            // Insert list to database
            DataManager.getInstance().insertToDatabase(messageEntities);
        } else {
            MessageModel messageModel = buildTextMessage(textMessage);

            // Insert new message to database
            DataManager.getInstance().insertToDatabase(ChatManager.getInstance().convertToEntity(messageModel));

            sendMessage(messageModel);
        }
        // Run queue after list is updated
        //runMessageQueue();
    }

    private MessageModel buildTextMessage(String message) {
        // Create new MessageModel based on text
        MessageModel messageModel = MessageModel.Builder(
                message,
                activeRoom,
                DefaultConstant.MessageType.TYPE_TEXT,
                System.currentTimeMillis(),
                activeUser);

        // Add encrypted message to queue
        //try {
        //    JSONObject messageObject = new JSONObject();
        //    messageObject.put(MESSAGE, MessageModel.BuilderEncrypt(messageModel));
        //    messageObject.put(DefaultConstant.MessageQueue.NUM_OF_ATTEMPT, 0);
        //    messageQueue.put(messageModel.getLocalID(), messageObject);
        //} catch (GeneralSecurityException | JSONException e) {
        //    e.printStackTrace();
        //}

        // Call listener
        if (null != chatListeners && !chatListeners.isEmpty()) {
            for (HomingPigeonChatListener chatListener : chatListeners)
                chatListener.onSendTextMessage(messageModel);
        }

        return messageModel;
    }

    /**
     * save text to draft
     */
    public void saveMessageToDraft(String message) {
        messageDrafts.put(activeRoom, message);
    }

    public String getMessageFromDraft() {
        return messageDrafts.get(activeRoom);
    }

    /**
     * send pending messages from queue
     */
//    public void runMessageQueue() {
//        if (!messageQueue.isEmpty()) {
//            Map.Entry<String, JSONObject> message = messageQueue.entrySet().iterator().next();
//            EmitModel<MessageModel> emitModel = null;
//            Log.e("KRIM", "runMessageQueue: ");
//            try {
//                emitModel = new EmitModel<>(kSocketNewMessage, (MessageModel) message.getValue().get(MESSAGE));
//                message.getValue().put(NUM_OF_ATTEMPT, message.getValue().getInt(NUM_OF_ATTEMPT) + 1);
//                if (message.getValue().getInt(NUM_OF_ATTEMPT) < 2)
//                    sendMessage(Utils.getInstance().toJsonString(emitModel));
//                else {
//                    MessageModel tempMessage = MessageModel.BuilderDecrypt(emitModel.getData());
//                    tempMessage.setIsFailedSend(1);
//                    tempMessage.setIsSending(0);
//                    DataManager.getInstance().updatePendingStatus(tempMessage.getLocalID());
//                    removeFromQueue(tempMessage.getLocalID());
//                    if (null != chatListeners && !chatListeners.isEmpty()) {
//                        for (HomingPigeonChatListener chatListener : chatListeners)
//                            chatListener.onSendFailed(tempMessage);
//                    }
//                    Log.e("KRIM", "runMessageQueue: "+ tempMessage);
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//                Log.e("KRIM", "runMessageQueue: ",e );
//            }
//        }
//    }

    /**
     * remove delivered messages from queue
     */
//    private void removeFromQueue(String localID) {
//        messageQueue.remove(localID);
//    }

    /**
     * sending emit to server
     */
    private void sendMessage(MessageModel messageModel) {
        EmitModel<MessageModel> emitModel = null;
        try {
            emitModel = new EmitModel<>(kSocketNewMessage, MessageModel.BuilderEncrypt(messageModel));
            ConnectionManager.getInstance().send(Utils.getInstance().toJsonString(emitModel));
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
    }
}
