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
                responseCode = "500",
                description = "Internal server error",
                content = @Content(
                        schema = @Schema(implementation = ProblemDetail.class),
                        examples = {
                                @ExampleObject(
                                        name = "InternalServerError",
                                        summary = "Example internal server error response",
                                        value = """
                                                {
                                                  "detail": "An unexpected error has occurred",
                                                  "instance": "/api/v1/accounts",
                                                  "status": 500,
                                                  "title": "Internal Server Error"
                                                }
                                                """
                                )
                        }
                )
        )
})
public @interface InternalServerErrorResponse {
}
