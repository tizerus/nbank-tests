package ui.pages;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import org.assertj.core.api.Assertions;
import org.openqa.selenium.Alert;

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

}
