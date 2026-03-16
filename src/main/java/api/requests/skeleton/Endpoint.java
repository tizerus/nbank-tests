package api.requests.skeleton;

import api.models.CreateUserResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import api.models.CreateAccountRequest;
import api.models.BaseModel;
import api.models.CreateAccountResponse;
import api.models.CreateUserRequest;
import api.models.DepositRequest;
import api.models.DepositResponse;
import api.models.GetCustomerAccountsResponse;
import api.models.LoginUserRequest;
import api.models.LoginUserResponse;
import api.models.GetTransferRequest;
import api.models.Profile;
import api.models.Transaction;
import api.models.GetTransferResponse;

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
    ),
    DEPOSIT(
            "/accounts/deposit",
            DepositRequest.class,
            DepositResponse.class
    ),
    TRANSFER(
            "/accounts/transfer",
            GetTransferRequest.class,
            GetTransferResponse.class
    ),
    TRANSACTION(
            "/accounts/{id}/transactions",
            null,
            Transaction.class
    ),
    PROFILE(
            "/customer/profile",
            null,
            Profile.class
    )
    ;

    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;

}
