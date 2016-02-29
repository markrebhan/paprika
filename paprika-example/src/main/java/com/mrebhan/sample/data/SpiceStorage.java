package com.mrebhan.sample.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.mrebhan.paprika.PrimaryKey;
import com.mrebhan.paprika.Table;

@Table(version = 6)
public class SpiceStorage implements Parcelable {

    //TODO make this so this doesn't has to be added
    @PrimaryKey
    long id;

    String name;

    int storageType;

    boolean coolAndDark;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStorageType() {
        return storageType;
    }

    public void setStorageType(int storageType) {
        this.storageType = storageType;
    }

    public boolean isCoolAndDark() {
        return coolAndDark;
    }

    public void setCoolAndDark(boolean coolAndDark) {
        this.coolAndDark = coolAndDark;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.storageType);
        dest.writeByte(coolAndDark ? (byte) 1 : (byte) 0);
    }

    public SpiceStorage() {
    }

    protected SpiceStorage(Parcel in) {
        this.name = in.readString();
        this.storageType = in.readInt();
        this.coolAndDark = in.readByte() != 0;
    }

    public static final Parcelable.Creator<SpiceStorage> CREATOR = new Parcelable.Creator<SpiceStorage>() {
        public SpiceStorage createFromParcel(Parcel source) {
            return new SpiceStorage(source);
        }

        public SpiceStorage[] newArray(int size) {
            return new SpiceStorage[size];
        }
    };
}
