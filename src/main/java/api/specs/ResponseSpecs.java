package api.specs;

import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.specification.ResponseSpecification;
import org.apache.http.HttpStatus;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

public class ResponseSpecs {

    private ResponseSpecs() {

    }

    private static ResponseSpecBuilder defaultResponseSpecBuilder() {
        return new ResponseSpecBuilder();
    }

    public static ResponseSpecification entityCreated() {
        return defaultResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_CREATED)
                .build();
    }

    public static ResponseSpecification requestReturnsOk() {
        return defaultResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_OK)
                .build();
    }

    public static ResponseSpecification requestReturnsBadResponse(String errorKey, String errorValue) {
        return defaultResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(errorKey, Matchers.hasItem(errorValue))
                .build();
    }

    public static ResponseSpecification requestReturnsBadResponse(Matcher<?> bodyMatcher) {
        return defaultResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .expectBody(bodyMatcher)
                .build();
    }

    public static ResponseSpecification requestReturns403() {
        return defaultResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_FORBIDDEN)
                .build();
    }

    public static ResponseSpecification requestReturns401() {
        return defaultResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_UNAUTHORIZED)
                .build();
    }

    public static ResponseSpecification requestReturns404() {
        return defaultResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_NOT_FOUND)
                .build();
    }

    public static ResponseSpecification requestReturns400() {
        return defaultResponseSpecBuilder()
                .expectStatusCode(HttpStatus.SC_BAD_REQUEST)
                .build();
    }

}
