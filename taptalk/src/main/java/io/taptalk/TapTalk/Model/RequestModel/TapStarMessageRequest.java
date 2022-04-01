package io.taptalk.TapTalk.Model.RequestModel;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TapStarMessageRequest{

	@JsonProperty("messageIDs")
	private List<String> messageIDs;

	@JsonProperty("roomID")
	private String roomID;

	public void setMessageIDs(List<String> messageIDs){
		this.messageIDs = messageIDs;
	}

	public List<String> getMessageIDs(){
		return messageIDs;
	}

	public void setRoomID(String roomID){
		this.roomID = roomID;
	}

	public String getRoomID(){
		return roomID;
	}
}