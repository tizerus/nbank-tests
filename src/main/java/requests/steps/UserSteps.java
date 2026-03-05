package requests.steps;

import models.CreateAccountResponse;
import models.CreateUserRequest;
import models.DepositRequest;
import models.DepositResponse;
import models.GetTransferRequest;
import models.GetTransferResponse;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.ValidatableCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class UserSteps {

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

}
