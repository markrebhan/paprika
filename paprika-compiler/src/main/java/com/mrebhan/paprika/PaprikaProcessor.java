package com.mrebhan.paprika;

import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;

import java.io.IOException;
import java.util.HashMap;
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

//TODO add support for having _id in class or generate it

@AutoService(Processor.class)
public final class PaprikaProcessor extends AbstractProcessor {

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
        types.add(Default.class.getCanonicalName());
        types.add(ForeignObject.class.getCanonicalName());
        types.add(Ignore.class.getCanonicalName());
        types.add(NonNull.class.getCanonicalName());
        types.add(PrimaryKey.class.getCanonicalName());
        types.add(Table.class.getCanonicalName());
        types.add(Unique.class.getCanonicalName());

        return types;
    }

    private PaprikaMappings paprikaMappings;

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

        if (isProcessed) {
            return true;
        }

        new Logger(processingEnv.getMessager());

        isProcessed = true;

        Map<Element, Map<String, Element>> tableMap = new HashMap<>();

        for (Element element : roundEnv.getElementsAnnotatedWith(Table.class)) {
            tableMap.put(element, getElementMap(element));
        }

        paprikaMappings = PaprikaMappings.getInstance(tableMap);

        for (Element element : tableMap.keySet()) {
            Table table = element.getAnnotation(Table.class);
            writeToFiler(paprikaMappings.constructDataMappings(element, table.version(), getPackageName(element)));
        }

        writeToFiler(JavaFile.builder(PAPRIKA_PACKAGE, paprikaMappings.build())
                .addFileComment("Code Generated for Paprika. Do not modify!")
                .build());

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
