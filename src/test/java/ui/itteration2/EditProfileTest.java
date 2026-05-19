package ui.itteration2;

import api.models.User;
import api.requests.steps.AdminSteps;
import com.codeborne.selenide.Selenide;
import common.annotations.Browsers;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ui.BaseUiTest;
import ui.pages.BankAlert;
import ui.pages.BasePage;
import ui.pages.EditProfilePage;

import java.util.stream.Stream;

public class EditProfileTest extends BaseUiTest {

    public static Stream<Arguments> invalidUserName() {
        return Stream.of(
                Arguments.of("", BankAlert.EDIT_PROFILE_ENTER_VALID_NAME.getMsg())
                        );
    }

    public static Stream<String> validUserNames() {
        return Stream.of(
                "Johny Depp"
                //"Carlos José"          //actual: Name must contain two words with letters only
                        );
    }

    @MethodSource("invalidUserName")
    @ParameterizedTest
    @Browsers(values = {"chrome"})
    public void userCantEditProfileWithInvalidNameTest(String name, String msg) {
        User user = AdminSteps.createUserAndAcc(1);

        BasePage.authAsUser(user);
        EditProfilePage editPage = new EditProfilePage();
        editPage.open();
        String oldUiProfileName = editPage.getUserName();
        String oldApiProfileName = user.getProfileName();
        editPage
                .editProfileName(name)
                .checkAlertMsgAndAccept(msg);

        Assertions.assertThat(editPage.getUserName()).isEqualTo(oldUiProfileName);
        //api check
        Assertions.assertThat(user.getProfileName()).isEqualTo(oldApiProfileName);
    }

    @MethodSource("validUserNames")
    @ParameterizedTest
    //@UserSession
    @Browsers(values = {"chrome"})
    public void userCanEditProfileWithValidNameTest(String validName) {
        User user = AdminSteps.createUserAndAcc(1);
        BasePage.authAsUser(user);
        EditProfilePage editPage = new EditProfilePage();

        editPage.open()
                .editProfileName(validName)
                .checkAlertMsgAndAccept(BankAlert.NAME_UPDATED_SUCCESSFULLY.getMsg());

        Selenide.refresh();

        Assertions.assertThat(editPage.getUserName()).isEqualTo(validName);
        //api check
        Assertions.assertThat(user.getProfileName()).isEqualTo(validName);
    }

}
