package ui.itteration1;

import api.generators.RandomModelGenerator;
import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.models.GetCustomerAccountsResponse;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.ValidatableCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.annotations.UserSession;
import common.extension.ScreenshotOnFailureExtension;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.BaseUiTest;
import ui.pages.BankAlert;
import ui.pages.BasePage;
import ui.pages.UserDashboard;

import java.util.List;

@ExtendWith(ScreenshotOnFailureExtension.class)
public class CreateAccountTest extends BaseUiTest {

    @Test
    @UserSession
    public void userCanCreateAccountTest() {
        CreateUserRequest userRequest = RandomModelGenerator.generate(CreateUserRequest.class);

        CreateUserResponse user = new ValidatableCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityCreated())
                .post(userRequest);

        BasePage.authAsUser(userRequest);
        new UserDashboard().open()
                .createUserAccount();

        int accCount = new ValidatableCrudRequester<GetCustomerAccountsResponse>(
                RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                Endpoint.CUSTOMER_ACCOUNTS,
                ResponseSpecs.requestReturnsOk())
                .getAll(GetCustomerAccountsResponse[].class)
                .size();

        List<GetCustomerAccountsResponse> existingUserAccounts = user.getAccounts();
        Assertions.assertThat(existingUserAccounts).hasSize(accCount);
        GetCustomerAccountsResponse createdUserAcc = existingUserAccounts.get(0);
        Assertions.assertThat(createdUserAcc.getBalance()).isZero();

        new UserDashboard().checkAlertMsgAndAccept(BankAlert.ACCOUNT_NUMBER_CREATED.getMsg() + createdUserAcc.getAccountNumber());
    }

}
