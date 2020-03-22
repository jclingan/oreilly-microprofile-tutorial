package org.acme;

import java.security.Principal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Gauge;
import org.eclipse.microprofile.metrics.annotation.SimplyTimed;
import org.eclipse.microprofile.rest.client.inject.RestClient;

@Counted(absolute = true, name = "FrontendCounter")
@Path("/frontend")
public class FrontendResource {
    int numStudents;

    @Inject
    CircuitBreakerTracker tracker;

    @Inject
    @RestClient
    StudentRestClient student;

    @Inject
    Principal principal;

    @Inject
    JsonWebToken token;

    @RolesAllowed("user")
    @GET
    @Path("/tokeninfo")
    @Produces(MediaType.TEXT_PLAIN)
    public String tokeninfo() {
        String string = "Principal: " + principal.getName();
        string += ",\n";
        string += token.getClaimNames()
        .stream()
        .map(claim -> "\n " + claim + ": " + token.getClaim(claim))
        .collect(Collectors.toList())
        .toString();
        return string;
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return student.hello();
    }

    @RolesAllowed("admin")
    @SimplyTimed(absolute = true, name = "listStudentsTime", displayName = "FrontendResource.listStudents()")
    // @Fallback(value = ListStudentsFallbackHandler.class)
    @Fallback(fallbackMethod = "listStudentsFallback")
    // @Timeout
    // @Retry
    @CircuitBreaker(requestVolumeThreshold = 4, failureRatio = 0.5, delay = 10000, successThreshold = 3)
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("/list")
    public List<String> listStudents() {
        List<String> students;

        tracker.track();

        students = student.listStudents();
        numStudents = students.size();

        return students;
    }

    public List<String> listStudentsFallback() {
        List<String> students = Arrays.asList("Smart Sam", "Genius Gabby", "A-Student Angie", "Intelligent Irene");
        numStudents = students.size();
        return students;
    }

    @Gauge(unit = MetricUnits.NONE, name = "numberOfStudents", absolute = true)
    public int getNumberOfStudents() {
        return numStudents;
    }
}