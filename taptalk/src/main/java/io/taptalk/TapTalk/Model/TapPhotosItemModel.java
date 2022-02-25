package io.taptalk.TapTalk.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TapPhotosItemModel {

	@JsonProperty("fullsizeImageURL")
	private String fullsizeImageURL;

	@JsonProperty("thumbnailImageURL")
	private String thumbnailImageURL;

	@JsonProperty("createdTime")
	private long createdTime;

	@JsonProperty("id")
	private int id;

	public void setFullsizeImageURL(String fullsizeImageURL){
		this.fullsizeImageURL = fullsizeImageURL;
	}

	public String getFullsizeImageURL(){
		return fullsizeImageURL;
	}

	public void setThumbnailImageURL(String thumbnailImageURL){
		this.thumbnailImageURL = thumbnailImageURL;
	}

	public String getThumbnailImageURL(){
		return thumbnailImageURL;
	}

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