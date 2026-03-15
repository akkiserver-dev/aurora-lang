# Basics

Aurora's syntax is inspired by modern languages like Kotlin, Swift, and Rust, focusing on readability and unambiguous grammar.

## Lexical Structure

- **Character Set**: UTF-8 is the mandatory encoding for all source files.
- **Line Endings**: Automatically supports both LF (Unix/macOS) and CRLF (Windows).

### Comments
- **Single-line**: `// comment`
- **Multi-line**: `/* comment */` (Note: Multi-line comments do not support nesting).
- **Documentation**: `/** doc comment */` (Used by tools to generate documentation).

## Keywords

Aurora (v2) features 54 keywords. They are reserved and cannot be used as identifiers.

- **Control Flow**: `if`, `elseif`, `else`, `match`, `for`, `while`, `repeat`, `until`, `do`
- **Definition**: `fun`, `class`, `record`, `enum`, `trait`, `constructor`, `override`, `abstract`
- **Data Types**: `int`, `long`, `float`, `double`, `bool`, `string`, `void`, `object`, `none`
- **Variable Modifiers**: `val` (immutable), `var` (mutable), `const`, `lazy`
- **Visibility & Scoping**: `pub`, `protected`, `local`, `static`, `namespace`, `use`
- **Execution & Flow**: `return`, `break`, `continue`, `throw`, `try`, `catch`, `finally`, `expect`, `await`, `thread`, `async`
- **Literal & Reference**: `null`, `true`, `false`, `self`, `super`, `new`, `is`, `as`, `in`

## Identifiers & Naming Conventions

Identifiers must start with a letter (A-Z, a-z) or an underscore (`_`), followed by letters, digits, or underscores.

| Target | Convention | Example |
|--------|------------|---------|
| Variables / Functions | `camelCase` | `userName`, `calculateSum()` |
| Classes / Types / Namespaces | `PascalCase` | `UserRepository`, `Aurora.Io` |
| Constants | `SCREAMING_SNAKE_CASE` | `MAX_CONNECTIONS` |
| Private / Internal members | `_camelCase` | `_internalState` |

## Operators and Precedence

Aurora operators follow standard mathematical precedence rules.

1.  **Accessors**: `.` (member), `?.` (safe call), `[]` (index)
2.  **Calls**: `()` (call), `!!` (non-null assertion)
3.  **Unary**: `-` (negate), `!` (logical NOT)
4.  **Multiplicative**: `*`, `/`, `%`
5.  **Additive**: `+`, `-`
6.  **Range**: `..` (exclusive), `..=` (inclusive)
7.  **Comparison**: `<`, `>`, `<=`, `>=`, `is`, `as`
8.  **Equality**: `==`, `!=`
9.  **Logical AND**: `&&`
10. **Logical OR**: `||`
11. **Elvis**: `?:`
12. **Assignment**: `=`, `+=`, `-=`, etc.
