package api.itteration1;

import api.ApiBaseTest;
import comparison.ModelAssertions;
import generators.RandomModelGenerator;
import models.CreateUserRequest;
import models.CreateUserResponse;
import models.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.CrudRequester;
import requests.skeleton.requests.ValidatableCrudRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

public class CreateUserTest extends ApiBaseTest {

    @Test
    public void adminCanCreateUserWithCorrectDataTest() {
        CreateUserRequest createUserRequest = RandomModelGenerator.generate(CreateUserRequest.class);

        CreateUserResponse createUserResponse = new ValidatableCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityCreated())
                .post(createUserRequest);

        ModelAssertions.assertThatModels(createUserRequest, createUserResponse).match();
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
        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.requestReturnsBadResponse(errorKey, error)
        )
                .post(createUserRequest);
    }

}
