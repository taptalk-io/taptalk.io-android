package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;
import java.util.List;

import io.taptalk.TapTalk.Data.Message.TAPMessageEntity;
import io.taptalk.TapTalk.Helper.TAPUtils;

import static io.taptalk.TapTalk.Const.TAPDefaultConstant.RoomType.TYPE_PERSONAL;

public class TAPRoomModel implements Parcelable {

    @JsonProperty("roomID")
    @JsonAlias("id")
    private String roomID;
    @JsonProperty("xcRoomID") private String xcRoomID;
    @JsonProperty("name") private String name;
    @JsonProperty("color") private String color;
    @JsonProperty("type") private int type;
    @JsonProperty("unreadCount") private int unreadCount;
    @JsonProperty("lockedTime") private long lockedTime;
    @JsonProperty("deleted") private long deleted;
    @JsonProperty("isLocked") private boolean isLocked;
    @JsonProperty("isDeleted") private boolean isDeleted;
    @Nullable
    @JsonProperty("imageURL")
    private TAPImageURL imageURL;
    @Nullable
    private List<TAPUserModel> participants;
    @Nullable
    @JsonIgnore
    private Integer numOfParticipants;
    @Nullable
    @JsonProperty("admins")
    private List<String> admins;
    @JsonIgnore
    private boolean isMuted;

    public TAPRoomModel(String roomID, String name, int type, TAPImageURL imageURL, String color) {
        this.roomID = null != roomID ? roomID : "";
        this.name = null != name ? name : "";
        this.type = type;
        this.imageURL = null != imageURL ? imageURL : new TAPImageURL();
        this.color = null != color ? color : "";
    }

    public TAPRoomModel(String roomID, String name, int type, TAPImageURL imageURL, String color, int unreadCount) {
        this.roomID = null != roomID ? roomID : "";
        this.name = null != name ? name : "";
        this.type = type;
        this.imageURL = null != imageURL ? imageURL : new TAPImageURL();
        this.color = null != color ? color : "";
        this.unreadCount = unreadCount;
    }

    public TAPRoomModel(TAPMessageEntity messageEntity) {
        this.roomID = messageEntity.getRoomID();
        this.xcRoomID = messageEntity.getXcRoomID();
        this.name = messageEntity.getRoomName();
        this.type = null == messageEntity.getRoomType() ? TYPE_PERSONAL : messageEntity.getRoomType();
        this.imageURL = TAPUtils.fromJSON(new TypeReference<TAPImageURL>() {
        }, messageEntity.getRoomImage());
        this.color = messageEntity.getRoomColor();
        this.isLocked = null == messageEntity.getRoomLocked() ? false : messageEntity.getRoomLocked();
        this.isDeleted = null == messageEntity.getRoomDeleted() ? false : messageEntity.getRoomDeleted();
        this.lockedTime = null == messageEntity.getRoomLockedTimestamp() ? 0L : messageEntity.getRoomLockedTimestamp();
        this.deleted = null == messageEntity.getRoomDeletedTimestamp() ? 0L : messageEntity.getRoomDeletedTimestamp();
    }

    public TAPRoomModel() {
    }

    public static TAPRoomModel Builder(String roomID, String roomName, int roomType, TAPImageURL roomImage, String roomColor) {
        return new TAPRoomModel(roomID, roomName, roomType, roomImage, roomColor);
    }

    public static TAPRoomModel Builder(String roomID, String roomName, int roomType, TAPImageURL roomImage, String roomColor, int unreadCount) {
        return new TAPRoomModel(roomID, roomName, roomType, roomImage, roomColor, unreadCount);
    }

    public static TAPRoomModel Builder(TAPMessageEntity messageEntity) {
        return new TAPRoomModel(messageEntity);
    }

    public static TAPRoomModel fromHashMap(HashMap<String, Object> hashMap) {
        try {
            return TAPUtils.convertObject(hashMap, new TypeReference<TAPRoomModel>() {
            });
        } catch (Exception e) {
            return null;
        }
    }

    public HashMap<String, Object> toHashMap() {
        return TAPUtils.toHashMap(this);
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }

    public String getXcRoomID() {
        return xcRoomID;
    }

    public void setXcRoomID(String xcRoomID) {
        this.xcRoomID = xcRoomID;
    }

    /**
     * @deprecated use {@link #getName()} instead.
     */
    @Deprecated
    @JsonIgnore
    public String getRoomName() {
        return name;
    }

    public String getName() {
        return name;
    }

