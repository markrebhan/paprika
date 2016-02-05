package com.mrebhan.paprika;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.mrebhan.paprika.internal.SqlScripts;

import static com.mrebhan.paprika.consts.Constants.PAPRIKA_PACKAGE;
import static com.mrebhan.paprika.consts.Constants.PAPRIKA_SQL_SCRIPTS_CLASS_NAME;

public class PaprikaDataHelper extends SQLiteOpenHelper {

    private SqlScripts sqlScripts;

    public PaprikaDataHelper(Context context, String name, int version) {
        super(context, name, null, version);
        init();
    }

    private void init() {
        try {

            Class<?> sqlScriptBinder = Class.forName(PAPRIKA_PACKAGE + "." + PAPRIKA_SQL_SCRIPTS_CLASS_NAME);
            sqlScripts = (SqlScripts) sqlScriptBinder.newInstance();

        } catch (Exception e) {
            throw new NullPointerException(e.getMessage());
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (String script : sqlScripts.getCreateScripts()) {
            db.execSQL(script);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
