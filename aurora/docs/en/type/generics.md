# Generics

Generics allow you to write reusable code that works with multiple types while maintaining strict type safety.

## Generic Classes

Generic types are defined using angle brackets `<T>`.

```ar
class Box<T> {
    val value: T
    constructor(value: T) { self.value = value }
    pub fun getValue() -> T = self.value
}

val intBox = new Box<int>(42)
val strBox = new Box<string>("Aurora")
```

### Multiple Type Parameters
```ar
class Pair<T, U>(val first: T, val second: U)
```

## Generic Functions

Functions can also define their own type parameters.

```ar
fun identity<T>(value: T) -> T = value

val v = identity("test") // T is inferred as string
```

## Type Constraints

You can restrict type parameters to be a subtype of a specific class or trait using `:`.
Multiple constraints are separated by commas.

```ar
class Container<T : Comparable<T>> {
    // T must implement Comparable
}

fun merge<T : Cloneable, Serializable>(a: T, b: T) -> T { ... }
```

## Variance

Aurora supports declaration-site variance to make generic types more flexible.

| Modifier | Name | Meaning |
|----------|------|---------|
| `out T`  | Covariance | `T` is only produced (returned). Allows `Box<Dog>` to be used as `Box<Animal>`. |
| `in T`   | Contravariance | `T` is only consumed (parameter). Allows `Sink<Animal>` to be used as `Sink<Dog>`. |
| _(none)_ | Invariance | `T` is both read and written. No subtype relationship. |

```ar
// Covariant – T is only returned, never accepted
trait Producer<out T> {
    pub fun get() -> T
}

// Contravariant – T is only accepted, never returned
trait Consumer<in T> {
    pub fun accept(value: T)
}
```

## Default Type Parameters

A generic parameter can declare a **default type** using the `default` keyword.
This makes the type argument optional at use sites when the default is sufficient.

```ar
// Callers can write Range, Range<int>, or Range<float>, etc.
pub abstract class Range<out T default int> : Checkable {
    local val min: T
    local val max: T
    local val exc: bool

    constructor(min: T, max: T, exc: bool = true) {
        self.min = min
        self.max = max
        self.exc = exc
    }

    pub fun min() -> T = self.min
    pub fun max() -> T = self.max

    pub override fun check() -> bool =
        if exc (min > v < max) else (min >= v <= max)
}
```

Variance and defaults can be combined on the same parameter:

```ar
class Cache<out V default string, in K default string> { ... }
```

## The `in` Operator

The `in` operator tests whether a value is contained within a range or collection.
This is distinct from the `in` variance keyword, which only appears inside `< >`.

```ar
val r = 1..10
val inside = 5 in r       // true  (exclusive upper bound by default)
val outside = 10 in r     // false

// Works with inclusive ranges too
val incl = 1..=10
val edge = 10 in incl     // true

// Works with any type that implements Checkable
val valid = value in someRange
```

When used in a `for` loop, `in` iterates over a range or collection — it does **not** call `check()`.

```ar
for i in 1..10 { Io::print(i) }   // prints 1..9
for i in 1..=10 { Io::print(i) }  // prints 1..10
```

## Reification Notice

At the VM level, Aurora's generics are currently handled via type erasure (similar to Java), but the compiler ensures type consistency during the static analysis phase.
