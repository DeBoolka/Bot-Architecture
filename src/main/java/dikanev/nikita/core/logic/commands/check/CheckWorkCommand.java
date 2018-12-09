package dikanev.nikita.core.logic.commands.check;

import dikanev.nikita.core.api.objects.ApiObject;
import dikanev.nikita.core.api.objects.MessageObject;
import dikanev.nikita.core.api.users.User;
import dikanev.nikita.core.logic.commands.Command;
import dikanev.nikita.core.service.server.URLParameter.Parameter;

import java.util.Map;

public class CheckWorkCommand extends Command {
    public CheckWorkCommand(int id) {
        super(id, true);
    }

    @Override
    protected ApiObject work(User user, Parameter args) {
        return new MessageObject("Ok");
    }
}
