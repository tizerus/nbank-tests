package api.itteration1;

import api.ApiBaseTest;
import api.generators.RandomModelGenerator;
import api.models.CreateAccountResponse;
import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.models.LoginUserRequest;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.CrudRequester;
import api.requests.skeleton.requests.ValidatableCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class LoginUserTest extends ApiBaseTest {

    @Test
    public void adminCanGenerateAuthTokenTest() {
        CreateUserRequest userRequest = CreateUserRequest.builder()
                .username("admin")
                .password("admin")
                .build();

        new ValidatableCrudRequester<CreateAccountResponse> (
                RequestSpecs.unAuthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOk()
        )
                .post(userRequest);
    }

    @Test
    public void userCanGenerateAuthTokenTest() {
        CreateUserRequest createUserRequest = RandomModelGenerator.generate(CreateUserRequest.class);

        new ValidatableCrudRequester<CreateUserResponse> (
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityCreated())
                .post(createUserRequest);

        new CrudRequester(
                RequestSpecs.unAuthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturnsOk())
                .post(LoginUserRequest.builder()
                        .username(createUserRequest.getUsername())
                        .password(createUserRequest.getPassword())
                        .role(createUserRequest.getRole())
                        .build());
    }

    @Test
    @DisplayName("Negative: Login with non-existent user returns 404")
    public void loginWithNonExistentUserTest() {
        new CrudRequester(
                RequestSpecs.unAuthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturns401())
                .post(LoginUserRequest.builder()
                        .username("nonexistent_user_12345")
                        .password("password123")
                        .role("USER")
                        .build());
    }

    @Test
    @DisplayName("Negative: Login without username returns 401")
    public void loginWithoutUsernameTest() {
        new CrudRequester(
                RequestSpecs.unAuthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturns401())
                .post(LoginUserRequest.builder()
                        .password("password123")
                        .role("USER")
                        .build());
    }

    @Test
    @DisplayName("Negative: Login without password returns 401")
    public void loginWithoutPasswordTest() {
        new CrudRequester(
                RequestSpecs.unAuthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturns401())
                .post(LoginUserRequest.builder()
                        .username("testuser")
                        .role("USER")
                        .build());
    }

    @Test
    @DisplayName("Negative: Login with empty body returns 401")
    public void loginWithEmptyBodyTest() {

        new CrudRequester(
                RequestSpecs.unAuthSpec(),
                Endpoint.LOGIN,
                ResponseSpecs.requestReturns401())
                .post(null);
    }

}
