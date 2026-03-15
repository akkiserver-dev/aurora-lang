# Functions

## Basic Functions

```ar
fun add(a: int, b: int) -> int {
    return a + b
}

// Single-expression function (return is optional)
fun double(x: int) -> int = x * 2

// Void function
fun printMsg(msg: string) -> void {
    Io::println(msg)
}
```

## Parameters

### Default Parameters
```ar
fun greet(name: string, greeting: string = "Hello") -> string {
    return "${greeting}, ${name}!"
}
```

### Named Arguments
```ar
greet(name: "Alice", greeting: "Hi")
```

### Variadic Parameters (Varargs)
```ar
fun sum(varargs numbers: int) -> int {
    var total: int = 0
    for num in numbers { total = total + num }
    return total
}
```

## Lambdas & Higher-Order Functions

```ar
// Lambdas
val double = (x: int) => x * 2

// Higher-order function
fun apply(a: int, b: int, op: (int, int) -> int) -> int {
    return op(a, b)
}

val res = apply(5, 3, (x, y) => x + y)
```

## Extension Functions (Inject)

Add methods to existing types or classes.

```ar
inject type string {
    pub fun repeat(count: int) -> string {
        var result: string = ""
        for i in 1..count { result = result + self }
        return result
    }
}

val s = "Hi".repeat(3) // "HiHiHi"
```
