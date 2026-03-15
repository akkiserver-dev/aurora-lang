# プリミティブ型

Auroraの型システムは、高パフォーマンスと安全性の両立を目的として設計されており、数値型や各種リテラル型を提供します。

## 数値型

Auroraの数値型は、実行時にJITコンパイルが容易なJavaのプリミティブ型に直接マップされます。

| 型 | 範囲 | メモリ | サフィックス | 用途 |
|----|------|--------|------------|------|
| `int` | −2³¹ ～ 2³¹−1 | 4バイト | なし | 整数（デフォルト）、配列インデックス |
| `long` | −2⁶³ ～ 2⁶³−1 | 8バイト | `L` | 大きな数値、タイムスタンプ、ID |
| `float` | IEEE 754単精度 | 4バイト | `f` | グラフィックス、メモリ節約 |
| `double` | IEEE 754倍精度 | 8バイト | なし | 浮動小数点（デフォルト）、科学計算 |

### リテラル形式
Auroraは可読性を高めるためにアンダースコア（`_`）を区切り文字として使用できます。

```ar
val decimal: int = 1_000_000
val hex: int = 0xFF_00_FF
val binary: int = 0b1010_1100
val longVal: long = 123_456_789_012L
val floatVal: float = 3.14f
val scientific: double = 1.5e10
```

## 文字列型 (`string`)

`string` 型はUTF-8でエンコードされた不変（immutable）な文字列を表します。内部的にはオブジェクトですが、コア型として最適化されています。

```ar
val name: string = "Aurora"
val greeting: string = `こんにちは、${name}!` // 文字列補間
val multiline: string = """
    Auroraでは
    複数行の文字列を
    簡単に扱えます。
"""
```

### 主要な文字列メソッド
- `length() -> int`: 文字数を返します。
- `substring(start: int, end: int) -> string`: 指定範囲の部分文字列を返します。
- `indexOf(sub: string) -> int`: 最初の出現位置を返します。
- `contains(sub: string) -> bool`: 部分文字列が含まれているか判定します。
- `toUpperCase()`, `toLowerCase()`: 大文字/小文字変換。
- `trim() -> string`: 前後の空白を削除します。
- `split(delimiter: string) -> string[]`: 区切り文字で配列に分割します。

## 真偽値型 (`bool`)

論理的な真偽（`true` または `false`）を表します。

- 整数（0や1）や null からの暗黙的な型変換はありません。
- 論理演算子（`&&`, `||`, `!`）は短絡評価（ショートサーキット）を行います。

## 特殊型

- **`void`**: 関数が値を返さないことを示します。
- **`object`**: すべての型のルートです（`none`を除く）。
- **`none`**: 決して値を持たないことを表します（例：例外をスローし続ける関数の戻り値）。
- **`none?`**: `null` リテラル専用の型です。
