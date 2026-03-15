# Package System

## Namespaces

Aurora uses **PascalCase** for namespaces (C# style), without domain reversal.

```ar
namespace Aurora.Util.Math
namespace Aurora.Collections
```

> [!NOTE]
> Unlike languages like Java, Aurora namespaces are **not dependent on the directory structure**. You can define any namespace in any file, regardless of its location in the project.

## Importing (use)

Use the `use` keyword to import members from other namespaces.

```ar
use Math.Calculator
use Math.{Calculator, MathUtils}
use Math.*
use Math.Calculator as MathCalc // Aliasing
```

## Visibility Modifiers

Visibility can also be applied at the package level.

- **`pub`**: Accessible globally.
- **`protected`**: Accessible within the same package and by subclasses.
- **`local`**: Accessible only within the same file.
- **(none)**: Accessible only within the same package (Package-private).
