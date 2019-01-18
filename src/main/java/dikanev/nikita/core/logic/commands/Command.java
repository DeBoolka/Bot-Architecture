package dikanev.nikita.core.logic.commands;

import dikanev.nikita.core.api.exceptions.ApiException;
import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.exceptions.NoAccessException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.commands.CommandController;
import dikanev.nikita.core.service.server.CommandParser;
import dikanev.nikita.core.service.server.parameter.Parameter;
import dikanev.nikita.core.service.storage.CommandStorage;
import org.checkerframework.checker.nullness.compatqual.NonNullType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public abstract class Command {

    private static final Logger LOG = LoggerFactory.getLogger(Command.class);

    protected static final String HEADER_ENTER_POINT = "O-Enter-Method";

    private int id;

    private boolean isFreeAccess = false;

    private CommandParser commandParser = null;

    public Command(int id){
        this.id = id;
    }

    public Command(int id, boolean isFreeAccess){
        this.id = id;
        setFreeAccess(isFreeAccess);
    }

    //Предварительные проверки перед запуском метода
    public ApiObject run(@NonNullType Parameter args, CommandParser commandParser) throws Exception{
        String hash = args.getFOrDefault("token", "");
        if(hash.equals("") && !hasFreeAccess()){
            throw new NoAccessException("Operation id " + getId()
                    + ", name " + CommandController.getInstance().getName(getId()));
        }

        User user;
        if (hasFreeAccess()) {
            user = new User(User.DEFAULT_ID, User.DEFAULT_GROUP);
        } else {
            user = new User(hash);
            if (!user.hasRightByGroup(getId())) {
                throw new NoAccessException("Operation id " + getId()
                        + ", name " + CommandController.getInstance().getName(getId()));
            }

        }

        try {
            checkingParameters(args);
            if (commandParser.headers.contains("O-Enter-Method")) {
                return ChoiceStartPointOfWork(user, args, commandParser);
            }
            return work(user, args);
        } catch (IllegalStateException | NumberFormatException e) {
            return new ExceptionObject(new ApiException(400, e.getMessage()));
        }  catch (InvalidParametersException | NoSuchFieldException e) {
            return new ExceptionObject(new InvalidParametersException(e.getMessage()));
        }
    }

    protected ApiObject ChoiceStartPointOfWork(User user, Parameter params, CommandParser commandParser) throws NoSuchFieldException, ApiException, SQLException {
        return work(user, params);
    }

    private void checkingParameters(Parameter parameter) throws InvalidParametersException {
        PreparedParameter preparation = setupParameters(parameter);
        if (preparation == null
                || preparation.validInputParameters == null
                || preparation.validInputParameters.length == 0) {
            return;
        }

        for (String[] params : preparation.validInputParameters) {
            if (parameter.containsAll(params)) {
                for (String param: params) {
                    ParameterHandler pp = preparation.consumerParameters.get(param);
                    if (pp == null) {
                        continue;
                    }

                    String err = pp.processing(parameter, parameter.get(param));
                    if (err != null) {
                        throw new InvalidParametersException(err);
                    }
                }
                return;
            }
        }

        throw new InvalidParametersException("The required parameters were not found.");
    }

    //Перегружаемый метод с работой команды
    protected abstract ApiObject work(User user, Parameter params) throws ApiException, SQLException, NoSuchFieldException;

    //Перегружаемый метод с обработкой аргументов
    protected abstract PreparedParameter setupParameters(Parameter params) throws InvalidParametersException;

    public Command setFreeAccess(boolean isFreeAccess) {
        this.isFreeAccess = isFreeAccess;
        return this;
    }

    private boolean hasFreeAccess() {
        return isFreeAccess;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){ this.id = id;}

    public CommandParser getCommandParser() {
        return commandParser;
    }

    public void setCommandParser(CommandParser commandParser) {
        this.commandParser = commandParser;
    }

    protected class PreparedParameter {
        String[][] validInputParameters;

        Map<String, ParameterHandler> consumerParameters;

        public PreparedParameter(String[][] validInputParameters) {
            this.validInputParameters = validInputParameters;
        }

        public PreparedParameter(String[][] validInputParameters, Map<String, ParameterHandler> consumerParameters) {
            this.validInputParameters = validInputParameters;
            this.consumerParameters = consumerParameters;
        }

        public PreparedParameter(String[] validInputParameters, Map<String, ParameterHandler> consumerParameters) {
            this.validInputParameters = new String[][]{validInputParameters};
            this.consumerParameters = consumerParameters;
        }

        public PreparedParameter(String... validInputParameters) {
            this.validInputParameters = new String[][]{validInputParameters};
        }
    }

    public interface ParameterHandler{
        String processing(Parameter parameter, List<String> val);
    }
}
