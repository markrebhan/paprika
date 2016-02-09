package com.mrebhan.paprika;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.mrebhan.paprika.consts.Constants.PAPRIKA_PACKAGE;
import static com.mrebhan.paprika.consts.Constants.PAPRIKA_SQL_SCRIPTS_CLASS_NAME;
import static com.mrebhan.paprika.consts.Constants.PAPRIKA_MAPPER_SUFFIX;
import static javax.tools.Diagnostic.Kind.ERROR;

//TODO add support for having _id in class or generate it

@AutoService(Processor.class)
public final class PaprikaProcessor extends AbstractProcessor {
    private static final ClassName SQL_SCRIPTS = ClassName.get("com.mrebhan.paprika.internal", "SqlScripts");
    private static final ClassName PAPRIKA_MAPPER = ClassName.get("com.mrebhan.paprika.internal", "PaprikaMapper");
    private static final ClassName LIST = ClassName.get("java.util", "List");
    private static final ClassName STRING = ClassName.get("java.lang", "String");
    private static final ClassName ARRAY_LIST = ClassName.get("java.util", "ArrayList");
    private static final ClassName CONTENT_VALUES = ClassName.get("android.content", "ContentValues");
    private static final ClassName CURSOR = ClassName.get("android.database", "Cursor");
    private static final Map<String, String> COLUMN_MODIFIERS = new HashMap<>();

    static {
        COLUMN_MODIFIERS.put(Ignore.class.getName(), "");
        COLUMN_MODIFIERS.put(NonNull.class.getName(), "NOT NULL");
        COLUMN_MODIFIERS.put(PrimaryKey.class.getName(), "PRIMARY KEY");
        COLUMN_MODIFIERS.put(Unique.class.getName(), "UNIQUE");
    }

    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer;

    //TODO use SuperFicialValidation
    private boolean isProcessed;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        elementUtils = processingEnv.getElementUtils();
        typeUtils = processingEnv.getTypeUtils();
        filer = processingEnv.getFiler();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();

        types.add(Column.class.getCanonicalName());
        types.add(Ignore.class.getCanonicalName());
        types.add(NonNull.class.getCanonicalName());
        types.add(PrimaryKey.class.getCanonicalName());
        types.add(Table.class.getCanonicalName());
        types.add(Unique.class.getCanonicalName());

        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (isProcessed) {
            return true;
        }

        isProcessed = true;

        TypeSpec.Builder builder = TypeSpec.classBuilder(PAPRIKA_SQL_SCRIPTS_CLASS_NAME).addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(SQL_SCRIPTS);

        TypeName listOfString = ParameterizedTypeName.get(LIST, STRING);

        MethodSpec.Builder createMethod = MethodSpec.methodBuilder("getCreateScripts")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(listOfString);

        //add a list of strings
        createMethod.addStatement("$T statements = new $T<>()", listOfString, ARRAY_LIST);

        for (Element element : roundEnv.getElementsAnnotatedWith(Table.class)) {

            Map<String, Element> elementMappers = new LinkedHashMap<>();

            for (Element classElement : element.getEnclosedElements()) {
                if (classElement.getKind() == ElementKind.FIELD && !classElement.getModifiers().contains(Modifier.STATIC)) {
                    elementMappers.put(classElement.getSimpleName().toString(), classElement);
                }
            }

            buildCreateScript(elementMappers, element, createMethod);
            buildMapperClass(elementMappers, element);
        }

        createMethod.addStatement("return statements");
        builder.addMethod(createMethod.build());

