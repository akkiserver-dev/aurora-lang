# 並行処理と非同期プログラミング

Auroraは、シンプルさ（コルーチン）と能力（OSスレッド）のバランスが取れたモダンな並行処理モデルを採用しています。

## 非同期プログラミング (`async`/`await`)

Auroraの `async` 関数は、協調的マルチタスキング（コルーチン）に基づいています。これはネットワークリクエストやファイルアクセスなどのI/Oバウンドなタスクに最適です。

- **非ブロッキング**: `await` は、下層のOSスレッドをブロックすることなく、現在の関数の実行を中断して待機します。
- **Future ベース**: 内部的には、非同期操作は `ArFuture` を返します。これはJavaの `CompletableFuture<ArObject>` をラップしたものです。

```ar
async fun fetch() {
    val data = await Http::get("https://api.aurora-lang.org")
    Io::println(data)
}
```

## マルチスレッディング (`thread`)

CPU負荷の高い計算処理のために、AuroraはOSスレッドによる真の並列実行を提供します。

```ar
thread HeavyCalculation(n: int) {
    // これは独立したOSスレッドで実行されます
    return calculateFibonacci(n)
}

val future = HeavyCalculation.start(30)
val result = future.wait() // 完了を待機
```

## メモリモデルと安全性

Auroraは共有メモリ（Shared-State）モデルを採用していますが、以下の安全策が講じられています：

1.  **スレッドプール**: VMは `CachedThreadPool` を使用してスレッドを管理し、リソースの枯渇を防ぎます。
2.  **グローバル変数**: グローバル変数はVMの `SharedState` 内の `ConcurrentHashMap` に保持されており、単純な参照や代入はスレッドセーフです。
3.  **同期処理**: 複雑な共有状態を扱う場合は、`Concurrent` モジュールの `Mutex` などの同期プリミティブの使用が推奨されます。

```ar
val lock = new Mutex()
lock.lock()
try {
    sharedCounter = sharedCounter + 1
} finally {
    lock.unlock()
}
```

## 並行処理ツールのまとめ

| ツール | 実装 | 主な用途 |
|------|----------------|------------|
| `async`/`await` | コルーチン / Future | I/O, ネットワーク, ユーザー対話 |
| `thread` | OSスレッド | 重い計算処理, バックグラウンドタスク |
| `Channel` | ブロッキング・キュー | スレッド/タスク間でのデータ受け渡し |
| `Mutex` | 再入可能ロック | 共有された可変状態の保護 |
