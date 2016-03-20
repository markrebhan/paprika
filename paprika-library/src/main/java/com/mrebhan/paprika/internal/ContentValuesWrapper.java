package com.mrebhan.paprika.internal;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ContentValuesWrapper {

    private final ContentValues contentValues;
    private final String tableName;
    private final List<ContentValuesWrapper> contentValueNodes;
    private final ContentValuesWrapper parentNode;
    private final Iterator<String> externalMappings;
    private boolean isConsumed;

    public ContentValuesWrapper(ContentValuesWrapper wrapper, ContentValues contentValues, String tableName, List<String> externalMappings) {
        this.parentNode = wrapper;
        this.contentValues = contentValues;
        this.tableName = tableName;
        this.externalMappings = externalMappings.iterator();
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

    public ContentValuesWrapper getParentNode() {
        return parentNode;
    }

    public void addExternalMappingIndex(long row) {
        if (externalMappings.hasNext()) {
            contentValues.put(externalMappings.next(), row);
        }
    }
}
