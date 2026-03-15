# Standard Library (StdLib)

Aurora provides a growing suite of standard modules for common programming tasks. Modules are accessed via their full namespace or imported using `use`.

## Io Module (`Aurora.Io.Io`)

The `Io` module handles input and output operations.

```ar
use Aurora.Io.Io

Io::println("Hello, Aurora!")
val input = Io::readLine()
```

### Static Methods
- **`Io::print(msg: object)`**: Prints a message to standard output.
- **`Io::println(msg: object)`**: Prints a message with a trailing newline.
- **`Io::readLine() -> string?`**: Reads a line from standard input. Returns `null` if the end of the stream is reached.

## Concurrent Module (`Aurora.Runtime.Concurrent`)

Provides primitives for multi-threading and asynchronous management.

### Classes
- **`Future<T>`**: Represents the result of an asynchronous operation.
    - `wait() -> T`: Blocks until the result is available.
- **`Mutex`**: A reentrant mutual exclusion lock.
    - `lock()`: Acquires the lock.
    - `unlock()`: Releases the lock.
- **`Channel<T>(capacity: int)`**: A thread-safe communication primitive.
    - `send(item: T)`: Sends an item (blocks if full).
    - `receive() -> T?`: Receives an item (blocks if empty).

## Collections

Standard collections are available in the `Aurora.Collections` namespace.

- **`ArrayList<T>`**: A dynamic array.
- **`HashSet<T>`**: A set based on a hash table.
- **`HashMap<K, V>`**: A key-value map.

```ar
use Aurora.Collections.*

val list = new ArrayList<string>()
list.add("Aurora")
Io::println(list.get(0))
```

## Internal Native Binding
Under the hood, Aurora standard library modules are implemented in Java and mapped to Aurora using the `@AuroraLib` and `@AuroraNative` annotations. For example, `IoModule.java` maps `Aurora.Io.Io._println` to `System.out.println`.
