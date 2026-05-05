package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

@Getter
public class EditProfilePage extends BasePage<EditProfilePage> {

    private SelenideElement editProfileTitle = $(Selectors.byXpath("//h1[contains(text(), 'Edit Profile')]"));
    private SelenideElement nameInput = $(Selectors.byPlaceholder("Enter new name"));
    private SelenideElement saveChangesButton = $(Selectors.byXpath("//button[contains(text(), 'Save Changes')]"));

    @Override
    public String url() {
        return "/edit-profile";
    }

    public EditProfilePage editProfileName(String newName) {
        Selenide.sleep(300);
        nameInput.shouldBe(visible).click();
        nameInput.shouldBe(visible).sendKeys(newName);
        saveChangesButton.shouldBe(visible).click();
        return this;
    }

}
