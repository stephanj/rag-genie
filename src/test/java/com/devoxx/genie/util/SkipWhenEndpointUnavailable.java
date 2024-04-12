package com.devoxx.genie.util;

import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(EndpointAvailabilityCondition.class)
public @interface SkipWhenEndpointUnavailable {

    String uri();

}
