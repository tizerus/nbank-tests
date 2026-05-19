package ui.itteration1;

import com.codeborne.selenide.Condition;
import api.models.CreateUserRequest;
import common.annotations.Browsers;
import common.extension.ScreenshotOnFailureExtension;
import org.junit.jupiter.api.Test;
import api.requests.steps.AdminSteps;
import org.junit.jupiter.api.extension.ExtendWith;
import ui.BaseUiTest;
import ui.pages.AdminPanel;
import ui.pages.LoginPage;
import ui.pages.UserDashboard;

import static com.codeborne.selenide.Condition.visible;
@ExtendWith(ScreenshotOnFailureExtension.class)
public class LoginUserTest extends BaseUiTest {

    @Test
    @Browsers(values = {"chrome"})
    public void adminCanLoginWithCorrectDataTest() {
        CreateUserRequest admin = CreateUserRequest.getAdmin();

        new LoginPage().open()
                .login(admin.getUsername(), admin.getPassword(), AdminPanel.class)
                .getAdminPanelText()
                .shouldBe(visible);
    }

    @Test
    @Browsers(values = {"chrome"})
    public void userCanLoginWithCorrectDataTest() {
        CreateUserRequest createUserRequest = AdminSteps.createUserResponse();
        UserDashboard loginPage = new LoginPage().open()
                .login(createUserRequest.getUsername(), createUserRequest.getPassword(), UserDashboard.class);
        loginPage
                .getWelcomeText()
                .shouldBe(visible)
                .shouldHave(Condition.text("Welcome, noname!"));
        loginPage.getUserDashboardTextElement().shouldBe(visible);
    }

}
