# Control Flow

## If Expression

In Aurora, `if` is an expression that returns a value.

```ar
val category: string = if age < 13 {
    "child"
} elseif age < 18 {
    "teenager"
} else {
    "adult"
}
```

## Match Expression

`match` provides powerful pattern matching.

```ar
val result: string = match value {
    1 => "one"
    2, 3 => "two or three"
    10..20 => "range"
    is string => "string type"
    default => "other"
}
```

## Loops

### For

Iterates over a range or any iterable collection using the `in` keyword.

```ar
for i in 1..10  { Io::print(i) }   // exclusive: 1 to 9
for i in 1..=10 { Io::print(i) }   // inclusive: 1 to 10

for item in items { process(item) } // iterate over a collection
```

> **Note:** `..` creates an exclusive upper-bound range; `..=` creates an inclusive one.
> The built-in `Range` type (see `Aurora.Runtime.Range`) backs these range literals.

### While

Executes the body as long as `condition` is `true`.

```ar
while condition { doSomething() }
```

### Repeat-Until (Do-While)

Runs the body at least once, then repeats while `condition` is `false`.

```ar
repeat {
    doSomething()
} until !condition
```

### Break & Continue

`break` exits the innermost loop; `continue` skips to the next iteration.
Labels can be used to target an outer loop.

```ar
outer: for i in 1..10 {
    for j in 1..10 {
        if i * j > 50 { break outer }
        if j == 5     { continue outer }
    }
}
```

## The `in` Containment Operator

Outside of a `for` loop, `in` is a binary operator that checks whether a value is inside a range or collection, returning `bool`.

```ar
val r = 1..10
Io::print(5 in r)   // true
Io::print(10 in r)  // false (exclusive upper bound)

val incl = 1..=10
Io::print(10 in incl) // true
```

See [generics.md](../type/generics.md) for how `in` / `out` are used as variance modifiers inside generic parameter lists.
