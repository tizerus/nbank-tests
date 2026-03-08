package api.itteration1;

import api.ApiBaseTest;
import models.CreateAccountResponse;
import models.CreateUserRequest;
import models.GetCustomerAccountsResponse;
import org.junit.jupiter.api.Test;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.ValidatableCrudRequester;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.List;

public class CreateAccTest extends ApiBaseTest {

    @Test
    public void userCanCreateAccountTest() {
        CreateUserRequest createUserRequest = AdminSteps.createUserResponse();

        CreateAccountResponse userAcc = UserSteps
                .createAccount(createUserRequest);

        List<GetCustomerAccountsResponse> accounts = new ValidatableCrudRequester<GetCustomerAccountsResponse>(
                RequestSpecs.authAsUser(createUserRequest.getUsername(), createUserRequest.getPassword()),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOk())
                .getAll();

        softAssert.assertThat(accounts)
                .extracting(GetCustomerAccountsResponse::getAccountNumber)
                .as("Extracting all account numbers")
                .contains(userAcc.getAccountNumber());
    }

}
