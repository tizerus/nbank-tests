package ui.itteration2;

import api.generators.RandomData;
import api.models.User;
import api.requests.steps.AdminSteps;
import api.requests.steps.UserSteps;
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
import ui.pages.TransactionPage;
import ui.pages.UserDashboard;

import java.util.stream.Stream;

import static com.codeborne.selenide.Condition.visible;

public class UserTransactionTest extends BaseUiTest {

    public static Stream<Arguments> invalidTransferAmountData() {
        return Stream.of(
                Arguments.of(-100F, BankAlert.TRANSFER_AMOUNT_MUST_BE_AT_LEAST.getMsg()),
                Arguments.of(-0.01F, BankAlert.TRANSFER_AMOUNT_MUST_BE_AT_LEAST.getMsg()),
                Arguments.of(0F, BankAlert.TRANSFER_AMOUNT_MUST_BE_AT_LEAST.getMsg()),
                Arguments.of(10000.01F, BankAlert.TRANSFER_AMOUNT_CANNOT_EXCEED.getMsg()),
                Arguments.of(15000F, BankAlert.TRANSFER_AMOUNT_CANNOT_EXCEED.getMsg()),
                Arguments.of("", BankAlert.TRANSACTION_FILL_ALL_FIELDS_AND_CONFIRM.getMsg()),
                Arguments.of(" ", BankAlert.TRANSACTION_FILL_ALL_FIELDS_AND_CONFIRM.getMsg()),
                Arguments.of("  ", BankAlert.TRANSACTION_FILL_ALL_FIELDS_AND_CONFIRM.getMsg()),
                Arguments.of("$", BankAlert.TRANSACTION_FILL_ALL_FIELDS_AND_CONFIRM.getMsg()),
                Arguments.of("null", BankAlert.TRANSACTION_FILL_ALL_FIELDS_AND_CONFIRM.getMsg()),
                Arguments.of(RandomStringUtils.secure().nextAlphabetic(5, 10), BankAlert.TRANSACTION_FILL_ALL_FIELDS_AND_CONFIRM.getMsg())
                        );
    }

    public static Stream<Arguments> validTransferAmountData() {
        return Stream.of(
                //Arguments.of(0.01F, BankAlert.SUCCESSFULLY_TRANSFERRED_TO_ACCOUNT.getMsg(), "0.01"), //✅ Successfully transferred $.01 to account ACC194!
                Arguments.of(10.5F, BankAlert.SUCCESSFULLY_TRANSFERRED_TO_ACCOUNT.getMsg(),"10.5"),
                Arguments.of(100F, BankAlert.SUCCESSFULLY_TRANSFERRED_TO_ACCOUNT.getMsg(),"100.0"),
                Arguments.of(999.99F, BankAlert.SUCCESSFULLY_TRANSFERRED_TO_ACCOUNT.getMsg(),"999.99"),
                Arguments.of(5000F, BankAlert.SUCCESSFULLY_TRANSFERRED_TO_ACCOUNT.getMsg(),"5000.0"),
                Arguments.of(9999.99F, BankAlert.SUCCESSFULLY_TRANSFERRED_TO_ACCOUNT.getMsg(),"9999.99")
                //Arguments.of(".01", BankAlert.SUCCESSFULLY_TRANSFERRED_TO_ACCOUNT.getMsg(),"0.01") //✅ Successfully transferred $.01 to account ACC194!
                        );
    }

    @Test
    @UserSession
    @Browsers(values = {"chrome"})
    public void transactionPageButtonsVisibleChecksTest() {
        BasePage.authAsUser(SessionStorage.getUser(1));
        new UserDashboard().open().createUserAccount();

        TransactionPage transactionPage = new TransactionPage().open();
        transactionPage.getTransferTitle().shouldBe(visible).shouldHave(Condition.text("Make a Transfer"));
        transactionPage.getAccountSelector().shouldBe(visible);
        transactionPage.getRecipientNameInput().shouldBe(visible);
        transactionPage.getRecipientAccountNumberInput().shouldBe(visible);
        transactionPage.getAmountInput().shouldBe(visible);
        transactionPage.getInfoConfirmCheckbox().shouldBe(visible);
        transactionPage.getTransferButton().shouldBe(visible);
        transactionPage.getLogoutButton().shouldBe(visible);
        transactionPage.getHomeButton().shouldBe(visible);
    }

