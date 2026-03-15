# Object-Oriented Programming

Aurora provides a robust OOP model with a focus on simplicity and performance.

## Classes and Objects

Classes are templates for objects, supporting attributes (fields) and methods.

```ar
class Calculator {
    val brand: string
    
    constructor(brand: string) {
        self.brand = brand
    }
    
    pub fun add(a: int, b: int) -> int = a + b
}

val calc = new Calculator("AuroraTech")
```

### The `new` Keyword
Aurora (v2) requires the `new` keyword to instantiate classes. This distinguishes constructor calls from regular function calls.

### The `self` Keyword
Within a class, `self` refers to the current instance. It is required to access instance fields or call other instance methods.

## Inheritance

Aurora supports single inheritance. All classes implicitly inherit from `object`.

```ar
class ScientificCalculator(brand: string) : Calculator(brand) {
    pub override fun add(a: int, b: int) -> int {
        Io::println("Scientific add")
        return super.add(a, b)
    }
}
```

- **`super`**: Used to access members of the parent class.
- **`override`**: Required when overriding a method from the superclass.

## Abstract Classes and Traits

### Abstract Classes
Use the `abstract` keyword. They cannot be instantiated and can contain abstract methods (methods without a body).

### Traits
Traits define a contract that a class must fulfill. A class can implement multiple traits separated by commas. Traits support default method implementations (Mixins).
```ar
trait Reset { pub fun reset() }
class Counter : Reset { pub override fun reset() { ... } }
```

## Internal Implementation
- **`ArClass`**: The VM stores classes as `ArClass` objects, containing a method table and a reference to the superclass.
- **Method Dispatch**: Method resolution is dynamic. If a method isn't found in the current class, Aurora traverses the `superClass` chain until it's found or an error occurs.
- **`ArInstance`**: Instances of classes are `ArInstance` objects, which store a map of field values.
