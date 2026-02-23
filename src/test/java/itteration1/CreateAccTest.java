package itteration1;

import generators.RandomData;
import models.Account;
import models.CreateUserRequest;
import models.UserRole;
import org.junit.jupiter.api.Test;
import requests.AdminCreateUserRequester;
import requests.CreateAccountRequester;
import requests.GetCustomerAccountsRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class CreateAccTest extends BaseTest {

    @Test
    public void userCanCreateAccountTest() {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username(RandomData.getUserName())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();

        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityCreated())
                .post(createUserRequest);

        Account userAcc = new CreateAccountRequester(
                RequestSpecs.authAsUser(createUserRequest.getUsername(), createUserRequest.getPassword()),
                ResponseSpecs.entityCreated())
                .postWithoutBody()
                .extract()
                .as(Account.class);

        Account[] accounts = new GetCustomerAccountsRequester(
                RequestSpecs.authAsUser(createUserRequest.getUsername(), createUserRequest.getPassword()),
                ResponseSpecs.requestReturnsOk())
                .get()
                .extract()
                .as(Account[].class);

        softAssert.assertThat(accounts)
                .extracting(Account::getAccountNumber)
                .as("Extracting all account numbers")
                .contains(userAcc.getAccountNumber());
    }

}
