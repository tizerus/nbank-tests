package common.storage;

import api.models.User;
import api.requests.steps.AdminSteps;

import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

public class UserPool {
    private static final ConcurrentHashMap<String, UserPool> pools = new ConcurrentHashMap<>();

    private final BlockingQueue<User> availableUsers = new LinkedBlockingQueue<>();
    private final Set<User> usedUsers = ConcurrentHashMap.newKeySet();
    private final String poolName;
    private final int accountsPerUser;

    public UserPool(String poolName, int size, int accountsPerUser) {
        this.poolName = poolName;
        this.accountsPerUser = accountsPerUser;
        for (int i = 0; i < size; i++) {
            availableUsers.add(AdminSteps.createUserAndAcc(this.accountsPerUser));
        }
    }

    public static UserPool getOrCreate(String poolName, int size, int accountsPerUser) {
        return pools.computeIfAbsent(poolName, k -> new UserPool(poolName, size, accountsPerUser));
    }

    public User acquireUser() throws InterruptedException {
        User user = availableUsers.poll(10, TimeUnit.SECONDS);
        if (user != null) {
            usedUsers.add(user);
        }
        return user;
    }

    public void releaseUser(User user) {
        if (usedUsers.remove(user)) {
            availableUsers.offer(user);
        }
    }

    public int getAvailableCount() {
        return availableUsers.size();
    }

    public int getUsedCount() {
        return usedUsers.size();
    }

    public String getPoolName() {
        return poolName;
    }
}
