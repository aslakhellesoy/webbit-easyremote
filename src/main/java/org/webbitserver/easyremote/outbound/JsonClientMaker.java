package org.webbitserver.easyremote.outbound;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class JsonClientMaker extends DynamicProxyClientMaker {
    private static final ObjectMapper JSON = new ObjectMapper();

    @Override
    public String createMessage(String methodName, Object[] args) throws IOException {
        Map<String, Object> outgoing = new HashMap<String, Object>();
        outgoing.put("action", methodName);
        outgoing.put("args", args);
        StringWriter writer = new StringWriter();
        JSON.writerWithDefaultPrettyPrinter().writeValue(writer, outgoing);
        return writer.toString();
    }
}
