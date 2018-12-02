package dikanev.nikita.core.logic.commands;

import dikanev.nikita.core.api.exceptions.InvalidParametersException;
import dikanev.nikita.core.api.exceptions.NoAccessException;
import dikanev.nikita.core.api.exceptions.UnidentifiedException;
import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.ExceptionObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.controllers.commands.CommandController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class Command {

    private static final Logger LOG = LoggerFactory.getLogger(Command.class);

    private int id;

    private boolean isFreeAccess = false;

    public Command(int id){
        this.id = id;
    }

    public Command(int id, boolean isFreeAccess){
        this.id = id;
        setFreeAccess(isFreeAccess);
    }

    //Предварительные проверки перед запуском метода
    public ApiObject run(Map<String, String[]> args) throws NoAccessException{
        if (args == null) {
            args = new LinkedHashMap<>();
        }

        String hash = args.getOrDefault("token", new String[]{""})[0];
        if(hash.equals("") && !hasFreeAccess()){
            throw new NoAccessException("Operation id " + getId());
        }

        User user;
        if (hasFreeAccess()) {
            user = new User(User.DEFAULT_ID, User.DEFAULT_GROUP);
        } else {
            user = new User(hash);
            if (!user.hasRightByGroup(getId())) {
                throw new NoAccessException("Operation id " + getId());
            }
        }

        try {
            return work(user, args);
        } catch (Exception e) {
            LOG.warn("Unknown error in the command: ", e);
            return new ExceptionObject(new UnidentifiedException(e.getMessage()));
        }
    }

    //Перегружаемый метод с работой команды
    protected abstract ApiObject work(User user, Map<String, String[]> args);

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

    //Получение имени команды по id
    public static String getName(int id) {
        try {
            return CommandController.getInstance().getName(id);
        } catch (SQLException e) {
            LOG.warn(e.getSQLState());
        }

        return "";
    }

    //Получение всех команд
    //TODO: Удалить
    public static Map<Integer, String> getCommands(){
        try {
            return CommandController.getInstance().getCommands();
        } catch (SQLException e) {
            LOG.warn(e.getSQLState());
        }

        return new HashMap<>();
    }

    //Возращает одно слово из мапа
    protected String getString(String parameter, Map<String, String[]> parameters) throws InvalidParametersException {
        String[] parameterArray = parameters.get(parameter);
        if (parameterArray == null || parameterArray.length > 1) {
            throw new InvalidParametersException(parameter);
        }

        return parameterArray[0];
    }

    //Возращает одно слово из мапа
    protected int getInt(String parameter, Map<String, String[]> parameters) throws InvalidParametersException {
        String parameterString = getString(parameter, parameters);

        try {
            return Integer.parseInt(parameterString);
        } catch (NumberFormatException e) {
            throw new InvalidParametersException(parameter);
        }
    }

    //Проверяет наличие параметра
    protected boolean hasParameter(String parameter, Map<String, String[]> parameters) {
        return parameters.get(parameter) != null;
    }
}
