package ui.pages;

import lombok.Getter;

@Getter
public enum BankAlert {

    USER_CREATED_SUCCESSFULLY("✅ User created successfully!"),
    USERNAME_MUST_BE_BETWEEN_3_15("Username must be between 3 and 15 characters"),
    ACCOUNT_NUMBER_CREATED("✅ New Account Created! Account Number: ");

    private final String msg;

    BankAlert(String msg) {
        this.msg = msg;
    }
}
