# レコードと列挙型

## レコード型 (record)

レコードは値セマンティクスを提供し、ボイラープレート（定型コード）を大幅に削減します。データ保持に適しています。

```ar
record Point(x: double, y: double)
record Person(name: string, age: int, email: string)

val point = new Point(1.0, 2.0)

// 自動生成される機能:
val p2 = point.copy(x: 5.0)     // 一部の値を変更してコピー
val (x, y) = point             // 分解宣言
val equal = (point == p2)       // 値ベースの比較
Io::print(point)                // 自動 toString() 生成
```

## 列挙型 (enum)

```ar
enum Color {
    Red, Green, Blue
}

// 数値を持つ列挙型
enum HttpStatus {
    Ok = 200,
    BadRequest = 400,
    NotFound = 404
}

// メソッドを持つ列挙型
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
