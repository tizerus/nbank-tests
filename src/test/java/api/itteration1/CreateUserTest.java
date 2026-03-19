package api.itteration1;

import api.ApiBaseTest;
import api.comparison.ModelAssertions;
import api.generators.RandomModelGenerator;
import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.models.UserRole;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.CrudRequester;
import api.requests.skeleton.requests.ValidatableCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

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
