package org.webbitserver.easyremote.inbound;

import org.junit.Test;
import org.webbitserver.WebSocketConnection;
import org.webbitserver.easyremote.ClientException;
import org.webbitserver.stub.StubConnection;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.webbitserver.easyremote.inbound.InboundDispatcher.newStackTraceElement;

public class JsonInboundDispatcherTest {
    @Test
    public void throws_exception_with_javascript_stack_trace() throws Throwable {
        String clientException = "" +
                "{\"action\":\"__reportClientException\"," +
                "\"args\":[\"The error message\",[" +
                "" +
                "\"Object.stringify (native)\"," +
                "\"Object.say (http://aslak.us.drwholdings.com:9877/chatroom.js:50:22)\"," +
                "\"invokeOnTarget (http://aslak.us.drwholdings.com:9877/webbit.easyremote.js:68:28)\"," +
                "\"{anonymous}()@http://aslak.us.drwholdings.com:9877/webbit.easyremote.js:98:21\"," +
                "\"jsonParser (http://aslak.us.drwholdings.com:9877/webbit.easyremote.js:34:9)\"," +
                "\"WebSocket.<anonymous> (http://aslak.us.drwholdings.com:9877/webbit.easyremote.js:93:9)" +
                "" +
                "\"]]}";
        InboundDispatcher dispatcher = new JsonInboundDispatcher(this, SomeClient.class);
        WebSocketConnection connection = new StubConnection();
        try {
            dispatcher.dispatch(connection, clientException, this);
            fail();
        } catch (ClientException e) {
            StringWriter trace = new StringWriter();
            e.printStackTrace(new PrintWriter(trace));
            String expected = "" +
                    "org.webbitserver.easyremote.ClientException: The error message\n" +
                    "\tat Object.stringify(native)\n" +
                    "\tat Object.say(http://aslak.us.drwholdings.com:9877/chatroom.js:50:22)\n" +
                    "\tat .invokeOnTarget(http://aslak.us.drwholdings.com:9877/webbit.easyremote.js:68:28)\n" +
                    "\tat .anonymous(http://aslak.us.drwholdings.com:9877/webbit.easyremote.js:98:21)\n" +
                    "\tat .jsonParser(http://aslak.us.drwholdings.com:9877/webbit.easyremote.js:34:9)\n" +
                    "\tat WebSocket.<anonymous>(http://aslak.us.drwholdings.com:9877/webbit.easyremote.js:93:9)\n" +
                    "";
            assertEquals(expected, trace.toString());
        }
    }

    @Test
    public void createsTraceForRegularLine() {
        assertStackTrace("Object.say (http://aslak.us.drwholdings.com:9877/chatroom.js:50:22)", "Object", "say", "http://aslak.us.drwholdings.com:9877/chatroom.js:50", 22);
    }

    private void assertStackTrace(String jsLine, String className, String methodName, String fileName, int line) {
        StackTraceElement e = newStackTraceElement(jsLine);
        assertEquals(line, e.getLineNumber());
        assertEquals(fileName, e.getFileName());
        assertEquals(className, e.getClassName());
        assertEquals(methodName, e.getMethodName());
    }


    private interface SomeClient {
    }
}
