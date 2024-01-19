package org.checkmarx.codescanner.service;

import java.util.concurrent.Callable;

public class RetryExecutor<T> {

    private final int maxRetries;

    public RetryExecutor(int maxRetries) {
        this.maxRetries = maxRetries;
    }

    public T execute(Callable<T> task) {
        int retryCount = 0;

        while (retryCount <= maxRetries) {
            try {
                return task.call();
            } catch (Exception e) {
                retryCount++;
                handleException(e, retryCount);
            }
        }

        throw new RuntimeException("Thread reached max retries number. Aborting...");
    }

    private void handleException(Exception e, int retryCount) {
        System.err.println("An error occurred on thread. Error: " + e.getMessage());

        if (retryCount < maxRetries) {
            System.out.println("Retrying... (Attempt " + retryCount + ")");
        }
    }
}