# エラーハンドリング

Auroraでは、エラーを処理するために2つの主要なメカニズムを提供しています：関数型の `Result<T, E>` 型と、伝統的な `try-catch` ブロックです。

## Result 型 (`Result<T, E>`)

Auroraで期待される（予期できる）エラーを処理する推奨される方法は、`Result` 列挙型を使用することです。

```ar
enum Result<T, E> {
    Ok(T),
    Err(E)
}
```

### `?` 演算子によるエラーの伝播
`?` 演算子を使用すると、エラーを簡単に上位の関数へ伝播させることができます。式の結果が `Err` の場合、その関数は即座にそのエラーを返します。

```ar
fun parse(input: string) -> Result<int, string> {
    val num = parseNumber(input)? // parseNumberが失敗した場合、Errを返します
    return Ok(num * 2)
}
```

### 結果のパターンマッチング
```ar
match parse("123") {
    Ok(val) => Io::println(`成功: ${val}`)
    Err(err) => Io::println(`エラー: ${err}`)
}
```

## 例外処理 (`try-catch`)

予期しない状況や例外的な条件には、伝統的な例外処理を使用します。

```ar
try {
    val result = 10 / 0
} catch (e: ArithmeticException) {
    Io::println("ゼロ除算が発生しました")
} catch (e: Exception) {
    Io::println(`不明なエラー: ${e.getMessage()}`)
} finally {
    // クリーンアップ処理
}
```

### 例外のスロー
`throw` キーワードを使用して例外を発生させます。
```ar
fun validate(age: int) {
    if age < 0 { throw IllegalArgumentException("年齢に負の値を指定することはできません") }
}
```

## カスタム例外
Auroraのインスタンスは内部的に `ArInstance` であるため、`Exception`（または他の例外クラス）を継承して独自の例外クラスを定義できます。

```ar
class MyError(val msg: string) : Exception() {
    pub override fun getMessage() -> string = self.msg
}
```

## ベストプラクティス
1.  **`Result` を使う**: ファイルが見つからない、バリデーション失敗などの「回復可能で、想定内」のエラー。
2.  **`例外` を使う**: インデックス範囲外アクセスなどの「回復不能、またはプログラミング上のミス」によるエラー。
3.  **`!!` を避ける**: エラー伝播には `?` を、デフォルト値の提供には `?:` を優先して使用してください。
