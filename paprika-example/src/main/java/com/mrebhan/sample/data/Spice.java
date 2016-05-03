package com.mrebhan.sample.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import com.mrebhan.paprika.Column;
import com.mrebhan.paprika.Default;
import com.mrebhan.paprika.ForeignObject;
import com.mrebhan.paprika.NonNull;
import com.mrebhan.paprika.Table;
import com.mrebhan.paprika.Unique;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Table(version = 1)
public class Spice implements Parcelable {

    public Spice() {
    }

    protected Spice(Parcel in) {
        this.name = in.readString();
        this.description = in.readString();
        this.scovilleValue = in.readInt();
        this.color = in.readString();
        this.spiceScientificData = in.readParcelable(SpiceScientificData.class.getClassLoader());
    }

    String name;

    @Column(version = 2)
    String description;

    int scovilleValue;

    String color;

    @ForeignObject(version = 2)
    SpiceScientificData spiceScientificData;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getScovilleValue() {
        return scovilleValue;
    }

    public void setScovilleValue(int scovilleValue) {
        this.scovilleValue = scovilleValue;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public SpiceScientificData getSpiceScientificData() {
        return spiceScientificData;
    }

    public void setSpiceScientificData(SpiceScientificData spiceScientificData) {
        this.spiceScientificData = spiceScientificData;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeInt(this.scovilleValue);
        dest.writeString(this.color);
        dest.writeParcelable(spiceScientificData, flags);
    }

    public static final Creator<Spice> CREATOR = new Creator<Spice>() {
        public Spice createFromParcel(Parcel source) {
            return new Spice(source);
        }

        public Spice[] newArray(int size) {
            return new Spice[size];
        }
    };
}
