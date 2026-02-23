package itteration1;

import generators.RandomData;
import models.CreateUserRequest;
import models.CreateUserResponse;
import models.LoginUserRequest;
import models.UserRole;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import requests.AdminCreateUserRequester;
import requests.LoginUserRequester;
import specs.RequestSpecs;
import specs.ResponseSpecs;

public class LoginUserTest extends BaseTest {

    @Test
    public void adminCanGenerateAuthTokenTest() {
        CreateUserRequest loginUserRequest = CreateUserRequest.builder()
                .username(RandomData.getUserName())
                .password(RandomData.getPassword())
                .role(UserRole.USER.toString())
                .build();
        CreateUserResponse createUserResponse = new AdminCreateUserRequester(
                RequestSpecs.adminSpec(),
                ResponseSpecs.entityCreated()
        )
                .post(loginUserRequest)
                .extract()
                .as(CreateUserResponse.class);

        new LoginUserRequester(
                RequestSpecs.unAuthSpec(),
                ResponseSpecs.requestReturnsOk()
        )
                .post(LoginUserRequest.builder()
                        .username(loginUserRequest.getUsername())
                        .password(loginUserRequest.getPassword())
                        .role(loginUserRequest.getRole())
                        .build())
                .header("Authorization", Matchers.notNullValue());
    }

}
