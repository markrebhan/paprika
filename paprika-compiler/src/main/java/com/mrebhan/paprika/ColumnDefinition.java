package com.mrebhan.paprika;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import static javax.lang.model.type.TypeKind.*;

public final class ColumnDefinition {

    public static final int FLAG_NON_NULL = 1;
    public static final int FLAG_UNIQUE = 2;
    public static final int FLAG_PRIMARY_KEY = 4;
    public static final int FLAG_AUTOINCREMENT = 8;

    private static final Map<String, String> SQL_DATA_TYPE_CLASS_MAPPER = new HashMap<>();
    private static final Map<TypeKind, String> SQL_DATA_TYPE_KIND_MAPPER = new HashMap<>();

    static {
        SQL_DATA_TYPE_CLASS_MAPPER.put("java.lang.Integer", "INTEGER");
        SQL_DATA_TYPE_CLASS_MAPPER.put("java.lang.Long", "INTEGER");
        SQL_DATA_TYPE_CLASS_MAPPER.put("java.lang.String", "TEXT");
        SQL_DATA_TYPE_CLASS_MAPPER.put("java.lang.Float", "REAL");
        SQL_DATA_TYPE_CLASS_MAPPER.put("java.lang.Double", "REAL");
        SQL_DATA_TYPE_CLASS_MAPPER.put("java.lang.Boolean", "BOOLEAN");
        SQL_DATA_TYPE_CLASS_MAPPER.put("java.util.Date", "DATE");

        SQL_DATA_TYPE_KIND_MAPPER.put(SHORT, "INTEGER");
        SQL_DATA_TYPE_KIND_MAPPER.put(INT, "INTEGER");
        SQL_DATA_TYPE_KIND_MAPPER.put(LONG, "INTEGER");
        SQL_DATA_TYPE_KIND_MAPPER.put(FLOAT, "REAL");
        SQL_DATA_TYPE_KIND_MAPPER.put(DOUBLE, "REAL");
        SQL_DATA_TYPE_KIND_MAPPER.put(BOOLEAN, "BOOLEAN");
    }

    private String name;
    private String dataType;
    public int flags;

    private ColumnDefinition() {
    }

    public ColumnDefinition(Element element) {
        this.name = element.getSimpleName().toString();
        setupDataType(element);
        setupFlags(element);
    }

    private void setupDataType(Element element) {

        TypeKind kind = element.asType().getKind();
        if (kind.isPrimitive()) {
            dataType = SQL_DATA_TYPE_KIND_MAPPER.get(kind);
        } else {
            HashSet<Element> elements = new HashSet<>();
            elements.add(element);
            Set<VariableElement> fields = ElementFilter.fieldsIn(elements);
            TypeMirror fieldType = fields.iterator().next().asType();
            String className = fieldType.toString();

            dataType = SQL_DATA_TYPE_CLASS_MAPPER.get(className);
        }

        if (dataType == null) {
            dataType = "BLOB";
        }
    }

    private void setupFlags(Element element) {
//        NonNull nonNull = element.getAnnotation(NonNull.class);
//
//        if (nonNull != null) {
//            flags |= FLAG_NON_NULL;
//        }

        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            String modifier = annotationMirror.toString().substring(1);

            if (NonNull.class.getName().equals(modifier)) {
                flags |= FLAG_NON_NULL;
            } else if (Unique.class.getName().equals(modifier)) {
                flags |= FLAG_UNIQUE;
            } else if (PrimaryKey.class.getName().equals(modifier)) {
                // TODO do not force auto autoincrement here
                flags |= FLAG_PRIMARY_KEY | FLAG_AUTOINCREMENT;
                name = "_id";
            } else if (Ignore.class.getName().equals(modifier)) {
                flags = -1;
            }
        }
    }

    private String getFlagString() {
        if (flags < 0) {
            return null;
        }

        StringBuilder flagsString = new StringBuilder("");

        if ((flags & FLAG_NON_NULL) != 0) {
            flagsString.append("NON NULL ");
        }

        if ((flags & FLAG_UNIQUE) != 0) {
            flagsString.append("UNIQUE ");
        }

        if ((flags & FLAG_PRIMARY_KEY) != 0) {
            flagsString.append("PRIMARY KEY ");
        }

        if ((flags & FLAG_AUTOINCREMENT) != 0) {
            flagsString.append("AUTOINCREMENT ");
        }

        return flagsString.toString();
    }

    @Override
    public String toString() {
        String flags = getFlagString();

        if (flags == null) {
            return "";
        } else {
            return name + " " + dataType + " " + flags;
        }
    }
}
