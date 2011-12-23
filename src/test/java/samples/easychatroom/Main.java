package samples.easychatroom;

import org.webbitserver.WebServer;
import org.webbitserver.easyremote.outbound.CsvClientMaker;
import org.webbitserver.handler.EmbeddedResourceHandler;
import org.webbitserver.handler.StaticFileHandler;
import org.webbitserver.handler.logging.LoggingHandler;
import org.webbitserver.handler.logging.SimpleLogSink;

import static org.webbitserver.WebServers.createWebServer;
import static org.webbitserver.easyremote.EasyRemote.easyRemote;

public class Main {

    public static void main(String[] args) throws Exception {
        WebServer webServer = createWebServer(9877)
                .add(new LoggingHandler(new SimpleLogSink(ChatServer.USERNAME_KEY)))
                .add("/chatsocket", easyRemote(ChatClient.class, new ChatServer(), new CsvClientMaker()))
                .add(new StaticFileHandler("./src/test/java/samples/easychatroom/content"))
                .add(new EmbeddedResourceHandler("org/webbitserver/easyremote"))
                .start();

        System.out.println("Chat room running on: " + webServer.getUri());
    }

}