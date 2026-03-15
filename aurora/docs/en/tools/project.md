# Project Management (aurora.toml)

Aurora projects are managed using `aurora.toml`. This file defines package metadata, dependencies, and build configurations.

## Example aurora.toml

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

## Recommended Project Structure

```text
project/
├── src/            # Source code (.ar files)
│   ├── main.ar
│   └── Utils/
├── test/           # Unit tests
├── resources/      # Static assets
└── aurora.toml     # Project configuration
```
