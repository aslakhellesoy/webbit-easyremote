package org.webbitserver.easyremote.inbound;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This exception is thrown when an exception is thrown on the client.
 * The stack trace is constructed from a JavaScript stack trace.
 */
public class JavaScriptException extends RuntimeException {
    public JavaScriptException(String message, List<String> trace) {
        super(message);

        StackTraceElement[] traces = new StackTraceElement[trace.size()];
        for (int i = 0; i < trace.size(); i++) {
            traces[i] = newStackTraceElement(trace.get(i));
        }
        setStackTrace(traces);
    }

    private static final Pattern JS_PATTERN = Pattern.compile("(?:([^\\s]+)\\s*\\((.*)\\)|\\{(.*)\\}\\(\\)@(.*))");
    private static final Pattern FILE_LINE_PATTERN = Pattern.compile("(.*):(\\d+)$");

    static StackTraceElement newStackTraceElement(String jsLine) {
        Matcher matcher = JS_PATTERN.matcher(jsLine);
        if (matcher.matches()) {
            String classMethod = matcher.group(1);
            String fileLine = matcher.group(2);
            if (classMethod == null) {
                classMethod = matcher.group(3);
                fileLine = matcher.group(4);
            }

            String className;
            String methodName;
            {
                String[] classMethodSegments = classMethod.split("\\.");
                if (classMethodSegments.length == 2) {
                    className = classMethodSegments[0];
                    methodName = classMethodSegments[1];
                } else {
                    className = "";
                    methodName = classMethod;
                }
            }

            String fileName;
            int lineNumber;
            {
                Matcher fileLineMatcher = FILE_LINE_PATTERN.matcher(fileLine);
                if (fileLineMatcher.matches()) {
                    fileName = fileLineMatcher.group(1);
                    lineNumber = Integer.parseInt(fileLineMatcher.group(2));
                } else {
                    fileName = fileLine;
                    lineNumber = -1;
                }
            }
            return new StackTraceElement(className, methodName, fileName, lineNumber);
        } else {
            return new StackTraceElement("", jsLine, null, -1);
        }
    }

}
