package ui.itteration1;

import api.comparison.ModelAssertions;
import api.generators.RandomModelGenerator;
import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.requests.steps.AdminSteps;
import com.codeborne.selenide.Condition;
import common.annotations.AdminSession;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import ui.BaseUiTest;
import ui.pages.AdminPanel;
import ui.pages.BankAlert;

public class CreateUserTest extends BaseUiTest {

    @Test
    @AdminSession
    public void adminCanCreateUserTest() {
        CreateUserRequest userRequest = RandomModelGenerator.generate(CreateUserRequest.class);
        boolean match = new AdminPanel().open().createUser(userRequest)
                .checkAlertMsgAndAccept(BankAlert.USER_CREATED_SUCCESSFULLY)
                .getAllUsers().stream()
                .anyMatch(userBage -> userBage.getUsername().equals(userRequest.getUsername()));
        Assertions.assertThat(match)
                .withFailMessage("Пользователь с именем % не наиден", userRequest.getUsername())
                .isTrue();

        CreateUserResponse createdUser = AdminSteps.getAllUsers().stream()
                .filter(user -> user.getUsername().equals(userRequest.getUsername()))
                .findFirst().get();

        ModelAssertions.assertThatModels(userRequest, createdUser);
    }

    @Test
    @AdminSession
    public void adminCannotCreateUserWithInvalidDataTest() {
        CreateUserRequest userRequest = RandomModelGenerator.generate(CreateUserRequest.class);
        userRequest.setUsername("Oh");
        boolean match = new AdminPanel().open().createUser(userRequest)
                .checkAlertMsgAndAccept(BankAlert.USERNAME_MUST_BE_BETWEEN_3_15)
                .getAllUsers().stream()
                .anyMatch(userBage -> userBage.getUsername().equals(userRequest.getUsername()));
        Assertions.assertThat(match)
                .withFailMessage("Пользователь с именем % наиден, но не должен был быть создан", userRequest.getUsername())
                .isFalse();

        long createdUsersWithSameNameAsNewUser = AdminSteps.getAllUsers().stream()
                .filter(user -> user.getUsername().equals(userRequest.getUsername()))
                .count();

        ModelAssertions.assertThatModels(createdUsersWithSameNameAsNewUser, 0);
    }

}
