package com.example.lab1.service;

import com.example.lab1.async.AsyncOperationResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
public class AsyncOperationsService {

    private final AtomicBoolean emailCompleted =
            new AtomicBoolean(false);

    private volatile String lastEmailThread;

    @Async("labTaskExecutor")
    public void sendEmailAsync(
            String recipient,
            long delayMs
    ) {
        emailCompleted.set(false);

        sleep(delayMs);

        lastEmailThread =
                Thread.currentThread().getName();

        emailCompleted.set(true);

        System.out.println(
                "Email sent to " + recipient
                        + " in thread " + lastEmailThread
        );
    }

    @Async("labTaskExecutor")
    public CompletableFuture<AsyncOperationResult> generatePdfAsync(
            String reportName,
            long delayMs
    ) {
        long startedAt = System.nanoTime();

        sleep(delayMs);

        long finishedAt = System.nanoTime();

        AsyncOperationResult result =
                new AsyncOperationResult(
                        "PDF generated: " + reportName,
                        startedAt,
                        finishedAt,
                        Thread.currentThread().getName()
                );

        return CompletableFuture.completedFuture(result);
    }

    @Async("labTaskExecutor")
    public CompletableFuture<AsyncOperationResult> callExternalServiceAsync(
            String request,
            long delayMs
    ) {
        long startedAt = System.nanoTime();

        sleep(delayMs);

        long finishedAt = System.nanoTime();

        AsyncOperationResult result =
                new AsyncOperationResult(
                        "External response: " + request,
                        startedAt,
                        finishedAt,
                        Thread.currentThread().getName()
                );

        return CompletableFuture.completedFuture(result);
    }

    @Async("labTaskExecutor")
    public CompletableFuture<String> failingFutureAsync() {
        throw new IllegalStateException(
                "External service failed"
        );
    }

    @Async("labTaskExecutor")
    public void failingVoidAsync() {
        throw new IllegalStateException(
                "Fire-and-forget operation failed"
        );
    }

    @Async("overloadTaskExecutor")
    public CompletableFuture<String> overloadJobAsync(
            int jobId,
            long delayMs
    ) {
        sleep(delayMs);

        return CompletableFuture.completedFuture(
                "Job " + jobId + " completed"
        );
    }

    public boolean isEmailCompleted() {
        return emailCompleted.get();
    }

    public String getLastEmailThread() {
        return lastEmailThread;
    }

    public void resetEmailState() {
        emailCompleted.set(false);
        lastEmailThread = null;
    }

    private void sleep(long delayMs) {
        try {
            Thread.sleep(delayMs);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException(
                    "Async operation was interrupted",
                    e
            );
        }
    }
}