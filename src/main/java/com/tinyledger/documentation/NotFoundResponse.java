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
                responseCode = "404",
                description = "Not found",
                content = @Content(
                        schema = @Schema(implementation = ProblemDetail.class),
                        examples = {
                                @ExampleObject(
                                        name = "NotFound",
                                        summary = "Example not found response",
                                        value = """
                                                {
                                                  "detail": "Account with ID 550e8400-e29b-41d4-a716-446655440000 not found.",
                                                  "instance": "/api/v1/accounts/550e8400-e29b-41d4-a716-446655440000",
                                                  "status": 404,
                                                  "title": "Not Found"
                                                }
                                                """
                                )
                        }
                )
        )
})
public @interface NotFoundResponse {
}
