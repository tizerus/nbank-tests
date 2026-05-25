package db;

public enum Tables {
    CUSTOMERS("customers"),
    ACCOUNTS("accounts"),
    TRANSACTIONS("transactions");

    private final String tableName;

    Tables(String tableName) {
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public String toString() {
        return tableName;
    }
}
