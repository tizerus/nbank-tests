package api.itteration1;

import api.ApiBaseTest;
import api.comparison.ModelAssertions;
import api.generators.RandomModelGenerator;
import api.models.CreateUserRequest;
import api.models.CreateUserResponse;
import api.models.UserRole;
import common.annotations.SkipForBrokenImage;
import db.DbService;
import db.models.Customer;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import api.requests.skeleton.Endpoint;
import api.requests.skeleton.requests.CrudRequester;
import api.requests.skeleton.requests.ValidatableCrudRequester;
import api.specs.RequestSpecs;
import api.specs.ResponseSpecs;

import java.util.List;
import java.util.stream.Stream;

import static db.Tables.CUSTOMERS;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.containsString;

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
        Customer dbUser = DbService.withConnection(ctx -> ctx.select()
                .from(CUSTOMERS.getTableName())
                .where("id = ?", createUserResponse.getId())
                .fetchInto(Customer.class)
                .getFirst());

        ModelAssertions.assertThatModels(dbUser, createUserResponse).match();
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
    @SkipForBrokenImage
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

    @Test
    public void adminCanDeleteUserTest() {
        CreateUserRequest createUserRequest = RandomModelGenerator.generate(CreateUserRequest.class);

        CreateUserResponse createUserResponse = new ValidatableCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityCreated())
                .post(createUserRequest);

        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.requestReturnsOk())
                .delete(createUserResponse.getId(), null);

        List<CreateUserResponse> userAfterDelete = new ValidatableCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.requestReturnsOk())
                .getAll();

        Assertions.assertThat(createUserResponse).isNotIn(userAfterDelete);
    }

    @Test
    public void adminCanNotDeleteNonExistentUserTest() {
        long nonExistentId = 999999L;

        new CrudRequester(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.requestReturns404())
                .delete(nonExistentId, null);
    }

    @Test
    public void unAuthUserCanNotDeleteUserTest() {
        CreateUserRequest createUserRequest = RandomModelGenerator.generate(CreateUserRequest.class);

        CreateUserResponse createUserResponse = new ValidatableCrudRequester<CreateUserResponse>(
                RequestSpecs.adminSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.entityCreated())
                .post(createUserRequest);

        new CrudRequester(
                RequestSpecs.unAuthSpec(),
                Endpoint.ADMIN_USER,
                ResponseSpecs.requestReturns401())
                .delete(createUserResponse.getId(), null);
    }

    @Test
    public void adminCanNotDeleteUserWithInvalidIdFormatTest() {
        // Используем прямой вызов с некорректным ID
        String invalidUrl = Endpoint.ADMIN_USER.getUrl().replace("{id}", "invalid");

        given()
                .spec(RequestSpecs.adminSpec())
                .delete(invalidUrl)
                .then()
                .assertThat()
                .statusCode(405);
    }

}
