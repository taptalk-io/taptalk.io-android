package com.moselo.HomingPigeon.Data;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Index;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "Message_Table")
public class MessageEntity {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "MessageID", index = true)
    private int id;

    @ColumnInfo(name = "UserName")
    private String userName;

    @ColumnInfo(name = "MessageType")
    private int type;

    @ColumnInfo(name = "Message")
    private String message;

    public MessageEntity(String userName, int type, String message) {
        this.userName = userName;
        this.type = type;
        this.message = message;
    }

    public MessageEntity() {
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public int getType() {
        return type;
    }

    public String getMessage() {
        return message;
    }
}
