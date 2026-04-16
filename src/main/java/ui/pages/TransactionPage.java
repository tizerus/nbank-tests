package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.openqa.selenium.By;
import org.openqa.selenium.NotFoundException;

import java.util.List;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@Getter
public class TransactionPage extends BasePage<TransactionPage> {

    private SelenideElement transferTitle = $(By.xpath("//h1[contains(text(), 'Make a Transfer')]"));
    private SelenideElement accountSelector = $("select.form-control.account-selector");
    private SelenideElement recipientNameInput = $(Selectors.byPlaceholder("Enter recipient name"));
    private SelenideElement recipientAccountNumberInput = $(Selectors.byPlaceholder("Enter recipient account number"));
    private SelenideElement amountInput = $(Selectors.byPlaceholder("Enter amount"));
    private SelenideElement transferButton = $(Selectors.withText("Send Transfer"));
    private SelenideElement infoConfirmCheckbox = $(By.id("confirmCheck"));


    @Override
    public String url() {
        return "/transfer";
    }

    public TransactionPage selectAccount(int accountNum) {
        accountSelector.selectOption(accountNum);
        return this;
    }

    public TransactionPage selectAccountByValue(String accName) {
        accountSelector.$$("option").shouldHave(sizeGreaterThan(2));
        var options = accountSelector.$$("option");
        var account = options.stream()
                .filter(acc -> acc.getText().contains(accName))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Can't find account: " + accName +
                        " in array: " + options.stream().map(SelenideElement::getText).toList()));
        String valueToSelect = account.getValue();
        accountSelector.selectOptionByValue(valueToSelect);
        return this;
    }

    public String getSelectedAccountName() {
        return accountSelector.getSelectedOption().getText().split(" ")[0];
    }

    public String getSelectedAccountOptionText() {
        return accountSelector.getSelectedOption().getText();
    }

    public List<String> getAllOptions() {
        return getAllOptions(accountSelector);
    }

    /*public List<TransactionAccount> getAllAccounts() {
        ElementsCollection collection = accountSelector.parent().findAll("option");
        return generatePageElement(collection, TransactionAccount::new);
    }*/

    public TransactionPage fillRecipientName(String name) {
        recipientNameInput.shouldBe(visible).sendKeys(name);
        return this;
    }

    public TransactionPage fillRecipientAccountNumber(String accountNumber) {
        recipientAccountNumberInput.shouldBe(visible).sendKeys(accountNumber);
        return this;
    }

    public TransactionPage fillAmount(String amount) {
        amountInput.shouldBe(visible).sendKeys(amount);
        return this;
    }

    public TransactionPage checkConfirmCheckbox() {
        setCheckboxState(infoConfirmCheckbox, true);
        return this;
    }

    public TransactionPage uncheckConfirmCheckbox() {
        setCheckboxState(infoConfirmCheckbox, false);
        return this;
    }

    public boolean isConfirmCheckboxSelected() {
        return isCheckboxSelected(infoConfirmCheckbox);
    }

    public TransactionPage performTransfer(int accountIndex, String recipientName,
            String recipientAccountNumber, String amount) {
        selectAccount(accountIndex);
        fillRecipientName(recipientName);
        fillRecipientAccountNumber(recipientAccountNumber);
        fillAmount(amount);
        checkConfirmCheckbox();
        transferButton.shouldBe(visible).click();
        return this;
    }

    public TransactionPage performTransferByValue(String accountValue, String recipientName,
            String recipientAccountNumber, String amount) {
        selectAccountByValue(accountValue);
        fillRecipientName(recipientName);
        fillRecipientAccountNumber(recipientAccountNumber);
        fillAmount(amount);
        checkConfirmCheckbox();
        transferButton.shouldBe(visible).click();
        return this;
    }

    public SelenideElement getLogoutButton() {
        return logoutButton;
    }

    public SelenideElement getHomeButton() {
        return homeButton;
    }

}

