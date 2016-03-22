package com.mrebhan.paprika;

import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

import static com.mrebhan.paprika.PaprikaMappings.*;

public final class SqlCreateScripts {

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
