package com.mrebhan.sample.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.mrebhan.paprika.Column;
import com.mrebhan.paprika.Table;

@Table
public class SpiceScientificData implements Parcelable {

    public SpiceScientificData() {
    }

    protected SpiceScientificData(Parcel in) {
        this.genus = in.readString();
        this.species = in.readString();
        this.kCalPerHundredGrams = in.readInt();
    }

    String genus;

    String species;

    @Column(version = 3)
    int kCalPerHundredGrams;

    public String getGenus() {
        return genus;
    }

    public void setGenus(String genus) {
        this.genus = genus;
    }

    public String getSpecies() {
        return species;
    }

    public void setSpecies(String species) {
        this.species = species;
    }

    public int getkCalPerHundredGrams() {
        return kCalPerHundredGrams;
    }

    public void setkCalPerHundredGrams(int kCalPerHundredGrams) {
        this.kCalPerHundredGrams = kCalPerHundredGrams;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.genus);
        dest.writeString(this.species);
        dest.writeInt(this.kCalPerHundredGrams);
    }

    public static final Parcelable.Creator<SpiceScientificData> CREATOR = new Parcelable.Creator<SpiceScientificData>() {
        public SpiceScientificData createFromParcel(Parcel source) {
            return new SpiceScientificData(source);
        }

        public SpiceScientificData[] newArray(int size) {
            return new SpiceScientificData[size];
        }
    };
}
