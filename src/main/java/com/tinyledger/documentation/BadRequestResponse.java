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
                responseCode = "400",
                description = "Bad request",
                content = @Content(
                        schema = @Schema(implementation = ProblemDetail.class),
                        examples = {
                                @ExampleObject(
                                        name = "BadRequest",
                                        summary = "Example invalid request response",
                                        value = """
                                                {
                                                  "detail": "The request is invalid",
                                                  "instance": "/api/v1/accounts",
                                                  "status": 400,
                                                  "title": "Bad Request",
                                                  "errors": {
                                                    "accountName": "Account name must contain at least 3 characters"
                                                  }
                                                }
                                                """
                                )
                        }
                )
        )
})
public @interface BadRequestResponse {
}