    @Test
    @UserSession
    @Browsers(values = {"chrome"})
    public void transactionEmptyAccountFieldTest() {
        BasePage.authAsUser(SessionStorage.getUser(1));
        new UserDashboard().open().createUserAccount();

        TransactionPage transactionPage = new TransactionPage().open();
        transactionPage.fillRecipientName("Test Recipient")
                .fillRecipientAccountNumber("ACC123")
                .fillAmount(String.valueOf(RandomData.generateFloatInclusive(0.01F, 10000F)))
                .checkConfirmCheckbox()
                .getTransferButton()
                .click();

        transactionPage.checkAlertMsgAndAccept(BankAlert.TRANSACTION_FILL_ALL_FIELDS_AND_CONFIRM);
    }

    @Test
    @Browsers(values = {"chrome"})
    //Recipient Name Field is optional so transaction going to be SUCCESSFULLY
    public void transactionEmptyRecipientNameFieldTest() {
        User user = AdminSteps.createUserAndAcc(2);
        BasePage.authAsUser(user);

        String accToGetTransfer = user.getAccountsNumbers().get(1);

        UserSteps.putDeposit(user.getUserRequest(), user.getAccountResponse().get(0), RandomData.generateFloatInclusive(1001F, 5000F));
        String tranferAmount = String.valueOf(RandomData.generateFloatInclusive(0.01F, 1000F));

        TransactionPage transactionPage = new TransactionPage().open();
        transactionPage.selectAccountByValue(user.getAccountsNumbers().get(0))
                .fillRecipientAccountNumber(accToGetTransfer)
                .fillAmount(tranferAmount)
                .checkConfirmCheckbox()
                .getTransferButton()
                .click();

        transactionPage.checkAlertMsgAndAccept(String.format(BankAlert.SUCCESSFULLY_TRANSFERRED_TO_ACCOUNT.getMsg(),
                tranferAmount, accToGetTransfer));
    }

    @Test
    @Browsers(values = {"chrome"})
    public void transactionEmptyRecipientAccountFieldTest() {
        User user = AdminSteps.createUserAndAcc(1);
        BasePage.authAsUser(user);

        UserDashboard userDashboard = new UserDashboard();
        userDashboard.open().createUserAccount();

        TransactionPage transactionPage = new TransactionPage().open();
        transactionPage.selectAccount(1)
                .fillRecipientName(user.getAccountsNumbers().get(0))
                .fillAmount(String.valueOf(RandomData.generateFloatInclusive(0.01F, 10000F)))
                .checkConfirmCheckbox()
                .getTransferButton()
                .click();

        transactionPage.checkAlertMsgAndAccept(BankAlert.TRANSACTION_FILL_ALL_FIELDS_AND_CONFIRM);
    }

    @Test
    @UserSession
    @Browsers(values = {"chrome"})
    public void transactionEmptyAmountFieldTest() {
        BasePage.authAsUser(SessionStorage.getUser(1));
        UserDashboard userDashboard = new UserDashboard();
        userDashboard.open().createUserAccount();

        TransactionPage transactionPage = new TransactionPage().open();
        transactionPage.selectAccount(1)
                .fillRecipientName("Test Recipient")
                .fillRecipientAccountNumber("ACC123")
                .checkConfirmCheckbox()
                .getTransferButton()
                .click();

        transactionPage.checkAlertMsgAndAccept(BankAlert.TRANSACTION_FILL_ALL_FIELDS_AND_CONFIRM);
    }

    @Test
    @UserSession
    @Browsers(values = {"chrome"})
    public void transactionUncheckedConfirmCheckboxTest() {
        BasePage.authAsUser(SessionStorage.getUser(1));
        UserDashboard userDashboard = new UserDashboard();
        userDashboard.open().createUserAccount();

        TransactionPage transactionPage = new TransactionPage().open();
        transactionPage.selectAccount(1)
                .fillRecipientName("Test Recipient")
                .fillRecipientAccountNumber("ACC123")
                .fillAmount("100")
                .getTransferButton()
                .click();

        transactionPage.checkAlertMsgAndAccept(BankAlert.TRANSACTION_FILL_ALL_FIELDS_AND_CONFIRM);
    }

