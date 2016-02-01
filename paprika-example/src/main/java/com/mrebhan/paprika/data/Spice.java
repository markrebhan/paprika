package com.mrebhan.paprika.data;

import android.support.annotation.IntDef;

import com.mrebhan.NonNull;
import com.mrebhan.PrimaryKey;
import com.mrebhan.Table;
import com.mrebhan.Unique;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Table(version = 1)
public class Spice {

    private static final int SWEET = 0;
    private static final int SOUR = 1;
    private static final int BITTER = 2;
    private static final int SPICY = 3;
    private static final int SAVORY = 4;

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
}
