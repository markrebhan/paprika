package com.mrebhan.paprika;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mrebhan.paprika.internal.ContentValuesTree;
import com.mrebhan.paprika.internal.ContentValuesWrapper;
import com.mrebhan.paprika.internal.PaprikaMapper;
import com.mrebhan.paprika.internal.SqlScripts;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import static com.mrebhan.paprika.consts.Constants.PAPRIKA_MAPPER_SUFFIX;
import static com.mrebhan.paprika.consts.Constants.PAPRIKA_PACKAGE;
import static com.mrebhan.paprika.consts.Constants.PAPRIKA_SQL_SCRIPTS_CLASS_NAME;

public final class Paprika {

    private static PaprikaDataHelper dataHelper;
    private static final Map<Class, Class> CLASS_TO_PAPRIKA_MAP = new WeakHashMap<>();
    private static SqlScripts sqlScripts;

    //TODO pass in name and version
    public static void init(Context context, String databaseName) {
        if (dataHelper == null) {
            try {

                Class<?> sqlScriptBinder = Class.forName(PAPRIKA_PACKAGE + "." + PAPRIKA_SQL_SCRIPTS_CLASS_NAME);
                sqlScripts = (SqlScripts) sqlScriptBinder.newInstance();
                dataHelper = new PaprikaDataHelper(context, databaseName, sqlScripts);

            } catch (Exception e) {
                throw new NullPointerException(e.getMessage());
            }
        }
    }

    /**
     * Saves an object into the database
     * @param data
     * @return
     */
    public static Object createOrUpdate(Object data) {

        final PaprikaMapper mapperClass = getMapperClass(data);
        final long id = mapperClass.getId();


        if (id == 0) {
            create(mapperClass);
        } else {
           update(mapperClass);
        }

        return mapperClass;
    }

    public static Object create(Object data) {
        final PaprikaMapper mapperClass = getMapperClass(data);
        final long id = mapperClass.getId();

        if (id == 0) {
            ContentValuesTree contentValuesTree = mapperClass.getContentValuesTree();
            SQLiteDatabase db = dataHelper.getWritableDatabase();

            while (contentValuesTree.hasNext()) {
                ContentValuesWrapper contentValuesWrapper = contentValuesTree.next();
                long row = db.insert(contentValuesWrapper.getTableName(), null, contentValuesWrapper.getContentValues());

                ContentValuesWrapper parent = contentValuesWrapper.getParentNode();
                if (parent != null) {
                    parent.addExternalMappingIndex(row);
                }
            }
        } else {
            throw new IllegalArgumentException("The item you are trying to insert already exists.");
        }

        return mapperClass;
    }

    public static Object update(Object data) {
        final PaprikaMapper mapperClass = getMapperClass(data);
        ContentValues contentValues = mapperClass.getContentValues();

        final long id = mapperClass.getId();

        if (id != 0) {
            SQLiteDatabase db = dataHelper.getWritableDatabase();
            db.update(getTableName(data.getClass()), contentValues, "_id = ?", new String[]{Long.toString(mapperClass.getId())});
        } else {
            throw new IllegalArgumentException("The item you are trying to update does not exist in the database");
        }

        return mapperClass;
    }

    public static <T> T get(Class<T> objectClazz, long id) {
        Class superClass = findMapperClass(objectClazz);

        SQLiteDatabase db = dataHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(sqlScripts.getSelectQuery(getTableName(superClass)) + " WHERE " + getTableName(objectClazz) + "._id=" + id, null);

        try {
            T item = null;

            if (cursor.getCount() > 0 && !cursor.isClosed()) {
                cursor.moveToFirst();

                try {
                    PaprikaMapper mapper = (PaprikaMapper) superClass.newInstance();
                    mapper.setupModel(cursor, 0);
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
        Class superClass = findMapperClass(objectClazz);

        SQLiteDatabase db = dataHelper.getWritableDatabase();

        Cursor cursor = db.rawQuery(sqlScripts.getSelectQuery(getTableName(superClass)), null);

        try {
            List<T> resultList = new ArrayList<>();

            if (cursor.getCount() > 0 && !cursor.isClosed()) {
                cursor.moveToFirst();

                do {
                    try {
                        PaprikaMapper mapper = (PaprikaMapper) superClass.newInstance();
                        mapper.setupModel(cursor, 0);
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

    public static void delete(Object data) {
        final PaprikaMapper mapperClass = getMapperClass(data);
        final long id = mapperClass.getId();

        if (id != 0) {
            SQLiteDatabase db = dataHelper.getWritableDatabase();
            db.delete(getTableName(data.getClass()), "_id = ?", new String[]{Long.toString(id)});
        }
    }

    public static long getId(Object data) {
        final PaprikaMapper mapperClass = getMapperClass(data);
        return mapperClass.getId();
    }

    private static PaprikaMapper getMapperClass(Object data) {
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

        return mapperClass;
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
