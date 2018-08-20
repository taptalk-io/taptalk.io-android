package com.moselo.HomingPigeon.Data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "Message_Table")
public class MessageEntity {

    @PrimaryKey()
    @NonNull
    @ColumnInfo(name = "MessageID", index = true)
    private String id;

    @ColumnInfo(name = "Room")
    private String room;

    @ColumnInfo(name = "MessageType")
    private int type;

    @ColumnInfo(name = "Message")
    private String message;

    @ColumnInfo(name = "Created")
    private long created;

    @ColumnInfo(name = "User")
    private String user;

    public MessageEntity(String id, String room, int type, String message, long created, String user) {
        this.id = id;
        this.room = room;
        this.type = type;
        this.message = message;
        this.created = created;
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
