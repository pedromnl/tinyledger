package com.tinyledger.documentation;

import io.swagger.v3.oas.annotations.Parameter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Parameter(
        name = "accountId",
        description = "Account identifier",
        example = "550e8400-e29b-41d4-a716-446655440000"
)
public @interface AccountIdParameter {
}