    /**
     * @deprecated use {@link #setName(String)} instead.
     */
    @Deprecated
    @JsonIgnore
    public void setRoomName(String roomName) {
        this.name = roomName;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * @deprecated use {@link #getColor()} instead.
     */
    @Deprecated
    @JsonIgnore
    public String getRoomColor() {
        return color;
    }

    public String getColor() {
        return color;
    }

    /**
     * @deprecated use {@link #setColor(String)} instead.
     */
    @Deprecated
    @JsonIgnore
    public void setRoomColor(String roomColor) {
        this.color = roomColor;
    }

    public void setColor(String color) {
        this.color = color;
    }

    /**
     * @deprecated use {@link #getType()} instead.
     */
    @Deprecated
    @JsonIgnore
    public int getRoomType() {
        return type;
    }

    public int getType() {
        return type;
    }

    /**
     * @deprecated use {@link #setType(int)} instead.
     */
    @Deprecated
    @JsonIgnore
    public void setRoomType(int roomType) {
        this.type = roomType;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    /**
     * @deprecated use {@link #getLockedTime()} instead.
     */
    @Deprecated
    @JsonIgnore
    public long getLockedTimestamp() {
        return lockedTime;
    }

    public long getLockedTime() {
        return lockedTime;
    }

    /**
     * @deprecated use {@link #setLockedTime(long)} instead.
     */
    @Deprecated
    @JsonIgnore
    public void setLockedTimestamp(long lockedTimestamp) {
        this.lockedTime = lockedTimestamp;
    }

    public void setLockedTime(long lockedTime) {
        this.lockedTime = lockedTime;
    }

    /**
     * @deprecated use {@link #getDeleted()} instead.
     */
    @Deprecated
    @JsonIgnore
    public long getDeletedTimestamp() {
        return deleted;
    }

    public long getDeleted() {
        return deleted;
    }

    /**
     * @deprecated use {@link #setDeleted(long)} instead.
     */
    @Deprecated
    @JsonIgnore
    public void setDeletedTimestamp(long deletedTimestamp) {
        this.deleted = deletedTimestamp;
    }

    public void setDeleted(long deleted) {
        this.deleted = deleted;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public void setLocked(boolean locked) {
        isLocked = locked;
    }

    /**
     * @deprecated use {@link #isDeleted()} instead.
     */
    @Deprecated
    @JsonIgnore
    public boolean isRoomDeleted() {
        return isDeleted;
    }

    public boolean isDeleted() {
        return isDeleted;
    }

    /**
     * @deprecated use {@link #setIsDeleted(boolean)} instead.
     */
    @Deprecated
    @JsonIgnore
    public void setRoomDeleted(boolean roomDeleted) {
        isDeleted = roomDeleted;
    }

    public void setIsDeleted(boolean deleted) {
        isDeleted = deleted;
    }

    /**
     * @deprecated use {@link #getImageURL()} instead.
     */
    @Deprecated
    @JsonIgnore
    @Nullable
    public TAPImageURL getRoomImage() {
        return imageURL;
    }

    @Nullable
    public TAPImageURL getImageURL() {
        return imageURL;
    }

    /**
     * @deprecated use {@link #setImageURL(TAPImageURL)} instead.
     */
    @Deprecated
    @JsonIgnore
    public void setRoomImage(@Nullable TAPImageURL roomImage) {
        this.imageURL = roomImage;
    }

    public void setImageURL(@Nullable TAPImageURL imageURL) {
        this.imageURL = imageURL;
    }

    /**
     * @deprecated use {@link #getParticipants()} instead.
     */
    @Deprecated
    @JsonIgnore
    @Nullable
    public List<TAPUserModel> getGroupParticipants() {
        return participants;
    }

    @Nullable
    public List<TAPUserModel> getParticipants() {
        return participants;
    }

    /**
     * @deprecated use {@link #setParticipants(List)} instead.
     */
    @Deprecated
    @JsonIgnore
    public void setGroupParticipants(@Nullable List<TAPUserModel> groupParticipants) {
        setParticipants(groupParticipants);
    }

    public void setParticipants(@Nullable List<TAPUserModel> participants) {
        this.participants = participants;
        if (null != participants) {
            setNumOfParticipants(participants.size());
        } else {
            setNumOfParticipants(0);
        }
    }

    @JsonIgnore
    @Nullable
    public Integer getNumOfParticipants() {
        return numOfParticipants;
    }

    @JsonIgnore
    public void setNumOfParticipants(@Nullable Integer numOfParticipants) {
        this.numOfParticipants = numOfParticipants;
    }

    @Nullable
    public List<String> getAdmins() {
        return admins;
    }

    public void setAdmins(@Nullable List<String> admins) {
        this.admins = admins;
    }

    public boolean getIsMuted() {
        return isMuted;
    }

    public void setIsMuted(boolean muted) {
        isMuted = muted;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.roomID);
        dest.writeString(this.xcRoomID);
        dest.writeString(this.name);
        dest.writeString(this.color);
        dest.writeInt(this.type);
        dest.writeInt(this.unreadCount);
        dest.writeLong(this.lockedTime);
        dest.writeLong(this.deleted);
        dest.writeByte(this.isLocked ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isDeleted ? (byte) 1 : (byte) 0);
        dest.writeParcelable(this.imageURL, flags);
        dest.writeTypedList(this.participants);
        dest.writeValue(this.numOfParticipants);
        dest.writeStringList(this.admins);
        dest.writeByte(this.isMuted ? (byte) 1 : (byte) 0);
    }

    protected TAPRoomModel(Parcel in) {
        this.roomID = in.readString();
        this.xcRoomID = in.readString();
        this.name = in.readString();
        this.color = in.readString();
        this.type = in.readInt();
        this.unreadCount = in.readInt();
        this.lockedTime = in.readLong();
        this.deleted = in.readLong();
        this.isLocked = in.readByte() != 0;
        this.isDeleted = in.readByte() != 0;
        this.imageURL = in.readParcelable(TAPImageURL.class.getClassLoader());
        this.participants = in.createTypedArrayList(TAPUserModel.CREATOR);
        this.numOfParticipants = (Integer) in.readValue(Integer.class.getClassLoader());
        this.admins = in.createStringArrayList();
        this.isMuted = in.readByte() != 0;
    }

    public static final Creator<TAPRoomModel> CREATOR = new Creator<TAPRoomModel>() {
        @Override
        public TAPRoomModel createFromParcel(Parcel source) {
            return new TAPRoomModel(source);
        }

        @Override
        public TAPRoomModel[] newArray(int size) {
            return new TAPRoomModel[size];
        }
    };
}
