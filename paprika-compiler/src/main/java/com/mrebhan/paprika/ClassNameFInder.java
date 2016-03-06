package com.mrebhan.paprika;

import java.util.HashSet;
import java.util.Set;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementFilter;

public final class ClassNameFinder {

    public static String getClassName(Element element, String packageName, boolean includePackage) {
        final int packageLen = packageName.length() + 1;

        try {
            String className = ((TypeElement) element).getQualifiedName().toString();

            if (!includePackage) {
                return className.substring(packageLen).replace('.', '$');
            } else {
                return className;
            }
        } catch (ClassCastException e) {
            HashSet<Element> elements = new HashSet<>();
            elements.add(element);
            Set<VariableElement> fields = ElementFilter.fieldsIn(elements);
            TypeMirror fieldType = fields.iterator().next().asType();
            String className = fieldType.toString();

            if (!includePackage) {
                return className.substring(packageLen).replace('.', '$');
            } else {
                return className;
            }
        }
    }
}
