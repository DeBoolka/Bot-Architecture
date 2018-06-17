package dikanev.nikita.core;

import dikanev.nikita.core.api.exceptions.NoAccessException;
import dikanev.nikita.core.model.commands.Command;
import dikanev.nikita.core.model.commands.user.DeleteUserCommand;
import dikanev.nikita.core.model.jobs.Job;
import dikanev.nikita.core.model.storage.CommandStorage;
import dikanev.nikita.core.model.storage.DBStorage;
import dikanev.nikita.core.model.storage.JobStorage;
import dikanev.nikita.core.model.storage.ServerStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    private static String version = "1.0";

    private static List<Job> jobs;

    public static void  main(String... args) throws Exception{
        init();

        LOG.info("Start");
        start();
    }

    private static void init() throws Exception{
        Properties properties = loadConfiguration();

        version = properties.getProperty("version");

        String urlDB = properties.getProperty("db.url");
        String loginDB = properties.getProperty("db.login");
        String passwordDB = properties.getProperty("db.password");
        DBStorage.getInstance().init(urlDB, loginDB, passwordDB);

        int portServer = Integer.parseInt(properties.getProperty("server.port", "9090"));
        ServerStorage.getInstance().init(portServer);

        jobs = JobStorage.getInstance().getJobs();
    }

    private static Properties loadConfiguration() {
        Properties properties = new Properties();
        try (InputStream is = Application.class.getResourceAsStream("/config.properties")) {
            properties.load(is);
        } catch (IOException e) {
            LOG.error("Can't load properties file", e);
            throw new IllegalStateException(e);
        }

        return properties;
    }

    private static void start() throws Exception{

        try {
            System.out.println(CommandStorage.getInstance().getCommand("user/register").run(null));
            Command command = new DeleteUserCommand(2);
            System.out.println(command.getId());
        } catch (NoAccessException e) {
            LOG.warn(e.getMessage());
        }

        if (jobs.isEmpty()) {
            LOG.warn("No jobs configured. Exist");
            return;
        }

        while (true) {
            for (Job job : jobs) {
                try {
                    job.doJob();
                } catch (Exception e) {
                    LOG.error("Something wrong", e);
                }
            }

            TimeUnit.SECONDS.sleep(1);
            return;
        }
    }

}
