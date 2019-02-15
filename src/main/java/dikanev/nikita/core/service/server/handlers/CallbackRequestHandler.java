package dikanev.nikita.core.service.server.handlers;

import dikanev.nikita.core.api.exceptions.ApiException;
import dikanev.nikita.core.api.exceptions.NoAccessException;
import dikanev.nikita.core.api.exceptions.NotFoundException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.server.CommandParser;
import dikanev.nikita.core.service.item.parameter.HttpGetParameter;
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
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setHeader("Access-Control-Allow-Origin", "*");
        resp.setStatus( HttpServletResponse.SC_OK );

        CommandParser commandParser = commandParse(req);
        ApiObject responseObject;
        int status = 200;

        try (PrintWriter pr = resp.getWriter()) {
            try {
                Command responseCommand = CommandStorage.getInstance().getCommand(commandParser.getRoute());
                responseObject = getResponseObject(responseCommand, commandParser, req);

                pr.write(responseObject.getJson());
                if (responseObject.getType().equals("error")) {
                    status = ((ExceptionObject) responseObject).getCode();
                }
            } catch (NoAccessException e) {
                pr.write(new ExceptionObject(e).getJson());
                status = 403;
            } catch (Exception e) {
                LOG.error("Server error: ", e);
                pr.write(new ExceptionObject(new ApiException(500, "Server error.")).getJson());
                status = 500;
            }
            resp.setStatus(status);
        } catch (IOException ex) {
            LOG.warn("The client could not be contacted. Error: ", ex);
            resp.setStatus( HttpServletResponse.SC_BAD_REQUEST );
        }
    }

    private ApiObject getResponseObject(Command responseCommand, CommandParser commandParser, HttpServletRequest req) throws Exception {
        if (responseCommand == null) {
            return new ExceptionObject(new NotFoundException("Command not found."));
        }
        commandParser.addHeaders(responseCommand.getCommandParser().headers);

        HttpGetParameter parameters = new HttpGetParameter(req.getParameterMap());
        if (commandParser.headers.contains("O-Parameters")) {
            parameters.add(commandParser.headers.getF("O-Parameters"));
        }

        return responseCommand.run(parameters, commandParser);

    }

    private CommandParser commandParse(HttpServletRequest req) {
        CommandParser commandParser = new CommandParser(req.getPathInfo().substring(1));
        commandParser.parseHttpHeaders(req);

        return commandParser;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }

    private void printTextResponse(HttpServletResponse resp, String text) {
        try (PrintWriter printWriter = resp.getWriter()) {
            printWriter.write(text);
        } catch (IOException ex) {
            LOG.warn("The client could not be contacted. Exception: ", ex);
        }
    }
}
