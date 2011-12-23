package org.webbitserver.easyremote.inbound;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class JsonInboundDispatcher extends InboundDispatcher {
    private static final ObjectMapper JSON = new ObjectMapper();

    public JsonInboundDispatcher(Object server, Class<?> clientType) {
        super(server, clientType);
    }

    @Override
    protected InboundMessage unmarshalInboundRequest(String msg) throws IOException {
        return JSON.readValue(msg, ActionArgsTuple.class);
    }

    public static class ActionArgsTuple implements InboundMessage {
        public String action;
        public Object[] args;

        @Override
        public String method() {
            return action;
        }

        @Override
        public Object[] args() {
            return args;
        }
    }

}
