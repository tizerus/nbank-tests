package itteration1;

import generators.RandomData;
import models.CreateUserRequest;
import models.CreateUserResponse;
import models.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.AdminCreateUserRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

public class CreateUserTest extends BaseTest {

    @Test
    public void adminCanCreateUserWithCorrectDataTest() {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username(RandomData.getUserName())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();
        CreateUserResponse createUserResponse = new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityCreated()
        )
                .post(createUserRequest)
                .extract()
                .as(CreateUserResponse.class);

        softAssert.assertThat(createUserRequest.getUsername()).isEqualTo(createUserResponse.getUsername());
        softAssert.assertThat(createUserRequest.getPassword()).isNotEqualTo(createUserResponse.getPassword());
        softAssert.assertThat(createUserRequest.getRole()).isNotEqualTo(createUserResponse.getRole());

    }

    public static Stream<Arguments> invalidUserData() {
        return Stream.of(
                Arguments.of("  ", "Password12$", UserRole.USER.toString(), "username", "Username cannot be blank"),
                Arguments.of("ab", "Password12$", UserRole.USER.toString(), "username", "Username must be between 3 and 15 characters"),
                Arguments.of("abc$", "Password12$", UserRole.USER.toString(), "username", "Username must contain only letters, digits, dashes, underscores, and dots"),
                Arguments.of("abc%", "Password12$", UserRole.USER.toString(), "username", "Username must contain only letters, digits, dashes, underscores, and dots")
                        );
    }

    @MethodSource("invalidUserData")
    @ParameterizedTest
    public void adminCanNotCreateUserWithIncorrectDataTest(String userName, String pass, String role,
            String errorKey, String error) {
        CreateUserRequest createUserRequest = CreateUserRequest.builder()
                .username(userName)
                .password(pass)
                .role(role)
                .build();
        new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.requestReturnsBadResponse(errorKey, error)
        )
                .post(createUserRequest);
    }

}
