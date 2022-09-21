package io.taptalk.TapTalk.Model.RequestModel;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

public class TapRoomIdsRequest{

	@JsonProperty("roomIDs")
	private List<String> roomIDs;

	@JsonProperty("expiredAt")
	private long expiredAt;

	public void setRoomIDs(List<String> roomIDs){
		this.roomIDs = roomIDs;
	}

	public List<String> getRoomIDs(){
		return roomIDs;
	}

	public long getExpiredAt() {
		return expiredAt;
	}

	public void setExpiredAt(long expiredAt) {
		this.expiredAt = expiredAt;
	}
}