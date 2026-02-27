package requests.skeleton;

import lombok.AllArgsConstructor;
import lombok.Getter;
import models.CreateAccountRequest;
import models.BaseModel;
import models.CreateAccountResponse;
import models.CreateUserRequest;
import models.CreateUserResponse;
import models.DepositRequest;
import models.DepositResponse;
import models.GetCustomerAccountsResponse;
import models.LoginUserRequest;
import models.LoginUserResponse;
import models.GetTransferRequest;
import models.Transaction;
import models.GetTransferResponse;

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
    )
    ;

    private final String url;
    private final Class<? extends BaseModel> requestModel;
    private final Class<? extends BaseModel> responseModel;

}
