package ui.pages;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@Getter
public class UserDashboard extends BasePage<UserDashboard> {

    private SelenideElement welcomeText = $(Selectors.byClassName("welcome-text"));
    private SelenideElement userDashboardTextElement = $(Selectors.byText("User Dashboard"));
    private SelenideElement createUserAccountButton = $(Selectors.withText("Create New Account"));

    @Override
    public String url() {
        return "/dashboard";
    }

    public UserDashboard createUserAccount() {
        createUserAccountButton.shouldBe(visible).click();
        return this;
    }

}
