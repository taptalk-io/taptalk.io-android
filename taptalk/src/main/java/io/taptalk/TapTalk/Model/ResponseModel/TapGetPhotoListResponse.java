package io.taptalk.TapTalk.Model.ResponseModel;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.taptalk.TapTalk.Model.TapPhotosItemModel;

public class TapGetPhotoListResponse{

	@JsonProperty("photos")
	private List<TapPhotosItemModel> photos;

	public void setPhotos(List<TapPhotosItemModel> photos){
		this.photos = photos;
	}

	public List<TapPhotosItemModel> getPhotos(){
		return photos;
	}
}