package db;

import db.models.Accounts;
import db.models.Customer;
import lombok.experimental.UtilityClass;

import static db.Tables.ACCOUNTS;
import static db.Tables.CUSTOMERS;

@UtilityClass
public class DbHelper {
    public static <T> T getEntity(Class<T> clazz, String tableName, long id) {
        return DbService.withConnection(ctx -> ctx.select()
                .from(tableName)
                .where("id = ?", id)
                .fetchInto(clazz)
                .getFirst());
    }

    public static Accounts getAccount(long id) {
        return getEntity(Accounts.class, ACCOUNTS.getTableName(), id);
    }

    public static Customer getCustomer(long id) {
        return getEntity(Customer.class, CUSTOMERS.getTableName(), id);
    }

}
