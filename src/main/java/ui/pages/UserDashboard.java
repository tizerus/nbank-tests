package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.withText;
import static com.codeborne.selenide.Selenide.$;

@Getter
public class UserDashboard extends BasePage<UserDashboard> {

    private SelenideElement welcomeText = $(Selectors.byClassName("welcome-text"));
    private SelenideElement userDashboardTextElement = $(Selectors.byText("User Dashboard"));
    private SelenideElement createUserAccountButton = $(withText("Create New Account"));
    private SelenideElement depositMoneyButton = $(Selectors.byXpath("//button[contains(text(), 'Deposit Money')]"));
    private SelenideElement makeTransferButton = $(withText("Make a Transfer"));

    @Override
    public String url() {
        return "/dashboard";
    }

    public UserDashboard createUserAccount() {
        createUserAccountButton.shouldBe(visible).click();
        return this;
    }

}
