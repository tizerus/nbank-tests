package db.models;

public record Accounts (
        Long id,
        String account_number,
        float balance,
        Long customer_id
) {}
