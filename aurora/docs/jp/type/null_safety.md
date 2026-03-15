# Null安全性と型システム

Auroraは**Null安全性**を設計の柱としており、コンパイル時に `NullPointerException` を排除することを目指しています。

## Null許容システム

Auroraでは、型はデフォルトで「null非許容」です。

- `val a: string = null` // **コンパイルエラー**
- `val b: string? = null` // **OK** (Null許容型)

### 安全呼び出し演算子 (`?.`)
オブジェクトが null でない場合のみメソッドを呼び出します。null の場合、式全体が `null` を返します。
```ar
val length: int? = person?.name?.length()
```

### エルビス演算子 (`?:`)
左辺が `null` の場合に右辺の値を返します。
```ar
val name: string = getName() ?: "Unknown"
```

### 非null表明 (`!!` または `notnull`)
型を強制的に非許容型として扱います。もし値が実際に null であった場合、実行時に `NullPointerException` がスローされます。
```ar
val value = nullableValue!!
```

## 型チェックとスマートキャスト

Auroraはスマートキャストをサポートしており、型チェックの後はコンパイラが自動的に型を特定します。

```ar
val x: object = "Hello"

if x is string {
    // このブロック内では、xは自動的にstringとして扱われます
    Io::println(x.length())
}
```

### 型チェック (`is`)
オブジェクトが指定された型のインスタンスである場合に true を返します。

### 明示的キャスト (`as`)
オブジェクトをターゲットの型に変換します。キャストに失敗した場合は `ClassCastException` がスローされます。
```ar
val s = x as string
```

## 実装のヒント
Auroraの仮想マシン(VM)は内部的に `ArObject` の継承階層を使用しています。すべての変数は `ArObject` を継承したクラス（`ArInt`, `ArString` など）のインスタンスです。Nullは `ArNone` というシングルトン・インスタンスで表現されます。
