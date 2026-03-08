package api.itteration2;

import api.ApiBaseTest;
import models.GetTransferRequest;
import models.Transaction;
import models.TransactionType;
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

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.groups.Tuple.tuple;

public class UserTransactionTest extends ApiBaseTest {

    public static Stream<Arguments> invalidAmountData() {
        return Stream.of(
                Arguments.of(10000.01F, "Transfer amount cannot exceed 10000"),
                Arguments.of(0, "Transfer amount must be at least 0.01"),
                Arguments.of(-0.01F, "Transfer amount must be at least 0.01")
                        );
    }

    @MethodSource("invalidAmountData")
    @ParameterizedTest
    public void userCannotTransferTest(float amount, String error) {
        User user1 = AdminSteps.createUserAndAcc(1);
        User user2 = AdminSteps.createUserAndAcc(1);

        new CrudRequester(
                RequestSpecs.authAsUser(user1.getUserRequest().getUsername(), user1.getUserRequest().getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadResponse(Matchers.equalTo(error)))
                .post(GetTransferRequest.builder()
                        .senderAccountId(user1.getAccountResponse().get(0).getId())
                        .receiverAccountId(user2.getAccountResponse().get(0).getId())
                        .amount(amount)
                        .build());
    }

    @Test
    public void userCannotTransferMoreThanHeHas() {
        float amountToTransfer = 3000F;
        User user1 = AdminSteps.createUserAndAcc(1);
        User user2 = AdminSteps.createUserAndAcc(1);

        UserSteps.putDeposit(user1.getUserRequest(), user1.getAccountResponse().get(0), amountToTransfer);

        new CrudRequester(
                RequestSpecs.authAsUser(user1.getUserRequest().getUsername(), user1.getUserRequest().getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadResponse(
                        Matchers.equalTo("Invalid transfer: insufficient funds or invalid accounts")))
                .post(GetTransferRequest.builder()
                        .senderAccountId(user1.getAccountResponse().get(0).getId())
                        .receiverAccountId(user2.getAccountResponse().get(0).getId())
                        .amount(amountToTransfer + 0.01F)
                        .build());

    }

    @Test
    public void userCanTransferToAnotherAcc() {
        float amount = 3000F;
        User user1 = AdminSteps.createUserAndAcc(1);
        User user2 = AdminSteps.createUserAndAcc(1);

        UserSteps.putDeposit(user1.getUserRequest(), user1.getAccountResponse().get(0), amount);
        UserSteps.transfer(user1.getUserRequest(), user1.getAccountResponse().get(0),
                user2.getAccountResponse().get(0), amount);

        List<Transaction> transferResponse = new ValidatableCrudRequester<Transaction>(
                RequestSpecs.authAsUser(user1.getUserRequest().getUsername(), user1.getUserRequest().getPassword()),
                Endpoint.TRANSACTION,
                ResponseSpecs.requestReturnsOk())
                .getAll(user1.getAccountResponse().get(0).getId());

        Assertions.assertThat(transferResponse)
                .extracting(Transaction::getAmount,
                        Transaction::getType,
                        Transaction::getRelatedAccountId)
                .contains(
                        tuple(
                                amount,
                                TransactionType.TRANSFER_OUT,
                                user2.getAccountResponse().get(0).getId()
                             )
                         );
    }

    @Test
    public void userCanTransferToHisSecondAcc() {
        float amountToTransfer = 3000F;
        User user1 = AdminSteps.createUserAndAcc(2);

        UserSteps.putDeposit(user1.getUserRequest(), user1.getAccountResponse().get(0), amountToTransfer);
        UserSteps.transfer(user1.getUserRequest(), user1.getAccountResponse().get(0),
                user1.getAccountResponse().get(1), amountToTransfer);

        List<Transaction> transferResponse = new ValidatableCrudRequester<Transaction>(
                RequestSpecs.authAsUser(user1.getUserRequest().getUsername(), user1.getUserRequest().getPassword()),
                Endpoint.TRANSACTION,
                ResponseSpecs.requestReturnsOk())
                .getAll(user1.getAccountResponse().get(0).getId());

        Assertions.assertThat(transferResponse)
                .extracting(Transaction::getAmount,
                        Transaction::getType,
                        Transaction::getRelatedAccountId)
                .contains(
                        tuple(
                                amountToTransfer,
                                TransactionType.TRANSFER_OUT,
                                user1.getAccountResponse().get(1).getId()
                             )
                         );

    }

}
