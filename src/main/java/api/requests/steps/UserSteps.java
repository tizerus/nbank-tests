package api.requests.steps;

import api.models.CreateAccountResponse;
import api.models.GetCustomerAccountsResponse;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.ValidatableCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import api.models.CreateUserRequest;
import api.models.DepositRequest;
import api.models.DepositResponse;
import api.models.GetTransferRequest;
import api.models.GetTransferResponse;
import lombok.Getter;

import java.util.List;

@Getter
public class UserSteps {

    private String userName;
    private String password;

    public UserSteps(CreateUserRequest userRequest) {
        this.userName = userRequest.getUsername();
        this.password = userRequest.getPassword();
    }

    public static CreateAccountResponse createAccount(CreateUserRequest userRequest) {
        return new ValidatableCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityCreated())
                .post(null);
    }

    public static DepositResponse putDeposit(CreateUserRequest userRequest,
            CreateAccountResponse accountResponse, float amount) {
        return new ValidatableCrudRequester<DepositResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOk())
                .post(DepositRequest.builder()
                        .id(accountResponse.getId())
                        .balance(amount)
                        .build());
    }

    public static DepositResponse putDeposit(CreateUserRequest userRequest,
            DepositRequest depositResponse) {
        return new ValidatableCrudRequester<DepositResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsOk())
                .post(depositResponse);
    }

    public static GetTransferResponse transfer(CreateUserRequest userRequest,
            CreateAccountResponse account1,
            CreateAccountResponse account2,
            float amount) {
        return new ValidatableCrudRequester<GetTransferResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsOk())
                .post(GetTransferRequest.builder()
                        .senderAccountId(account1.getId())
                        .receiverAccountId(account2.getId())
                        .amount(amount)
                        .build());
    }

    public List<GetCustomerAccountsResponse> getAllAccounts() {
        return new ValidatableCrudRequester<GetCustomerAccountsResponse>(
                RequestSpecs.authAsUser(this.userName, this.password),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOk())
                .getAll(GetCustomerAccountsResponse[].class);
    }

}
