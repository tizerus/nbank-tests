package api;

import common.extension.SkipForBrokenImageExtension;
import common.extension.TimingExtension;
import db.DbService;
import org.apache.commons.lang3.time.StopWatch;
import org.assertj.core.api.SoftAssertions;
import org.jooq.DSLContext;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(TimingExtension.class)
@ExtendWith(SkipForBrokenImageExtension.class)
public class ApiBaseTest {

    protected SoftAssertions softAssert;
    private static final ThreadLocal<StopWatch> stopWatchThreadLocal = ThreadLocal.withInitial(StopWatch::new);

    @AfterAll
    public static void clear() {
        DbService.closePool();
    }

    @BeforeEach
    public void setupTest() {
        System.out.println("Thread [" + Thread.currentThread().getName() + "] START");
        this.softAssert = new SoftAssertions();
        startWatch();
    }

    @AfterEach
    public void afterTest() {
        System.out.println("Thread [" + Thread.currentThread().getName() + "] END ");
        softAssert.assertAll();
        stopWatch();
    }

    private void startWatch() {
        // Получаем StopWatch для текущего потока и запускаем
        StopWatch stopWatch = stopWatchThreadLocal.get();
        if (stopWatch.isStarted()) {
            stopWatch.reset(); // Сбрасываем, если уже был запущен
        }
        stopWatch.start();
    }

    private void stopWatch() {
        StopWatch stopWatch = stopWatchThreadLocal.get();
        if (stopWatch != null && stopWatch.isStarted()) {
            stopWatch.stop();
            String executionTime = stopWatch.getDuration()
                    .toString()
                    .substring(2) // убирает "PT" в начале
                    .toLowerCase();

            System.out.println("Execution result: Thread [" + Thread.currentThread().getName() + "] "
                    + "END - Time: " + executionTime);
        }
        // Очищаем ThreadLocal для предотвращения утечек памяти
        stopWatchThreadLocal.remove();
    }

}
