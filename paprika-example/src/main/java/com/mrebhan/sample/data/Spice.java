package com.mrebhan.sample.data;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.IntDef;

import com.mrebhan.paprika.Column;
import com.mrebhan.paprika.NonNull;
import com.mrebhan.paprika.PrimaryKey;
import com.mrebhan.paprika.Table;
import com.mrebhan.paprika.Unique;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Table(version = 1)
public class Spice implements Parcelable {

    public static final int SWEET = 0;
    public static final int SOUR = 1;
    public static final int BITTER = 2;
    public static final int SPICY = 3;
    public static final int SAVORY = 4;

    @Retention(RUNTIME)
    @IntDef({SWEET, SOUR, BITTER, SPICY, SAVORY})
    public @interface Flavor{}

    @PrimaryKey
    long id;

    @NonNull
    @Unique
    String name;

    @Spice.Flavor
    int flavor;

    int tastiness;

    @Column(version = 3)
    String description;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getFlavor() {
        return flavor;
    }

    public void setFlavor(int flavor) {
        this.flavor = flavor;
    }

    public int getTastiness() {
        return tastiness;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTastiness(int tastiness) {
        this.tastiness = tastiness;
    }

    public String getFlavorString() {
        switch (flavor) {
            case SWEET:
                return "Sweet";
            case SOUR:
                return "Sour";
            case BITTER:
                return "Bitter";
            case SPICY:
                return "Spicy";
            case SAVORY:
                return "Savory";
        }

        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.flavor);
        dest.writeInt(this.tastiness);
    }

    public Spice() {
    }

    @SuppressWarnings("ResourceType")
    protected Spice(Parcel in) {
        this.name = in.readString();
        this.flavor = in.readInt();
        this.tastiness = in.readInt();
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
