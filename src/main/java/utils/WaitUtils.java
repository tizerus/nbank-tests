package utils;

import org.awaitility.Awaitility;
import org.awaitility.core.ConditionFactory;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.function.Predicate;

public class WaitUtils {

    private static final Duration DEFAULT_TIMEOUT = Duration.ofSeconds(15);
    private static final Duration DEFAULT_POLL_INTERVAL = Duration.ofSeconds(5);

    public static ConditionFactory getDefaultAwait(Duration timeout, Duration pollInterval) {
        return Awaitility.await()
                .atMost(timeout)
                .pollInterval(pollInterval)
                .ignoreExceptions();
    }

    public static ConditionFactory getDefaultAwait() {
        return getDefaultAwait(DEFAULT_TIMEOUT, DEFAULT_POLL_INTERVAL);
    }

    public static ConditionFactory getAwaitWithTimeout(Duration timeout) {
        return Awaitility.await()
                .atMost(timeout)
                .pollInterval(DEFAULT_POLL_INTERVAL)
                .ignoreExceptions();
    }

    // Ожидание выполнения условия
    public static <T> T waitFor(Callable<T> supplier, Predicate<T> condition) {
        return getDefaultAwait()
                .until(supplier, condition);
    }

    public static void waitForCondition(Callable<Boolean> condition) {
        getDefaultAwait()
                .until(condition);
    }
}