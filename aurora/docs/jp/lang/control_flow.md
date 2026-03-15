# 制御フロー

## If 式

Auroraでは、`if` は値を返す式として機能します。

```ar
val category: string = if age < 13 {
    "child"
} elseif age < 18 {
    "teenager"
} else {
    "adult"
}
```

## Match 式（パターンマッチング）

`match` は強力なパターンマッチングを提供します。

```ar
val result: string = match value {
    1 => "one"
    2, 3 => "two or three"
    10..20 => "range"
    is string => "string type"
    default => "other"
}
```

## ループ制御

### For

`in` キーワードを使用して、範囲またはイテラブルなコレクションを反復処理します。

```ar
for i in 1..10  { Io::print(i) }   // 排他的：1から9まで
for i in 1..=10 { Io::print(i) }   // 包含的：1から10まで

for item in items { process(item) } // コレクションを反復処理
```

> **注意:** `..` は上限を排他とする範囲を、`..=` は上限を包含する範囲を生成します。
> これらの範囲リテラルは、組み込みの `Range` 型（`Aurora.Runtime.Range` 参照）に基づいています。

### While

`condition` が `true` の間、本体を繰り返し実行します。

```ar
while condition { doSomething() }
```

### Repeat-Until (Do-While)

本体を少なくとも1回実行した後、`condition` が `false` の間繰り返します。

```ar
repeat {
    doSomething()
} until !condition
```

### Break & Continue

`break` は最内ループを終了し、`continue` は次の反復にスキップします。
ラベルを使用して外側のループを対象にすることができます。

```ar
outer: for i in 1..10 {
    for j in 1..10 {
        if i * j > 50 { break outer }
        if j == 5     { continue outer }
    }
}
```

## `in` 包含演算子

`for` ループの外では、`in` は値が範囲またはコレクションに含まれているかを検査する二項演算子で、`bool` を返します。

```ar
val r = 1..10
Io::print(5 in r)    // true
Io::print(10 in r)   // false（上限が排他的なため）

val incl = 1..=10
Io::print(10 in incl) // true
```

ジェネリックパラメータリスト内での変位修飾子としての `in` / `out` の使い方は [generics.md](../type/generics.md) を参照してください。
