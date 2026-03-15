# Best Practices

Follow these guidelines to write clean, safe, and efficient Aurora code.

## Null Safety

1.  **Prefer Non-nullable Types**: Use nullable types (`?`) only when a value is truly optional.
2.  **Use Safe Calls**: Use `?.` instead of manually checking for null.
3.  **Provide Defaults**: Use the Elvis operator `?:` to handle null cases gracefully.
4.  **Avoid `!!`**: Avoid non-null assertions unless you are absolutely certain the value is not null and cannot use other means.

```ar
// Good
val name = person?.name ?: "Guest"

// Bad
val name = person!!.name
```

## Error Handling

1.  **Result for Logic**: Use `Result<T, E>` for errors that are part of the normal program flow (e.g., user input errors, network timeouts).
2.  **Exception for Bugs**: Use exceptions for conditions that should not happen in a correct program (e.g., out-of-bounds access).
3.  **Chain with `?`**: Use the question mark operator to keep error propagation concise.

## Concurrency

1.  **I/O -> Async**: Use `async`/`await` for all I/O bound tasks to keep the system responsive.
2.  **CPU -> Thread**: Use `thread` for intensive computations to utilize multiple CPU cores.
3.  **Avoid Shared Mutability**: Prefer passing data through `Channel` rather than sharing mutable state between threads.
4.  **Use Mutex**: If you must share mutable state, always protect it with a `Mutex`.

## Coding Style

1.  **Naming**: Stick to the standard naming conventions (`camelCase` for functions/variables, `PascalCase` for classes).
2.  **Inference**: Let the compiler infer types when the meaning is obvious, but provide explicit types for public APIs.
3.  **Immutability**: Prefer `val` over `var` whenever possible.
