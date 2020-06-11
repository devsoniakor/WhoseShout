package com.mad.whosetreat.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * modelling an individual person object.
 * Parcelable and mapped to Gson and Json
 */
public class Person implements Parcelable {

    public static final Creator<Person> CREATOR = new Creator<Person>() {
        @Override
        public Person createFromParcel(Parcel source) {
            return new Person(source);
        }

        @Override
        public Person[] newArray(int size) {
            return new Person[size];
        }
    };
    // fields
    @SerializedName("id")
    @Expose
    private Integer id = 0;
    @SerializedName("name")
    @Expose
    private String name = "";
    @SerializedName("phone")
    @Expose
    private String phone = "";
    @SerializedName("count")
    @Expose
    private Integer count = 0;
    @SerializedName("paid")
    @Expose
    private Double paid = 0.0;
    private int color = 0;
    private boolean isSelected = false;

    public Person() {
    }

    protected Person(Parcel in) {
        this.id = (Integer) in.readValue(Integer.class.getClassLoader());
        this.name = in.readString();
        this.phone = in.readString();
        this.count = (Integer) in.readValue(Integer.class.getClassLoader());
        this.paid = (Double) in.readValue(Double.class.getClassLoader());
        this.color = in.readInt();
        this.isSelected = in.readByte() != 0;
    }

    /**
     * getters and setters of the fields
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Double getPaid() {
        return paid;
    }

    public void setPaid(Double paid) {
        this.paid = paid;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        return name != null ? name.equalsIgnoreCase(person.name) : person.name == null;

    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(this.id);
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeValue(this.count);
        dest.writeValue(this.paid);
        dest.writeInt(this.color);
        dest.writeByte(this.isSelected ? (byte) 1 : (byte) 0);
    }
}