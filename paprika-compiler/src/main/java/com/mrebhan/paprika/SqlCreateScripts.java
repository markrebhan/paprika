package com.mrebhan.paprika;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

public final class SqlCreateScripts {

    private static final ClassName LIST = ClassName.get("java.util", "List");
    private static final ClassName STRING = ClassName.get("java.lang", "String");
    private static final ClassName ARRAY_LIST = ClassName.get("java.util", "ArrayList");

    private final Set<String> createScripts = new HashSet<>();
    private final SqlUpgradeScripts upgradeScripts;

    public SqlCreateScripts(SqlUpgradeScripts upgradeScripts) {
        this.upgradeScripts = upgradeScripts;
    }

    public void addCreateStatement(Map<String, Element> elementMap, Element parent) {
        createScripts.add(new SqlCreateStatement(elementMap, parent, upgradeScripts).toString());
    }

    public MethodSpec buildMethod() {
        TypeName listOfString = ParameterizedTypeName.get(LIST, STRING);

        MethodSpec.Builder createMethod = MethodSpec.methodBuilder("getCreateScripts")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(listOfString);


        createMethod.addStatement("$T statements = new $T<>()", listOfString, ARRAY_LIST);

        for (String script : createScripts) {
            createMethod.addStatement("statements.add($S)", script);
        }

        createMethod.addStatement("return statements");

        return createMethod.build();
    }
}
