package com.mrebhan.paprika;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;

import static com.mrebhan.paprika.consts.Constants.PAPRIKA_PACKAGE;
import static com.mrebhan.paprika.consts.Constants.PAPRIKA_SQL_SCRIPTS_CLASS_NAME;
import static javax.tools.Diagnostic.Kind.ERROR;

//TODO add support for having _id in class or generate it

@AutoService(Processor.class)
public final class PaprikaProcessor extends AbstractProcessor {
    private static final ClassName SQL_SCRIPTS = ClassName.get("com.mrebhan.paprika.internal", "SqlScripts");
    private static final ClassName LIST = ClassName.get("java.util", "List");
    private static final ClassName STRING = ClassName.get("java.lang", "String");
    private static final ClassName ARRAY_LIST = ClassName.get("java.util", "ArrayList");

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

        createMethod.addStatement("$T statements = new $T<>()", listOfString, ARRAY_LIST);

        for (Element element : roundEnv.getElementsAnnotatedWith(Table.class)) {

            Map<String, Element> elementMappers = new LinkedHashMap<>();

            for (Element classElement : element.getEnclosedElements()) {
                if (classElement.getKind() == ElementKind.FIELD && !classElement.getModifiers().contains(Modifier.STATIC)) {
                    elementMappers.put(classElement.getSimpleName().toString(), classElement);
                }
            }

            String createStatement = new SqlCreateStatement(elementMappers, element).toString();
            createMethod.addStatement("statements.add(\"" + createStatement + "\")");

            writeToFiler(new MapperClassBuilder(elementMappers, element, getPackageName(element)).build());
        }

        createMethod.addStatement("return statements");
        builder.addMethod(createMethod.build());

        JavaFile javaFile = JavaFile.builder(PAPRIKA_PACKAGE, builder.build())
                .addFileComment("Code Generated for Paprika. Do not modify!")
                .build();

        writeToFiler(javaFile);

        return true;
    }

    private void writeToFiler(JavaFile javaFile) {
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            error(null, "Unable to write create script: %s", e.getMessage());
        }
    }

    private String getPackageName(Element element) {
        return elementUtils.getPackageOf(element).getQualifiedName().toString();
    }
    private void error(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }
        processingEnv.getMessager().printMessage(ERROR, message, element);
    }
}
