package requests.skeleton.requests;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import models.BaseModel;
import requests.skeleton.Endpoint;
import requests.skeleton.HttpRequest;
import requests.skeleton.interfaces.CrudEndpointInterface;

import java.util.List;

public class ValidatableCrudRequester<T extends BaseModel> extends HttpRequest implements CrudEndpointInterface {

    private CrudRequester crudRequester;

    public ValidatableCrudRequester(RequestSpecification requestSpecification, Endpoint endpoint, ResponseSpecification responseSpecification) {
        super(requestSpecification, endpoint, responseSpecification);
        this.crudRequester = new CrudRequester(requestSpecification, endpoint, responseSpecification);
    }

    @Override
    public T get() {
        return (T) crudRequester
                .get()
                .extract()
                .as(endpoint.getResponseModel());
    }

    public List<T> getList() {
        Response response = crudRequester
                .get()
                .extract()
                .response();
        Class<T> responseType = (Class<T>) endpoint.getResponseModel();
        return response.jsonPath().getList(".", responseType);
    }

    @Override
    public T post(BaseModel model) {
        return (T) crudRequester
                .post(model)
                .extract()
                .as(endpoint.getResponseModel());
    }

    @Override
    public Object put(long id, BaseModel model) {
        return null;
    }

    @Override
    public Object delete(long id, BaseModel model) {
        return null;
    }

}
