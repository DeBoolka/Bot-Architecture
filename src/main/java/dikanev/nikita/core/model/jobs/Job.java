package dikanev.nikita.core.model.jobs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface Job {
    void doJob() throws Exception;
}
