package itteration1.ui;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import generators.RandomModelGenerator;
import models.CreateUserRequest;
import models.LoginUserRequest;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.CrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;

public class CreateAccountTest {

    @BeforeEach
    public void selenideSetup() {
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://172.19.48.1:3000";
        Configuration.browserSize = "1920x1080";
        Configuration.browser = "chrome";

        Configuration.browserCapabilities.setCapability("sselenoid:options",
                Map.of("enableVNC", true, "enableLog", true));
    }

    @Test
    public void userCanCreateAccountTest() {
        //admin login to bank a08b6d46cbbb63405301d61fa39796a2fb077b08
        //admin create user
        //user login into the bank
        //user created his nwe account
        CreateUserRequest createUserRequest = RandomModelGenerator.generate(CreateUserRequest.class);
        String authToken = new CrudRequester(
                RequestSpecs.unAuthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOk())
                .post(LoginUserRequest.builder()
                        .username(createUserRequest.getUsername())
                        .password(createUserRequest.getPassword())
                        .role(createUserRequest.getRole())
                        .build())
                .header("Authorization", Matchers.notNullValue())
                .extract()
                .header("Authorization");
        Selenide.open("/");
        Selenide.executeJavaScript("localStorage.setItem('authToken', arguments[0]);", authToken);
        Selenide.open("/dashboard");

        $(Selectors.byText("Create New Account")).click();
        Alert alert = switchTo().alert();
        Assertions.assertEquals(alert.getText(), "Account Number Created! Account Number:");
        alert.accept();

        Pattern pattern = Pattern.compile("Account Number: (\\w+)");
        Matcher matcher = pattern.matcher(alert.getText());

        matcher.find();
        String accName = matcher.group(1);
    }

}
