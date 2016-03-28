package com.mrebhan.paprika;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.annotation.Annotation;

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

    public static void logError(Element element, String message, Exception e) {
        StringWriter stackTrace = new StringWriter();
        e.printStackTrace(new PrintWriter(stackTrace));
        logError(element, message, stackTrace);
    }
}
