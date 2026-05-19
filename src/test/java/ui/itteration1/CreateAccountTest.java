package ui.itteration1;

import api.models.CreateUserRequest;
import api.models.GetCustomerAccountsResponse;
import common.annotations.UserSession;
import common.extension.ScreenshotOnFailureExtension;
import common.storage.SessionStorage;
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
        CreateUserRequest userRequest = SessionStorage.getUser(1);

        BasePage.authAsUser(userRequest);
        new UserDashboard().open()
                .createUserAccount();

        List<GetCustomerAccountsResponse> existingUserAccounts = SessionStorage.getUserSteps(userRequest).getAllAccounts();
        Assertions.assertThat(existingUserAccounts).hasSize(1);
        GetCustomerAccountsResponse createdUserAcc = existingUserAccounts.get(0);
        Assertions.assertThat(createdUserAcc.getBalance()).isZero();

        new UserDashboard().checkAlertMsgAndAccept(BankAlert.ACCOUNT_NUMBER_CREATED.getMsg() + createdUserAcc.getAccountNumber());
    }

}
