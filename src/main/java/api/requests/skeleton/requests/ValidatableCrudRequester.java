package api.requests.skeleton.requests;

import api.requests.skeleton.interfaces.GetAllEndpointInterface;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import api.models.BaseModel;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.HttpRequest;
import api.requests.skeleton.interfaces.CrudEndpointInterface;

import java.util.Arrays;
import java.util.List;

public class ValidatableCrudRequester<T extends BaseModel> extends HttpRequest
        implements CrudEndpointInterface, GetAllEndpointInterface {

    private CrudRequester crudRequester;

    public ValidatableCrudRequester(RequestSpecification requestSpecification, Endpoint endpoint,
            ResponseSpecification responseSpecification) {
        super(requestSpecification, endpoint, responseSpecification);
        this.crudRequester = new CrudRequester(requestSpecification, endpoint, responseSpecification);
    }

    @Override
    public T get(long id) {
        return (T) crudRequester
                .get(id)
                .extract()
                .as(endpoint.getResponseModel());
    }

    @Override
    public T get() {
        return (T) crudRequester
                .get()
                .extract()
                .as(endpoint.getResponseModel());
    }

    public List<T> getAll() {
        Response response = crudRequester
                .getAll()
                .extract()
                .response();
        Class<T> responseType = (Class<T>) endpoint.getResponseModel();
        return response.jsonPath().getList(".", responseType);
    }

    public List<T> getAll(long id) {
        Response response = crudRequester
                .getAll(id)
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

    @Override
    public List<T> getAll(Class<?> clazz) {
        T[] arr = (T[]) crudRequester.getAll(clazz).extract().as(clazz);
        return Arrays.asList(arr);
    }

}
