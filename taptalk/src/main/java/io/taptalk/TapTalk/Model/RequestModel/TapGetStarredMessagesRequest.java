package io.taptalk.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TapGetStarredMessagesRequest{

	@JsonProperty("pageNumber")
	private int pageNumber;

	@JsonProperty("pageSize")
	private int pageSize;

	@JsonProperty("roomID")
	private String roomID;

	public void setPageNumber(int pageNumber){
		this.pageNumber = pageNumber;
	}

	public int getPageNumber(){
		return pageNumber;
	}

	public void setPageSize(int pageSize){
		this.pageSize = pageSize;
	}

	public int getPageSize(){
		return pageSize;
	}

	public void setRoomID(String roomID){
		this.roomID = roomID;
	}

	public String getRoomID(){
		return roomID;
	}
}