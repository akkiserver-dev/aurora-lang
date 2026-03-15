# Records & Enums

## Record Types

Records provide value semantics and automatic boilerplate reduction. Ideal for data transfer objects.

```ar
record Point(x: double, y: double)
record Person(name: string, age: int, email: string)

val point = new Point(1.0, 2.0)

// Generated features:
val p2 = point.copy(x: 5.0)     // Copy with modifications
val (x, y) = point             // Destructuring
val equal = (point == p2)       // Value-based comparison
Io::print(point)                // Automatic toString()
```

## Enum Types

```ar
enum Color {
    Red, Green, Blue
}

// Enums with values
enum HttpStatus {
    Ok = 200,
    BadRequest = 400,
    NotFound = 404
}

// Enums with methods
enum Direction {
    North, South, East, West
    
    pub fun opposite() -> Direction {
        return match self {
            North => South
            South => North
            East => West
            West => East
        }
    }
}

val color = Color::Red
val opposite = Direction::North.opposite()
```
