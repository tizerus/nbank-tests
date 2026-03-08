package itteration2;

import comparison.ModelAssertions;
import generators.RandomData;
import io.restassured.response.ValidatableResponse;
import itteration1.BaseTest;
import models.DepositRequest;
import models.DepositResponse;
import models.Profile;
import models.User;
import org.assertj.core.api.Assertions;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import requests.skeleton.Endpoint;
import requests.skeleton.requests.CrudRequester;
import requests.skeleton.requests.ValidatableCrudRequester;
import requests.steps.AdminSteps;
import requests.steps.UserSteps;
import specs.RequestSpecs;
import specs.ResponseSpecs;

import java.util.stream.Stream;

public class UserDepositTest extends BaseTest {

    public static Stream<Arguments> invalidAmountData() {
        return Stream.of(
                Arguments.of(-0.01F, "Deposit amount must be at least 0.01"),
                Arguments.of(5000.01F, "Deposit amount cannot exceed 5000"),
                Arguments.of(0, "Deposit amount must be at least 0.01")
                        );
    }

    @MethodSource("invalidAmountData")
    @ParameterizedTest
    public void userCannotPutDepositTest(float amount, String error) {
        User user = AdminSteps.createUserAndAcc(1);

        float balanceBeforeDeposit = user.getBalance(user.getAccountsNumbers().get(0));

        new CrudRequester(
                RequestSpecs.authAsUser(user.getUserRequest().getUsername(), user.getUserRequest().getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturnsBadResponse(Matchers.equalTo(error)))
                .post(DepositRequest.builder()
                        .id(user.getAccountResponse().get(0).getId())
                        .balance(amount)
                        .build());

        float balanceAfterDeposit = user.getBalance(user.getAccountsNumbers().get(0));

        Assertions.assertThat(balanceBeforeDeposit).isEqualTo(balanceAfterDeposit);
    }

    public static Stream<Arguments> validAmountData() {
        return Stream.of(
                Arguments.of(0.01F),
                Arguments.of(5000.0F),
                Arguments.of(3000F)
                        );
    }

    @MethodSource("validAmountData")
    @ParameterizedTest
    public void userCanPutDepositTest(float amount) {
        User user = AdminSteps.createUserAndAcc(1);
        DepositRequest depositRequest = DepositRequest.builder()
                .id(user.getAccountResponse().get(0).getId())
                .balance(amount)
                .build();
        DepositResponse depositResponse = UserSteps.putDeposit(user.getUserRequest(), depositRequest);

        ModelAssertions.assertThatModels(depositRequest, depositResponse).match();

        float balanceAfterDeposit = user.getBalance(depositResponse.getAccountNumber());

        Assertions.assertThat(amount).isEqualTo(balanceAfterDeposit);
    }

    @Test
    public void userCannotDepositToAnotherUserAcc() {
        User user1 = AdminSteps.createUserAndAcc(1);
        User user2 = AdminSteps.createUserAndAcc(1);

        float balanceBeforeDeposit = user2.getBalance(user2.getAccountsNumbers().get(0));

        new CrudRequester(
                RequestSpecs.authAsUser(user1.getUserRequest().getUsername(), user1.getUserRequest().getPassword()),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturns403())
                .post(DepositRequest.builder()
                        .id(user2.getAccountResponse().get(0).getId())
                        .balance(RandomData.generateFloatInclusive(0.01F, 5000))
                        .build());

        float balanceAfterDeposit = user2.getBalance(user2.getAccountsNumbers().get(0));

        Assertions.assertThat(balanceBeforeDeposit).isEqualTo(balanceAfterDeposit);
    }

    @Test
    public void unauthUserCannotDepositToAcc() {
        new CrudRequester(
                RequestSpecs.unAuthSpec(),
                Endpoint.DEPOSIT,
                ResponseSpecs.requestReturns401())
                .post(DepositRequest.builder()
                        .id(1)
                        .balance(RandomData.generateFloatInclusive(0.01F, 5000))
                        .build());
    }

}
