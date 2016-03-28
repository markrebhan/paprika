package com.mrebhan.paprika;

import com.squareup.javapoet.AnnotationSpec;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.Map;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;

import static com.mrebhan.paprika.ClassNameFinder.*;
import static com.mrebhan.paprika.PaprikaMappings.*;
import static com.mrebhan.paprika.consts.Constants.PAPRIKA_MAPPER_SUFFIX;
import static javax.lang.model.type.TypeKind.*;

public final class MapperClassBuilder {

    private static final Map<String, String> CURSOR_METHOD_MAP = new HashMap<String, String>() {{
        put("java.lang.Integer", "getInt");
        put("java.lang.Long", "getLong");
        put("java.lang.String", "getString");
        put("java.lang.Float", "getFloat");
        put("java.lang.Double", "getDouble");
        put("java.lang.Boolean", "getInt");
        put("byte[]", "getBlob");
    }};

    private static final Map<TypeKind, String> CURSOR_METHOD_MAP_PRIMITIVE = new HashMap<TypeKind, String>() {{
        put(SHORT, "getInt");
        put(INT, "getInt");
        put(LONG, "getLong");
        put(FLOAT, "getFloat");
        put(DOUBLE, "getDouble");
        put(BOOLEAN, "getInt");
    }};

    private static final Map<String, String> CURSOR_FORMAT_MAP = new HashMap<String, String>() {{
        put("java.lang.Boolean", "$L = cursor.$L($N) != 0");
    }};

    private static final Map<TypeKind, String> CURSOR_FORMAT_MAP_PRIMITIVE = new HashMap<TypeKind, String>() {{
        put(BOOLEAN, "$L = cursor.$L($N) != 0");
    }};

    private final TypeSpec.Builder builder;
    private final String packageName;
    private final String className;

    private final Map<Element, Map<String, Element>> tableMap;

    public MapperClassBuilder(Map<String, Element> elementMap, Element parent, Map<Element, Map<String, Element>> tableMap, String packageName) {
        this.packageName = packageName;
        this.tableMap = tableMap;
        className = getClassName(parent, packageName, false);
        String superClassName = className + PAPRIKA_MAPPER_SUFFIX;

        TypeMirror type = parent.asType();
        builder = TypeSpec.classBuilder(superClassName)
                .superclass(ClassName.get((TypeElement) parent))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ParameterizedTypeName.get(PAPRIKA_MAPPER, TypeName.get(type)))
                .addAnnotation(AnnotationSpec.builder(SuppressWarnings.class).addMember("value", "$S", "ParcelCreator").build());

