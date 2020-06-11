package com.mad.whosetreat.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * modelling group of groups.
 * will be altered to Gson and Json interchangeably
 */
public class Groups implements Parcelable {

    /**
     * implemented for being parcelable object
     */
    public final static Parcelable.Creator<Groups> CREATOR = new Creator<Groups>() {

        @SuppressWarnings({
                "unchecked"
        })
        public Groups createFromParcel(Parcel in) {
            Groups instance = new Groups();
            in.readList(instance.groups, (Group.class.getClassLoader()));
            return instance;
        }

        public Groups[] newArray(int size) {
            return (new Groups[size]);
        }

    };

    // fields
    @SerializedName("groups")
    @Expose
    private List<Group> groups = null;
    @SerializedName("count")
    @Expose
    private Integer count = 0;


    /**
     * getters and setters of fields
     */
    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(groups);
    }

    public int describeContents() {
        return 0;
    }

    public Integer getCount() {
        return count;
    }

    public void addCount() {
        ++this.count;
    }


}
