package api.itteration1;

import api.ApiBaseTest;
import api.comparison.ModelAssertions;
import api.models.CreateAccountResponse;
import api.models.CreateUserRequest;
import api.models.GetCustomerAccountsResponse;
import db.DbService;
import db.models.Accounts;
import org.junit.jupiter.api.Test;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.ValidatableCrudRequester;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.List;

import static db.Tables.ACCOUNTS;

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

        //DB check
        Accounts dbUserAcc = DbService.withConnection(ctx -> ctx.select()
                .from(ACCOUNTS.getTableName())
                .where("id = ?", userAcc.getId())
                .fetchInto(Accounts.class)
                .getFirst());

        ModelAssertions.assertThatModels(dbUserAcc, accounts.getFirst()).match();

    }

}
