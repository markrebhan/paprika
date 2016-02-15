package com.mrebhan.paprika;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

import static com.mrebhan.paprika.consts.Constants.PAPRIKA_MAPPER_SUFFIX;
import static javax.lang.model.type.TypeKind.*;

public final class MapperClassBuilder {

    private static final ClassName PAPRIKA_MAPPER = ClassName.get("com.mrebhan.paprika.internal", "PaprikaMapper");
    private static final ClassName CONTENT_VALUES = ClassName.get("android.content", "ContentValues");
    private static final ClassName CURSOR = ClassName.get("android.database", "Cursor");

    private static final Map<String, String> CURSOR_METHOD_MAP = new HashMap<String, String>() {{
        put("java.lang.Integer", "getInt");
        put("java.lang.Long", "getLong");
        put("java.lang.String", "getString");
        put("java.lang.Float", "getFloat");
        put("java.lang.Double", "getDouble");
        put("java.lang.Boolean", "getBoolean");
    }};

    private static final Map<TypeKind, String> CURSOR_METHOD_MAP_PRIMITIVE = new HashMap<TypeKind, String>() {{
        put(SHORT, "getInt");
        put(INT, "getInt");
        put(LONG, "getLong");
        put(FLOAT, "getFloat");
        put(DOUBLE, "getDouble");
        put(BOOLEAN, "getBoolean");
    }};



    private final TypeSpec.Builder builder;
    private final String packageName;

    public MapperClassBuilder(Map<String, Element> elementMap, Element parent, String packageName) {
        this.packageName = packageName;
        String className = getClassName(parent, packageName) + PAPRIKA_MAPPER_SUFFIX;

        TypeMirror type = parent.asType();
        builder = TypeSpec.classBuilder(className)
                .superclass(ClassName.get((TypeElement) parent))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ParameterizedTypeName.get(PAPRIKA_MAPPER, TypeName.get(type)));

        builder.addMethod(buildSetupModelCopyMethod(elementMap, type));
        builder.addMethod(buildSetupModelCursorMethod(elementMap));
        builder.addMethod(buildContentValuesMethod(elementMap));
    }

    public JavaFile build() {
        return JavaFile.builder(packageName, builder.build())
                .addFileComment("Code Generated for Paprika. Do not modify!")
                .build();
    }

    private MethodSpec buildContentValuesMethod(Map<String, Element> elementMap) {
        MethodSpec.Builder getContentResolverMethod = MethodSpec.methodBuilder("getContentValues")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(CONTENT_VALUES);

        getContentResolverMethod.addStatement("$T contentValues = new $T()", CONTENT_VALUES, CONTENT_VALUES);

        for (String key : elementMap.keySet()) {
            // TODO only for autoincrement
            if (elementMap.get(key).getAnnotation(PrimaryKey.class) == null) {
                getContentResolverMethod.addStatement("contentValues.put($S,$L)", key, key);
            }
        }

        getContentResolverMethod.addStatement("return contentValues");

        return getContentResolverMethod.build();
    }

    private MethodSpec buildSetupModelCursorMethod(Map<String, Element> elementMap) {
        MethodSpec.Builder setupModel = MethodSpec.methodBuilder("setupModel")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(CURSOR, "cursor");

        int index = 0;
        for (String key : elementMap.keySet()) {
            Element element = elementMap.get(key);
            setupModel.addStatement("$L = cursor.$L($L)", key, getCursorMethod(element), index);
            index++;
        }

        return setupModel.build();
    }

    private MethodSpec buildSetupModelCopyMethod(Map<String, Element> elementMap, TypeMirror type) {
        MethodSpec.Builder setupModel = MethodSpec.methodBuilder("setupModel")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(TypeName.get(type), "model");


        for (String key : elementMap.keySet()) {
            setupModel.addStatement("$L = model.$L", key, key);
        }

        return setupModel.build();
    }

    private String getCursorMethod(Element element) {
        TypeKind kind = element.asType().getKind();

        final String method;

        if (kind.isPrimitive()) {
            method = CURSOR_METHOD_MAP_PRIMITIVE.get(kind);
        } else {
            HashSet<Element> elements = new HashSet<>();
            elements.add(element);
            Set<VariableElement> fields = ElementFilter.fieldsIn(elements);
            TypeMirror fieldType = fields.iterator().next().asType();
            String className = fieldType.toString();

            method = CURSOR_METHOD_MAP.get(className);
        }

        return method != null ? method : "getBlob";
    }

    private String getClassName(Element element, String packageName) {
        int packageLen = packageName.length() + 1;
        return ((TypeElement) element).getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }

}
