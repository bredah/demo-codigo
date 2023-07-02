package org.example.utils;

import org.junit.jupiter.api.DisplayNameGenerator;

import java.lang.reflect.Method;
import java.util.StringJoiner;

public class DisplayTestName extends DisplayNameGenerator.Standard {

    private static final String PARENTHESES_REGEX = "\\(.*\\)";

    @Override
    public String generateDisplayNameForClass(Class<?> testClass) {
        return super.generateDisplayNameForClass(testClass);
    }

    @Override
    public String generateDisplayNameForNestedClass(Class<?> nestedClass) {
        return this.fixDisplayClassName(super.generateDisplayNameForNestedClass(nestedClass));
    }

    @Override
    public String generateDisplayNameForMethod(Class<?> testClass, Method testMethod) {
        return this.fixDisplayTestName(testMethod.getName());
    }

    private String fixDisplayClassName(String context) {
        String[] testClassName = context.split("(?<!^)(?=[A-Z])");
        return String.join(" ", testClassName);
    }

    private String fixDisplayTestName(String context) {
        String sentences[] = context.split("_");
        StringJoiner result = new StringJoiner(" - ");
        for (String sentence : sentences) {
            StringBuilder newSentence = new StringBuilder();
            for (int i = 0; i < sentence.length(); i++) {
                if (i == 0) {
                    newSentence.append(Character.toUpperCase(sentence.charAt(i)));
                } else if (Character.isUpperCase(sentence.charAt(i))) {
                    newSentence.append(' ');
                    newSentence.append(Character.toLowerCase(sentence.charAt(i)));
                } else {
                    newSentence.append(sentence.charAt(i));
                }
            }
            result.add(newSentence.toString());
        }
        return result.toString().replaceAll(PARENTHESES_REGEX, "");
    }
}
