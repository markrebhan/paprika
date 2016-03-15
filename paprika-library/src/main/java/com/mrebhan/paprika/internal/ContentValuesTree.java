package com.mrebhan.paprika.internal;

import android.content.ContentValues;

public class ContentValuesTree {

    private final ContentValuesWrapper rootContentValuesWrapper;

    private ContentValuesWrapper currentWrapper;

    public ContentValuesTree(ContentValuesWrapper rootContentValuesWrapper) {
        this.rootContentValuesWrapper = rootContentValuesWrapper;
        findNextWrapper(rootContentValuesWrapper);
    }

    public boolean hasNext() {
        return !currentWrapper.isConsumed();
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

    public ContentValuesWrapper next() {
        findNextWrapper(rootContentValuesWrapper);
        currentWrapper.consume();;
        return currentWrapper;
    }

    public static class Builder {

        private ContentValuesWrapper rootContentValuesWrapper;

        public ContentValuesWrapper setRootNode(ContentValues contentValues, String table) {
            rootContentValuesWrapper = new ContentValuesWrapper(contentValues, table);
            return rootContentValuesWrapper;
        }

        public ContentValuesWrapper addChild(ContentValuesWrapper parent, ContentValues contentValues, String table) {
            ContentValuesWrapper contentValuesWrapper = new ContentValuesWrapper(contentValues, table);
            parent.addNode(new ContentValuesWrapper(contentValues, table));
            return contentValuesWrapper;
        }

        public ContentValuesTree build() {
            return new ContentValuesTree(rootContentValuesWrapper);
        }

    }
}
