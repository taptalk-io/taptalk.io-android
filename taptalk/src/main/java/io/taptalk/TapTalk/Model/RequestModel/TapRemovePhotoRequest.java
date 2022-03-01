package io.taptalk.TapTalk.Model.RequestModel;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TapRemovePhotoRequest{

	@JsonProperty("createdTime")
	private Long createdTime;

	@JsonProperty("id")
	private int id;

	public TapRemovePhotoRequest(int id, Long createdTime) {
		this.id = id;
		this.createdTime = createdTime;
	}

	public void setCreatedTime(Long createdTime){
		this.createdTime = createdTime;
	}

	public Long getCreatedTime(){
		return createdTime;
	}

	public void setId(int id){
		this.id = id;
	}

	public int getId(){
		return id;
	}
}