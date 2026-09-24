package com.tinyledger.documentation;

import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ProblemDetail;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@ApiResponses({
        @ApiResponse(
                responseCode = "409",
                description = "Insufficient funds for the withdrawal",
                content = @Content(
                        schema = @Schema(implementation = ProblemDetail.class),
                        examples = {
                                @ExampleObject(
                                        name = "InsufficientFunds",
                                        summary = "Example insufficient funds response",
                                        value = """
                                                {
                                                  "detail": "Insufficient funds for the withdrawal.",
                                                  "instance": "/api/v1/accounts/550e8400-e29b-41d4-a716-446655440000/transactions/withdrawal",
                                                  "status": 409,
                                                  "title": "Conflict"
                                                }
                                                """
                                )
                        }
                )
        )
})
public @interface InsufficientFundsResponse {
}
