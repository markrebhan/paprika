package com.mrebhan.paprika.internal;

import android.content.ContentValues;

import java.util.List;

public class ContentValuesTree {

    private final ContentValuesWrapper rootContentValuesWrapper;

    private ContentValuesWrapper currentWrapper;

    public ContentValuesTree(ContentValuesWrapper rootContentValuesWrapper) {
        this.rootContentValuesWrapper = rootContentValuesWrapper;
        findNextWrapper(rootContentValuesWrapper);
    }

    private void findNextWrapper(ContentValuesWrapper contentValuesWrapper) {
        currentWrapper = contentValuesWrapper;

        if (!currentWrapper.getContentValueNodes().isEmpty()) {
            for (ContentValuesWrapper wrapper : currentWrapper.getContentValueNodes()) {
                if (!wrapper.isConsumed()) {
                    findNextWrapper(wrapper);
                }
            }
        }
    }

    public boolean hasNext() {
        return !currentWrapper.isConsumed();
    }

    public ContentValuesWrapper next() {
        findNextWrapper(rootContentValuesWrapper);

        if (currentWrapper.isConsumed()) {
            return null;
        } else {
            currentWrapper.consume();
            return currentWrapper;
        }
    }

    public static class Builder {

        private ContentValuesWrapper rootContentValuesWrapper;

        public ContentValuesWrapper setRootNode(ContentValues contentValues, String table, List<String> externalMappings) {
            rootContentValuesWrapper = new ContentValuesWrapper(null, contentValues, table, externalMappings);
            return rootContentValuesWrapper;
        }

        public ContentValuesWrapper addChild(ContentValuesWrapper parent, ContentValues contentValues, String table, List<String> externalMappings) {
            ContentValuesWrapper contentValuesWrapper = new ContentValuesWrapper(parent, contentValues, table, externalMappings);
            parent.addNode(contentValuesWrapper);
            return contentValuesWrapper;
        }

        public ContentValuesTree build() {
            return new ContentValuesTree(rootContentValuesWrapper);
        }

    }
}
