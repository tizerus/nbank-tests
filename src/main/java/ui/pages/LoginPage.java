package ui.pages;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class LoginPage extends BasePage<LoginPage> {

    private SelenideElement button = $("button");


    @Override
    public String url() {
        return "/login";
    }

    public <T extends BasePage<T>> T login(String userName, String password, Class<T> expectedPage) {
        usernameInput.sendKeys(userName);
        passwordInput.sendKeys(password);
        button.click();
        return Selenide.page(expectedPage);
    }
}

