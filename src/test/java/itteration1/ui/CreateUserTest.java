package itteration1.ui;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import comparison.ModelAssertions;
import generators.RandomModelGenerator;
import io.restassured.RestAssured;
import models.CreateUserRequest;
import models.CreateUserResponse;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import specs.RequestSpecs;

import java.util.Arrays;
import java.util.Map;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;

public class CreateUserTest {

    @BeforeEach
    public void selenideSetup() {
        Configuration.remote = "http://localhost:4444/wd/hub";
        Configuration.baseUrl = "http://172.19.48.1:3000";
        Configuration.browserSize = "1920x1080";
        Configuration.browser = "chrome";

        Configuration.browserCapabilities.setCapability("sselenoid:options",
                Map.of("enableVNC", true, "enableLog", true)
                                                       );
    }

    @Test
    public void adminCanCreateUserTest() {
        //step 1 - create admin user
        CreateUserRequest admin = CreateUserRequest.builder()
                .username("admin")
                .password("admin")
                .build();
        Selenide.open("/login");

        //step 2 - login as admin
        $(Selectors.byAttribute("placeholder", "Username"))
                .sendKeys(admin.getUsername());
        $(Selectors.byAttribute("placeholder", "Password"))
                .sendKeys(admin.getPassword());
        $("button").click();

        $(Selectors.byText("Admin Panel")).shouldBe(Condition.visible);

        // step 3 - create user by admin
        CreateUserRequest userRequest = RandomModelGenerator.generate(CreateUserRequest.class);
        $(Selectors.byAttribute("placeholder", "Username"))
                .sendKeys(userRequest.getUsername());
        $(Selectors.byAttribute("placeholder", "Password"))
                .sendKeys(userRequest.getPassword());
        $(Selectors.byText("Add User")).click();

        // step 4 check that we have alert User created successfully!
        Alert alert = switchTo().alert();
        Assertions.assertEquals(alert.getText(), "User created successfully!" + "\nUser");
        alert.accept();

        //step 5 find user in user list in UI
        ElementsCollection allUsersFromDashboard = $(Selectors.byText("All Users")).parent().findAll("li");
        allUsersFromDashboard.findBy(Condition.exactText(userRequest.getUsername())).shouldBe(Condition.visible);

        //step 6 find created user in by API
        CreateUserResponse[] userResponses = RestAssured.given()
                .spec(RequestSpecs.adminSpec())
                .get("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(CreateUserResponse[].class);

        CreateUserResponse createdUser = Arrays.stream(userResponses)
                .filter(user -> user.getUsername().equals(userRequest.getUsername()))
                .findFirst().get();
        ModelAssertions.assertThatModels(userRequest, createdUser);
    }

    @Test
    public void adminCannotCreateUserWithInvalidDataTest() {
        //step 1 - create admin user
        CreateUserRequest admin = CreateUserRequest.builder()
                .username("admin")
                .password("admin")
                .build();
        Selenide.open("/login");

        //step 2 - login as admin
        $(Selectors.byAttribute("placeholder", "Username"))
                .sendKeys(admin.getUsername());
        $(Selectors.byAttribute("placeholder", "Password"))
                .sendKeys(admin.getPassword());
        $("button").click();

        $(Selectors.byText("Admin Panel")).shouldBe(Condition.visible);

        // step 3 - create user by admin
        CreateUserRequest userRequest = RandomModelGenerator.generate(CreateUserRequest.class);
        userRequest.setUsername("Oh");
        $(Selectors.byAttribute("placeholder", "Username"))
                .sendKeys(userRequest.getUsername());
        $(Selectors.byAttribute("placeholder", "Password"))
                .sendKeys(userRequest.getPassword());
        $(Selectors.byText("Add User")).click();

        // step 4 check that we have alert User must be between 3 and 15 characters
        Alert alert = switchTo().alert();
        Assertions.assertEquals(alert.getText(), "User must be between 3 and 15 characters");
        alert.accept();

        //step 5 we have no user on UI
        ElementsCollection allUsersFromDashboard = $(Selectors.byText("All Users")).parent().findAll("li");
        allUsersFromDashboard.findBy(Condition.exactText(userRequest.getUsername())).shouldNotBe(Condition.exist);

        //step 6 find created user in by API
        CreateUserResponse[] userResponses = RestAssured.given()
                .spec(RequestSpecs.adminSpec())
                .get("http://localhost:4111/api/v1/admin/users")
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .as(CreateUserResponse[].class);

        long createdUsersWithSameNameAsNewUser = Arrays.stream(userResponses)
                .filter(user -> user.getUsername().equals(userRequest.getUsername()))
                .count();

        ModelAssertions.assertThatModels(createdUsersWithSameNameAsNewUser, 0);
    }

}
