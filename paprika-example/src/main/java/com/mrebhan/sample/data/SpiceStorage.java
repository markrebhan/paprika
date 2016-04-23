package com.mrebhan.sample.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.mrebhan.paprika.ForeignObject;
import com.mrebhan.paprika.PrimaryKey;
import com.mrebhan.paprika.Table;

@Table(version = 6)
public class SpiceStorage implements Parcelable {

    @PrimaryKey
    long id;

    String name;

    int storageType;

    boolean coolAndDark;

    @ForeignObject(version = 7)
    public SpiceContainer spiceContainer;

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

    public SpiceContainer getSpiceContainer() {
        return spiceContainer;
    }

    public void setSpiceContainer(SpiceContainer spiceContainer) {
        this.spiceContainer = spiceContainer;
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
