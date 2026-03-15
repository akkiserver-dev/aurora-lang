# プロジェクト管理 (aurora.toml)

Auroraプロジェクトは `aurora.toml` ファイルで管理されます。このファイルには、パッケージのメタデータ、依存関係、およびビルド設定を記述します。

## aurora.toml の例

```toml
[package]
name = "my-app"
version = "1.0.0"
edition = "2025"

[dependencies]
http-client = "2.0.0"
json-parser = "1.5.0"
math-lib = "^1.2"
utils = { path = "../utils" }

[dev-dependencies]
test-framework = "1.0.0"

[build]
optimization-level = 2
debug-symbols = true
```

## 推奨されるプロジェクト構成

```text
project/
├── src/            # ソースコード (.ar ファイル)
│   ├── main.ar
│   └── Utils/
├── test/           # ユニットテスト
├── resources/      # 静的リソース
└── aurora.toml     # プロジェクト設定ファイル
```
