# Null Safety & Typing

Aurora is designed with **Null Safety** as a core pillar, aiming to eliminate `NullPointerException` at compile-time.

## Nullable Systems

In Aurora, types are non-nullable by default.

- `val a: string = null` // **Compile Error**
- `val b: string? = null` // **OK** (Nullable type)

### Safe Call Operator (`?.`)
Calls a method only if the object is not null. If it is null, the expression evaluates to `null`.
```ar
val length: int? = person?.name?.length()
```

### Elvis Operator (`?:`)
Returns the right-hand side if the left-hand side is `null`.
```ar
val name: string = getName() ?: "Unknown"
```

### Non-null Assertion (`!!` or `notnull`)
Forces the type to be non-nullable. If the value is actually null, a `NullPointerException` (runtime) is thrown.
```ar
val value = nullableValue!!
```

## Type Checking & Smart Casting

Aurora supports smart casting, where the compiler automatically tracks type checks.

```ar
val x: object = "Hello"

if x is string {
    // Inside this block, x is automatically cast to string
    Io::println(x.length())
}
```

### Type Checking (`is`)
Returns true if the object is an instance of the specified type.

### Explicit Casting (`as`)
Casts an object to a target type. Throws `ClassCastException` if the cast fails.
```ar
val s = x as string
```

## Implementation Note
Under the hood, Aurora's VM uses an `ArObject` hierarchy. Every variable is an instance of a class extending `ArObject` (e.g., `ArInt`, `ArString`). Nulls are represented by a singleton `ArNone`.