    @Test
    @Browsers(values = {"chrome"})
    public void userCantTransferMoreThanBalanceTest() {
        User user = AdminSteps.createUserAndAcc(2);
        BasePage.authAsUser(user);

        String accToGetTransfer = user.getAccountsNumbers().get(1);

        String tranferAmount = String.valueOf(RandomData.generateFloatInclusive(0.01F, 1000F));

        TransactionPage transactionPage = new TransactionPage().open();
        transactionPage.selectAccount(1)
                .fillRecipientAccountNumber(accToGetTransfer)
                .fillAmount(tranferAmount)
                .checkConfirmCheckbox()
                .getTransferButton()
                .click();

        transactionPage.checkAlertMsgAndAccept(BankAlert.INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS.getMsg());
    }

    @MethodSource("invalidTransferAmountData")
    @ParameterizedTest
    @UserSession
    @Browsers(values = {"chrome"})
    public void userCantTransferInvalidAmountTest(Object amount, String expectedMsg) {
        User user = AdminSteps.createUserAndAcc(1);
        User recipient = AdminSteps.createUserAndAcc(1);
        BasePage.authAsUser(user);

        float balanceBefore = user.getBalance(user.getAccountsNumbers().get(0));

        TransactionPage transactionPage = new TransactionPage().open();
        transactionPage.selectAccount(1)
                .fillRecipientName(recipient.getUserName())
                .fillRecipientAccountNumber(recipient.getAccountsNumbers().get(0))
                .fillAmount(String.valueOf(amount))
                .checkConfirmCheckbox()
                .getTransferButton()
                .click();

        transactionPage.checkAlertMsgAndAccept(expectedMsg);

        // Verify balance unchanged via API
        float balanceAfter = user.getBalance(user.getAccountsNumbers().get(0));
        Assertions.assertThat(balanceBefore).isEqualTo(balanceAfter);
    }

    @MethodSource("validTransferAmountData")
    @ParameterizedTest
    @UserSession
    @Browsers(values = {"chrome"})
    public void userCanTransferValidAmountTest(Object amount, String msg, String expectedAmount) {
        User user = AdminSteps.createUserAndAcc(2);
        BasePage.authAsUser(user);

        String accToGetTransfer = user.getAccountsNumbers().get(1);
        float balanceBefore = user.getBalance(user.getAccountsNumbers().get(0));
        UserSteps.putDeposit(user.getUserRequest(), user.getAccountResponse().get(0), RandomData.generateFloatInclusive(5000F, 5000F));
        UserSteps.putDeposit(user.getUserRequest(), user.getAccountResponse().get(0), RandomData.generateFloatInclusive(5000F, 5000F));

        TransactionPage transactionPage = new TransactionPage().open();
        transactionPage.selectAccountByValue(user.getAccountsNumbers().get(0))
                .fillRecipientAccountNumber(accToGetTransfer)
                .fillAmount(amount.toString())
                .checkConfirmCheckbox()
                .getTransferButton()
                .click();

        transactionPage.checkAlertMsgAndAccept(String.format(msg, expectedAmount, accToGetTransfer));

        // Verify balance unchanged via API
        float balanceAfter = user.getBalance(user.getAccountsNumbers().get(1));
        Assertions.assertThat(balanceBefore + (float) amount).isEqualTo(balanceAfter);
    }

    @Test
    @Browsers(values = {"chrome"})
    public void userCantTransferToSameAccountTest() {
        User user = AdminSteps.createUserAndAcc(1);
        BasePage.authAsUser(user);

        String accToGetTransfer = user.getAccountsNumbers().get(0);

        String tranferAmount = String.valueOf(RandomData.generateFloatInclusive(0.01F, 1000F));

        TransactionPage transactionPage = new TransactionPage().open();
        transactionPage.selectAccount(1)
                .fillRecipientAccountNumber(accToGetTransfer)
                .fillAmount(tranferAmount)
                .checkConfirmCheckbox()
                .getTransferButton()
                .click();

        transactionPage.checkAlertMsgAndAccept(BankAlert.INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS.getMsg());
    }

}
