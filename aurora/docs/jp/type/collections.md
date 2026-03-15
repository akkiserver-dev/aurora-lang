# コレクション

Auroraは、固定サイズの配列と動的なコレクションの両方を提供し、効率的なデータ管理をサポートします。

## 配列 (Array)

配列は固定サイズのインデックス付き要素シーケンスです。配列は共変性（例：`string[]` を `object[]` として扱える）を持っています。

```ar
val numbers: int[] = [1, 2, 3, 4, 5]
val fruits: string[] = ["りんご", "バナナ"]

// サイズを指定した初期化
val empty: int[10] = [] // すべての要素が0で初期化されたサイズ10の配列
```

### 要素のアクセス
```ar
val first = numbers[0]
numbers[1] = 10
```

### 主要な配列メソッド
- `length() -> int`: 要素の数を返します。
- `push(item: T) -> T[]`: 要素を末尾に追加した**新しい**配列を返します。
- `filter(p: (T) -> bool) -> T[]`: 条件に合う要素を抽出します。
- `map(f: (T) -> U) -> U[]`: 各要素を関数で変換した新しい配列を返します。

## 動的コレクション

これらは `Aurora.Collections` 名前空間で提供されています。

### リスト (`List<T>`)
重複を許容する順序付きコレクションです。デフォルトの実装は `ArrayList` です。

```ar
use Aurora.Collections.*

val names = new ArrayList<string>()
names.add("Alice")
names.add("Bob")
val size = names.size()
```

### セット (`Set<T>`)
重複を許容しない要素の集合です。デフォルトの実装は `HashSet` です。

```ar
val uniqueIds = new HashSet<int>()
uniqueIds.add(101)
uniqueIds.add(101) // 重複しているため何もしない
```

### マップ (`Map<K, V>`)
キーと値をマッピングするコレクションです。デフォルトの実装は `HashMap` です。

```ar
val scores = new HashMap<string, int>()
scores.put("Player1", 100)
val score = scores.get("Player1")
```

## 内部実装
Auroraのネイティブ配列は、VM内の `ArArray` クラスによって管理されます。`ArrayList` などの動的コレクションは、現在は `NativeBinder` を介してJavaのネイティブオブジェクトとして実装されており、大規模なデータセットでも高いパフォーマンスを維持します。
