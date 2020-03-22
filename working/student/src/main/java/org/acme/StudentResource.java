package org.acme;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.config.inject.ConfigProperty;

@Path("/student")
public class StudentResource {
    @Inject
    @ConfigProperty(name = "delay", defaultValue = "2000")
    int delay;

    @Inject
    @ConfigProperty(name = "students")
    List<String> students = new ArrayList<>();

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Howdy";
    }

    @GET
    @Path("/list")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> listStudents() {
        doDelay();
        return students;
    }

    void doDelay() {
        int delayTime;
        try {
            delayTime = delay;
            System.out.println("** Waiting " + delayTime + "ms **");
            TimeUnit.MILLISECONDS.sleep(delayTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}