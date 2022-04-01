package io.taptalk.TapTalk.Model.ResponseModel;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TapGetUnreadRoomIdsResponse{

	@JsonProperty("unreadRoomIDs")
	private List<String> unreadRoomIDs;

	public void setUnreadRoomIDs(List<String> unreadRoomIDs){
		this.unreadRoomIDs = unreadRoomIDs;
	}

	public List<String> getUnreadRoomIDs(){
		return unreadRoomIDs;
	}
}