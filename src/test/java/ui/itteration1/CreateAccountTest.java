package ui.itteration1;

import api.generators.RandomModelGenerator;
import api.models.CreateUserRequest;
import api.models.GetCustomerAccountsResponse;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.BankAlert;
import ui.pages.UserDashboard;

import java.util.List;

public class CreateAccountTest extends BaseUiTest {

    @Test
    public void userCanCreateAccountTest() {
        CreateUserRequest createUserRequest = AdminSteps.createUserResponse();

        authAsUser(createUserRequest);
        new UserDashboard().open()
                .createUserAccount();

        List<GetCustomerAccountsResponse> existingUserAccounts = new UserSteps(createUserRequest).getAllAccounts();
        Assertions.assertThat(existingUserAccounts).hasSize(1);
        GetCustomerAccountsResponse createdUserAcc = existingUserAccounts.get(0);
        Assertions.assertThat(createdUserAcc.getBalance()).isZero();

        new UserDashboard().checkAlertMsgAndAccept(BankAlert.ACCOUNT_NUMBER_CREATED.getMsg() + createdUserAcc.getAccountNumber());

    }

}
