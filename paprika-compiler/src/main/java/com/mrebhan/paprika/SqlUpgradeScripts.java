package com.mrebhan.paprika;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

public final class SqlUpgradeScripts {

    private static final ClassName HASH_MAP = ClassName.get("java.util", "HashMap");
    private static final ClassName ARRAY_LIST = ClassName.get("java.util", "ArrayList");
    private static final ClassName MAP = ClassName.get("java.util", "Map");
    private static final ClassName INTEGER = ClassName.get("java.lang", "Integer");
    private static final ClassName LIST = ClassName.get("java.util", "List");
    private static final ClassName STRING = ClassName.get("java.lang", "String");

    private final Map<Integer, Set<String>> upgradeScripts = new HashMap<>();
    private final Version versionChecker;

    public SqlUpgradeScripts(Version version) {
        this.versionChecker = version;
    }

    public void addUpgradeNewTable(Map<String, Element> elementMap, Element parent, int version) {
        String createScript =  new SqlCreateStatement(elementMap, parent, null).toString();
        addUpgradeScriptToMap(version, createScript);
        versionChecker.updateMaxVersion(version);
    }

    public void addAlterAddColumn(ColumnDefinition columnDefinition, String table, int version) {
        String alterScript = new SqlAlterAddColumnStatement(columnDefinition, table).toString();
        addUpgradeScriptToMap(version, alterScript);
        versionChecker.updateMaxVersion(version);
    }

    private void addUpgradeScriptToMap(int version, String script) {

        Set<String> scriptsList = upgradeScripts.get(version);

        if (scriptsList == null) {
            scriptsList = new HashSet<>();
        }

        scriptsList.add(script);

        upgradeScripts.put(version, scriptsList);
    }

    public MethodSpec buildMethod() {
        TypeName listOfString = ParameterizedTypeName.get(LIST, STRING);
        TypeName mapOfList = ParameterizedTypeName.get(MAP, INTEGER, listOfString);

        MethodSpec.Builder upgradeMethod = MethodSpec.methodBuilder("getUpgradeScripts")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(mapOfList);

        upgradeMethod.addStatement("$T statementsMap = new $T<>()", mapOfList, HASH_MAP);

        for (int version : upgradeScripts.keySet()) {
            Set<String> scripts = upgradeScripts.get(version);

            upgradeMethod.addStatement("$T statements$L = new $T<>()", listOfString, version, ARRAY_LIST);

            for (String statement : scripts) {
                upgradeMethod.addStatement("statements$L.add($S)", version, statement);
            }

            upgradeMethod.addStatement("statementsMap.put($L, statements$L)", version, version);
        }

        upgradeMethod.addStatement("return statementsMap");

        return upgradeMethod.build();
    }

}
