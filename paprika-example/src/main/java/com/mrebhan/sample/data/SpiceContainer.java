package com.mrebhan.sample.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.mrebhan.paprika.Table;

@Table(version = 7)
public class SpiceContainer implements Parcelable {

    public SpiceContainer() {
    }

    float length;
    float width;
    float height;
    boolean isRound;

    public float getLength() {
        return length;
    }

    public void setLength(float length) {
        this.length = length;
    }

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public boolean isRound() {
        return isRound;
    }

    public void setIsRound(boolean isRound) {
        this.isRound = isRound;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(this.length);
        dest.writeFloat(this.width);
        dest.writeFloat(this.height);
        dest.writeByte(isRound ? (byte) 1 : (byte) 0);
    }

    protected SpiceContainer(Parcel in) {
        this.length = in.readFloat();
        this.width = in.readFloat();
        this.height = in.readFloat();
        this.isRound = in.readByte() != 0;
    }

    public static final Parcelable.Creator<SpiceContainer> CREATOR = new Parcelable.Creator<SpiceContainer>() {
        public SpiceContainer createFromParcel(Parcel source) {
            return new SpiceContainer(source);
        }

        public SpiceContainer[] newArray(int size) {
            return new SpiceContainer[size];
        }
    };
}
