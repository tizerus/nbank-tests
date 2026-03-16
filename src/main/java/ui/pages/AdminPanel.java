package ui.pages;

import api.models.CreateUserRequest;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Selenide.$;

@Getter
public class AdminPanel extends BasePage<AdminPanel> {

    private final SelenideElement adminPanelText = $(Selectors.byText("Admin Panel"));
    private final SelenideElement addUserButton = $(Selectors.byText("Add User"));


    @Override
    public String url() {
        return "/admin";
    }

    public AdminPanel createUser(CreateUserRequest userRequest) {
        usernameInput.sendKeys(userRequest.getUsername());
        passwordInput.sendKeys(userRequest.getPassword());
        addUserButton.click();
        return this;
    }

    public ElementsCollection getAllUsers() {
        return $(Selectors.byText("All Users")).parent().findAll("li");
    }

}
