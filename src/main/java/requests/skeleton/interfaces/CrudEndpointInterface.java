package requests.skeleton.interfaces;

import models.BaseModel;

public interface CrudEndpointInterface {

    Object get();
    Object post(BaseModel model);
    Object put(long id, BaseModel model);
    Object delete(long id, BaseModel model);

}
