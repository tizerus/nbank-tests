package ui.pages;

import api.models.CreateUserRequest;
import api.specs.RequestSpecs;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.Alert;
import ui.elements.BaseElement;

import java.util.List;
import java.util.function.Function;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;

public abstract class BasePage<T extends BasePage> {

    protected SelenideElement usernameInput = $(Selectors.byAttribute("placeholder", "Username"));
    protected SelenideElement passwordInput = $(Selectors.byAttribute("placeholder", "Password"));

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
        Assertions.assertThat(alert.getText()).contains(msg);
        alert.accept();
        return (T) this;
    }

    public static void authAsUser(String user, String password) {
        Selenide.open("/");
        String authToken = RequestSpecs.getUserAuthHeader(user, password);
        Selenide.executeJavaScript("localStorage.setItem('authToken', arguments[0]);", authToken);
    }

    public static void authAsUser(CreateUserRequest user) {
        Selenide.open("/");
        String authToken = RequestSpecs.getUserAuthHeader(user.getUsername(), user.getPassword());
        Selenide.executeJavaScript("localStorage.setItem('authToken', arguments[0]);", authToken);
    }

    //ElementCollection -> List<BaseElement>
    protected <T extends BaseElement> List<T> generatePageElement(ElementsCollection collection, Function<SelenideElement, T> constructor) {
        return collection.stream().map(constructor).toList();
    }

}
