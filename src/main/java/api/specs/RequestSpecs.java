package api.specs;

import api.config.Config;
import api.models.LoginUserRequest;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.CrudRequester;
import com.github.viclovsky.swagger.coverage.SwaggerCoverageV3RestAssured;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import java.util.HashMap;
import java.util.Map;

public class RequestSpecs {

    private static final ThreadLocal<Map<String, String>> authHeaderMap =
            ThreadLocal.withInitial(() -> new HashMap<>(Map.of("admin", "Basic YWRtaW46YWRtaW4=")));

    private RequestSpecs() {}

    private static Map<String, String> getAuthHeaderMap() {
        return authHeaderMap.get();
    }

    private static RequestSpecBuilder defaultRequestSpecBuilder() {
        return new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter())
                .addFilter(new AllureRestAssured())
                .addFilter(new SwaggerCoverageV3RestAssured())
                .setBaseUri(Config.getProperty("base.api.url"));
    }

    public static RequestSpecification unAuthSpec() {
        return defaultRequestSpecBuilder().build();
    }

    public static RequestSpecification adminSpec() {
        return defaultRequestSpecBuilder()
                .addHeader("Authorization", getAuthHeaderMap().get("admin"))
                .build();
    }

    public static RequestSpecification authAsUser(String username, String password) {
        return defaultRequestSpecBuilder()
                .addHeader("Authorization", getUserAuthHeader(username, password))
                .build();
    }

    public static String getUserAuthHeader(String username, String password) {
        String auth;
        if (!getAuthHeaderMap().containsKey(username)) {
            auth = new CrudRequester(
                    RequestSpecs.unAuthSpec(),
                    Endpoint.LOGIN,
                    ResponseSpecs.requestReturnsOk()
            )
                    .post(LoginUserRequest.builder().username(username).password(password).build())
                    .extract()
                    .header("Authorization");
            getAuthHeaderMap().put(username, auth);
        } else {
            auth = getAuthHeaderMap().get(username);
        }

        return auth;
    }

}
