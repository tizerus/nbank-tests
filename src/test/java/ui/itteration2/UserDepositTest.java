package ui.itteration2;

import api.generators.RandomData;
import api.models.User;
import api.requests.steps.AdminSteps;
import com.codeborne.selenide.Condition;
import common.annotations.Browsers;
import common.annotations.UserSession;
import common.storage.SessionStorage;
import org.apache.commons.lang3.RandomStringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ui.BaseUiTest;
import ui.pages.BankAlert;
import ui.pages.BasePage;
import ui.pages.DepositPage;
import ui.pages.UserDashboard;

import java.util.stream.Stream;

import static com.codeborne.selenide.Condition.visible;

public class UserDepositTest extends BaseUiTest {

    public static Stream<Arguments> invalidFloatAmountData() {
        return Stream.of(
                Arguments.of(-0F, BankAlert.ENTER_VALID_AMOUNT.getMsg()),
                Arguments.of(-0.01F, BankAlert.ENTER_VALID_AMOUNT.getMsg()),
                Arguments.of(5000.001F, BankAlert.DEPOSIT_LESS_OR_EQUAL_TO_5000.getMsg()),
                Arguments.of(0.0, BankAlert.ENTER_VALID_AMOUNT.getMsg()),
                Arguments.of("", BankAlert.ENTER_VALID_AMOUNT.getMsg()),
                Arguments.of(" ", BankAlert.ENTER_VALID_AMOUNT.getMsg()),
                Arguments.of("  ", BankAlert.ENTER_VALID_AMOUNT.getMsg()),
                /*Arguments.of("123.23.23", BankAlert.ENTER_VALID_AMOUNT.getMsg()), //second dot ignored sum = 123.2323
                Arguments.of("123,123", BankAlert.ENTER_VALID_AMOUNT.getMsg()), // comma ignored, sum = 123123
                Arguments.of("123 123", BankAlert.ENTER_VALID_AMOUNT.getMsg()), // space  ignored, sum = 123123
                Arguments.of("123?123", BankAlert.ENTER_VALID_AMOUNT.getMsg()), // ?  ignored, sum = 123123*/
                Arguments.of("$", BankAlert.ENTER_VALID_AMOUNT.getMsg()),
                Arguments.of("null", BankAlert.ENTER_VALID_AMOUNT.getMsg()),
                Arguments.of(RandomStringUtils.secure().nextAlphabetic(5,10), BankAlert.ENTER_VALID_AMOUNT.getMsg())
                        );
    }

    public static Stream<Arguments> validFloatAmountData() {
        return Stream.of(
                Arguments.of(0.01F, BankAlert.DEPOSIT_SUCCESS_MSG.getMsg(), "0.01"),
                //Arguments.of(0.001F, BankAlert.DEPOSIT_SUCCESS_MSG.getMsg(), "0.001"), ❌ Failed to deposit. Please try again.
                Arguments.of(10.5F, BankAlert.DEPOSIT_SUCCESS_MSG.getMsg(), "10.5"),
                Arguments.of(499.99F, BankAlert.DEPOSIT_SUCCESS_MSG.getMsg(), "499.99"),
                Arguments.of(4999.99F, BankAlert.DEPOSIT_SUCCESS_MSG.getMsg(), "4999.99"),
                //Arguments.of(5000.00, BankAlert.DEPOSIT_SUCCESS_MSG.getMsg(), "5000.00"), ✅ Successfully deposited $5000.0 to account ACC36!
                Arguments.of("123.45", BankAlert.DEPOSIT_SUCCESS_MSG.getMsg(), "123.45"),
                //Arguments.of("0.01", BankAlert.DEPOSIT_SUCCESS_MSG.getMsg(), "0.01"), ✅ Successfully deposited $.01 to account ACC39!
                Arguments.of(".01", BankAlert.DEPOSIT_SUCCESS_MSG.getMsg(), ".01"),
                Arguments.of("5000", BankAlert.DEPOSIT_SUCCESS_MSG.getMsg(), "5000")
                        );
    }

    @Test
    @UserSession
    @Browsers(values = {"chrome"})
    public void userDepositPageButtonsVisibleChecksTest() {
        BasePage.authAsUser(SessionStorage.getUser(1));
        new UserDashboard().open().createUserAccount();
        DepositPage depositPage = new DepositPage().open();
        depositPage.getDepositTitle().shouldBe(visible).shouldHave(Condition.text("💰 Deposit Money"));
        depositPage.getAccountSelector().shouldBe(visible);
        depositPage.getAmountInput().shouldBe(visible);
        depositPage.getDepositButton().shouldBe(visible);
        depositPage.getLogoutButton().shouldBe(visible);
        depositPage.getHomeButton().shouldBe(visible);
    }

    @Test
    @Browsers(values = {"chrome"})
    public void userDepositEmptyAccountFieldChecksTest() {
        User user = AdminSteps.createUserAndAcc(1);
        BasePage.authAsUser(user);
        DepositPage depositPage = new DepositPage().open();
        depositPage.getAmountInput().sendKeys(String.valueOf(RandomData.generateFloatInclusive(0.01F, 5000F)));
        depositPage.getDepositButton().click();
        depositPage.checkAlertMsgAndAccept(BankAlert.ACCOUNT_EMPTY_FIELD_ALERT);
    }

    @Test
    @Browsers(values = {"chrome"})
    public void userDepositEmptyAmountFieldChecksTest() {
        User user = AdminSteps.createUserAndAcc(1);
        BasePage.authAsUser(user);
        DepositPage depositPage = new DepositPage();
        depositPage.open()
                .selectAccount(1)
                .getDepositButton()
                .click();
        depositPage.checkAlertMsgAndAccept(BankAlert.ENTER_VALID_AMOUNT);
    }

    @MethodSource("invalidFloatAmountData")
    @ParameterizedTest
    @UserSession
    @Browsers(values = {"chrome"})
    public void userCantDepositInvalidFloatAmountTest(Object amount, String msg) {
        User user = AdminSteps.createUserAndAcc(1);
        BasePage.authAsUser(user);
        float balanceBeforeDeposit = user.getBalance(user.getAccountsNumbers().get(0));
        DepositPage depositPage = new DepositPage();
        depositPage.open().deposit(1, String.valueOf(amount));
        depositPage.checkAlertMsgAndAccept(msg);
        //api check
        float balanceAfterDeposit = user.getBalance(user.getAccountsNumbers().get(0));
        Assertions.assertThat(balanceBeforeDeposit).isEqualTo(balanceAfterDeposit);
    }

    @MethodSource("validFloatAmountData")
    @ParameterizedTest
    @Browsers(values = {"chrome"})
    public void userCanDepositValidAmountTest(Object amount, String expectedMsg, String expectedAmount) {
        User user = AdminSteps.createUserAndAcc(1);
        BasePage.authAsUser(user);
        float balanceBeforeDeposit = user.getBalance(user.getAccountsNumbers().get(0));
        DepositPage depositPage = new DepositPage().open()
                .deposit(1, String.valueOf(amount));
        String accNum = user.getAccountsNumbers().get(0);
        depositPage.checkAlertMsgAndAccept(String.format(expectedMsg, expectedAmount, accNum));
        //api check
        float balanceAfterDeposit = user.getBalance(user.getAccountsNumbers().get(0));
        Assertions.assertThat(balanceBeforeDeposit + Float.valueOf(expectedAmount)).isEqualTo(balanceAfterDeposit);
    }

}
