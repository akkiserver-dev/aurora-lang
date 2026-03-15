# Collections

Aurora provides a rich set of collection types, categorized into fixed-size arrays and dynamic collections.

## Arrays

Arrays are fixed-size, indexed sequences of elements. They are covariant (e.g., `string[]` can be treated as `object[]`).

```ar
val numbers: int[] = [1, 2, 3, 4, 5]
val fruits: string[] = ["apple", "banana"]

// Sized initialization
val empty: int[10] = [] // Initialized with 10 zeros
```

### Accessing Elements
```ar
val first = numbers[0]
numbers[1] = 10
```

### Common Array Methods
- `length() -> int`: Returns the number of elements.
- `push(item: T) -> T[]`: Returns a **new** array with the item added at the end.
- `filter(p: (T) -> bool) -> T[]`: Filters elements based on a predicate.
- `map(f: (T) -> U) -> U[]`: Transforms elements using a function.

## Dynamic Collections

These are available in the `Aurora.Collections` namespace.

### List (`List<T>`)
An ordered collection that allows duplicates. The default implementation is `ArrayList`.

```ar
use Aurora.Collections.*

val names = new ArrayList<string>()
names.add("Alice")
names.add("Bob")
val size = names.size()
```

### Set (`Set<T>`)
A collection that contains no duplicate elements. The default implementation is `HashSet`.

```ar
val uniqueIds = new HashSet<int>()
uniqueIds.add(101)
uniqueIds.add(101) // Does nothing
```

### Map (`Map<K, V>`)
An object that maps keys to values. The default implementation is `HashMap`.

```ar
val scores = new HashMap<string, int>()
scores.put("Player1", 100)
val score = scores.get("Player1")
```

## Internal Implementation
Aurora's native arrays are managed by the `ArArray` class in the VM. Dynamic collections like `ArrayList` are currently implemented as native Java objects bound to Aurora using the `NativeBinder`, ensuring high performance for large datasets.
