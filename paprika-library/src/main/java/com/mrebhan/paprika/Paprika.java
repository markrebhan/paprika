package com.mrebhan.paprika;

import android.content.Context;

public class Paprika {

    private PaprikaDataHelper dataHelper;

    public Paprika() {
    }

    //TODO pass in name and version
    public void init(Context context) {
        dataHelper = new PaprikaDataHelper(context, "paprika", 1);
    }
}
