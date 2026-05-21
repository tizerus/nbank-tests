package ui.itteration1;

import api.models.CreateUserRequest;
import api.models.GetCustomerAccountsResponse;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.ValidatableCrudRequester;
import api.requests.steps.AdminSteps;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import common.utils.WaitUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.BankAlert;
import ui.pages.BasePage;
import ui.pages.UserDashboard;

import java.util.List;

public class CreateAccountTest extends BaseUiTest {

    @Test
    public void userCanCreateAccountTest() {
        CreateUserRequest userRequest = AdminSteps.createUserResponse();

        BasePage.authAsUser(userRequest);
        new UserDashboard().open()
                .createUserAccount();

        List<GetCustomerAccountsResponse> accCount = WaitUtils.waitForResult(
                () -> new ValidatableCrudRequester<GetCustomerAccountsResponse>(
                        RequestSpecs.authAsUser(userRequest.getUsername(), userRequest.getPassword()),
                        Endpoint.CUSTOMER_ACCOUNTS,
                        ResponseSpecs.requestReturnsOk())
                        .getAll(GetCustomerAccountsResponse[].class),
                accounts -> !accounts.isEmpty() && accounts.get(0).getBalance() == 0
                                                                            );
        Assertions.assertThat(accCount).hasSize(1);
        Assertions.assertThat(accCount.get(0).getBalance()).isZero();

        new UserDashboard().checkAlertMsgAndAccept(BankAlert.ACCOUNT_NUMBER_CREATED.getMsg()
                + accCount.get(0).getAccountNumber());
    }

}
