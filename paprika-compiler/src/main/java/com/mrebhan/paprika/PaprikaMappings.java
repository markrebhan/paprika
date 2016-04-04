package com.mrebhan.paprika;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

import static com.mrebhan.paprika.consts.Constants.PAPRIKA_SQL_SCRIPTS_CLASS_NAME;

public final class PaprikaMappings {

    public static final ClassName SQL_SCRIPTS = ClassName.get("com.mrebhan.paprika.internal", "SqlScripts");
    public static final ClassName PAPRIKA_MAPPER = ClassName.get("com.mrebhan.paprika.internal", "PaprikaMapper");
    public static final ClassName CONTENT_VALUES_TREE = ClassName.get("com.mrebhan.paprika.internal", "ContentValuesTree");
    public static final ClassName CONTENT_VALUES_WRAPPER = ClassName.get("com.mrebhan.paprika.internal", "ContentValuesWrapper");
    public static final ClassName CONTENT_VALUES = ClassName.get("android.content", "ContentValues");
    public static final ClassName CURSOR = ClassName.get("android.database", "Cursor");
    public static final ClassName STRING = ClassName.get("java.lang", "String");
    public static final ClassName ARRAY_LIST = ClassName.get("java.util", "ArrayList");
    public static final ClassName LIST = ClassName.get("java.util", "List");
    public static final ClassName HASH_MAP = ClassName.get("java.util", "HashMap");
    public static final ClassName MAP = ClassName.get("java.util", "Map");
    public static final ClassName INTEGER = ClassName.get("java.lang", "Integer");

    private final Map<Element, Map<String, Element>> tableMap;
    private final TypeSpec.Builder builder;

    private final Version version;
    private final SqlUpgradeScripts sqlUpgradeScripts;
    private final SqlCreateScripts sqlCreateScripts;
    private final SqlSelectScripts sqlSelectScripts;

    public PaprikaMappings(Map<Element, Map<String, Element>> tableMap) {
        this.tableMap = tableMap;
        this.builder = TypeSpec.classBuilder(PAPRIKA_SQL_SCRIPTS_CLASS_NAME).addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(SQL_SCRIPTS);

        this.version = new Version();
        this.sqlUpgradeScripts = new SqlUpgradeScripts(version);
        this.sqlCreateScripts = new SqlCreateScripts(sqlUpgradeScripts);
        this.sqlSelectScripts = new SqlSelectScripts(builder, tableMap);
    }

    public JavaFile constructDataMappings(Element element, int version, String packageName) {
        Map<String, Element> elementMappers = tableMap.get(element);

        sqlCreateScripts.addCreateStatement(elementMappers, element);
        sqlSelectScripts.addSelectStatement(element, packageName);

        if (version > 1) {
            sqlUpgradeScripts.addUpgradeNewTable(elementMappers, element, version);
        }

        return new MapperClassBuilder(elementMappers, element, tableMap, packageName).build();
    }

    public TypeSpec build() {
        builder.addMethod(sqlCreateScripts.buildMethod());
        builder.addMethod(sqlUpgradeScripts.buildMethod());
        builder.addMethod(version.buildMethod());
        sqlSelectScripts.build();
        return builder.build();
    }
}
