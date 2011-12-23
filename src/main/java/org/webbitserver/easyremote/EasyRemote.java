package org.webbitserver.easyremote;

import org.webbitserver.WebSocketConnection;
import org.webbitserver.WebSocketHandler;
import org.webbitserver.easyremote.inbound.JsonInboundDispatcher;
import org.webbitserver.easyremote.inbound.InboundDispatcher;
import org.webbitserver.easyremote.outbound.ClientMaker;
import org.webbitserver.easyremote.outbound.Exporter;
import org.webbitserver.easyremote.outbound.JsonClientMaker;

@SuppressWarnings({"unchecked"})
public class EasyRemote<CLIENT> implements WebSocketHandler {

    public static final String CLIENT_KEY = EasyRemote.class.getPackage().getName() + ".client";

    private final Class<CLIENT> clientType;
    private final ClientMaker clientMaker;
    private final Server<CLIENT> server;
    private final InboundDispatcher in;

    public EasyRemote(Class<CLIENT> clientType, Server<CLIENT> server, ClientMaker clientMaker) {
        this.clientType = clientType;
        this.clientMaker = clientMaker;
        this.in = new JsonInboundDispatcher(server, clientType);
        this.server = server;
    }

    public static <T> WebSocketHandler easyRemote(Class<T> clientType, Server<T> server) {
        return new EasyRemote<T>(clientType, server, new JsonClientMaker());
    }

    public static <T> WebSocketHandler easyRemote(Class<T> clientType, Server<T> server, ClientMaker clientMaker) {
        return new EasyRemote<T>(clientType, server, clientMaker);
    }

    @Override
    public void onOpen(WebSocketConnection connection) throws Exception {
        CLIENT client = clientMaker.implement(clientType, connection);
        ((Exporter) client).__exportMethods(in.availableMethods());
        connection.data(CLIENT_KEY, client);
        server.onOpen(connection, client);
    }

    @Override
    public void onMessage(WebSocketConnection connection, String msg) throws Throwable {
        in.dispatch(connection, msg, connection.data(CLIENT_KEY));
    }

    @Override
    public void onMessage(WebSocketConnection webSocketConnection, byte[] bytes) throws Throwable {
    }

    @Override
    public void onPong(WebSocketConnection webSocketConnection, String s) throws Throwable {
    }

    @Override
    public void onClose(WebSocketConnection connection) throws Exception {
        server.onClose(connection, (CLIENT) connection.data(CLIENT_KEY));
    }
}
