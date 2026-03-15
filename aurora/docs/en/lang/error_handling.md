# Error Handling

Aurora provides two primary mechanisms for handling errors: the functional `Result<T, E>` type and the traditional `try-catch` blocks.

## Result Type (`Result<T, E>`)

The recommended way to handle expected errors in Aurora is the `Result` enum.

```ar
enum Result<T, E> {
    Ok(T),
    Err(E)
}
```

### Propagating Errors with `?`
The `?` operator can be used to propagate errors. If the expression evaluates to `Err`, the function returns that error immediately.

```ar
fun parse(input: string) -> Result<int, string> {
    val num = parseNumber(input)? // Returns Err if parseNumber fails
    return Ok(num * 2)
}
```

### Pattern Matching on Results
```ar
match parse("123") {
    Ok(val) => Io::println(`Success: ${val}`)
    Err(err) => Io::println(`Error: ${err}`)
}
```

## Exception Handling (`try-catch`)

For unexpected or exceptional conditions, Aurora supports traditional exception handling.

```ar
try {
    val result = 10 / 0
} catch (e: ArithmeticException) {
    Io::println("Zero division error")
} catch (e: Exception) {
    Io::println("Unknown error: ${e.getMessage()}")
} finally {
    // Cleanup code
}
```

### Throwing Exceptions
Use the `throw` keyword to raise an exception.
```ar
fun validate(age: int) {
    if age < 0 { throw IllegalArgumentException("Age cannot be negative") }
}
```

## Custom Exceptions
Since all instances are `ArInstance` internally, you can define your own exception classes by inheriting from `Exception` (or other exception classes).

```ar
class MyError(val msg: string) : Exception() {
    pub override fun getMessage() -> string = self.msg
}
```

## Best Practices
1.  **Use `Result`** for recoverable, expected errors (like file not found, validation).
2.  **Use `Exceptions`** for unrecoverable errors or developer mistakes (like index out of bounds).
3.  **Avoid overusing `!!`**: Prefer using `?` for error propagation or providing default values with `?:`.
