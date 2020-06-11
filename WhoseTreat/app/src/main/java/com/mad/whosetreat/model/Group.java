package com.mad.whosetreat.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Group models the group object containing list of person representing the members.
 * Parcelable object which can also be mapped to Json interchangeably.
 */
public class Group implements Parcelable {

    public static final Creator<Group> CREATOR = new Creator<Group>() {
        @Override
        public Group createFromParcel(Parcel source) {
            return new Group(source);
        }

        @Override
        public Group[] newArray(int size) {
            return new Group[size];
        }
    };
    // fields
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("person")
    @Expose
    private List<Person> person = null;
    @SerializedName("count")
    @Expose
    private Integer count = 0;
    @SerializedName("places")
    @Expose
    private List<PlaceDto.Place> places = new ArrayList<>();

    private boolean isSelected = false;
    private boolean isRandom = false;
    private String imagePath;

    public Group() {
    }

    protected Group(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.person = in.createTypedArrayList(Person.CREATOR);
        this.count = (Integer) in.readValue(Integer.class.getClassLoader());
        this.places = new ArrayList<PlaceDto.Place>();
        in.readList(this.places, PlaceDto.Place.class.getClassLoader());
        this.isSelected = in.readByte() != 0;
    }

    // getters and setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Person> getPerson() {
        return person;
    }

    public void setPerson(List<Person> person) {
        this.person = person;
    }

    public Integer getCount() {
        return count;
    }

    public void addCount() {
        ++this.count;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public List<PlaceDto.Place> getPlaces() {
        return places;
    }

    public void setPlaces(List<PlaceDto.Place> places) {
        this.places = places;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeTypedList(this.person);
        dest.writeValue(this.count);
        dest.writeList(this.places);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }

    public boolean isRandom() {
        return isRandom;
    }

    public void setRandom(boolean random) {
        isRandom = random;
    }

    public String getImage() {
        return imagePath;
    }

    public void setImage(String imagePath) {
        this.imagePath = imagePath;
    }
}
