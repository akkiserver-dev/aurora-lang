# オブジェクト指向プログラミング

Auroraは、シンプルさとパフォーマンスを重視した強力なオブジェクト指向モデルを提供します。

## クラスとオブジェクト

クラスはオブジェクト（インスタンス）のテンプレートであり、属性（フィールド）とメソッドを定義します。

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

### `new` キーワード
Aurora (v2) では、クラスのインスタンス化に `new` キーワードが必須です。これにより、通常の関数呼び出しとコンストラクタ呼び出しが明確に区別されます。

### `self` キーワード
クラス内では、`self` は現在のインスタンスを指します。インスタンスフィールドへのアクセスや、自身のメソッドを呼び出す際に必要となります。

## 継承

Auroraは単一継承をサポートしています。すべてのクラスは明示的に指定しない限り、暗黙的に `object` を継承します。

```ar
class ScientificCalculator(brand: string) : Calculator(brand) {
    pub override fun add(a: int, b: int) -> int {
        Io::println("科学計算モードで実行")
        return super.add(a, b)
    }
}
```

- **`super`**: 親クラスのメンバーにアクセスするために使用します。
- **`override`**: 親クラスのメソッドを上書きする際に必須の修飾子です。

## 抽象クラスとトレイト

### 抽象クラス
`abstract` キーワードを使用します。インスタンス化はできず、本体のない「抽象メソッド」を持つことができます。

### トレイト
トレイトはクラスが満たすべき契約（コントラクト）を定義します。Auroraでは、カンマ区切りで複数のトレイトを実装できます。トレイトはデフォルト実装（Mixin）をサポートします。
```ar
trait Reset { pub fun reset() }
class Counter : Reset { pub override fun reset() { ... } }
```

## 内部実装の詳細
- **`ArClass`**: VM内ではクラスは `ArClass` オブジェクトとして管理されます。これにはメソッドテーブルと親クラスへの参照が含まれています。
- **メソッドディスパッチ**: メソッドの解決は動的に行われます。現在のクラスにメソッドが見つからない場合、親クラスのチェーン（`superClass`）を順に辿ります。
- **`ArInstance`**: クラスのインスタンスは `ArInstance` オブジェクトとして表現され、フィールドの値を管理するマップを保持します。
