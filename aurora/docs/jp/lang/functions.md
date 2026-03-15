# 関数

## 基本的な関数

```ar
fun add(a: int, b: int) -> int {
    return a + b
}

// 単一式関数（return省略可能）
fun double(x: int) -> int = x * 2

// 戻り値なし（void）
fun printMsg(msg: string) -> void {
    Io::println(msg)
}
```

## パラメータ

### デフォルトパラメータ
```ar
fun greet(name: string, greeting: string = "こんにちは") -> string {
    return "${greeting}、${name}!"
}
```

### 名前付き引数
```ar
greet(name: "Alice", greeting: "やあ")
```

### 可変長引数 (Varargs)
```ar
fun sum(varargs numbers: int) -> int {
    var total: int = 0
    for num in numbers { total = total + num }
    return total
}
```

## ラムダ式と高階関数

```ar
// ラムダ式
val double = (x: int) => x * 2

// 高階関数（関数を引数に取る）
fun apply(a: int, b: int, op: (int, int) -> int) -> int {
    return op(a, b)
}

val res = apply(5, 3, (x, y) => x + y)
```

## 拡張関数 (Inject)

既存の型やクラスにメソッドを追加します。

```ar
inject type string {
    pub fun repeat(count: int) -> string {
        var result: string = ""
        for i in 1..count { result = result + self }
        return result
    }
}

val s = "Hi".repeat(3) // "HiHiHi"
```
