package itteration2;

import generators.RandomData;
import itteration1.BaseTest;
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

public class UserTransactionTest extends BaseTest {

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

        float balanceBeforeTransfer1 = user2.getBalance(user2.getAccountsNumbers().get(0));
        float balanceBeforeTransfer2 = user2.getBalance(user2.getAccountsNumbers().get(0));

        new CrudRequester(
                RequestSpecs.authAsUser(user1.getUserRequest().getUsername(), user1.getUserRequest().getPassword()),
                Endpoint.TRANSFER,
                ResponseSpecs.requestReturnsBadResponse(Matchers.equalTo(error)))
                .post(GetTransferRequest.builder()
                        .senderAccountId(user1.getAccountResponse().get(0).getId())
                        .receiverAccountId(user2.getAccountResponse().get(0).getId())
                        .amount(amount)
                        .build());

        float user1BalanceAfterDeposit = user2.getBalance(user2.getAccountsNumbers().get(0));
        float user2BalanceAfterDeposit = user2.getBalance(user2.getAccountsNumbers().get(0));

        Assertions.assertThat(balanceBeforeTransfer1).isEqualTo(user1BalanceAfterDeposit);
        Assertions.assertThat(balanceBeforeTransfer2).isEqualTo(user2BalanceAfterDeposit);
    }

    @Test
    public void userCannotTransferMoreThanHeHas() {
        float amountToTransfer = RandomData.generateFloatInclusive(0.01F, 5000);
        User user1 = AdminSteps.createUserAndAcc(1);
        User user2 = AdminSteps.createUserAndAcc(1);

        UserSteps.putDeposit(user1.getUserRequest(), user1.getAccountResponse().get(0), amountToTransfer);

        float balanceBeforeTransfer1 = user2.getBalance(user2.getAccountsNumbers().get(0));
        float balanceBeforeTransfer2 = user2.getBalance(user2.getAccountsNumbers().get(0));

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

        float user1BalanceAfterDeposit = user2.getBalance(user2.getAccountsNumbers().get(0));
        float user2BalanceAfterDeposit = user2.getBalance(user2.getAccountsNumbers().get(0));

        Assertions.assertThat(balanceBeforeTransfer1).isEqualTo(user1BalanceAfterDeposit);
        Assertions.assertThat(balanceBeforeTransfer2).isEqualTo(user2BalanceAfterDeposit);
    }

    @Test
    public void userCanTransferToAnotherAcc() {
        float amount = 5000;
        float amountToTransfer = 10000;
        User user1 = AdminSteps.createUserAndAcc(1);
        User user2 = AdminSteps.createUserAndAcc(1);

        UserSteps.putDeposit(user1.getUserRequest(), user1.getAccountResponse().get(0), amount);
        UserSteps.putDeposit(user1.getUserRequest(), user1.getAccountResponse().get(0), amount);

        float balanceBeforeTransfer1 = user2.getBalance(user2.getAccountsNumbers().get(0));
        float balanceBeforeTransfer2 = user2.getBalance(user2.getAccountsNumbers().get(0));

        UserSteps.transfer(user1.getUserRequest(), user1.getAccountResponse().get(0),
                user2.getAccountResponse().get(0), amountToTransfer);

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
                                user2.getAccountResponse().get(0).getId()
                             )
                         );

        float user1BalanceAfterDeposit = user2.getBalance(user2.getAccountsNumbers().get(0));
        float user2BalanceAfterDeposit = user2.getBalance(user2.getAccountsNumbers().get(0));

        Assertions.assertThat(balanceBeforeTransfer1).isEqualTo(user1BalanceAfterDeposit - amountToTransfer);
        Assertions.assertThat(balanceBeforeTransfer2 + amountToTransfer).isEqualTo(user2BalanceAfterDeposit);
    }

    @Test
    public void userCanTransferToHisSecondAcc() {
        float amountToTransfer = RandomData.generateFloatInclusive(0.01F, 5000);
        User user1 = AdminSteps.createUserAndAcc(2);

        UserSteps.putDeposit(user1.getUserRequest(), user1.getAccountResponse().get(0), amountToTransfer);

        float balanceBeforeTransfer = user1.getBalance(user1.getAccountsNumbers().get(0));

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

        float balanceAcc1 = user1.getBalance(user1.getAccountsNumbers().get(0));
        float balanceAcc2 = user1.getBalance(user1.getAccountsNumbers().get(1));

        Assertions.assertThat(0.0F).isEqualTo(balanceAcc1);
        Assertions.assertThat(amountToTransfer).isEqualTo(balanceAcc2);

    }

}
