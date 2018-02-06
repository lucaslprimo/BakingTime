package br.com.lucaslprimo.bakingtime.data;

import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Lucas Primo on 01-Feb-18.
 */

public class Step implements Parcelable{

    private static final String KEY_ID = "id";
    private static final String KEY_SHORT_DESCRIPTION = "shortDescription";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_VIDEO_URL = "videoURL";
    private static final String KEY_THUMBNAIL_URL = "thumbnailURL";

    private int id;
    private String shortDescription;
    private String description;
    private String videoUrl;
    private String thumbnailUrl;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public static Step fromJson(JSONObject jsonObject)
    {
        Step step = null;

        try {
            step = new Step();

            step.setId(jsonObject.getInt(KEY_ID));
            step.setDescription(jsonObject.getString(KEY_DESCRIPTION));
            step.setShortDescription(jsonObject.getString(KEY_SHORT_DESCRIPTION));
            step.setVideoUrl(jsonObject.getString(KEY_VIDEO_URL));
            step.setThumbnailUrl(jsonObject.getString(KEY_THUMBNAIL_URL));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return step;
    }

    //Parcelable implementation
    private Step(Parcel in)
    {
        this.id = in.readInt();
        this.description = in.readString();
        this.shortDescription = in.readString();
        this.videoUrl = in.readString();
        this.thumbnailUrl = in.readString();
    }

    public Step()
    {

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(id);
        parcel.writeString(description);
        parcel.writeString(shortDescription);
        parcel.writeString(videoUrl);
        parcel.writeString(thumbnailUrl);
    }

    public static final Parcelable.Creator<Step> CREATOR = new Parcelable.Creator<Step>()
    {
        @Override
        public Step createFromParcel(Parcel parcel) {
            return new Step(parcel);
        }

        @Override
        public Step[] newArray(int size) {
            return new Step[size];
        }
    };
}
