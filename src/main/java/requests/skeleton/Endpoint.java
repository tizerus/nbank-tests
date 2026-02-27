package requests.skeleton;

import lombok.AllArgsConstructor;
import lombok.Getter;
import models.CreateAccountRequest;
import models.BaseModel;
import models.CreateAccountResponse;
import models.CreateUserRequest;
import models.CreateUserResponse;
import models.GetCustomerAccountsResponse;
import models.LoginUserRequest;
import models.LoginUserResponse;

@AllArgsConstructor
@Getter
public enum Endpoint {

    ADMIN_USER(
            "/admin/users",
            CreateUserRequest.class,
            CreateUserResponse.class
    ),
    ACCOUNTS(
            "/accounts",
            CreateAccountRequest.class,
            CreateAccountResponse.class
    ),
    CUSTOMER_ACCOUNTS(
            "/customer/accounts",
            null,
            GetCustomerAccountsResponse.class
    ),
    LOGIN(
            "/auth/login",
            LoginUserRequest.class,
            LoginUserResponse.class
    );

    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;

}
