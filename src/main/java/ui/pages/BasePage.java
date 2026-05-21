package ui.pages;

import api.models.CreateUserRequest;
import api.models.User;
import api.specs.RequestSpecs;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import common.utils.StepLogger;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import ui.elements.BaseElement;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.codeborne.selenide.Condition.enabled;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;

public abstract class BasePage<T extends BasePage> {

    protected SelenideElement usernameInput = $(Selectors.byAttribute("placeholder", "Username"));
    protected SelenideElement passwordInput = $(Selectors.byAttribute("placeholder", "Password"));
    protected SelenideElement logoutButton = $(By.xpath("//button[contains(text(), 'Logout')]"));
    protected SelenideElement homeButton = $(By.xpath("//button[contains(text(), 'Home')]"));
    protected SelenideElement userNameText = $("span.user-name");

    public abstract String url();

    public T open() {
        return Selenide.open(url(), (Class<T>) this.getClass());
    }

    public T checkAlertMsgAndAccept(BankAlert msg) {
        Alert alert = switchTo().alert();
        Assertions.assertThat(alert.getText()).contains(msg.getMsg());
        alert.accept();
        return (T) this;
    }

    public T checkAlertMsgAndAccept(String msg) {
        Alert alert = switchTo().alert();
        Assertions.assertThat(alert.getText())
                .contains(msg);
        alert.accept();
        return (T) this;
    }

    public String getUserName() {
        return userNameText.shouldBe(visible).getText();
    }

    public static void authAsUser(String user, String password) {
        StepLogger.log("Auth as user: " + user, () -> {
            Selenide.open("/");
            String authToken = RequestSpecs.getUserAuthHeader(user, password);
            Selenide.executeJavaScript("localStorage.setItem('authToken', arguments[0]);", authToken);
        });
    }

    public static void authAsUser(CreateUserRequest user) {
        StepLogger.log("Auth as user: " + user.getUsername(), () -> {
            Selenide.open("/");
            String authToken = RequestSpecs.getUserAuthHeader(user.getUsername(), user.getPassword());
            Selenide.executeJavaScript("localStorage.setItem('authToken', arguments[0]);", authToken);
        });

    }

    public static void authAsUser(User user) {
        StepLogger.log("Auth as user: " + user.getUserName(), () -> {
            Selenide.open("/");
            String authToken = RequestSpecs.getUserAuthHeader(user.getUserName(), user.getPassword());
            Selenide.executeJavaScript("localStorage.setItem('authToken', arguments[0]);", authToken);
        });

    }

    //ElementCollection -> List<BaseElement>
    protected <T extends BaseElement> List<T> generatePageElement(ElementsCollection collection,
            Function<SelenideElement, T> constructor) {
        return collection.stream().map(constructor).toList();
    }

    protected T checkCheckbox(SelenideElement checkbox) {
        checkbox.shouldBe(visible)
                .shouldBe(enabled);

        if (!checkbox.isSelected()) {
            checkbox.click();
        }
        return (T) this;
    }

    protected T uncheckCheckbox(SelenideElement checkbox) {
        checkbox.shouldBe(visible)
                .shouldBe(enabled);

        if (checkbox.isSelected()) {
            checkbox.click();
        }
        return (T) this;
    }

    protected boolean isCheckboxSelected(SelenideElement checkbox) {
        return checkbox.shouldBe(visible).isSelected();
    }

    protected T setCheckboxState(SelenideElement checkbox, boolean state) {
        if (state) {
            checkCheckbox(checkbox);
        } else {
            uncheckCheckbox(checkbox);
        }
        return (T) this;
    }

    public List<String> getAllOptions(SelenideElement element) {
        return element.findAll("option")
                .stream()
                .map(option -> option.getText())
                .collect(Collectors.toList());
    }

}
