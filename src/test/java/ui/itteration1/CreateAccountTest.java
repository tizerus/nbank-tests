package ui.itteration1;

import api.models.CreateAccountResponse;
import api.models.GetCustomerAccountsResponse;
import api.models.User;
import common.annotations.UserSession;
import common.extension.ScreenshotOnFailureExtension;
import common.storage.SessionStorage;
import common.storage.UserPool;
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
    public void userCanCreateAccountTest() throws InterruptedException {
        int numberOfAccs = 1;
        UserPool pool = UserPool.getOrCreate("default", 10, numberOfAccs);
        User user = pool.acquireUser();

        BasePage.authAsUser(user);
        new UserDashboard().open()
                .createUserAccount();

        List<CreateAccountResponse> existingUserAccounts = user.getAccountResponse();
        Assertions.assertThat(existingUserAccounts).hasSize(numberOfAccs);

        Assertions.assertThat(user.getAccountResponse().get(0).getBalance()).isZero();

        new UserDashboard().checkAlertMsgAndAccept(BankAlert.ACCOUNT_NUMBER_CREATED.getMsg() +
                (user.getAccountResponse().get(0).getAccountNumber()));
        pool.releaseUser(user);
    }

}
