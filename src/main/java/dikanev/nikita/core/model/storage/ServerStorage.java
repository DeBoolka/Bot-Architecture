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

    final int port = 90909;

    public void init(int port) throws Exception {
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
            throw new Exception("The server could not be started. port = " + port + "\nMessage: " + e.getMessage());
        }
    }

    public void init() throws Exception{
        init(this.port);
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