        JavaFile javaFile = JavaFile.builder(PAPRIKA_PACKAGE, builder.build())
                .addFileComment("Code Generated for Paprika. Do not modify!")
                .build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            error(null, "Unable to write create script: %s", e.getMessage());
        }

        return true;
    }

    private void buildCreateScript(Map<String, Element> elementMap, Element parent, MethodSpec.Builder createMethod) {
        // create a table
        // TODO figure out if the table needs the _id
        StringBuilder stringBuilder = new StringBuilder("CREATE TABLE " + parent.getSimpleName() + "(_ID INTEGER PRIMARY KEY AUTOINCREMENT, ");

        for (String key : elementMap.keySet()) {
            Element element = elementMap.get(key);
            stringBuilder.append(key).
                    append(" ").
                    append(getDataType(element)).
                    append(" ");

            addSqlModifiers(stringBuilder, element);
            stringBuilder.append(", ");
        }

        stringBuilder.replace(stringBuilder.length() - 2, stringBuilder.length(), ")");

        createMethod.addStatement("statements.add(\"" + stringBuilder.toString() + "\")");
    }

    private void buildMapperClass(Map<String, Element> elementMap, Element parent) {
        String packageName = getPackageName(parent);
        String className = getClassName(parent, packageName) + PAPRIKA_MAPPER_SUFFIX;
        TypeMirror type = parent.asType();
        TypeSpec.Builder builder = TypeSpec.classBuilder(className)
                .superclass(ClassName.get((TypeElement) parent))
                .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(ParameterizedTypeName.get(PAPRIKA_MAPPER, TypeName.get(type)));

        builder.addMethod(buildSetupModelCopyMethod(elementMap, type));
        builder.addMethod(buildSetupModelCursorMethod(elementMap));
        builder.addMethod(buildContentValuesMethod(elementMap));

        JavaFile javaFile = JavaFile.builder(packageName, builder.build())
                .addFileComment("Code Generated for Paprika. Do not modify!")
                .build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            error(null, "Unable to write create script: %s", e.getMessage());
        }
    }

    private MethodSpec buildContentValuesMethod(Map<String, Element> elementMap) {
        MethodSpec.Builder getContentResolverMethod = MethodSpec.methodBuilder("getContentValues")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .returns(CONTENT_VALUES);

        getContentResolverMethod.addStatement("$T contentValues = new $T()", CONTENT_VALUES, CONTENT_VALUES);

        for (String key : elementMap.keySet()) {
            getContentResolverMethod.addStatement("contentValues.put($S,$L)", key, key);
        }

        getContentResolverMethod.addStatement("return contentValues");

        return getContentResolverMethod.build();
    }

    private MethodSpec buildSetupModelCursorMethod(Map<String, Element> elementMap) {
        MethodSpec.Builder setupModel = MethodSpec.methodBuilder("setupModel")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(Override.class)
                .addParameter(CURSOR, "cursor");

        //TODO adjust index based on if table has _id or not
        int index = 1;
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


    private void addSqlModifiers(StringBuilder stringBuilder, Element element) {

        for (AnnotationMirror annotationMirror : element.getAnnotationMirrors()) {
            String modifier = COLUMN_MODIFIERS.get(annotationMirror.toString().substring(1));

            if (modifier != null) {
                stringBuilder.append(modifier);
                stringBuilder.append(" ");
            }
        }
    }

    private String getDataType(Element element) {
        HashSet<Element> elements = new HashSet<>();
        elements.add(element);
        Set<VariableElement> fields = ElementFilter.fieldsIn(elements);
        TypeMirror fieldType = fields.iterator().next().asType();
        String className = fieldType.toString();

        if (className.equals(int.class.getName()) ||
                className.equals(long.class.getName())) {
            return "INTEGER";
        } else if (className.equals(String.class.getName())) {
            return "TEXT";
        }

        return "BLOB";
    }

    private String getCursorMethod(Element element) {
        HashSet<Element> elements = new HashSet<>();
        elements.add(element);
        Set<VariableElement> fields = ElementFilter.fieldsIn(elements);
        TypeMirror fieldType = fields.iterator().next().asType();
        String className = fieldType.toString();

        if (className.equals(int.class.getName())) {
            return "getInt";
        } else if (className.equals(long.class.getName())) {
            return "getLong";
        } else if (className.equals(String.class.getName())) {
            return "getString";
        }

        return "getBlob";
    }

    private String getPackageName(Element element) {
        return elementUtils.getPackageOf(element).getQualifiedName().toString();
    }

    private String getClassName(Element element, String packageName) {
        int packageLen = packageName.length() + 1;
        return ((TypeElement) element).getQualifiedName().toString().substring(packageLen).replace('.', '$');
    }

    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        processingEnv.getMessager().printMessage(ERROR, message, element);
    }
}
