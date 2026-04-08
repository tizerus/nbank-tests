package ui.pages;

import lombok.Getter;

@Getter
public enum BankAlert {

    USER_CREATED_SUCCESSFULLY("✅ User created successfully!"),
    NAME_UPDATED_SUCCESSFULLY("✅ Name updated successfully!"),
    NAME_MUST_CONTAIN_2_WORDS_LETTERS("Name must contain two words with letters only"),
    USERNAME_MUST_BE_BETWEEN_3_15("Username must be between 3 and 15 characters"),
    DEPOSIT_SUCCESS_MSG("✅ Successfully deposited $%s to account %s!"),
    ACCOUNT_NUMBER_CREATED("✅ New Account Created! Account Number: "),
    ACCOUNT_EMPTY_FIELD_ALERT("❌ Please select an account."),
    ENTER_VALID_AMOUNT("❌ Please enter a valid amount."),
    DEPOSIT_LESS_OR_EQUAL_TO_5000("❌ Please deposit less or equal to 5000$."),
    TRANSACTION_FILL_ALL_FIELDS_AND_CONFIRM("❌ Please fill all fields and confirm."),
    TRANSFER_AMOUNT_MUST_BE_AT_LEAST("❌ Error: Transfer amount must be at least 0.01"),
    TRANSFER_AMOUNT_CANNOT_EXCEED("❌ Error: Transfer amount cannot exceed 10000"),
    WRONG_RECIPIENT_NAME("❌ The recipient name does not match the registered name."),
    NO_USER_WITH_THIS_ACCOUNT_NUMBER("❌ No user found with this account number."),
    SUCCESSFULLY_TRANSFERRED_TO_ACCOUNT("✅ Successfully transferred $%s to account %s!"),
    TRANSACTION_CANNOT_BE_MORE_BALANCE("❌ Cannot transfer more than balance."),
    TRANSACTION_CANNOT_BE_TO_THE_SAME_ACCOUNT("❌ Cannot transfer to the same account."),
    EDIT_PROFILE_ENTER_VALID_NAME("❌ Please enter a valid name."),
    INSUFFICIENT_FUNDS_OR_INVALID_ACCOUNTS("❌ Error: Invalid transfer: insufficient funds or invalid accounts"),
    ;

    private final String msg;

    BankAlert(String msg) {
        this.msg = msg;
    }
}
