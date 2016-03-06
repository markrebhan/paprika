package com.mrebhan.paprika;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

public final class Logger {

    private static Logger instance;
    private final Messager messager;

    public Logger(Messager messager) {
        this.messager = messager;
        instance = this;
    }

    public static Logger getInstance() {
        return instance;
    }

    public static void logError(Element element, String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }

        instance.messager.printMessage(Diagnostic.Kind.ERROR, message, element);
    }

    public static void logNote(String message, Object... args) {
        if (args.length > 0) {
            message = String.format(message, args);
        }

        instance.messager.printMessage(Diagnostic.Kind.NOTE, message);
    }
}
