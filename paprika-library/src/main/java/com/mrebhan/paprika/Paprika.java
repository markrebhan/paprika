package com.mrebhan.paprika;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mrebhan.paprika.internal.PaprikaMapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mrebhan.paprika.consts.Constants.PAPRIKA_MAPPER_SUFFIX;

public final class Paprika {

    private static PaprikaDataHelper dataHelper;
    private static final Map<Class, Class> CLASS_TO_SUPER_MAP = new HashMap<>();

    public Paprika() {
    }

    //TODO pass in name and version
    public static void init(Context context) {
        if (dataHelper == null) {
            dataHelper = new PaprikaDataHelper(context, "paprika", 1);
        }
    }

    /**
     * Saves an object into the database
     * @param data
     * @return
     */
    public static Object save(Object data) {

        Class superClass = findMapperClass(data.getClass());
        PaprikaMapper mapperClass;

        if (data.getClass().getName().equals(superClass.getClass().getName())) {
            mapperClass = (PaprikaMapper) data;
        } else {
            try {
                mapperClass = (PaprikaMapper) superClass.newInstance();
                mapperClass.setupModel(data);

            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }

        ContentValues contentValues = mapperClass.getContentValues();

        SQLiteDatabase db = dataHelper.getWritableDatabase();

        db.beginTransaction();
        db.insertWithOnConflict(data.getClass().getSimpleName(), null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);
        db.endTransaction();

        return mapperClass;
    }

    public static <T> List<T> getList(Class<T> objectClazz) {

        Class superClass = findMapperClass(objectClazz);

        SQLiteDatabase db = dataHelper.getWritableDatabase();

        db.beginTransaction();
        Cursor cursor = db.query(objectClazz.getSimpleName(), null, null, null, null, null, null);
        db.endTransaction();

        try {
            List<T> resultList = new ArrayList<>();

            if (cursor.getCount() > 0 && !cursor.isClosed()) {
                cursor.moveToFirst();

                do {
                    try {
                        PaprikaMapper mapper = (PaprikaMapper) superClass.newInstance();
                        mapper.setupModel(cursor);
                        resultList.add((T) mapper);

                    } catch (Exception e) {
                        throw new IllegalArgumentException(e.getMessage());
                    }

                } while (cursor.moveToNext());
            }

            return resultList;

        } finally {
            cursor.close();
        }
    }

    private static <T> Class<? extends T> findMapperClass(Class<T> dataClass)  {
        Class<? extends T> mappedSuperClass = CLASS_TO_SUPER_MAP.get(dataClass);

        if (mappedSuperClass == null) {
            try {
                Class<?> findClass = Class.forName(dataClass.getName() + PAPRIKA_MAPPER_SUFFIX);
                mappedSuperClass = (Class<? extends T>) findClass;
                CLASS_TO_SUPER_MAP.put(dataClass, mappedSuperClass);
            } catch (ClassNotFoundException e) {
                throw new ClassCastException(e.getMessage());
            }
        }
        return mappedSuperClass;
    }




}
