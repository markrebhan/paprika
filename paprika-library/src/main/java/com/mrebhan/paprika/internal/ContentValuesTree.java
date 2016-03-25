package com.mrebhan.paprika.internal;

import android.content.ContentValues;

import java.util.List;

public class ContentValuesTree {

    private final ContentValuesWrapper rootContentValuesWrapper;

    private ContentValuesWrapper currentWrapper;

    public ContentValuesTree(ContentValuesWrapper rootContentValuesWrapper) {
        this.rootContentValuesWrapper = rootContentValuesWrapper;
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
        findNextWrapper(rootContentValuesWrapper);
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
            rootContentValuesWrapper = new ContentValuesWrapper(contentValues, table, externalMappings);
            return rootContentValuesWrapper;
        }

        public void addChild(ContentValuesWrapper parent, ContentValuesTree childTree) {
            childTree.rootContentValuesWrapper.setParentNode(parent);
            parent.addNode(childTree.rootContentValuesWrapper);
        }

        public ContentValuesTree build() {
            return new ContentValuesTree(rootContentValuesWrapper);
        }

    }
}
