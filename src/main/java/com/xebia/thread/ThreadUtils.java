package com.xebia.thread;

import java.util.concurrent.*;

public class ThreadUtils {

    public static <T> CompletableFuture<T> timeoutAfter(long timeout, TimeUnit unit) {
        ScheduledThreadPoolExecutor delayer = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(2);
        CompletableFuture<T> result = new CompletableFuture<T>();
        delayer.schedule(() -> result.completeExceptionally(new TimeoutException()), timeout, unit);
        return result;
    }
}
