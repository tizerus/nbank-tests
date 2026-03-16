package api.requests.skeleton.interfaces;

import api.models.BaseModel;

public interface CrudEndpointInterface {

    Object get(long id);
    Object get();
    Object post(BaseModel model);
    Object put(long id, BaseModel model);
    Object delete(long id, BaseModel model);

}
