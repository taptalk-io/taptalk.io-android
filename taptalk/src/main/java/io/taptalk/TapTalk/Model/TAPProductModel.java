package io.taptalk.TapTalk.Model;

import android.os.Parcel;
import android.os.Parcelable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_NULL)

public class TAPProductModel implements Parcelable {
    @JsonProperty("buttonOption1Color") private String buttonOption1Color;
    @JsonProperty("buttonOption1Text") private String buttonOption1Text;
    @JsonProperty("buttonOption2Color") private String buttonOption2Color;
    @JsonProperty("buttonOption2Text") private String buttonOption2Text;
    @JsonProperty("currency") private String currency;
    @JsonProperty("description") private String description;
    @JsonProperty("id") private String id;
    @JsonProperty("imageURL") private String imageURL;
    @JsonProperty("name") private String name;
    @JsonProperty("price") private String price;
    @JsonProperty("rating") private String rating;

    public TAPProductModel(String buttonOption1Color, String buttonOption1Text
            , String buttonOption2Color, String buttonOption2Text, String currency
            , String description, String id, String imageURL, String name
            , String price, String rating) {
        this.buttonOption1Color = buttonOption1Color;
        this.buttonOption1Text = buttonOption1Text;
        this.buttonOption2Color = buttonOption2Color;
        this.buttonOption2Text = buttonOption2Text;
        this.currency = currency;
        this.description = description;
        this.id = id;
        this.imageURL = imageURL;
        this.name = name;
        this.price = price;
        this.rating = rating;
    }

    public TAPProductModel() {
    }

    public String getButtonOption1Color() {
        return buttonOption1Color;
    }

    public void setButtonOption1Color(String buttonOption1Color) {
        this.buttonOption1Color = buttonOption1Color;
    }

    public String getButtonOption1Text() {
        return buttonOption1Text;
    }

    public void setButtonOption1Text(String buttonOption1Text) {
        this.buttonOption1Text = buttonOption1Text;
    }

    public String getButtonOption2Color() {
        return buttonOption2Color;
    }

    public void setButtonOption2Color(String buttonOption2Color) {
        this.buttonOption2Color = buttonOption2Color;
    }

    public String getButtonOption2Text() {
        return buttonOption2Text;
    }

    public void setButtonOption2Text(String buttonOption2Text) {
        this.buttonOption2Text = buttonOption2Text;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.buttonOption1Color);
        dest.writeString(this.buttonOption1Text);
        dest.writeString(this.buttonOption2Color);
        dest.writeString(this.buttonOption2Text);
        dest.writeString(this.currency);
        dest.writeString(this.description);
        dest.writeString(this.id);
        dest.writeString(this.imageURL);
        dest.writeString(this.name);
        dest.writeString(this.price);
        dest.writeString(this.rating);
    }

    protected TAPProductModel(Parcel in) {
        this.buttonOption1Color = in.readString();
        this.buttonOption1Text = in.readString();
        this.buttonOption2Color = in.readString();
        this.buttonOption2Text = in.readString();
        this.currency = in.readString();
        this.description = in.readString();
        this.id = in.readString();
        this.imageURL = in.readString();
        this.name = in.readString();
        this.price = in.readString();
        this.rating = in.readString();
    }

    public static final Creator<TAPProductModel> CREATOR = new Creator<TAPProductModel>() {
        @Override
        public TAPProductModel createFromParcel(Parcel source) {
            return new TAPProductModel(source);
        }

        @Override
        public TAPProductModel[] newArray(int size) {
            return new TAPProductModel[size];
        }
    };
}
