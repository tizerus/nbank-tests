package ui.pages;

import api.models.CreateUserRequest;
import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import ui.elements.UserBage;

import java.util.List;

import static com.codeborne.selenide.Condition.visible;
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
        adminPanelText.shouldBe(visible);
        usernameInput.sendKeys(userRequest.getUsername());
        passwordInput.sendKeys(userRequest.getPassword());
        addUserButton.click();
        return this;
    }

    public List<UserBage> getAllUsers() {
        ElementsCollection collection = $(Selectors.byText("All Users")).parent().findAll("li");
        collection.shouldHave(CollectionCondition.sizeGreaterThan(0));
        return generatePageElement(collection, UserBage::new);
    }

}
