package aurora.runtime;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Represents a handle to an asynchronous computation that will produce a value.
 */
public class ArFuture extends ArObject {
    /** The underlying Java {@link CompletableFuture} that manages the asynchronous result. */
    public final CompletableFuture<ArObject> future;

    public ArFuture(CompletableFuture<ArObject> future) {
        this.future = future;
    }

    public ArObject waitValue() {
        try {
            return future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new AuroraRuntimeException("Thread wait failed: " + e.getMessage());
        }
    }

    public ArObject waitValue(long timeoutMillis) {
        try {
            return future.get(timeoutMillis, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            throw new AuroraRuntimeException("Thread wait failed/timed out: " + e.getMessage());
        }
    }

    @Override
    public String toString() {
        return "<future " + (future.isDone() ? "done" : "running") + ">";
    }
}
