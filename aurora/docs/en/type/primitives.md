# Primitive Types

Aurora's type system is designed for both performance and safety, providing a range of numeric and literal types.

## Numeric Types

Numeric types in Aurora map directly to high-performance Java primitives.

| Type | Range | Memory | Suffix | Usage |
|------|-------|--------|--------|-------|
| `int` | −2³¹ to 2³¹−1 | 4 bytes | None | Default for integers, array indexing |
| `long` | −2⁶³ to 2⁶³−1 | 8 bytes | `L` | Large numbers, timestamps, IDs |
| `float` | IEEE 754 single | 4 bytes | `f` | Memory-efficient decimals, graphics |
| `double` | IEEE 754 double | 8 bytes | None | Default for decimals, scientific calc |

### Literal Formats
Aurora supports modern numeric literal formats, including underscores for readability.

```ar
val decimal: int = 1_000_000
val hex: int = 0xFF_00_FF
val binary: int = 0b1010_1100
val longVal: long = 123_456_789_012L
val floatVal: float = 3.14f
val scientific: double = 1.5e10
```

## String Type (`string`)

The `string` type represents an immutable sequence of UTF-8 characters. It is not a primitive but is treated as a core type.

```ar
val name: string = "Aurora"
val greeting: string = `Hello, ${name}!` // String interpolation
val multiline: string = """
    This is a multiline
    string in Aurora.
"""
```

### Core String Methods
- `length() -> int`: Returns the number of characters.
- `substring(start: int, end: int) -> string`: Extracts a part of the string.
- `indexOf(sub: string) -> int`: Returns the index of the first occurrence.
- `contains(sub: string) -> bool`: Checks if the substring exists.
- `toUpperCase()`, `toLowerCase()`: Case transformation.
- `trim() -> string`: Removes leading/trailing whitespace.
- `split(delimiter: string) -> string[]`: Splits into an array.

## Boolean Type (`bool`)

Represents a logical truth value, either `true` or `false`.

- No implicit conversion from integers (0/1) or null.
- Logical operators (`&&`, `||`, `!`) are short-circuiting.

## Special Types

- **`void`**: Indicates that a function returns no value.
- **`object`**: The root of the type hierarchy. All types (except `none`) can be treated as `object`.
- **`none`**: Represents a value that never exists (e.g., return type of a function that always throws).
- **`none?`**: The specific type of the `null` literal.
