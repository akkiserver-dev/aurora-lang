# 標準ライブラリ (StdLib)

Auroraは、一般的なプログラミングタスクのための標準モジュールを提供しています。これらのモジュールは完全な名前空間でアクセスするか、`use` を使ってインポートして使用します。

## Io モジュール (`Aurora.Io.Io`)

`Io` モジュールは、コンソールへの出力や標準入力からの読み取りを処理します。

```ar
use Aurora.Io.Io

Io::println("Hello, Aurora!")
val input = Io::readLine()
```

### 静的メソッド
- **`Io::print(msg: object)`**: 標準出力にメッセージを表示します。
- **`Io::println(msg: object)`**: 行末に改行を付けて標準出力にメッセージを表示します。
- **`Io::readLine() -> string?`**: 標準入力から1行読み込みます。ストリームの末尾に達した場合は `null` を返します。

## Concurrent モジュール (`Aurora.Runtime.Concurrent`)

マルチスレッディングや非同期処理のためのプリミティブを提供します。

### 主要なクラス
- **`Future<T>`**: 非同期操作の結果を表します。
    - `wait() -> T`: 結果が利用可能になるまで現在のスレッドをブロックします。
- **`Mutex`**: 再入可能な相互排除ロック。
    - `lock()`: ロックを取得します。
    - `unlock()`: ロックを解除します。
- **`Channel<T>(capacity: int)`**: スレッドセーフな通信路。
    - `send(item: T)`: データを送信します（バッファが一杯の場合はブロックします）。
    - `receive() -> T?`: データを受信します（空の場合はブロックします）。

## コレクション

標準的なコレクションは `Aurora.Collections` 名前空間で提供されています。

- **`ArrayList<T>`**: 動的配列。
- **`HashSet<T>`**: ハッシュテーブルに基づく集合。
- **`HashMap<K, V>`**: キーと値のペアを管理するマップ。

```ar
use Aurora.Collections.*

val list = new ArrayList<string>()
list.add("Aurora")
Io::println(list.get(0))
```

## ネイティブ・バイディングの仕組み
内部的に、Auroraの標準ライブラリはJavaで実装されており、`@AuroraLib` と `@AuroraNative` アノテーションを使用してAuroraの関数にマップされています。たとえば、`IoModule.java` は `Aurora.Io.Io._println` を `System.out.println` に結びつけています。
