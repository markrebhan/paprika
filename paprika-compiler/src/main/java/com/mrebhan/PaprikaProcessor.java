package com.mrebhan;

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

import static javax.tools.Diagnostic.Kind.ERROR;

@AutoService(Processor.class)
public final class PaprikaProcessor extends AbstractProcessor {
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

        TypeSpec.Builder builder = TypeSpec.classBuilder("SqlScripts").addModifiers(Modifier.PUBLIC);

        ClassName list = ClassName.get("java.util", "List");
        ClassName string = ClassName.get("java.lang", "String");
        ClassName arrayList = ClassName.get("java.util", "ArrayList");
        TypeName listOfString = ParameterizedTypeName.get(list, string);

        MethodSpec.Builder createMethod = MethodSpec.methodBuilder("createSqlStatements")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(listOfString);

        //add a list of strings
        createMethod.addStatement("$T statements = new $T<>()", listOfString, arrayList);

        for (Element element : roundEnv.getElementsAnnotatedWith(Table.class)) {
            // create a table
            StringBuilder stringBuilder = new StringBuilder("CREATE TABLE " + element.getSimpleName() + "( ");

            for (Element classElement : element.getEnclosedElements()) {
                if (classElement.getKind() == ElementKind.FIELD) {
                    // add field to table
                    if (!classElement.getModifiers().contains(Modifier.STATIC)) {
                        stringBuilder.append(classElement.getSimpleName()).
                                append(" ").
                                append(getDataType(classElement)).
                                append(" ");

                        addSqlModifiers(stringBuilder, classElement);
                        stringBuilder.append(", ");
                    }
                }
            }

            stringBuilder.replace(stringBuilder.length() - 2, stringBuilder.length(), ")");

            createMethod.addStatement("statements.add(\"" + stringBuilder.toString() + "\")");
        }

        createMethod.addStatement("return statements");
        builder.addMethod(createMethod.build());

        JavaFile javaFile = JavaFile.builder("com.mrebhan", builder.build())
                .addFileComment("Code Generated for Paprika. Do not modify!")
                .build();

        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            error(null, "Unable to write create script: %s", e.getMessage());
        }

        return true;
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

    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        processingEnv.getMessager().printMessage(ERROR, message, element);
    }
}
