package ui.itteration1;

import api.requests.steps.AdminSteps;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.Selenide;
import api.comparison.ModelAssertions;
import api.generators.RandomModelGenerator;
import io.restassured.RestAssured;
import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.Alert;
import api.specs.RequestSpecs;
import ui.BaseUiTest;
import ui.pages.AdminPanel;
import ui.pages.BankAlert;

import java.util.Arrays;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.switchTo;

public class CreateUserTest extends BaseUiTest {

    @Test
    public void adminCanCreateUserTest() {
        //step 1 - create admin user
        CreateUserRequest admin = CreateUserRequest.getAdmin();
        authAsUser(admin);

        // step 3 - create user by admin
        CreateUserRequest userRequest = RandomModelGenerator.generate(CreateUserRequest.class);
        new AdminPanel().open().createUser(userRequest)
                .checkAlertMsgAndAccept(BankAlert.USER_CREATED_SUCCESSFULLY)
                .getAllUsers()
                .findBy(Condition.exactText(userRequest.getUsername() + "\nUser")).shouldBe(Condition.visible);

        //step 6 find created user in by API

        CreateUserResponse createdUser = AdminSteps.getAllUsers().stream()
                .filter(user -> user.getUsername().equals(userRequest.getUsername()))
                .findFirst().get();

        ModelAssertions.assertThatModels(userRequest, createdUser);
    }

    @Test
    public void adminCannotCreateUserWithInvalidDataTest() {
        //step 1 - create admin user
        CreateUserRequest admin = CreateUserRequest.getAdmin();
        authAsUser(admin);

        // step 3 - create user by admin
        CreateUserRequest userRequest = RandomModelGenerator.generate(CreateUserRequest.class);
        userRequest.setUsername("Oh");
        new AdminPanel().open().createUser(userRequest)
                .checkAlertMsgAndAccept(BankAlert.USERNAME_MUST_BE_BETWEEN_3_15)
                .getAllUsers()
                .findBy(Condition.exactText(userRequest.getUsername() + "\nUser")).shouldNot(Condition.exist);

        long createdUsersWithSameNameAsNewUser = AdminSteps.getAllUsers().stream()
                .filter(user -> user.getUsername().equals(userRequest.getUsername()))
                .count();

        ModelAssertions.assertThatModels(createdUsersWithSameNameAsNewUser, 0);
    }

}
