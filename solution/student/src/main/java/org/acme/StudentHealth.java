package org.acme;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;
import org.eclipse.microprofile.health.Readiness;

@Liveness
@Readiness
public class StudentHealth implements HealthCheck {
    @Override
    public HealthCheckResponse call() {
        double random = Math.random();

        return HealthCheckResponse
            .named("StudentLivenessReadiness")
            .state(random < .50 ? true : false)
            .withData("random", "" + random)
            .build();
    }
}
