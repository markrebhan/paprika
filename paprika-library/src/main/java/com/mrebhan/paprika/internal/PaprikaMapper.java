package com.mrebhan.paprika.internal;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public interface PaprikaMapper<T> {
    void setupModel(T copy);
    int setupModel(Cursor cursor, int index);
    ContentValues getContentValues();
    ContentValuesTree getContentValuesTree();
    ArrayList<String> getExternalMappings();
}
