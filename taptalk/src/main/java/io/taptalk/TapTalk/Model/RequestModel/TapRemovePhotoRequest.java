package io.taptalk.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TapRemovePhotoRequest{

	@JsonProperty("createdTime")
	private long createdTime;

	@JsonProperty("id")
	private int id;

	public void setCreatedTime(long createdTime){
		this.createdTime = createdTime;
	}

	public long getCreatedTime(){
		return createdTime;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}
}