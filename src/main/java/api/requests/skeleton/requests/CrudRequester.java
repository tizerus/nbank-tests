package api.requests.skeleton.requests;

import api.models.BaseModel;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.HttpRequest;
import api.requests.skeleton.interfaces.CrudEndpointInterface;
import api.requests.skeleton.interfaces.GetAllEndpointInterface;
import common.utils.StepLogger;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static io.restassured.RestAssured.given;

public class CrudRequester extends HttpRequest implements CrudEndpointInterface, GetAllEndpointInterface {

    public CrudRequester(RequestSpecification requestSpecification, Endpoint endpoint,
            ResponseSpecification responseSpecification) {
        super(requestSpecification, endpoint, responseSpecification);
    }

    @Override
    public ValidatableResponse get(long id) {
        return StepLogger.log("GET Request to " + endpoint.getUrl() + " with id:" + id, () -> {
            return given()
                    .spec(requestSpecification)
                    .get(String.format(endpoint.getUrl(), id))
                    .then()
                    .assertThat()
                    .spec(responseSpecification);
        });
    }

    @Override
    public ValidatableResponse get() {
        return StepLogger.log("GET Request to " + endpoint.getUrl(), () -> {
            return given()
                    .spec(requestSpecification)
                    .get(endpoint.getUrl())
                    .then()
                    .assertThat()
                    .spec(responseSpecification);
        });
    }

    public ValidatableResponse getAll() {
        return StepLogger.log("GET_ALL Request to " + endpoint.getUrl(), () -> {
            return given()
                    .spec(requestSpecification)
                    .get(endpoint.getUrl())
                    .then()
                    .assertThat()
                    .spec(responseSpecification);
        });
    }

    public ValidatableResponse getAll(long id) {
        return StepLogger.log("GET_ALL Request to " + endpoint.getUrl() + " with id:" + id, () -> {
            return given()
                    .spec(requestSpecification)
                    .pathParam("id", id)
                    .get(endpoint.getUrl())
                    .then()
                    .assertThat()
                    .spec(responseSpecification);
        });
    }

    @Override
    public ValidatableResponse post(BaseModel model) {
        var body = model == null ? "" : model;
        return StepLogger.log("POST Request to " + endpoint.getUrl() + " with model:" + model, () -> {
            return given()
                    .spec(requestSpecification)
                    .body(body)
                    .post(endpoint.getUrl())
                    .then()
                    .assertThat()
                    .spec(responseSpecification);
        });
    }

    @Override
    public Object put(long id, BaseModel model) {
        return null;
    }

    @Override
    public Object delete(long id, BaseModel model) {
        return given()
                .spec(requestSpecification)
                .delete(endpoint.getUrl() + "/" + id)
                .then().assertThat()
                .spec(responseSpecification);
    }

    @Override
    public ValidatableResponse getAll(Class<?> clazz) {
        return StepLogger.log("GET_ALL Request to " + endpoint.getUrl() + " with class:" + clazz, () -> {
            return given()
                    .spec(requestSpecification)
                    .get(endpoint.getUrl())
                    .then().assertThat()
                    .spec(responseSpecification);
        });
    }

}
