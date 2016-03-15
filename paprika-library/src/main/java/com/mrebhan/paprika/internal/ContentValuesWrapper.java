package com.mrebhan.paprika.internal;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.List;

public class ContentValuesWrapper {

    private final ContentValues contentValues;
    private final String tableName;
    private final List<ContentValuesWrapper> contentValueNodes;
    private boolean isConsumed;

    public ContentValuesWrapper(ContentValues contentValues, String tableName) {
        this.contentValues = contentValues;
        this.tableName = tableName;
        this.contentValueNodes = new ArrayList<>();
    }

    public void addNode(ContentValuesWrapper contentValuesWrapper) {
        contentValueNodes.add(contentValuesWrapper);
    }

    public List<ContentValuesWrapper> getContentValueNodes() {
        return contentValueNodes;
    }

    public ContentValues getContentValues() {
        isConsumed = true;
        return contentValues;
    }

    public String getTableName() {
        return tableName;
    }

    public void consume() {
        isConsumed = true;
    }

    public boolean isConsumed() {
        return isConsumed;
    }
}
