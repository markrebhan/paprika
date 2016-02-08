package com.mrebhan.sample.data;

import android.support.annotation.IntDef;

import com.mrebhan.paprika.NonNull;
import com.mrebhan.paprika.PrimaryKey;
import com.mrebhan.paprika.Table;
import com.mrebhan.paprika.Unique;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Table(version = 1)
public class Spice {

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

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getFlavor() {
        return flavor;
    }

    public int getTastiness() {
        return tastiness;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setFlavor(int flavor) {
        this.flavor = flavor;
    }

    public void setTastiness(int tastiness) {
        this.tastiness = tastiness;
    }
}
