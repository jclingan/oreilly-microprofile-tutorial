package org.acme;

import java.util.Arrays;
import java.util.List;

import org.eclipse.microprofile.faulttolerance.ExecutionContext;
import org.eclipse.microprofile.faulttolerance.FallbackHandler;

/*
   A fallback handler has access to the ExecutionContext, which can
   be used to determine where the failure originated (class and method)
   and the cause of the failure

   Instead of returning a fallback list of students, return the failure
   method and the cause of the failure to give insight into the failure
*/

public class ListStudentsFallbackHandler implements FallbackHandler<List<String>> {
    @Override
    public List<String> handle(ExecutionContext ctx) {
        String failure;
        if (ctx.getFailure() == null) {
            failure = "unknown";
        } else {
            failure = ctx.getFailure().getMessage();
        }

        List<String> aList = Arrays.asList(ctx.getMethod().getName(), failure);
        return aList;
    }
}