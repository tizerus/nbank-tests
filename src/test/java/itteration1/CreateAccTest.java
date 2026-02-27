package itteration1;

import generators.RandomModelGenerator;
import models.CreateAccountResponse;
import models.CreateUserRequest;
import models.GetCustomerAccountsResponse;
import org.junit.jupiter.api.Test;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.CrudRequester;
import requests.skeleton.requests.ValidatableCrudRequester;
import requests.steps.AdminSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.List;

public class CreateAccTest extends BaseTest {

    @Test
    public void userCanCreateAccountTest() {
        CreateUserRequest createUserRequest = AdminSteps.createUserResponse();

        CreateAccountResponse userAcc = new ValidatableCrudRequester<CreateAccountResponse>(
                RequestSpecs.authAsUser(createUserRequest.getUsername(), createUserRequest.getPassword()),
                Endpoint.ACCOUNTS,
                ResponseSpecs.entityCreated())
                .post(null);

        List<GetCustomerAccountsResponse> accounts = new ValidatableCrudRequester<GetCustomerAccountsResponse>(
                RequestSpecs.authAsUser(createUserRequest.getUsername(), createUserRequest.getPassword()),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOk())
                .getList();

        softAssert.assertThat(accounts)
                .extracting(GetCustomerAccountsResponse::getAccountNumber)
                .as("Extracting all account numbers")
                .contains(userAcc.getAccountNumber());
    }

}
