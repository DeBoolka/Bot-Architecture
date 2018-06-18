package dikanev.nikita.core.model.storage;

import dikanev.nikita.core.server.handlers.CallbackRequestHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerStorage {

    private static final Logger LOG = LoggerFactory.getLogger(ServerStorage.class);

    private static ServerStorage ourInstance = new ServerStorage();

    private Server server = null;

    private ServletContextHandler contextHandler = null;

    public static ServerStorage getInstance() {
        return ourInstance;
    }

    private final int PORT = 9090;

    public void start(int port) throws Exception {
        try {
            contextHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
            contextHandler.setContextPath("/api/");
            connectServlet();

            server = new Server(port);
            server.setHandler(contextHandler);
            server.start();

            LOG.info("Start Server");
        } catch (Exception e) {
            LOG.error("The server could not be started.");
            throw new Exception("The server could not be started. PORT = " + port + "\nMessage: " + e.getMessage());
        }
    }

    public void start() throws Exception{
        start(this.PORT);
    }

    //Возращает текущий сервер
    public Server getServer() {
        return server;
    }

    /*Add servlet*/
    private void connectServlet(){
        contextHandler.addServlet(new ServletHolder(new CallbackRequestHandler()), "/*");
    }
}
