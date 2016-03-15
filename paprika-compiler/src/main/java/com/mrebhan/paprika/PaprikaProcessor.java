package com.mrebhan.paprika;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.HashMap;
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

import sun.rmi.runtime.Log;

import static com.mrebhan.paprika.consts.Constants.PAPRIKA_PACKAGE;
import static com.mrebhan.paprika.consts.Constants.PAPRIKA_SQL_SCRIPTS_CLASS_NAME;

//TODO add support for having _id in class or generate it

@AutoService(Processor.class)
public final class PaprikaProcessor extends AbstractProcessor {
    private static final ClassName SQL_SCRIPTS = ClassName.get("com.mrebhan.paprika.internal", "SqlScripts");

    private Elements elementUtils;
    private Types typeUtils;
    private Filer filer;

    private Map<Element, Map<String, Element>> tableMap = new HashMap<>();

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
        types.add(Default.class.getCanonicalName());
        types.add(ForeignObject.class.getCanonicalName());
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

        new Logger(processingEnv.getMessager());

        isProcessed = true;

        for (Element element : roundEnv.getElementsAnnotatedWith(Table.class)) {
            tableMap.put(element, getElementMap(element));
        }

        TypeSpec.Builder builder = TypeSpec.classBuilder(PAPRIKA_SQL_SCRIPTS_CLASS_NAME).addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                .addSuperinterface(SQL_SCRIPTS);

        final Version versionChecker = new Version();
        final SqlUpgradeScripts upgradeScripts = new SqlUpgradeScripts(versionChecker);
        final SqlCreateScripts createScripts = new SqlCreateScripts(upgradeScripts);
        final SqlSelectScripts selectScripts = new SqlSelectScripts(builder, tableMap);

        for (Element element : tableMap.keySet()) {
            Table table = element.getAnnotation(Table.class);
            constructDataMappings(element, table.version(), createScripts, upgradeScripts, selectScripts);
        }

        builder.addMethod(createScripts.buildMethod());
        builder.addMethod(upgradeScripts.buildMethod());
        builder.addMethod(versionChecker.buildMethod());
        selectScripts.build();

        JavaFile javaFile = JavaFile.builder(PAPRIKA_PACKAGE, builder.build())
                .addFileComment("Code Generated for Paprika. Do not modify!")
                .build();

        writeToFiler(javaFile);

        return true;
    }

    private Map<String, Element> getElementMap(Element parent) {
        Map<String, Element> elementMap = new HashMap<>();

        for (Element classElement : parent.getEnclosedElements()) {
            if (classElement.getKind() == ElementKind.FIELD && !classElement.getModifiers().contains(Modifier.STATIC)) {
                Logger.logNote("Found column for  " + parent.getSimpleName() + " : " + classElement.getSimpleName());
                elementMap.put(classElement.getSimpleName().toString(), classElement);
            }
        }

        return elementMap;
    }

    private void constructDataMappings(Element element, int version, SqlCreateScripts createScripts, SqlUpgradeScripts upgradeScripts, SqlSelectScripts sqlSelectScripts) {

        Map<String, Element> elementMappers = tableMap.get(element);

        createScripts.addCreateStatement(elementMappers, element);
        sqlSelectScripts.addSelectStatement(element, getPackageName(element));

        if (version > 1) {
            upgradeScripts.addUpgradeNewTable(elementMappers, element, version);
        }

        writeToFiler(new MapperClassBuilder(elementMappers, element, tableMap, getPackageName(element)).build());
    }


    private void writeToFiler(JavaFile javaFile) {
        try {
            javaFile.writeTo(filer);
        } catch (IOException e) {
            Logger.logError(null, "Unable to write create script: %s", e.getMessage());
        }
    }

    private String getPackageName(Element element) {
        return elementUtils.getPackageOf(element).getQualifiedName().toString();
    }
}