        builder.addMethod(buildSetupModelCopyMethod(elementMap, type));
        builder.addMethod(buildSetupModelCursorMethod(elementMap));
        builder.addMethod(buildContentValuesMethod(elementMap));
        builder.addMethod(buildContentValuesTreeMethod(elementMap, parent));
        builder.addMethod(externalMappingsMethod.build());
    }

    public JavaFile build() {
        return JavaFile.builder(packageName, builder.build())
                .addFileComment("Code Generated for Paprika. Do not modify!")
                .build();
    }

    private MethodSpec.Builder externalMappingsMethod;

    private MethodSpec buildContentValuesMethod(Map<String, Element> elementMap) {
        MethodSpec.Builder getContentResolverMethod = MethodSpec.methodBuilder("getContentValues")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(CONTENT_VALUES);

        getContentResolverMethod.addStatement("$T contentValues = new $T()", CONTENT_VALUES, CONTENT_VALUES);

        TypeName listOfString = ParameterizedTypeName.get(ARRAY_LIST, STRING);

        externalMappingsMethod = MethodSpec.methodBuilder("getExternalMappings")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(listOfString);

        externalMappingsMethod.addStatement("$T externalMappings = new $T()", listOfString, listOfString);


        for (String key : elementMap.keySet()) {
            // TODO only for autoincrement
            Element element = elementMap.get(key);
            if (element.getAnnotation(PrimaryKey.class) == null) {

                ForeignObject foreignObject = element.getAnnotation(ForeignObject.class);

                if (foreignObject == null) {
                    getContentResolverMethod.addStatement("contentValues.put($S,$L)", key, key);
                } else {
                    // TODO do not use keys in case of multiple
                    externalMappingsMethod.addStatement("externalMappings.add($S)", key);
                }
            }
        }

        getContentResolverMethod.addStatement("return contentValues");
        externalMappingsMethod.addStatement("return externalMappings");

        return getContentResolverMethod.build();
    }

    private MethodSpec buildContentValuesTreeMethod(Map<String, Element> elementMap, Element parent) {
        MethodSpec.Builder getContentResolverMethod = MethodSpec.methodBuilder("getContentValuesTree")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(CONTENT_VALUES_TREE);

        getContentResolverMethod.addStatement("$T.Builder builder = new $T.Builder()", CONTENT_VALUES_TREE, CONTENT_VALUES_TREE);

        getContentResolverMethod.addStatement("$T rootWrapper = builder.setRootNode(getContentValues(), $S, getExternalMappings())", CONTENT_VALUES_WRAPPER, className);

        for (String key : elementMap.keySet()) {
            Element element = elementMap.get(key);
            if (element.getAnnotation(PrimaryKey.class) == null) {

                ForeignObject foreignObject = element.getAnnotation(ForeignObject.class);

                if (foreignObject != null) {
                    addChildWrapper(element, getContentResolverMethod, "rootWrapper");
                }
            }
        }

        getContentResolverMethod.addStatement("return builder.build()");

        return getContentResolverMethod.build();
    }

    private void addChildWrapper(Element childElement, MethodSpec.Builder builder, String parentMemberName) {

        String className = getClassName(childElement, packageName, false);
        Map<String, Element> elementMap = getElementMapFromClassName(className);

        if (elementMap != null) {

            String mapperClassName = className + PAPRIKA_MAPPER_SUFFIX;

            String childName = childElement.getSimpleName().toString();
            builder.beginControlFlow("if ($L != null)", childName);
            builder.addStatement("builder.addChild($L, (($L) $L).getContentValuesTree())",
                    parentMemberName, mapperClassName, childName);
            builder.endControlFlow();
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

    private MethodSpec buildSetupModelCursorMethod(Map<String, Element> elementMap) {

        MethodSpec.Builder setupModel = MethodSpec.methodBuilder("setupModel")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(CURSOR, "cursor")
                .addParameter(int.class, "index")
                .returns(int.class);

        for (String key : elementMap.keySet()) {
            Element element = elementMap.get(key);

            ForeignObject foreignObject = element.getAnnotation(ForeignObject.class);

            if (foreignObject == null) {
                setupModel.addStatement(getCursorFormat(element), key, getCursorMethod(element), "index");
                setupModel.addStatement("$L++", "index");
            } else {
                String mapperClassName = getClassName(element, packageName, false) + PAPRIKA_MAPPER_SUFFIX;

                setupModel.addStatement("$L = new $L()", key, mapperClassName);
                setupModel.addStatement("$L = (($L)$L).setupModel($L,$L)", "index", mapperClassName, key, "cursor", "index");
            }
        }

        setupModel.addStatement("return $L", "index");

        return setupModel.build();
    }

    private MethodSpec buildSetupModelCopyMethod(Map<String, Element> elementMap, TypeMirror type) {
        MethodSpec.Builder setupModel = MethodSpec.methodBuilder("setupModel")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TypeName.get(type), "model");


        for (String key : elementMap.keySet()) {
            Element element = elementMap.get(key);
            ForeignObject foreignObject = element.getAnnotation(ForeignObject.class);

            if (foreignObject == null) {
                setupModel.addStatement("$L = model.$L", key, key);
            } else {
                String mapperClassName = getClassName(element, packageName, false) + PAPRIKA_MAPPER_SUFFIX;

                setupModel.beginControlFlow("if (model.$L != null)", key);
                setupModel.addStatement("$L = new $L()", key, mapperClassName);
                setupModel.addStatement("(($L)$L).setupModel(model.$L)", mapperClassName, key, key);
                setupModel.endControlFlow();
            }
        }

        return setupModel.build();
    }

    private String getCursorFormat(Element element) {
        final TypeKind kind = element.asType().getKind();

        final String format;

        if (kind.isPrimitive()) {
            format = CURSOR_FORMAT_MAP_PRIMITIVE.get(kind);
        } else {
            format = CURSOR_FORMAT_MAP.get(getClassName(element, packageName, true));
        }

        return format != null ? format : "$L = cursor.$L($N)";
    }

    private String getCursorMethod(Element element) {
        final TypeKind kind = element.asType().getKind();

        final String method;

        if (kind.isPrimitive()) {
            method = CURSOR_METHOD_MAP_PRIMITIVE.get(kind);

        } else {
            method = CURSOR_METHOD_MAP.get(getClassName(element, packageName, true));

        }

        if (method == null) {
            Logger.logError(element, "This type is currently not supported: " + kind);
        }

        return method;
    }

}
