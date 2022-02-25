package io.taptalk.TapTalk.Model;

import androidx.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonProperty;

public class TapPhotosItemModel {

	@Nullable
	@JsonProperty("fullsizeImageURL")
	private String fullsizeImageURL;

	@Nullable
	@JsonProperty("thumbnailImageURL")
	private String thumbnailImageURL;

	@Nullable
	@JsonProperty("createdTime")
	private Long createdTime;

	@JsonProperty("id")
	private int id;

	public void setFullsizeImageURL(@Nullable String fullsizeImageURL){
		this.fullsizeImageURL = fullsizeImageURL;
	}

	@Nullable
	public String getFullsizeImageURL(){
		return fullsizeImageURL;
	}

	public void setThumbnailImageURL(@Nullable String thumbnailImageURL){
		this.thumbnailImageURL = thumbnailImageURL;
	}

	@Nullable
	public String getThumbnailImageURL(){
		return thumbnailImageURL;
	}

	public void setCreatedTime(@Nullable Long createdTime){
		this.createdTime = createdTime;
	}

	@Nullable
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