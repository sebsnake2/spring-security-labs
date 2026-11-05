package com.example.lab1.service;

import com.example.lab1.async.AsyncOperationResult;
import org.springframework.core.task.TaskRejectedException;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Service
public class AsyncDemoService {

    private final AsyncOperationsService asyncOperationsService;

    public AsyncDemoService(
            AsyncOperationsService asyncOperationsService
    ) {
        this.asyncOperationsService = asyncOperationsService;
    }

    public String startEmailFireAndForget(
            String recipient,
            long delayMs
    ) {
        asyncOperationsService.sendEmailAsync(
                recipient,
                delayMs
        );

        return "Email operation started";
    }

    public CompletableFuture<String> runParallelOperations(
            long pdfDelayMs,
            long externalDelayMs
    ) {
        CompletableFuture<AsyncOperationResult> pdfFuture =
                asyncOperationsService.generatePdfAsync(
                        "tasks-report.pdf",
                        pdfDelayMs
                );

        CompletableFuture<AsyncOperationResult> externalFuture =
                asyncOperationsService.callExternalServiceAsync(
                        "task-statistics",
                        externalDelayMs
                );

        return pdfFuture.thenCombine(
                externalFuture,
                (pdf, external) ->
                        pdf.value()
                                + " | "
                                + external.value()
        );
    }

    public CompletableFuture<String> handleFailedFuture() {
        return asyncOperationsService
                .failingFutureAsync()
                .exceptionally(ex ->
                        "Recovered from async failure"
                );
    }

    public String waitForResultWithTimeout(
            long operationDelayMs,
            long timeoutMs
    ) {
        CompletableFuture<AsyncOperationResult> future =
                asyncOperationsService.callExternalServiceAsync(
                        "slow-request",
                        operationDelayMs
                );

        try {
            AsyncOperationResult result =
                    future.get(
                            timeoutMs,
                            TimeUnit.MILLISECONDS
                    );

            return result.value();

        } catch (TimeoutException e) {
            return "Async operation timed out";

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return "Waiting was interrupted";

        } catch (ExecutionException e) {
            return "Async operation failed";
        }
    }

    public String submitOverloadJob(
            int jobId,
            long delayMs
    ) {
        try {
            asyncOperationsService.overloadJobAsync(
                    jobId,
                    delayMs
            );

            return "Job " + jobId + " accepted";

        } catch (TaskRejectedException e) {
            return "Job " + jobId
                    + " rejected: try again later";
        }
    }
}