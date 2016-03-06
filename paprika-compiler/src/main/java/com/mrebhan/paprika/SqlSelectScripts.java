package com.mrebhan.paprika;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;

import static com.mrebhan.paprika.ClassNameFinder.*;

public final class SqlSelectScripts {

    private static final ClassName HASH_MAP = ClassName.get("java.util", "HashMap");
    private static final ClassName STRING = ClassName.get("java.lang", "String");
    private static final String ID = "_id";

    private TypeSpec.Builder builder;
    private CodeBlock.Builder staticCodeBlockBuilder;

    private final Map<Element, Map<String, Element>> tableMap;

    public SqlSelectScripts(TypeSpec.Builder builder, Map<Element, Map<String, Element>> tableMap) {
        this.builder = builder;
        this.tableMap = tableMap;
        this.staticCodeBlockBuilder = CodeBlock.builder();
        TypeName hashMap = ParameterizedTypeName.get(HASH_MAP, STRING, STRING);
        builder.addField(hashMap, "selectQueries", Modifier.PRIVATE, Modifier.STATIC, Modifier.FINAL);
        staticCodeBlockBuilder.addStatement("selectQueries = new $T()", HASH_MAP);
    }


    public void addSelectStatement(Element parent, String packageName) {

        final String className = getClassName(parent, packageName, false);

        StringBuilder stringBuilder = new StringBuilder("SELECT ");

        Map<String, String> joinMappers = new HashMap<>();

        addSelectColumn(tableMap.get(parent), packageName, className, stringBuilder, joinMappers);

        stringBuilder.replace(stringBuilder.length() - 2, stringBuilder.length(), " FROM ");
        stringBuilder.append(className);

        for (String key : joinMappers.keySet()) {
            String joinedColumn = joinMappers.get(key);
            stringBuilder.append(" LEFT JOIN ");
            stringBuilder.append(joinedColumn);
            stringBuilder.append(" ON ");
            stringBuilder.append(key);
            stringBuilder.append(" = ");
            stringBuilder.append(joinedColumn);
            stringBuilder.append('.');
            stringBuilder.append(ID);
        }

        staticCodeBlockBuilder.addStatement("selectQueries.put($S,$S)", className, stringBuilder.toString());
    }

    private void addSelectColumn(Map<String, Element> elementMap, String packageName, String className, StringBuilder stringBuilder, Map<String, String> joinMappers) {
        if (elementMap == null) {
            return;
        }

        for (String key : elementMap.keySet()) {
            Element element = elementMap.get(key);
            ForeignObject foreignObject = element.getAnnotation(ForeignObject.class);

            if (foreignObject == null) {

                if (element.getAnnotation(PrimaryKey.class) != null) {
                    key = ID;
                }

                stringBuilder.append(className);
                stringBuilder.append('.');
                stringBuilder.append(key);
                stringBuilder.append(", ");
            } else {
                final String childClassName = getClassName(element, packageName, false);
                joinMappers.put(className + "." + key, childClassName);
                addSelectColumn(getElementMapFromClassName(childClassName), packageName, childClassName, stringBuilder, joinMappers);
            }
        }
    }

    private Map<String, Element> getElementMapFromClassName(String className) {
        Map<String, Element> elementMap = null;

        for (Element elementMapper : tableMap.keySet()) {
            if (elementMapper.getSimpleName().toString().equals(className)) {
                elementMap = tableMap.get(elementMapper);
                break;
            }
        }

        return elementMap;
    }

    public void build() {
        MethodSpec.Builder methodBuilder = MethodSpec.methodBuilder("getSelectQuery")
                .returns(STRING)
                .addAnnotation(Override.class)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(STRING, "className");

        methodBuilder.addStatement("return selectQueries.get($L)", "className");

        builder.addStaticBlock(staticCodeBlockBuilder.build());
        builder.addMethod(methodBuilder.build());
    }
}
