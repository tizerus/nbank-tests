package ui.pages;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import ui.elements.DepositUserAccount;

import java.util.List;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@Getter
public class DepositPage extends BasePage<DepositPage> {

    public static String BALANCE = "%s (Balance: $%s)";
    private SelenideElement depositTitle = $(Selectors.byXpath("//h1[contains(text(), 'Deposit Money')]"));
    private SelenideElement accountSelector = $(Selectors.byCssSelector("select.form-control.account-selector"));
    private SelenideElement amountInput = $(Selectors.byPlaceholder("Enter amount"));
    private SelenideElement depositButton = $(Selectors.byXpath("//button[contains(text(), 'Deposit')]"));

    @Override
    public String url() {
        return "/deposit";
    }

    /**
     * @param accountNum number in the list from 1, cause 0 - "-- Choose an account --"
     */
    public DepositPage selectAccount(int accountNum) {
        accountSelector.selectOption(accountNum);
        return this;
    }

    public DepositPage selectAccountByValue(String value) {
        accountSelector.selectOptionByValue(value);
        return this;
    }

    public SelenideElement getLogoutButton() {
        return logoutButton;
    }

    public SelenideElement getHomeButton() {
        return homeButton;
    }

    public DepositPage deposit(int accNum, String amount) {
        selectAccount(accNum);
        amountInput.shouldBe(visible).sendKeys(amount);
        depositButton.shouldBe(visible).click();
        return this;
    }

    /**
     * Takes selected option value from account dropdown menu
     * example: ACC4 (Balance: $0.00)!
     */
    public String getSelectedAccountOptionValue() {
        return accountSelector.getSelectedOption().getText();
    }

    public String getSelectedAccountName() {
        return accountSelector.getSelectedOption().getText().split(" ")[0];
    }

    public List<DepositUserAccount> getAllOptions() {
        ElementsCollection collection = accountSelector.parent().findAll("option");
        return generatePageElement(collection, DepositUserAccount::new);
    }

    public static String getBalanceString(String accNum, String formattedAmount) {
        return String.format(BALANCE, accNum, formattedAmount);
    }

}
