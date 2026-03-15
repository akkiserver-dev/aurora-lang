# Concurrency & Async

Aurora features a modern concurrency model that balances simplicity (coroutines) with raw power (OS threads).

## Asynchronous Programming (`async`/`await`)

Aurora's `async` functions are built on top of cooperative multitasking (coroutines). They are ideal for I/O-bound tasks like network requests or file access.

- **Non-blocking**: `await` pauses the execution of the current function without blocking the underlying thread.
- **Future based**: Under the hood, async operations return an `ArFuture`, which wraps a Java `CompletableFuture<ArObject>`.

```ar
async fun fetch() {
    val data = await Http::get("https://api.aurora-lang.org")
    Io::println(data)
}
```

## Multi-threading (`thread`)

For CPU-bound tasks, Aurora provides true parallel execution via OS threads.

```ar
thread HeavyCalculation(n: int) {
    // This runs on a separate OS thread
    return calculateFibonacci(n)
}

val future = HeavyCalculation.start(30)
val result = future.wait() // Wait for completion
```

## Memory Model & Safety

Aurora uses a Shared-State model, but with safety precautions:

1.  **Thread Pool**: The VM manages threads using a `CachedThreadPool`, preventing resource exhaustion.
2.  **Global Variables**: Globals are stored in a `ConcurrentHashMap` in the VM's `SharedState`, making them thread-safe for simple lookups and assignments.
3.  **Synchronization**: For complex shared state, the `Mutex` primitive (from the `Concurrent` module) is mandatory.

```ar
val lock = new Mutex()
lock.lock()
try {
    sharedCounter = sharedCounter + 1
} finally {
    lock.unlock()
}
```

## Summary of Concurrency Tools

| Tool | Implementation | Best Usage |
|------|----------------|------------|
| `async`/`await` | Coroutines / Futures | I/O, Network, User Interaction |
| `thread` | OS Threads | Intensive computation, background tasks |
| `Channel` | Blocking Queue | Passing data between threads/tasks |
| `Mutex` | Reentrant Lock | Protecting shared mutable state |
