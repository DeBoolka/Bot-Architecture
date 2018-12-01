package dikanev.nikita.core.service.server.handlers;

import dikanev.nikita.core.api.exceptions.NoAccessException;
import dikanev.nikita.core.api.exceptions.NotFoundException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.storage.CommandStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/SrvltCallbackRequest")
public class CallbackRequestHandler extends HttpServlet {

    private static final Logger LOG = LoggerFactory.getLogger(CallbackRequestHandler.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        handler(req, resp);
    }

    private void handler(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json; charset=UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setStatus( HttpServletResponse.SC_OK );

        String commandName = req.getPathInfo().substring(1);
        ApiObject responseObject;

        try (PrintWriter pr = resp.getWriter()) {
            try {
                Command responseCommand = CommandStorage.getInstance().getCommand(commandName);
                if (responseCommand == null) {
                    responseObject = new ExceptionObject(new NotFoundException("Command not found"));
                } else {
                    responseObject = responseCommand.run(req.getParameterMap());
                }

                pr.write(responseObject.getJson());
            } catch (NoAccessException e) {
                pr.write(new ExceptionObject(e).getJson());
            }
        } catch (IOException ex) {
            LOG.warn("The client could not be contacted.");
            resp.setStatus( HttpServletResponse.SC_BAD_REQUEST );
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private void printTextResponse(HttpServletResponse resp, String text) {
        try (PrintWriter printWriter = resp.getWriter()) {
            printWriter.write(text);
        } catch (IOException ex) {
            LOG.warn("The client could not be contacted. Exception: " + ex.getMessage());
        }
    }
}
