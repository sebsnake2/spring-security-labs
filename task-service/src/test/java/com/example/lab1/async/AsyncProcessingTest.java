package com.example.lab1.async;

import com.example.lab1.config.AsyncConfig;
import com.example.lab1.service.AsyncDemoService;
import com.example.lab1.service.AsyncOperationsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

import static org.junit.jupiter.api.Assertions.*;

@SpringJUnitConfig(classes = {
        AsyncConfig.class,
        RecordingAsyncExceptionHandler.class,
        AsyncOperationsService.class,
        AsyncDemoService.class
})
class AsyncProcessingTest {

    @Autowired
    private AsyncOperationsService asyncOperationsService;

    @Autowired
    private AsyncDemoService asyncDemoService;

    @Autowired
    private RecordingAsyncExceptionHandler exceptionHandler;

    @Autowired
    @Qualifier("labTaskExecutor")
    private ThreadPoolTaskExecutor labTaskExecutor;

    @BeforeEach
    void setUp() {
        asyncOperationsService.resetEmailState();
        exceptionHandler.clear();
    }

    @Test
    @DisplayName("Fire-and-forget returns before slow operation finishes")
    void fireAndForget_returnsBeforeOperationFinishes() throws Exception {
        // Act
        String response =
                asyncDemoService.startEmailFireAndForget(
                        "student@example.com",
                        500
                );

        // Assert
        assertEquals(
                "Email operation started",
                response
        );

        assertFalse(
                asyncOperationsService.isEmailCompleted()
        );

        waitUntil(
                asyncOperationsService::isEmailCompleted,
                2000
        );

        assertTrue(
                asyncOperationsService.isEmailCompleted()
        );
    }

    @Test
    @DisplayName("Async method uses configured limited executor")
    void asyncMethod_usesConfiguredExecutor() throws Exception {
        // Act
        AsyncOperationResult result =
                asyncOperationsService
                        .generatePdfAsync(
                                "executor-test.pdf",
                                100
                        )
                        .get(2, TimeUnit.SECONDS);

        // Assert
        assertEquals(
                2,
                labTaskExecutor.getCorePoolSize()
        );

        assertEquals(
                2,
                labTaskExecutor.getMaxPoolSize()
        );

        assertEquals(
                "lab-async-",
                labTaskExecutor.getThreadNamePrefix()
        );

        assertTrue(
                result.threadName()
                        .startsWith("lab-async-")
        );
    }

    @Test
    @DisplayName("Two independent async calls really run concurrently")
    void independentAsyncCalls_runConcurrently()
            throws Exception {

        // Arrange + Act
        CompletableFuture<AsyncOperationResult> pdfFuture =
                asyncOperationsService.generatePdfAsync(
                        "parallel-report.pdf",
                        400
                );

        CompletableFuture<AsyncOperationResult> externalFuture =
                asyncOperationsService.callExternalServiceAsync(
                        "parallel-request",
                        400
                );

        AsyncOperationResult pdf =
                pdfFuture.get(
                        2,
                        TimeUnit.SECONDS
                );

        AsyncOperationResult external =
                externalFuture.get(
                        2,
                        TimeUnit.SECONDS
                );

        // Assert that execution intervals overlap
        long latestStart =
                Math.max(
                        pdf.startedAt(),
                        external.startedAt()
                );

        long earliestFinish =
                Math.min(
                        pdf.finishedAt(),
                        external.finishedAt()
                );

        assertTrue(
                latestStart < earliestFinish,
                "Async operations should overlap in time"
        );

        // Also verify production thenCombine logic
        String combinedResult =
                asyncDemoService
                        .runParallelOperations(
                                100,
                                100
                        )
                        .get(
                                2,
                                TimeUnit.SECONDS
                        );

        assertTrue(
                combinedResult.contains(
                        "PDF generated"
                )
        );

        assertTrue(
                combinedResult.contains(
                        "External response"
                )
        );
    }

    @Test
    @DisplayName("Async failures are handled without crashing caller")
    void asyncFailures_areHandled() throws Exception {
        // Future exception handling
        String recoveredResult =
                asyncDemoService
                        .handleFailedFuture()
                        .get(
                                2,
                                TimeUnit.SECONDS
                        );

        assertEquals(
                "Recovered from async failure",
                recoveredResult
        );

        // void @Async exception handling
        assertDoesNotThrow(
                () -> asyncOperationsService
                        .failingVoidAsync()
        );

        waitUntil(
                () -> exceptionHandler
                        .getLastException() != null,
                2000
        );

        assertNotNull(
                exceptionHandler.getLastException()
        );

        assertEquals(
                "Fire-and-forget operation failed",
                exceptionHandler
                        .getLastException()
                        .getMessage()
        );
    }

    @Test
    @DisplayName("Waiting for Future is limited by timeout")
    void waitingForFuture_isLimitedByTimeout() {
        // Act
        String result =
                asyncDemoService
                        .waitForResultWithTimeout(
                                300,
                                50
                        );

        // Assert
        assertEquals(
                "Async operation timed out",
                result
        );
    }

    @Test
    @DisplayName("Executor overload is handled predictably")
    void executorOverload_isHandledPredictably() {
        // One task is running
        String first =
                asyncDemoService.submitOverloadJob(
                        1,
                        1000
                );

        // One task occupies the only queue slot
        String second =
                asyncDemoService.submitOverloadJob(
                        2,
                        1000
                );

        // Pool + queue are now full
        String third =
                asyncDemoService.submitOverloadJob(
                        3,
                        1000
                );

        assertEquals(
                "Job 1 accepted",
                first
        );

        assertEquals(
                "Job 2 accepted",
                second
        );

        assertEquals(
                "Job 3 rejected: try again later",
                third
        );
    }

    private void waitUntil(
            BooleanSupplier condition,
            long timeoutMs
    ) throws InterruptedException {

        long deadline =
                System.currentTimeMillis()
                        + timeoutMs;

        while (!condition.getAsBoolean()
                && System.currentTimeMillis()
                < deadline) {

            Thread.sleep(20);
        }

        assertTrue(
                condition.getAsBoolean(),
                "Condition was not met within timeout"
        );
    }
}