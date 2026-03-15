# パッケージシステム

## 名前空間 (Namespace)

Auroraは名前空間に **PascalCase**（C#スタイル）を採用しています。ドメインを反転させる形式ではありません。

```ar
namespace Aurora.Util.Math
namespace Aurora.Collections
```

> [!NOTE]
> Javaなどの言語とは異なり、Auroraの名前空間は**ディレクトリ構造に依存しません**。プロジェクト内の場所に関係なく、任意のファイルで任意の名前空間を定義することが可能です。

## インポート (use)

`use` キーワードを使用して、他の名前空間のメンバーをインポートします。

```ar
use Math.Calculator
use Math.{Calculator, MathUtils}
use Math.*
use Math.Calculator as MathCalc // エイリアス（別名）の使用
```

## 可視性制御

パッケージレベルでも可視性修飾子が適用されます。

- **`pub`**: 全体に公開されます。
- **`protected`**: 同一パッケージ内およびサブクラスからアクセス可能です。
- **`local`**: 同一ファイル内でのみアクセス可能です。
- **(なし)**: 同一パッケージ内でのみアクセス可能です（デフォルト）。
