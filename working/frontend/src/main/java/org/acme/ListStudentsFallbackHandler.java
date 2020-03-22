package org.acme;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricRegistry.Type;
import org.eclipse.microprofile.metrics.annotation.RegistryType;

/*
A fallback handler has access to the ExecutionContext, which can
be used to determine where the failure originated (class and method)
and the cause of the failure
Print the cause of the failure to stdout, and return a list of "top students"
*/
public class ListStudentsFallbackHandler implements FallbackHandler<List<String>> {
    @Inject
    @RegistryType(type = Type.APPLICATION)
    MetricRegistry registry;

    @Inject
    CircuitBreakerTracker tracker;

    @Override
    public List<String> handle(ExecutionContext ctx) {
        List<String> students = Arrays.asList("Smart Sam", "Genius Gabby", "AStudent Angie", "Intelligent Irene");
        String failure;
        registry.counter("listStudentsCounter").inc();
        if (ctx.getFailure() == null) {
            failure = "unknown";
        } else {
            failure = ctx.getFailure().getMessage();
        }
        System.out.println("Exception " + failure);
        System.out.println("listStudentsFallbackCounter: "
                + registry.counter("ft.org.acme.FrontendResource.listStudents.fallback.calls.total").getCount());
        tracker.track();
        return students;
    }
}