package dikanev.nikita.core.model.storage;

import dikanev.nikita.core.model.jobs.Job;
import dikanev.nikita.core.model.jobs.SendNotificationJob;

import java.util.ArrayList;
import java.util.List;

public class JobStorage {
    private static JobStorage ourInstance = new JobStorage();

    public static JobStorage getInstance() {
        return ourInstance;
    }

    private List<Job> jobs = new ArrayList<>();

    private JobStorage(){
        initJob();
    }

    private void initJob() {
        jobs.add(new SendNotificationJob());
    }

    public List<Job> getJobs(){
        return jobs;
    }
}
