package org.acme;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.eclipse.microprofile.metrics.Gauge;
import org.eclipse.microprofile.metrics.MetricID;
import org.eclipse.microprofile.metrics.MetricRegistry;
import org.eclipse.microprofile.metrics.MetricRegistry.Type;
import org.eclipse.microprofile.metrics.annotation.RegistryType;

@Dependent
public class CircuitBreakerTracker {
    @Inject
    @RegistryType(type = Type.APPLICATION)
    MetricRegistry registry;

    public Map<String, String> track() {
        HashMap<String, String> map = new HashMap<>();
        MetricID id = new MetricID("ft.org.acme.FrontendResource.listStudents.circuitbreaker.closed.total");
        Gauge gauge = registry.getGauges().get(id);
        if (gauge != null) {
            map.put("CBClosedTime", "" + Duration.ofNanos((long) gauge.getValue()).toMillis() + "ms\n");
        }
        id = new MetricID("ft.org.acme.FrontendResource.listStudents.circuitbreaker.halfOpen.total");
        gauge = registry.getGauges().get(id);
        if (gauge != null) {
            map.put("CBHalfOpenTime", "" + Duration.ofNanos((long) gauge.getValue()).toMillis() + "ms\n");
        }
        id = new MetricID("ft.org.acme.FrontendResource.listStudents.circuitbreaker.open.total");
        gauge = registry.getGauges().get(id);
        if (gauge != null) {
            map.put("CBOpenTime", "" + Duration.ofNanos((long) gauge.getValue()).toMillis() + "ms\n");
        }
        map.put("CBSucceededCount",
                "" + registry.counter("ft.org.acme.FrontendResource.listStudents.circuitbreaker.callsSucceeded.total")
                        .getCount() + "\n");
        map.put("CBPreventedCount",
                "" + registry.counter("ft.org.acme.FrontendResource.listStudents.circuitbreaker.callsPrevented.total")
                        .getCount() + "\n");
        map.forEach((key, value) -> System.out.print(key + ": " + value));
        System.out.println();
        return map;
    }
}