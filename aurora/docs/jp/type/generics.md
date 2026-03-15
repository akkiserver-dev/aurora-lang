# ジェネリクス

ジェネリクスを使用すると、厳密な型安全性を維持しながら、複数の型で動作する再利用可能なコードを作成できます。

## ジェネリッククラス

ジェネリック型は、山括弧 `<T>` を使用して定義します。

```ar
class Box<T> {
    val value: T
    constructor(value: T) { self.value = value }
    pub fun getValue() -> T = self.value
}

val intBox = new Box<int>(42)
val strBox = new Box<string>("Aurora")
```

### 複数の型パラメータ
```ar
class Pair<T, U>(val first: T, val second: U)
```

## ジェネリック関数

関数自体に型パラメータを定義することも可能です。

```ar
fun identity<T>(value: T) -> T = value

val v = identity("test") // T は string と推論されます
```

## 型制約

型パラメータが特定のクラスやトレイトを実装していることを条件（制約）として `:` で指定できます。
複数の制約はカンマで区切ります。

```ar
class Container<T : Comparable<T>> {
    // T は Comparable を実装している必要があります
}

fun merge<T : Cloneable, Serializable>(a: T, b: T) -> T { ... }
```

## 変位 (Variance)

Auroraは、ジェネリック型をより柔軟に扱うための**宣言箇所での変位指定**をサポートしています。

| 修飾子 | 名称 | 意味 |
|--------|------|------|
| `out T` | 共変 (Covariance) | `T` は戻り値としてのみ使用（プロデューサー）。`Box<Dog>` を `Box<Animal>` として扱える。 |
| `in T`  | 反変 (Contravariance) | `T` は引数としてのみ使用（コンシューマー）。`Sink<Animal>` を `Sink<Dog>` として扱える。 |
| _(なし)_ | 不変 (Invariance) | `T` は読み書き両方に使用。サブタイプ関係なし。 |

```ar
// 共変 – T は返すだけで受け取らない
trait Producer<out T> {
    pub fun get() -> T
}

// 反変 – T は受け取るだけで返さない
trait Consumer<in T> {
    pub fun accept(value: T)
}
```

## デフォルト型パラメータ

`default` キーワードを使用して、型パラメータに**デフォルト型**を指定できます。
デフォルト型が適切な場合、使用側で型引数の指定を省略できます。

```ar
// Range、Range<int>、Range<float> のいずれの形式でも使用可能
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

変位指定とデフォルト型は同一パラメータで組み合わせることができます：

```ar
class Cache<out V default string, in K default string> { ... }
```

## `in` 演算子（包含チェック）

`in` 演算子は、値が範囲またはコレクションに含まれているかどうかを検査します。
これは `< >` 内で変位指定として使われる `in` キーワードとは異なります。

```ar
val r = 1..10
val inside = 5 in r       // true  （上限は排他的）
val outside = 10 in r     // false

// 包含範囲の場合
val incl = 1..=10
val edge = 10 in incl     // true

// Checkable を実装した任意の型に対して使用可能
val valid = value in someRange
```

`for` ループ内の `in` は範囲やコレクションを**反復処理**します。`check()` を呼び出すわけではありません。

```ar
for i in 1..10 { Io::print(i) }   // 1から9を出力
for i in 1..=10 { Io::print(i) }  // 1から10を出力
```

## 実装上の注意

VMレベルでは、Auroraのジェネリクスは現在（Javaと同様に）型消去によって処理されます。しかし、コンパイラの静的解析フェーズによって型の一貫性は厳密に保証されます。
