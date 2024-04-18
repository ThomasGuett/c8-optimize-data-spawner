package io.camunda.app.service;

import io.camunda.zeebe.client.api.response.ActivatedJob;
import io.camunda.zeebe.client.api.worker.JobClient;
import io.camunda.zeebe.client.api.worker.JobHandler;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class Dataspawner implements JobHandler {
    private Integer timeMin = Integer.valueOf(System.getenv("JOBTIME_MIN"));
    private Integer timeMax = Integer.valueOf(System.getenv("JOBTIME_MAX"));

    @Override
    public void handle(final JobClient client, final ActivatedJob job) {
        // provide random wait value based on system env or default
        timeMax = (null != timeMax && timeMax >= 0) ? timeMax : 1000;
        timeMin = (null != timeMin && timeMin <= timeMax) ? timeMin : 0;
        int timeDelay = ThreadLocalRandom.current().nextInt(timeMin, timeMax + 1);

        // optional variable handling
        Map<String, Object> variables = job.getVariablesAsMap();
        // trigger if error to be thrown
        String strFailJobTypes = variables.containsKey("failJobs") ? String.valueOf(variables.get("failJobs")).replace(" ", "") : "";
        List<String> failJobTypes = Arrays.asList(strFailJobTypes.split(","));
        if (failJobTypes.contains(job.getType())) {
            // found job type in fail list
            System.out.println("failing jobType as requested: " + job.getType() + " instance: " + job.getProcessInstanceKey());
            client.newThrowErrorCommand(job).errorCode("spawner").errorMessage("as requested").send().join();
            client.newFailCommand(job);
        } else {
            // wait for time delay to complete
            try {
                System.out.println("handling jobType: " + job.getType() + " delay: " + timeDelay + "ms instance: " + job.getProcessInstanceKey());
                TimeUnit.MILLISECONDS.sleep(timeDelay);
            } catch(InterruptedException error) {
                System.out.print("Silent Error: Sleep not possible due to Interrupted Exception: " + error.getMessage());
            }
            client.newCompleteCommand(job).variables(variables).send().join();
        }
    }
}
