package com.mrebhan.sample.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.mrebhan.paprika.NonNull;
import com.mrebhan.paprika.PrimaryKey;
import com.mrebhan.paprika.Table;
import com.mrebhan.paprika.Unique;

@Table(version = 2)
public class Protein implements Parcelable {
    
    @PrimaryKey
    long id;
    
    @NonNull
    @Unique
    String name;
    
    boolean isVegetarian;

    boolean isPescetarian;

    int caloriesPerHundredGrams;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeByte(isVegetarian ? (byte) 1 : (byte) 0);
        dest.writeByte(isPescetarian ? (byte) 1 : (byte) 0);
        dest.writeInt(this.caloriesPerHundredGrams);
    }

    public Protein() {
    }

    protected Protein(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.isVegetarian = in.readByte() != 0;
        this.isPescetarian = in.readByte() != 0;
        this.caloriesPerHundredGrams = in.readInt();
    }

    public static final Creator<Protein> CREATOR = new Creator<Protein>() {
        public Protein createFromParcel(Parcel source) {
            return new Protein(source);
        }

        public Protein[] newArray(int size) {
            return new Protein[size];
        }
    };
}
