package specs;

import config.Config;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import models.LoginUserRequest;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.CrudRequester;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestSpecs {

    private static Map<String, String> authHeaderMap = new HashMap<>(
            Map.of("admin", "Basic YWRtaW46YWRtaW4=")
    );

    private RequestSpecs() {}

    private static RequestSpecBuilder defaultRequestSpecBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilters(List.of(new RequestLoggingFilter(), new ResponseLoggingFilter()))
                .setBaseUri(Config.getProperty("server") + Config.getProperty("apiVersion"));
    }

    public static RequestSpecification unAuthSpec() {
        return defaultRequestSpecBuilder().build();
    }

    public static RequestSpecification adminSpec() {
        return defaultRequestSpecBuilder()
                .addHeader("Authorization",authHeaderMap.get("admin"))
                .build();
    }

    public static RequestSpecification authAsUser(String username, String password) {
        String auth;
        if (!authHeaderMap.containsKey(username)){
            auth = new CrudRequester(
                    RequestSpecs.unAuthSpec(),
                    Endpoint.LOGIN,
                    ResponseSpecs.requestReturnsOk()
            )
                    .post(LoginUserRequest.builder().username(username).password(password).build())
                    .extract()
                    .header("Authorization");
            authHeaderMap.put(username, auth);
        } else {
            auth = authHeaderMap.get(username);
        }

        return defaultRequestSpecBuilder()
                .addHeader("Authorization", auth)
                .build();
    }

}
