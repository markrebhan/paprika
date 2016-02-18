package com.mrebhan.paprika;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mrebhan.paprika.internal.PaprikaMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import static com.mrebhan.paprika.consts.Constants.PAPRIKA_MAPPER_SUFFIX;

public final class Paprika {

    private static PaprikaDataHelper dataHelper;
    private static final Map<Class, Class> CLASS_TO_PAPRIKA_MAP = new WeakHashMap<>();

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
    public static Object create(Object data) {

        final PaprikaMapper mapperClass;

        if (isSuperClass(data.getClass())) {
            mapperClass = (PaprikaMapper) data;
        } else {
            final Class superClass = findMapperClass(data.getClass());

            try {
                mapperClass = (PaprikaMapper) superClass.newInstance();
                mapperClass.setupModel(data);

            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }

        ContentValues contentValues = mapperClass.getContentValues();

        SQLiteDatabase db = dataHelper.getWritableDatabase();

        db.insertWithOnConflict(getTableName(data.getClass()), null, contentValues, SQLiteDatabase.CONFLICT_REPLACE);

        return mapperClass;
    }

    public static Object update(Object data, long id) {
        final PaprikaMapper mapperClass;

        if (isSuperClass(data.getClass())) {
            mapperClass = (PaprikaMapper) data;
        } else {
            final Class superClass = findMapperClass(data.getClass());

            try {
                mapperClass = (PaprikaMapper) superClass.newInstance();
                mapperClass.setupModel(data);

            } catch (Exception e) {
                throw new IllegalArgumentException(e.getMessage());
            }
        }

        ContentValues contentValues = mapperClass.getContentValues();

        SQLiteDatabase db = dataHelper.getWritableDatabase();

        db.update(getTableName(data.getClass()), contentValues, "_id = ?", new String[]{Long.toString(id)});

        return mapperClass;
    }

    public static Object createOrUpdate(Object data, long id) {
        if (id <= 0) {
            return create(data);
        } else {
            return update(data, id);
        }
    }

    public static <T> T get(Class<T> objectClazz, long id) {
        Class superClass = findMapperClass(objectClazz);

        SQLiteDatabase db = dataHelper.getWritableDatabase();

        // TODO add dynamic ID in mapper class
        Cursor cursor = db.query(getTableName(objectClazz), null, "_id = ?", new String[]{Long.toString(id)}, null, null, null);

        try {
            T item = null;

            if (cursor.getCount() > 0 && !cursor.isClosed()) {
                cursor.moveToFirst();

                try {
                    PaprikaMapper mapper = (PaprikaMapper) superClass.newInstance();
                    mapper.setupModel(cursor);
                    item = (T) mapper;

                } catch (Exception e) {
                    throw new IllegalArgumentException(e.getMessage());
                }

            }
            return item;

        } finally {
            cursor.close();
        }
    }

    public static <T> List<T> getList(Class<T> objectClazz) {
        return getList(objectClazz, null, null, null);
    }

    // TODO add query builder maybe
    public static <T> List<T> getList(Class<T> objectClazz, String selection, String[] selectionArgs, String orderBy) {
        Class superClass = findMapperClass(objectClazz);

        SQLiteDatabase db = dataHelper.getWritableDatabase();

        Cursor cursor = db.query(getTableName(objectClazz), null, selection, selectionArgs, null, null, orderBy);

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

    public static <T> void delete(Class<T> objectClazz, long id) {
        SQLiteDatabase db = dataHelper.getWritableDatabase();

        db.delete(getTableName(objectClazz), "_id = ?", new String[]{Long.toString(id)});
    }

    private static boolean isSuperClass(Class clazz) {
        return clazz.getName().contains(PAPRIKA_MAPPER_SUFFIX);
    }

    private static String getTableName(Class clazz) {
        return clazz.getSimpleName().replace(PAPRIKA_MAPPER_SUFFIX, "");
    }

    private static <T> Class<? extends T> findMapperClass(Class<T> dataClass)  {

        Class<? extends T> mappedSuperClass = CLASS_TO_PAPRIKA_MAP.get(dataClass);

        if (mappedSuperClass == null) {
            try {
                Class<?> findClass = Class.forName(dataClass.getName() + PAPRIKA_MAPPER_SUFFIX);
                mappedSuperClass = (Class<? extends T>) findClass;
                CLASS_TO_PAPRIKA_MAP.put(dataClass, mappedSuperClass);
            } catch (ClassNotFoundException e) {
                throw new ClassCastException(e.getMessage());
            }
        }
        return mappedSuperClass;
    }

}
