package requests;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;

public abstract class Request<T extends BaseModel> {

    protected RequestSpecification requestSpecification;
    protected ResponseSpecification responseSpecification;

    public Request(RequestSpecification requestSpec, ResponseSpecification responseSpec) {
        this.requestSpecification = requestSpec;
        this.responseSpecification = responseSpec;
    }

    public abstract ValidatableResponse post(T model);

}
