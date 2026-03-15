# Aurora VM & Language Implementation Tasks

## Phase 2: Control Flow & Advanced Types
- [ ] Complete `throw` support (Compiler -> VM emission).
- [ ] Add support for `DestructurePattern` in records (match expression).
- [ ] Implement `record` type (Compiler implementation needed).
    - [ ] Built-in `copy()`, `toString()`, and equals.
    - [ ] Decomposition support in match.
- [ ] Implement `enum` type (Compiler implementation needed).
    - [ ] Support for associated values/body members.
- [ ] Support linearization for multiple trait inheritance.
- [ ] Support `lazy val` (Deferred assignment for local variables only — declare now, assign once later; compiler enforces: no read before assignment, no reassignment, all code paths must assign before use; not applicable to class fields).

## Phase 3: Concurrency & Async
- [ ] Implement `async/await` (Coroutines).
    - [ ] Compiler support for `async` functions and `await` expressions.
    - [ ] VM support for suspension/resumption (state snapshots).
    - [ ] Task scheduler implementation (event loop).

## Phase 4: Standard Library & Ecosystem
- [ ] Implement `Collections` module (List, Map, Set).
- [ ] Implement `Math` module (abs, sin, cos, etc.).
- [ ] Implement `Json` module (Serialization/Deserialization).
- [ ] Implement `Http` module (Async client).

## Phase 5: Optimization & Internals
- [ ] Optimize VM loop (Switch-table optimization).
- [ ] Improve stack management (Register-based VM experimentation?).
- [ ] Refine module loader (Caching, circular dependency resolution).
- [ ] Implement `nonnull` runtime assertion check.

## Phase 6: Tooling & DX
- [ ] LSP: Go to Definition / Find References.
- [ ] LSP: Rename refactoring support.
- [ ] LSP: Semantic Highlighting refinements (Type variables, constants).
- [ ] VSCode: Debugger Integration (DAP - Debug Adapter Protocol).
- [ ] VSCode: Integrated REPL or "Run in Aurora" command.

## Phase 2b: Singleton Object
- [ ] Implement `object` declaration (syntactic sugar — desugars to `pub class` with all members forced `static`).
    - [ ] `AuroraParser.g4`: Add `objectDeclaration` rule; disambiguate from `object` root type by lookahead (identifier follows → singleton, otherwise → root type).
    - [ ] Compiler: desugar `object Foo { ... }` → `pub class Foo { static ... }` before type-checking pass.
    - [ ] Support anonymous companion `object` block inside a class body (desugars to static members on the enclosing class).
    - [ ] Access syntax: `Foo::member` (consistent with existing `Io::println`, `Color::Red`).
    - [ ] `var` fields inside `object` → compile error (enforce immutability; satisfies `Sendable` automatically).
    - [ ] Namespace support: `object` definable inside any namespace, resolved via `use`.

## Phase 8: Known Issues & Bugs
- [ ] Compiler: `visitElvisExpr` is currently empty.
- [ ] VM: Improve error reporting for ClassCastException in `as` operator.

## Phase 9: Language Design Improvements
- [ ] **#1 Remove generic type erasure** (reified generics)
    - [ ] Design reification strategy on JVM (implicit `ArClass` tokens or inline reified functions).
    - [ ] Update `ArInstance` / `ArClass` to carry runtime type information.
    - [ ] Enable `T is String`-style runtime type checks in generic contexts.
    - [ ] Note: revisit after Rust VM migration for full monomorphization option.
- [ ] **#2 Remove `new` keyword**
    - [ ] `AuroraLexer.g4`: Remove `NEW` token.
    - [ ] `AuroraParser.g4`: Remove `objectCreation` rule; instantiation falls into `CallExpr`.
    - [ ] `TypeChecker.java` (`visitCallExpr`): Resolve callee — if class/record, compile as `NewExpr`.
    - [ ] Add PascalCase naming violation as a hard compiler error to preserve disambiguation.
    - [ ] Deprecation phase: `new` emits warning → remove in next major version.
    - [ ] Add `aurora fmt --fix` to strip `new` automatically.
    - [ ] Tests: `test_newless.ar`, `test_naming_violation.ar`.
- [ ] **#4 Compile-time concurrency safety** (`Sendable` trait)
    - [ ] Define `Sendable` as a compiler-intrinsic marker trait.
    - [ ] Auto-tag types: primitives and `val`-only classes → `Sendable`; any `var` field → non-`Sendable`.
    - [ ] `TypeChecker.java` (`visitThreadExpr`): Verify all arguments to `thread Name(...)` are `Sendable`; emit `SemanticError` if not.
    - [ ] Upgrade `Mutex<T>` to expose `withLock { }` scoped API; hardcode as intrinsically `Sendable`.
    - [ ] Add lightweight escape analysis: block `lockedT` from escaping `withLock` closure scope.
    - [ ] Tests: data race caught at compile time, `Mutex.withLock` usage.
- [ ] **#7 Keyword reduction**
    - [ ] `inject` → receiver syntax: `pub fun TypeName.method()`
        - [ ] `AuroraLexer.g4`: Remove `INJECT` token.
        - [ ] `AuroraParser.g4`: Remove `injectDeclaration` rule; add optional `(typeType DOT)?` to `functionDeclaration`.
        - [ ] `FunctionDecl.java`: Add optional `receiverType` field.
        - [ ] Bind extension functions per-namespace to prevent name collisions across `use` imports.
        - [ ] Tests: `test_extension.ar`, `test_extension_scope.ar`.
    - [ ] `lazy` → keep as compiler keyword (deferred assignment for local variables, no runtime overhead).
    - [ ] `expect` → remove keyword; move to `Result.expect(msg)` standard library method.
- [ ] **#8 Nested block comments**
    - [ ] `AuroraLexer.g4`: Add `commentDepth` counter in `@lexer::members`.
    - [ ] Replace `BLOCK_COMMENT` with stateful push/pop mode rules tracking `/*` / `*/` depth.
    - [ ] Ensure `/*` inside string literals does not increment depth counter.
    - [ ] Tests: `test_comments.ar`, `test_comments_string.ar`.